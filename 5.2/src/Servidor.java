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
	/*
	Clase  principal  de  la  aplicaci ́on  servidor.  Tendr ́a  como  atributo
	una  o  varias  estructuras  de  datos  que  contendr ́an  la  informaci ́on  de  los  usuarios.
	
	El servidor espera la llegada de peticiones de inicio de sesi ́on, y asocia un hilo de
	ejecuci ́on con cada usuario.
	*/
	final static String origen = "S";
	private static ServerSocket ss;
	static int puerto = 500;
	static String ip = "localhost";
	private static HashMap<String,Usuario> usuarios;

	public static void main(String[] args) throws IOException {
		usuarios = new HashMap<String,Usuario> ();
//		init();
		/*		
		El servidor atiende de forma concurrente todas las peticiones que realizan los clientes
		conectados al sistema, en particular:
		
		•Solicitud de b ́usqueda de usuarios conectados: El servidor realiza una b ́usqueda
		en su base de datos y devuelve los resultados obtenidos.
		
		•Solicitud de descarga de informaci ́on: El servidor se comunica con los dos clientes
		en cuesti ́on, gestionando el inicio de la comunicaci ́on p2p entre ellos. Una
		vez los clientes establecen conexi ́on el servidor se desentiende de la comunicaci ́on p2p.
		
		•Fin de sesi ́on: Se actualiza apropiadamente la bases de datos.
		*/
		ss = new ServerSocket(puerto);
		Socket s;
		while (true) {
	       s = ss.accept();
	       (new OyenteCliente(s)).start();
	    }
	}

	private static void init() {
		/*
	 Al iniciarse, leer ́a de un fichero “users.txt” la informaci ́on de los usuarios registrados
		en el sistema y todos aquellos datos relativos a  ́estos que consideres oportunos.
		*/
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
		String[] parse1 = linea.substring(0,linea.length()-1).split(" ", 2);
		String id = parse1[0];
		String[] parse2 = parse1[1].split(" ",2);
		String ip = parse2[0];
		ArrayList<String>compartido= new ArrayList<String> (Arrays.asList(parse2[1].split(" ")));
		return new Usuario(id,ip,compartido);
		
	}

}

