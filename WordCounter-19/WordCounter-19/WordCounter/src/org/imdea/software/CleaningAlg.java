package org.imdea.software;

public class CleaningAlg extends Thread {

	public static void main(String[] args) {

		// Prueba en multiples hilos
        int n = 8; // Number of threads 
        for (int i=0; i<8; i++) 
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
    		String htmlt1 ="<p>hola<p>";
    		String rest1 = Cleaning(htmlt1);
    		System.out.println(rest1);
    		System.out.println("Prueba 2: <p><l tttt >hola l p o o <l tttt ><p>");
    		String htmlt2 ="<p><l tttt >hola l p o o <l tttt ><p>";
    		String rest2 = Cleaning(htmlt2);
    		System.out.println(rest2);  		
    		System.out.println("Prueba 3: <p> 1 < 2 <p>");
    		String htmlt31 ="<p> 1 < 2 <p>";
    		String rest31 = Cleaning(htmlt31);
    		System.out.println(rest31);
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught"); 
        } 
    } 
	 
    
    /**
     * @param line
     * @return the param line without the tags of html in a String
     */
	public static String Cleaning(String line) {
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
