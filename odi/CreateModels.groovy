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

import oracle.odi.domain.topology.finder.IOdiLogicalSchemaFinder;
import oracle.odi.domain.topology.finder.IOdiContextFinder;
import oracle.odi.domain.topology.OdiLogicalSchema;
import oracle.odi.domain.topology.OdiContext;
import oracle.odi.domain.model.OdiModel;
import oracle.odi.domain.model.OdiDataStore; 
import oracle.odi.domain.model.OdiColumn; 
import oracle.odi.domain.model.OdiKey;

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

// Transaction Instance

ITransactionDefinition txnDef = new DefaultTransactionDefinition();
ITransactionManager tm = odiInstance.getTransactionManager();
ITransactionStatus txnStatus = tm.getTransaction(txnDef);

try {

// Logical Schema Instance

OdiContext sdkContext = ((IOdiContextFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiContext.class)).findDefaultContext();

// Source Model Object

println 'Creating Source Model...';
OdiLogicalSchema srcLogicalSchema = ((IOdiLogicalSchemaFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiLogicalSchema.class)).findByName("ODI");
OdiModel srcModel = new OdiModel(srcLogicalSchema, "MODEL_SRC_STG1", "MODEL_SRC_STG1");
srcModel.setReverseContext(sdkContext);
srcModel.setReverseObjectMask('%SRC%');
odiInstance.getTransactionalEntityManager().persist(srcModel);
println 'Created. Model Name: MODEL_SRC_STG1';


// Target Model Object

println 'Creating Target Model...';
OdiLogicalSchema trgLogicalSchema = ((IOdiLogicalSchemaFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiLogicalSchema.class)).findByName("ODI");
OdiModel trgModel = new OdiModel(srcLogicalSchema, "MODEL_TRG_STG1", "MODEL_TRG_STG1");
trgModel.setReverseContext(sdkContext);
trgModel.setReverseObjectMask('%TRG%');
odiInstance.getTransactionalEntityManager().persist(trgModel);
println 'Created. Model Name: MODEL_TRG_STG1';

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