import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OyenteServidor extends Thread {
	private Socket s;
	private String origen;
	private final String destino = "S";
	private ObjectInputStream fin;
	private ObjectOutputStream fout;
	/*
	Implementa el interfaz “Runnable” o hereda de la clase “Thread”, y ser ́a usada para 
	llevar a cabo una escucha continua en el canal de comuni-caci ́on con el servidor, 
	en un hilo diferente.
	*/
	public OyenteServidor(Socket s, String o) {
		this.s = s;
		origen = o;
	}
}
