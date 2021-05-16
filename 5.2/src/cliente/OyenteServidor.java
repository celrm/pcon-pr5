package cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

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
			if(!msj.getDestino().equals(origen)) continue; // está pocho
			switch(msj.getTipo()) {
			case CONFIRMACION_CONEXION:
				System.out.println("Conexión establecida."); System.out.flush();
				break;
			case CONFIRMACION_LISTA_USUARIOS:
				String lista = ((Msj_Information) msj).getContent1();

				JOptionPane.showMessageDialog(null, lista,
						"Lista de usuarios", JOptionPane.PLAIN_MESSAGE);
				break;
			case EMITIR_FICHERO:
				String receptor = ((Msj_Information) msj).getContent1();
				String fichero = ((Msj_Information) msj).getContent2();
				Msj_Information send = new Msj_Information(Msj.PREPARADO_CLIENTESERVIDOR,origen,destino);
				send.setContent1(receptor);
				send.setContent2(Cliente.ip());
				int puerto = 505; // TODO generar puertos
				send.setEntero1(puerto);
				
				(new Emisor(origen,receptor,puerto,fichero)).start();
				
				foutc.writeObject(send); foutc.flush();
				break;
			case PREPARADO_SERVIDORCLIENTE:
				String ip_em = ((Msj_Information) msj).getContent1();
				int p_em = ((Msj_Information) msj).getEntero1();
				(new Receptor(origen,ip_em,p_em)).start();
				break;
			case CONFIRMACION_CERRAR_CONEXION:
				JOptionPane.showMessageDialog(null, "¡Adiós!",
						"Despedida", JOptionPane.CLOSED_OPTION);
				return;
			default:
				break;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
