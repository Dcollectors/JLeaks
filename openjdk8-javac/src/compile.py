import sys
import os
import io
import re
import openpyxl
from xml.dom import minidom
import atexit

JAVAC_PATH = "/usr/lib/jvm/java-8-openjdk-amd64/bin/javac"
JAVA_PATH = "/usr/lib/jvm/java-8-openjdk-amd64/bin/java"

CLASS_PATH = "/home/autocompile/javac_test/openjdk8-javac/classpath.txt"
MAVENLOG_PATH = "/home/autocompile/javac_test/openjdk8-javac/src/resultmvn.log"
SOURCE_PATH = "/home/autocompile/javac_test/openjdk8-javac/source.txt"
MAVENJAR_PATH = "/home/autocompile/javac_test/openjdk8-javac/mavenJar.txt"
MAVENLOCALREPO = "/home/autocompile/.m2"

#########################################################################################

def del_class_file(filepath):
    files = os.listdir(filepath)
    for file in files:
        if '.' in file:
            suffix = file.split('.')[-1]
            if suffix == 'class':
                os.remove(os.path.join(filepath, file))
                
def del_dir(dirroot):
    if os.path.exists(dirroot):
        for root, dirs, files in os.walk(dirroot, topdown=False):
            for name in files:
                os.remove(os.path.join(root, name))
                
def genSourceTxT(proRoot):
    proSrcRoot = os.path.join(proRoot)
    if not os.path.exists(proSrcRoot):
        print("Error: path not exists : " + proSrcRoot)
        return

    if not os.path.isdir(proSrcRoot):
        print("Error: path is not a directory : " + proSrcRoot)
        return

    with open(SOURCE_PATH, "w") as f:
        for root, dirs, files in os.walk(proSrcRoot):
            for file in files:
              if re.match(r".*\.java$", file):
                f.write(os.path.abspath(os.path.join(root, file)) + "\n")
         
def genMavenJarTxT():
    mavenLocalRepo = MAVENLOCALREPO
    if not os.path.exists(mavenLocalRepo):
        print("Error: path not exists : " + mavenLocalRepo)
        return

    if not os.path.isdir(mavenLocalRepo):
        print("Error: path is not a directory : " + mavenLocalRepo)
        return

    with open(MAVENJAR_PATH, "w") as f:
        for root, dirs, files in os.walk(mavenLocalRepo):
            for file in files:
                if re.match(r".*\.jar$", file):
                    f.write(os.path.abspath(os.path.join(root, file)) + "\n")
                    
def callMaven(proRoot):
    curdir = os.getcwd()
    os.chdir(proRoot)
    # cmd = "mvn -fn -Dmaven.test.skip=true -Dfile.enconding=utf-8 --settings /mnt/disk3/openjdk8-javac/settings.xml -l {} dependency:build-classpath -Dmdep.outputFile={}".format(MAVENLOG_PATH, CLASS_PATH)
    cmd = "mvn install -fn -Dmaven.test.skip=true -Dfile.enconding=utf-8 --settings /mnt/disk3/openjdk8-javac/settings-1.xml -l {} dependency:build-classpath -Dmdep.outputFile={}".format(MAVENLOG_PATH, CLASS_PATH)
    ret = os.system(cmd)
    os.chdir(curdir)
    return ret
    
def getSrcRoot():
    sourcetxt = SOURCE_PATH
    fp = open(sourcetxt, 'r')
    firstFile = fp.readline()
    fp.close()
    if firstFile == None:
        print("source.txt is empty, program exit.")
        exit(0)
    
    firstFile = firstFile[:-1]
    with open(firstFile, 'r') as fp:
        lines = fp.readlines()
        line = None
        for l in lines:
            if "package" in l:
                line = l
                break
        if line == None:
            return None
        else:
            spackage = line.split(" ")[1][:-1]
            spackage = spackage.replace('.', '/')
            spackage = spackage[0:-1]
            idx = firstFile.find(spackage)
            idx = firstFile.rfind('/', 0, idx-1)
            if idx == -1:
                print(spackage)
                print(firstFile)
                return None
            proPath = firstFile[0:idx] + '/'
            print(proPath)
            return proPath
                
def getModulePath(proRoot):
    if os.path.exists(proRoot + "/src/main/java"):
        modules.append(proRoot + "/src/main/java")
    elif os.path.exists(proRoot + "/src"):
        modules.append(proRoot + "/src")
    else:
        doc = minidom.parse(proRoot + "/pom.xml")
        elements = doc.getElementsByTagName("modules")
        for elem in elements:
            for child in elem.childNodes:
                if child.nodeType == child.ELEMENT_NODE and child.tagName == "module":
                    getModulePath(proRoot + "/" + child.firstChild.nodeValue)
            break
        
def pidend(pidfile):
    with open(pidfile, 'a+') as f:
        f.write("pid exit.")
    
def autoCompile(proRoot, javaFilePath, log_path):
    with open(CLASS_PATH, "a") as f:
        f.write(":" + proRoot + "/TempClass")
    # 重写source.txt
    genSourceTxT(proRoot)
    # 编译
    os.system("export LANG=zh_CN.UTF-8 && " + JAVAC_PATH + " ./com/sun/tools/javac/MyCompiler/MyCompiler.java -encoding UTF-8")
    os.system("export LANG=zh_CN.UTF-8 && {} com.sun.tools.javac.MyCompiler.MyCompiler {} {} {} {} 1>{}/projects_log/{}.log 2>{}/temp.log"
        .format(JAVA_PATH, proRoot,  javaFilePath, SOURCE_PATH, CLASS_PATH, log_path, javaFilePath.split("/")[-1], log_path))
    
    # 检查编译结果
    if os.path.exists(classFilePath):
        print("AutoCompile successfully!\n")
    else:
        print("AutoCompile failed!\n")
     
    
if __name__ == '__main__':  
    proRoot = sys.argv[1]
    javaFilePath = sys.argv[2]
    classFilePath = javaFilePath.split(".")[0] + ".class"

    log_path = proRoot + "/Mycompiler_Log"

    if os.path.exists(log_path) == False:
        os.system("mkdir " + log_path)
        os.system("mkdir " + log_path + "/projects_log")
    
    # 清除日志文件
    # del_dir("/mnt/disk3/Log")
    
    # daemonize()

    # 检查项目、文件
    if os.path.exists(proRoot) == False:
        print("Project not exist:", proRoot, "\n")
        exit(1)
    elif os.path.exists(javaFilePath) == False:
        print("File not exists:", javaFilePath, "\n")
        exit(1)
    
    print("Compiling:", javaFilePath)
    
    # 置零 classpath.txt 文件
    os.system("rm {}".format(CLASS_PATH))
    os.system("touch {}".format(CLASS_PATH))
    
    # 判断是否Maven项目
    if os.path.exists(proRoot + "/pom.xml") == False:
        print("Not Maven Project!\n")
        exit(1)
    
    # 调用 maven
    if "/src" in javaFilePath:
        pomPath = javaFilePath.split("/src")[0]
    else:
        pomPath = proRoot
    ret = callMaven(pomPath)
    # callMaven(proRoot)
    if ret == 256:
        print("Maven Compile Error!")
        autoCompile(proRoot, javaFilePath, log_path)
        exit(1)
    
    # 将maven模块路径写入classpath
    # modules = list()
    # getModulePath(proRoot)
    # with open(CLASS_PATH, "a") as f:
        # for module in modules:
        #     f.write(":" + module + "/")
        # f.write(":" + proRoot + "/TempClass")

    
    # autoCompile(proRoot, javaFilePath)
            
    print("AutoCompile Finished!")
