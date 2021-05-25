package servidor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import ambos.Mensaje;
import ambos.Msj;
import ambos.Msj_Information;

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
				boolean session = datos.guardar_usuario(msj.getOrigen(),ip,fout);
				if(session) {
					send = new Msj_Information(Msj.CONFIRMACION_CONEXION,Servidor.origen,msj.getOrigen());
					fout.writeObject(send); fout.flush();
				}
				else {
					send_error(msj.getOrigen(),fout,"Ya tiene una sesión abierta.",2);
				}
				break;
			case LISTA_USARIOS:
				String lista = datos.usuarios_sistema();
				send = new Msj_Information(Msj.CONFIRMACION_LISTA_USUARIOS,Servidor.origen,msj.getOrigen());
				send.putContent(lista);
				fout.writeObject(send); fout.flush();
				break;
			case ANADIR_FICHERO:
				String f_add = ((Msj_Information)msj).getContent(0);
				boolean anadido = datos.anadir_fichero(f_add,msj.getOrigen());
				if(anadido) {
					send = new Msj_Information(Msj.CONFIRMACION_ANADIR_FICHERO,Servidor.origen,msj.getOrigen());
					send.putContent(f_add);
					fout.writeObject(send); fout.flush();
				}
				else {					
					send_error(msj.getOrigen(),fout,"No se pudo añadir el fichero "+f_add,0);
				}
				break;
			case ELIMINAR_ALGUN_FICHERO:
				List<String> mis_ficheros = datos.ficheros_usuario(msj.getOrigen());
				send = new Msj_Information(Msj.CONFIRMACION_ELIMINAR_ALGUN_FICHERO,Servidor.origen,msj.getOrigen());
				send.setContent(mis_ficheros);
				fout.writeObject(send); fout.flush();
				break;
			case ELIMINAR_ESTE_FICHERO:
				String f_del = ((Msj_Information)msj).getContent(0);
				boolean eliminado = datos.eliminar_fichero(f_del,msj.getOrigen());
				if(eliminado) {
					send = new Msj_Information(Msj.CONFIRMACION_ELIMINAR_ESTE_FICHERO,Servidor.origen,msj.getOrigen());
					send.putContent(f_del);
					fout.writeObject(send); fout.flush();
				}
				else {
					send_error(msj.getOrigen(),fout,"No se pudo eliminar el fichero "+f_del,0);
				}
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
					send_error(msj.getOrigen(),fout,"No hay usuarios conectados con ese fichero "+fichero,1);
				}
					break;
			case PREPARADO_CLIENTESERVIDOR:
				String receptor = ((Msj_Information) msj).getContent(0);
				String ip_emisor = ((Msj_Information) msj).getContent(1);
				String fich = ((Msj_Information) msj).getContent(2);
				int puerto_emisor = ((Msj_Information) msj).getEntero();
				send = new Msj_Information(Msj.PREPARADO_SERVIDORCLIENTE,Servidor.origen,receptor);
				send.putContent(fich);
				send.putContent(ip_emisor);
				send.setEntero(puerto_emisor);
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
					send_error(msj.getOrigen(),fout,"Error al eliminar usuario.",1);
				}
			default:
				break;
			}
		}
		} catch (EOFException e) {return;}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void send_error(String destino, ObjectOutputStream fout, String error, int b) throws IOException {
		Msj_Information send = new Msj_Information(Msj.ERROR,Servidor.origen,destino);
		send.putContent(error);
		send.setEntero(b); // sin EM o con EM
		fout.writeObject(send); fout.flush();
	}
}
