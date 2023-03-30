package net.xeill.elpuig;

import net.xeill.elpuig.controller.ExistController;

import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws SQLException, XQException {

        Menu menu = new Menu();
        int option;
        option = menu.mainMenu();

        while (option > 0 && option <= 3) {
            switch (option) {
                case 1:
                    menu.menuQuerys();
                    break;
                case 2:
                    menu.menuUpdates();
                    break;
                case 3:
                    menu.menuDeletes();
                    break;
                case 0:
                    menu.close();
                    break;
            }
            option = menu.mainMenu();
        }


        XQResultSequence xqrs = menu.controller.executeQuery("for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/prediccio where $prediccio/@data = '29-03-2023' return $prediccio");
        /*menu.controller.printResultSequence(xqrs);
        xqrs = menu.controller.executeQuery("for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/prediccio where $prediccio/@idcomarca = '3' return $prediccio");
        menu.controller.printResultSequence(xqrs);
        xqrs = menu.controller.executeQuery("for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/prediccio/variable where $prediccio/../@idcomarca = '3' return $prediccio");
        menu.controller.printResultSequence(xqrs);

        consulta de predicciones por fecha
        xqrs = menu.controller.executeQuery("for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/prediccio/variable where $prediccio/@data = '29-03-2023' return $prediccio");
        menu.controller.printResultSequence(xqrs);
        */

        xqrs = menu.controller.executeQuery("distinct-values(/smc/prediccio/variable/@data)");

        // Print results
        List<String> dates = new ArrayList<>();
        while (xqrs.next()) {
            dates.add(xqrs.getItemAsString(null));
        }
        System.out.println("Available dates: " + dates);


    }
}

