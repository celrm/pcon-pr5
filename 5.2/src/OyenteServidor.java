import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OyenteServidor extends Thread {
	private Socket s;
	private String origen;
	private final String destino = Servidor.origen;
	private String cliente2;
	private ObjectInputStream finc;
	private ObjectOutputStream foutc;
	/*
	Implementa el interfaz “Runnable” o hereda de la clase “Thread”, y ser ́a usada para 
	llevar a cabo una escucha continua en el canal de comuni-caci ́on con el servidor, 
	en un hilo diferente.
	*/
	public OyenteServidor(Socket s, String o) {
		this.s = s;
		origen = o;
	}
	
	public void run() {
		try {
			finc = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			Mensaje m;
			try {
			m = (Mensaje) finc.readObject();
			System.out.println("Recibido "+m.getTipo()+ " de "+m.getOrigen()+" para "+m.getDestino()); 
			System.out.flush();			
			if(!m.getDestino().equals(origen)) continue; // está pocho			
			Mensaje n;
			switch(m.getTipo()) {
			case CONFIRMACION_CONEXION:
				System.out.println("Conexión establecida."); System.out.flush();
				break;
			case CONFIRMACION_LISTA_USUARIOS:
				String lista = ((Msj_String) m).getContent();
				System.out.println(lista); System.out.flush();
				break;
			case EMITIR_FICHERO:
				cliente2 = ((Msj_MasCosas) m).getContent1();
				String fichero = ((Msj_MasCosas) m).getContent2();
				n = new Msj_MasCosas(Msj.PREPARADO_CLIENTESERVIDOR,origen,destino);
				((Msj_MasCosas)n).setContent1(cliente2);
				((Msj_MasCosas)n).setContent2(Cliente.ip);
				int puerto = 505; // TODO generar puertos
				((Msj_MasCosas)n).setEntero1(puerto);
				
				foutc.writeObject(n); foutc.flush();
				(new Emisor(Cliente.ip,puerto,fichero)).start();
				break;
			case PREPARADO_SERVIDORCLIENTE:
				String ip_em = ((Msj_MasCosas) m).getContent1();
				int p_em = ((Msj_MasCosas) m).getEntero1();
				(new Receptor(ip_em,p_em)).start();
				break;
			case CONFIRMACION_CERRAR_CONEXION:
				System.out.println("¡Adiós!"); System.out.flush();
				return;
			default:
				break;
			}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
