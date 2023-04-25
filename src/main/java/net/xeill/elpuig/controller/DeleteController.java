package net.xeill.elpuig.controller;


import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQResultSequence;
import java.util.Scanner;

public class DeleteController {

    ExistController controller;
    QueryController queryController;

    public DeleteController(ExistController controller, QueryController queryController) {
        this.controller = controller;
        this.queryController = queryController;
    }

    public void deleteComarcas(){
        queryController.showComarcas();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ingrese el ID de la comarca que desea eliminar: ");
        int comarcaId = scanner.nextInt();
        scanner.nextLine();

        try {
            XQExpression xqe = controller.getConnection().createExpression();
            String deleteQuery = "update delete \n" + "doc('/db/apps/foaf/foaf/temps.xml')/smc/comarca[@id='" + comarcaId + "']";
            try {
                xqe.executeCommand(deleteQuery);
                System.out.println("Información eliminada de la comarca ID: " + comarcaId);
            } catch (Exception e){
                System.out.println("ERROR - No se ha podido eliminar la información de la comarca ID: " + comarcaId);
                e.printStackTrace();
            }
        } catch (XQException e) {}

    }


}