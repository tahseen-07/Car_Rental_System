// Path: src/com/carrental/util/XmlDataService.java
package com.carrental.util;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import jakarta.servlet.ServletContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class now handles all data operations by connecting to a MySQL database
 * instead of reading/writing XML files.
 */
public class XmlDataService {

    private static final Logger LOGGER = Logger.getLogger(XmlDataService.class.getName());

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

        // Use try-with-resources to automatically close the connection
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cars.add(extractCarFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all cars", e);
        }
        return cars;
    }

    /**
     * Retrieves a single car by its ID from the 'cars' table.
     */
    public Car getCarById(ServletContext context, String carId) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "SELECT * FROM cars WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, carId); // Set the ID parameter
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCarFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching car by ID", e);
        }
        return null; // No car found
    }

    /**
     * Updates a car's availability in the 'cars' table.
     */
    public synchronized boolean updateCarAvailability(ServletContext context, String carId, boolean isAvailable) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "UPDATE cars SET availability = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, isAvailable);
            ps.setString(2, carId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // True if update was successful
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating car availability", e);
        }
        return false;
    }

    /**
     * Inserts a new rental record into the 'rentals' table.
     */
    public synchronized void addRental(ServletContext context, Rental rental) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "INSERT INTO rentals (carId, customerName, days, totalRent) VALUES (?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rental.getCarId());
            ps.setString(2, rental.getCustomerName());
            ps.setInt(3, rental.getRentDays());
            ps.setDouble(4, rental.getTotalRent());

            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding new rental", e);
        }
    }

    /**
     * Deletes a rental record by carId. Returns true if a row was deleted.
     */
    public synchronized boolean deleteRentalByCarId(ServletContext context, String carId) {
        if (context == null) { /* no-op - keep signature compatible */ }
        String sql = "DELETE FROM rentals WHERE carId = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, carId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting rental by carId", e);
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

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Rental rental = new Rental();
                rental.setCarId(rs.getString("carId"));
                rental.setCustomerName(rs.getString("customerName"));
                rental.setRentDays(rs.getInt("days"));
                rental.setTotalRent(rs.getDouble("totalRent"));
                rentals.add(rental);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all rentals", e);
        }
        return rentals;
    }
}