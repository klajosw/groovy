//Created by ODI Studio

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

import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.OdiFolder;



// Connections Details

String odiSupervisorUser = "SUPERVISOR";
String odiSupervisorPassword = "simple4u";

String masterRepositoryJdbcUrl = "jdbc:oracle:thin:@//localhost:1521/XE";
String masterRepositoryJdbcDriver = "oracle.jdbc.OracleDriver";
String masterRepositoryJdbcUser = "DEV_ODI_REPO";
String masterRepositoryJdbcPassword = "simple4u";
String workRepositoryName = "WORKREP";

// Respository and ODI Instance

MasterRepositoryDbInfo mRepDbInfo= new MasterRepositoryDbInfo(masterRepositoryJdbcUrl, masterRepositoryJdbcDriver, masterRepositoryJdbcUser, masterRepositoryJdbcPassword.toCharArray(), new PoolingAttributes()); 
WorkRepositoryDbInfo wRepDbInfo= new WorkRepositoryDbInfo(workRepositoryName, new PoolingAttributes()); 
OdiInstance odiInstance = OdiInstance.createInstance(new OdiInstanceConfig(mRepDbInfo, wRepDbInfo));

// Authentication 

Authentication auth = odiInstance.getSecurityManager().createAuthentication(odiSupervisorUser, odiSupervisorPassword.toCharArray());
odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);

try {

// Transaction Instance

ITransactionDefinition txnDef = new DefaultTransactionDefinition();
ITransactionManager tm = odiInstance.getTransactionManager();
ITransactionStatus txnStatus = tm.getTransaction(txnDef);

// Create Project

println 'Createing ODI Project...';

OdiProject sdkProject = new OdiProject("ODI SDK Project","ODI_SDK_PROJECT");
OdiFolder sdkFolder = new OdiFolder(sdkProject,"SDK Folder");
odiInstance.getTransactionalEntityManager().persist(sdkProject);

println 'Created. Project Name: ODI SDK Project';

// Commit transaction, Close Aithentication and ODI Instance

tm.commit(txnStatus);
auth.close();
odiInstance.close();

} 
catch (Exception e)
{

// Commit transaction, Close Aithentication and ODI Instance in Exception Block

  auth.close();
  odiInstance.close();
  println(e);
}