package net.xeill.elpuig;

import net.xeill.elpuig.controller.ExistController;

import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

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

        ExistController ec = new ExistController();
        XQResultSequence xqrs = ec.executeQuery("for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/book where $book/author='Stephen King' return $book/title");
        ec.printResultSequence(xqrs);
        xqrs = ec.executeQuery("for $book in doc('/db/apps/foaf/books.xml')/library/book where $book/year < 1960 return $book");
        ec.printResultSequence(xqrs);

    }
}

