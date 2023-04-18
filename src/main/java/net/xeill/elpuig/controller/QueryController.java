package net.xeill.elpuig.controller;

import javax.xml.transform.OutputKeys;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public class QueryController {

    ExistController controller;

    public QueryController(ExistController controller) {
        this.controller = controller;
    }

    public void showPredictionsForSpecificDate() {
        try {
            // Obtener las fechas disponibles
            XQResultSequence dateXqrs = controller.executeQuery("distinct-values(/smc/prediccio/variable/@data)");

            int counter = 1;
            List<String> availableDates = new ArrayList<>();
            while (dateXqrs.next()) {
                String date = dateXqrs.getItemAsString(null);
                System.out.println(counter + ". " + date.trim());
                availableDates.add(date.trim());
                counter++;
            }

            // Solicitar al usuario que elija una fecha
            System.out.print("Introduce el número correspondiente a la fecha que deseas consultar: ");
            Scanner scanner = new Scanner(System.in);
            int selectedIndex = scanner.nextInt() - 1;

            // Validar la selección del usuario
            if (selectedIndex >= 0 && selectedIndex < availableDates.size()) {
                String selectedDate = availableDates.get(selectedIndex);

                // Consultar las predicciones para la fecha seleccionada
                String query = "for $prediccio in /smc/prediccio/variable[@data = '" + selectedDate + "'] return $prediccio";

                XQResultSequence xqrs = controller.executeQuery(query);
                System.out.println("Predicciones del tiempo para " + selectedDate + ":");

                /// Formatear e imprimir el XML
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

                while (xqrs.next()) {
                    Node node = xqrs.getNode();
                    //String comarca = node.getAttributes().getNamedItem("idcomarca").getNodeValue();
                    String data = node.getAttributes().getNamedItem("data").getNodeValue();
                    String tempMax = node.getAttributes().getNamedItem("tempmax").getNodeValue();
                    String tempMin = node.getAttributes().getNamedItem("tempmin").getNodeValue();
                    String simbolMati = node.getAttributes().getNamedItem("simbolmati").getNodeValue();
                    String simbolTarda = node.getAttributes().getNamedItem("simboltarda").getNodeValue();
                    String probPrecipitacioMati = node.getAttributes().getNamedItem("probprecipitaciomati").getNodeValue();
                    String probPrecipitacioTarda = node.getAttributes().getNamedItem("probprecipitaciotarda").getNodeValue();
                    String intensitatPreciMati = node.getAttributes().getNamedItem("intensitatprecimati").getNodeValue();
                    String intensitatPreciTarda = node.getAttributes().getNamedItem("intensitatprecitarda").getNodeValue();
                    String precipitacioAcumuladaMati = node.getAttributes().getNamedItem("precipitacioacumuladamati").getNodeValue();
                    String precipitacioAcumuladaTarda = node.getAttributes().getNamedItem("precipitacioacumuladatarda").getNodeValue();
                    String probCalaMati = node.getAttributes().getNamedItem("probcalamati").getNodeValue();
                    String probCalaTarda = node.getAttributes().getNamedItem("probcalatarda").getNodeValue();

                    System.out.println(data);
                    System.out.println("Max: " + tempMax + " | Min: " + tempMin);
                    System.out.println("Matí - " + simbolMati);
                    System.out.println("Precipitacions: " + probPrecipitacioMati);
                    System.out.println("Intensitat: " + intensitatPreciMati);
                    System.out.println("Acumulada: " + precipitacioAcumuladaMati);
                    System.out.println("Calamarsa: " + probCalaMati);
                    System.out.println("");
                    System.out.println("Tarda - " + simbolTarda);
                    System.out.println("Precipitacions: " + probPrecipitacioTarda);
                    System.out.println("Intensitat: " + intensitatPreciTarda);
                    System.out.println("Acumulada: " + precipitacioAcumuladaTarda);
                    System.out.println("Calamarsa: " + probCalaTarda);
                    System.out.println("------------");
                }

            } else {
                System.out.println("Selección inválida. Por favor, intenta de nuevo.");
            }
        } catch (XQException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
