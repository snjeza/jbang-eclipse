JBang / Eclipse integration POC
===============================

Import JBang scripts in Eclipse: Import Project... > JBang > JBang script


Currently expects to use JBang from `~/.sdkman/candidates/jbang/current/` and if it can’t find JAVA_HOME, for some reason, it'll use `~/.sdkman/candidates/java/current`. Yes it sucks, I know.

Cuurently supports 
- DEPS
- JAVA
- SOURCES

Build
-----

Open a terminal and execute:

    ./mvnw clean package
    
You can then install the generated update site from `jbang.eclipse.site/target/jbang.eclipse.site-<VERSION>-SNAPSHOT.zip`

License
-------
EPL 2.0, See [LICENSE](LICENSE) file.

