package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Usuario {
	private int number;
	private String id;
	private String ip;
	private List<String> archivos;
	private ObjectOutputStream fout;
	private boolean connected = false;
	
	public Usuario(int number, String id, String ip, List<String> archivos) {
		this.setNumber(number);
		this.id = id;
		this.setIp(ip);
		if(archivos != null)
			this.archivos = archivos;
		else
			this.archivos = new ArrayList<>();
	}

	String getId() { return id; }

	Collection<String> getArchivos() { return Collections.unmodifiableCollection(archivos); }
	
	void addArchivo(String archivo) { this.archivos.add(archivo); }

	boolean deleteArchivo(String f_del) { return this.archivos.remove(f_del); }

	String getIp() { return ip; }

	void setIp(String ip) { this.ip = ip; }

	ObjectOutputStream getOutput() { return fout; }

	void setOutput(ObjectOutputStream fout) { this.fout = fout; }

	int getNumber() { return number; }

	void setNumber(int number) { this.number = number; }

	boolean isConnected() { return connected; }

	void setConnected(boolean connected) { this.connected = connected; }
}
