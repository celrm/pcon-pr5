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
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (nw > 0) {
			dr = dr + 1;
			e.release();
			try {
				r.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		nr = nr + 1;
		if (dr > 0) { 
			dr = dr -1;
			r.release();
		}
		else {
			e.release();
		}
		
		//read
		ObjectOutputStream ret = canales.get(usuario);
		//
		
		try {
			e.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nr = nr-1;
		if (nr == 0 && dw > 0) {
			dw = dw -1;
			w.release();
		}
		else {
			e.release();
		}
		return ret;
	}
	// WRITE
	boolean poner_output(String usuario, ObjectOutputStream fout) {
		try {
			e.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (nr > 0 || nw > 0) {
			dw = dw + 1;
			e.release();
			try {
				w.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		nw = nw + 1;
		e.release();
		
		//write
		boolean ret =  canales.put(usuario, fout) == null;
		//
		
		try {
			e.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		nw = nw-1;
		if (dw > 0) {
			dw = dw-1;
			w.release();
		}
		else if (dr > 0) {
			dr = dr -1;
			r.release();
		}
		else {
			e.release();
		}
		return ret;
	}
}
