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

	synchronized String getId() { return id; }

	synchronized Collection<String> getArchivos() { return Collections.unmodifiableCollection(archivos); }
	
	synchronized void addArchivo(String archivo) { this.archivos.add(archivo); }

	synchronized boolean deleteArchivo(String f_del) { return this.archivos.remove(f_del); }

	synchronized String getIp() { return ip; }

	synchronized void setIp(String ip) { this.ip = ip; }

	synchronized ObjectOutputStream getOutput() { return fout; }

	synchronized void setOutput(ObjectOutputStream fout) { this.fout = fout; }

	synchronized int getNumber() { return number; }

	synchronized void setNumber(int number) { this.number = number; }

	synchronized boolean isConnected() { return connected; }

	synchronized void setConnected(boolean connected) { this.connected = connected; }
}
