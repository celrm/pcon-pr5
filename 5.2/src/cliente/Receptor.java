package cliente;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import ambos.*;

public class Receptor extends Thread {
	private String receptor;
	private String ip;
	private int puerto;
	private String fichero;
	public Receptor(String r,String ip, int puerto,String fich) {
		this.ip=ip;
		this.puerto=puerto;
		receptor = r;
		fichero=fich;
	}
	
	public void run() {
		try {
			Socket s = new Socket(ip,puerto);
			ObjectInputStream finc = new ObjectInputStream(s.getInputStream());
			Msj_File m = (Msj_File) finc.readObject();
			
			receive(m);
			s.close();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			System.out.println("El fichero "+fichero+" no ha podido ser recibido.");
			e.printStackTrace();
		}
	}
	
	private void receive(Msj_File m) throws IOException, InterruptedException {
		String ruta = "ficheros/"+receptor+"/"+m.getName();
		
		File f = new File(ruta);
		if (!f.createNewFile()) {
			System.out.println("El fichero "+m.getName()+ " ya existe y no debería sobreescribirse.");
		}
        else {
			String content = m.getContent();
		    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
		    bw.write(content);
		    bw.close();
			System.out.println("Llegó el fichero " +m.getName()+
					"con contenido:\n"+content);
        }
	}
}
