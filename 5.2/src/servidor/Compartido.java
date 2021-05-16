package servidor;

import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Compartido {
	private HashMap<String,Usuario> usuarios;
	public Compartido() {
		usuarios = new HashMap<String,Usuario> ();

	}
	void anadir_usuario(Usuario user) {
		usuarios.put(user.getId(), user);
	}

	ObjectOutputStream buscar_output(String usuario) {
		Usuario u = usuarios.get(usuario);
		return u.getOutput();
	}

	String buscar_usuario(String fichero) {
		for(Usuario u : usuarios.values()) {			
			for(String f : u.getArchivos()) {
				if(f.equals(fichero) && u.isConnected())
					return u.getId();
			}
		}
		return null;
	}

	void eliminar_usuario(String usuario) {
		Usuario u = usuarios.get(usuario);
		u.setConnected(false);
	}

	String usuarios_sistema() {
		String lista = "";
		for(Usuario u : usuarios.values()) {
			lista = lista.concat(u.getId()).concat(": ");
			lista = lista.concat(u.getArchivos().toString()).concat("\n");
		}
		return lista;
	}

	void guardar_usuario(String usuario, ObjectOutputStream fout) {
		Usuario u = usuarios.get(usuario);
		u.setOutput(fout);
		u.setConnected(true);
	}
}
