/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datos;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Kevin
 */
public class validaciones {
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
}
