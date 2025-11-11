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

public class CarListServlet extends HttpServlet {

    private XmlDataService dataService = new XmlDataService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Car> carList = dataService.getAllCars(getServletContext());

        request.setAttribute("carList", carList);

        RequestDispatcher dispatcher = request.getRequestDispatcher("viewCars.jsp");
        dispatcher.forward(request, response);
    }
}