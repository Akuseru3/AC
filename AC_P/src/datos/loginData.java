/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;
import datos.sqlCon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
/**
 *
 * @author Akuseru
 */

public class loginData {
    sqlCon cc = new sqlCon();
    Connection cn = cc.conexion();
    
    public int checkContraseña(String usuario,String contra){        
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call getContraseña('"+usuario+"')");
            if (rs.next() == false) {           
                return -1;
            }else {
                return resultLooper(rs,contra);
            }
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    private int resultLooper(ResultSet rs, String contra) throws SQLException{
        do{
            if(rs.getString(1).equals(contra)){
                if(rs.getString(2).equals('1')){
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
