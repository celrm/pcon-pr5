package servidor;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class Compartido {
	private HashMap<String,Usuario> usuarios;
	
	//Problema de readers-writers usando las tecnicas de la practica 4: monitores
	
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

	// READ
	ObjectOutputStream buscar_output(String usuario) {
		l.lock();
		while(nwriters > 0)
			try {
				condr.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nreaders;
		Usuario u = usuarios.get(usuario);
		--nreaders;
		if (nreaders == 0) condw.signal();
		l.unlock();
		return u.getOutput();
	}
	 String buscar_usuario(String fichero)  {
		String ret = null;
		l.lock();
		while(nwriters > 0)
			try {
				condr.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++nreaders;
		for(Usuario u : usuarios.values())
			if(u.isConnected()) // problema
				for(String f : u.getArchivos())
					if(f.equals(fichero))
						ret =  u.getId();
		--nreaders;
		if (nreaders == 0) condw.signal();
		l.unlock();
		return ret;
	}
	 String usuarios_sistema() {
		String lista = "";
		l.lock();
		while(nwriters > 0)
			try {
				condr.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++nreaders;
		for(Usuario u : usuarios.values()) {
			if(u.isConnected()) { // problema
				lista = lista.concat(u.getId()).concat(": ");
				lista = lista.concat(u.getArchivos().toString()).concat("\n");
			}
		}
		--nreaders;
		if (nreaders == 0) condw.signal();
		l.unlock();
		return lista;
	}
	 List<String> ficheros_usuario(String usuario) {
		ArrayList<String> ret;
		l.lock();
		while(nwriters > 0)
			try {
				condr.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++nreaders;
		Usuario u = usuarios.get(usuario);
		if(u == null) ret =  null;
		else ret = new ArrayList<>(u.getArchivos());
		--nreaders;
		if (nreaders == 0) condw.signal();
		l.unlock();
		return ret;
	}
	
	// WRITE
	void anadir_usuario(Usuario user) {
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nwriters;
		usuarios.put(user.getId(), user);
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
	}
	 boolean eliminar_usuario(String usuario) {
		boolean ret;
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nwriters;
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			ret= false;
		}
		else {
			u.setConnected(false);
			ret= true;
		}
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
		return ret;
	}
	 boolean guardar_usuario(String usuario, String ip, ObjectOutputStream fout)  {
		boolean ret;
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nwriters;
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			u = new Usuario(usuarios.size(), usuario, ip, null);
			usuarios.put(usuario, u);
		}
		if(u.isConnected()) {
			ret=  false;
		}
		else {
		u.setIp(ip);
		u.setOutput(fout);
		u.setConnected(true);
		ret =true;
		}
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
		return ret;
		
	}
	 boolean anadir_fichero(String f_add, String usuario){
		boolean ret;
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nwriters;
		Usuario u = usuarios.get(usuario);
		if(u == null || u.getArchivos().contains(f_add)) { // problema?
			ret = false;
		}
		else {
			u.addArchivo(f_add);
			ret = true;
		}
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
		return ret; 
	}
	 public boolean eliminar_fichero(String f_del, String usuario)  {
		boolean ret;
		l.lock();
		while (nreaders > 0 || nwriters > 0)
			try {
				condw.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		++ nwriters;
		Usuario u = usuarios.get(usuario);
		if(u == null) {
			ret =  false;
		}
		else {
			ret = u.deleteArchivo(f_del);
		}
		--nwriters;
		condw.signal();
		condr.signalAll();
		l.unlock();
		return ret; 
	}
}
