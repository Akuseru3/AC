/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
/**
 *
 * @author Akuseru
 */
public class sqlCon {    
    //Nuevo objeto de tipo connection
    static Connection conectar = null;
    public static Connection conexion(){
        //Proceso de conexion de java con la base de datos
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conectar = DriverManager.getConnection("jdbc:mysql://localhost:3306/loteria?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false","root","kedareto");
            System.out.println("Conexion exitosa");
            
        }catch(ClassNotFoundException | SQLException e){
            //System.out.print(e);
            System.out.println(e);
            System.out.println("Fallo al conectar");
        }
        return conectar;
    }

}
