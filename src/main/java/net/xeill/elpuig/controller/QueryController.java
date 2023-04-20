package net.xeill.elpuig.controller;

import javax.xml.transform.OutputKeys;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQResultSequence;
import java.io.StringWriter;
import java.util.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class QueryController {

    ExistController controller;

    public QueryController(ExistController controller) {
        this.controller = controller;
    }

    public void showComarcas(){
        String query = "for $comarca in /smc/comarca return $comarca";
        XQResultSequence xqrs = controller.executeQuery(query);

        System.out.println("Listado de comarcas:");
        try {
            while (xqrs.next()) {
                Node node = xqrs.getNode();
                String id = node.getAttributes().getNamedItem("id").getNodeValue();
                String nomComarca = node.getAttributes().getNamedItem("nomCOMARCA").getNodeValue();
                String nomCapital = node.getAttributes().getNamedItem("nomCAPITALCO").getNodeValue();

                System.out.printf("ID: %s | Comarca: %s | Capital: %s%n", id, nomComarca, nomCapital);
            }
        } catch (Exception e){}
    }

    public void showSimbols() {
        String query = "for $simbol in /smc/simbol return $simbol";
        XQResultSequence xqrs = controller.executeQuery(query);

        Map<String, String> symbolEmojis = getSymbolEmojis();

        System.out.println("Listado de símbolos y emojis:");
        try {
            while (xqrs.next()) {
                Node node = xqrs.getNode();
                String id = node.getAttributes().getNamedItem("id").getNodeValue();
                String nomSimbol = node.getAttributes().getNamedItem("nomsimbol").getNodeValue();
                String emoji = symbolEmojis.get(id);

                System.out.printf("ID: %s | %s - %s%n", id, nomSimbol, emoji);
            }
        } catch (Exception e){}
    }

    private static Map<String, String> getSymbolEmojis() {
        Map<String, String> symbolEmojis = new HashMap<>();
        symbolEmojis.put("1", "☀️");
        symbolEmojis.put("2", "🌤️");
        symbolEmojis.put("3", "⛅");
        symbolEmojis.put("4", "🌥️");
        symbolEmojis.put("5", "🌦️");
        symbolEmojis.put("6", "🌧️");
        symbolEmojis.put("7", "🌦️");
        symbolEmojis.put("8", "⛈️");
        symbolEmojis.put("9", "⛈️🌧️");
        symbolEmojis.put("10", "🌨️");
        symbolEmojis.put("11", "🌫️");
        symbolEmojis.put("12", "🌁");
        symbolEmojis.put("13", "🌨️🌧️");
        symbolEmojis.put("20", "⛅☁️");
        symbolEmojis.put("21", "☁️");
        symbolEmojis.put("22", "🌫️");
        symbolEmojis.put("23", "🌦️");
        symbolEmojis.put("24", "⛈️");
        symbolEmojis.put("25", "🌦️");
        symbolEmojis.put("26", "🌧️");
        symbolEmojis.put("27", "❄️");
        symbolEmojis.put("28", "⛈️❄️");
        symbolEmojis.put("29", "🌦️");
        symbolEmojis.put("30", "🌧️❄️");
        symbolEmojis.put("31", "🌧️");
        symbolEmojis.put("32", "🌦️");

        return symbolEmojis;
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

                String query = "for $prediccio in /smc/prediccio[variable/@data = '" + selectedDate + "'] return $prediccio";
                XQResultSequence xqrs = controller.executeQuery(query);
                System.out.println("Predicciones del tiempo para " + selectedDate + ":");

                // Consulta para obtener el mapeo entre el ID y el nombre de la comarca
                String comarcaQuery = "for $comarca in /smc/comarca return $comarca";
                XQResultSequence comarcaResultSet = controller.executeQuery(comarcaQuery);
                Map<String, String> comarcaNameById = new HashMap<>();

                while (comarcaResultSet.next()) {
                    Node comarcaNode = comarcaResultSet.getNode();
                    String id = comarcaNode.getAttributes().getNamedItem("id").getNodeValue();
                    String nomCOMARCA = comarcaNode.getAttributes().getNamedItem("nomCOMARCA").getNodeValue();
                    comarcaNameById.put(id, nomCOMARCA);
                }
                while (xqrs.next()) {
                    processNode(xqrs.getNode(), comarcaNameById, selectedDate);
                }
            } else {
                System.out.println("Selección inválida. Por favor, intenta de nuevo.");
            }
        } catch (Exception e){}
    }

    private static void processNode(Node node, Map<String, String> comarcaNameById, String selectedDate) {
        String idComarca = node.getAttributes().getNamedItem("idcomarca").getNodeValue();
        String comarcaName = comarcaNameById.getOrDefault(idComarca, "Unknown");

        String[] attributeNames = {
                "data", "tempmax", "tempmin",
                "simbolmati", "simboltarda",
                "probprecipitaciomati", "probprecipitaciotarda",
                "intensitatprecimati", "intensitatprecitarda",
                "precipitacioacumuladamati", "precipitacioacumuladatarda",
                "probcalamati", "probcalatarda"
        };

        NodeList variables = node.getChildNodes();

        for (int i = 0; i < variables.getLength(); i++) {
            Node variableNode = variables.item(i);
            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                Map<String, String> attributes = new HashMap<>();

                for (String attributeName : attributeNames) {
                    attributes.put(attributeName, variableNode.getAttributes().getNamedItem(attributeName).getNodeValue());
                }

                if (!attributes.get("data").equals(selectedDate)){
                    return;
                }

                System.out.printf("%s - %s%n", comarcaName, attributes.get("data"));
                System.out.printf("Max: %s | Min: %s%n", attributes.get("tempmax"), attributes.get("tempmin"));
                System.out.printf("Matí - %s%nPrecipitacions: %s%nIntensitat: %s%nAcumulada: %s%nCalamarsa: %s%n%n", attributes.get("simbolmati"), attributes.get("probprecipitaciomati"), attributes.get("intensitatprecimati"), attributes.get("precipitacioacumuladamati"), attributes.get("probcalamati"));
                System.out.printf("Tarda - %s%nPrecipitacions: %s%nIntensitat: %s%nAcumulada: %s%nCalamarsa: %s%n------------%n", attributes.get("simboltarda"), attributes.get("probprecipitaciotarda"), attributes.get("intensitatprecitarda"), attributes.get("precipitacioacumuladatarda"), attributes.get("probcalatarda"));
            }
        }
    }

}