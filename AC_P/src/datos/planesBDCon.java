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
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Kevin
 */
public class planesBDCon {
    sqlCon cc = new sqlCon();
    Connection cn = cc.conexion();
    
    public int addPlan(String name,String numSorteo,ArrayList<Premio> prices){
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createPlan('"+name+"','"+numSorteo+"',"+0+")");
            System.out.println(rs);
            for(int i = 0; i<prices.size();i++){
                Statement stP = cn.createStatement();
                ResultSet rsP = stP.executeQuery("call createPremio('"+name+"','"+"Premio"+"',"+prices.get(i).cantidad+","+prices.get(i).ganancia+")");
                System.out.println(rsP);
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public boolean checkDoubleName(String nombre){
       try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select nombrePlan from planPremios where nombrePlan = '"+nombre+"';");
            if(!rs.next())
                return true;
            return false;
       }catch(SQLException ex){
            System.out.println(ex);
            return false;
       }
       
    }
    
    public DefaultTableModel getPlanes(String filter){
        String[] columnNames = {"Nombre", "Numero de Sorteo", "Total en Premios"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try{
            Statement st = cn.createStatement();
            ResultSet rs;
            if(filter == ""){
                rs = st.executeQuery("select * from planPremios;");
            }
            else{
                rs = st.executeQuery("select * from planPremios where estadoPlan = "+filter+";");
            }
            while (rs.next()) {
                String name = rs.getString("nombrePlan");
                String num = rs.getString("numeroSorteo");
                String total = rs.getString("totalPremios");

                // create a single array of one row's worth of data
                String[] data = { name, num, total} ;

                // and add this row of data into the table model
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public int deletePlan(String name){        
        try{
            Statement stP = cn.createStatement();
            ResultSet rsP = stP.executeQuery("call deletePremio('"+name+"')");
            System.out.println(rsP);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call deletePlan('"+name+"')");
            System.out.println(rs);
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public DefaultTableModel getPremios(String name){
        String[] columnNames = {"Cantidad", "Ganancia"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try{
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select * from premios where planAsociado='"+name+"';");
            while (rs.next()) {
                String cant = rs.getString("cantidadPremios");
                String amount = rs.getString("gananciaPremio");

                // create a single array of one row's worth of data
                String[] data = { cant, amount} ;

                // and add this row of data into the table model
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public int updatePlan(String name,String numSorteo,ArrayList<Premio> prices){
        try{
            Statement stE = cn.createStatement();
            int rsE = stE.executeUpdate("delete from premios where planAsociado='"+name+"'");
            System.out.println(rsE);
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call updatePlan('"+name+"','"+numSorteo+"',"+0+")");
            System.out.println(rs);
            for(int i = 0; i<prices.size();i++){
                Statement stP = cn.createStatement();
                ResultSet rsP = stP.executeQuery("call createPremio('"+name+"','"+"Premio"+"',"+prices.get(i).cantidad+","+prices.get(i).ganancia+")");
                System.out.println(rsP);
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
}
