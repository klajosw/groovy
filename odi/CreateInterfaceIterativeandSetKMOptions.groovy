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
import oracle.odi.domain.topology.finder.IOdiContextFinder;
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

import oracle.odi.domain.project.OdiIKM;
import oracle.odi.domain.project.finder.IOdiIKMFinder;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetKM;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetKM.KMType;
import oracle.odi.interfaces.interactive.support.actions.InterfaceActionSetKMOptionValue;
import oracle.odi.interfaces.interactive.support.km.optionretainer.KMOptionRetainerLazy;


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

try 
{    
    ITransactionDefinition txnDef = new DefaultTransactionDefinition();
    ITransactionManager tm = odiInstance.getTransactionManager();
    ITransactionStatus txnStatus = tm.getTransaction(txnDef);
    
    // SDK Folder and Intreface Objects
    
    OdiFolder sdkFolder = ((IOdiFolderFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiFolder.class)).findByName("SDK Folder").get(0);
    OdiContext sdkContext = ((IOdiContextFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiContext.class)).findDefaultContext();
    
    for (oracle.odi.domain.model.OdiDataStore odiDataSourceCollection : odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class).findAll())
    {
  
        if  (odiDataSourceCollection.getName().getAt([0..2]) == 'SRC' & odiDataSourceCollection.getModel().getName().getAt([0..8]) == 'MODEL_SRC')
        {
            
            println 'Creating Interface...';
            String odiInterfaceName = 'IFC'.concat('_'.concat(odiDataSourceCollection.getName().concat('_'.concat('TRG'.concat('_'.concat(odiDataSourceCollection.getName().getAt([4..-1])))))));        
            println "Interface Name:"+ odiInterfaceName;
            OdiInterface sdkInterface = new OdiInterface(sdkFolder, odiInterfaceName, sdkContext);
            
            //Source Mapping
            
            println "SOURCE MODEL: " + odiDataSourceCollection.getModel().getName();
            println "SOURCE DATASTORE NAME: " + odiDataSourceCollection.getName(); 
            InteractiveInterfaceHelperWithActions sdkIntHelper = new InteractiveInterfaceHelperWithActions(sdkInterface, odiInstance, odiInstance.getTransactionalEntityManager());
            DataSet dataSet = sdkInterface.getDataSets().get(0);
            sdkIntHelper.performAction(new InterfaceActionAddSourceDataStore(odiDataSourceCollection, dataSet, new AliasComputerDoubleChecker(), new ClauseImporterDefault(), new AutoMappingComputerColumnName()));
            
            // Target Mapping
            
            sdkTrgDataSourceModelName = 'MODEL_TRG'.concat('_'.concat(odiDataSourceCollection.getModel().getName().getAt([10..-1])));
            sdkTrgDataStoreName = 'TRG'.concat('_'.concat(odiDataSourceCollection.getName().getAt([4..-1])));
            println "TARGET MODEL: " + sdkTrgDataSourceModelName;
            println "TARGET DATASTORE NAME: " + sdkTrgDataStoreName;
            
            OdiDataStore sdkTrgDataStore = ((IOdiDataStoreFinder)odiInstance.getTransactionalEntityManager().getFinder(OdiDataStore.class)).findByName(sdkTrgDataStoreName, sdkTrgDataSourceModelName);
            
            sdkIntHelper.performAction(new InterfaceActionSetTargetDataStore(sdkTrgDataStore, new MappingMatchPolicyColumnName(), new AutoMappingComputerColumnName(), new AutoMappingComputerColumnName(), new TargetKeyChooserPrimaryKey()));
            sdkIntHelper.performAction(new InterfaceActionOnTargetDataStoreComputeAutoMapping());
            sdkIntHelper.computeSourceSets();
            
            for (Collection<OdiIKM> odiIKM : ((IOdiIKMFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiIKM.class)).findByName('IKM Oracle Incremental Update','ODI_SDK_PROJECT'))
              {
                
                sdkIntHelper.performAction(new InterfaceActionSetKM(odiIKM, sdkInterface.getTargetDataStore(), KMType.IKM, new KMOptionRetainerLazy()));
                sdkIntHelper.performAction(new InterfaceActionSetKMOptionValue(sdkInterface.getTargetDataStore(), KMType.IKM, "TRUNCATE", true)) ;
                
              } 
              
            sdkIntHelper.preparePersist();
            
            println "##########################################################";
        
        }
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