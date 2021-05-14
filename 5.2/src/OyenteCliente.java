import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OyenteCliente extends Thread {
	final static String origen = "S";
	private Socket s;
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
			fout = new ObjectOutputStream(s.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			Mensaje m;
			try {
			m = (Mensaje) fin.readObject();
			if(m.getDestino() != origen) continue; // está pocho
			Mensaje n;
			switch(m.getTipo()) {
			case CONEXION:
				guardar_usuario(m.getOrigen());
				n = new Msj_Vacio(Msj.CONFIRMACION_CONEXION,origen,m.getOrigen());
				fout.writeObject(n);
				break;
			case LISTA_USARIOS:
				String lista = usuarios_sistema();
				n = new Msj_String(Msj.CONFIRMACION_LISTA_USUARIOS,origen,m.getOrigen(),lista);
				fout.writeObject(n);
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_String) m).getContent();
				String destino2 = buscar_usuario(fichero);
				ObjectOutputStream fout2 = buscar_output(destino2);
				n = new Msj_MasCosas(Msj.EMITIR_FICHERO,origen,destino2);
				((Msj_MasCosas)n).setContent1(m.getOrigen());
				((Msj_MasCosas)n).setContent2(fichero);
				fout2.writeObject(n);
				break;
			case PREPARADO_CLIENTESERVIDOR:
				String receptor = ((Msj_MasCosas) m).getContent1();
				String ip_emisor = ((Msj_MasCosas) m).getContent2();
				int puerto_emisor = ((Msj_MasCosas) m).getEntero1();
				fout2 = buscar_output(receptor);
				n = new Msj_MasCosas(Msj.PREPARADO_SERVIDORCLIENTE,origen,receptor);
				((Msj_MasCosas)n).setContent1(ip_emisor);
				((Msj_MasCosas)n).setEntero1(puerto_emisor);
				fout2.writeObject(n);
				break;
			case CERRAR_CONEXION:
				eliminar_usuario(m.getOrigen());
				n = new Msj_Vacio(Msj.CONFIRMACION_CERRAR_CONEXION,origen,m.getOrigen());
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

	private void eliminar_usuario(String usuario) {
		// TODO Auto-generated method stub
		
	}

	private String usuarios_sistema() {
		return null;
		// TODO Auto-generated method stub
		
	}

	private void guardar_usuario(String usuario) {
		// TODO Auto-generated method stub
		
	}
}
