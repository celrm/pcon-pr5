import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
	System.out.println("Esperando conexión..."); System.out.flush();
		Socket s = new Socket("localhost",501);
	System.out.println("Conectado"); System.out.flush();

		ObjectOutputStream foutc = new ObjectOutputStream(s.getOutputStream());
		foutc.writeObject(new Paquete("f1.txt")); foutc.flush();
	System.out.println("Mandada petición"); System.out.flush();		
		
		ObjectInputStream finc = new ObjectInputStream(s.getInputStream());
		Paquete recibido = (Paquete) finc.readObject(); 
		System.out.println(recibido.get());
		
		s.close();
	System.out.println("Conexión terminada"); System.out.flush();
	}

}
