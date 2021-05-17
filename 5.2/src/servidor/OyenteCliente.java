package servidor;

import java.io.IOException;
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
			if(!msj.getDestino().equals(Servidor.origen)) continue;
			Msj_Information send;
			switch(msj.getTipo()) {
			case CONEXION:
				String ip = ((Msj_Information) msj).getContent(0);
				datos.guardar_usuario(msj.getOrigen(),ip,fout);
				send = new Msj_Information(Msj.CONFIRMACION_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send); fout.flush();
				break;
			case LISTA_USARIOS:
				String lista = datos.usuarios_sistema();
				send = new Msj_Information(Msj.CONFIRMACION_LISTA_USUARIOS,Servidor.origen,msj.getOrigen());
				send.putContent(lista);
				fout.writeObject(send); fout.flush();
				break;
			case ANADIR_FICHERO:
				String f_add = ((Msj_Information)msj).getContent(0);
				boolean bien = datos.anadir_fichero(f_add,msj.getOrigen());
				if(bien) {
					send = new Msj_Information(Msj.CONFIRMACION_ANADIR_FICHERO,Servidor.origen,msj.getOrigen());
					fout.writeObject(send); fout.flush();
				}
				else {					
					send_error(msj.getOrigen(),fout,"No se pudo añadir el fichero "+f_add);
				}
				break;
			case ELIMINAR_ALGUN_FICHERO:
				// TODO modificar ficheros
				String mis_ficheros = datos.ficheros_usuario(msj.getOrigen());
				send = new Msj_Information(Msj.CONFIRMACION_ELIMINAR_ALGUN_FICHERO,Servidor.origen,msj.getOrigen());
				send.putContent(mis_ficheros);
				fout.writeObject(send); fout.flush();
				break;
			case ELIMINAR_ESTE_FICHERO:
				// TODO modificar ficheros
				send = new Msj_Information(Msj.CONFIRMACION_ELIMINAR_ESTE_FICHERO,Servidor.origen,msj.getOrigen());
				fout.writeObject(send); fout.flush();
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_Information) msj).getContent(0);			
				String emisor = datos.buscar_usuario(fichero);
				if(emisor != null) {
					send = new Msj_Information(Msj.EMITIR_FICHERO,Servidor.origen,emisor);
					send.putContent(msj.getOrigen());
					send.putContent(fichero);
					ObjectOutputStream fout2 = datos.buscar_output(emisor);
					fout2.writeObject(send); fout2.flush();
				}
				else {	
					send_error(msj.getOrigen(),fout,"No hay usuarios conectados con ese fichero "+fichero);
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
				fout1.writeObject(send); fout1.flush();
				break;
			case CERRAR_CONEXION:
				boolean ok = datos.eliminar_usuario(msj.getOrigen());
				if(ok) {
					send = new Msj_Information(Msj.CONFIRMACION_CERRAR_CONEXION,Servidor.origen,msj.getOrigen());
					fout.writeObject(send); fout.flush();
					s.close();
					return;
				}
				else {
					send_error(msj.getOrigen(),fout,"Error al eliminar usuario.");
				}
			default:
				break;
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void send_error(String destino, ObjectOutputStream fout, String error) throws IOException {
		Msj_Information send = new Msj_Information(Msj.ERROR,Servidor.origen,destino);
		send.putContent(error);
		fout.writeObject(send); fout.flush();
	}
}
