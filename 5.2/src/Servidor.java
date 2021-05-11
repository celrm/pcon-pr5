import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	/*
	Clase  principal  de  la  aplicaci ́on  servidor.  Tendr ́a  como  atributo
	una  o  varias  estructuras  de  datos  que  contendr ́an  la  informaci ́on  de  los  usuarios.
	
	El servidor espera la llegada de peticiones de inicio de sesi ́on, y asocia un hilo de
	ejecuci ́on con cada usuario.
	*/
	private static ServerSocket ss;
	static int puerto = 500;
	static String ip = "localhost";

	public static void main(String[] args) throws IOException {
		init();
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
		TODO Al iniciarse, leer ́a de un fichero “users.txt” la informaci ́on de los usuarios registrados
		en el sistema y todos aquellos datos relativos a  ́estos que consideres oportunos.
		*/
		
	}

}
