/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 20:38:22
 * @LastEditTime: 2023-07-05 20:50:25
 */
package com.sun.tools.javac.MyCompiler;

public abstract class MyMember {

    protected boolean isStatic;
    protected String name;
    protected String returnType;

    public MyMember(boolean isStatic, String name, String returnType) {
        this.isStatic = isStatic;
        this.name = name;
        this.returnType = returnType;
    }

    public String genStr() {
        return null;
    };

}
