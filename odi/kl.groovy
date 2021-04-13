Create Project using ODI12c Groovy Script:
This is a quick post based on the request coming from newbies in OTN. Using this post you will be able to create a project and mapping in ODI 12c. To create a groovy script in ODIStudio, navigate to Tools>Groovy>New Script. You can refer this post written by DavidAllan to get more insight about 12c Scripts.
Make sure you have reversed EMP table under these models.
USERSRC
USERDEST

import oracle.odi.domain.project.OdiProject
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.model.finder.IOdiDataStoreFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiKMFinder
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.adapter.project.IKnowledgeModule.ProcessingType
import oracle.odi.domain.model.OdiDataStore
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition

def setExpr(comp, tgtTable, propertyName, expressionText) {
DatastoreComponent.findAttributeForColumn(comp,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}

//CREATE PROJECT STARTS
def createProject(project_name, project_folder_name){
project_code = project_name
txnDef = new DefaultTransactionDefinition();
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)

pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
project = pf.findByCode(project_name)
if (project != null) {
println "Project Already Exists. Project Creation Skipped"
}
else{
project = new OdiProject(project_name, project_name)
tme.persist(project)
folder = new OdiFolder(project, project_folder_name)
tme.persist(folder)
tm.commit(txnStatus)
println "Project Created Successfully"
}
}
//CREATE PROJECT COMPLETES

//CREATE MAPPING STARTS
def createMapping(project_name,project_folder_name,myMap) {

txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)

pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
project = pf.findByCode(project_name)
folderColl = ff.findByName(project_folder_name, project_name)
OdiFolder folder = null
if (folderColl.size() == 1)
folder = folderColl.iterator().next()

dsf = (IOdiDataStoreFinder)tme.getFinder(OdiDataStore.class)
mapf = (IMappingFinder) tme.getFinder(Mapping.class)

Mapping map = (mapf).findByName(folder, myMap)
if ( map!=null) {
println "Map Already Exists. Map Creation Skipped"
}
else{
map = new Mapping(myMap, folder)
tme.persist(map)

ds_source = dsf.findByName("EMP", "USERSRC")
ds_src_comp = new DatastoreComponent(map, ds_source)
ds_target = dsf.findByName("EMP", "USERDEST")
ds_tgt_comp = new DatastoreComponent(map, ds_target)

ds_src_comp.connectTo(ds_tgt_comp)
setExpr(ds_tgt_comp, ds_target, "EMPNO", "EMP.EMPNO")
setExpr(ds_tgt_comp, ds_target, "ENAME", "EMP.ENAME")
setExpr(ds_tgt_comp, ds_target, "JOB", "EMP.JOB")
setExpr(ds_tgt_comp, ds_target, "MGR", "EMP.MGR")
setExpr(ds_tgt_comp, ds_target, "HIREDATE", "EMP.HIREDATE")
setExpr(ds_tgt_comp, ds_target, "SAL", "EMP.SAL")
setExpr(ds_tgt_comp, ds_target, "COMM", "EMP.COMM")
setExpr(ds_tgt_comp, ds_target, "DEPTNO", "EMP.DEPTNO")

deploymentspec = map.getDeploymentSpec(0)
node = deploymentspec.findNode(ds_tgt_comp)
println deploymentspec.getExecutionUnits()
aps = deploymentspec.getAllAPNodes()
tgts = deploymentspec.getTargetNodes()

ikmf = (IOdiKMFinder)tme.getFinder(OdiIKM.class)
ins_ikm = ikmf.findByName("IKM Oracle Insert");
lkmf = (IOdiKMFinder)tme.getFinder(OdiLKM.class)
sql_lkm = lkmf.findByName("LKM Oracle to Oracle Pull (DB Link)");

api = aps.iterator()
ap_node = api.next()
ap_node.setLKM(sql_lkm)
ap_node.getOptionValue(ProcessingType.TARGET,"ADD_DRIVING_SITE_HINT").setValue("true")

tme.persist(map)
tm.commit(txnStatus)
println "Mapping Created SUccessfully"
}
}
//CREATE MAPPING ENDS

//Call project
createProject("PRO_SCOTT","First Folder")
//Call mapping
createMapping("PRO_SCOTT", "First Folder", "New_Mapping")


[code lang=”java”]
ds_source = dsf.findByName(“EMP”, “MODEL_SRC_STG1”)
ds_src_comp = new DatastoreComponent(map, ds_source)
ds_target = dsf.findByName(“EMP”, “MODEL_TRG_STG1 “)
[/code]


----------------

i have replced this models in code then i am getting above error already i posted..
please check following code..
import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.model.finder.IOdiDataStoreFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiKMFinder
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.adapter.project.IKnowledgeModule.ProcessingType
import oracle.odi.domain.model.OdiDataStore
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition
import java.util.Iterator
def setExpr(comp, tgtTable, propertyName, expressionText) {
DatastoreComponent.findAttributeForColumn(comp,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
}
//CREATE PROJECT STARTS
def createProject(project_name, project_folder_name){
project_code = project_name
txnDef = new DefaultTransactionDefinition();
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)
pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
project = pf.findByCode(project_name)
if (project != null) {
println “Project Already Exists. Project Creation Skipped”
}
else{
project = new OdiProject(project_name, project_name)
tme.persist(project)
folder = new OdiFolder(project, project_folder_name)
tme.persist(folder)
tm.commit(txnStatus)
println “Project Created Successfully”
}
}
//CREATE PROJECT COMPLETES
//CREATE MAPPING STARTS
def createMapping(project_name,project_folder_name,myMap) {
txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)
pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
project = pf.findByCode(project_name)
folderColl = ff.findByName(project_folder_name, project_name)
OdiFolder folder = null
if (folderColl.size() == 1)
folder = folderColl.iterator().next()
dsf = (IOdiDataStoreFinder)tme.getFinder(OdiDataStore.class)
mapf = (IMappingFinder) tme.getFinder(Mapping.class)
Mapping map = (mapf).findByName(folder, myMap)
if ( map!=null) {
println “Map Already Exists. Map Creation Skipped”
}
else{
map = new Mapping(myMap, folder)
tme.persist(map)
ds_source = dsf.findByName(“EMP”, “MODEL_SRC_STG1”)
ds_src_comp = new DatastoreComponent(map, ds_source)
ds_target = dsf.findByName(“EMP”, “MODEL_TRG_STG1”)
ds_tgt_comp = new DatastoreComponent(map, ds_target)
ds_src_comp.connectTo(ds_tgt_comp)
setExpr(ds_tgt_comp, ds_target, “EMPNO”, “EMP.EMPNO”)
setExpr(ds_tgt_comp, ds_target, “ENAME”, “EMP.ENAME”)
setExpr(ds_tgt_comp, ds_target, “DEPTNO”, “EMP.DEPTNO”)
deploymentspec = map.getDeploymentSpec(0)
node = deploymentspec.findNode(ds_tgt_comp)
println deploymentspec.getExecutionUnits()
aps = deploymentspec.getAllAPNodes()
tgts = deploymentspec.getTargetNodes()
ikmf = (IOdiKMFinder)tme.getFinder(OdiIKM.class)
ins_ikm = ikmf.findByName(“IKM Oracle Insert”);
lkmf = (IOdiKMFinder)tme.getFinder(OdiLKM.class)
sql_lkm = lkmf.findByName(“LKM Oracle to Oracle Pull (DB Link)”);
api = aps.iterator()
ap_node = api.next()
ap_node.setLKM(sql_lkm)
ap_node.getOptionValue(ProcessingType.TARGET,”ADD_DRIVING_SITE_HINT”).setValue(“true”)
tme.persist(map)
tm.commit(txnStatus)
println “Mapping Created SUccessfully”
}
}
//CREATE MAPPING ENDS
//Call project
createProject(“PRO_SCOTT”,”First Folder”)
//Call mapping
createMapping(“PRO_SCOTT”, “First Folder”, “New_Mapping”)


----------------------------------

ODI Version: 12.1.3


import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.model.finder.IOdiDataStoreFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder 
import oracle.odi.domain.mapping.finder.IMappingFinder 
import oracle.odi.domain.model.OdiDataStore
import oracle.odi.domain.model.OdiModel
import oracle.odi.domain.model.finder.IOdiModelFinder
import  oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition


//set expression to the component
def createExp(comp, tgtTable, colName) { 
  DatastoreComponent.findAttributeForColumn(comp,tgtTable.getColumn(colName))    .setExpressionText(sourceDatastoreName+"."+colName)
}

//delete mapping with the same name
def removeMapping(folder, map_name) {
  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)
  try {
    Mapping map = ((IMappingFinder)     tme.getFinder(Mapping.class)).findByName(folder, map_name)
    if (map != null) {
      odiInstance.getTransactionalEntityManager().remove(map);
    }
  } catch (Exception e) {e.printStackTrace();}
  tm.commit(txnStatus)
}

//looking for a project and folder
def find_folder(project_code, folder_name) {
  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)
  pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
  ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
  project = pf.findByCode(project_code)

//if there is no project, create new one
  if (project == null) {
     project = new OdiProject(project_code, project_code) 
     tme.persist(project)
  }
//if there is no folder, create new one
  folderColl = ff.findByName(folder_name, project_code)
  OdiFolder folder = null
  if (folderColl.size() == 1)
    folder = folderColl.iterator().next()
  if (folder == null) {
     folder = new OdiFolder(project, folder_name) 
     tme.persist(folder)
  }
  tm.commit(txnStatus)
  return folder
}

def get_datastores_by_model(sourcemodelName) {
    txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager() 
  
  smf  = (IOdiModelFinder) tme.getFinder(OdiModel.class)
  sourcemodel= smf.findByCode(sourcemodelName)
 ds = sourcemodel.getDataStores()
 
  return ds
}

 

def create_map(projectName, folderName, sourcemodelName, targetmodelName, sourceDatastoreName){

targetDatastoreName = "STG_"+sourceDatastoreName
//mapping
mappingName = targetDatastoreName+"_INT"

//find project and the folder
  folder = find_folder(projectName,folderName)
//delete old mapping
  removeMapping(folder, mappingName)

  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)

  dsf = (IOdiDataStoreFinder)tme.getFinder(OdiDataStore.class)
  mapf = (IMappingFinder) tme.getFinder(Mapping.class)

//create new mapping
  map = new Mapping(mappingName, folder);
  tme.persist(map)

//insert source/target table
  boundTo_emp = dsf.findByName(sourceDatastoreName, sourcemodelName)
  comp_emp = new DatastoreComponent(map, boundTo_emp)

  boundTo_tgtemp = dsf.findByName(targetDatastoreName, targetmodelName)
  comp_tgtemp = new DatastoreComponent(map, boundTo_tgtemp)

//create filter  
comp_filter = new FilterComponent(map, "FILTER") 
comp_emp.connectTo(comp_filter)  
comp_filter.connectTo(comp_tgtemp) 
comp_filter.setFilterCondition("1=1")

//map s2t
for (i=0; i<boundTo_tgtemp.getColumns().size(); i++) {
 createExp(comp_tgtemp, boundTo_tgtemp,  boundTo_tgtemp.getColumns().get(i).getName())
}

deploymentspec = map.getDeploymentSpec(0) 
//assign IKM
tgts = deploymentspec.getTargetNodes() 
tgts_it = tgts.iterator()
while (tgts_it.hasNext() ){
tgt_node = tgts_it.next() 
tgt_node.setIKMByName("IKM SQL Control Append_v3")}
//assign LKM
aps = deploymentspec.getAllAPNodes() 
aps_it = aps.iterator() 
while(aps_it.hasNext()){
ap_node = aps_it.next()  
ap_node.setLKMByName("LKM SQL to Oracle")}

tme.persist(map)
tm.commit(txnStatus)

println "Mapping Created Successfully"
}





//Variables 
projectName = "ABONENET" 
folderName = "test" 
sourcemodelName = "USERSRC"
targetmodelName = "USERDEST"  

ds= get_datastores_by_model(sourcemodelName) 
ds_it = ds.iterator()
while(ds_it.hasNext()){
ds_one = ds_it.next()
 println ds_one.getName() 
  create_map(projectName, folderName, sourcemodelName, targetmodelName, ds_one.getName() )
 }
 
 
 -----------------------
 //Von ODI Studio erstellt
//
//name of the project
projectName = "SRC_TO_TRG"
//name of the folder
ordnerName = "FEN_TEST"
//name of the mapping
mappingName = "MAP1_FF_TO_TRG"
//name of the model
modelName = "DB_FEN"
//name of the source datastore
sourceDatastoreName = "SRC_TEST_FEN"
//name of the target datastore
targetDatastoreName = "TRG_TEST_FEN"

import oracle.odi.domain.project.finder.IOdiProjectFinder
import oracle.odi.domain.model.finder.IOdiDataStoreFinder
import oracle.odi.domain.project.finder.IOdiFolderFinder
import oracle.odi.domain.project.finder.IOdiKMFinder
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.adapter.project.IKnowledgeModule.ProcessingType
import oracle.odi.domain.model.OdiDataStore
import  oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition


//set expression to the component
def createExp(comp, tgtTable, propertyName, expressionText) { 
  DatastoreComponent.findAttributeForColumn(comp,tgtTable.getColumn(propertyName))    .setExpressionText(expressionText)
}

//delete mapping with the same name
def removeMapping(folder, map_name) {
  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)
  try {
    Mapping map = ((IMappingFinder)     tme.getFinder(Mapping.class)).findByName(folder, map_name)
    if (map != null) {
      odiInstance.getTransactionalEntityManager().remove(map);
    }
  } catch (Exception e) {e.printStackTrace();}
  tm.commit(txnStatus)
}

//looking for a project and folder
def find_folder(project_code, folder_name) {
  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)
  pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
  ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)
  project = pf.findByCode(project_code)

//if there is no project, create new one
  if (project == null) {
     project = new OdiProject(project_code, project_code) 
     tme.persist(project)
  }
//if there is no folder, create new one
  folderColl = ff.findByName(folder_name, project_code)
  OdiFolder folder = null
  if (folderColl.size() == 1)
    folder = folderColl.iterator().next()
  if (folder == null) {
     folder = new OdiFolder(project, folder_name) 
     tme.persist(folder)
  }
  tm.commit(txnStatus)
  return folder
}

//name of the project and the folder
  folder = find_folder(projectName,ordnerName)
//delete old mapping
  removeMapping(folder, mappingName)

  txnDef = new DefaultTransactionDefinition()
  tm = odiInstance.getTransactionManager()
  tme = odiInstance.getTransactionalEntityManager()
  txnStatus = tm.getTransaction(txnDef)

  dsf = (IOdiDataStoreFinder)tme.getFinder(OdiDataStore.class)
  mapf = (IMappingFinder) tme.getFinder(Mapping.class)

//create new mapping
  map = new Mapping(mappingName, folder);
  tme.persist(map)

//insert source table
  boundTo_emp = dsf.findByName(sourceDatastoreName, modelName)
  comp_emp = new DatastoreComponent(map, boundTo_emp)

 //insert target table
  boundTo_tgtemp = dsf.findByName(targetDatastoreName, modelName)
  comp_tgtemp = new DatastoreComponent(map, boundTo_tgtemp)

 //create expression-operator  
  comp_expression = new ExpressionComponent(map, "EXPRESSION")

// define expression
  comp_expression.addExpression("LAND_KM",     "TO_NUMBER(SRC_TEST_FEN.LAND_KM)", null,null,null);
  comp_expression.addExpression("DATE_OF_ELECTION",     "TO_DATE(SRC_TEST_FEN.DATE_OF_ELECTION, 'DD.MM.YYYY')", null,null,null);
//weitere Transformationen anhängen möglich   

//link source table with expression
  comp_emp.connectTo(comp_expression)

//link expression with target table
  comp_expression.connectTo(comp_tgtemp)

  createExp(comp_tgtemp, boundTo_tgtemp, "ABBR", "SRC_TEST_FEN.ABBR")
  createExp(comp_tgtemp, boundTo_tgtemp, "NAME", "SRC_TEST_FEN.NAME")
  createExp(comp_tgtemp, boundTo_tgtemp, "LAND_KM", "EXPRESSION.LAND_KM")
  createExp(comp_tgtemp, boundTo_tgtemp, "DATE_OF_ELECTION",     "EXPRESSION.DATE_OF_ELECTION")

  tme.persist(map)
  tm.commit(txnStatus)
  
  ---------------------------
  
  /*
 * OdiDsl to create a SCD2 dimension load mapping
 */

mapping.drop('MY_PROJECT', 'DEMO_FOLDER', 'EMPLOYEE_DIM_LOAD')

mapping.create('MY_PROJECT', 'DEMO_FOLDER', 'EMPLOYEE_DIM_LOAD')
        .datastores([
                [name: "HR.EMPLOYEES"],
                [name: "HR.DEPARTMENTS"],
                [name: "HR.JOBS"],
                [name: "PERF.D_EMPLOYEE", integration_type: "SCD"],
        ])
        .select("EMPLOYEES")
            .filter('NAME_FILTER', [filter_condition: "EMPLOYEES.FIRST_NAME LIKE 'D%'" ])
            .join('EMP_DEPT', ['DEPARTMENTS'], [join_condition: "EMPLOYEES.DEPARTMENT_ID = DEPARTMENTS.DEPARTMENT_ID" ])
            .join('DEPT_JOBS', ['JOBS'], [join_condition: "EMPLOYEES.JOB_ID = JOBS.JOB_ID" ])
            .connect("D_EMPLOYEE", [
                                        [ attr: "employee_id", key_indicator: true ],
                                        [ attr: "eff_from_date", expression: "sysdate", execute_on_hint: "TARGET"],
                                        [ attr: "eff_to_date", expression: "sysdate", execute_on_hint: "TARGET"],
                                        [ attr: "current_flag", expression: 1, execute_on_hint: "TARGET"],
                                        [ attr: "surr_key", expression: ":RM_PROJECT.D_EMPLOYEE_SEQ_NEXTVAL", execute_on_hint: "TARGET"],
                                   ])
        .commit()
        .validate()
		
---------------------------------

same time, retaining its inherent power. We call this mini mapping language OdiDsl ( Oracle Data Integrator Domain Specific Language ) catchy heh?!

If we execute .reverse() on this mapping by calling...
mapping.reverse('MY_PROJECT', 'DEMO_FOLDER', 'CRAZY_MAPPING')
...OdiDsl will return the following output to the console. What you are seeing here is the OdiDsl required to recreate the crazy mapping above.
Connecting to the repository...

mapping.create('MY_PROJECT', 'DEMO_FOLDER', 'CRAZY_MAPPING')
	.datastores([
		 ['name':'STAGING.TABLE1', 'alias':'TABLE1'],
		 ['name':'STAGING.TABLE9', 'alias':'TABLE9'],
		 ['name':'STAGING.TABLE3', 'alias':'TABLE3'],
		 ['name':'STAGING.TABLE4', 'alias':'TABLE4'],
		 ['name':'STAGING.TABLE6', 'alias':'TABLE6'],
		 ['name':'STAGING.TABLE5', 'alias':'TABLE5'],
		 ['name':'STAGING.TABLE7', 'alias':'TABLE7'],
		 ['name':'STAGING.TABLE2', 'alias':'TABLE2'],
		 ['name':'STAGING.TABLE8', 'alias':'TABLE8'],
		 ['name':'STAGING.TABLE11', 'alias':'TABLE11'],
		 ['name':'STAGING.TABLE12', 'alias':'TABLE12'],
		 ['name':'STAGING.TABLE13', 'alias':'TABLE13'],
		 ['name':'STAGING.TABLE15', 'alias':'TABLE15'],
		 ['name':'STAGING.TABLE14', 'alias':'TABLE14'],
		 ['name':'STAGING.TABLE16', 'alias':'TABLE16'],
		 ['name':'STAGING.TABLE17', 'alias':'TABLE17'],
		 ['name':'STAGING.TABLE42', 'alias':'TABLE42'],
	])
	.select('TABLE5')
		.join('JOIN2', ['TABLE7'], [join_condition: "TABLE5.ID = TABLE7.ID" ])
		.join('JOIN3', ['TABLE6'], [join_condition: "TABLE6.ID = TABLE7.ID" ])
		.connect('TABLE14', [
				[ attr: "ID", expression: "TABLE5.ID" ],
				[ attr: "COL1", expression: "TABLE7.COL1" ],
				[ attr: "COL2", expression: "TABLE6.COL2" ],
				[ attr: "COL3", expression: "TABLE7.COL3" ],
				[ attr: "COL4", expression: "TABLE7.COL4" ],
		])
	.select('JOIN3')
		.expr('EXPRESSION1', [attrs: [
				[ attr: "ID", expression: "TABLE6.ID * 42", datatype: "NUMERIC", size: "38", scale: "0"]]])
		.connect('TABLE15', [
				[ attr: "ID", expression: "EXPRESSION1.ID" ],
				[ attr: "COL1", expression: "", active_indicator: false ],
				[ attr: "COL2", expression: "TABLE6.COL2" ],
				[ attr: "COL3", expression: "TABLE7.COL3" ],
				[ attr: "COL4", expression: "", active_indicator: false ],
		])
		.join('JOIN', ['TABLE14'], [join_condition: "TABLE14.ID = TABLE15.ID" ])
		.filter('FILTER2', [filter_condition: "TABLE15.COL3 != 'FOOBAR'" ])
		.connect('TABLE16', [
				[ attr: "ID", expression: "TABLE15.ID" ],
				[ attr: "COL1", expression: "TABLE15.COL1" ],
				[ attr: "COL2", expression: "TABLE14.COL2" ],
				[ attr: "COL3", expression: "TABLE14.COL3" ],
				[ attr: "COL4", expression: "TABLE14.COL4" ],
		])
	.select('JOIN')
		.connect('TABLE17', [
				[ attr: "ID", expression: "TABLE15.ID" ],
				[ attr: "COL1", expression: "TABLE15.COL1" ],
				[ attr: "COL2", expression: "TABLE14.COL2" ],
				[ attr: "COL3", expression: "TABLE14.COL3" ],
				[ attr: "COL4", expression: "TABLE14.COL4" ],
		])
	.select('TABLE5')
		.sort('SORT1', [sorter_condition: "TABLE5.ID, TABLE5.COL2, TABLE5.COL4" ])
		.connect('TABLE13', [
				[ attr: "ID", expression: "TABLE5.ID" ],
				[ attr: "COL1", expression: "TABLE5.COL1" ],
				[ attr: "COL2", expression: "TABLE5.COL2" ],
				[ attr: "COL3", expression: "TABLE5.COL3" ],
				[ attr: "COL4", expression: "TABLE5.COL4" ],
		])
	.select('TABLE3')
		.filter('FILTER1', [filter_condition: "TABLE3.ID != 42" ])
	.select('TABLE4')
		.filter('FILTER', [filter_condition: "TABLE4.COL1 = 42" ])
		.lookup('LOOKUP1', 'FILTER1', [join_condition: "TABLE4.ID = TABLE3.ID AND TABLE3.COL1 = TABLE4.COL1"])
		.join('JOIN5', ['TABLE13'], [join_condition: "TABLE13.ID = TABLE3.ID" ])
		.distinct('DISTINCT_', [attrs: [
				[ attr: "COL3_1", expression: "TABLE4.COL3", datatype: "VARCHAR", size: "30"],
				[ attr: "COL4_1", expression: "TABLE4.COL4", datatype: "VARCHAR", size: "30"]]])
	.select('DISTINCT_')
		.join('JOIN4', ['EXPRESSION1'], [join_condition: "TABLE5.ID = TABLE6.COL1" ])
		.sort('SORT', [sorter_condition: "EXPRESSION1.ID" ])
		.connect('TABLE8', [
				[ attr: "ID", expression: "EXPRESSION1.ID" ],
				[ attr: "COL1", expression: "", active_indicator: false ],
				[ attr: "COL2", expression: "", active_indicator: false ],
				[ attr: "COL3", expression: "TABLE7.COL3" ],
				[ attr: "COL4", expression: "", active_indicator: false ],
		])
		.connect('TABLE12', [
				[ attr: "ID", expression: "TABLE8.ID" ],
				[ attr: "COL1", expression: "TABLE8.COL1" ],
				[ attr: "COL2", expression: "TABLE8.COL2" ],
				[ attr: "COL3", expression: "TABLE8.COL3" ],
				[ attr: "COL4", expression: "TABLE8.COL4" ],
		])
	.select('TABLE9')
		.expr('EXPRESSION', [attrs: [
				[ attr: "ID", expression: "TABLE9.ID *42", datatype: "NUMERIC", size: "38", scale: "0"],
				[ attr: "COL4", expression: "TABLE9.COL4 || 'FOOBAR'", datatype: "VARCHAR", size: "30"]]])
		.connect('TABLE1', [
				[ attr: "ID", expression: "EXPRESSION.ID" ],
				[ attr: "COL1", expression: "", active_indicator: false ],
				[ attr: "COL2", expression: "", active_indicator: false ],
				[ attr: "COL3", expression: "", active_indicator: false ],
				[ attr: "COL4", expression: "TABLE9.COL4" ],
		])
		.join('JOIN1', ['TABLE2'], [join_condition: "TABLE1.ID = TABLE2.ID" ])
		.aggregate('AGGREGATE', [attrs: [
				[ attr: "ID", expression: "TABLE1.ID", datatype: "NUMERIC", size: "38", scale: "0", group_by: "YES"],
				[ attr: "COL4_1", expression: "MAX(TABLE2.COL4)", datatype: "VARCHAR", size: "30", group_by: "AUTO"]]])
		.lookup('LOOKUP', 'DISTINCT_', [join_condition: "AGGREGATE.ID = DISTINCT_.COL3_1"])
		.aggregate('AGGREGATE1', [attrs: [
				[ attr: "ID", expression: "AGGREGATE.ID", datatype: "NUMERIC", size: "38", scale: "0", group_by: "YES"],
				[ attr: "COL4_1_1", expression: "SUM(AGGREGATE.COL4_1)", datatype: "VARCHAR", size: "30", group_by: "AUTO"]]])
		.filter('FILTER3', [filter_condition: "AGGREGATE1.COL4_1_1 > 42" ])
		.connect('TABLE42', [
				[ attr: "ID", expression: "AGGREGATE1.ID" ],
		])
	.select('AGGREGATE1')
		.join('JOIN6', ['TABLE8'], [join_condition: "AGGREGATE1.ID = TABLE8.ID" ])
		.connect('TABLE11', [
				[ attr: "ID", expression: "TABLE8.ID" ],
				[ attr: "COL1", expression: "" ],
				[ attr: "COL2", expression: "" ],
				[ attr: "COL3", expression: "TABLE8.COL3" ],
				[ attr: "COL4", expression: "TABLE8.COL4" ],
		])
	.commit()
	.validate()

----------------------	
  
  
  My first thought was very procedural: lets do it do old fashioned way and create a class which will traverse the folder tree of a project and build a list of all the mappings. The class I built looks like this:
?
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
36
37
38
39
40
41
42
43
44
45
46
47
48
49
50
51
52
53
import oracle.odi.domain.mapping.Mapping
import oracle.odi.domain.mapping.finder.IMappingFinder
import oracle.odi.domain.project.OdiProject
 
 
/**
 *  A class to find all Mapping objects within a project
 *  All folders are traversed recursively and the mappings are returned in a collection.
 */
class FindMappings {
    def private allMappings = null
 
    def processProject(odiInstance, projectCode) {
        def odiProjectsList = (odiInstance.getTransactionalEntityManager().getFinder(OdiProject.class).findByCode(projectCode))
 
        odiProjectsList.each { p ->
            def odiFoldersList = p.getFolders()
            odiFoldersList.each { f ->
                /* process interfaces of the current folder */
                this.listMappings(odiInstance, p.getCode(), f.getName())
                /* Process sub folders recursively */
                this.processSubFolder(odiInstance, f, p.getCode())
            }
        }
 
        return (allMappings)
    }
 
    def private listMappings(odiInstance, projectCode, folderName) {
        def mappingList = ((IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class)).findByProject(projectCode, folderName)
 
        if (allMappings == null) {
            allMappings = mappingList
        } else {
            allMappings = allMappings + mappingList
        }
    }
 
    /* given an odiInstance, folder and project code, we will parse all subfolders (recursively) and print the name of all interfaces found at all levels*/
 
    def private processSubFolder(odiInstance, Folder, projectCode) {
        def subFolderList = Folder.getSubFolders()
 
        if (subFolderList.size() != 0) {
            subFolderList.each { s ->
                /* process interfaces of the current folder */
                this.listMappings(odiInstance, projectCode, s.getName())
                /* Process sub folders recursively */
                this.processSubFolder(odiInstance, s, projectCode)
            }
        }
    }
}

--------------------------------------

//Created by DI Studio
//Created by DI Studio
//Created by DI Studio
//Created by DI Studio

//Created by DI Studio
//Created by DI Studio
//Created by DI Studio
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.model.finder.IOdiDataStoreFinder;
import oracle.odi.domain.project.finder.IOdiFolderFinder; 
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.model.OdiDataStore;
import oracle.odi.domain.model.OdiModel;
import oracle.odi.domain.model.finder.IOdiModelFinder;
import  oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.generation.support.OdiScenarioGeneratorImpl;
import oracle.odi.generation.IOdiScenarioGenerator;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.mapping.Mapping;
import oracle.odi.domain.mapping.finder.IMappingFinder;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;
import oracle.odi.domain.project.OdiProject;



  //txnDef = new DefaultTransactionDefinition()
  //tm = odiInstance.getTransactionManager()
  //tme = odiInstance.getTransactionalEntityManager()
  //txnStatus = tm.getTransaction(txnDef)
  //pf = (IOdiProjectFinder)tme.getFinder(OdiProject.class)
  //ff = (IOdiFolderFinder)tme.getFinder(OdiFolder.class)

//Variables 
//projectName = "Project Name" 
//folderName = "Folder Name" 
//sourcemodelName = "Source Name"
//targetmodelName = "Target name"  
//find project and the folder

/*
def mappingList = ((IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class)).findByProject("STOO3", "STO3")
ms=mapplingList.iterator()
while(ms.hasNext()){
println ms.getName()
}

*/

txnDef = new DefaultTransactionDefinition()
tm = odiInstance.getTransactionManager()
tme = odiInstance.getTransactionalEntityManager()
txnStatus = tm.getTransaction(txnDef)
def fm = ((IMappingFinder) tme.getFinder(Mapping.class))         // shorcut to Find Mapping
def mappingList = fm.findAll().findAll{w -> w.getProject().getCode() == 'CUSTDBN'}
//def mappingList = ((IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class)).findByProject("SOCAIRO", "STO")
if (mappingList == null){
  println "Map is null"
}
ms=mappingList.iterator()
while(ms.hasNext()){
              ms_i = ms.next()
              println ms_i.getName()
              scenName = ms_i.getName();

              //IIOdiScenarioGenerator gene = new OdiScenarioGeneratorImpl((odiInstance)
              //OdiScenario newScen = gene.generateScenario(ms_i, scenName,newVersion)}
              //odiInstance.getTransactionalEntityManager().persist(ms_i);
              //OdiScenarioGenerator gene = new OdiScenarioGeneratorImpl((odiInstance)
            //gene.generateScenario(ms_i, scenName,"001")

            stxnDef = new DefaultTransactionDefinition()
            stm = odiInstance.getTransactionManager()
            stme = odiInstance.getTransactionalEntityManager()
            stxnStatus = stm.getTransaction(stxnDef)

            OdiScenario sc = ((IOdiScenarioFinder)     stme.getFinder(OdiScenario.class)).findLatestByName(scenName)
            if (sc != null){
              println "Scenario already exist"
              println sc
            }
            IOdiScenarioGenerator gene = new OdiScenarioGeneratorImpl(odiInstance);
            OdiScenario newScen = gene.generateScenario(ms_i, scenName, "001")
            println newScen 
            //tme.persist(newScen)
            stm.commit(stxnStatus)
            println "Created"
            //odiInstance.close()

            }
tm.commit(txnStatus)

//println ((IMappingFinder) odiInstance.getTransactionalEntityManager().getFinder(Mapping.class)).findByProject("STOO3", "STO3")

              //ms_i.validate()
              //ms_i.submit()
			  
			  ----------------------------
			  
			  I am very new to groovy/java programming so please bear with me. trying to execute groovy script
package com.LoadPlan.OdiTool;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.runtime.agent.invocation.RemoteRuntimeAgentInvoker;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan.*;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStep;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStepContainer;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanStepSerial;
import oracle.odi.domain.runtime.loadplan.finder.IOdiLoadPlanFinder;
import oracle.odi.domain.runtime.loadplan.OdiLoadPlanSchedule;
import oracle.odi.runtime.agent.invocation.*;
import oracle.odi.runtime.agent.invocation.StartupParams.*;
import oracle.odi.runtime.agent.invocation.LoadPlanStartupParams;
import java.util.HashMap;
import java.util.Map;

public class CustomLoadPlan {
public static void main(String[] args) {
String Master_User=""Master_User"";
String Master_Pass=""Master_Pass"";
String WorkRep=""WORKREP"";
String Odi_User=""myuser"";
String Odi_Pass=""mypass"";
MasterRepositoryDbInfo masterInfo = new MasterRepositoryDbInfo(Url, Driver, Master_User,Master_Pass.toCharArray(), new PoolingAttributes());
WorkRepositoryDbInfo workInfo = new WorkRepositoryDbInfo(WorkRep, new PoolingAttributes());
OdiInstance odiInstance=OdiInstance.createInstance(new OdiInstanceConfig(masterInfo,workInfo));
Authentication auth = odiInstance.getSecurityManager().createAuthentication(Odi_User,Odi_Pass.toCharArray());
odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
ITransactionStatus trans = odiInstance.getTransactionManager().getTransaction(new DefaultTransactionDefinition());
RemoteRuntimeAgentInvoker agent = new RemoteRuntimeAgentInvoker(""http://localhost"", ""myuser"", ""mypassword"".toCharArray());
Map paramsValues = new HashMap();
Properties lpProps = new Properties();
LoadPlanStartupParams startupParams = new LoadPlanStartupParams(paramsValues);
//LoadPlan Search
OdiLoadPlan odiLoadPlan= ((IOdiLoadPlanFinder) odiInstance.getTransactionalEntityManager().getFinder(OdiLoadPlan.class)).findByName(planName);
//Load Plan Execution
ExecutionInfo res= agent.invokeStartLoadPlan(planName,""GLOBAL"",startupParams,'',""WORKREP"",lpProps,1);
}
And getting following error
org.codehaus.groovy.runtime.InvokerInvocationException: oracle.odi.runtime.agent.invocation.InvocationException: http://host.com:port/OracleDIAgent:org.apache.commons.httpclient.ProtocolException: The server host.com failed to respond with a valid HTTP response
at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:95)
at groovy.lang.MetaMethod.doMethodInvoke(MetaMethod.java:233)
at groovy.lang.MetaClassImpl.invokeStaticMethod(MetaClassImpl.java:1302)
at org.codehaus.groovy.runtime.InvokerHelper.invokeMethod(InvokerHelper.java:759)
at groovy.lang.GroovyShell.runScriptOrMainOrTestOrRunnable(GroovyShell.java:271)
at groovy.lang.GroovyShell.run(GroovyShell.java:513)
at groovy.lang.GroovyShell.run(GroovyShell.java:170)
at oracle.odi.ui.groovy.GroovyScriptRunInstance.run(GroovyScriptRunInstance.java:228)
Caused by: oracle.odi.runtime.agent.invocation.InvocationException: http://host:port2/OracleDIAgent:org.apache.commons.httpclient.ProtocolException: The server host.com failed to respond with a valid HTTP response
Please note that via this script I am trying to lanuch Oracle Data Integrator Load Plan. I checked and can confirm all basic conditions are meeting i.e. ODi Agent is up and running, Load Plan does exist etc.

----------------------
  
  
  
  
  
  
  
  
  
  
  
  
  ---------------
  Linkek:
  http://www.groovy-lang.org/documentation.html