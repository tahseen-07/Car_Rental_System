// Path: src/com/carrental/servlet/AdminServlet.java

package com.carrental.servlet;

import com.carrental.model.Car;
import com.carrental.model.Rental;
import com.carrental.util.XmlDataService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminServlet extends HttpServlet {

    private final XmlDataService dataService = new XmlDataService();

    private String adminUsername;
    private String adminPassword;

    @Override
    public void init() {
        // Use explicit credentials defined in the servlet for quick testing.
        // NOTE: For production, store credentials securely (database or env/config), not in source.
        adminUsername = "Tahseen";
        adminPassword = "Tahseen@123";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Check if user is logged
        if (session == null || session.getAttribute("adminLoggedIn") == null) {
            // Not logged in, redirect to login page
            response.sendRedirect("adminLogin.jsp");
            return;
        }

        // User is logged in, show admin panel
        // 1. Get all rental records from the XML file
        List<Rental> rentalList = dataService.getAllRentals(getServletContext());

        // 2. Get car details for each rental
        Map<String, Car> carMap = new HashMap<>();
        for (Rental rental : rentalList) {
            Car car = dataService.getCarById(getServletContext(), rental.getCarId());
            if (car != null) {
                carMap.put(rental.getCarId(), car);
            }
        }
        // 3. Set both lists as attributes to be read by the JSP
        request.setAttribute("rentalList", rentalList);
        request.setAttribute("carMap", carMap);

        // 4. Forward the request to the admin.jsp page
        RequestDispatcher dispatcher = request.getRequestDispatcher("admin.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Handle delete action separately
        if ("delete".equals(request.getParameter("action"))) {
            String carId = request.getParameter("carId");
            if (carId != null && !carId.isEmpty()) {
                // delete rental by carId and set car availability to true
                boolean deleted = dataService.deleteRentalByCarId(getServletContext(), carId);
                if (deleted) {
                    dataService.updateCarAvailability(getServletContext(), carId, true);
                }
            }
            response.sendRedirect("admin");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Check if logout request
        if ("logout".equals(request.getParameter("action"))) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("index.jsp");
            return;
        }

        // Validate credentials
        if (adminUsername != null && adminPassword != null && adminUsername.equals(username) && adminPassword.equals(password)) {
            // Credentials are correct, create session
            HttpSession session = request.getSession(true);
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("adminUsername", username);

            // Redirect to admin panel (which will trigger doGet)
            response.sendRedirect("admin");
        } else {
            // Invalid credentials, redirect back to login with error
            response.sendRedirect("adminLogin.jsp?error=1");
        }
    }
}