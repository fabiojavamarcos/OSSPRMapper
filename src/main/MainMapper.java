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
	private String separator = ",";
	private String title = null;
	private String body = null;
	private String bin = null;
	private ArrayList<AprioriNew> apns = new ArrayList<AprioriNew>();
	private String classes = null;


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
		separator = args[7];
		bin = args[8];
		classes = args[9];
		if (isOnlyCSV==1) {
			getPrs();
			genBinaryExit();
		}
		else {
			readData();
		}
	}


	private void genBinaryExit() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream os = new FileOutputStream(bin);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
	    	//bw.write("header \n");
			String line = "";
			FileDAO dao = FileDAO.getInstancia(db, user, pswd);
			// write header
			line = line + "pr";
			ArrayList<String> dbGenerals = dao.getDistinctGenerals();
			for (int k=0; k<dbGenerals.size(); k++) {
				line = line + ","+dbGenerals.get(k);
			}
			line = line + ",Title,Body\n";
			bw.write(line);
			
			// end header
			boolean found = false;
			int pr = 0;
			for (int i=0; i<apns.size(); i++) {
				AprioriNew apnAux = apns.get(i);
				ArrayList<String> gs = apnAux.getGenerals();
				pr = apnAux.getPr();
							
				// order line in order of generals generals
				
				ArrayList<String> printLine = new ArrayList();			
				
				for (int t=0; t<dbGenerals.size(); t++) {
					for (int j=0; j<gs.size(); j++) {
						if (gs.get(j).equals(dbGenerals.get(t))){
							found = true;
						}
						
					}
					if (found){
						printLine.add(t,"1" );
						found = false;
					}
					else {
						printLine.add(t, "0");
					}
				}
				
				line = "";
				line = line + pr;
				
				if(apnAux.getPr()==18)
				{
					System.out.println("Debug");
				}
				for (int j=0; j<printLine.size(); j++) {
					line = line + ";"+printLine.get(j);
				}
				ArrayList<String> result = dao.getTitleBody(pr);
				String title = result.get(0);
				String body = result.get(1);
				if(title.equals("nan")) {
					title="";
				}
				if(body.equals("nan")) {
					body="";
				}
				line = line + ";"+title+ ";"+body;// title and body
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
		//System.out.println("Read: "+s);
		ArrayList<String> api = null;
		while (s != null) {
			System.out.println("linha:"+s);
			splitLine(s);
			api = findAPI(pr,java);
			if (api==null)
				System.out.println("not found in jabref: "+pr+"  - "+java);
			else {
				insertApriori(api);
				insertPr();
			}
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
		ArrayList<Apriori> aps = fd.getAprioris();
		
		
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
				
				FileOutputStream osc = new FileOutputStream(classes);
				OutputStreamWriter oswc = new OutputStreamWriter(osc);
				BufferedWriter bwc = new BufferedWriter(oswc);
		    	//bw.write("header \n");
				String line = "";
				String lineClasses = "";
				for (int i=0; i<apns.size(); i++) {
					AprioriNew apnAux = apns.get(i);
					ArrayList<String> gs = apnAux.getGenerals();
					pr = apnAux.getPr();
					FileDAO dao = FileDAO.getInstancia(db, user, pswd);
					ArrayList<String> result = dao.getTitleBody(pr);
					String title = result.get(0);
					String body = result.get(1);
					if (title!=null&&!title.contentEquals("nan")&&!title.equals("NaN")&&!title.isEmpty()){
						if (body!=null&&!body.contentEquals("nan")&&!body.equals("NaN")&&!body.isEmpty()){
							
							line = line + pr;
							lineClasses = lineClasses + pr +";";
							line = line + ","+result.get(0)+ ","+result.get(1);// title and body
							if(apnAux.getPr()==18)
							{
								System.out.println("Debug");
							}
							for (int j=0; j<gs.size(); j++) {
								line = line + ","+gs.get(j);
								if (j==(gs.size()-1))
									
									lineClasses = lineClasses + gs.get(j);
								else
									lineClasses = lineClasses + gs.get(j)+"-";
							}
							line = line + "\n";
							lineClasses = lineClasses + "\n";
							bw.write(line);
							bwc.write(lineClasses);
						}
					}
					line = "";
					lineClasses = "";
		    	}
		    	bw.close();
		    	bwc.close();
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
	
	private void insertPr() {
		// TODO Auto-generated method stub
		FileDAO fd = FileDAO.getInstancia(db,user,pswd);
		
		boolean result = fd.insertPr(pr, title, body);
		if (!result) {
				System.out.println("Insert pr failed: "+ pr + " - "+ title + " - "+ body);
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


	private boolean splitLine(String s) {
		// TODO Auto-generated method stub
		boolean isOk = false;
		int comma = s.indexOf(separator);
		if (comma == -1) {
			System.out.println(" line with problems:  first separator missing...");
			return isOk;
		}
		pr = s.substring(0, comma);
		int comma1 = s.indexOf(separator, comma+1);
		if (comma1 == -1) {
			System.out.println(" line with problems:  second separator missing...");
			return isOk;
		}
		java = s.substring(comma+1, comma1);
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		int slash = java.lastIndexOf("/");
		if (slash == -1) {
			System.out.println(" line with problems:  path slash missing...");
			return isOk;
		}
		java = java.substring(slash+1, java.length());
		int comma2 = s.indexOf(separator, comma1+1);
		if (comma2 == -1) {
			System.out.println(" line with problems:  third separator missing...");
			return isOk;
		}
		title = s.substring(comma1+1, comma2);
		body = s.substring(comma2+1, s.length());
		// get only the file name (because in the OSSParser that is filling the database without the last "/" before file name!!!)
		//int slash = s.lastIndexOf("/");
		//java = s.substring(slash+1, s.length());
		pr = pr.trim();
		java = java.trim();
		title = title.trim();
		body = body.trim();
		System.out.println("pr: "+pr+" , java: "+java + " title "+ title);
		
		isOk = true;
		
		return isOk;
		
	}

}
