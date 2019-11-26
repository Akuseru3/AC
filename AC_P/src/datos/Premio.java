/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.math.BigDecimal;
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
    /**
     * Constructor de premio
     * @param cantidad
     * @param ganancia 
     */
    public Premio(String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
    }
    
    /**
     * Constructor de premio
     * @param nombre
     * @param cantidad
     * @param ganancia 
     */
    public Premio(String nombre,String cantidad,String ganancia){
        this.cantidad = cantidad;
        this.ganancia = ganancia;
        this.nombre = nombre;
    }
    
    /*Las siguientes 3 funciones obtienen informacion de la clase.*/
    public String getCantidad(){
        return cantidad;
    }
    
    public String getGanancia(){
        return ganancia;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    /*Obtiene los precios de los premios*/
    public static ArrayList<Premio> getTablePrices(TableModel table){
        int cantPremios = table.getRowCount();
        ArrayList<Premio> premios = new ArrayList<>();
        for(int i = 0;i<cantPremios;i++){
            String name = table.getValueAt(i, 0).toString();
            String cant = table.getValueAt(i, 1).toString();
            String amount = table.getValueAt(i, 2).toString();
            premios.add(new Premio(name,cant,amount));
        }
        return premios;
    }
    
    /**
     * Genera los ganadores un juego
     * @param sorteo
     * @param premios
     * @return 
     */
    public static ArrayList<Premio> generateWinners(String sorteo,ArrayList<Premio> premios){
        ArrayList<Premio> ganadores = new ArrayList<Premio>();
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
            }

        }
        return ganadores;
    }
    
    /**
     * Suma todos los premios y retorna el total
     * @param premios
     * @return 
     */
    public static Double sumTotalPremios(ArrayList<Premio> premios){
        Double total = 0.0;
        System.out.println(premios.size());
        for(Premio temp : premios){
            Double val = Double.parseDouble(temp.getGanancia());
            total += val;
        }
        System.out.println(total);
        return total;
    }
    
    
    /**
     * Genera el siguiente random
     * @param end
     * @return 
     */
    private static int generateRandom(int end){
        int n = rand.nextInt(end);
        n+=1;
        return n;
    }
}
