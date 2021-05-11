import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

	public static void main(String[] args) throws UnknownHostException, IOException {
	System.out.println("Esperando conexión..."); System.out.flush();
		Socket s = new Socket("localhost",501);
	System.out.println("Conectado"); System.out.flush();

		PrintWriter foutc = new PrintWriter(s.getOutputStream());
		foutc.println("f1.txt"); foutc.flush();
	System.out.println("Mandada petición"); System.out.flush();		
		
		BufferedReader finc = new BufferedReader(new InputStreamReader(s.getInputStream()));
		char c = (char) finc.read();
		while (c != (char)0) {
			System.out.print(c);
			c = (char) finc.read();
		}
		System.out.println();
		
		s.close();
	System.out.println("Conexión terminada"); System.out.flush();
	}

}
