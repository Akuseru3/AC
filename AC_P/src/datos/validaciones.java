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
public class validaciones {
    private String result = "";
    
    public String validatePlan(String name,String sorteo,ArrayList<Premio> premios){
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
    
    private boolean checkDubName(String name){
        planesBDCon con = new planesBDCon();
        return con.checkDoubleName(name);
    }
    
    private String validatePrices(ArrayList<Premio> premios,String sorteo){
        String type = sorteo.substring(0, 1);
        if(type.equals("C") && premios.size() != 3)
            return "Los sorteos de chances deben tener una cantidad de 3 premios.";
        if(type.equals("L") && premios.size() < 3)
            return "Los sorteos de loteria deben tener al menos 3 premios.";
        return "";
    }
    
    public String validateSorteo(String name,String type,String day, String month, String year,String price,String fracc){
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
    
    private String validateDateSorteo(String type,String day, String month, String year){
        if(!isDateValid(day,month,year))
            return "La fecha ingresada no es una fecha valida.";
        if(!validateDateLoteria(type,day,month,year))
            return "La fecha de los sorteos de loteria debe ser martes o viernes.";
        if(!validateDateChances(type,day,month,year))
            return "La fecha de los sorteos de chances debe ser domingo.";
        return "";
    }
    
    public boolean checkIfInt(String value){
        try {  
            Double.parseDouble(value);  
            return true;
        } catch(NumberFormatException e){  
            return false;  
        }
    }
    
    public boolean checkIntRange(int value,int start,int end){
        if(value<=end && value>=start){
            return true;
        }
        return false;
    }
    
    public boolean isDateValid(String day, String month, String year){
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
    
    public boolean validateDateLoteria(String type,String day, String month, String year){
        if(type.equals("Loteria")){
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
    
    public boolean validateDateChances(String type,String day, String month, String year){
        if(type.equals("Chances")){
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
}
