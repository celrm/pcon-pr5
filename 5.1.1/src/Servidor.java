import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(501);

		while(true) {
		System.out.println("Esperando conexión..."); System.out.flush();
			Socket s = ss.accept();
		System.out.println("Conectado"); System.out.flush();
			
			ObjectInputStream fin = new ObjectInputStream(s.getInputStream());
			Paquete fichero = (Paquete) fin.readObject();
		System.out.println("He recibido fichero " + fichero.get()); System.out.flush();

			String dentro = procesar(fichero.get());
		
		System.out.println("Contenido:"); System.out.flush();
		System.out.println(dentro); System.out.flush();
		
				Paquete contenido = new Paquete(dentro);
				ObjectOutputStream fout = new ObjectOutputStream(s.getOutputStream());
		        fout.writeObject(contenido); fout.flush();
		System.out.println("Mandado contenido"); System.out.flush();
			
		}
	}

	private static String procesar(String fichero) {
		try {
			File file = new File(fichero);
		    Scanner lectura = new Scanner(file);
		    String dentro = "";
		    while (lectura.hasNextLine()) {
		    	dentro = dentro + "\n" + lectura.nextLine();
		    }
		    dentro = dentro.substring(1); // droppeo el '\n'
		    lectura.close();
		    return dentro;
		} catch (FileNotFoundException e) {
			System.out.println("No fichero " + fichero); System.out.flush();
		    e.printStackTrace();
		}
		return null;
	}
}
