package net.xeill.elpuig.controller;




import net.xeill.elpuig.Menu;

import javax.xml.xquery.XQResultSequence;
import java.util.*;

public class UpdateController {

    ExistController controller;
    QueryController queryController;

    public UpdateController(ExistController controller, QueryController queryController) {
        this.controller = controller;
        this.queryController = queryController;
    }

    public void modifyComarcas(){
        queryController.showComarcas();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el ID de la comarca que desea editar: ");
        int comarcaId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Ingrese el nuevo nombre de la comarca: ");
        String newNomCOMARCA = scanner.nextLine();

        System.out.println("Ingrese el nuevo nombre de la capital de la comarca: ");
        String newNomCAPITALCO = scanner.nextLine();

        updateComarcaInfo(comarcaId, newNomCOMARCA, newNomCAPITALCO);
    }

    public void updateComarcaInfo(int comarcaId, String newNomCOMARCA, String newNomCAPITALCO) {
        String query = "for $comarca in /smc/comarca where $comarca/@id = " + comarcaId + " return $comarca";
        XQResultSequence xqrs = controller.executeQuery(query);
        try {
            if (xqrs.next()) {
                // Update the comarca node in the eXist-db
                String updateQuery1 = "update value /smc/comarca[@id=" + comarcaId + "]/@nomCOMARCA with '" + newNomCOMARCA + "'";
                String updateQuery2 = "update value /smc/comarca[@id=" + comarcaId + "]/@nomCAPITALCO with '" + newNomCAPITALCO + "'";

                controller.executeNonReturningQuery(updateQuery1);
                controller.executeNonReturningQuery(updateQuery2);

                System.out.println("Información actualizada de la comarca ID: " + comarcaId);
            } else {
                System.out.println("No se encontró la comarca con ID: " + comarcaId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPredictionsForComarca(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el ID de la comarca que deseas listar: ");
        int comarcaId = scanner.nextInt();

        // Obtener las fechas disponibles
        Map<String, String> precipitacionMap = queryController.createCategoryMap("precipitacio", "nomprobprecipitaciomati");
        Map<String, String> intensitatMap = queryController.createCategoryMap("intensitat", "nomintensitatprecimati");
        Map<String, String> acumuladaMap = queryController.createCategoryMap("acumulacio", "nomprecipitacioacumuladamati");
        Map<String, String> calamarsaMap = queryController.createCategoryMap("calamarsa", "nomprobcalamati");

        // Validar la selección del usuario

        String query = "for $variable in /smc/prediccio[@idcomarca = '" + comarcaId + "'] return $variable";

        XQResultSequence xqrs = controller.executeQuery(query);

        String nomComarca = queryController.getNomComarcaById(comarcaId);

        try {
            while (xqrs.next()){
                queryController.processNodeComarca(xqrs.getNode(), nomComarca, precipitacionMap, intensitatMap, acumuladaMap, calamarsaMap);
            }
        } catch (Exception e){}

        String fecha = "";
        switch (Menu.scannerInt("Elige que predicción deseas editar: ",0, 4)){
            case 1: fecha = "29-03-2023";
            case 2: fecha = "30-03-2023";
            case 3: fecha = "19-04-2023";
            case 4: fecha = "20-04-2023";

        }

        int nuevaTempMax = Menu.scannerInt("Introduce la nueva máxima: ",-40, 50);
        int nuevaTempMin = Menu.scannerInt("Introduce la nueva máxima: ",-40, 50);

        String updateQueryMax = "for $prediccio in /smc/prediccio[@idcomarca=" + comarcaId + "] where $prediccio/data = '" + fecha + "' return update value $prediccio/tempmax with " + nuevaTempMax;
        String updateQueryMin = "for $prediccio in /smc/prediccio[@idcomarca=" + comarcaId + "] where $prediccio/data = '" + fecha + "' return update value $prediccio/tempmin with " + nuevaTempMin;

        try {
            controller.executeNonReturningQuery(updateQueryMax);
            controller.executeNonReturningQuery(updateQueryMin);
        } catch (Exception e){
            System.out.println("ERROR - No se ha podido editar.");
        }
    }

}