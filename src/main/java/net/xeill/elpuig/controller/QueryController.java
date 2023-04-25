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

                System.out.printf("ID: %s | %s | Capital: %s%n", id, nomComarca, nomCapital);
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

    public void showPredictionsForComarca(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el ID de la comarca que deseas listar: ");
        int comarcaId = scanner.nextInt();

        // Obtener las fechas disponibles
        Map<String, String> precipitacionMap = createCategoryMap("precipitacio", "nomprobprecipitaciomati");
        Map<String, String> intensitatMap = createCategoryMap("intensitat", "nomintensitatprecimati");
        Map<String, String> acumuladaMap = createCategoryMap("acumulacio", "nomprecipitacioacumuladamati");
        Map<String, String> calamarsaMap = createCategoryMap("calamarsa", "nomprobcalamati");

        // Validar la selección del usuario

        String query = "for $variable in /smc/prediccio[@idcomarca = '" + comarcaId + "'] return $variable";

        XQResultSequence xqrs = controller.executeQuery(query);

        String nomComarca = "";
        String query2 = "for $comarca in /smc/comarca where $comarca/@id = " + comarcaId + " return $comarca";
        XQResultSequence xqrs2 = controller.executeQuery(query);
        try {
            while (xqrs2.next()) {
                Node node = xqrs2.getNode();
                 nomComarca = node.getAttributes().getNamedItem("nomCOMARCA").getNodeValue();
            }
        } catch (Exception e){}



        try {
            while (xqrs.next()){
                processNodeComarca(xqrs.getNode(), nomComarca, precipitacionMap, intensitatMap, acumuladaMap, calamarsaMap);
            }
        } catch (Exception e){

        }

    }

    private static void processNodeComarca(Node node, String comarcaName, Map<String, String> precipitacionMap, Map<String, String> intensitatMap, Map<String, String> acumuladaMap, Map<String, String> calamarsaMap) {
        String idComarca = node.getAttributes().getNamedItem("idcomarca").getNodeValue();

        String[] attributeNames = {
                "data", "tempmax", "tempmin",
                "simbolmati", "simboltarda",
                "probprecipitaciomati", "probprecipitaciotarda",
                "intensitatprecimati", "intensitatprecitarda",
                "precipitacioacumuladamati", "precipitacioacumuladatarda",
                "probcalamati", "probcalatarda"
        };

        NodeList variables = node.getChildNodes();
        Map<String, String> symbolEmojis = getSymbolEmojis();

        for (int i = 0; i < variables.getLength(); i++) {
            Node variableNode = variables.item(i);
            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                Map<String, String> attributes = new HashMap<>();

                for (String attributeName : attributeNames) {
                    Node attributeNode = variableNode.getAttributes().getNamedItem(attributeName);
                    String attributeValue = attributeNode != null ? attributeNode.getNodeValue() : null;
                    attributes.put(attributeName, checkNull(attributeValue));
                }


                String simbolMatiKey = attributes.get("simbolmati").replace(".png", "");
                String simbolTardaKey = attributes.get("simboltarda").replace(".png", "");

                String simbolMatiEmoji = symbolEmojis.getOrDefault(simbolMatiKey, " - ");
                String simbolTardaEmoji = symbolEmojis.getOrDefault(simbolTardaKey, " - ");


                String precipitacionsMatiDescription = precipitacionMap.getOrDefault(attributes.get("probprecipitaciomati"), " - ");
                String intensitatMatiDescription = intensitatMap.getOrDefault(attributes.get("intensitatprecimati"), " - ");
                String acumuladaMatiDescription = acumuladaMap.getOrDefault(attributes.get("precipitacioacumuladamati"), " - ");
                String calamarsaMatiDescription = calamarsaMap.getOrDefault(attributes.get("probcalamati"), " - ");


                String precipitacionsTardaDescription = precipitacionMap.getOrDefault(attributes.get("probprecipitaciotarda"), " - ");
                String intensitatTardaDescription = intensitatMap.getOrDefault(attributes.get("intensitatprecitarda"), " - ");
                String acumuladaTardaDescription = acumuladaMap.getOrDefault(attributes.get("precipitacioacumuladatarda"), " - ");
                String calamarsaTardaDescription = calamarsaMap.getOrDefault(attributes.get("probcalatarda"), " - ");

                int colWidth = 35;
                int spaceBetweenCols = 3;

                String titleFormat = "│ %-28s Max: %-1sºC - Min: %-1sºC          %-12s      │";
                String lineFormat = "│ %-"+colWidth+"s %-"+spaceBetweenCols+"s %-"+colWidth+"s  │";

                System.out.printf("┌──────────────────────────────────────────────────────────────────────────────┐%n");
                System.out.printf(titleFormat + "%n", comarcaName, attributes.get("tempmax"), attributes.get("tempmin"), attributes.get("data"));
                System.out.printf("├──────────────────────────────────────────────────────────────────────────────┤%n");
                System.out.printf(lineFormat + "%n", "Matí - " + simbolMatiEmoji, "", "Tarda - " + simbolTardaEmoji);
                System.out.printf(lineFormat + "%n", "Precipitacions: " + precipitacionsMatiDescription, "", "Precipitacions: " + precipitacionsTardaDescription);
                System.out.printf(lineFormat + "%n", "Intensitat: " + intensitatMatiDescription, "", "Intensitat: " + intensitatTardaDescription);
                System.out.printf(lineFormat + "%n", "Acumulada: " + acumuladaMatiDescription, "", "Acumulada: " + acumuladaTardaDescription);
                System.out.printf(lineFormat + "%n", "Calamarsa: " + calamarsaMatiDescription, "", "Calamarsa: " + calamarsaTardaDescription);
                System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");

            }
        }
    }

    public void showPredictionsForSpecificDate() {
        try {
            // Obtener las fechas disponibles
            XQResultSequence dateXqrs = controller.executeQuery("distinct-values(/smc/prediccio/variable/@data)");
            Map<String, String> precipitacionMap = createCategoryMap("precipitacio", "nomprobprecipitaciomati");
            Map<String, String> intensitatMap = createCategoryMap("intensitat", "nomintensitatprecimati");
            Map<String, String> acumuladaMap = createCategoryMap("acumulacio", "nomprecipitacioacumuladamati");
            Map<String, String> calamarsaMap = createCategoryMap("calamarsa", "nomprobcalamati");

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
                    processNode(xqrs.getNode(), comarcaNameById, selectedDate, precipitacionMap, intensitatMap, acumuladaMap, calamarsaMap);
                }
            } else {
                System.out.println("Selección inválida. Por favor, intenta de nuevo.");
            }
        } catch (Exception e){}
    }

    private static void processNode(Node node, Map<String, String> comarcaNameById, String selectedDate, Map<String, String> precipitacionMap, Map<String, String> intensitatMap, Map<String, String> acumuladaMap, Map<String, String> calamarsaMap) {
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
        Map<String, String> symbolEmojis = getSymbolEmojis();

        for (int i = 0; i < variables.getLength(); i++) {
            Node variableNode = variables.item(i);
            if (variableNode.getNodeType() == Node.ELEMENT_NODE) {
                Map<String, String> attributes = new HashMap<>();

                for (String attributeName : attributeNames) {
                    Node attributeNode = variableNode.getAttributes().getNamedItem(attributeName);
                    String attributeValue = attributeNode != null ? attributeNode.getNodeValue() : null;
                    attributes.put(attributeName, checkNull(attributeValue));
                }


                if (!attributes.get("data").equals(selectedDate)){
                    return;
                }

                String simbolMatiKey = attributes.get("simbolmati").replace(".png", "");
                String simbolTardaKey = attributes.get("simboltarda").replace(".png", "");

                String simbolMatiEmoji = symbolEmojis.getOrDefault(simbolMatiKey, " - ");
                String simbolTardaEmoji = symbolEmojis.getOrDefault(simbolTardaKey, " - ");


                String precipitacionsMatiDescription = precipitacionMap.getOrDefault(attributes.get("probprecipitaciomati"), " - ");
                String intensitatMatiDescription = intensitatMap.getOrDefault(attributes.get("intensitatprecimati"), " - ");
                String acumuladaMatiDescription = acumuladaMap.getOrDefault(attributes.get("precipitacioacumuladamati"), " - ");
                String calamarsaMatiDescription = calamarsaMap.getOrDefault(attributes.get("probcalamati"), " - ");


                String precipitacionsTardaDescription = precipitacionMap.getOrDefault(attributes.get("probprecipitaciotarda"), " - ");
                String intensitatTardaDescription = intensitatMap.getOrDefault(attributes.get("intensitatprecitarda"), " - ");
                String acumuladaTardaDescription = acumuladaMap.getOrDefault(attributes.get("precipitacioacumuladatarda"), " - ");
                String calamarsaTardaDescription = calamarsaMap.getOrDefault(attributes.get("probcalatarda"), " - ");

                int colWidth = 35;
                int spaceBetweenCols = 3;

                String titleFormat = "│ %-28s Max: %-1sºC - Min: %-1sºC          %-12s      │";
                String lineFormat = "│ %-"+colWidth+"s %-"+spaceBetweenCols+"s %-"+colWidth+"s  │";

                System.out.printf("┌──────────────────────────────────────────────────────────────────────────────┐%n");
                System.out.printf(titleFormat + "%n", comarcaName, attributes.get("tempmax"), attributes.get("tempmin"), selectedDate);
                System.out.printf("├──────────────────────────────────────────────────────────────────────────────┤%n");
                System.out.printf(lineFormat + "%n", "Matí - " + simbolMatiEmoji, "", "Tarda - " + simbolTardaEmoji);
                System.out.printf(lineFormat + "%n", "Precipitacions: " + precipitacionsMatiDescription, "", "Precipitacions: " + precipitacionsTardaDescription);
                System.out.printf(lineFormat + "%n", "Intensitat: " + intensitatMatiDescription, "", "Intensitat: " + intensitatTardaDescription);
                System.out.printf(lineFormat + "%n", "Acumulada: " + acumuladaMatiDescription, "", "Acumulada: " + acumuladaTardaDescription);
                System.out.printf(lineFormat + "%n", "Calamarsa: " + calamarsaMatiDescription, "", "Calamarsa: " + calamarsaTardaDescription);
                System.out.println("└──────────────────────────────────────────────────────────────────────────────┘");

            }
        }
    }

    private Map<String, String> createCategoryMap(String categoryTagName, String attributeName) {
        Map<String, String> categoryMap = new HashMap<>();
        try {
            String query = "for $" + categoryTagName + " in /smc/" + categoryTagName + " return $" + categoryTagName;
            XQResultSequence xqrs = controller.executeQuery(query);

            while (xqrs.next()) {
                Node node = xqrs.getNode();
                String id = node.getAttributes().getNamedItem("id").getNodeValue();
                String description = node.getAttributes().getNamedItem(attributeName).getNodeValue();
                categoryMap.put(id, description);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryMap;
    }


    public static String checkNull(String value) {
        return value != null ? value : " - ";
    }


}