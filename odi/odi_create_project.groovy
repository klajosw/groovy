/*
//Created by klajosw@gmail.com  // https://klajosw.blogspot.com/
HU:   ODI12c project létrehozása ODIstudio-ban groovy script editort elérése a menűből Tools>Groovy>New Script
ENG : Create Project ODI12c Groovy Script in ODIStudio menu Tools>Groovy>New Script
// ----
*/

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

//CREATE PROJECT START -----

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
  	println "HU: Project már létezik. Project létrehozás átlépése"
    println "ENG:Project Already Exists. Project Creation Skipped"

  } else {
    project = new OdiProject(project_name, project_name)
    tme.persist(project)
    folder = new OdiFolder(project, project_folder_name)
    tme.persist(folder)
    tm.commit(txnStatus)
	println "HU: Project létrehozás sikeres  "
    println "ENG:Project Created Successfully"
  }
}

//CREATE PROJECT END -----

//CALL PROJECT START -----

createProject("ODI_EDW","First Folder")  // HU : Alapértelmezett könyvtár neve : First Folder //ENG : defaulf folder name : First Folder

//CALL PROJECT END -----
