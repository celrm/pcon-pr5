package ambos;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class Mensaje implements Serializable {

	private Msj tipo;
	private String origen;
	private String destino;
	
	public Mensaje(Msj t, String o, String d) {
		tipo = t;
		origen = o;
		destino = d;
	}
	
	public Msj getTipo() {return tipo;}
	public String getOrigen() {return origen;}
	public String getDestino() {return destino;}
	
}
