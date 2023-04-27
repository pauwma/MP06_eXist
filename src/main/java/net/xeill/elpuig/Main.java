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
    }
}

