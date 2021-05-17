package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Compartido {
	private HashMap<String,Usuario> usuarios;
	public Compartido() {
		usuarios = new HashMap<String,Usuario> ();
	}

	// READ
	synchronized ObjectOutputStream buscar_output(String usuario) {
		Usuario u = usuarios.get(usuario);
		return u.getOutput();
	}
	synchronized String buscar_usuario(String fichero) {
		for(Usuario u : usuarios.values())
			if(u.isConnected()) // problema
				for(String f : u.getArchivos())
					if(f.equals(fichero))
						return u.getId();
		return null;
	}
	synchronized String usuarios_sistema() {
		String lista = "";
		for(Usuario u : usuarios.values()) {
			if(u.isConnected()) { // problema
				lista = lista.concat(u.getId()).concat(": ");
				lista = lista.concat(u.getArchivos().toString()).concat("\n");
			}
		}
		return lista;
	}
	synchronized List<String> ficheros_usuario(String usuario) {
		Usuario u = usuarios.get(usuario);
		if(u == null) return null;
		return new ArrayList<>(u.getArchivos());
	}
	
	// WRITE
	synchronized void anadir_usuario(Usuario user) {
		usuarios.put(user.getId(), user);
	}
	synchronized boolean eliminar_usuario(String usuario) {
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			return false;
		}
		u.setConnected(false);
		return true;
	}
	synchronized boolean guardar_usuario(String usuario, String ip, ObjectOutputStream fout) {
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			u = new Usuario(usuarios.size(), usuario, ip, null);
			usuarios.put(usuario, u);
		}
		if(u.isConnected()) return false;
		u.setIp(ip);
		u.setOutput(fout);
		u.setConnected(true);
		return true;
	}
	synchronized boolean anadir_fichero(String f_add, String usuario) {
		Usuario u = usuarios.get(usuario);
		if(u == null || u.getArchivos().contains(f_add)) { // problema?
			return false;
		}
		u.addArchivo(f_add);
		return true;
	}
	synchronized public boolean eliminar_fichero(String f_del, String usuario) {
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			return false;
		}
		return u.deleteArchivo(f_del);
	}
}
