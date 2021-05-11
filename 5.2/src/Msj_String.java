
public class Msj_String extends Mensaje {
	private String content;

	public Msj_String(Msj t, String o, String d, String c) {
		super(t,o,d);
		content = c;
	}

	public String getContent() {
		return content;
	}
}
