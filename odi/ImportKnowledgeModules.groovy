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

import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.impexp.support.ImportServiceImpl;
import oracle.odi.impexp.IImportService;

// Connections Details

String odiSupervisorUser = "SUPERVISOR";
String odiSupervisorPassword = "simple4u";

String masterRepositoryJdbcUrl = "jdbc:oracle:thin:@//localhost:1521/XE";
String masterRepositoryJdbcDriver = "oracle.jdbc.OracleDriver";
String masterRepositoryJdbcUser = "DEV_ODI_REPO";
String masterRepositoryJdbcPassword = "simple4u";
String workRepositoryName = "WORKREP";

// KM XML reference Path

String kmInstallFolder = "C:/Oracle/MiddlewareWeblogic/Oracle_ODI/oracledi/xml-reference/";

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

// Project lookup

OdiProject sdkProject = ((IOdiProjectFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiProject.class)).findByCode("ODI_SDK_PROJECT");

println 'Importing Knowledge Modules...';

// Import KMs

IImportService impService = new ImportServiceImpl(odiInstance);
impService.importObjectFromXml(IImportService.IMPORT_MODE_DUPLICATION, kmInstallFolder + "KM_IKM Oracle Incremental Update.xml", sdkProject, false);
impService.importObjectFromXml(IImportService.IMPORT_MODE_DUPLICATION, kmInstallFolder + "KM_CKM Oracle.xml", sdkProject, false);
impService.importObjectFromXml(IImportService.IMPORT_MODE_DUPLICATION, kmInstallFolder + "KM_LKM SQL to Oracle.xml", sdkProject, false);

println 'KMs Imported:';
println 'KM_IKM Oracle Incremental Update';
println 'KM_CKM Oracle';
println 'KM_LKM SQL to Oracle';

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