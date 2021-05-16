package ambos;

public class Msj_Information extends Mensaje {
	private static final long serialVersionUID = 3L;
	private String content1;
	private String content2;
	private int entero1;

	public Msj_Information(Msj t, String o, String d) {
		super(t,o,d);
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public String getContent1() {
		return content1;
	}

	public void setContent1(String content1) {
		this.content1 = content1;
	}

	public int getEntero1() {
		return entero1;
	}

	public void setEntero1(int entero1) {
		this.entero1 = entero1;
	}

}
