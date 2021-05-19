package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class Compartido {
	private HashMap<String,Usuario> usuarios;
	
	// Problema de readers-writers usando las tecnicas de la practica 4: monitores
	// Prioridad a los readers
	
	private int nreaders;
	private int nwriters;
	private final ReentrantLock l;
	private final Condition condr;
	private final Condition condw;
	
	public Compartido() {
		usuarios = new HashMap<String,Usuario> ();
		nreaders = 0;
		nwriters = 0;
		l = new ReentrantLock(true);
		condr = l.newCondition();
		condw = l.newCondition();
	}
	private void entrada_lectura() {
		l.lock();
		while(nwriters > 0)
			try {
				condr.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		++ nreaders;
	}
	private void salida_lectura() {
		--nreaders;
		if (nreaders == 0) condw.signal();
		l.unlock();
	}

	// READ
	ObjectOutputStream buscar_output(String usuario) {
		entrada_lectura();
		Usuario u = usuarios.get(usuario);
		ObjectOutputStream fout = u.getOutput();
		salida_lectura();
		return fout; // este lo pueden tener varios
	}
	 String buscar_usuario(String fichero)  {
		entrada_lectura();
		String ret = null;
		for(Usuario u : usuarios.values())
			if(u.isConnected())
				for(String f : u.getArchivos())
					if(f.equals(fichero))
						ret =  u.getId();
		salida_lectura();
		return ret;
	}
	 String usuarios_sistema() {
		entrada_lectura();
		String lista = "";
		for(Usuario u : usuarios.values()) {
			if(u.isConnected()) {
				lista = lista.concat(u.getId()).concat(": ");
				lista = lista.concat(u.getArchivos().toString()).concat("\n");
			}
		}
		salida_lectura();
		return lista;
	}
	 List<String> ficheros_usuario(String usuario) {
		entrada_lectura();
		ArrayList<String> ret;
		Usuario u = usuarios.get(usuario);
		if(u == null) ret =  null;
		else ret = new ArrayList<>(u.getArchivos());
		salida_lectura();
		return ret;
	}

	private void entrada_escritura() {
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		++ nwriters;
	}
	private void salida_escritura() {
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
	}
	// WRITE
	void anadir_usuario(Usuario user) {
		entrada_escritura();
		usuarios.put(user.getId(), user);
		salida_escritura();
	}
	 boolean eliminar_usuario(String usuario) {
		entrada_escritura();
		boolean ret;
		Usuario u = usuarios.get(usuario);
		if(u == null) ret= false;
		else {
			u.setConnected(false);
			ret= true;
		}
		salida_escritura();
		return ret;
	}
	 boolean guardar_usuario(String usuario, String ip, ObjectOutputStream fout)  {
		entrada_escritura();
		boolean ret;
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			u = new Usuario(usuarios.size(), usuario, ip, null);
			usuarios.put(usuario, u);
		}
		if(u.isConnected()) ret = false; 
		else {
			u.setIp(ip);
			u.setOutput(fout);
			u.setConnected(true);
			ret = true;
		}
		salida_escritura();
		return ret;
		
	}
	 boolean anadir_fichero(String f_add, String usuario){
		entrada_escritura();
		boolean ret;
		Usuario u = usuarios.get(usuario);
		if(u == null || u.getArchivos().contains(f_add)) {
			ret = false;
		}
		else {
			u.addArchivo(f_add);
			ret = true;
		}
		salida_escritura();
		return ret; 
	}
	 public boolean eliminar_fichero(String f_del, String usuario)  {
		entrada_escritura();
		boolean ret;
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			ret =  false;
		}
		else {
			ret = u.deleteArchivo(f_del);
		}
		salida_escritura();
		return ret; 
	}
}
