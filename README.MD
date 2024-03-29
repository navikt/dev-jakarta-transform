K9 Jakarta Transformer
========================

This is a tool to perform a one-off transformation of source code for a Javava maven/gradle project from Java EE to Jakarta EE, built on the [Eclipse Transformer](https://github.com/eclipse/transformer)

It will do 80-90% of the grunt work of replacing package namespaces in source and configuration (xml files).
What remains is primarily correcting the dependencies - implementation versions of the [Jakarta EE](https://jakarta.ee/specifications/) - and dependent libraries.

See  [TRANSFORMATIONS.ADOC](TRANSFORMATIONS.ADOC) for details and known limitations.


Usage
=====

1. Clone the repo
   ```sh
   git clone https://github.com/navikt/dev-jakarta-transform.git
   ```
2. Build
   ```sh
   mvn clean package
   export JAR_PATH=$(pwd)/target
   ```
3. Go to project to be converted
   NB! if this project depends on a another project that doesn't support Jakarta EE 9.x - convert that one first.  
   If it's a shared project, consider dual publishing artifacts under a new coordinate (e.g. myproject-jakarta) to allow gradual transition.
   ```sh
   java -jar $JAR_PATH/dev-jakarta-transform-0.6.0-SNAPSHOT.jar <project-folder> <output-folder>
   ```
   This will produce a new folder `output_myproject` with all artifacts either copied or transformed. 
   
   By passing the "--reverse" flag, all transformations will be done in reverse - e.g. "jakarta"->"javax" or similar. Note that there may be ambiguity if there are multiple overlapping keys, or multiple keys in the original transformation that maps to the same value.  So the transformation a->b plus b->a may not give exact same result a.

4. Transformations are mainly based on search/replace of known Strings, and has some known limitations.
   there may be additional tasks to be performed, see [transformations](TRANSFORMATIONS.ADOC).
   
   Mainly to:
   
   * Update Maven POM versions and Maven dependencies (update, remove, add)
   * Remove possible exclusions added for old JavaEE artifacts (these did not have any official release artifacts, so were generally built and deployed by different organizations under different maven coordinates - glassfish, sun, jboss, etc.)
   * Update use of libraries which have incompatible changes (jetty-jaspi, jersey)
   * Update use of plugin configurations for code-generation (e.g. graphql, jaxb)

Acknowledgments
===============
* The driver for handling the namespace replacements is the [Eclipse Transformer](https://github.com/eclipse/transformer)
* Configuration files for rename / string replacements have come from [Open Liberty](https://github.com/OpenLiberty/open-liberty/tree/integration/dev/wlp-jakartaee-transform/rules)

