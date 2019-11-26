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
    private String nombre;
    private  String cantidad;
    private String ganancia;
    public Premio(String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
    }
    
    public Premio(String nombre,String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
        this.nombre = nombre;
    }
    
    public String getCantidad(){
        return cantidad;
    }
    
    public String getGanancia(){
        return ganancia;
    }
    
    public String getNombre(){
        return nombre;
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
    
    public static ArrayList<Premio> generateWinners(String sorteo,ArrayList<Premio> premios){
        ArrayList<Premio> ganadores = new ArrayList<Premio>();
        String[] columnNames = {"Numero","Serie","Premio"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        SorteoBDManager connector = new SorteoBDManager();
        connector.jugarSorteo(sorteo);
        for(Premio temp:premios){
            int cant = Validate.toInt(temp.cantidad);
            for(int i = 0;i<cant;i++){
                int numero = generateRandom(99);
                int serie = generateRandom(999);
                connector.addGanador(sorteo, Integer.toString(numero), Integer.toString(serie), temp.getGanancia());
                Premio nuevo = new Premio(String.valueOf(numero),String.valueOf(serie),temp.ganancia);
                ganadores.add(nuevo);
                String[] data = { Integer.toString(numero), Integer.toString(serie), temp.ganancia} ;
                tableModel.addRow(data);
            }

        }
        return ganadores;
    }
    
    private static int generateRandom(int end){
        int n = rand.nextInt(end);
        n+=1;
        return n;
    }
}
