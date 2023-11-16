/*
 * @Description:
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 20:34:25
 * @LastEditTime: 2023-07-08 22:27:40
 */
package com.sun.tools.javac.MyCompiler;

public class MyMethod extends MyMember{
    private String[] paramTypes;

    public MyMethod(boolean isStatic, String name, String returnType, String[] paramTypes) {
        super(isStatic, name, returnType);
        this.paramTypes = paramTypes;
    }

    @Override
    public String genStr() {
        String paramStr = "";
        int paramCount = 0;
        for(String paramType : paramTypes){
            paramStr += "Object" + " " + "param" + paramCount + ",";
            paramCount++;
        }
        if(!paramStr.isEmpty()){
            paramStr = paramStr.substring(0, paramStr.length() - 1);
        }

        String returnBodyStr = "";
        if(returnType.equals("void")){
            returnBodyStr = "";
        }
        else if(returnType.equals("boolean")){
            returnBodyStr = "return true;";
        }
        else if(returnType.equals("Object")){
            returnBodyStr = "return null;";
        }
        else{
            returnBodyStr = "return 0;";
        }

        String returnStr = "";
        if(isStatic == false){
            returnStr += "public " + returnType + " " + name + "(" + paramStr + ")" + 
                "{" + returnBodyStr + "}" + "\n";
        }
        else{
            returnStr += "public static " + returnType + " " + name + "(" + paramStr + ")" + 
                "{" + returnBodyStr + "}" + "\n";
        }
        System.out.println(returnStr);
        return returnStr;
    }
}
