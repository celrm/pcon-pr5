import java.io.Serializable;

public class Paquete implements Serializable {
	private static final long serialVersionUID = 5954700252107466565L;
	private String body;

	public Paquete(String dentro) {
		body = dentro;
	}

	public String get() {
		return body;
	}

	public void set(String body) {
		this.body = body;
	}
}
