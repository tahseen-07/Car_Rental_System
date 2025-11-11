<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome - Car Rental System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
    <style>
        .bg-image {
            background-image: url('images/car rental background.jpg');
            background-size: cover;
            background-position: center;
        }
    </style>
</head>
<body>

<div class="bg-image min-vh-100 position-relative">
    <!-- Dark Overlay -->
    <div class="position-absolute top-0 start-0 w-100 h-100 bg-dark bg-opacity-50"></div>

    <!-- Content -->
    <div class="position-relative h-100 d-flex flex-column text-white">
        <!-- Top Section: Welcome Heading and Admin Panel Button -->
        <div class="p-4 d-flex justify-content-between align-items-center">
            <h1 class="display-5 fw-bold mb-0 ms-10 text-white">Welcome to the Car Rental System</h1>
            <a href="admin" class="btn btn-success btn-lg">
                Admin Panel
            </a>
        </div>

        <!-- Center Content: View Available Cars Button -->
        <div class="flex-grow-1 d-flex align-items-center justify-content-center flex-column px-5">
            <p class="lead fs-4 mb-4 text-center text-light">Your one-stop solution for renting the best cars.</p>
            <a href="viewCars" class="btn btn-primary btn-lg px-5 py-3">
                View Available Cars
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>