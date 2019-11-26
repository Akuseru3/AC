/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
/**
 *
 * @author Akuseru
 */

public class LoginDatas {
    /**
	 * Variable global que maneja la conexion.
	 */
    Connection cn = SQLConManager.createCon();;
    
    /**
	 * Objetivo: Obtener la contraseña de un usuario.
	 * @param usuario
	 * @param contra
	 * @return 
	 */
    public int checkContraseña(String usuario,String contra){        
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call getContraseña('"+usuario+"')");
        ){
            
            return resultLooper(rs,contra);

        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    /**
	 * Objetivo: Verifica en los usuarios encontrados la contraseña dada
	 * @param rs
	 * @param contra
	 * @return
	 * @throws SQLException 
	 */
    private int resultLooper(ResultSet rs, String contra) throws SQLException{
        if (rs.next() == false) {           
                return -1;
        }else {
            do{
                if(rs.getString(1).equals(contra)){
                    if(rs.getString(2).equals("1")){
                        return 1;
                    }else{
                        return 2;
                    }
                }else{
                    return -1;
                }
            }while (rs.next());
        }
    }
}
