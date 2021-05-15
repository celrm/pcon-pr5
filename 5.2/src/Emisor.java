import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Emisor extends Thread {
	private String emisor;
	private String receptor;
	private int puerto;
	private String fichero;
	public Emisor(String e, String r, int puerto,String fichero) {
		emisor=e;
		receptor=r;
		this.puerto=puerto;
		this.fichero = fichero; // pregunta: dónde se crea el serversocket
	}
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(puerto);
			Socket s = ss.accept();
			ObjectOutputStream fout = new ObjectOutputStream(s.getOutputStream());

			Msj_File send = create();			
			fout.writeObject(send);
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Msj_File create() throws FileNotFoundException {
		String ruta = emisor+"/"+fichero;
	    Scanner sc = new Scanner(new File(ruta));

	    String content = ""; 
	    while(sc.hasNextLine()){
			String line = sc.nextLine();
			content = content.concat(line + "\n");                     
		}
		sc.close();
		content = content.substring(0,content.length()-1);
		Msj_File send = new Msj_File(Msj.ENVIAR_FICHERO,emisor,receptor);
		send.setName(fichero);
		send.setContent(content);
		return send;
	}
}
