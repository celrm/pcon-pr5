import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Cliente {
	private static String usuario;
	private static String ip;
	private static ObjectOutputStream foutc;
	private static Scanner keyboard;
	static Semaphore terminal; // con paso de testigo

	public static void main(String[] args) throws Exception {
		
		terminal = new Semaphore(1);
		ip = Inet4Address.getLocalHost().getHostAddress();
		
		keyboard = new Scanner(System.in);
		System.out.print("Introduzca usuario: ");
		usuario = keyboard.nextLine();
		while(usuario.equals(Servidor.origen)) {
			System.out.print("Usuario inválido. Introduzca usuario: ");
			usuario = keyboard.nextLine();
		}		
		Socket s = new Socket(Servidor.ip,Servidor.puerto);
		foutc = new ObjectOutputStream(s.getOutputStream());

		(new OyenteServidor(s,foutc)).start();
		
		terminal.acquire();
		Mensaje n = new Msj_Information(Msj.CONEXION,usuario,Servidor.origen);
		foutc.writeObject(n); foutc.flush();
		
		opciones();

		keyboard.close();
	}

	private static void opciones() throws Exception {
		Msj_Information send;
		terminal.acquire();
		int opcion = menu_opcion();
		while(opcion < 1 || opcion > 3) opcion = menu_opcion();
		switch(opcion) {
		case 1:
			send = new Msj_Information(Msj.LISTA_USARIOS,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			break;
		case 2:
			System.out.print("Introduzca fichero: "); 
			String fichero = keyboard.nextLine();
			send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
			send.setContent1(fichero);
			foutc.writeObject(send); foutc.flush();
			break;
		case 3:
			send = new Msj_Information(Msj.CERRAR_CONEXION,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			return;
		}
		opciones();		
	}

	private static int menu_opcion() {
		String mensaje = "\t(1) Consultar lista usuarios\n" +
				"\t(2) Pida fichero\n" +
				"\t(3) Salir\n"+
				"Introduzca acción: ";
		System.out.println(mensaje); System.out.flush();
		return Integer.parseInt(keyboard.nextLine()); 
	}

	public static String ip() {
		return ip;
	}

	public static String usuario() {
		return usuario;
	}
}
