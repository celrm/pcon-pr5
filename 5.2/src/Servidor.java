import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Servidor {
	final static String origen = "S";
	private static ServerSocket ss;
	static int puerto = 500;
	static String ip = "localhost"; // todos deben saber su ip así que es protected
	static HashMap<String,Usuario> usuarios;

	public static void main(String[] args) throws IOException {
		usuarios = new HashMap<String,Usuario> ();
		init();
		
		ss = new ServerSocket(puerto);
		Socket s;
		while (true) {
	       s = ss.accept();
	       (new OyenteCliente(s)).start();
	    }
	}

	private static void init() {
		try {
			File file = new File("users.txt");
		    Scanner lectura = new Scanner(file);
		    while (lectura.hasNextLine()) {
		    	Usuario us = parseUsuario(lectura.nextLine());
		    	usuarios.put(us.getId(),us);
		    }
		    lectura.close();
		} catch (FileNotFoundException e) {
			System.out.println("users.txt not found"); System.out.flush();
		    e.printStackTrace();
		}
	}
	
	private static Usuario parseUsuario (String linea) {
		String[] parse1 = linea.split(" ", 2);
		String id = parse1[0];
		String[] parse2 = parse1[1].split(" ",2);
		String ip = parse2[0];
		ArrayList<String>compartido= new ArrayList<String> (Arrays.asList(parse2[1].split(" ")));
		return new Usuario(id,ip,compartido);
	}

}

