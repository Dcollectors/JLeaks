/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-08 22:14:58
 * @LastEditTime: 2023-07-08 22:21:08
 */
package com.sun.tools.javac.MyCompiler;

public class MyUtils {
    public static String getMethodBasicRetType(String errLine) {
        String errtrim = errLine.trim();
        String[] errsplits = errtrim.split(" ");
        if(errsplits.length == 0){
            return "Object";
        }

        String checkType = errsplits[0];
        String[] allBasicType = {
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "char",
            "boolean"
        };
        for(String basicType : allBasicType){
            if(checkType.equals(basicType)){
                return basicType;
            }
        }
        return "Object";
    }
}
