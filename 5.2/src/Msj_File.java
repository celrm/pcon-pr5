public class Msj_File extends Mensaje {
	private static final long serialVersionUID = 2L;
	private String name;
	private String content;

	public Msj_File(Msj t, String o, String d) {
		super(t,o,d);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
