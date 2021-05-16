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
	public Receptor(String r,String ip, int puerto) {
		this.ip=ip;
		this.puerto=puerto;
		receptor = r;
	}
	public void run() {
		try {
			Socket s = new Socket(ip,puerto);
			ObjectInputStream finc = new ObjectInputStream(s.getInputStream());
			Msj_File m = (Msj_File) finc.readObject();
			
			receive(m);
			s.close();
		} catch (IOException | ClassNotFoundException e) {
			JOptionPane.showMessageDialog(null, "El fichero no ha podido ser recibido.",
					"Error de recepción", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	private void receive(Msj_File m) throws IOException {
		String ruta = "ficheros/"+receptor+"/"+m.getName();
		
		File f = new File(ruta);
		if (!f.createNewFile())
			JOptionPane.showMessageDialog(null, "El fichero "+m.getName()+ " ya existe.",
					"Error de sobreescritura", JOptionPane.ERROR_MESSAGE);
        else {
			String content = m.getContent();
		    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
		    bw.write(content);
		    bw.close();
			JOptionPane.showMessageDialog(null, content,
					m.getName(), JOptionPane.PLAIN_MESSAGE);
		    // plantear modificar usuarios
        }
	}
}
