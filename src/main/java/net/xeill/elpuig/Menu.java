package net.xeill.elpuig;

import net.xeill.elpuig.controller.DeleteController;
import net.xeill.elpuig.controller.ExistController;
import net.xeill.elpuig.controller.QueryController;
import net.xeill.elpuig.controller.UpdateController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private int option;
    private boolean close;
    public ExistController controller;

    private QueryController queryController;
    private UpdateController updateController;
    private DeleteController deleteController;

    public Menu() {
        super();
        this.controller = new ExistController();
        queryController = new QueryController(controller);
        updateController = new UpdateController(controller, queryController);
        deleteController = new DeleteController(controller, queryController);
    }

    /**
     * Muestra el menú principal de la aplicación.
     *
     * @return La opción elegida por el usuario.
     */
    public int mainMenu() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean validOption = false;
        do {
            System.out.println("\n┏━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
            System.out.println("┃       MENU PRINCIPAL      ┃");
            System.out.println("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
            System.out.println("┃  1 -     Selects     - 1  ┃");
            System.out.println("┃  2 -     Updates     - 2  ┃");
            System.out.println("┃  3 -     Deletes     - 3  ┃");
            System.out.println("┃  0 -      Salir      - 0  ┃");
            System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
            try {
                option = scannerInt("Elige una opción: ",0,3);
                validOption = true;
            } catch (Exception e) {
                System.out.println("ERROR - Opción no válida, por favor introduce un número entero");
            }

        } while (!validOption);
        return option;
    }


    /**
     * Muestra el menú del control de las funciones de selects y permite elegir una opción.
     *
     * @return La opción elegida por el usuario.
     */
    public int menuQuerys() throws SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int option = 0;
        boolean cerrarSubmenu = false;
        while (!cerrarSubmenu) {
            System.out.println("\n┌───────────────────────────┐");
            System.out.println("│        MENU SELECTS       │");
            System.out.println("├───────────────────────────┤");
            System.out.println("│  1 -      Tiempo     - 1  │");
            System.out.println("│  2 -     Comarcas    - 2  │");
            System.out.println("│  3 -      Leyenda    - 3  │");
            System.out.println("│  0 -      Cerrar     - 0  │");
            System.out.println("└───────────────────────────┘");
            try {
                option = scannerInt("Elige una opción: ",0,3);
                if (option >= 0 && option <= 3) {
                    switch (option){
                        case 1:
                            Scanner scanner = new Scanner(System.in);
                            switch (scannerInt("Quieres listar por 1-Comarca / 2-Fecha? ",1,2)){
                                case 1:
                                    queryController.showPredictionsForComarca();
                                    break;
                                case 2:
                                    queryController.showPredictionsForSpecificDate();
                                    break;
                            }
                            break;
                        case 2:
                            queryController.showComarcas();
                            break;
                        case 3:
                             queryController.showSimbols();
                            break;
                        case 0:
                            cerrarSubmenu = true;
                            break;
                    }
                } else {
                    System.out.println("Opción inválida, por favor ingrese una opción válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error en el formato, por favor ingrese una opción válida.");
            }
        }
        return option;
    }
    /**
     * Muestra el menú del control de las funciones de updates y permite elegir una opción.
     *
     * @return La opción elegida por el usuario.
     */
    public int menuUpdates() throws SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int option = 0;
        boolean cerrarSubmenu = false;
        while (!cerrarSubmenu) {

            System.out.println("\n┌───────────────────────────┐");
            System.out.println("│       MENU  UPDATES       │");
            System.out.println("├───────────────────────────┤");
            System.out.println("│  1 -     Comarcas    - 1  │");
            System.out.println("│  2 -      Tiempo     - 2  │");
            System.out.println("│  0 -      Cerrar     - 0  │");
            System.out.println("└───────────────────────────┘");
            try {
                option = scannerInt("Elige una opción: ",0,2);
                if (option >= 0 && option <= 2) {
                    switch (option){
                        case 1:
                            updateController.modifyComarcas();
                            break;
                        case 2:
                            updateController.showPredictionsForComarca();
                            break;
                        case 0:
                            cerrarSubmenu = true;
                            break;
                    }
                } else {
                    System.out.println("Opción inválida, por favor ingrese una opción válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error en el formato, por favor ingrese una opción válida.");
            }
        }
        return option;
    }

    /**
     * Muestra el menú del control de las funciones de deletes y permite elegir una opción.
     *
     * @return La opción elegida por el usuario.
     */
    public int menuDeletes() throws SQLException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int option = 0;
        boolean cerrarSubmenu = false;
        while (!cerrarSubmenu) {

            System.out.println("\n┌───────────────────────────┐");
            System.out.println("│       MENU  DELETES       │");
            System.out.println("├───────────────────────────┤");
            System.out.println("│  1 -     Comarcas    - 1  │");
            System.out.println("│  2 -   Condiciones   - 2  │");
            System.out.println("│  0 -      Cerrar     - 0  │");
            System.out.println("└───────────────────────────┘");
            try {
                option = scannerInt("Elige una opción: ",0,2);
                if (option >= 0 && option <= 2) {
                    switch (option){
                        case 1:
                            deleteController.deleteComarcas();
                            break;
                        case 2:
                            break;
                        case 0:
                            cerrarSubmenu = true;
                            break;
                    }
                } else {
                    System.out.println("Opción inválida, por favor ingrese una opción válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error en el formato, por favor ingrese una opción válida.");
            }
        }
        return option;
    }

    /**
     * Función para cerrar el menú principal
     */
    public void close(){
        close = true;
    }

    /**
     * Método para preguntar al usuario por un Integer con excepciones.
     * @param pregunta Pregunta para mostrar por pantalla.
     * @param min Número mínimo.
     * @param max Número máximo.
     */
    public static int scannerInt(String pregunta, int min, int max) {
        Scanner sc = new Scanner(System.in);
        int userInput = 0;
        boolean isValid = false;
        while (!isValid) {
            try {
                System.out.print(pregunta);
                userInput = sc.nextInt();
                if (userInput >= min && userInput <= max) {
                    isValid = true;
                } else {
                    System.out.println("El número debe estar entre " + min + " y " + max + ".");
                }
            } catch (Exception e) {
                System.out.println("Ocurrió un error al leer la entrada. Por favor, inténtalo de nuevo con un número entero.");
                sc.next();
            }
        }
        return userInput;
    }
}