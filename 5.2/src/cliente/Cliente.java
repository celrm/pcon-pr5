package cliente;

import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ambos.Msj;
import ambos.Msj_Information;
import servidor.Servidor;

public class Cliente {
	private static String usuario;
	private static String ip;
	private static ObjectOutputStream foutc = null;
	final static JPanel parent = new JPanel();
	volatile static Semaphore flow = new Semaphore(0); // al principio tengo la atención

	public static void main(String[] args) throws Exception {
		ip = Inet4Address.getLocalHost().getHostAddress();
		
		input_usuario();
		Socket s = new Socket(Servidor.ip,Servidor.puerto);
		foutc = new ObjectOutputStream(s.getOutputStream());
		(new OyenteServidor(s,foutc)).start();
		
		Msj_Information n = new Msj_Information(Msj.CONEXION,usuario,Servidor.origen); // al recibir la confirmación de conexión se hace el release 
		n.putContent(ip);
		foutc.writeObject(n); foutc.flush();
		
		opciones();
	}

	private static void input_usuario() {
		usuario = JOptionPane.showInputDialog(parent,"Introduzca usuario");
		boolean error = true;
		if(usuario == null) {
			error("No has iniciado sesión");
		}
		else if(usuario.equals(Servidor.origen)) {
			error("Usuario protegido");
		}
		else error = false;
		if(error) input_usuario();
	}

	private static void opciones() throws Exception {
		flow.acquire();
		Msj_Information send;
		int opcion = menu_opcion();
		switch(opcion) {
		case 0:
			send = new Msj_Information(Msj.LISTA_USARIOS,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			break;
		case 1:
			String fichero = JOptionPane.showInputDialog(parent,
					"Introduzca fichero",usuario);
			if(fichero != null) {
				send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
				send.putContent(fichero);
				foutc.writeObject(send); foutc.flush();
			}
			break;
		case 2:
			send = new Msj_Information(Msj.MODIFICAR_FICHEROS,usuario,Servidor.origen);
//			foutc.writeObject(send); foutc.flush(); // TODO debug
			flow.release();
			break;
		case 3:
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
			"Modificar mis ficheros",
	        "Salir"};
		int number = JOptionPane.showOptionDialog(
				parent, "Introduzca acción",
				usuario, JOptionPane.YES_NO_OPTION,
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
	public static void error(String content) {
		JOptionPane.showMessageDialog(new JPanel(), content, 
				usuario, JOptionPane.ERROR_MESSAGE);
	}
	public static void error_lock(String content) {
		try {
			Cliente.flow.acquire(); // esto es bastante horrible
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		error(content);
		Cliente.flow.release();
	}
}
