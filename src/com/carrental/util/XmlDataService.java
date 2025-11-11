// Path: src/com/carrental/util/XmlDataService.java
package com.carrental.util;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import jakarta.servlet.ServletContext;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This class now handles all data operations by connecting to a MySQL database
 * instead of reading/writing XML files. If the DB connection cannot be obtained,
 * it falls back to the XML files in WEB-INF/xml/ so the app remains functional.
 */
public class XmlDataService {

    private static final Logger LOGGER = Logger.getLogger(XmlDataService.class.getName());

    private static final String CARS_XML = "/WEB-INF/xml/cars.xml";
    private static final String RENTALS_XML = "/WEB-INF/xml/rentals.xml";

    // Helper method to create a Car object from a database ResultSet
    private Car extractCarFromResultSet(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getString("id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setType(rs.getString("type"));
        car.setRentPerDay(rs.getDouble("rentPerDay"));
        car.setAvailable(rs.getBoolean("availability"));
        car.setImage(rs.getString("image"));
        return car;
    }

    // --- XML helper methods for fallback ---
    private File getFile(ServletContext context, String xmlPath) {
        try {
            String realPath = context.getRealPath(xmlPath);
            return new File(realPath);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error resolving XML file path: " + xmlPath, e);
            return null;
        }
    }

    private Document getDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }

    private void saveDocument(File file, Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    /**
     * Retrieves all cars from the 'cars' table in the database.
     * The ServletContext is no longer needed but is kept for compatibility
     * with the servlet's method signature.
     */
    public List<Car> getAllCars(ServletContext context) {
        // Mark the context as referenced to avoid unused-parameter warnings
        if (context == null) {
            // no-op, context intentionally unused beyond compatibility
        }

        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in getAllCars(). Falling back to XML.");

                // Fallback: read from XML
                File file = getFile(context, CARS_XML);
                if (file == null || !file.exists()) return cars;
                try {
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
                            try {
                                car.setRentPerDay(Double.parseDouble(getValue(e, "rentPerDay")));
                            } catch (NumberFormatException ex) {
                                car.setRentPerDay(0.0);
                            }
                            car.setAvailable(Boolean.parseBoolean(getValue(e, "availability")));
                            car.setImage(getValue(e, "image"));
                            cars.add(car);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error reading cars XML fallback", ex);
                }
                return cars;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    cars.add(extractCarFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all cars", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return cars;
    }

    private static String getValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0 && nodeList.item(0) != null) {
            return nodeList.item(0).getTextContent();
        }
        return ""; // Return empty string if element doesn't exist
    }

    /**
     * Retrieves a single car by its ID from the 'cars' table.
     */
    public Car getCarById(ServletContext context, String carId) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "SELECT * FROM cars WHERE id = ?";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in getCarById(). Falling back to XML.");
                // fallback: search XML
                List<Car> cars = getAllCars(context);
                for (Car c : cars) {
                    if (c.getId().equals(carId)) return c;
                }
                return null;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, carId); // Set the ID parameter
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractCarFromResultSet(rs);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching car by ID", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return null; // No car found
    }

    /**
     * Updates a car's availability in the 'cars' table.
     */
    public synchronized boolean updateCarAvailability(ServletContext context, String carId, boolean isAvailable) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "UPDATE cars SET availability = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in updateCarAvailability(). Falling back to XML.");
                // fallback: update XML
                File file = getFile(context, CARS_XML);
                if (file == null || !file.exists()) return false;
                try {
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
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error updating car availability in XML fallback", ex);
                }
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, isAvailable);
                ps.setString(2, carId);

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0; // True if update was successful
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating car availability", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return false;
    }

    /**
     * Inserts a new rental record into the 'rentals' table.
     */
    public synchronized void addRental(ServletContext context, Rental rental) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "INSERT INTO rentals (carId, customerName, days, totalRent) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in addRental(). Falling back to XML.");
                // fallback: append to rentals.xml
                File file = getFile(context, RENTALS_XML);
                if (file == null) return;
                try {
                    Document doc = getDocument(file);
                    Element root = doc.getDocumentElement();
                    Element rentalElem = doc.createElement("rental");

                    Element carIdElem = doc.createElement("carId");
                    carIdElem.setTextContent(rental.getCarId());
                    rentalElem.appendChild(carIdElem);

                    Element nameElem = doc.createElement("customerName");
                    nameElem.setTextContent(rental.getCustomerName());
                    rentalElem.appendChild(nameElem);

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
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error adding rental in XML fallback", ex);
                }
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, rental.getCarId());
                ps.setString(2, rental.getCustomerName());
                ps.setInt(3, rental.getRentDays());
                ps.setDouble(4, rental.getTotalRent());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding new rental", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Deletes a rental record by carId. Returns true if a row was deleted.
     */
    public synchronized boolean deleteRentalByCarId(ServletContext context, String carId) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "DELETE FROM rentals WHERE carId = ?";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in deleteRentalByCarId(). Falling back to XML.");
                File file = getFile(context, RENTALS_XML);
                if (file == null || !file.exists()) return false;
                try {
                    Document doc = getDocument(file);
                    NodeList rentalNodes = doc.getElementsByTagName("rental");
                    for (int i = 0; i < rentalNodes.getLength(); i++) {
                        Node node = rentalNodes.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) node;
                            String cid = getValue(e, "carId");
                            if (carId.equals(cid)) {
                                doc.getDocumentElement().removeChild(node);
                                saveDocument(file, doc);
                                return true;
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error deleting rental in XML fallback", ex);
                }
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, carId);
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting rental by carId", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return false;
    }

    /**
     * Retrieves all rental records from the 'rentals' table.
     */
    public List<Rental> getAllRentals(ServletContext context) {
        if (context == null) { /* no-op - keep signature compatible */ }
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals";

        Connection conn = null;
        try {
            conn = DbConnection.getConnection();
            if (conn == null) {
                LOGGER.severe("Failed to obtain DB connection in getAllRentals(). Falling back to XML.");

                File file = getFile(context, RENTALS_XML);
                if (file == null || !file.exists()) return rentals;
                try {
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
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error reading rentals XML fallback", ex);
                }
                return rentals;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Rental rental = new Rental();
                    rental.setCarId(rs.getString("carId"));
                    rental.setCustomerName(rs.getString("customerName"));
                    rental.setRentDays(rs.getInt("days"));
                    rental.setTotalRent(rs.getDouble("totalRent"));
                    rentals.add(rental);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all rentals", e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
        return rentals;
    }
}

