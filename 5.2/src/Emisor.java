
public class Emisor extends Thread {
// TODO
	private String ip;
	private int puerto;
	public Emisor(String ip, int puerto) {
		this.ip=ip;
		this.puerto=puerto;
	}
	public void run() {
		System.out.println("Would wait for port "+puerto+" in ip " +ip);
		// TODO crear proceso emisor y esperar en accept la conexión
		// con puerto arriba
	}
}
