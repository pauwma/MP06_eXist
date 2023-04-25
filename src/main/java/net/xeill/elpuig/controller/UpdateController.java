package net.xeill.elpuig.controller;




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


}