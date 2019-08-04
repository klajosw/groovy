/*
//Created by klajosw@gmail.com  // https://klajosw.blogspot.com/
HU:   ODI12c project létrehozása ODIstudio-ban groovy script editort elérése a menűből Tools>Groovy>New Script 
ENG : Create Project ODI12c Groovy Script in ODIStudio menu Tools>Groovy>New Script 
*/


// ODI class importálás // Core
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.security.Authentication;
import oracle.odi.core.persistence.transaction.ITransactionDefinition;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.persistence.transaction.ITransactionManager;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
// ODI class importálás // Domain
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.OdiFolder;


// HU: ODI kapcsolat részleteinek beállitása / ODI Connections Details

String odiSupervisorUser = "SUPERVISOR";
String odiSupervisorPassword = "jelszo01";

String masterRepositoryJdbcUrl = "jdbc:oracle:thin:@//localhost:1521/XE";  /// oracle expres locat intall
String masterRepositoryJdbcDriver = "oracle.jdbc.OracleDriver";
String masterRepositoryJdbcUser = "ODI_REPO";
String masterRepositoryJdbcPassword = "REPO_User";
String workRepositoryName = "jelszo01";

// HU: ODI repozitori és adatbázis példány kapcsolat / ENG:Respository and ODI Instance connect
MasterRepositoryDbInfo mRepDbInfo= new MasterRepositoryDbInfo(masterRepositoryJdbcUrl, masterRepositoryJdbcDriver, masterRepositoryJdbcUser, masterRepositoryJdbcPassword.toCharArray(), new PoolingAttributes()); 
WorkRepositoryDbInfo wRepDbInfo= new WorkRepositoryDbInfo(workRepositoryName, new PoolingAttributes()); 
OdiInstance odiInstance = OdiInstance.createInstance(new OdiInstanceConfig(mRepDbInfo, wRepDbInfo));


// HU: Azonositás / ENG : Authentication (Super user)
Authentication auth = odiInstance.getSecurityManager().createAuthentication(odiSupervisorUser, odiSupervisorPassword.toCharArray());
odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);

// HU: Hibakezelés / ENG : error handle
try {


// HU Tranzkció kezelés / Eng: Transaction Instance
ITransactionDefinition txnDef = new DefaultTransactionDefinition();
ITransactionManager tm = odiInstance.getTransactionManager();
ITransactionStatus txnStatus = tm.getTransaction(txnDef);
OdiProject sdkProject = new OdiProject("ODI EDW","ODI_EDW");
OdiFolder sdkFolder = new OdiFolder(sdkProject,"First Folder");
odiInstance.getTransactionalEntityManager().persist(sdkProject);

println 'HI: Project létrehozás megtörtént minden OK';
println 'ENG:Created. Project, It is ALL RIGHT';

// HU :Sikeres végrejajtás után véglegesités és lezárások / ENG OK event : Commit transaction, Close Aithentication and ODI Instance

tm.commit(txnStatus);
auth.close();
odiInstance.close();

} 
catch (Exception e)
{

// HU: Hiba esetén hibakezelő ág lezárásai / ENG: Error event : Close Aithentication and ODI Instance in Exception Block

  auth.close();
  odiInstance.close();
  println(e);           // HU : Hiba özenet kiiratása / ENG : Print error message
}

/// ------- HU : Vége / ENG : END