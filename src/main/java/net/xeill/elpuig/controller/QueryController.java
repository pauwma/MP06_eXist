package net.xeill.elpuig.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class QueryController {

    ExistController controller;

    public QueryController(ExistController controller) {
        this.controller = controller;
    }

    /*
    public List<String> getWeatherPredictionsByDate(String date) throws ParserConfigurationException {
        List<String> predictions = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(controller.getXmlInputStream());

            NodeList nodeList = document.getElementsByTagName("prediction");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String predictionDate = element.getAttribute("date");

                    if (predictionDate.equals(date)) {
                        String city = element.getElementsByTagName("city").item(0).getTextContent();
                        String minTemp = element.getElementsByTagName("minTemp").item(0).getTextContent();
                        String maxTemp = element.getElementsByTagName("maxTemp").item(0).getTextContent();
                        String windSpeed = element.getElementsByTagName("windSpeed").item(0).getTextContent();
                        String precipitationProbability = element.getElementsByTagName("precipitationProbability").item(0).getTextContent();

                        String formattedPrediction = String.format("Ciudad: %s | Fecha: %s | Temperatura mínima: %s | Temperatura máxima: %s | Velocidad del viento: %s | Probabilidad de precipitaciones: %s",
                                city, predictionDate, minTemp, maxTemp, windSpeed, precipitationProbability);
                        predictions.add(formattedPrediction);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return predictions;
    }*/


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

                while (xqrs.next()) {
                    String result = xqrs.getItemAsString(null);
                    String data = result.substring(result.indexOf("data=\"") + 6, result.indexOf("\"", result.indexOf("data=\"") + 6));
                    String dia = result.substring(result.indexOf("dia=\"") + 5, result.indexOf("\"", result.indexOf("dia=\"") + 5));
                    String tempmin = result.substring(result.indexOf("tempmin=\"") + 9, result.indexOf("\"", result.indexOf("tempmin=\"") + 9));
                    String tempmax = result.substring(result.indexOf("tempmax=\"") + 9, result.indexOf("\"", result.indexOf("tempmax=\"") + 9));
                    String simbolmati = result.substring(result.indexOf("simbolmati=\"") + 12, result.indexOf("\"", result.indexOf("simbolmati=\"") + 12));
                    String simboltarda = result.substring(result.indexOf("simboltarda=\"") + 13, result.indexOf("\"", result.indexOf("simboltarda=\"") + 13));
                    String intensitatprecimati = result.substring(result.indexOf("intensitatprecimati=\"") + 22, result.indexOf("\"", result.indexOf("intensitatprecimati=\"") + 22));
                    String intensitatprecitarda = result.substring(result.indexOf("intensitatprecitarda=\"") + 23, result.indexOf("\"", result.indexOf("intensitatprecitarda=\"") + 23));
                    String precipitacioacumuladamati = result.substring(result.indexOf("precipitacioacumuladamati=\"") + 28, result.indexOf("\"", result.indexOf("precipitacioacumuladamati=\"") + 28));
                    String precipitacioacumuladatarda = result.substring(result.indexOf("precipitacioacumuladatarda=\"") + 29, result.indexOf("\"", result.indexOf("precipitacioacumuladatarda=\"") + 29));
                    String probprecipitaciomati = result.substring(result.indexOf("probprecipitaciomati=\"") + 22, result.indexOf("\"", result.indexOf("probprecipitaciomati=\"") + 22));
                    String probprecipitaciotarda = result.substring(result.indexOf("probprecipitaciotarda=\"") + 23, result.indexOf("\"", result.indexOf("probprecipitaciotarda=\"") + 23));
                    String probcalamati = result.substring(result.indexOf("probcalamati=\"") + 14, result.indexOf("\"", result.indexOf("probcalamati=\"") + 14));
                    String probcalatarda = result.substring(result.indexOf("probcalatarda=\"") + 15, result.indexOf("\"", result.indexOf("probcalatarda=\"") + 15));
                    System.out.printf("Fecha: %s, Día: %s, Temperatura mínima: %s, Temperatura máxima: %s, Símbolo mañana: %s, Símbolo tarde: %s, Intensidad precipitación mañana: %s, Intensidad precipitación tarde: %s, Acumulación precipitación mañana: %s, Acumulación precipitación tarde: %s, Probabilidad de precipitación mañana: %s, Probabilidad de precipitación tarde: %s, Probabilidad de calima mañana: %s%n",
                            data, dia, tempmin, tempmax, simbolmati, simboltarda, intensitatprecimati, intensitatprecitarda,
                            precipitacioacumuladamati, precipitacioacumuladatarda, probprecipitaciomati, probprecipitaciotarda,
                            probcalamati);
                }
            } else {
                System.out.println("Selección inválida. Por favor, intenta de nuevo.");
            }
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }
}
