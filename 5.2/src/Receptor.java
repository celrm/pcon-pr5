
public class Receptor extends Thread {
// TODO
	private String ip;
	private int puerto;
	public Receptor(String ip, int puerto) {
		this.ip=ip;
		this.puerto=puerto;
	}
	public void run() {
		System.out.println("Would connect at port "+puerto+" in ip " +ip);
		// TODO crear proceso emisor y esperar en accept la conexión
		// con puerto arriba
	}
}
