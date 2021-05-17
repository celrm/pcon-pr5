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
		for(Usuario u : usuarios.values())
			if(u.isConnected())
				for(String f : u.getArchivos())
					if(f.equals(fichero))
						return u.getId();
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

	void guardar_usuario(String usuario, String ip, ObjectOutputStream fout) {
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			u = new Usuario(usuarios.size(), usuario, ip, null);
			usuarios.put(usuario, u);
		}
		u.setIp(ip);
		u.setOutput(fout);
		u.setConnected(true);
	}
	public int buscar_num(String origen) {
		for(Usuario u : usuarios.values())
			if(u.getId().equals(origen) && u.isConnected())
				return u.getNumber();
		return -1;
	}
}
