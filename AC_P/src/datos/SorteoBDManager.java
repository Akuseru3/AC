/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.awt.List;
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
    
    public double checkWin(String sorteo,String num,String serie){
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select cantidadPremio from ganadores where numeroSorteo='"+sorteo+"' and numeroFraccion="+num+" and serieFraccion="+serie+";");
        ){
            if(rs.next()){
                String winnin = rs.getString("cantidadPremio");
                double amount = Double.valueOf(winnin);
                return amount;
            }  
            return 0;
        }catch(SQLException ex){
            return 0;
        }
    }
    
    public ArrayList<String> lastSorteo(String type){
        ArrayList<String> params = new ArrayList<String>();
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select idSorteo, fechaSorteo from sorteos where estadoSorteo = 3 and tipoSorteo="+type+" order by fechaSorteo DESC limit 1;");
        ){
            if(rs.next()){
                String code = rs.getString("idSorteo");
                String date = rs.getString("fechaSorteo");
                params.add(code);
                params.add(date);
            }  
            return params;
        }catch(SQLException ex){
            return params;
        }
    }
    
    public ArrayList<Premio> topPremios(String sorteo){
        ArrayList<Premio> params = new ArrayList<Premio>();
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion, serieFraccion, cantidadPremio from ganadores where numeroSorteo = '"+sorteo+"'  order by cantidadPremio DESC limit 3;");
        ){
            while(rs.next()){
                String num = rs.getString("numeroFraccion");
                String serie = rs.getString("serieFraccion");
                String premio = rs.getString("cantidadPremio");
                Premio temp = new Premio(num,serie,premio);
                params.add(temp);
            }  
            return params;
        }catch(SQLException ex){
            return params;
        }
    }
    
    public DefaultTableModel getStatMasJugados(){
        String[] columnNames = {"Numero", "Veces Jugado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion,count(numeroFraccion) as VecesWin from ganadores \n" +
                                            "group by numeroFraccion \n" +
                                            "order by VecesWin Desc limit 10;");
        ){
            while (rs.next()) {
                String num = rs.getString("numeroFraccion");
                String times = rs.getString("VecesWin");
                String[] data = {num,times} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public DefaultTableModel getStatMasPremiadoGen(){
        String[] columnNames = {"Numero", "Total ganado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion,sum(cantidadPremio) as totalPremios from ganadores \n" +
                                            "group by numeroFraccion \n" +
                                            "order by totalPremios Desc limit 5;");
        ){
            while (rs.next()) {
                String num = rs.getString("numeroFraccion");
                String times = rs.getString("totalPremios");
                Double val = Double.parseDouble(times);
                String[] data = {num,String.format ("%.1f", val)} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    public DefaultTableModel getStatGanadoMayorGen(){
        String[] columnNames = {"Numero", "Total ganado"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion, count(numeroFraccion) as vecesGanado from PrimerPremioXsorteo as PPS\n" +
                                            "inner join ganadores as G on PPS.numeroSorteo = G.numeroSorteo and PrimerPremio = cantidadPremio\n" +
                                            "order by numeroFraccion limit 5;");
        ){
            while (rs.next()) {
                String num = rs.getString("numeroFraccion");
                String times = rs.getString("vecesGanado");
                String[] data = {num,times} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
}
