package servidor;

import java.io.ObjectOutputStream;
import java.util.HashMap;


public class MonitorSem {
	private HashMap<String,ObjectOutputStream> canales;
	
	// Problema de readers-writers usando las tecnicas de la practica 3: semáforos
	
	public MonitorSem() {
		canales = new HashMap<String,ObjectOutputStream> ();
	}
	// READ
	ObjectOutputStream buscar_output(String usuario) {
		return canales.get(usuario);
	}
	// WRITE
	boolean poner_output(String usuario, ObjectOutputStream fout) {
		return canales.put(usuario, fout) != null;
	}
}
