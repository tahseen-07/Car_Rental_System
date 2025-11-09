<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rental Confirmation</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <style>
        .success-animation {
            animation: scaleIn 0.5s ease-in-out;
        }
        @keyframes scaleIn {
            0% {
                transform: scale(0.8);
                opacity: 0;
            }
            100% {
                transform: scale(1);
                opacity: 1;
            }
        }
        .total-amount {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            color: white;
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
        }
        .detail-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .checkmark-icon {
            font-size: 4rem;
            color: #28a745;
            animation: checkmarkPop 0.6s ease-in-out;
        }
        @keyframes checkmarkPop {
            0%, 100% {
                transform: scale(1);
            }
            50% {
                transform: scale(1.2);
            }
        }
    </style>
</head>
<body class="bg-light">
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-7">
                <div class="card shadow-lg success-animation border-0">
                    <div class="card-header bg-success text-white text-center py-4">
                        <div class="checkmark-icon">✓</div>
                        <h1 class="h3 mb-0">Rental Confirmed!</h1>
                        <p class="mb-0 mt-2">Your booking has been successfully confirmed</p>
                    </div>
                    <div class="card-body p-4">
                        <div class="alert alert-success" role="alert">
                            <strong>Thank you, <%= request.getAttribute("customerName") %>!</strong>
                        </div>

                        <h5 class="mb-3">Booking Details:</h5>
                        <div class="detail-row">
                            <span class="text-muted">Car Model:</span>
                            <strong><%= request.getAttribute("carModel") %></strong>
                        </div>
                        <div class="detail-row">
                            <span class="text-muted">Rental Duration:</span>
                            <strong><%= request.getAttribute("rentDays") %> days</strong>
                        </div>
                        <div class="detail-row">
                            <span class="text-muted">Customer Name:</span>
                            <strong><%= request.getAttribute("customerName") %></strong>
                        </div>

                        <div class="total-amount text-center">
                            <p class="mb-1">Total Rental Amount</p>
                            <h2 class="mb-0">₹<%= String.format("%.0f", (Double)request.getAttribute("totalRent")) %></h2>
                        </div>

                        <div class="alert alert-info mt-3">
                            <strong>📌 Important:</strong> Please bring your original driving license and a valid ID proof at the time of pickup.
                        </div>
                    </div>
                    <div class="card-footer bg-light text-center py-3">
                        <a href="viewCars" class="btn btn-primary me-2">
                            <i class="bi bi-car-front"></i> View More Cars
                        </a>
                        <a href="index.jsp" class="btn btn-outline-secondary">
                            <i class="bi bi-house"></i> Back to Home
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>