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
                String query = "for $prediccio in doc('/db/apps/foaf/temps.xml')/smc/prediccio/variable where $prediccio/@data = '" + selectedDate + "' return $prediccio";

                XQResultSequence xqrs = controller.executeQuery(query);
                System.out.println("Predicciones del tiempo para " + selectedDate + ":");
                controller.printResultSequence(xqrs);
            } else {
                System.out.println("Selección inválida. Por favor, intenta de nuevo.");
            }
        } catch (XQException e) {
            throw new RuntimeException(e);
        }
    }
}
