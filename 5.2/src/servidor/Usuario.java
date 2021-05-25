package servidor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Usuario {
	private String id;
	private String ip;
	private List<String> archivos;
	private boolean connected = false;
	
	public Usuario(String id, String ip, List<String> archivos) {
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

	boolean isConnected() { return connected; }

	void setConnected(boolean connected) { this.connected = connected; }
}
