package org.imdea.software;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.IOException;


public class WoCoServer extends Thread {
	long service_time=0;
	//public static final String rute ="C://Users//paula//Documents//WordCounter-19//WordCounter/";
	public static final String rute ="/Users/luisdominguez/Desktop/";
	public static final char SEPARATOR = '$';
	private HashMap<Integer, StringBuilder> buffer;
	private HashMap<Integer, HashMap<String, Integer>> results;
	
	/**
	 * Performs the word count on a document. It first converts the document to 
	 * lower case characters and then extracts words by considering "a-z" english characters
	 * only (e.g., "alpha-beta" become "alphabeta"). The code breaks the text up into
	 * words based on spaces.
	 * @param line The document encoded as a string.
	 * @param wc A HashMap to store the results in.
	 */
public static void doWordCount(String line, HashMap<String, Integer> wc) {
		
		//calculo de hora de inicio y fin 
		long startTime = System.nanoTime();
		
		//Limpieza 
		
		String ucLine = line.toLowerCase();
		StringBuilder asciiLine = new StringBuilder();
		
		//limpieza 
		ucLine = Cleaning(ucLine);
		long endTime_Tags = System.nanoTime();
		
		//conteo
		countword(ucLine ,wc);
		
		
       	//Toma el tiempo final cuando termina de contar todo el archivo
		long endTime = System.nanoTime();
		//calcula la diferencia para el tiempo total del word count
		float duration = (float) ((endTime - startTime)/1000000000.0);
		//calcula la diferencia para el tiempo de limpieza de tags html 
		float duration_cleaning = (float) ((endTime - endTime_Tags)/1000000000.0);
				
		try {
			float service_time_Cleaning=0;
		    float service_time_counting=0;
	        String ruta = rute+"2-3.Cleaning-Counting.txt";
	        String contenido = "Contenido de ejemplo";
	        File file = new File(ruta);
	        // Si el archivo no esta creado aun
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write("FINE-GRAINED STATISTICS GATHERING INSIDE THE SERVER" + "\n");
	        bw.write("Time spent cleaning the document (removing tags): " + duration_cleaning + "\n");  
	        bw.write("Time spent Counting:" + duration+ "\n");    
	        if(duration!=0) {
	        	service_time_Cleaning = 1/duration_cleaning;
	            bw.write("Time to process Cleaning (μ): "  +  service_time_Cleaning  + "seconds");
	            }  
	        if(duration!=0) {
	        	service_time_counting = 1/duration;
	            bw.write("Time to process Counting (μ): "  +  service_time_counting  + "seconds");
	            }  
	        bw.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
		
		
	}
	
	/**
	 * Constructor of the server.
	 */
	public WoCoServer() {
		buffer = new HashMap<Integer, StringBuilder>();	
		results = new HashMap<Integer, HashMap<String, Integer>>();
	}
	
	/**
	 * This function handles data received from a specific client (TCP connection).
	 * Internally it will check if the buffer associated with the client has a full
	 * document in it (based on the SEPARATOR). If yes, it will process the document and
	 * return true, otherwise it will add the data to the buffer and return false
	 * @param clientId
	 * @param dataChunk
	 * @return A document has been processed or not.
	 */
	public boolean receiveData(int clientId, String dataChunk) {
		
		long startTime = System.nanoTime();
		
		StringBuilder sb;
		
		if (!results.containsKey(clientId)) {
			results.put(clientId, new HashMap<String, Integer>());
		}
		
		if (!buffer.containsKey(clientId)) {
			sb = new StringBuilder();
			buffer.put(clientId, sb);
		} else {
			sb = buffer.get(clientId);
		}
		
		sb.append(dataChunk);
				
		if (dataChunk.indexOf(WoCoServer.SEPARATOR)>-1) {
			//we have at least one line
			String bufData = sb.toString();
			int indexNL = bufData.indexOf(WoCoServer.SEPARATOR);
			
			String line = bufData.substring(0, indexNL);
			String rest = (bufData.length()>indexNL+1) ? bufData.substring(indexNL+1) : null;
			
			if (indexNL==0) {
				System.out.println("SEP@"+indexNL+" bufdata:\n"+bufData);
			}
			
			if (rest != null) {
				System.out.println("more than one line: \n"+rest);
				try {
					System.in.read();
				} catch (IOException e) {
					e.printStackTrace();
				}
				buffer.put(clientId, new StringBuilder(rest));
			} else {
				buffer.put(clientId, new StringBuilder());
			}
			
			//word count in line
			HashMap<String, Integer> wc = results.get(clientId);
			doWordCount(line, wc);
			
			saveWC(wc);
			 // CALCULATE SERVICE RATE 
			long endTime = System.nanoTime();
			float duration = (float) ((endTime - startTime)/1000000000.0);
			
			System.out.println("tiempo 1 " + startTime  +   "  tiempo 2 "  + endTime    + "duration " + duration );
			
			try {
				
		        String ruta = rute+"1.Time_receive.txt.txt";
		        String contenido = "Contenido de ejemplo";
		        File file = new File(ruta);
		        // Si el archivo no esta creado aun
		        if (!file.exists()) {
		            file.createNewFile();
		        }
		        FileWriter fw = new FileWriter(file);
		        BufferedWriter bw = new BufferedWriter(fw);
		      //time to process a job 
		        bw.write("TIME SPENT UNTIL THE ENTIRE DOCUMENT HAS BEEN RECEIVED" + "\n");
		        bw.write("******************************************************" + "\n");
		        bw.write("time seconds:" +duration + "\n");

		        if(duration!=0) {
		    		service_time = (long) (1/duration);
		            bw.write("Time to process a job: "  +  service_time  + "seconds");
		            }        
		   
		        bw.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			
			return true;
			
		} else {
			return false;
		}
		
	}
	
	/**
	 * Returns a serialized version of the word count associated with the last
	 * processed document for a given client. If not called before processing a new
	 * document, the result is overwritten by the new one.
	 * @param clientId
	 * @return
	 */
	public String serializeResultForClient(int clientId) {
		
		long startTime = System.nanoTime();
		long endTime =0;
		long duration =0;
		if (results.containsKey(clientId)) {
			StringBuilder sb = new StringBuilder();
			HashMap<String, Integer> hm = results.get(clientId);
			for (String key : hm.keySet()) {
				sb.append(key+",");
				sb.append(hm.get(key)+",");
			}
			results.remove(clientId);
			sb.append("\n");
			
			
			
			endTime = System.nanoTime();
			duration = (long) ((endTime - startTime)/1000000000.0);
			 
			try {
				
		        String ruta = "C://Users//paula//Documents//WordCounter-19//WordCounter/4.Serializing.txt";
		        String contenido = "Contenido de ejemplo";
		        File file = new File(ruta);
		        // Si el archivo no esta creado aun
		        if (!file.exists()) {
		            file.createNewFile();
		        }
		        FileWriter fw = new FileWriter(file);
		        BufferedWriter bw = new BufferedWriter(fw);
		        bw.write("FINE-GRAINED STATISTICS GATHERING INSIDE THE SERVER" + "\n");
		        bw.write("time spent serializing the results :" + duration + "\n");  
		      
		        bw.close();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			
			
			return sb.substring(0);
		} else {
			return "";
		}
	}
	

	public static void main(String[] args) throws IOException {
		
		if (args.length!=4) {
			System.out.println("Usage: <listenaddress> <listenport> <cleaning> <threadcount>");
			System.exit(0);
		}
		
		String lAddr = args[0];
		int lPort = Integer.parseInt(args[1]);
		boolean cMode = Boolean.parseBoolean(args[2]);
		int threadCount = Integer.parseInt(args[3]);
		
		if (cMode==true) {
			//TODO: will have to implement cleaning from HTML tags
		//	String noHTMLString = htmlString.replaceAll("\\<.*?\\>", "");
			System.out.println("FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}
		
		if (threadCount>1) {
			//TODO: will have to implement multithreading
			System.out.println("FEATURE NOT IMPLEMENTED");
			System.exit(0);

		}
		
		
		WoCoServer server = new WoCoServer();
		
		Selector selector = Selector.open(); 
 		
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress myAddr = new InetSocketAddress(lAddr, lPort);
 
		serverSocket.bind(myAddr);
 
		serverSocket.configureBlocking(false);
 
		int ops = serverSocket.validOps();
		SelectionKey selectKey = serverSocket.register(selector, ops, null);
 
		// Infinite loop..
		// Keep server running
		ByteBuffer bb = ByteBuffer.allocate(1024*1024);
		ByteBuffer ba;
		
		while (true) {
 			
			selector.select();
 
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = readyKeys.iterator();
 
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
 
				if (key.isAcceptable()) {
					SocketChannel client = serverSocket.accept();
 
					client.configureBlocking(false);
 
					client.register(selector, SelectionKey.OP_READ);
					System.out.println("Connection Accepted: " + client.getLocalAddress() + "\n");
 
				} else if (key.isReadable()) {										
					SocketChannel client = (SocketChannel) key.channel();
					int clientId = client.hashCode();
					
					bb.rewind();
		            int readCnt = client.read(bb);
		            
		            if (readCnt>0) {
		            	String result = new String(bb.array(),0, readCnt);		            
		         	         	
		            	//Re
		            	new Thread(new Runnable() {
		            	     @Override
		            	     public void run() {
		            	          // code goes here.
		            	    	 boolean hasResult = server.receiveData(clientId, result);
		            	    	 if (hasResult) {
		 							try {
		 								ByteBuffer baa = ByteBuffer.wrap(server.serializeResultForClient(clientId).getBytes());
										client.write(baa);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
		 						}
		            	     }
		            	}).start();
		            	
		            } else {
		            	key.cancel();
		            }
 
					
				}
				iterator.remove();
			}
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
	
    /**
     * Imprime HashMap en un directorio
     * @param wc
     */
    public  static void saveWC(HashMap<String, Integer> wc) {
    	try {
	        String ruta = rute+"resultWC.txt";
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
     * 
     * @param ucLine
     * @return
     */
    public synchronized static void countword(String ucLine ,HashMap<String, Integer> wc) {
    	
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
    }
}


