// Path: src/com/carrental/util/XmlDataService.java

package com.carrental.util;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import jakarta.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys; // <-- IMPORT THIS
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlDataService {

    private static final String CARS_XML = "/WEB-INF/xml/cars.xml";
    private static final String RENTALS_XML = "/WEB-INF/xml/rentals.xml";

    private static File getFile(ServletContext context, String xmlPath) {
        String realPath = context.getRealPath(xmlPath);
        return new File(realPath);
    }

    private static Document getDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    // *** THIS METHOD IS UPDATED ***
    private static void saveDocument(File file, Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        // --- ADD THESE LINES FOR PRETTY-PRINTING ---
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        // --- END OF ADDED LINES ---

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static String getValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0) != null) {
            return nodeList.item(0).getTextContent();
        }
        return ""; // Return empty string if element doesn't exist
    }

    public List<Car> getAllCars(ServletContext context) {
        List<Car> cars = new ArrayList<>();
        try {
            File file = getFile(context, CARS_XML);
            if (!file.exists()) return cars;
            Document doc = getDocument(file);
            NodeList carNodes = doc.getElementsByTagName("car");

            for (int i = 0; i < carNodes.getLength(); i++) {
                Node node = carNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Car car = new Car();
                    car.setId(e.getAttribute("id"));
                    car.setBrand(getValue(e, "brand"));
                    car.setModel(getValue(e, "model"));
                    car.setType(getValue(e, "type"));
                    car.setRentPerDay(Double.parseDouble(getValue(e, "rentPerDay")));
                    car.setAvailable(Boolean.parseBoolean(getValue(e, "availability")));
                    car.setImage(getValue(e, "image"));
                    cars.add(car);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cars;
    }

    public Car getCarById(ServletContext context, String carId) {
        List<Car> cars = getAllCars(context);
        for (Car car : cars) {
            if (car.getId().equals(carId)) {
                return car;
            }
        }
        return null;
    }

    public synchronized boolean updateCarAvailability(ServletContext context, String carId, boolean isAvailable) {
        try {
            File file = getFile(context, CARS_XML);
            Document doc = getDocument(file);
            NodeList carNodes = doc.getElementsByTagName("car");

            for (int i = 0; i < carNodes.getLength(); i++) {
                Node node = carNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    if (e.getAttribute("id").equals(carId)) {
                        e.getElementsByTagName("availability").item(0).setTextContent(String.valueOf(isAvailable));
                        saveDocument(file, doc);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void addRental(ServletContext context, Rental rental) {
        try {
            File file = getFile(context, RENTALS_XML);
            Document doc = getDocument(file);

            Element root = doc.getDocumentElement();
            Element rentalElem = doc.createElement("rental");

            Element carIdElem = doc.createElement("carId");
            carIdElem.setTextContent(rental.getCarId());
            rentalElem.appendChild(carIdElem);

            Element nameElem = doc.createElement("customerName");
            nameElem.setTextContent(rental.getCustomerName());
            rentalElem.appendChild(nameElem);

            // include licenseNumber (may be null/empty)
            Element licenceElem = doc.createElement("licenseNumber");
            licenceElem.setTextContent(rental.getLicenseNumber() != null ? rental.getLicenseNumber() : "");
            rentalElem.appendChild(licenceElem);

            Element daysElem = doc.createElement("days");
            daysElem.setTextContent(String.valueOf(rental.getRentDays()));
            rentalElem.appendChild(daysElem);

            Element totalElem = doc.createElement("totalRent");
            totalElem.setTextContent(String.valueOf(rental.getTotalRent()));
            rentalElem.appendChild(totalElem);

            root.appendChild(rentalElem);
            saveDocument(file, doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete a rental by carId (returns true if deleted)
    public synchronized boolean deleteRentalByCarId(ServletContext context, String carId) {
        try {
            File file = getFile(context, RENTALS_XML);
            Document doc = getDocument(file);

            NodeList rentalNodes = doc.getElementsByTagName("rental");
            for (int i = 0; i < rentalNodes.getLength(); i++) {
                Node node = rentalNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    String cid = getValue(e, "carId");
                    if (carId.equals(cid)) {
                        // remove node
                        doc.getDocumentElement().removeChild(node);
                        saveDocument(file, doc);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Rental> getAllRentals(ServletContext context) {
        List<Rental> rentals = new ArrayList<>();
        try {
            File file = getFile(context, RENTALS_XML);
            if (!file.exists()) return rentals;

            Document doc = getDocument(file);
            NodeList rentalNodes = doc.getElementsByTagName("rental");

            for (int i = 0; i < rentalNodes.getLength(); i++) {
                Node node = rentalNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;
                    Rental rental = new Rental();
                    rental.setCarId(getValue(e, "carId"));
                    rental.setCustomerName(getValue(e, "customerName"));
                    rental.setLicenseNumber(getValue(e, "licenseNumber"));
                    rental.setRentDays(Integer.parseInt(getValue(e, "days")));
                    rental.setTotalRent(Double.parseDouble(getValue(e, "totalRent")));
                    rentals.add(rental);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rentals;
    }
}