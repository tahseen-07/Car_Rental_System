<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.carrental.model.Car" %>
<html>
<head>
    <title>Available Cars</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        .header-wrapper {
            position: relative;
            min-height: 64px;
        }
        .page-title {
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
            margin: 0;
            text-align: center;
            font-weight: 700;
            letter-spacing: 0.4px;
            color: #2c3e50;
            font-size: 2rem;
        }
        .back-btn {
            position: absolute;
            right: 0;
            top: 50%;
            transform: translateY(-50%);
        }
        /* small responsive tweak */
        @media (max-width: 576px) {
            .page-title { font-size: 1.2rem; }
            .back-btn { right: 8px; }
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="header-wrapper mb-4">
            <h1 class="page-title">Available Cars for Rent</h1>
            <a href="index.jsp" class="back-btn btn btn-outline-secondary">&laquo; Back to Home</a>
        </div>

        <div class="card shadow-sm">
            <div class="card-body p-0">
                <table class="table table-hover table-striped mb-0">
                    <thead class="table-dark">
                        <tr>
                            <th>Photo</th>
                            <th>Brand</th>
                            <th>Model</th>
                            <th>Type</th>
                            <th>Rent/Day</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            List<Car> carList = (List<Car>) request.getAttribute("carList");
                            if (carList != null && !carList.isEmpty()) {
                                for (Car car : carList) {
                        %>
                            <tr class="align-middle">
                                <td>
                                    <img src="<%= request.getContextPath() %>/images/<%= car.getImage() %>" alt="<%= car.getModel() %>"
                                         style="width: 150px; height: 90px; object-fit: cover; border-radius: 5px;"
                                         onerror="this.src='<%= request.getContextPath() %>/images/default-car.jpg'; this.onerror=null;">
                                </td>
                                <td><strong><%= car.getBrand() %></strong></td>
                                <td><strong><%= car.getModel() %></strong></td>
                                <td><span class="badge bg-secondary"><%= car.getType() %></span></td>
                                <td>₹<%= String.format("%.0f", car.getRentPerDay()) %></td>
                                <% if (car.isAvailable()) { %>
                                    <td><span class="badge bg-success">Available</span></td>
                                    <td>
                                        <a href="rentCar?carId=<%= car.getId() %>" class="btn btn-primary btn-sm">Rent Now</a>
                                    </td>
                                <% } else { %>
                                    <td><span class="badge bg-danger">Rented</span></td>
                                    <td>
                                        <button class="btn btn-secondary btn-sm" disabled>Unavailable</button>
                                    </td>
                                <% } %>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="7" class="text-center p-4">No cars found.</td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>