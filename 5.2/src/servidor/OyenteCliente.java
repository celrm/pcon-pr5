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
				datos.guardar_usuario(msj.getOrigen(),fout);
				send = new Msj_Information(Msj.CONFIRMACION_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				break;
			case LISTA_USARIOS:
				String lista = datos.usuarios_sistema();
				send = new Msj_Information(Msj.CONFIRMACION_LISTA_USUARIOS,Servidor.origen,msj.getOrigen());
				send.setContent1(lista);
				fout.writeObject(send);
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_Information) msj).getContent1();			
				String emisor = datos.buscar_usuario(fichero);
				send = new Msj_Information(Msj.EMITIR_FICHERO,Servidor.origen,emisor);
				send.setContent1(msj.getOrigen()); // este es el receptor
				send.setContent2(fichero);
				ObjectOutputStream fout2 = datos.buscar_output(emisor);
				fout2.writeObject(send);
				break;
			case PREPARADO_CLIENTESERVIDOR:
				String receptor = ((Msj_Information) msj).getContent1();
				String ip_emisor = ((Msj_Information) msj).getContent2();
				int puerto_emisor = ((Msj_Information) msj).getEntero1();
				send = new Msj_Information(Msj.PREPARADO_SERVIDORCLIENTE,Servidor.origen,receptor);
				send.setContent1(ip_emisor);
				send.setEntero1(puerto_emisor);
				fout2 = datos.buscar_output(receptor);
				fout2.writeObject(send);
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
