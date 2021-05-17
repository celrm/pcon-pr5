package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambos.*;
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
		
		while (true) {
			Mensaje msj;
			try {
			msj = (Mensaje) finc.readObject();
			System.out.println("Recibido "+msj.getTipo()+ " de "+msj.getOrigen()+" para "+msj.getDestino()); 
			System.out.flush();			
			if(!msj.getDestino().equals(origen)) continue;
			switch(msj.getTipo()) {
			case CONFIRMACION_CONEXION:
				System.out.println("Conexión establecida."); System.out.flush();
				Cliente.flow.release();
				break;
			case CONFIRMACION_LISTA_USUARIOS:
				String lista = ((Msj_Information) msj).getContent(0);
				JOptionPane.showMessageDialog(new JPanel(), "Lista de usuarios:\n\n"+lista, 
						origen, JOptionPane.PLAIN_MESSAGE);
				Cliente.flow.release();
				break;
			case CONFIRMACION_ANADIR_FICHERO:
				System.out.println("Fichero añadido."); System.out.flush(); // TODO poner cuál
				Cliente.flow.release();
				break;
			case CONFIRMACION_ELIMINAR_ALGUN_FICHERO:
				String mis_ficheros = ((Msj_Information) msj).getContent(0);
				JOptionPane.showMessageDialog(new JPanel(), "Lista de ficheros:\n\n"+mis_ficheros, 
						origen, JOptionPane.PLAIN_MESSAGE);
				break;
			case CONFIRMACION_ELIMINAR_ESTE_FICHERO:
				System.out.println("Fichero eliminado."); System.out.flush(); // TODO poner cuál
				Cliente.flow.release();
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
				send.setEntero1(puerto);

				(new Emisor(origen,receptor,ss,fichero)).start();
				
				foutc.writeObject(send); foutc.flush();
				break;
			case PREPARADO_SERVIDORCLIENTE:
				String fich = ((Msj_Information) msj).getContent(0);
				String ip_em = ((Msj_Information) msj).getContent(1);
				int p_em = ((Msj_Information) msj).getEntero1();
				(new Receptor(origen,ip_em,p_em,fich)).start();

				JOptionPane.showMessageDialog(null, "El fichero "+fich+" se descargará en el fondo.",
						origen, JOptionPane.OK_OPTION);
				Cliente.flow.release();
				break;
			case CONFIRMACION_CERRAR_CONEXION:
				JOptionPane.showMessageDialog(null, "¡Adiós!",
						origen, JOptionPane.WARNING_MESSAGE);
				Cliente.flow.release();
				return;
			case ERROR:
				Cliente.error("Error recibido de "+msj.getOrigen()+":\n"+
						((Msj_Information) msj).getContent(0));
				Cliente.flow.release();
			default:
				break;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
