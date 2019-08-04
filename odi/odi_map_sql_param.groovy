/*
//Created by klajosw@gmail.com  // https://klajosw.blogspot.com/
HU:   ODI12c project létrehozása ODIstudio-ban groovy script editort elérése a menűből Tools>Groovy>New Script
ENG : Create Project ODI12c Groovy Script in ODIStudio menu Tools>Groovy>New Script
// ----
*/ 

//-- ODI osztályok importja
import groovy.sql.Sql
import oracle.odi.domain.project.finder.IOdiVariableFinder
import oracle.odi.domain.project.finder.IOdiSequenceFinder
import groovy.swing.SwingBuilder
 

tme = odiInstance.getTransactionalEntityManager()
varList = ((IOdiVariableFinder) tme.getFinder(OdiVariable.class)).findAll()
seqList = ((IOdiSequenceFinder) tme.getFinder(OdiSequence.class)).findAll()
actVarMap = new HashMap<String,String>()
 

// SQL
url = "jdbc:oracle:thin://@Aszerver:1152/D10"
user = "REPO"
pw = "Ejelszo"
sql = Sql.newInstance(url, user, pw, "oracle.jdbc.driver.OracleDriver")

 

// ehhez a sessionhoz tartozó logot dolgozza fel (régi működés)
// *** ha -1, akkor az sqltxt változóban megadott stringet dolgozza fel ***
tmp_sess_id = 11611 //-1
actual_vars_fl = true // ha igen, akkor az aktuális paraméterekkel helyettesít, egyébként az alapértelmezettekkel
if ( tmp_sess_id < 0 ) {
  sqltxt = """
  
  """
// tobsoros szöveges változo definiállás ÜRES ÉRTÉK !!

} else {
  stmt = """
         select def_txt as sqltxt
         from SNP_sess_task_log
         where sess_no = :p_sess_id
         and def_txt is not null
         order by length(def_txt) desc
         """;

  stmt2 = """
        select startup_variables as act_vars
        from EBH_ODI_REPO.SNP_SESSION
        where sess_no = :p_sess_id
        """;
		
  sqltxt = sql.firstRow(stmt, [p_sess_id: tmp_sess_id]).sqltxt.getAsciiStream().getText() // SQL log
  tmp = sql.firstRow(stmt2, [p_sess_id: tmp_sess_id]).act_vars.getAsciiStream().getText() // aktuális paraméterek
  sql.close()

  for ( l in tmp.split('\n') ) {
    tmp2 = l.split('=')
    try {
      actVarMap.put(tmp2[0], tmp2[1])   
    } catch (Exception e) { null }

  }

}

sqltxt = sqltxt.replace(':', '')
sqltxt = sqltxt.replace('#', '')

if ( actual_vars_fl == true ) {
    // legújabb működés
    // aktuális változók
    for ( i in actVarMap ) {
      from = i.key
      to = i.value + ' /*' + i.key + '*/'
      sqltxt = sqltxt.replaceAll('\\b' + from + '\\b', to) // regexp word boundary check
    }
}

else {
    // változók
    for ( i in varList ) {
      from = i.getQualifiedName()
    /* a régi KM modulhoz kellett
      if ( from == 'GLOBAL.P_EFFECTIVE_LOAD_DATE' ) {
        pre = "to_date('"
        post = "', 'YYYYMMDDHH24MISS')"
      } else {
        pre = ''
        post = ''
      }
    */ 
      //to = pre + i.getDefaultValue() + post + ' /*' + i.getQualifiedName() + '*/'
      to = i.getDefaultValue() + ' /*' + i.getQualifiedName() + '*/'
      //sqltxt = sqltxt.replace(from, to)
      sqltxt = sqltxt.replaceAll('\\b' + from + '\\b', to) // regexp word boundary check

    }
}

 

// szekvenciák
for (i in seqList ) {
  from = i.getQualifiedName() + '_NEXTVAL'
  to = 'EBH_DW.' + i.getNativeSequenceName() + '.nextval'
  //sqltxt = sqltxt.replace(from, to)
  sqltxt = sqltxt.replaceAll('\\b' + from + '\\b', to) // regexp word boundary check
}

print sqltxt + '\n;\n\n'
def swing = new SwingBuilder()

// ez nem is kell
swing.edt{
    frame( title: 'Session: ' + tmp_sess_id, pack:true, show:true ){
        panel(){
          scrollPane( preferredSize:[800, 800] ){
            editorPane( contentType: ("text/sql"), text: sqltxt)
          }

        }

    }

}

/*
--- munkamenet futtáskor futtatott SQL maszk
select sess_no, def_txt as sqltxt, NNO, NB_RUN,  SCEN_TASK_NO, TASK_BEG, TASK_END,  TASK_DUR, TASK_STATUS, NB_ROW, NB_INS
from ebh_odi_repo.SNP_sess_task_log where 1=1
and sess_no = 39
and def_txt is not null and def_txt like 'insert%'
--  order by length(def_txt) desc

--- munkamenethez tartozó paraméterek
select sess_no, startup_variables as act_vars, SESS_NAME, SESS_BEG, SESS_END, SCEN_NAME, NB_ROW, NB_INS, NB_UPD, NB_DEL, NB_ERR, SB_NO
from EBH_ODI_REPO.SNP_SESSION
where sess_no = 39
*/