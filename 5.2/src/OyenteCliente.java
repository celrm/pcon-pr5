import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class OyenteCliente extends Thread {
	private Socket s;
	private ObjectInputStream fin;
	private ObjectOutputStream fout;

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
			Mensaje msj;
			try {
			msj = (Mensaje) fin.readObject();
			System.out.println("Recibido "+msj.getTipo()+ " de "+msj.getOrigen()+" para "+msj.getDestino()); 
			System.out.flush();
			if(!msj.getDestino().equals(Servidor.origen)) continue; // está pocho			
			Msj_Information send;
			switch(msj.getTipo()) {
			case CONEXION:
				guardar_usuario(msj.getOrigen());
				send = new Msj_Information(Msj.CONFIRMACION_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				break;
			case LISTA_USARIOS:
				String lista = usuarios_sistema();
				send = new Msj_Information(Msj.CONFIRMACION_LISTA_USUARIOS,Servidor.origen,msj.getOrigen());
				send.setContent1(lista);
				fout.writeObject(send);
				break;
			case PEDIR_FICHERO:
				String fichero = ((Msj_Information) msj).getContent1();			
				String destino2 = buscar_usuario(fichero);
				ObjectOutputStream fout2 = buscar_output(destino2);
				send = new Msj_Information(Msj.EMITIR_FICHERO,Servidor.origen,destino2);
				send.setContent1(msj.getOrigen());
				send.setContent2(fichero);
				fout2.writeObject(send);
				break;
			case PREPARADO_CLIENTESERVIDOR:
				String receptor = ((Msj_Information) msj).getContent1();
				String ip_emisor = ((Msj_Information) msj).getContent2();
				int puerto_emisor = ((Msj_Information) msj).getEntero1();
				fout2 = buscar_output(receptor);
				send = new Msj_Information(Msj.PREPARADO_SERVIDORCLIENTE,Servidor.origen,receptor);
				send.setContent1(ip_emisor);
				send.setEntero1(puerto_emisor);
				fout2.writeObject(send);
				break;
			case CERRAR_CONEXION:
				eliminar_usuario(msj.getOrigen());
				send = new Msj_Information(Msj.CONFIRMACION_CERRAR_CONEXION,Servidor.origen,msj.getOrigen());
				fout.writeObject(send);
				s.close();
				return;
			default:
				break;
			}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ObjectOutputStream buscar_output(String usuario) {
		Usuario u = Servidor.usuarios.get(usuario);
		return u.getOutput();
	}

	private String buscar_usuario(String fichero) {
		for(Usuario u : Servidor.usuarios.values()) {			
			for(String f : u.getCompartido()) {
				if(f.equals(fichero))
					return u.getId();
			}
		}
		return null;
	}

	private void eliminar_usuario(String usuario) {
		// TODO Auto-generated method stub
	}

	private String usuarios_sistema() {
		String lista = "";
		for(Usuario u : Servidor.usuarios.values()) {
			lista = lista.concat(u.getId()).concat("\n");
		}
		return lista;
	}

	private void guardar_usuario(String usuario) {
		Usuario u = Servidor.usuarios.get(usuario);
		u.setOutput(fout);
	}
}
