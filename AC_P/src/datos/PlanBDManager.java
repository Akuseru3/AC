/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.math.BigDecimal;
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
public class PlanBDManager {
    Connection cn = SQLConManager.createCon();
    
    public int addPlan(String name,String numSorteo,ArrayList<Premio> prices){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createPlan('"+name+"','"+numSorteo+"',"+0+")");
        ){
            for(int i = 0; i<prices.size();i++){
                addPrice(name,prices.get(i).getCantidad(),prices.get(i).getGanancia());
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    private int addPrice(String name,String cant,String winn) throws SQLException{
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createPremio('"+name+"','"+"Premio"+"',"+cant+","+winn+")");
        ){
            return 1;
        }
    }
    
    
    public boolean checkDoubleName(String nombre){
       try(Statement st = cn.createStatement();
           ResultSet rs = st.executeQuery("select nombrePlan from planPremios where nombrePlan = '"+nombre+"';");
        ){ 
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
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select * from planPremios where estadoPlan = "+filter+";"
        );){
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
    
    public int deletePlan(String name,String numSorteo){        
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call deletePlan('"+name+"','"+numSorteo+"')");
        ){
            stateDeletePrices(name);
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    private int stateDeletePrices(String name) throws SQLException{
        try(Statement stP = cn.createStatement();
            ResultSet rsP = stP.executeQuery("call deletePremio('"+name+"')");
        ){
            return 1;
        }
    }
    
    public DefaultTableModel getPremios(String name){
        String[] columnNames = {"Cantidad", "Ganancia"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select * from premios where planAsociado='"+name+"' order by gananciaPremio DESC;");
        ){
            while (rs.next()) {
                String cant = rs.getString("cantidadPremios");
                String amount = rs.getString("gananciaPremio");
                double win = Double.parseDouble(amount);

                String[] data = { cant, new BigDecimal(win).toPlainString()} ;
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public int updatePlan(String name,String numSorteo,String oldNum,ArrayList<Premio> prices){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call updatePlan('"+name+"','"+numSorteo+"',"+0+",'"+oldNum+"')");
        ){
            trueDeletePrices(name);
            for(int i = 0; i<prices.size();i++){
                addPrice(name,prices.get(i).getCantidad(),prices.get(i).getGanancia());
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    private int trueDeletePrices(String name) throws SQLException{
        try(Statement stE = cn.createStatement();){
            int rsE = stE.executeUpdate("delete from premios where planAsociado='"+name+"'");
            return rsE;
        }
    }
    
    
}
