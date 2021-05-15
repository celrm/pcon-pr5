import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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
			Socket s = new Socket(ip,puerto); // pregunta: qué pasa si llego antes de tiempo
			ObjectInputStream finc = new ObjectInputStream(s.getInputStream());
			Msj_File m = (Msj_File) finc.readObject();
			
			receive(m);
			s.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			Cliente.terminal.release(); // cuándo recibo el archivo? puedo hacer otras cosas entre medias?
		}
	}
	
	private void receive(Msj_File m) throws IOException {
		String ruta = receptor+"/"+m.getName();

		File f = new File(ruta);
		if (!f.createNewFile())
            System.out.println("El fichero "+m.getName()+ " ya existe.");
        else {
			String content = m.getContent();
		    BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
		    bw.write(content);
		    bw.close();
            System.out.println(content);
        }
	}
}
