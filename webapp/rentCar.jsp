<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.carrental.model.Car" %>
<%
    // Fetch car details based on carId
    String carId = request.getParameter("carId");
    Car car = (Car) request.getAttribute("car"); // Assumes servlet forwards car object
%>
<html>
<head>
    <title>Rent a Car</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        .car-preview {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 8px;
            padding: 20px;
            color: white;
            margin-bottom: 20px;
        }
        .car-image-wrapper {
            background-color: #fff;
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 15px;
        }
        .car-preview img {
            width: 100%;
            height: 300px;
            object-fit: contain;
            border-radius: 8px;
            display: block;
            background-color: #f8f9fa;
        }
        .car-info {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
        }
        .price-highlight {
            font-size: 1.5rem;
            font-weight: bold;
            color: #ffc107;
        }
        .form-label {
            font-weight: 500;
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow-lg">
                    <div class="card-header bg-primary text-white text-center">
                        <h1 class="h4 mb-0">Complete Your Rental</h1>
                    </div>
                    <div class="card-body">
                        <% if (car != null) { %>
                            <!-- Car Preview Section -->
                            <div class="car-preview">
                                <div class="car-image-wrapper">
                                    <img src="<%= request.getContextPath() %>/images/<%= car.getImage() %>"
                                         alt="<%= car.getBrand() %> <%= car.getModel() %>"
                                         onerror="this.src='<%= request.getContextPath() %>/images/default-car.jpg'; this.onerror=null;">
                                </div>
                                <h3 class="mb-2"><%= car.getBrand() %> <%= car.getModel() %></h3>
                                <div class="car-info">
                                    <span><strong>Type:</strong> <%= car.getType() %></span>
                                    <span class="price-highlight">₹<%= String.format("%.0f", car.getRentPerDay()) %>/day</span>
                                </div>
                            </div>

                            <!-- Rental Form -->
                            <form action="rentCar" method="POST">
                                <input type="hidden" name="carId" value="<%= car.getId() %>" />

                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="customerName" class="form-label">Full Name <span class="text-danger">*</span></label>
                                        <input type="text" id="customerName" name="customerName" class="form-control" placeholder="Enter your full name" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label for="licenseNumber" class="form-label">Driving License Number <span class="text-danger">*</span></label>
                                        <input type="text" id="licenseNumber" name="licenseNumber" class="form-control" placeholder="e.g., DL-1234567890" required>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="rentDays" class="form-label">Number of Days <span class="text-danger">*</span></label>
                                    <input type="number" id="rentDays" name="rentDays" class="form-control" min="1" value="1" required>
                                    <small class="text-muted">Daily Rate: ₹<%= String.format("%.0f", car.getRentPerDay()) %></small>
                                </div>

                                <div class="alert alert-info">
                                    <strong>Total Estimated Cost:</strong> <span id="totalCost">₹<%= String.format("%.0f", car.getRentPerDay()) %></span>
                                </div>

                                <button type="submit" class="btn btn-success w-100 py-2">
                                    Confirm Rental
                                </button>
                            </form>
                        <% } else { %>
                            <div class="alert alert-warning">Car details not found.</div>
                        <% } %>
                    </div>
                    <div class="card-footer text-center bg-light">
                        <a href="viewCars" class="btn btn-outline-secondary btn-sm">&laquo; Cancel and Go Back</a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        // Calculate total cost dynamically
        const rentDaysInput = document.getElementById('rentDays');
        const totalCostSpan = document.getElementById('totalCost');
        const dailyRate = <%= car != null ? car.getRentPerDay() : 0 %>;

        if (rentDaysInput && totalCostSpan) {
            rentDaysInput.addEventListener('input', function() {
                const days = parseInt(this.value) || 1;
                const total = days * dailyRate;
                totalCostSpan.textContent = '₹' + total.toFixed(0);
            });
        }
    </script>
</body>
</html>
