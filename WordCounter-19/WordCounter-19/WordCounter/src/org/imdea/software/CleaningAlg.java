package org.imdea.software;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CleaningAlg extends Thread {

	public static void main(String[] args) {
		// Prueba en multiples hilos
        int n = 1; // Number of threads 
        for (int i=0; i<n; i++) 
        { 
        	CleaningAlg object = new CleaningAlg(); 
            object.start(); 
        } 
	}

	/**
	 * Metodo Run de la clase sobre la que se realizan los hilos de ejecucion
	 */
    public void run() 
    { 
        try
        {     
    		System.out.println("Prueba 1: <p>hola<p>");
    		String htmlt1 ="<p>hola hola hola la la a<p>";
    		String ucLine = Cleaning(htmlt1);
    		System.out.println(ucLine);
    		System.out.println("Prueba Conteo de Palabras");
    		HashMap<String, Integer> result = countword(ucLine);
    		System.out.println(result);
    		saveWC(result);
    		System.out.println(" ");
    		
    		System.out.println("Prueba 2: <p><l tttt >hola l p o o <l tttt ><p>");
    		String htmlt2 ="<p><l tttt >hola l p o o <l tttt ><p>";
    		String rest2 = Cleaning(htmlt2);
    		System.out.println(rest2);
    		System.out.println("Prueba Conteo de Palabras");
    		HashMap<String, Integer> result1 = countword(rest2);
    		System.out.println(result1);
    		System.out.println(" ");
    		
    		System.out.println("Prueba 3: <p> 1 < 2 <p>");
    		String htmlt31 ="<p> 1 < 2 <p>";
    		String rest31 = Cleaning(htmlt31);
    		System.out.println(rest31);
    		System.out.println("Prueba Conteo de Palabras");
    		HashMap<String, Integer> result2 = countword(rest31);
    		System.out.println(result2);
    		System.out.println(" ");
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught"); 
        } 
    } 
	 
    /**
     * 
     * @param ucLine
     * @return
     */
    public  static HashMap<String, Integer> countword(String ucLine) {
    	
    	HashMap<String, Integer> wc = new HashMap<String, Integer> () ;
		StringBuilder asciiLine = new StringBuilder();
		char lastAdded = ' ';
		for (int i=0; i<ucLine.length(); i++) {
			
			char cc = ucLine.charAt(i);
			if ((cc>='a' && cc<='z') || (cc==' ' && lastAdded!=' ')) {
				asciiLine.append(cc);
				lastAdded = cc;
			}
		}
		
		String[] words = asciiLine.toString().split(" ");
		for (String s : words) {	
			if (wc.containsKey(s)) {
				wc.put(s, wc.get(s)+1);
			} else {
				wc.put(s, 1);
			}
		}
		return wc;
    }
    
    /**
     * Imprime HashMap en un directorio
     * @param wc
     */
    public  static void saveWC(HashMap<String, Integer> wc) {
    	try {
	        String ruta = "/Users/luisdominguez/Desktop/resultWC.txt";
	        File file = new File(ruta);
	        // Si el archivo no esta creado aun
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	    
	        for (Entry<String, Integer> entry : wc.entrySet()) {
			    System.out.println(entry.getKey() + " = " + entry.getValue());
			    bw.write(entry.getKey() + " = " + entry.getValue());
			    bw.newLine();
			}
	        bw.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
    	
    }
    
    
    /**
     * @param line
     * @return the param line without the tags of html in a String
     */
	public synchronized static String Cleaning(String line) {
		//init varaibles usadas
		char[] cline = line.toCharArray();
		int tamline = line.length();
		char[] tagline = new char[tamline];
		char[] clean = new char[tamline];
		boolean enTag = false;
		int conttag=0;
		int contclean=0;
		// Empezamos a mirar <
		for(int x=0; x<cline.length ;x++) {
			if(cline[x] == '<' || enTag) {
				//tenemos que mirar si es un tag
				if(!enTag) {
					conttag=0;
					enTag=true;
					tagline[conttag]=cline[x];
					conttag++;
				}else {
					if(cline[x]=='<') {
						//añadir a clean
						for(int z=0;z<conttag;z++) {
							clean[contclean]=tagline[z];
							contclean++;
						}
						tagline = new char[tamline];
						conttag=0;
						tagline[conttag]=cline[x];
						conttag++;
						//
					}else if(cline[x]=='>') {
						//cerrar tag
						tagline = new char[tamline];
						conttag=0;
						enTag=false;
					}else {
						//seguir recorrido
						tagline[conttag]=cline[x];
						conttag++;
					}
				}	
			}else {
				//
				clean[contclean]=cline[x];
				contclean++;
			}
		}
		return String.copyValueOf(clean);
		
	}
}
