import java.util.Collection
import java.io.*
import jxl.*
import jxl.write.*

//def setExpr(comp, tgtTable, propertyName, expressionText) {
//    println "4:"
//    DatastoreComponent.findAttributeForColumn(comp,tgtTable.getColumn(propertyName)).setExpressionText(expressionText)
//    println "5:"
//}

def createMapping(){
    // Filepath - change the file path
    filepath="C:/Users/botieno/Desktop/groovy_config/iter6.xls"

    // ds_src_comp.connectTo(ds_tgt_comp)
    try {
        Workbook workbook = Workbook.getWorkbook(new File(filepath))
        String [] sheetNames = workbook.getSheetNames()
        Sheet sheet

        // Get all sheets and loop in them
        try {

            // source_variables
            source_model_1 = null
            source_model_2 = null
            source_model_3 = null
            source_model_4 = null
            source_model_5 = null

            // datastore_variables
            source_ds_1 = null
            source_ds_2 = null
            source_ds_3 = null
            source_ds_4 = null
            source_ds_5 = null


            for (int sheetNumber =0; sheetNumber<sheetNames.length; sheetNumber++){
                sheet = workbook.getSheet(sheetNames[sheetNumber])
                int rows = sheet.getRows()
                print "Processing "+sheet.getName()+"…..\n"

                project_name = sheet.getCell(0,0).getContents()
                project_folder_name = sheet.getCell(1,0).getContents()
                mapping_name = sheet.getCell(2,0).getContents()


                initial_cell = 1

                // Array to hold the values of the source_model and source_ds
                List sourceDatastore = []

                // loop over the sources to capture the model and data store details
                while(sheet.getCell(0, initial_cell).getContents() == 'source'){
                    sourceDatastore.add([sheet.getCell(1, initial_cell).getContents(),
                                           sheet.getCell(2, initial_cell).getContents()])
                    initial_cell++
                }

                println(sourceDatastore[0][1])

                switch(sourceDatastore.size()){
                    case 1:
                        source_model_1 = sourceDatastore[0][0]
                        source_ds_1 = sourceDatastore[0][1]
                        break
                    case 2:
                        source_model_1 = sourceDatastore[0][0]
                        source_ds_1 = sourceDatastore[0][1]
                        source_model_2 = sourceDatastore[1][0]
                        source_ds_2 = sourceDatastore[1][0]
                        break
                    case 3:
                        source_model_1 = sourceDatastore[0][0]
                        source_ds_1 = sourceDatastore[0][1]
                        source_model_2 = sourceDatastore[1][0]
                        source_ds_2 = sourceDatastore[1][1]
                        source_model_3 = sourceDatastore[2][0]
                        source_ds_3 = sourceDatastore[2][1]
                        break
                    case 4:
                        source_model_1 = sourceDatastore[0][0]
                        source_ds_1 = sourceDatastore[0][1]
                        source_model_2 = sourceDatastore[1][0]
                        source_ds_2 = sourceDatastore[1][1]
                        source_model_3 = sourceDatastore[2][0]
                        source_ds_3 = sourceDatastore[2][1]
                        source_model_4= sourceDatastore[3][0]
                        source_ds_4 = sourceDatastore[3][1]
                        break
                    case 5:
                        source_model_1 = sourceDatastore[0][0]
                        source_ds_1 = sourceDatastore[0][1]
                        source_model_2 = sourceDatastore[1][0]
                        source_ds_2 = sourceDatastore[1][1]
                        source_model_3 = sourceDatastore[2][0]
                        source_ds_3 = sourceDatastore[2][1]
                        source_model_4= sourceDatastore[3][0]
                        source_ds_4 = sourceDatastore[3][1]
                        source_model_5 = sourceDatastore[4][0]
                        source_ds_5 = sourceDatastore[4][1]
                        break
                    default:
                        break
                }

                target_ds=sheet.getCell(2,initial_cell).getContents()
                target_model=sheet.getCell(1,initial_cell).getContents()

                int count = 1
                Cell [] myCells = sheet.getColumn(0)
                for(Cell cells : myCells){
                    println(cells.getContents())
                    if(cells.getContents() == 'join_condition')
                        break
                    count++
                }

                println(count)


                println('project_name:'+ project_name)
                println('project_folder_name:'+ project_folder_name)
                println('source_1_model = ' + source_model_1 + " : " + "source_1_ds = " + source_ds_1)
                println('source_2_model = ' + source_model_2 + " : " + "source_2_ds = " + source_ds_2)
                println('source_3_model = ' + source_model_3 + " : " + "source_3_ds = " + source_ds_3)
                println('source_4_model = ' + source_model_4 + " : " + "source_4_ds = " + source_ds_4)
                println('source_5_model = ' + source_model_5 + " : " + "source_5_ds = " + source_ds_5)
                println('target_ds:'+ target_ds)
                println('target_model:'+ target_model)

            }
        }
        catch (Exception e) {
            println "Exception at final catch: "+e
        }
    }catch (Exception e) {
        println "Exception at final catch: "+e
    }

} //end of create mapping

createMapping()
