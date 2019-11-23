/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JTable;

/**
 *
 * @author Kevin
 */
public class Premio {
    static Random rand = new Random();
    public String cantidad;
    public String ganancia;
    public Premio(String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
    }
    
    public static ArrayList<Premio> getTablePrices(JTable table){
        int cantPremios = table.getRowCount();
        ArrayList<Premio> premios = new ArrayList<>();
        for(int i = 0;i<cantPremios;i++){
            String cant = table.getValueAt(i, 0).toString();
            String amount = table.getValueAt(i, 1).toString();
            premios.add(new Premio(cant,amount));
        }
        return premios;
    }
    
    public static void generateWinners(ArrayList<Premio> premios){
        for(Premio temp:premios){
            

        }
    }
    
    private static int generateRandom(int end){
        int n = rand.nextInt(end);
        n+=1;
        return n;
    }
}
