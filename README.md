# JLeaks: A Featured Resource Leak Repository Collected From Hundreds of Open-Source Java Projects
JLeaks is a resource leaks repository collected from real-world projects which facilitates in-depth researches and evaluation of defect-related algorithms. Each defect in Leaks includes four aspects key information: project information, defect information, code characteristics, and file information. You can also access detailed information for each defect on our website at  *[**`https://presentation-of-j-leaks.vercel.app/`**](https://presentation-of-j-leaks.vercel.app/)*. For the best experience, we recommend using the Chrome browser.

![image](./presentation.gif#pic_center)


- [Repository Structure](#repository-structure)
- [Contents of JLeaks](#contents-of-jleaks)
- [How to use](#how-to-use)
  - [Quality of JLeaks](#quality-of-jleaks)
    - [Requirements](#requirements)
    - [Uniqueness](#uniqueness)
    - [Consistency](#consistency)
    - [Currentness](#currentness)
  - [Evaluation of defect detection tools using JLeaks](#evaluation-of-defect-detection-tools-using-jleaks)
    - [Requirements](#requirements-1)
    - [Run Tools](#run-tools)
    - [Analysis tool detection results](#analysis-tool-detection-results)
  - [Evaluation of GPT-3.5](#evaluation-of-gpt-35)
    - [Prompt for commits classification](#prompt-for-commits-classification)
    - [Prompt for defect detection](#prompt-for-defect-detection)
- [How to compile projects](#how-to-compile-projects)
- [References](#references)



## Repository Structure
```
├─ JLeaksDataset                   // full data
│  ├─ JLeaks.xlsx                  // detail information for each defect
│  ├─ all_bug_method.zip           // faulty methods
│  └─ all_fix_method.zip           // fixed methods
│  └─ all_bug_files.zip            // faulty files
│  └─ all_fix_files.zip            // fixed files
│  └─ bug_bytecode_files.zip       // faulty bytecode files
└─ quality                         // code used to compute uniqueness, consistency and currentness for JLeaks and DroidLeaks
│   ├─ analysis
│   │  ├─ DuplicateCodeDetector    // clone detection tool
│   │  ├─ analysis.py         
│   │  ├─ currency.py         
│   │  ├─ data.py
│   │  ├─ prepare.py
│   │  ├─ toJsonl.py
│   └─ dataset                     // input files used in computing quality
│       ├─ DroidLeaks
│       │  ├─ DroidLeaks_bug_method.zip
│       │  ├─ DroidLeaks_fix_method.zip
│       │  ├─ DroidLeaks.csv
│       └─ JLeaks
│           ├─ JLeaks_bug_method.zip
│           ├─ JLeaks_fix_method.zip
│           ├─ JLeaks.csv
└─ evaluationTools                // code used to evaluate PMD, Infer, and SpotBugs based on JLeaks
│    ├─ file
│    │  ├─ data-tool.xlsx          // save tool result for each defect
│    │  ├─ pmd_resource_leak.xml   // the custom PMD rule file        
│    │  ├─ spotbugs_filterFile.xml // the custom SpotBugs filter file      
│    ├─ toolAnalysis.py            // analysis the results of defect detection tools
│    ├─ toolResult.zip             // the results of defect detection tools
└─ openjdk8-javac                  // code used to compile defect files
│    ├─ src
│    │  ├─ com                     // modified javac code 
│    │  ├─ compile.py                  
```

## Contents of JLeaks
So far, JLeaks contains **`1,094`** real-world resource leaks from 321 open-source Java projects. Detailed information about these defects can be found in the **`JLeaks.xlsx`**.

Item  |  Description
----------------------- | -----------------------
ID                      | defect ID
projects                | Github project name in the format owner/repository (e.g., "aaberg/sql2o")
\# of commits           | the number of version control commits for the project
UTC of create           | UTC of the project creation
UTC of last modify      | UTC of last modification of the project
\# of stars             | the number of stars
\# of issues            | the number of issues
\# of forks             | the number of forks
\# of releases          | the number of releases
\# of contributors      | the number of contributes
\# of requests          | the number of requests
about                   | project description
commit url              | the URL including the commit details, defect code, and patch code
UTC of buggy commit     | UTC of defect code submission
UTC of fix commit       | UTC of fixed code submission
start line              | the start line of defect method
end line                | the end line of defect method
defect method           | the location and name of defect method (e.g., "src/main/java/org/sql2o/Query.java:executeAndFetchFirst")
change lines            | the change line between defect code and fixed code (e.g., "src/main/java/org/sql2o/Query.java:@@ -271,151 +271,180 @@")
resource types          | the type of system resource (options: **`file`**, **`socket`**, and **`thread`**)
root causes             | root causes of defect.
fix approaches          | approaches used to fixed the defect
patch correction        | indication of whether the patch is correct or not
standard libraries      | standard libraries related to defects
third-party libraries   | third-party libraries related to defects
is inter-procedural     | whether the resource leak is inter-procedural
key variable name       | the name of the key variable holding the system resource
key variable location   | the location of key variable (e.g., "src/main/java/org/sql2o/Query.java:413")
key variable attribute  | the attribute of key variable (options: **`anonymous variable`**, **`local variable`**, **`parameter`**, **`class variable`**, and **`instance variable`**) 
defect file hash        | hash value of the defect file
fix file hash           | hash value of the fixed file
defect file url         | url to download the defect file
fix file url            | url to download the fixed file

The root causes are displayed in the table below.
Causes  |  Description
------------- | -------------
noCloseEPath  | No close on exception paths
noCloseRPath  | No close on regular paths
notProClose   | Not provided close()
noCloseCPath  | No close for all branches paths

The fixed approaches are shown in the table below.
Fixed Approaches  |  Description
--------------- | ---------------
try-with        | Use try-with-resources
CloseInFinally  | Close in finally
CloseOnEPath    | Close on exception paths
CloseOnRPath    | Close on regular paths
AoRClose        | Add or rewrite close


## How to use
### Quality of JLeaks

Based on [1] and the standardized data quality framework ISO/IEC 25012 [2], we compare JLeaks and DroidLeaks [4] using three data quality attributes: uniqueness, consistency, and currentness. Allamanis [3], a code cloning detection tool, has benn utilized.

#### Requirements
- Python==3.7.0
- jsonlines==3.1.0
- matplotlib==3.5.3
- openpyxl==3.1.2
- tokenizers==0.9.4
- pandas==1.1.5
- scikit-learn==0.24.2
- scipy==1.6.3
- pygments == 2.14.0
- sctokenizer==0.0.8
- seaborn==0.9.0
- near-duplicate-code-detector (see https://github.com/microsoft/near-duplicate-code-detector/tree/main)
- .NET Core 2.1 or higher

**`NOTICE: Please avoid running *DuplicateCodeDetector.csproj* in parallel, as indicated in https://github.com/microsoft/near-duplicate-code-detector/issues/5 . Additionally, please use the same Python versions to ensure that the jsonl files are consistent.`**

#### Uniqueness
Before starting, please make sure **`./quality/dataset/DroidLeaks/DroidLeaks_bug_method.zip`** and **`./quality/dataset/JLeaks/JLeaks_bug_method.zip`** exist.

##### 1. Prepare data:
```
cd ./quality

unzip -n -d ./dataset/JLeaks ./dataset/JLeaks/JLeaks_bug_method.zip
unzip -n -d ./dataset/DroidLeaks ./dataset/DroidLeaks/DroidLeaks_bug_method.zip

cd ./analysis
python toJsonl.py -d JLeaks -f bug
python toJsonl.py -d DroidLeaks -f bug
```

##### 2. Analyse data:
```
cd DuplicateCodeDetector

nohup dotnet run DuplicateCodeDetector.csproj --key-jaccard-threshold=0.7 --jaccard-threshold=0.7 --dir=../../dataset/JLeaks/bug_method_gz > ../../dataset/JLeaks/uniqueness_method.log 2>&1 &

nohup dotnet run DuplicateCodeDetector.csproj --key-jaccard-threshold=0.7 --jaccard-threshold=0.7 --dir=../../dataset/DroidLeaks/bug_method_gz > ../../dataset/DroidLeaks/uniqueness_method.log 2>&1 &
```

**`The above process may take a few minutes to complete. If "Finished looking for duplicates " appears at the end of both two files "./quality/dataset/JLeaks/uniqueness_method.log" and "./quality/dataset/DroidLeaks/uniqueness_method.log", it means the duplication detection is finished. Then run:`**

```
cd ..
python analysis.py -d JLeaks -f uniqueness
python analysis.py -d DroidLeaks -f uniqueness
```
And all duplicate methods can be found in:
- ./quality/dataset/DroidLeaks/uniqueness_bug_method_clean.log
- ./quality/dataset/JLeaks/uniqueness_bug_method_clean.log


#### Consistency
Before starting, please make sure **`./quality/dataset/DroidLeaks/DroidLeaks_bug_method.zip`**, **`./quality/dataset/DroidLeaks/DroidLeaks_fix_method.zip`**, **`./quality/dataset/JLeaks/JLeaks_bug_method.zip`** and **`./quality/dataset/JLeaks/JLeaks_fix_method.zip`** exist.

##### 1. Prepare data:

```
cd ./quality

unzip -n -d ./dataset/JLeaks ./dataset/JLeaks/JLeaks_bug_method.zip
unzip -n -d ./dataset/DroidLeaks ./dataset/DroidLeaks/DroidLeaks_bug_method.zip

unzip -n -d ./dataset/JLeaks ./dataset/JLeaks/JLeaks_fix_method.zip
unzip -n -d ./dataset/DroidLeaks ./dataset/DroidLeaks/DroidLeaks_fix_method.zip

mkdir ./dataset/JLeaks/all_method
mkdir ./dataset/DroidLeaks/all_method

cp -r ./dataset/JLeaks/bug_method/* ./dataset/JLeaks/all_method
cp -r ./dataset/JLeaks/fix_method/* ./dataset/JLeaks/all_method

cp -r ./dataset/DroidLeaks/bug_method/* ./dataset/DroidLeaks/all_method
cp -r ./dataset/DroidLeaks/fix_method/* ./dataset/DroidLeaks/all_method

cd ./analysis
python toJsonl.py -d JLeaks -f all
python toJsonl.py -d DroidLeaks -f all

```

##### 2. Analyse data:
```
cd ./DuplicateCodeDetector

nohup dotnet run DuplicateCodeDetector.csproj --key-jaccard-threshold=0.95 --jaccard-threshold=0.95 --dir=../../dataset/JLeaks/all_method_gz > ../../dataset/JLeaks/all_method.log 2>&1 &

nohup dotnet run DuplicateCodeDetector.csproj --key-jaccard-threshold=0.95 --jaccard-threshold=0.95 --dir=../../dataset/DroidLeaks/all_method_gz > ../../dataset/DroidLeaks/all_method.log 2>&1 &
```
**`The above process may take a few minutes to complete. If "Finished looking for duplicates " appears at the end of both two files "./quality/dataset/JLeaks/all_method.log" and "./quality/dataset/DroidLeaks/all_method.log", it means the duplication detection is finished. Then run:`**

```
cd ..
python analysis.py -d JLeaks -f consistency
python analysis.py -d DroidLeaks -f consistency
```

#### Currentness
Before starting, please make sure **`./quality/dataset/JLeaks/JLeaks_bug_method.zip`**, **`./quality/dataset/DroidLeaks/DroidLeaks_bug_method.zip`**, **`./quality/dataset/JLeaks/JLeaks.csv`** and **`./quality/dataset/DroidLeaks/DroidLeaks.csv`** exist.

##### 1. Prepare data:
```
cd ./quality

unzip -n -d ./dataset/JLeaks ./dataset/JLeaks/JLeaks_bug_method.zip
unzip -n -d ./dataset/DroidLeaks ./dataset/DroidLeaks/DroidLeaks_bug_method.zip

cd ./analysis
python prepare.py JLeaks
python prepare.py DroidLeaks
```

##### 2. Analyse data:
```
python currency.py prepare JLeaks
python currency.py measure JLeaks

python currency.py prepare DroidLeaks
python currency.py measure DroidLeaks
```


### Evaluation of defect detection tools using JLeaks
We evaluated three open-source tools (PMD[5], Infer[6], SpotBugs[7]) of defect detection using JLeaks.

#### Requirements
  - PMD 7.0.0-rc4
  - Infer v1.0.0
  - SpotBugs 4.7.3

#### Run Tools
##### PMD
```
pmd check -f xml -R {resource_leak.xml} -d {javafile_path} -r {pmd_output_dir}/{file_name}.xml 2> {pmd_log_dir}/{file_name}.log
```
- {resource_leak.xml} is the custom PMD rule file.
- {javafile_path} is the path to the Java file.
- {pmd_output_dir} is the directory where PMD detection results will be stored.
- {file_name} is the name of the result file.
- {pmd_log_dir} is the directory for PMD run logs.

##### SpotBugs
```
spotbugs -textui -low -jvmArgs "-Xmx2048m" -progress -longBugCodes -include {filterFile_path} -xml={spotbugs_output_dir}/{file_name}.xml {classfile_path}
```
- {filterFile_path} is the path to the filter file.
- {spotbugs_output_dir} is the directory for storing SpotBugs detection results.
- {file_name} is the name of the result file.
- {classfile_path} is the path to the class file to be analyzed.

##### Infer
Infer with Maven:
```
infer run -o {infer_output_dir}/{project_name} -- mvn compile -Dmaven.test.skip=true -Dfile.enconding=utf-8 -l {infer_maven_log_dir}/{project_name}.log
```
Infer with Gradle:
```
infer run -o {infer_output_dir}/{project_name} -- gradle build -w -x test --build-file build.gradle 2> {infer_gradle_log_dir}/{project_name}.log
```
- {infer_output_dir} is the directory for storing Infer detection results.
- {project_name} is the name of the project to be analyzed.
- {infer_maven_log_dir} is the log file generated when using Maven.
- {infer_gradle_log_dir} is the log file generated during the Gradle build process.

#### Analyse tool detection results

After running the tool to obtain the detection results, it is necessary to analyze the result file and compare it with the information we annotated, to check whether the resource leak defect is correctly detected in the method.

Before starting, please make sure **`./evaluationTools/toolResult.zip`** exists.
```
cd ./evaluationTools
unzip -n toolResult.zip
```
SpotBugs output directory is './evaluationTools/toolResult/output-spotbugs'. Infer output directory is './evaluationTools/toolResult/output-infer'. PMD output directory is './evaluationTools/toolResult/output-pmd'

```
python toolAnalysis.py
```
The results of each tool is in the "Result" workbook in the "./evaluationTools/file/data-tool.xlsx" file. In addition, the accuracy of each tool will be shown on the console.


### Evaluation of ChatGPT

#### Prompt for commits classification ###
We randomly select 100 commits and relabel them to determine their relevance to resources. According to the Prompt Engineering Guide [8], the prompt contains a task description, resource explanation, typical scenario illustration, sample code fragments, and the expected output. The prompt is as follows::
```
You are an experienced Java developer. I will show you a code fragment, and you need to check whether the code is related to file, socket, database or thread operations. Your output should be a json which contains 1 field: result. The value of result can be 1 or 0. 1 means you are very certain that the code is related to to file, socket or thread operations, 0 means you are very certain that the code is NOT related to file, socket or thread operations. I will give 10 examples related to resource as follows:

Code1:	Cursor cur = getDB().getDatabase().rawQuery("SELECT value FROM deckVars WHERE key = '" + key + "'", null);if (cur.moveToFirst()) {return cur.getInt(0);} else {throw new SQLException("DeckVars.getInt: could not retrieve value for " + key);
Code2:while (!stack.isEmpty()) {File file = stack.pop();if (file.isFile()) {InputStream inputStream = new FileInputStream(file);try {ClassDescriptor classDescriptor = createClassDescriptor(inputStream);if (classDescriptor != null && classDescriptor.isMainMethodFound()) {String className = convertToClassName(file.getAbsolutePath(),
Code3:Transport transport = Transport.getInstance(K9.app, account);transport.close();transport.open();transport.close();
Code4:try {SQLiteDatabase mDb = openDB();cursor = mDb.rawQuery("SELECT primkey, value FROM preferences_storage", null);while (cursor.moveToNext()) {
Code5:stream = new Base64InputStream(stream);final int len = (int)stream.skip(stream.available());return new SliceInputStream(new Base64InputStream(baseInputStream()), 0, len);
Code6:public void onDestroy() {if (myLibrary != null) {yLibrary.deactivate();myLibrary = null;}super.onDestroy();}
Code7:while (true) {int count = streamReader.read(buffer);if (count <= 0) {return;}if (count < buffer.length) {
Code8:try {BufferedReader styleReader =new BufferedReader(new InputStreamReader(new FileInputStream(styleFile)));while (true) {String line = styleReader.readLine();if (line == null) {break;}
Code9:public boolean hasKey(String key) {return getDB().getDatabase().rawQuery("SELECT 1 FROM deckVars WHERE key = '" + key + "'", null).moveToNext();
Code10:try {final InputStream ins = res.openRawResource(resourceId);scanner = new Scanner(ins, CharEncoding.UTF_8);result = scanner.useDelimiter("\\A").next();IOUtils.closeQuietly(ins);} finally {if (scanner != null) {scanner.close

(ps: If the code contains calls to the close() method on objects or similar operations, or contains try statement, or contains calls to the stop() method on thread related objects, it indicates that the code may related to file, socket, database or thread operations.You can make a judgment by the following commit message:
```
In the prompt, 10 examples are randomly selected from JLeaks.

#### Prompt for defect detection
We used 299 defect cases and 299 non-defect cases randomly selected from JLeaks to evaluate the ability of ChatGPT. The prompt is as follows:
```
You are an experienced Java developer. I will show you a code fragment, and you need to check whether the code has resource leak defect related to file, socket, database or thread. Your output should be a json which contains 1 field: result. The value of result can be 1 or 0. 1 means you are very certain that the code has resource leak defects about file, socket, database connection or thread, 0 means you are very certain that the code do not have resource leak defects related to file, socket, database connection or thread.
```

## How to compile projects
Before running, it is necessary to modify the path parameters in **`openjdk8-javac/src/compile.py`**, which includes:

- `JAVAC_PATH`: The path to Javac.
- `JAVA_PATH`: The path to Java.
- `CLASS_PATH`: The path where successfully compiled class files are recorded.
- `MAVENLOG_PATH`: The log record for compiling programs using Maven.
- `SOURCE_PATH`: The path that records all the Java files in the project.
- `MAVENJAR_PATH`: The path where downloaded jar files are recorded.
- `MAVENLOCALREPO`: The path used by Maven to download jar packages.

After modifying, execute the program using a command such as: 
```
python compile.py /home/project /home/project/file.java
```

## References
[1] Roland Croft, Muhammad Ali Babar, and M. Mehdi Kholoosi. 2023. Data Quality for Software Vulnerability Datasets. In 45th IEEE/ACM International Conference on Software Engineering, ICSE 2023, Melbourne, Australia, May 14-20, 2023. IEEE, 21–133. https://doi.org/10.1109/ICSE48619.2023.00022

[2] 2008. ISO/IEC 25012:2008 - Systems and software engineering – Software product Quality Requirements and Evaluation (SQuaRE) – Data quality model. International Organization for Standardization. https://www.iso.org/standard/35736.html

[3] Miltiadis Allamanis. 2019. The adverse effects of code duplication in machine learning models of code. In Proceedings of the 2019 ACM SIGPLAN International Symposium on New Ideas, New Paradigms, and Reflections on Programming and Software, Onward! 2019, Athens, Greece, October 23-24, 2019, Hidehiko Masuhara and Tomas Petricek (Eds.). ACM, 143–153. https://doi.org/10.1145/3359591.3359735

[4] Yepang Liu, Jue Wang, Lili Wei, Chang Xu, Shing-Chi Cheung, Tianyong Wu, Jun Yan, and Jian Zhang. 2019. DroidLeaks: a comprehensive database of resource leaks in Android apps. Empir. Softw. Eng. 24, 6 (2019), 3435–3483. https://doi.org/10.1007/s10664-019-09715-8

[5] pmd. 2023. GitHub - pmd/pmd: An extensible multilanguage static code analyzer.https://github.com/pmd/pmd. (Accessed on 03/30/2023).

[6] FaceBook. 2023. GitHub - facebook/infer: A static analyzer for Java, C, C++, and Objective-C. https://github.com/facebook/infer. (Accessed on 03/30/2023).

[7] SpotBugs. 2023. SpotBugs. https://spotbugs.github.io/. (Accessed on 03/30/2023).

[8] Jie Wang, Wensheng Dou, Yu Gao, Chushu Gao, Feng Qin, Kang Yin, Jun Wei: A comprehensive study on real world concurrency bugs in Node.js. ASE 2017: 520-531
