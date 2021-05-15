import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
			Mensaje send;
			switch(msj.getTipo()) {
			case CONFIRMACION_CONEXION:
				System.out.println("Conexión establecida."); System.out.flush();
				Cliente.terminal.release();
				break;
			case CONFIRMACION_LISTA_USUARIOS:
				String lista = ((Msj_Information) msj).getContent1();
				System.out.println(lista); System.out.flush();
				Cliente.terminal.release();
				break;
			case EMITIR_FICHERO:
				String receptor = ((Msj_Information) msj).getContent1();
				String fichero = ((Msj_Information) msj).getContent2();
				send = new Msj_Information(Msj.PREPARADO_CLIENTESERVIDOR,origen,destino);
				((Msj_Information)send).setContent1(receptor);
				((Msj_Information)send).setContent2(Cliente.ip());
				int puerto = 505; // TODO generar puertos
				((Msj_Information)send).setEntero1(puerto);
				
				foutc.writeObject(send); foutc.flush();
				(new Emisor(origen,receptor,puerto,fichero)).start();
				break;
			case PREPARADO_SERVIDORCLIENTE:
				String ip_em = ((Msj_Information) msj).getContent1();
				int p_em = ((Msj_Information) msj).getEntero1();
				(new Receptor(origen,ip_em,p_em)).start();
				break;
			case CONFIRMACION_CERRAR_CONEXION:
				System.out.println("¡Adiós!"); System.out.flush();
				Cliente.terminal.release();
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
