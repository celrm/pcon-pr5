package cliente;

import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambos.Mensaje;
import ambos.Msj;
import ambos.Msj_Information;
import servidor.Servidor;

public class Cliente {
	private static String usuario;
	private static String ip;
	private static ObjectOutputStream foutc = null;
	final static JPanel parent = new JPanel();
	volatile static Semaphore sesion = new Semaphore(0);

	public static void main(String[] args) throws Exception {
		ip = Inet4Address.getLocalHost().getHostAddress();
		
		input_usuario();
		Socket s = new Socket(Servidor.ip,Servidor.puerto);
		foutc = new ObjectOutputStream(s.getOutputStream());
		(new OyenteServidor(s,foutc)).start();
		
		Mensaje n = new Msj_Information(Msj.CONEXION,usuario,Servidor.origen);
		foutc.writeObject(n); foutc.flush();
		
		opciones();
	}

	private static void input_usuario() {
		usuario = JOptionPane.showInputDialog(parent,"Introduzca usuario");
		boolean error = true;
		if(usuario == null) {
			error("Error de usuario","No has iniciado sesión");
		}
		else if(usuario.equals(Servidor.origen)) {
			error("Error de usuario","Usuario protegido");
		} // TODO más errores: comprobar si está en la lista de usuarios
		else error = false;
		if(error) input_usuario();
	}

	private static void opciones() throws Exception {
		sesion.acquire();
		Msj_Information send;
		int opcion = menu_opcion();
		switch(opcion) {
		case 0:
			send = new Msj_Information(Msj.LISTA_USARIOS,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			break;
		case 1:
			String fichero = JOptionPane.showInputDialog(parent,"Introduzca fichero");
			if(fichero != null) {
				send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
				send.putContent(fichero);
				foutc.writeObject(send); foutc.flush();
			}
			break;
		case 2:
			send = new Msj_Information(Msj.CERRAR_CONEXION,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			return;
		}
		opciones();		
	}

	private static int menu_opcion() {
		Object[] options = 
			{"Lista de usuarios",
	        "Pedir fichero", 
	        "Salir"};
		int number = JOptionPane.showOptionDialog(
				parent, "Estas son las opciones disponibles:",
				"Introduzca acción", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null,
				options, options[1]);
		return number;
	}

	public static String ip() {
		return ip;
	}

	public static String usuario() {
		return usuario;
	}
	public static void error(String title,String content) {
		JOptionPane.showMessageDialog(new JPanel(), content, 
				title, JOptionPane.ERROR_MESSAGE);
	}
}
