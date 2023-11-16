/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 21:26:10
 * @LastEditTime: 2023-07-08 22:24:17
 */
package com.sun.tools.javac.MyCompiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

enum ErrType {
    PACKAGE_NOT_FOUND,
    SYMBOL_ANNOTATION_NOT_FOUND,
    SYMBOL_CLASS_NOT_FOUND,
    SYMBOL_INTERFACE_NOT_FOUND,
    WRONGTYPE_WANTANNOTATION,
    SYMBOL_FUNCTION_NOT_FOUND,
    WRONGTYPE_ANNOTATION_ITEM_TYPE,
    SYMBOL_METHOD_NOT_FOUND,
    SYMBOL_STATIC_METHOD_NOT_FOUND,
    WRONGTYPE_METHOD_RETTYPE,
    UNKNOW,
}

public class MyErrHandler {

    public static List<MyTopSymbol> topsymbols = new ArrayList<>();
    public static HashMap<String, List<String>> importMap = new HashMap<>();

    public static void handleErr(List<String> errLines) {
        ErrType errType = getErrType(errLines);
        System.out.println(errType.toString());
        if (errType == ErrType.UNKNOW)
            System.out.println(errLines.toString());
        switch (errType) {
            case PACKAGE_NOT_FOUND:
                handlePNFErr(errLines);
                break;
            case SYMBOL_ANNOTATION_NOT_FOUND:
                handleSANFErr(errLines);
                break;
            case SYMBOL_CLASS_NOT_FOUND:
                handleSCNFErr(errLines);
                break;
            case SYMBOL_INTERFACE_NOT_FOUND:
                handleSINFErr(errLines);
                break;
            case WRONGTYPE_WANTANNOTATION:
                handleWTWAErr(errLines);
                break;
            case SYMBOL_FUNCTION_NOT_FOUND:
                handleSFNFErr(errLines);
                break;
            case WRONGTYPE_ANNOTATION_ITEM_TYPE:
                handleWTAITErr(errLines);
                break;
            case SYMBOL_METHOD_NOT_FOUND:
                handleSMNFErr(errLines);
                break;
            case SYMBOL_STATIC_METHOD_NOT_FOUND:
                handleSSMNFErr(errLines);
                break;
            case WRONGTYPE_METHOD_RETTYPE:
                handleWTMRErr(errLines);
                break;
            default:
                break;
        }
    }

    private static ErrType getErrType(List<String> errLines) {
        String err0 = errLines.get(0);
        if (err0.contains("错误: 程序包")) {
            return ErrType.PACKAGE_NOT_FOUND;
        } else if (err0.contains("错误: 找不到符号")) {
            String err1 = errLines.get(1);
            String err2 = errLines.get(2);
            int ptrIndex = err2.indexOf('^');
            if (err1.charAt(ptrIndex - 1) == '@')
                return ErrType.SYMBOL_ANNOTATION_NOT_FOUND;

            // 判断是不是Annotation方法
            if(errLines.size() >= 5){
                String err3 = errLines.get(3);
                String err4 = errLines.get(4);
                if(err3.contains("方法") && err4.contains("@interface")){
                    return ErrType.SYMBOL_FUNCTION_NOT_FOUND;
                }else if(err3.contains("方法") && err4.contains("位置: 类型为")){
                    return ErrType.SYMBOL_METHOD_NOT_FOUND;
                }else if(err3.contains("方法") && err4.contains("位置: 类 ")){
                    return ErrType.SYMBOL_STATIC_METHOD_NOT_FOUND;
                }else if(err3.contains("方法")) {
                    return ErrType.SYMBOL_FUNCTION_NOT_FOUND;
                }

            }

            // 判断是接口还是类
            String errtemp = err1.substring(0, ptrIndex);
            String[] errtempSplit = errtemp.split(" ");
            for (int i = errtempSplit.length - 1; i >= 0; i--) {
                String temp = errtempSplit[i];
                if (temp.equals("public") || temp.equals("private") || temp.equals("protected")) {
                    return ErrType.SYMBOL_CLASS_NOT_FOUND;
                } else if (temp.contains("implements"))
                    return ErrType.SYMBOL_INTERFACE_NOT_FOUND;
                else if (temp.equals("extends"))
                    return ErrType.SYMBOL_CLASS_NOT_FOUND;
            }
            return ErrType.SYMBOL_CLASS_NOT_FOUND;
        } else if (err0.contains("错误: 不兼容的类型:")) {
            if (err0.contains("无法转换为Annotation"))
                return ErrType.WRONGTYPE_WANTANNOTATION;
            else
                return ErrType.WRONGTYPE_ANNOTATION_ITEM_TYPE;
        }
        return ErrType.UNKNOW;
    }

    // PACKAGE_NOT_FOUND
    private static void handlePNFErr(List<String> errLines) {
        String err1 = errLines.get(1);
        err1 = err1.trim();

        String fullName = "";
        String packageName = "";
        String name = "";
        if(err1.startsWith("import")){
            fullName = err1.substring(err1.indexOf(' ') + 1, err1.indexOf(';'));
            packageName = fullName.substring(0, fullName.lastIndexOf('.'));
            name = fullName.substring(fullName.lastIndexOf('.') + 1);
        // }else{
        //     String err2 = errLines.get(2);
        //     int ptrIndex = err2.indexOf('^');
        //     int ptrTemp = ptrIndex;
        //     while(true){
        //         Character c = err1.charAt(ptrTemp);
        //         if(c == ' ' || c == '(')    break;
        //         fullName = c + fullName;
        //         ptrTemp--;
        //     }
        //     ptrTemp = ptrIndex + 1;
        //     while(true){
        //         Character c = err1.charAt(ptrTemp);
        //         if(c == ' ')    break;
        //         fullName += c;
        //         ptrTemp++;
        //     }

        //     packageName = fullName.substring(0, fullName.lastIndexOf('.'));
        //     name = fullName.substring(fullName.lastIndexOf('.') + 1);
        }
        MyTopSymbol topsymbol = new MyTopSymbol(MySymbolKind.CLASS, packageName, name);

        // 添加报错包中的所有import
        String err0 = errLines.get(0);
        err0 = err0.trim();
      
        int idx = err0.indexOf(".java");
        String errAbsPath = err0.substring(0, idx+5);
        String errTemp = err0.substring(MyCompiler.PROJECT_ROOT.length()+1, idx);
        errTemp = errTemp.replace("\\", ".");
        errTemp = errTemp.replace("/", ".");
        getAllImports(errTemp, errAbsPath);

        // 添加自身包，这辈子不写了
        MyCompiler.srcFiles.add(topsymbol.fullName);
        // 只有不是源代码的包才需要import到第三方包中
        List<String> importlist = importMap.get(errTemp);
        for(String imp : importlist) {
            if(MyCompiler.srcFiles.contains(imp)) continue;
            // 排除变量import的可能
            Boolean isFind = false;
            for(String pack : MyCompiler.srcFiles) {
                if(imp.contains(pack)) {
                    isFind = true;
                    break;
                }
            }
            if(isFind)  continue;

            topsymbol.importsList.add(imp);
        }

        // 添加报错包中的所有类
        Boolean isFind = false;
        for (MyTopSymbol sym : topsymbols) {
            if (sym.name.equals(name) && sym.packageName.equals(packageName)) {
                isFind = true;
                break;
            }
        }
        if (isFind == false)
            topsymbols.add(topsymbol);

    }

    // SYMBOL_ANNOTATION_NOT_FOUND
    private static void handleSANFErr(List<String> errLines) {
        String err1 = errLines.get(1);
        String err2 = errLines.get(2);
        int ptrIndex = err2.indexOf('^');
        String annoName = "";
        while (true) {
            if (ptrIndex >= err1.length())
                break;
            Character c = err1.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            annoName += c;
            ptrIndex++;
        }
        // 在topsymbols中找到对应的topsymbol，将类型修改为annotation即可
        // 没有的话无所谓，第二轮肯定能处理
        for (Integer i = 0; i < topsymbols.size(); i++) {
            if (topsymbols.get(i).name.equals(annoName)) {
                MyTopSymbol topsymbol = topsymbols.get(i);
                topsymbol.kind = MySymbolKind.ANNOTATION;
                topsymbols.set(i, topsymbol);
                return;
            }
        }
    }

    // SYMBOL_CLASS_NOT_FOUND
    private static void handleSCNFErr(List<String> errLines) {
        // pass, PACKGE_NOT_FOUND中已经处理了
    }

    // SYMBOL_INTERFACE_NOT_FOUND
    private static void handleSINFErr(List<String> errLines) {
        String err1 = errLines.get(1);
        String err2 = errLines.get(2);
        int ptrIndex = err2.indexOf('^');
        String interName = "";
        while (true) {
            if (ptrIndex >= err1.length())
                break;
            Character c = err1.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            interName += c;
            ptrIndex++;
        }
        String[] typeStrSplit = {};
        if (ptrIndex < err1.length() && err1.charAt(ptrIndex) == '<') {
            String typeStr = "";
            ptrIndex++;
            while (true) {
                Character c = err1.charAt(ptrIndex);
                if (c == '>')
                    break;
                typeStr += c;
                ptrIndex++;
            }
            typeStrSplit = typeStr.split(", ");
        }

        // 在topsymbols中找到对应的topsymbol，将类型修改为annotation即可
        // 没有的话无所谓，第二轮肯定能处理
        for (Integer i = 0; i < topsymbols.size(); i++) {
            if (topsymbols.get(i).name.equals(interName)) {
                MyTopSymbol topsymbol = topsymbols.get(i);
                topsymbol.kind = MySymbolKind.INTERFACE;
                topsymbol.templateTypes = Arrays.asList(typeStrSplit);
                topsymbols.set(i, topsymbol);
                return;
            }
        }
    }

    // WRONGTYPE_WANTANNOTATION
    private static void handleWTWAErr(List<String> errLines) {
        String err1 = errLines.get(1);
        String err2 = errLines.get(2);
        int ptrIndex = err2.indexOf('^');
        String annoName = "";
        while (true) {
            if (ptrIndex >= err1.length())
                break;
            Character c = err1.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            annoName += c;
            ptrIndex++;
        }
        // 在topsymbols中找到对应的topsymbol，将类型修改为annotation即可
        // 没有的话无所谓，第二轮肯定能处理
        for (Integer i = 0; i < topsymbols.size(); i++) {
            MyTopSymbol topsymbol = topsymbols.get(i);
            if (topsymbol.name.equals(annoName)) {
                topsymbol.kind = MySymbolKind.ANNOTATION;
                topsymbols.set(i, topsymbol);
                return;
            }
        }

    }

    // SYMBOL_FUNCTION_NOT_FOUND
    private static void handleSFNFErr(List<String> errLines) {
        String err3 = errLines.get(3);
        String err4 = errLines.get(4);
        int ptrIndex = err3.indexOf('(') - 1;
        String funcName = "";
        while (true) {
            if(ptrIndex < 0)
                break;
            Character c = err3.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            funcName = c + funcName;
            ptrIndex--;
        }

        String className = "";
        ptrIndex = err4.lastIndexOf(' ');
        ptrIndex++;
        while(true){
            if(ptrIndex >= err4.length())
                break;
            Character c = err4.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            className += c;
            ptrIndex++;
        }

        MyMember funcMem = new MyAnnotationItem(false, funcName, "String", "new Object()");
        for(Integer i = 0; i < topsymbols.size(); i++){
            MyTopSymbol topsymbol = topsymbols.get(i);
            if(topsymbol.name.equals(className)){

                boolean isFind = false;
                for(Integer j = 0; j < topsymbol.members.size(); j++){
                    if(topsymbol.members.get(j).name.equals(funcName)){
                        isFind = true;
                        break;
                    }
                }
                if(isFind == false)
                    topsymbol.members.add(funcMem);
                topsymbols.set(i, topsymbol);
                return;
            }
        }
    
    }

    // WRONGTYPE_ANNOTATION_ITEM_TYPE
    private static void handleWTAITErr(List<String> errLines) {
        String err0 = errLines.get(0);
        int ptrIndex = err0.lastIndexOf(": ");
        String typeName = "";
        ptrIndex += 2;
        while(true){
            if(ptrIndex >= err0.length())
                break;
            Character c = err0.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            typeName += c;
            ptrIndex++;
        }

        String err2 = errLines.get(2);
        ptrIndex = err2.indexOf('^');
        ptrIndex -= 2;
        String funcName = "";
        while(true){
            if(ptrIndex < 0)
                break;
            Character c = err2.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            funcName = c + funcName;
            ptrIndex--;
        }
    }

    // SYMBOL_METHOD_NOT_FOUND
    private static void handleSMNFErr(List<String> errLines) {
        String err3 = errLines.get(3);
        String err4 = errLines.get(4);
        int ptrIndex = err3.indexOf('(') - 1;
        String funcName = "";
        while (true) {
            if(ptrIndex < 0)
                break;
            Character c = err3.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            funcName = c + funcName;
            ptrIndex--;
        }

        String params = "";
        ptrIndex = err3.indexOf('(') + 1;
        while(true){
            if(ptrIndex >= err3.length())
                break;
            Character c = err3.charAt(ptrIndex);
            if(c == ')')
                break;
            params += c;
            ptrIndex++;
        }

        String[] paramTypeList = {};
        if(params.isEmpty() == false){
            paramTypeList = params.split(",");
            for(Integer i=0; i<paramTypeList.length; i++){
                String type = paramTypeList[i];
                int idx = type.indexOf('<');
                if(idx != -1){
                    type = type.substring(0, idx);
                    type += "<?>";
                    paramTypeList[i] = type;
                }
            }
        }

        String className = "";
        ptrIndex = err4.lastIndexOf("类型为");
        ptrIndex += 3;
        while(true){
            if(ptrIndex >= err4.length())
                break;
            Character c = err4.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            className += c;
            ptrIndex++;
        }

        // 尝试发现returnType
        String err1 = errLines.get(1);
        String returnType = MyUtils.getMethodBasicRetType(err1);
        MyMember methodMem = new MyMethod(false, funcName, returnType, paramTypeList);
        
        for(Integer i=0; i<topsymbols.size(); i++){
            MyTopSymbol topsymbol = topsymbols.get(i);
            if(topsymbol.name.equals(className)){
                boolean isFind = false;
                for(Integer j=0; j<topsymbol.members.size(); j++){
                    if(topsymbol.members.get(j).name.equals(funcName)){
                        isFind = true;
                        break;
                    }
                }
                if(isFind == false)
                    topsymbol.members.add(methodMem);
                topsymbols.set(i, topsymbol);
                return;
            }
        }
    }

    // SYMBOL_STATIC_METHOD_NOT_FOUND
    private static void handleSSMNFErr(List<String> errLines) {
        String err3 = errLines.get(3);
        String err4 = errLines.get(4);
        int ptrIndex = err3.indexOf('(') - 1;
        String funcName = "";
        while (true) {
            if(ptrIndex < 0)
                break;
            Character c = err3.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            funcName = c + funcName;
            ptrIndex--;
        }

        String params = "";
        ptrIndex = err3.indexOf('(') + 1;
        while(true){
            if(ptrIndex >= err3.length())
                break;
            Character c = err3.charAt(ptrIndex);
            if(c == ')')
                break;
            params += c;
            ptrIndex++;
        }

        String [] paramTypeList = {};
        if(params.isEmpty() == false){
            paramTypeList = params.split(",");
            for(Integer i=0; i<paramTypeList.length; i++){
                String type = paramTypeList[i];
                int idx = type.indexOf('<');
                if(idx != -1){
                    type = type.substring(0, idx);
                    type += "<?>";
                    paramTypeList[i] = type;
                }
            }
        }

        String className = "";
        ptrIndex = err4.lastIndexOf("位置: 类 ");
        ptrIndex += 6;
        while(true){
            if(ptrIndex >= err4.length())
                break;
            Character c = err4.charAt(ptrIndex);
            if (isEnglish(c) == false && Character.isDigit(c) == false && c != '_')
                break;
            className += c;
            ptrIndex++;
        }

        // 尝试发现returnType
        String err1 = errLines.get(1);
        String returnType = MyUtils.getMethodBasicRetType(err1);
        MyMember methodMem = new MyMethod(true, funcName, returnType, paramTypeList);
        for(Integer i=0; i<topsymbols.size(); i++){
            MyTopSymbol topsymbol = topsymbols.get(i);
            if(topsymbol.name.equals(className)){
                boolean isFind = false;
                for(Integer j=0; j<topsymbol.members.size(); j++){
                    if(topsymbol.members.get(j).name.equals(funcName)){
                        isFind = true;
                        break;
                    }
                }
                if(isFind == false)
                    topsymbol.members.add(methodMem);
                topsymbols.set(i, topsymbol);
                return;
            }
        }  
    }

    // WRONGTYPE_METHOD_RETTYPE
    private static void handleWTMRErr(List<String> errLines) {
        
    }



    private static Boolean isEnglish(Character c){
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'); 
    }

    private static void getAllImports(String classFullName, String classAbsPath) {
        if (importMap.containsKey(classFullName)) {
            return;
        }
        
        // 打开文件，读取里面所有的"import xxx", 并将"xxx"保存起来
        List<String> imports = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(classAbsPath));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("import")) {
                    String importName = line.substring(7, line.length() - 1);
                    imports.add(importName);
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        importMap.put(classFullName, imports);
    }

}
