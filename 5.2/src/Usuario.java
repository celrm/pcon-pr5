import java.util.ArrayList;

public class Usuario {
	
	private String id;
	private String ip;
	private ArrayList<String> compartido;
	
	public Usuario(String id, String ip, ArrayList<String> compartido) {
		this.setId(id);
		this.setIp(ip);
		this.setCompartido(compartido);
	}
	/*
	Guarda informaci ́on para un usuario registrado en el sistema. Tendr ́a
	al menos los siguientes atributos: identificador de usuario, direcci ́on ip y lista de 
	informaci ́on compartida. El servidor almacenar ́a informaci ́on sobre todos los usuarios
	registrados en el sistema (instancias de la clase Usuario).
	
	
	////// no de esta clase:
	Ser ́a necesario implementar adem ́as varias clases adicionales. En particular: 
	clases paracada tipo de mensaje, clases para la interfaz con el usuario, etc. 
	(GUI o de texto).
	*/

	public String getId() {
		return id;
	}

	private void setId(String id) {
		this.id = id;
	}

	public ArrayList<String> getCompartido() {
		return compartido;
	}

	private void setCompartido(ArrayList<String> compartido) {
		this.compartido = compartido;
	}

	public String getIp() {
		return ip;
	}

	private void setIp(String ip) {
		this.ip = ip;
	}
}
