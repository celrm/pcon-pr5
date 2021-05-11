import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Servidor {

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(501);

		while(true) {
		System.out.println("Esperando conexión..."); System.out.flush();
			Socket s = ss.accept();
		System.out.println("Conectado"); System.out.flush();
			
			BufferedReader fin = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String fichero = fin.readLine();
		System.out.println("He recibido fichero " + fichero); System.out.flush();

			try {
				File file = new File(fichero);
			    Scanner lectura = new Scanner(file);
			    String dentro = "";
			    while (lectura.hasNextLine()) {
			    	dentro = dentro + "\n" + lectura.nextLine();
			    }
			    dentro = dentro.substring(1); // droppeo el '\n'
			    lectura.close();
		System.out.println("Contenido:"); System.out.flush();
		System.out.println(dentro); System.out.flush();
		
				PrintWriter fout = new PrintWriter(s.getOutputStream());
		        fout.print(dentro); fout.print((char)0);
		        fout.flush();
		System.out.println("Mandado contenido"); System.out.flush();
			} catch (FileNotFoundException e) {
		System.out.println("No fichero " + fichero); System.out.flush();
			    e.printStackTrace();
			}
		}
	}
}
