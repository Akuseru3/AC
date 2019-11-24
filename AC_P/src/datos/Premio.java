/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Kevin
 */
public class Premio {
    static Random rand = new Random();
    private  String cantidad;
    private String ganancia;
    public Premio(String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
    }
    
    public String getCantidad(){
        return cantidad;
    }
    
    public String getGanancia(){
        return ganancia;
    }
    
    public static ArrayList<Premio> getTablePrices(TableModel table){
        int cantPremios = table.getRowCount();
        ArrayList<Premio> premios = new ArrayList<>();
        for(int i = 0;i<cantPremios;i++){
            String cant = table.getValueAt(i, 0).toString();
            String amount = table.getValueAt(i, 1).toString();
            premios.add(new Premio(cant,amount));
        }
        return premios;
    }
    
    public static DefaultTableModel generateWinners(String sorteo,ArrayList<Premio> premios){
        String[] columnNames = {"Numero","Serie","Premio"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SorteoBDManager connector = new SorteoBDManager();
        for(Premio temp:premios){
            int cant = Validate.toInt(temp.cantidad);
            for(int i = 0;i<cant;i++){
                int numero = generateRandom(99);
                int serie = generateRandom(999);
                connector.addGanador(sorteo, Integer.toString(numero), Integer.toString(serie), temp.getGanancia());
                String[] data = { Integer.toString(numero), Integer.toString(serie), temp.ganancia} ;
                tableModel.addRow(data);
            }

        }
        return tableModel;
    }
    
    private static int generateRandom(int end){
        int n = rand.nextInt(end);
        n+=1;
        return n;
    }
}
