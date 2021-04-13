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
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.session.finder.IOdiSessionFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
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

try 
{
  
  // Create Runtime Agent
  
  RuntimeAgent runtimeAgent = new RuntimeAgent(odiInstance, odiSupervisorUser, odiSupervisorPassword.toCharArray());
  
  for (oracle.odi.domain.project.OdiInterface odiInterfaceCollection : ((IOdiInterfaceFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiInterface.class)).findByProject('ODI_SDK_PROJECT','SDK Folder'))
  {
  
      int odiIinterfaceId = odiInterfaceCollection.getInterfaceId();
      for (oracle.odi.domain.runtime.scenario.OdiScenario odiScenarioCollection : ((IOdiScenarioFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiScenario.class)).findBySourceInterface(odiIinterfaceId))
      
      {
        
        ExecutionInfo sdkExecInfo = runtimeAgent.startScenario(odiScenarioCollection.getName(), "001", null, null, "GLOBAL", 5, null, true);    
        
        OdiSession sdkSession = ((IOdiSessionFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiSession.class)).findBySessionId(sdkExecInfo.getSessionId());
        println("Completed Interface Execution. Session " + sdkSession.getName() + " (" + sdkSession.getSessionId() + ").") ;
        println("Status: " + sdkSession.getStatus());
        println("Return Code: " + sdkSession.getReturnCode());
        String sdkError = sdkSession.getErrorMessage();
          if (sdkError != null) 
          {
            println("Error Message: " + sdkSession.getErrorMessage());
          }
        println '########################################################################';
      }
    
    }
    
// Commit transaction, Close Aithentication and ODI Instance

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
  