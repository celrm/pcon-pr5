import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	private static final long serialVersionUID = 1L;

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
