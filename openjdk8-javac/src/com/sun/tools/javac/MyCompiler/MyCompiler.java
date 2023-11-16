/*
 * @Description: 
 * @Version: 
 * @Autor: whm
 * @Date: 2023-07-05 20:28:01
 * @LastEditTime: 2023-07-08 21:50:46
 */
package com.sun.tools.javac.MyCompiler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.sun.tools.javac.MyCompiler.MyErrHandler;

public class MyCompiler {
    public static String JARROOT = "./TempClass/";
    public static Integer MAXEPOCH = 5;
    public static String PROJECT_ROOT = "";
    public static String File_PATH = "";
    public static String SOURCE_PATH = "";
    public static String CLASS_PATH = "";
    public static Set<String> srcFiles = new HashSet<>(); 

    public static void main(String[] args) throws IOException, InterruptedException {
        
        if(args.length < 1){
            System.out.println("please input project root");
            System.exit(1);
        }

        PROJECT_ROOT = args[0];
        File_PATH = args[1];
        SOURCE_PATH = args[2];
        CLASS_PATH = args[3];

        JARROOT = PROJECT_ROOT + "/TempClass/";
        System.out.println("JARROOT : " + JARROOT);

        // 读取所有的源文件，之后import的时候删除
        initSrcFiles(SOURCE_PATH);

        // 递归删除TempClass文件夹, 重新创建
        File FJarRoot = new File(JARROOT);
        if (FJarRoot.exists() == true) {
            deleteDir(FJarRoot);
        }
        
        Integer cnt = 0;
        while (true) {
            if (cnt++ >= MAXEPOCH) {
                System.out.println("epoch limit, exit");
                break;
            } else {
                System.out.println(
                        "==============================epoch " + cnt.toString() + "==============================");
            }

            ByteArrayInputStream bytesin = null;
            ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
            ByteArrayOutputStream byteserr = new ByteArrayOutputStream();

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                System.out.println("compiler is null");
                System.exit(1);
            }
            int flag = compiler.run(bytesin, bytesout, byteserr, "-cp", "@"+CLASS_PATH, "-encoding", "UTF-8", 
                File_PATH);    
                    
            if (flag == 0) {
                System.out.println("MYJAVAC COMPILE SUCCESS!");
                break;
            }
            
            // 获取所有错误
            String errstr = byteserr.toString();
            System.out.println(errstr);
            String[] errLines = errstr.split("\n");
            List<List<String>> errList = splitErrMessage(errLines);
            
            for (List<String> errBlock : errList) {
                MyErrHandler.handleErr(errBlock);
            }

            // 生成Jar文件
            for (MyTopSymbol topsym : MyErrHandler.topsymbols) {
                createJarFile(topsym, compiler);
            }

        }
    }

    private static List<List<String>> splitErrMessage(String[] errLines) {
        List<String> tmp = new ArrayList<String>();
        List<List<String>> res = new ArrayList<List<String>>();
        for (String str : errLines) {
            if (str.contains("错误") == true && tmp.isEmpty() == false) {
                res.add(tmp);
                tmp = new ArrayList<String>();
                tmp.add(str);
            } else {
                tmp.add(str);
            }
        }
        return res;
    }

    private static void createJarFile(MyTopSymbol topsym, JavaCompiler compiler) {
        String JavaFileDir = JARROOT + topsym.packageName.replace(".", "/");
        String JavaFileName = topsym.name + ".java";
        String JavaFilePath = JavaFileDir + "/" + JavaFileName;
        File FJavaFileDir = new File(JavaFileDir);
        if (FJavaFileDir.exists() == false) {
            FJavaFileDir.mkdirs();
        }

        try {
            PrintStream stream = new PrintStream(JavaFilePath);
            stream.print(topsym.genStr());
            stream.close();

            compiler.run(null, null, null, "-sourcepath", JARROOT, JavaFilePath, "-cp", "@"+CLASS_PATH);

            String jarPath = JARROOT + topsym.name + ".jar";
            String classPath = topsym.packageName.replace(".", "/") + "/" + topsym.name + ".class";
            Process process = Runtime.getRuntime().exec("jar cvf " + jarPath + " -C " + JARROOT + " " + classPath);
            process.waitFor();
            System.out.println("jarout : " + process.exitValue());
            System.out.println("jar cvf " + jarPath + " -C " + JARROOT + " " + classPath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    // sourceRoot带'/'
    private static void initSrcFiles(String sourcetxt) throws FileNotFoundException {
        Scanner sc = new Scanner( new FileReader(sourcetxt));
        while(sc.hasNextLine()) {
            String file = sc.nextLine();
            Scanner scfile = new Scanner( new FileReader(file));
            String fpackage = "";
            while(scfile.hasNext()){
                String line = scfile.nextLine();
                line = line.trim();
                if(line.startsWith("package")) {
                    fpackage = line;
                }
            }

            // package com.sun.tools.javac.MyCompiler;
            int spaceidx = fpackage.indexOf(' ');
            fpackage = fpackage.substring(spaceidx+1, fpackage.length()-1);
            int slashidx = file.lastIndexOf('/');
            if(slashidx == -1)
                slashidx = file.lastIndexOf('\\');
            int pointidx = file.lastIndexOf('.');
            String fileName = file.substring(slashidx+1, pointidx);
            String fileFullName = fpackage + "." + fileName;
            srcFiles.add(fileFullName);
        }
    }

}
