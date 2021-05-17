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
		this.id = id;
		this.setIp(ip);
		if(archivos != null)
			this.archivos = archivos;
		else
			this.archivos = new ArrayList<>();
	}

	String getId() {
		return id;
	}

	ArrayList<String> getArchivos() {
		return archivos;
	}
	
	void addArchivo(String archivo) {
		this.archivos.add(archivo);
	}

	String getIp() {
		return ip;
	}

	void setIp(String ip) {
		this.ip = ip;
	}

	ObjectOutputStream getOutput() {
		return fout;
	}

	void setOutput(ObjectOutputStream fout) {
		this.fout = fout;
	}

	int getNumber() {
		return number;
	}

	void setNumber(int number) {
		this.number = number;
	}

	boolean isConnected() {
		return connected;
	}

	void setConnected(boolean connected) {
		this.connected = connected;
	}
}
