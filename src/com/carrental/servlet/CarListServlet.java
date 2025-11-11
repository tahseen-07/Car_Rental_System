// Path: src/com/carrental/servlet/CarListServlet.java

package com.carrental.servlet;

import com.carrental.model.Car;
import com.carrental.util.XmlDataService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CarListServlet extends HttpServlet {

    private XmlDataService dataService = new XmlDataService();
    private static final Logger LOGGER = Logger.getLogger(CarListServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Car> carList = dataService.getAllCars(getServletContext());

        if (carList == null) {
            LOGGER.severe("carList is null in CarListServlet.doGet -- dataService.getAllCars returned null");
        } else {
            LOGGER.log(Level.INFO, "CarList size: {0}", carList.size());
        }

        request.setAttribute("carList", carList);

        RequestDispatcher dispatcher = request.getRequestDispatcher("viewCars.jsp");
        dispatcher.forward(request, response);
    }
}