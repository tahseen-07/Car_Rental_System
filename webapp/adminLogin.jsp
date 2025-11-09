<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Login</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center align-items-center" style="min-height: 100vh;">
            <div class="col-md-5">
                <div class="card shadow-lg border-0">
                    <div class="card-header bg-dark text-white text-center py-4">
                        <h3 class="mb-0">🔐 Admin Login</h3>
                        <p class="mb-0 mt-2 small">Enter your credentials to access the admin panel</p>
                    </div>
                    <div class="card-body p-5">
                        <%
                            String error = request.getParameter("error");
                            if (error != null && error.equals("1")) {
                        %>
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <strong>❌ Invalid Credentials!</strong> Please check your username and password.
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        <% } %>

                        <form action="admin" method="POST">
                             <div class="mb-4">
                                 <label for="username" class="form-label fw-bold">Username</label>
                                 <input type="text" class="form-control form-control-lg" id="username" name="username" placeholder="Enter username" required autofocus>
                             </div>
                             <div class="mb-4">
                                 <label for="password" class="form-label fw-bold">Password</label>
                                 <input type="password" class="form-control form-control-lg" id="password" name="password" placeholder="Enter password" required>
                             </div>
                            <button type="submit" class="btn btn-dark btn-lg w-100 mb-3">
                                🔓 Login
                            </button>
                            <div class="text-center">
                                <a href="index.jsp" class="text-decoration-none">← Back to Home</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>
