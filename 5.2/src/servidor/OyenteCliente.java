package servidor;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import ambos.*;

public class OyenteCliente extends Thread {
	private Socket s;
	private Compartido datos;
	
	public OyenteCliente(Socket s, Compartido datos) {
		this.s = s;
		this.datos = datos;
	}
	
	public void run() {
		try {
		ObjectInputStream fin = new ObjectInputStream(s.getInputStream());
		ObjectOutputStream fout = new ObjectOutputStream(s.getOutputStream());

		while (true) {
			Mensaje msj = (Mensaje) fin.readObject();
System.out.println("Recibido "+msj.getTipo()+ " de "+msj.getOrigen()+" para "+msj.getDestino()); System.out.flush();
			if(!msj.getDestino().equals(Servidor.origen)) continue;	// no era para mí?
			Msj_Information send;
			switch(msj.getTipo()) {
			case CONEXION:
				String ip = ((Msj_Information) msj).getContent(0);
				datos.guardar_usuario(msj.getOrigen(),ip,fout);
				send = new Msj_Information(Msj.CONFIRMACION_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				break;
			case LISTA_USARIOS:
				String lista = datos.usuarios_sistema();
				send = new Msj_Information(Msj.CONFIRMACION_LISTA_USUARIOS,Servidor.origen,msj.getOrigen());
				send.putContent(lista);
				fout.writeObject(send);
				break;
			case MODIFICAR_FICHEROS:
				// TODO modificar ficheros
				send = new Msj_Information(Msj.CONFIRMACION_MODIFICAR_FICHEROS,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_Information) msj).getContent(0);			
				String emisor = datos.buscar_usuario(fichero);
				if(emisor != null) {
					send = new Msj_Information(Msj.EMITIR_FICHERO,Servidor.origen,emisor);
					send.putContent(msj.getOrigen()); // este es el receptor
					send.putContent(fichero);
					send.setEntero1(datos.buscar_num(msj.getOrigen()));
					ObjectOutputStream fout2 = datos.buscar_output(emisor);
					fout2.writeObject(send);
				}
				else {	
					send = new Msj_Information(Msj.ERROR,Servidor.origen,msj.getOrigen());
					send.putContent("No hay usuarios conectados con ese fichero "+fichero);
					fout.writeObject(send);
				}
					break;
			case PREPARADO_CLIENTESERVIDOR:
				String receptor = ((Msj_Information) msj).getContent(0);
				String ip_emisor = ((Msj_Information) msj).getContent(1);
				String fich = ((Msj_Information) msj).getContent(2);
				int puerto_emisor = ((Msj_Information) msj).getEntero1();
				send = new Msj_Information(Msj.PREPARADO_SERVIDORCLIENTE,Servidor.origen,receptor);
				send.putContent(fich);
				send.putContent(ip_emisor);
				send.setEntero1(puerto_emisor);
				ObjectOutputStream fout1 = datos.buscar_output(receptor);
				fout1.writeObject(send);
				break;
			case CERRAR_CONEXION:
				datos.eliminar_usuario(msj.getOrigen());
				send = new Msj_Information(Msj.CONFIRMACION_CERRAR_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				s.close();
				return;
			default:
				break;
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
