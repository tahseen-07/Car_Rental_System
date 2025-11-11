<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.carrental.model.Rental" %>
<%@ page import="com.carrental.model.Car" %>
<html>
<head>
    <title>Admin Panel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-lg-11">
            <div class="card shadow-lg">
                <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                    <h1 class="h4 mb-0">🚗 Admin Panel — Rented Cars Details</h1>
                    <div>
                        <span class="me-3 text-light">Welcome, <strong><%= session.getAttribute("adminUsername") %></strong>!</span>
                        <a href="index.jsp" class="btn btn-light btn-sm me-2">&laquo; Home</a>
                        <form action="admin" method="POST" class="d-inline">
                            <input type="hidden" name="action" value="logout">
                            <button type="submit" class="btn btn-danger btn-sm">🔒 Logout</button>
                        </form>
                    </div>
                </div>
                <div class="card-body">
                    <%
                        List<Rental> rentalList = (List<Rental>) request.getAttribute("rentalList");
                        Map<String, Car> carMap = (Map<String, Car>) request.getAttribute("carMap");
                        if (rentalList == null || rentalList.isEmpty()) {
                    %>
                    <div class="alert alert-info" role="alert">
                        <strong>ℹ️ No cars have been rented yet.</strong>
                    </div>
                    <%
                    } else {
                    %>
                    <div class="table-responsive">
                        <table class="table table-striped table-hover table-bordered align-middle">
                            <thead class="table-dark">
                            <tr>
                                <th>Car ID</th>
                                <th>Car Details</th>
                                <th>Type</th>
                                <th>Rent/Day</th>
                                <th>Customer Name</th>
                                <th>License Number</th>
                                <th>Days</th>
                                <th>Total Rent</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%
                                for (Rental rental : rentalList) {
                                    Car car = carMap.get(rental.getCarId());
                            %>
                            <tr>
                                <td><span class="badge bg-primary"><%= rental.getCarId() %></span></td>
                                <td>
                                    <% if (car != null) { %>
                                        <strong><%= car.getBrand() %></strong><br>
                                        <small class="text-muted"><%= car.getModel() %></small>
                                    <% } else { %>
                                        <span class="text-muted">N/A</span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (car != null) { %>
                                        <span class="badge bg-secondary"><%= car.getType() %></span>
                                    <% } else { %>
                                        <span class="text-muted">N/A</span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (car != null) { %>
                                        ₹<%= String.format("%.0f", car.getRentPerDay()) %>
                                    <% } else { %>
                                        <span class="text-muted">N/A</span>
                                    <% } %>
                                </td>
                                <td><strong><%= rental.getCustomerName() %></strong></td>
                                <td>
                                    <% if (rental.getLicenseNumber() != null && !rental.getLicenseNumber().isEmpty()) { %>
                                        <code><%= rental.getLicenseNumber() %></code>
                                    <% } else { %>
                                        <span class="text-muted">N/A</span>
                                    <% } %>
                                </td>
                                <td><span class="badge bg-info text-dark"><%= rental.getRentDays() %> days</span></td>
                                <td><strong class="text-success">₹<%= String.format("%.0f", rental.getTotalRent()) %></strong></td>
                                <td>
                                    <form action="admin" method="post" onsubmit="return confirm('Delete this booking and release the car?');">
                                        <input type="hidden" name="action" value="delete" />
                                        <input type="hidden" name="carId" value="<%= rental.getCarId() %>" />
                                        <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                    </form>
                                </td>
                            </tr>
                            <%
                                }
                            %>
                            </tbody>
                        </table>
                    </div>

                    <div class="alert alert-success mt-3">
                        <strong>📊 Summary:</strong> Total Rented Cars: <span class="badge bg-success"><%= rentalList.size() %></span>
                    </div>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>