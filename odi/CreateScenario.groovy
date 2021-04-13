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

import oracle.odi.runtime.agent.RuntimeAgent;
import oracle.odi.domain.project.finder.IOdiInterfaceFinder;
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.generation.support.OdiScenarioGeneratorImpl;
import oracle.odi.generation.IOdiScenarioGenerator;
import oracle.odi.runtime.agent.invocation.ExecutionInfo;
import oracle.odi.domain.runtime.session.finder.IOdiSessionFinder;
import oracle.odi.domain.runtime.session.OdiSession;


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

ITransactionDefinition txnDef = new DefaultTransactionDefinition();
ITransactionManager tm = odiInstance.getTransactionManager();
ITransactionStatus txnStatus = tm.getTransaction(txnDef);

try 
{

  for (oracle.odi.domain.project.OdiInterface odiInterfaceCollection : ((IOdiInterfaceFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiInterface.class)).findByProject('ODI_SDK_PROJECT','SDK Folder'))
  
  {
    println "Generating Scenario for Interface " + odiInterfaceCollection.getName();
    IOdiScenarioGenerator sdkIntScenario = new OdiScenarioGeneratorImpl(odiInstance);
    
    odiScenarioName = "SCN_" + odiInterfaceCollection.getName().getAt([4..-1]);
    sdkIntScenario.generateScenario(odiInterfaceCollection, odiScenarioName, "001");
    }
    
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
  