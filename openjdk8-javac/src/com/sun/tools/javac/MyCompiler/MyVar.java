/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 20:53:15
 * @LastEditTime: 2023-07-05 20:56:45
 */
package com.sun.tools.javac.MyCompiler;

public class MyVar extends MyMember{
    
    public MyVar(boolean isStatic, String name, String returnType) {
        super(isStatic, name, returnType);
    }

    @Override
    public String genStr() {
        if(isStatic == false){
            return "public " + returnType + " " + name + ";" + "\n";
        }
        else{
            return "public static " + returnType + " " + name + ";" + "\n";
        }
    }

}
