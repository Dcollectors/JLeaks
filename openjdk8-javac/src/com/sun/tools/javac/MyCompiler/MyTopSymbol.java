/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 20:29:16
 * @LastEditTime: 2023-07-07 22:02:27
 */
package com.sun.tools.javac.MyCompiler;

import java.util.ArrayList;
import java.util.List;


enum MySymbolKind {
    CLASS,
    INTERFACE,
    ANNOTATION,
}

// class, interface, annotation
public class MyTopSymbol {
    public MySymbolKind kind;
    public String packageName;
    public String name;
    public String fullName;

    public List<MyMember> members;
    public List<String> importsList;
    public List<String> templateTypes;

    public MyTopSymbol(MySymbolKind kind, String packageName, String name) {
        this.kind = kind;
        this.packageName = packageName;
        this.name = name;
        this.fullName = packageName + "." + name;
        this.members = new ArrayList<>();
        this.importsList = new ArrayList<>();
        this.templateTypes = new ArrayList<>();
    }

    public String genStr(){
        String str = "";
        str += "package " + packageName + ";" + "\n";
        for(String importFullName : importsList){
            str += "import " + importFullName + ";" + "\n";
        }
        if(kind == MySymbolKind.ANNOTATION){
            str += "public @interface " + name + "{" + "\n";
        }else{
            str += "public " + kind.toString().toLowerCase() + " " + name + " ";
            if(templateTypes.isEmpty() == false){
                str += "<";
                for(Integer i=0; i<templateTypes.size(); i++){
                    str += "T" + i.toString();
                    if(i != templateTypes.size()-1)
                        str += ", ";
                }
                str += "> ";
            }
            str += "{" + "\n";
        }
        for(MyMember member : members){
            str += member.genStr();
        }
        str += "}" + "\n";
        return str;
    }

}
