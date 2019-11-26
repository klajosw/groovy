#  ODI groovy minták / ODI Groovy examples

### HU : telepítés / ENG: Install
Csomagold ki egy program könyvtárba.
Ennek a könnyvtárnak az elérési utját állítsd be GROOVY_HOME környezeti változónak.
A PATH környezeti változót bővítsd ki a %GROOVY_HOME%/bin bejegyzéssel.

 

### Fordítás
groovyc Hello.groovy

-- vagy tömeges forditás maszk alapján

groovyc *.groovy *.java

 

### Futtatás
java -cp %GROOVY_HOME%/embeddable/groovy-all-2.0.0.jar;. kl_class "Kecskemeti Lajos"

 
---------------------------------------------------------------------------

## Groovy használat előnye
---- egy lépésbe futtaható
groovy kl_minta.groovy



Blog : https://klajosw.blogspot.com/2019/05/groovy-alap-1.html

More info : https://klajosw.github.io/groovy/
