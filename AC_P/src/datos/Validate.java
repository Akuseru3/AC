/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author Kevin
 */
public class Validate {
    /**
     * Valida la revision de un numero para ver si gano
     * @param code
     * @param num
     * @param serie
     * @param fracc
     * @return 
     */
    public static String validateCheck(String code,String num,String serie,String fracc){
        String res = "";
        if(code.trim().equals(""))
            return "Codigo o numero de sorteo no existente.";
        if(!checkIfInt(fracc))
            return "La cantidad de fracciones debe ser un numero positivo.";
        res = validateCheckNums(num,serie);
        return res;
    }
    
    /**
     * valida los numeros de la revision de ganador
     * @param num
     * @param serie
     * @return 
     */
    private static String validateCheckNums(String num, String serie){
        if(!checkIfInt(num) || !checkIntRange(Integer.parseInt(num),1,99))
            return "El número debe ser un valor numerico entre 1 y 99";
        if(!checkIfInt(serie) || !checkIntRange(Integer.parseInt(serie),1,999))
            return "La serie debe ser un valor numerico entre 1 y 999";
        return "";
    }
    
    /*Valida si es un plan valido al modificar*/
    public static String validatePlanM(String name,String sorteo,ArrayList<Premio> premios){
        String res = "";
        if(name.trim().equals(""))
            return "El campo nombre no puede quedar vacio";
        if(sorteo.equals("--"))
            return "Se debe seleccionar un sorteo al cual se vinculara el plan";
        res = validatePrices(premios,sorteo);
        return res;
    }
    
    /*Valida si es un plan valido*/
    public static String validatePlan(String name,String sorteo,ArrayList<Premio> premios){
        String res = "";
        if(name.trim().equals(""))
            return "El campo nombre no puede quedar vacio";
        if(sorteo.equals("--"))
            return "Se debe seleccionar un sorteo al cual se vinculara el plan";
        if(!checkDubName(name))
            return "Ya existe un plan con el nombre indicado.";
        res = validatePrices(premios,sorteo);
        return res;
    }
    
    /*Valida si existe el nombre del plan*/
    private static boolean checkDubName(String name){
        PlanBDManager con = new PlanBDManager();
        return con.checkDoubleName(name);
    }
    
    /*Valida los premios*/
    private static String validatePrices(ArrayList<Premio> premios,String sorteo){
        String type = sorteo.substring(0, 1);
        if(type.equals("C") && premios.size() != 3)
            return "Los sorteos de chances deben tener una cantidad de 3 premios.";
        if(type.equals("L") && premios.size() < 3)
            return "Los sorteos de loteria deben tener al menos 3 premios.";
        if(!validatePremios(premios))
            return "Los tres premios mayores solo pueden tener una cantidad = 1";
        return "";
    }
    
    /*Valida si es un sorteo valido*/
    public static String validateSorteo(String name,String type,String day, String month, String year,String price,String fracc){
        System.out.println(type);
        String res = "";
        if(name.trim().equals(""))
            return "El campo nombre no puede quedar vacio";
        if(!checkIfInt(price))
            return "El precio debe ser un número entero positivo.";
        if(!checkIfInt(fracc))
            return "Error en alc antidfad de fracciones";
        res = validateDateSorteo(type,day,month,year);
        return res;
    }
    
    /*Valida si es fecha valida de sorteo*/
    private static String validateDateSorteo(String type,String day, String month, String year){
        if(!isDateValid(day,month,year))
            return "La fecha ingresada no es una fecha valida.";
        if(!validateDateLoteria(type,day,month,year))
            return "La fecha de los sorteos de loteria debe ser domingo.";
        if(!validateDateChances(type,day,month,year))
            return "La fecha de los sorteos de chances debe ser martes o viernes.";
        return "";
    }
    
    /*Valida si una entrada es valor numerico*/
    public static boolean checkIfInt(String value){
        try {  
            double val = Double.parseDouble(value); 
            if(val>=0)
                return true;
            return false;
        } catch(NumberFormatException e){  
            return false;  
        }
    }
    
    /*Valida si una entrada esta en un rango numerico*/
    public static boolean checkIntRange(int value,int start,int end){
        if(value<=end && value>=start){
            return true;
        }
        return false;
    }
    
    /*Valida si es fecha valida*/
    public static boolean isDateValid(String day, String month, String year){
        if(checkIfInt(day) && checkIfInt(month) && checkIfInt(year)){
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                df.parse(year+"-"+month+"-"+day);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    /**
     * COnvierte un nuemro a entero
     * @param toInt
     * @return 
     */
    public static int toInt(String toInt){
        try {  
            int transformed = Integer.parseInt(toInt);  
            return transformed;
        } catch(NumberFormatException e){  
            return 0;  
        }
    }
    
    /*Valida la fecha de un chance*/
    public static boolean validateDateChances(String type,String day, String month, String year){
        if(type.equals("Chances")){
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(year),Integer.parseInt(month)-1, Integer.parseInt(day));
            if(date.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY || date.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
                return true;
            else
                return false;
        }
        else
            return true;
    }
    
    /*Valida la fecha de una loteria*/
    public static boolean validateDateLoteria(String type,String day, String month, String year){
        if(type.equals("Loteria")){
            Calendar date = Calendar.getInstance();
            date.set(Integer.parseInt(year),Integer.parseInt(month)-1, Integer.parseInt(day));
            if(date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                return true;
            else
                return false;
        }
        else
            return true;
    }
    
    /**
     * valida los premios
     * @param premios
     * @return 
     */
    public static boolean validatePremios(ArrayList<Premio> premios){
        ArrayList<Premio> mayores = getMaxThree(premios);
        for(int i = 0;i<mayores.size();i++){
            premios.add(mayores.get(i));
            if(!mayores.get(i).getCantidad().equals("1"))
                return false;
        }
        return true;
    }
    
    /**
     * retorna los tres premios mayores
     * @param premios
     * @return 
     */
    public static ArrayList<Premio> getMaxThree(ArrayList<Premio> premios){
        ArrayList<Premio> mayores = new ArrayList<>();
        for(int i=0;i<3;i++){
            double max = 0;
            int index = 0;
            for(int j = 0;j<premios.size();j++){
                if(Double.parseDouble(premios.get(j).getGanancia()) > max){
                    max = Double.parseDouble(premios.get(j).getGanancia());
                    index = j;
                }
            }
            mayores.add(premios.get(index));
            premios.remove(index);
        }
        return mayores;
    }
}
