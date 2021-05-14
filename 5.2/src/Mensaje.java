import java.io.Serializable;

public abstract class Mensaje implements Serializable {
	/*
	Sirve como ra ́ız de la jerarqu ́ıa de mensajes que debere-mos dise ̃nar. 
	Tiene como atributos al tipo, origen y destino del mensaje en cuesti ́on;
	y declara al menos los siguientes m ́etodos:
	*/
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
