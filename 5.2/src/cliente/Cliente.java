package cliente;

import java.io.IOException;
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
	volatile static Semaphore flow = new Semaphore(0); // es un poco tonto, solo para esperar respuesta de OS

	public static void main(String[] args) throws Exception {
		ip = Inet4Address.getLocalHost().getHostAddress();
		
		input_usuario();
		Socket s = new Socket(Servidor.ip,Servidor.puerto);
		foutc = new ObjectOutputStream(s.getOutputStream());
		(new OyenteServidor(s,foutc)).start();
		
		Msj_Information n = new Msj_Information(Msj.CONEXION,usuario,Servidor.origen);
		n.putContent(ip);
		foutc.writeObject(n); foutc.flush();
		
		opciones();
	}

	private static void input_usuario() {
		usuario = JOptionPane.showInputDialog("Introduzca usuario");
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
			String fichero = JOptionPane.showInputDialog(null,
					"Introduzca fichero",usuario,JOptionPane.QUESTION_MESSAGE);
			if(fichero != null) {
				send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
				send.putContent(fichero);
				foutc.writeObject(send); foutc.flush();
			}
			else flow.release();
			break;
		case 2:
			menu_modificar();
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
				null, "Introduzca acción",
				usuario, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null,
				options, options[1]);
		return number;
	}
	private static void menu_modificar() throws IOException {
		Object[] options = 
			{"Añadir fichero",
	        "Quitar fichero",
	        "Cancelar"};
		int number = JOptionPane.showOptionDialog(
				null, "Introduzca acción",
				usuario, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null,
				options, options[1]);
		Msj_Information send;
		switch(number) {
		case 0:		
			String fichero = JOptionPane.showInputDialog(null,
				"Introduzca fichero a añadir",usuario,JOptionPane.QUESTION_MESSAGE);
			if(fichero != null) {
				send = new Msj_Information(Msj.ANADIR_FICHERO,usuario,Servidor.origen);
				send.putContent(fichero);
				foutc.writeObject(send); foutc.flush();
			}
			else flow.release();
			break;
		case 1:
			send = new Msj_Information(Msj.ELIMINAR_ALGUN_FICHERO,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
		case 2: 
			flow.release(); 
			break;
		}
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
}
