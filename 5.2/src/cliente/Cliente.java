package cliente;

import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import javax.swing.JOptionPane;

import ambos.*;
import servidor.Servidor;

public class Cliente {
	private static String usuario;
	private static String ip;
	private static ObjectOutputStream foutc;

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
		usuario = JOptionPane.showInputDialog("Introduzca usuario");
		boolean error = true;
		if(usuario.equals(Servidor.origen)) {
			JOptionPane.showMessageDialog(null, "Usuario protegido.", 
					"Error de usuario", JOptionPane.ERROR_MESSAGE);
		} // TODO más errores: comprobar si está en la lista de usuarios
		else error = false;
		if(error) input_usuario();
	}

	private static void opciones() throws Exception {
		Msj_Information send;
		int opcion = menu_opcion();
		switch(opcion) {
		case 0:
			send = new Msj_Information(Msj.LISTA_USARIOS,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			break;
		case 1:
			String fichero = JOptionPane.showInputDialog("Introduzca fichero");
			send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
			send.setContent1(fichero);
			foutc.writeObject(send); foutc.flush();
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
				null, "Estas son las opciones disponibles:",
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
}
