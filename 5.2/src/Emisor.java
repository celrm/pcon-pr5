
public class Emisor extends Thread {
// TODO
	private String ip;
	private int puerto;
	private String fichero;
	public Emisor(String ip, int puerto,String fichero) {
		this.ip=ip;
		this.puerto=puerto;
		this.fichero = fichero;
	}
	public void run() {
		System.out.println("Would wait for port "+puerto+" in ip " +ip);
		// TODO crear proceso emisor y esperar en accept la conexión
		// con puerto arriba
	}
}
