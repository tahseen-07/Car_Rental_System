// Path: src/com/carrental/servlet/RentCarServlet.java

package com.carrental.servlet;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.util.XmlDataService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RentCarServlet extends HttpServlet {

    private final XmlDataService dataService = new XmlDataService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String carId = request.getParameter("carId");

        if (carId != null && !carId.isEmpty()) {
            Car car = dataService.getCarById(getServletContext(), carId);
            request.setAttribute("car", car);
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("rentCar.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get form data
        String carId = request.getParameter("carId");
        String customerName = request.getParameter("customerName");
        String licenseNumber = request.getParameter("licenseNumber");
        int rentDays = Integer.parseInt(request.getParameter("rentDays"));

        // 2. Get car details from XML
        Car car = dataService.getCarById(getServletContext(), carId);

        // 3. Check if car is valid and available
        if (car == null || !car.isAvailable()) {
            // Handle error - car not found or already rented
            response.sendRedirect("viewCars"); // Redirect back to list
            return;
        }

        // 4. Calculate total rent
        double totalRent = car.getRentPerDay() * rentDays;

        // 5. Update car availability in cars.xml to false
        dataService.updateCarAvailability(getServletContext(), carId, false);

        // 6. Create Rental object and save to rentals.xml
        Rental rental = new Rental();
        rental.setCarId(carId);
        rental.setCustomerName(customerName);
        rental.setLicenseNumber(licenseNumber);
        rental.setRentDays(rentDays);
        rental.setTotalRent(totalRent);
        dataService.addRental(getServletContext(), rental);

        // 7. Set attributes for the confirmation page
        request.setAttribute("customerName", customerName);
        request.setAttribute("carModel", car.getBrand() + " " + car.getModel()); // e.g., "Toyota Innova"
        request.setAttribute("rentDays", rentDays);
        request.setAttribute("totalRent", totalRent);

        // 8. Forward to confirm.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("confirm.jsp");
        dispatcher.forward(request, response);
    }
}