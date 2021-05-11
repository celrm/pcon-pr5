import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
	
	/*
	 Clase principal de la aplicaci ́on cliente. Tendr ́a al menos los siguientes
	 atributos: nombre de usuario, direcci ́on ip de la m ́aquina. Puedes tener tambi ́en como
	 atributos  los  objetos  que  proporcionan  la  comunicaci ́on  con  el  servidor  (socket  y
	 flujos). Es responsable de llevar a cabo la comunicaci ́on con el servidor, y cuando
	 sea necesario ejecutar el env ́ıo o recepci ́on de informaci ́on. Adem ́as ofrece el soporte
	 para la interacci ́on con el usuario del sistema 
	 */
	private static String usuario;
	static String ip = "localhost";
	static Socket s;
	private static ObjectInputStream finc;
	private static ObjectOutputStream foutc;
	private static Scanner keyboard;

	public static void main(String[] args) throws Exception {
		/*
		 	 
		 Una vez iniciada la sesi ́on, el cliente puede realizar dos tipos de acciones: consultar
		 el nombre de todos los usuarios conectados y la informaci ́on que poseen, o descargar
		 informaci ́on.
		 
		 Una vez el usuario elija la informaci ́on a descargar, comenzar ́a el proceso de descarga
		 (en realidad se descarga directamente de la m ́aquina del usuario propietario) de tal
		 forma que el programa cliente siga su curso natural, y en particular permitiendo que
		 se realicen otras acciones e incluso otras descargas mientras continua la descarga de
		 la primera informaci ́on.
		 
		 Al margen de la voluntad del usuario, el programa cliente puede actuar como emisor
		 de  cualquier  informaci ́on  de  la  que  dispone  compartida,  como  propietario  de  una
		 informaci ́on que otro cliente solicite. Esta acci ́on ser ́a llevada a cabo en un segundo
		 plano permitiendo al usuario continuar con el uso normal de la aplicaci ́on.
		 
		 Al terminar la aplicaci ́on se deber ́a comunicar el fin de sesi ́on al servidor, y permitir
		 as ́ı que  ́este actualice apropiadamente su base de datos.
		 
		 */
		
		keyboard = new Scanner(System.in);
		System.out.print("Introduzca usuario: ");
		usuario = keyboard.nextLine();
		
		s = new Socket(Servidor.ip,Servidor.puerto);
		(new OyenteServidor(s,usuario)).start(); // segundo plano para "dar" info
		
		finc = new ObjectInputStream(s.getInputStream());
		foutc = new ObjectOutputStream(s.getOutputStream());
		
		Mensaje n = new Msj_Vacio(Msj.CONEXION,usuario,OyenteCliente.origen);
		foutc.writeObject(n); // recuerda los flushes

		recursion();
		
		keyboard.close();
	}

	private static void recursion() throws Exception {
		Mensaje m;
		int opcion = menu_opcion();
		while(opcion < 1 || opcion > 3) opcion = menu_opcion();
		switch(opcion) {
		case 1:
			m = new Msj_Vacio(Msj.LISTA_USARIOS,usuario,OyenteCliente.origen);
			foutc.writeObject(m);
			m = (Mensaje) finc.readObject();
			if (m.getTipo() != Msj.CONFIRMACION_LISTA_USUARIOS)
				throw new Exception("Esperaba confirmación de lista usuarios");
			System.out.println(((Msj_String) m).getContent());
			break;
		case 2:
			System.out.print("Introduzca fichero: ");
			String fichero = keyboard.nextLine();
			m = new Msj_String(Msj.PEDIR_FICHERO,usuario,OyenteCliente.origen,fichero);
			foutc.writeObject(m);
			// TODO protocolo conseguir fichero
			break;
		case 3:
			m = new Msj_Vacio(Msj.CERRAR_CONEXION,usuario,OyenteCliente.origen);
			foutc.writeObject(m);
			m = (Mensaje) finc.readObject();
			if (m.getTipo() != Msj.CONFIRMACION_CERRAR_CONEXION)
				throw new Exception("Esperaba confirmación de cierre de conexión");
			break;		
		}
		if(opcion != 3) recursion();		
	}

	private static int menu_opcion() {
		System.out.println("\t(1) Consultar lista usuarios");
		System.out.println("\t(2) Pida fichero");
		System.out.println("\t(3) Salir");
		System.out.print("Introduzca acción: ");
		return keyboard.nextInt();
	}

}
