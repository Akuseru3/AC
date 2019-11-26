/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.awt.List;
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
public class SorteoBDManager {
    Connection cn = SQLConManager.createCon();
    /**
     * Añade un nuevo sorteo de chances o loteria y su informacion
     * @param name
     * @param date
     * @param type
     * @param price
     * @param state
     * @param fracCant
     * @return 
     */
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
    
    /**
     * Muestra la informacion de un sorteo y la informacion de su plan asociado
     * @return 
     */
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
    
    /**
     * Obtiene la informacion de los sorteos jugados y su plan asociado
     * @param type
     * @return 
     */
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
    
    /**
     * Obtiene la informacion para cargar a la tabla de los sorteos.
     * @param rs
     * @return
     * @throws SQLException 
     */
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
                Double tot = Double.parseDouble(total);
                String[] data = { num, name, type, price, date,plan,BigDecimal.valueOf(tot).toPlainString()} ;
                tableModel.addRow(data);
            }
            return tableModel;
    }
    
    /**
     * Obtiene los sorteos
     * @param filter
     * @param typeFilt
     * @return 
     */
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
    
    /**
     * Crea la informacion de la tabla de sorteos.
     * @param rs
     * @return
     * @throws SQLException 
     */
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
    
    /**
     * Selecciona los sorteos filtrados por un estado especifico
     * @param filters
     * @param type
     * @return 
     */
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
    
    /**
     * Cambia el esatdo de un sorteo a inactivo
     * @param num
     * @return 
     */
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
    
    /**
     * Inicia un sorteo
     * @param num
     * @return 
     */
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
    
    /**
     * Modifica la informacion de un sorteo
     * @param id
     * @param name
     * @param date
     * @param type
     * @param price
     * @param fracCant
     * @return 
     */
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
    
    /**
     * Añade un nuevo numero ganador
     * @param sorteo
     * @param numWin
     * @param serieWin
     * @param priceWin
     * @return 
     */
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
    
    /**
     * Obtiene los ganadores
     * @param sorteo
     * @return 
     */
    public DefaultTableModel getWinners(String sorteo){
        String[] columnNames = {"Numero", "Serie", "Premio"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion, serieFraccion, cantidadPremio from ganadores where numeroSorteo = '"+sorteo+"' order by cantidadPremio DESC;");
        ){
            while (rs.next()) {
                String name = rs.getString("numeroFraccion");
                String type = rs.getString("serieFraccion");
                String fracc = rs.getString("cantidadPremio");
                double win = Double.parseDouble(fracc);
                String[] data = {name, type, BigDecimal.valueOf(win).toPlainString()} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    /**
     * Verifica si un usuario ha sido ganador
     * @param sorteo
     * @param num
     * @param serie
     * @return 
     */
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
    
    /**
     * Obtiene informacion del ultimo sorteo
     * @param type
     * @return 
     */
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
    
    /**
     * Obtiene los mayores premios
     * @param sorteo
     * @return 
     */
    public ArrayList<Premio> topPremios(String sorteo){
        ArrayList<Premio> params = new ArrayList<Premio>();
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("select numeroFraccion, serieFraccion, cantidadPremio from ganadores where numeroSorteo = '"+sorteo+"'  order by cantidadPremio DESC limit 3;");
        ){
            while(rs.next()){
                String num = rs.getString("numeroFraccion");
                String serie = rs.getString("serieFraccion");
                String premio = rs.getString("cantidadPremio");
                Double premioM = Double.parseDouble(premio);
                Premio temp = new Premio(num,serie,BigDecimal.valueOf(premioM).toPlainString());
                params.add(temp);
            }  
            return params;
        }catch(SQLException ex){
            return params;
        }
    }
    
    /**
     * Obtiene las estadisticas de los numeros mas jugados
     * @return 
     */
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
    
    /**
     * Obtiene las estadisticas de los numeros mas premiados
     * @return 
     */
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
                String[] data = {num,BigDecimal.valueOf(val).toPlainString()} ;

                tableModel.addRow(data);
            }
            return tableModel;
        }catch(SQLException ex){
            System.out.println(ex);
            return tableModel;
        }
    }
    
    /**
     * Obtiene la probabilidad del mayor ganador.
     * @return 
     */
    public DefaultTableModel getStatGanadoMayorGen(){
        String[] columnNames = {"Numero", "Veces Ganado"};
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
    
    /**
     * genera la tabla de estadisticas con lso porcentajes de nuemros jugados
     * @return 
     */
    public DefaultTableModel getStatPercentage(){
        String[] columnNames = {"Numero", "Probabilidad"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        try(Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery("SELECT numeroFraccion, COUNT( numeroFraccion ) AS test, concat(round(( COUNT( numeroFraccion )/Tot * 100 ),2),'%') AS percentage\n" +
                                            "FROM ganadores\n" +
                                            "inner join (select count(numeroFraccion) as Tot from ganadores)as nueva  on 1=1\n" +
                                            "GROUP BY numeroFraccion;");
        ){
            while (rs.next()) {
                String num = rs.getString("numeroFraccion");
                String times = rs.getString("percentage");
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
