package com.sun.tools.javac.MyCompiler;

public class MyAnnotationItem extends MyMember{
    
    private String defaultValue;

    public MyAnnotationItem(boolean isStatic, String name, String type, String value) {
        super(isStatic, name, type);
        this.defaultValue = value;
    }

    @Override
    public String genStr() {
        String str = "";
        if (isStatic) {
            str += "static ";
        }
        str += returnType + " " + name + "()";
        // str += " default " + defaultValue + ";\n";
        str += " ;\n";
        return str;
    }
    
}
