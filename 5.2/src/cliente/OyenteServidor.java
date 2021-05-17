package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;

import ambos.Mensaje;
import ambos.Msj;
import ambos.Msj_Information;
import servidor.Servidor;

public class OyenteServidor extends Thread {
	private final String origen = Cliente.usuario();
	private final String destino = Servidor.origen;
	private ObjectInputStream finc;
	private ObjectOutputStream foutc;

	public OyenteServidor(Socket s, ObjectOutputStream foutc) {
		try {
			finc = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.foutc = foutc;
	}
	
	public void run() {
		try {
		while (true) {
			Mensaje msj;
			msj = (Mensaje) finc.readObject();
System.out.println("Recibido "+msj.getTipo()+ " de "+msj.getOrigen()+" para "+msj.getDestino()); System.out.flush();			
			if(!msj.getDestino().equals(origen)) continue;
			switch(msj.getTipo()) {
			case CONFIRMACION_CONEXION:
				System.out.println("Conexión establecida."); System.out.flush();
				Cliente.release_flow();
				break;
			case CONFIRMACION_LISTA_USUARIOS:
				Cliente.info("Lista de usuarios:\n\n"+((Msj_Information) msj).getContent(0));
				Cliente.release_flow();
				break;
			case CONFIRMACION_ANADIR_FICHERO:
				System.out.println("Fichero "+((Msj_Information) msj).getContent(0)+" añadido."); System.out.flush();
				break;
			case CONFIRMACION_ELIMINAR_ALGUN_FICHERO:
				List<String> mis_ficheros = ((Msj_Information) msj).getContent();
				if(mis_ficheros.size() == 0) Cliente.error("No tienes ficheros disponibles.");
				else {
				    String f_del = (String) JOptionPane.showInputDialog(
		                    null, "Elige fichero a eliminar:", origen, JOptionPane.PLAIN_MESSAGE,
		                    null, mis_ficheros.toArray(new String[0]), mis_ficheros.get(0)); // lista a elegir
					if(f_del!=null) {
						Msj_Information send = new Msj_Information(Msj.ELIMINAR_ESTE_FICHERO,origen,destino);
						send.putContent(f_del);
						foutc.writeObject(send); foutc.flush();
						Cliente.info("El fichero "+f_del+" se eliminará en el fondo");
					}
				}
				Cliente.release_flow();
				break;
			case CONFIRMACION_ELIMINAR_ESTE_FICHERO:
				System.out.println("Fichero "+((Msj_Information) msj).getContent(0)+" eliminado."); System.out.flush();
				break;
			case EMITIR_FICHERO:
				String receptor = ((Msj_Information) msj).getContent(0);
				String fichero = ((Msj_Information) msj).getContent(1);
				Msj_Information send = new Msj_Information(Msj.PREPARADO_CLIENTESERVIDOR,origen,destino);
				send.putContent(receptor);
				send.putContent(Cliente.ip());
				send.putContent(fichero);

				ServerSocket ss = new ServerSocket(0);
				int puerto = ss.getLocalPort();
				send.setEntero(puerto);
				(new Emisor(origen,receptor,ss,fichero)).start();
				
				foutc.writeObject(send); foutc.flush();
				break;
			case PREPARADO_SERVIDORCLIENTE:
				String fich = ((Msj_Information) msj).getContent(0);
				String ip_em = ((Msj_Information) msj).getContent(1);
				int p_em = ((Msj_Information) msj).getEntero();
				(new Receptor(origen,ip_em,p_em,fich)).start();

				Cliente.info("El fichero "+fich+" se recibirá en el fondo.");
				Cliente.release_flow();
				break;
			case CONFIRMACION_CERRAR_CONEXION:
				Cliente.info("¡Adiós!");
				System.exit(0);
				return;
			case ERROR:
				Cliente.error("Error recibido de "+msj.getOrigen()+":\n"+
						((Msj_Information) msj).getContent(0));
				int codigo = ((Msj_Information) msj).getEntero();
				if(codigo==1) Cliente.release_flow(); // error final flow
				else if(codigo>1) System.exit(1); // error catastrófico
				break;
			default:
				break;
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
