package servidor;

import java.io.ObjectOutputStream;
import java.util.List;


public class Compartido {
	private MonitorLock usuarios;
	private MonitorSem canales;
	
	public Compartido() {
		usuarios = new MonitorLock();
		canales = new MonitorSem();
	}

	// READ
	ObjectOutputStream buscar_output(String usuario) {
		return canales.buscar_output(usuario);
	}
	String buscar_usuario(String fichero)  {
		return usuarios.buscar_usuario(fichero);
	}
	String usuarios_sistema() {
		return usuarios.usuarios_sistema();
	}
	List<String> ficheros_usuario(String usuario) {
			return usuarios.ficheros_usuario(usuario);
	}
	// WRITE
	void anadir_usuario(Usuario user) {
		usuarios.anadir_usuario(user);
	}
	boolean eliminar_usuario(String usuario) {
		return usuarios.eliminar_usuario(usuario);
	}
	boolean guardar_usuario(String usuario, String ip, ObjectOutputStream fout)  {
		boolean ponerfout = canales.poner_output(usuario,fout);
		boolean guardarus = usuarios.guardar_usuario(usuario,ip);
		return ponerfout && guardarus;
	}
	boolean anadir_fichero(String f_add, String usuario){
		return anadir_fichero(f_add,usuario);
	}
	boolean eliminar_fichero(String f_del, String usuario)  {
		 return usuarios.eliminar_fichero(f_del,usuario);
	}
}
