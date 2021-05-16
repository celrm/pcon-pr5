package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Usuario {
	private int number;
	private String id;
	private String ip;
	private ArrayList<String> archivos;
	private ObjectOutputStream fout; // to connect with servidor
	private boolean connected = false;
	
	public Usuario(int number, String id, String ip, ArrayList<String> archivos) {
		this.setNumber(number);
		this.setId(id);
		this.setIp(ip);
		this.setArchivos(archivos);
	}

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getArchivos() {
		return archivos;
	}

	private void setArchivos(ArrayList<String> archivos) {
		this.archivos = archivos;
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
