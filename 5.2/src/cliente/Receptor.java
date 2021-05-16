package cliente;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

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
	// TODO dar prioridad a los errores
	// TODO poner el semáforo en el error
	// está fuera de la recepción del paso de testigo así que tiene que esperar a que alguien le deje. 
	public void run() {
		try {
			Socket s = new Socket(ip,puerto);
			ObjectInputStream finc = new ObjectInputStream(s.getInputStream());
			Msj_File m = (Msj_File) finc.readObject();
			
			receive(m);
			s.close();
		} catch (IOException | ClassNotFoundException | InterruptedException e) {
			try {
				Cliente.sesion.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Cliente.error("Error de recepción","El fichero "+fichero+" no ha podido ser recibido.");
			Cliente.sesion.release();
			e.printStackTrace();
		}
	}
	
	private void receive(Msj_File m) throws IOException, InterruptedException {
		String ruta = "ficheros/"+receptor+"/"+m.getName();
		
		File f = new File(ruta);
		if (!f.createNewFile()) {
			Cliente.sesion.acquire();
			Cliente.error("Error de sobreescritura","El fichero "+m.getName()+ " ya existe.");
			Cliente.sesion.release();
		}
        else {
			String content = m.getContent();
		    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
		    bw.write(content);
		    bw.close();
			Cliente.sesion.acquire();
			JOptionPane.showMessageDialog(null, content,
					m.getName(), JOptionPane.PLAIN_MESSAGE);
			Cliente.sesion.release();
		    // plantear modificar usuarios
        }
	}
}
