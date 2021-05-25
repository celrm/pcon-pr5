package servidor;

import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class MonitorSem {
	private HashMap<String,ObjectOutputStream> canales;
	int nr,nw,dr,dw;
	Semaphore e;
	Semaphore r;
	Semaphore w;
	
	// Problema de readers-writers usando las tecnicas de la practica 3: semáforos con paso de testigo
	
	public MonitorSem() {
		canales = new HashMap<String,ObjectOutputStream> ();
		nr = 0;
		nw = 0;
		dr = 0;
		dw = 0;
		e = new Semaphore(1);
		r = new Semaphore(0);
		w = new Semaphore(0);
	}
	// READ
	ObjectOutputStream buscar_output(String usuario) {
		try {
			e.acquire();
			if (nw > 0) {
				dr = dr + 1;
				e.release();
				r.acquire();
			}
			nr = nr + 1;
			if (dr > 0) { 
				dr = dr -1;
				r.release();
			}
			else e.release();
			
			// READ
			ObjectOutputStream ret = canales.get(usuario);
			
			e.acquire();
			nr = nr-1;
			if (nr == 0 && dw > 0) {
				dw = dw -1;
				w.release();
			}
			else e.release();
			return ret;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// WRITE
	boolean poner_output(String usuario, ObjectOutputStream fout) {
		try {
			e.acquire();
		if (nr > 0 || nw > 0) {
			dw = dw + 1;
			e.release();
			w.acquire();
		}
		nw = nw + 1;
		e.release();
		
		// WRITE
		boolean ret =  canales.put(usuario, fout) == null;
		
		e.acquire();
		nw = nw-1;
		if (dw > 0) {
			dw = dw-1;
			w.release();
		}
		else if (dr > 0) {
			dr = dr -1;
			r.release();
		}
		else e.release();
		return ret;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
}
