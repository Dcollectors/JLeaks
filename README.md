# JLeaks: A Featured Resource Leak Repository Collected From Hundreds of Open-Source Java Projects
JLeaks is a resource leaks repository collected from real-world projects which facilitates in-depth researches and evaluation of defect-related algorithms. Each defect in Leaks includes four aspects key information: project information, defect information, code characteristics, and file information. You can also access detailed information for each defect on our website at  *[**`https://presentation-of-j-leaks.vercel.app/`**](https://presentation-of-j-leaks.vercel.app/)*. For the best experience, we recommend using the Chrome browser.

- [Repository Structure](#repository-structure)
- [Contents of JLeaks](#contents-of-jleaks)
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

## References
[1] Roland Croft, Muhammad Ali Babar, and M. Mehdi Kholoosi. 2023. Data Quality for Software Vulnerability Datasets. In 45th IEEE/ACM International Conference on Software Engineering, ICSE 2023, Melbourne, Australia, May 14-20, 2023. IEEE, 21–133. https://doi.org/10.1109/ICSE48619.2023.00022

[2] 2008. ISO/IEC 25012:2008 - Systems and software engineering – Software product Quality Requirements and Evaluation (SQuaRE) – Data quality model. International Organization for Standardization. https://www.iso.org/standard/35736.html

[3] Miltiadis Allamanis. 2019. The adverse effects of code duplication in machine learning models of code. In Proceedings of the 2019 ACM SIGPLAN International Symposium on New Ideas, New Paradigms, and Reflections on Programming and Software, Onward! 2019, Athens, Greece, October 23-24, 2019, Hidehiko Masuhara and Tomas Petricek (Eds.). ACM, 143–153. https://doi.org/10.1145/3359591.3359735

[4] Yepang Liu, Jue Wang, Lili Wei, Chang Xu, Shing-Chi Cheung, Tianyong Wu, Jun Yan, and Jian Zhang. 2019. DroidLeaks: a comprehensive database of resource leaks in Android apps. Empir. Softw. Eng. 24, 6 (2019), 3435–3483. https://doi.org/10.1007/s10664-019-09715-8

[5] pmd. 2023. GitHub - pmd/pmd: An extensible multilanguage static code analyzer.https://github.com/pmd/pmd. (Accessed on 03/30/2023).

[6] FaceBook. 2023. GitHub - facebook/infer: A static analyzer for Java, C, C++, and Objective-C. https://github.com/facebook/infer. (Accessed on 03/30/2023).

[7] SpotBugs. 2023. SpotBugs. https://spotbugs.github.io/. (Accessed on 03/30/2023).

[8] Jie Wang, Wensheng Dou, Yu Gao, Chushu Gao, Feng Qin, Kang Yin, Jun Wei: A comprehensive study on real world concurrency bugs in Node.js. ASE 2017: 520-531
