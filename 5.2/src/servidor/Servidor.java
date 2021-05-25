package servidor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
ALUMNOS: Celia Rubio Madrigal, María Arranz Lobo
*/

public class Servidor {
	public final static String origen = "S";
	public final static String ip = "localhost";
	public final static int puerto = 500;
	
	private static ServerSocket ss;
	
	static Compartido datos;

	public static void main(String[] args) throws IOException {
		datos = new Compartido();
		init();
		
		ss = new ServerSocket(puerto);
		while (true) {
			Socket s = ss.accept();
			(new OyenteCliente(s,datos)).start();
	    }
	}

	private static void init() {
		try {
			File file = new File("servidor/users.txt");
		    Scanner lectura = new Scanner(file);
		    while (lectura.hasNextLine()) {
		    	Usuario us = parseUsuario(lectura.nextLine());
		    	datos.anadir_usuario(us);
		    	System.out.println("Insertando: "+ us.getId());
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
		ArrayList<String> archivos = new ArrayList<String> (Arrays.asList(parse2[1].split(" ")));
		return new Usuario(id,ip,archivos);
	}

}

