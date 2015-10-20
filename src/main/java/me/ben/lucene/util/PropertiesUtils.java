package me.ben.lucene.util;

import java.io.*;
import java.util.*;

public class PropertiesUtils {
	private Properties props;  
    private String prefixName;  
	 /**
     * 
     * @param prefixName   e.g. "sys"  -> load "sys.properties"
     */
    public PropertiesUtils(String prefixName){  
    	try {  
    		prefixName = prefixName;
            props = new Properties();  
            //read properties from classpath
            InputStream fis =this.getClass().getClassLoader().getResourceAsStream(prefixName + ".properties"); 
            props.load(fis);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
   
    /** 
     * 获取某个属性 
     */  
    public String getProperty(String key){  
        return props.getProperty(key);  
    }  
    /** 
     * 获取所有属性，返回一个map,不常用 
     * 可以试试props.putAll(t) 
     */  
    public Map getAllProperty(){  
        Map map=new HashMap();  
        Enumeration enu = props.propertyNames();  
        while (enu.hasMoreElements()) {  
            String key = (String) enu.nextElement();  
            String value = props.getProperty(key);  
            map.put(key, value);  
        }  
        return map;  
    }  
    /** 
     * 在控制台上打印出所有属性，调试时用。 
     */  
    public void printProperties(){  
        props.list(System.out);  
    }  
    /** 
     * 写入properties信息 
     * 一般不需要
     */  
    /*
    public void writeProperties(String key, String value) {  
        try {  
            OutputStream fos = new FileOutputStream(prefixName + ".properties");  
            props.setProperty(key, value);  
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流  
            props.store(fos, "『comments』Update key：" + key);  
        } catch (IOException e) {  
        }  
    }  
    */   
    public static void main(String[] args) {  
        PropertiesUtils sysProps=new PropertiesUtils("sys");  
        System.out.println("value=" + sysProps.getProperty("name"));  
       //util.writeProperties("value", "value0");  
        
    } 
	
}
