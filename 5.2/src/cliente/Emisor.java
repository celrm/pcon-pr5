package cliente;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import ambos.*;

public class Emisor extends Thread {
	private String emisor;
	private String receptor;
	private ServerSocket ss;
	private String fichero;
	public Emisor(String e, String r, ServerSocket ss,String fichero) {
		emisor=e;
		receptor=r;
		this.ss=ss;
		this.fichero = fichero;
	}
	public void run() {
		try {
			Socket s = ss.accept();
			ObjectOutputStream fout = new ObjectOutputStream(s.getOutputStream());

			Msj_File send = create();			
			fout.writeObject(send);
			ss.close();
		} catch (IOException e) {
			System.out.println("El fichero "+fichero+ " no ha podido enviarse a "+receptor+" por:\n"+  e.getMessage()); System.out.flush();
			e.printStackTrace();
		}
	}
	
	private Msj_File create() throws FileNotFoundException {
		String ruta = "ficheros/"+emisor+"/"+fichero;
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
