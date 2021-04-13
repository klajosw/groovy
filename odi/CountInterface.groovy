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

import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiInterfaceFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.model.OdiDataStore; 
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiInterface;
import oracle.odi.domain.project.interfaces.DataSet;
import oracle.odi.interfaces.interactive.support.InteractiveInterfaceHelperWithActions;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionAddSourceDataStore;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetTargetDataStore;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionOnTargetDataStoreComputeAutoMapping;
import oracle.odi.interfaces.interactive.support.aliascomputers.AliasComputerDoubleChecker;
import oracle.odi.interfaces.interactive.support.clauseimporters.ClauseImporterDefault;
import oracle.odi.interfaces.interactive.support.mapping.automap.AutoMappingComputerColumnName;
import oracle.odi.interfaces.interactive.support.mapping.matchpolicy.MappingMatchPolicyColumnName;
import oracle.odi.interfaces.interactive.support.targetkeychoosers.TargetKeyChooserPrimaryKey;

try {

//Connections Details

String odiSupervisorUser = "SUPERVISOR";
String odiSupervisorPassword = "simple4u";

String masterRepositoryJdbcUrl = "jdbc:oracle:thin:@//localhost:1521/XE";
String masterRepositoryJdbcDriver = "oracle.jdbc.OracleDriver";
String masterRepositoryJdbcUser = "DEV_ODI_REPO";
String masterRepositoryJdbcPassword = "simple4u";
String workRepositoryName = "WORKREP";

//Respository and ODI Instance

MasterRepositoryDbInfo mRepDbInfo= new MasterRepositoryDbInfo(masterRepositoryJdbcUrl, masterRepositoryJdbcDriver, masterRepositoryJdbcUser, masterRepositoryJdbcPassword.toCharArray(), new PoolingAttributes()); 
WorkRepositoryDbInfo wRepDbInfo= new WorkRepositoryDbInfo(workRepositoryName, new PoolingAttributes()); 
OdiInstance odiInstance = OdiInstance.createInstance(new OdiInstanceConfig(mRepDbInfo, wRepDbInfo));

//Authentication

Authentication auth = odiInstance.getSecurityManager().createAuthentication(odiSupervisorUser, odiSupervisorPassword.toCharArray());
odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);

//Transaction Instance

ITransactionDefinition txnDef = new DefaultTransactionDefinition();
ITransactionManager tm = odiInstance.getTransactionManager();
ITransactionStatus txnStatus = tm.getTransaction(txnDef);

//SDK Folder and Intreface Objects

Collection <OdiInterface> sdkInterface = ((IOdiInterfaceFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiInterface.class)).findByProject('ODI_SDK_PROJECT','SDK Folder');
println sdkInterface.size();

//Commit transaction, Close Aithentication and ODI Instance

tm.commit(txnStatus);
auth.close();
odiInstance.close();

} 
catch (Exception e)
{

//Commit transaction, Close Aithentication and ODI Instance in Exception Block

  auth.close();
  odiInstance.close();
  println(e);
}