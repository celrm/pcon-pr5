package ambos;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Msj_Information extends Mensaje {

	private List<String> content;
	private int entero1;

	public Msj_Information(Msj t, String o, String d) {
		super(t,o,d);
		content = new ArrayList<>();
	}

	public String getContent(int i) {
		return content.get(i);
	}

	public void putContent(String s) {
		content.add(s);
	}

	public int getEntero1() {
		return entero1;
	}

	public void setEntero1(int entero1) {
		this.entero1 = entero1;
	}

}
