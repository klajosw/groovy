/*
//Created by klajosw@gmail.com  // https://klajosw.blogspot.com/
HU:   ODI12c project létrehozása ODIstudio-ban groovy script editort elérése a menűből Tools>Groovy>New Script
ENG : Create Project ODI12c Groovy Script in ODIStudio menu Tools>Groovy>New Script
// ----
*/ 

import groovy.sql.Sql
import java.sql.Driver

//---------------------

class kl_db_minta_csere {
   static void main(String[] args) {

//-------------------

def words = []
def readies = []
def di_words =[][]

//-------------------

try {
def driver = Class.forName('oracle.jdbc.OracleDriver').newInstance() as Driver
def props = new Properties()
props.setProperty("user", "user")
props.setProperty("password", "KLjelszo")
def conn = driver.connect("jdbc:oracle:thin:@ora:1152: T10", props)
def sql = new Sql(conn)
def sql_parancs ="""
Select a.object_name,
       a.object_long_name,
       se.startup_variables,
       st.def_txt,
       st.error_message     As task_error
  From ebh_odi_repo.snp_step_log      s,
       ebh_meta.mt_lp_all_executions  a,
       ebh_odi_repo.snp_step_report   sr,
       ebh_odi_repo.snp_scen          sc,
       ebh_odi_repo.snp_session       se,
       ebh_odi_repo.snp_sess_task_log st
Where s.sess_no = a.external_session_id(+)
   And s.sess_no = sr.scen_run_no
   and SR.SCEN_NO = SC.SCEN_NO(+)
   And s.step_beg > Date '2019-03-16'
   And s.sess_no = se.sess_no
   and S.SESS_NO = ST.SESS_NO
   and sc.scen_name like 'MAP_B%'
   and st.col_conn_name ='EDW' and st.def_conn_name ='EDW'
   and DEF_TXT like 'insert %' 
   and OBJECT_TYPE_NAME ='SCENARIO'
   and s.nb_row = 22065
   """

//--------------------------  
def results = sql.firstRow( sql_parancs )  ///sql.rows( sql_parancs )
def parameters = " "
def sql_nyers = " "
sql_nyers = results['def_txt'].asciiStream.text                           /// sql (CLOB tipusu mező miatt konverzió
parameters = results['startup_variables'].asciiStream.text.split(/\n/)    /// parameter tömbösítás
 

//---------------------
   parameters.each { parameter ->  
//      println(parameter)
      di_words.add(parameter.split(/=/))  
   }


//--------------------
      di_words.each { csere ->  
        if ( csere[0].length() > 1 ){
            sql_nyers = sql_nyers.replaceAll(":" + csere[0], csere[1])
//            print( csere[0])
//            print(' --> ')
//            println( csere[1])
        }       

      }

      readies.add(sql_nyers + "\n")

//------------------------

/*

readies.each {
    println it    //// tartalmának kiírása
}

*/

///------------------------
 
def file = new File("out2.txt")
file.write ""
readies.each {
   file.append(it)
}

//---------------------------

println file.text  //// file tartalmának kiírása
//---------------------------
 
conn.close()

} catch(Exception ex) {
         println("HIBA: az adat nincs meg");
conn.close()
}

 

/// Erőforrás felszabadítás

//sql.close()

 

}

}