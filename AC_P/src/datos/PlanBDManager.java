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
    
    /**
     * Añade un nuevo plan a la base de datos
     * @param name nombre del plan
     * @param numSorteo numero del sorteo
     * @param prices lista de premios
     * @param total total de premios
     * @return 
     */
    public int addPlan(String name,String numSorteo,ArrayList<Premio> prices,String total){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createPlan('"+name+"','"+numSorteo+"',"+total+")");
        ){
            for(int i = 0; i<prices.size();i++){
                addPrice(name,prices.get(i).getNombre(),prices.get(i).getCantidad(),prices.get(i).getGanancia());
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    /**
	 * Añade un premio a la base de datos, asociado a un plan.
	 * @param name
	 * @param cant
	 * @param winn
	 * @return
	 * @throws SQLException 
	 */
    private int addPrice(String name,String namePrem,String cant,String winn) throws SQLException{
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createPremio('"+name+"','"+namePrem+"',"+cant+","+winn+")");
        ){
            return 1;
        }
    }
    
    /**
	 * Verifica si el nombre del plan existe o no
	 * @param nombre
	 * @return 
	 */
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
    
    /**
	 * Extrae los planes existentes, y su informacion
	 * @param filter
	 * @return 
	 */
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
                Double tot = Double.parseDouble(total);
                // create a single array of one row's worth of data
                String[] data = { name, num, BigDecimal.valueOf(tot).toPlainString()} ;

                // and add this row of data into the table model
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    /**
	 * Cambia el estado de un plan a inactivo
	 * @param name
	 * @param numSorteo
	 * @return 
	 */
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
    
    /**
	 * Cambia el estado a inactivo de los premios de un plan
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
    private int stateDeletePrices(String name) throws SQLException{
        try(Statement stP = cn.createStatement();
            ResultSet rsP = stP.executeQuery("call deletePremio('"+name+"')");
        ){
            return 1;
        }
    }
    
    /**
     * Obtiene los premios e informacion, obtiene todos los de un mismo plan.
     * @param name
     * @return 
     */
    public DefaultTableModel getPremios(String name){
        String[] columnNames = {"Nombre","Cantidad", "Ganancia"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select * from premios where planAsociado='"+name+"' order by gananciaPremio DESC;");
        ){
            while (rs.next()) {
                String nameP = rs.getString("nombrePremio");
                String cant = rs.getString("cantidadPremios");
                String amount = rs.getString("gananciaPremio");
                double win = Double.parseDouble(amount);

                String[] data = { nameP,cant, BigDecimal.valueOf(win).toPlainString()} ;
                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    /**
     * Actualiza la informacion de los planes
     * @param name
     * @param numSorteo
     * @param oldNum
     * @param prices
     * @return 
     */
    public int updatePlan(String name,String numSorteo,String oldNum,ArrayList<Premio> prices,String total){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call updatePlan('"+name+"','"+numSorteo+"',"+total+",'"+oldNum+"')");
        ){
            trueDeletePrices(name);
            for(int i = 0; i<prices.size();i++){
                addPrice(name,prices.get(i).getNombre(),prices.get(i).getCantidad(),prices.get(i).getGanancia());
            }
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    /**
     * Elimina los premios de un plan asociado
     * @param name
     * @return
     * @throws SQLException 
     */
    private int trueDeletePrices(String name) throws SQLException{
        try(Statement stE = cn.createStatement();){
            int rsE = stE.executeUpdate("delete from premios where planAsociado='"+name+"'");
            return rsE;
        }
    }
    
    
}
