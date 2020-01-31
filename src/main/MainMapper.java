package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import DAO.FileDAO;
import model.Apriori;
import model.AprioriNew;

public class MainMapper {
	private String user = "postgres";
	private String pswd = "admin";
	private String project = "jabref";
	private String db = "dev";
	private String file = "dat.txt";
	private String pr = null;
	private String java = null;
	private String csv = null;
	private int isOnlyCSV = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MainMapper mp = new MainMapper();
		mp.execute(args);
		
		
	}


	private void execute(String[] args) {
		// TODO Auto-generated method stub
		user = args[0];
		pswd = args[1];
		project = args[2];
		db = args[3];
		file = args[4];
		csv = args[5];
		isOnlyCSV = Integer.parseInt(args[6]);
		if (isOnlyCSV==1) {
			getPrs();
		}
		readData();
	}


	private void readData() {
		// TODO Auto-generated method stub
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    String s = null;;
		try {
			s = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// primeira linha do arquivo
		//String source = s;
		System.out.println("Read: "+s);
		ArrayList<String> api = null;
		while (s != null) {
			splitLine(s);
			api = findAPI(pr,java);
			if (api==null)
				System.out.println("not found in jabref: "+pr+"  - "+java);
			else
				insertApriori(api);
			try {
				s = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		generateFile();
		try {
			br.close();
			isr.close();
			is.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void generateFile() {
		// TODO Auto-generated method stub
		getPrs();
	}


	private void getPrs() {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		ArrayList<Apriori> aps = fd.getPrs();
		
		ArrayList<AprioriNew> apns = new ArrayList<AprioriNew>();
		
		if (aps==null)
			System.out.println("No apriori found!!!");
		else {
			int prAux = 0;
			int pr = 0;
			AprioriNew apn = new AprioriNew();
			for (int i=0; i<aps.size(); i++) {
				
				Apriori ap = aps.get(i);
				pr = ap.getPr();
				
				if (i==0) { // first case treatment
					apn.setPr(pr);
					apn.insertGeneral(ap.getGeneral());
					prAux = pr;
				}
				else {
					
					if (pr==prAux) {
						apn.insertGeneral(ap.getGeneral());
						if (i+1==aps.size()) { // last case treatment
							apns.add(apn);
						}
					} else {
						apns.add(apn);
						
						prAux = pr;
						
						apn = new AprioriNew();
						apn.setPr(pr);
						apn.insertGeneral(ap.getGeneral());
					}
				}
				
			}
			try {
				FileOutputStream os = new FileOutputStream(csv);
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
		    	//bw.write("header \n");
				String line = "";
				for (int i=0; i<apns.size(); i++) {
					AprioriNew apnAux = apns.get(i);
					ArrayList<String> gs = apnAux.getGenerals();
					line = line + apnAux.getPr();
					for (int j=0; j<gs.size(); j++) {
						line = line + ","+gs.get(j);
					}
					line = line + "\n";
					bw.write(line);
					line = "";
		    	}
		    	bw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
			
	}


	private void insertApriori(ArrayList<String> api) {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		for(int i = 0; i<api.size(); i++) {
			boolean result = fd.insertApriori(pr, java, api.get(i));
			if (!result) {
				System.out.println("Insert apriori failed: "+ pr + " - "+ java + " - "+ api.get(i));
			}
		}
		
	}


	private ArrayList<String> findAPI(String pr2, String java2) {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		ArrayList<String> gs = fd.buscaAPI(pr2, java2);
		if (gs==null) {
			System.out.println("pr: "+pr+" - "+java+"not found in database!!!");
		}
		return gs;
	}


	private void splitLine(String s) {
		// TODO Auto-generated method stub
		int comma = s.indexOf(",");
		pr = s.substring(0, comma);
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		int slash = s.lastIndexOf("/");
		java = s.substring(slash+1, s.length());
		pr.trim();
		java.trim();
		System.out.println("pr: "+pr+" , java: "+java);
		
	}

}
