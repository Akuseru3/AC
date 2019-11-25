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
public class SorteoBDManager {
    Connection cn = SQLConManager.createCon();;
    
    public int addSorteo(String name,String date,String type,String price,String state,String fracCant){
        if(type.equals("Chances"))
                type = "1";
        else
            type = "2";
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createSorteo('"+name+"','"+date+"',"+type+","+price+","+state+","+fracCant+")");
        ){
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public DefaultTableModel getPlanInnSorteo(){
        String[] columnNames = {"Número", "Nombre", "Tipo","Precio","Fecha"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select sorteos.idSorteo, sorteos.nombre,sorteos.tipoSorteo,sorteos.precioSorteo,sorteos.fechaSorteo,planPremios.nombrePlan ,planPremios.totalPremios from sorteos \n" +
                                            "inner join planPremios on sorteos.idSorteo = planPremios.numeroSorteo\n" +
                                            "where sorteos.estadoSorteo = 2 ORDER BY fechaSorteo DESC;");
        ){
            return createPINSTable(rs);
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public DefaultTableModel getPlanInnSorteoType(String type){
        String[] columnNames = {"Número", "Nombre", "Tipo","Precio","Fecha"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select sorteos.idSorteo, sorteos.nombre,sorteos.tipoSorteo,sorteos.precioSorteo,sorteos.fechaSorteo,planPremios.nombrePlan ,planPremios.totalPremios from sorteos \n" +
                                            "inner join planPremios on sorteos.idSorteo = planPremios.numeroSorteo\n" +
                                            "where sorteos.estadoSorteo = 3 and tipoSorteo="+type+" ORDER BY fechaSorteo DESC;");
        ){
            return createPINSTable(rs);
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    private DefaultTableModel createPINSTable(ResultSet rs) throws SQLException{
        String[] columnNames = {"Número", "Nombre", "Tipo","Precio","Fecha","Plan","Premios"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
         while (rs.next()) {
                String num = rs.getString("idSorteo");
                String name = rs.getString("nombre");
                String type = rs.getString("tipoSorteo");
                if(type.equals("2"))
                    type = "Loteria";
                else
                    type = "Chances";
                String price = rs.getString("precioSorteo");
                String date = rs.getString("fechaSorteo");
                String plan = rs.getString("nombrePlan");
                String total = rs.getString("totalPremios");

                String[] data = { num, name, type, price, date,plan,total} ;

                tableModel.addRow(data);
            }
            return tableModel;
    }
    
    public DefaultTableModel getSorteos(String[] filter, String typeFilt){
        String[] columnNames = {"Número", "Nombre", "Tipo","Fracciones","Precio","Fecha"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(generateQuery(filter,typeFilt));
        ){
            return createSorteosTable(rs);
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    private DefaultTableModel createSorteosTable(ResultSet rs) throws SQLException{
        String[] columnNames = {"Número", "Nombre", "Tipo","Fracciones","Precio","Fecha"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        while (rs.next()) {
            String num = rs.getString("idSorteo");
            String name = rs.getString("nombre");
            String type = rs.getString("tipoSorteo");
            if(type.equals("2"))
                type = "Loteria";
            else
                type = "Chances";
            String fracc = rs.getString("cantidadFracciones");
            String price = rs.getString("precioSorteo");
            String date = rs.getString("fechaSorteo");

            String[] data = { num, name, type, fracc, price, date} ;

            tableModel.addRow(data);
        }
        return tableModel;
    }
    
    private String generateQuery(String[] filters,String type){
        String query = "select * from sorteos where (estadoSorteo = "+filters[0];
        for(int i = 1; i<filters.length;i++){
            query+= " or estadoSorteo = "+filters[i];
        }
        query+=" )";
        if(!type.equals(""))
            query += "and tipoSorteo = "+type;
        query+=" ORDER BY fechaSorteo DESC;";
        return query;
    }
    
    public int deleteSorteo(String num){        
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call deleteSorteo('"+num+"')");
        ){
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public int jugarSorteo(String num){        
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call jugarSorteo('"+num+"')");
        ){
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public int modifySorteo(String id,String name,String date,String type,String price,String fracCant){        
        if(type.equals("Chances"))
                type = "1";
        else
            type = "2";
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call updateSorteo('"+id+"','"+name+"','"+date+"',"+type+","+price+","+fracCant+")");
        ){
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public int addGanador(String sorteo, String numWin, String serieWin, String priceWin){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("call createGanador('"+sorteo+"',"+numWin+","+serieWin+","+priceWin+")");
        ){
            return 1;
        }catch(SQLException ex){
            System.out.println(ex);
            return -1;
        }
    }
    
    public DefaultTableModel getWinners(String sorteo){
        String[] columnNames = {"Numero", "Serie", "Premio"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion, serieFraccion, cantidadPremio from ganadores where numeroSorteo = '"+sorteo+"';");
        ){
            while (rs.next()) {
                String name = rs.getString("numeroFraccion");
                String type = rs.getString("serieFraccion");
                String fracc = rs.getString("cantidadPremio");
                double win = Double.parseDouble(fracc);
                String[] data = {name, type, String.format ("%.1f", win)} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
}
