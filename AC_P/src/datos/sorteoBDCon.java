/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Kevin
 */
public class sorteoBDCon {
    sqlCon cc = new sqlCon();
    Connection cn = cc.conexion();
    
    public int addSorteo(String name,String date,String type,String price,String state,String fracCant){        
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createSorteo('"+name+"','"+date+"',"+type+","+price+","+state+","+fracCant+")");
            System.out.println(rs);
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public DefaultTableModel getSorteos(String filter){
        String[] columnNames = {"Número", "Nombre", "Tipo","Fracciones","Precio","Fecha"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try{
            Statement st = cn.createStatement();
            ResultSet rs;
            if(filter == ""){
                rs = st.executeQuery("select * from sorteos;");
            }
            else{
                rs = st.executeQuery("select * from sorteos where estadoSorteo = "+filter+";");
            }
            while (rs.next()) {
                String num = rs.getString("idSorteo");
                String name = rs.getString("nombre");
                String type = rs.getString("tipoSorteo");
                String fracc = rs.getString("cantidadFracciones");
                String price = rs.getString("precioSorteo");
                String date = rs.getString("fechaSorteo");

                // create a single array of one row's worth of data
                String[] data = { num, name, type, fracc, price, date} ;

                // and add this row of data into the table model
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public int deleteSorteo(String num){        
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call deleteSorteo("+num+")");
            System.out.println(rs);
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public int modifySorteo(String id,String name,String date,String type,String price,String fracCant){        
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call updateSorteo("+id+",'"+name+"','"+date+"',"+type+","+price+","+fracCant+")");
            System.out.println(rs);
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
}
