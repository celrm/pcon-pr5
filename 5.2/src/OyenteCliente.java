import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OyenteCliente extends Thread {
	final static String origen = "S";
	private Socket s;
	private String destino;
	private ObjectInputStream fin;
	private ObjectOutputStream fout;
	/*
	Implementa el interfaz “Runnable” o hereda de la clase “Th-read”,  
	y  es  usada  para  proporcionar  concurrencia  respecto  a  las  sesiones  de  cada
	usuario con el servidor. El m ́etodo “run()” se limita a hacer lecturas del flujo de
	entrada correspondiente, realizar las acciones oportunas, y devolver los resultados
	en forma de mensajes que ser ́an enviados al usuario o usuarios involucrados.
	*/
	public OyenteCliente(Socket s) {
		this.s = s;
	}
	
	public void run() {
		try {
			fin = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			Mensaje m;
			try {
			m = (Mensaje) fin.readObject();
			Mensaje n;
			switch(m.getTipo()) {
			case CONEXION:
				guardar_usuario();
				n = new Msj_Vacio(Msj.CONFIRMACION_CONEXION,origen,destino);
				fout.writeObject(n);
				break;
			case LISTA_USARIOS:
				String lista = usuarios_sistema();
				n = new Msj_String(Msj.CONFIRMACION_LISTA_USUARIOS,origen,destino,lista);
				fout.writeObject(n);
				break;
			case CERRAR_CONEXION:
				eliminar_usuario();
				n = new Msj_Vacio(Msj.CONFIRMACION_CERRAR_CONEXION,origen,destino);
				fout.writeObject(n);
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_String) m).getContent();
				String destino2 = buscar_usuario(fichero);
				ObjectOutputStream fout2 = buscar_output(destino2);
				n = new Msj_String(Msj.EMITIR_FICHERO,origen,destino2,destino);
				fout2.writeObject(n);
				break;
			case PREPARADO_CLIENTESERVIDOR:
				destino = ((Msj_String) m).getContent();
				fout = buscar_output(destino);
				n = new Msj_Vacio(Msj.PREPARADO_SERVIDORCLIENTE,origen,destino);
				fout.writeObject(n);
				break;
			default:
				break;
			}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private ObjectOutputStream buscar_output(String destino2) {
		// TODO Auto-generated method stub
		return null;
	}

	private String buscar_usuario(String fichero) {
		return null;
		// TODO Auto-generated method stub
		
	}

	private void eliminar_usuario() {
		// TODO Auto-generated method stub
		
	}

	private String usuarios_sistema() {
		return null;
		// TODO Auto-generated method stub
		
	}

	private void guardar_usuario() {
		// TODO Auto-generated method stub
		
	}
}
