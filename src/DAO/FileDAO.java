package DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Apriori;
import util.DBUtil;

public class FileDAO {
	private static FileDAO instancia;
	private String dbcon;
	private String user;
	private String pswd;
	
	private FileDAO(String dbcon, String user, String pswd){
		this.dbcon = dbcon;
		this.user = user;
		this.pswd = pswd;
	}
	
	public static FileDAO getInstancia(String dbcon, String user, String pswd){
		if (instancia == null){
			instancia = new FileDAO(dbcon, user, pswd);
		}
		return instancia;
	}
	
	public ArrayList<String> buscaAPI(String pr,String java){
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<String> gs = new ArrayList<String>();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.full_name = b.file_name and c.api_name_fk = b.api_name and a.file_name like '%"+ java + "%' GROUP BY c.general";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			String general = null;
			
			
			while(rs.next()){
				general = rs.getString("general");
				gs.add(general);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (found)
			return gs;
		else
			return null;
		
	}

	public boolean insertApriori(String pr, String java, String general) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<String> gs = new ArrayList<String>();
		
		try {
			Statement comandoSql = con.createStatement();
			
			String sql = "insert into apriori values ("+pr+",'"+java+"'"+",'"+general+"')";

			System.out.println(sql);
			
			comandoSql.executeUpdate(sql);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}
	
	public boolean insertPr(String pr, String title, String body) {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		
		try {
			Statement comandoSql = con.createStatement();
			
			String sql = "insert into pr values ("+pr+",'"+title+"'"+",'"+body+"')";

			System.out.println(sql);
			
			comandoSql.executeUpdate(sql);
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}

	public ArrayList getAprioris() {
		// TODO Auto-generated method stub
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		ArrayList<Apriori> prs = new ArrayList<Apriori>();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select pr, a.general from apriori a GROUP BY pr, a.general order by pr";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			String general = null;
			int pr = 0;
			
			
			while(rs.next()){
				general = rs.getString("general");
				pr = rs.getInt("pr");
				Apriori ap = new Apriori();
				ap.setGeneral(general);
				ap.setPr(pr);
				prs.add(ap);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (found)
			return prs;
		else
			return null;
	
	}
	
	public ArrayList<String> getTitleBody(int pr){
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String title = null;
		String body = null;
		ArrayList<String> result = new ArrayList();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select title, body from pr where pr ="+ pr ;
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			
			
			
			
			if(rs.next()){
				title = rs.getString("title");
				body = rs.getString("body");
				result.add(title);
				result.add(body);
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (found)
			return result;
		else
			return null;
		
	}
	
	public ArrayList<String> getDistinctGenerals() {
		Connection con = DBUtil.getConnection(dbcon, user, pswd);
		String general = null;
		String col = null;
		ArrayList<String> result = new ArrayList();
		boolean found = false;
		try {
			Statement comandoSql = con.createStatement();
			
			//String sql = "select general from file a, \"file_API\" b, \"API_specific\" c where a.file_name = b.file_name and c.api_name_fk = b.api_name and a.full_name like '%"+ java + "%' GROUP BY c.general"; 
			String sql = "select distinct general from apriori ";
			
			System.out.println(sql);
			
			ResultSet rs = comandoSql.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()){
				col = rsmd.getColumnName(1);
				general = rs.getString(1);
				result.add(general);
				
				found = true;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (found)
			return result;
		else
			return null;
		
	}
	
}

