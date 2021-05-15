import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Usuario {
	
	private String id;
	private String ip;
	private ArrayList<String> compartido;
	private ObjectOutputStream fout;
	
	public Usuario(String id, String ip, ArrayList<String> compartido) {
		this.setId(id);
		this.setIp(ip);
		this.setCompartido(compartido);
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getCompartido() {
		return compartido;
	}

	private void setCompartido(ArrayList<String> compartido) {
		this.compartido = compartido;
	}

	public String getIp() {
		return ip;
	}

	private void setIp(String ip) {
		this.ip = ip;
	}

	public ObjectOutputStream getOutput() {
		return fout;
	}

	public void setOutput(ObjectOutputStream fout) {
		this.fout = fout;
	}
}
