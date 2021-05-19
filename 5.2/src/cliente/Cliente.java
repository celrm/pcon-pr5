package cliente;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;

import javax.swing.JOptionPane;

import ambos.*;
import servidor.Servidor;

public class Cliente {
	private static String usuario = "Entrada";
	private static String ip;
	private static ObjectOutputStream foutc;
	
	private static LockTicket flow = new LockTicket();
	private static int turn = 0;
	private static void take_flow() {flow.take(turn); turn = 1-turn;};
	static void release_flow() {flow.release();};

	public static void main(String[] args) throws Exception {
		ip = Inet4Address.getLocalHost().getHostAddress();

		take_flow();
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
		usuario = input("Introduzca usuario");
		boolean error = true;
		if(usuario == null)
			error("No has iniciado sesión");
		else if(usuario.equals(Servidor.origen))
			error("Usuario protegido");
		else error = false;
		if (error) input_usuario();
	}

	private static void opciones() throws Exception {
		while(true) {
			take_flow(); // Se espera a sí mismo por el paso de testigo
			Msj_Information send;
			Object[] opts = {"Lista de usuarios", "Pedir fichero","Modificar mis ficheros","Salir"};
			switch(menu(opts)) {
			case 0:
				send = new Msj_Information(Msj.LISTA_USARIOS,usuario,Servidor.origen);
				foutc.writeObject(send); foutc.flush();
				break;
			case 1:
				String fichero = input("Introduzca fichero a pedir");
				if(fichero != null) {
					send = new Msj_Information(Msj.PEDIR_FICHERO,usuario,Servidor.origen);
					send.putContent(fichero);
					foutc.writeObject(send); foutc.flush();
				}
				else release_flow();
				break;
			case 2:
				modificar();
				break;
			case 3:
				send = new Msj_Information(Msj.CERRAR_CONEXION,usuario,Servidor.origen);
				foutc.writeObject(send); foutc.flush();
				return;
			}
		}	
	}
	private static void modificar() throws IOException {
		Object[] opts = {"Compartir fichero", "Quitar fichero", "Cancelar"};
		switch(menu(opts)) {
		case 0:
			String fichero = input("Introduzca fichero a compartir");
			if(fichero != null) {
				File f = new File("ficheros/"+usuario+"/"+fichero);
				if(!f.exists() || !f.isFile())
					error("No tienes ese fichero "+fichero);
				else {
					Msj_Information send = new Msj_Information(Msj.ANADIR_FICHERO,usuario,Servidor.origen);
					send.putContent(fichero);
					foutc.writeObject(send); foutc.flush();
					info("El fichero "+fichero+" se añadirá en el fondo");
				}
			}
			release_flow();
			break;
		case 1:
			Msj_Information send = new Msj_Information(Msj.ELIMINAR_ALGUN_FICHERO,usuario,Servidor.origen);
			foutc.writeObject(send); foutc.flush();
			break;
		case 2: 
			release_flow(); 
			break;
		}
	}

	private static int menu(Object[] options) {
		int number = JOptionPane.showOptionDialog(
				null, "Introduzca acción",
				usuario, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null,
				options, options[1]);
		return number;
	}

	static String ip() {
		return ip;
	}

	static String usuario() {
		return usuario;
	}
	
	static void error(String content) {
		JOptionPane.showMessageDialog(null, content, 
				usuario, JOptionPane.ERROR_MESSAGE);
	}

	static void info(String content) {
		JOptionPane.showMessageDialog(null, content,
				usuario, JOptionPane.INFORMATION_MESSAGE);
	}
	static String input(String content) {
		return JOptionPane.showInputDialog(null,content,
				usuario,JOptionPane.QUESTION_MESSAGE);
	}
}
