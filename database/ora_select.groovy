/*
//Created by klajosw@gmail.com  // https://klajosw.blogspot.com/
HU:   ODI12c project létrehozása ODIstudio-ban groovy script editort elérése a menűből Tools>Groovy>New Script
ENG : Create Project ODI12c Groovy Script in ODIStudio menu Tools>Groovy>New Script
// ----
*/ 

import groovy.sql.Sql
import groovy.swing.SwingBuilder
import java.sql.Connection
import java.sql.DriverManager
import java.util.HashMap

url  = "jdbc:oracle:thin://@localhost:2126/DBT10"
user = "teszt_user"
pw   = "KLjs_1234_kl"


// --- Van-e oracle JDBC driver???
assert Class.forName("oracle.jdbc.driver.OracleDriver") != null


// kapcsolódás az adatbázishoz
conn = DriverManager.getConnection(url,user, pw)
def  stmt1 = conn.createStatement()
 

// Lekérdezés összeállítása
def sql1 = """
  select yyyymmdd, yyyy, c.* from CALENDAR c where yyyy='2019' and WEEK_OF_YEAR_NUM =1
   """


// Lekérdeés futtatása
def rs1 = stmt1.executeQuery(sql1)


// Eredmény kiolvasása amig van következő sor

while (rs1.next()) {
  print rs1.getString(1)
  print ' | '
  print rs1.getString('HOLIDAY_DESC')
  print ' | '
  println rs1.getString(3)


}

 

// Lezárások

stmt1.close()
conn.close()

///----- VÉGE

 
/*
>>>>>>>> 

20190101 | UJEV | 2019-01-01 00:00:00
20190102 | null | 2019-01-02 00:00:00
20190103 | null | 2019-01-03 00:00:00
20190104 | null | 2019-01-04 00:00:00
20190105 | SZOMBAT | 2019-01-05 00:00:00
20190106 | VASÁRNAP | 2019-01-06 00:00:00
20190107 | null | 2019-01-07 00:00:00
*/
 