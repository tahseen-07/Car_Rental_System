# CarRentalSystem

Simple Car Rental web application (JSP + Servlets) using XML files as data storage.

## Overview
CarRentalSystem is a small Java web application that demonstrates a basic car rental flow:
- View available cars with thumbnails.
- Open a car detail / rental page and place a booking (name, driving license, number of days).
- Bookings are saved to `WEB-INF/xml/rentals.xml` and cars' availability is updated in `WEB-INF/xml/cars.xml`.
- An Admin panel shows current bookings, license numbers and allows deleting a booking to free the car.

This project is intended for learning and quick demos (not production-ready).

## Technology stack
- Java (Servlets & JSP)
- Jakarta Servlet API (jakarta.servlet / jakarta.jsp)
- Plain Java classes for models (`src/com/carrental/model`)
- XML files for data storage (`webapp/WEB-INF/xml/*.xml`)
- Bootstrap 5 for UI
- No database (XML-backed persistence)

## Project structure (important files)
```
CarRentalSystem/
├─ src/
│  └─ com/carrental/
│     ├─ model/       (Car.java, Rental.java)
│     ├─ servlet/     (CarListServlet.java, RentCarServlet.java, AdminServlet.java)
│     └─ util/        (XmlDataService.java)
├─ webapp/
│  ├─ images/         (C101.jpeg ... C110.jpeg, background)
│  ├─ WEB-INF/
│  │  ├─ web.xml
│  │  └─ xml/
│  │     ├─ cars.xml
│  │     └─ rentals.xml
│  ├─ viewCars.jsp
│  ├─ rentCar.jsp
│  ├─ adminLogin.jsp
│  └─ admin.jsp
└─ README.md
```

## Default admin credentials
For convenience during development the admin credentials are set in code.
- Username: `Adnan`
- Password: `Adnan@123`

Where to change credentials:
- Quick-dev (currently used): `src/com/carrental/servlet/AdminServlet.java` — `init()` sets `adminUsername` and `adminPassword`.
- Alternative (safer): set `admin.username` and `admin.password` context params in `webapp/WEB-INF/web.xml` and change `AdminServlet` to read them. (Either approach is possible; currently the project contains a hard-coded fallback.)

## How data is stored
- Cars: `webapp/WEB-INF/xml/cars.xml` — contains car entries with id, brand, model, rentPerDay, availability and `image` filename.
- Rentals/bookings: `webapp/WEB-INF/xml/rentals.xml` — each booking stores `carId`, `customerName`, `licenseNumber`, `days`, `totalRent`.

Important: The application updates these XML files directly. Concurrent writes from multiple users can cause issues; this is intended as a simple demo implementation.

## How to run (development)
Requirements:
- JDK 11 or newer (Java 17 recommended)
- Apache Tomcat 10.x (or another Jakarta-compatible servlet container)
- An IDE that supports Java webapps (IntelliJ IDEA, Eclipse) is strongly recommended.

Run steps (recommended using IDE):
1. Import this project into your IDE as a web application (or open as an existing project).
2. Configure a Tomcat 10.x run configuration and point the artifact/context to this project.
3. Start Tomcat from the IDE — the app's `index.jsp` should appear at `http://localhost:8080/<app-context>/`.

Manual (quick) steps to deploy to Tomcat (use IDE for building classes):
- Build the project classes (compile source) and create a WAR artifact (IDE -> Build Artifact -> Build).
- Drop the WAR into Tomcat's `webapps/` and start Tomcat.

Sample commands (Windows `cmd.exe`) if you have a WAR artifact ready (replace paths appropriately):

```cmd
copy path\to\CarRentalSystem.war %TOMCAT_HOME%\webapps\
%TOMCAT_HOME%\bin\startup.bat
```

Then open: `http://localhost:8080/CarRentalSystem/` (context name may vary by WAR name or IDE config)

## Admin panel
- Go to `/admin` or click the Admin Panel button on the home page.
- Log in with the admin credentials above.
- You can view bookings and delete a booking which will mark the car as available again.

## Notes, known issues and next steps
- Not production-ready: no authentication best-practices, no input validation beyond basic HTML, no CSRF protection, and XML file-based persistence is not safe for concurrent access.
- If multiple users book at the same time, XML writes may corrupt files. Consider moving to a real DB (MySQL/Postgres/H2) and using JDBC or JPA.
- Consider adding server-side validation and sanitization (e.g., license number format, integer parsing checks) and better exception handling + logging.
- Consider adding tests and a build tool (Maven/Gradle) to manage compilation and artifact creation.

## Quick troubleshooting
- If a page shows `Car details not found.`: confirm you clicked `Rent Now` from the `viewCars` list (the `carId` query param must be present), or check the `cars.xml` for that car id.
- If images are not showing: check `webapp/images/` contains the files listed in `cars.xml` and that image filenames match exactly (case-sensitive on Linux). The JSPs use `/images/<imageName>`.
- If admin login fails: use the credentials above or edit `AdminServlet` / `web.xml` as described.

## New: Quick Maven Run (Jetty Embedded)
If you have Java (JDK 17+) and Maven installed, you can run the app immediately without installing Tomcat manually:

```bash
mvn jetty:run
```
Then open: http://localhost:8080/

Troubleshooting:
- If you see `command not found: mvn`, install Maven (see below).
- If pages 404, ensure Jetty started successfully and no port conflicts.

## macOS Setup Steps (No Tools Installed Yet)
1. Install Homebrew (package manager):
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
2. Add Homebrew to your shell (follow on-screen output, typically):
```bash
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
source ~/.zprofile
```
3. Install a JDK (Temurin / Eclipse Adoptium):
```bash
brew install temurin
```
4. Install Maven:
```bash
brew install maven
```
5. Verify:
```bash
java -version
mvn -version
```
6. Run the app:
```bash
mvn jetty:run
```

## Provided Run Script
You can also use the helper script:
```bash
./scripts/run-local.sh
```
This checks for Java/Maven and prints guidance if missing.

## Fallback: Run Without Maven (Manual Tomcat Deploy)
If you have Tomcat 10.x but no Maven:
1. Download Tomcat 10 (Jakarta-based) from official site and extract (e.g., /opt/tomcat10).
2. Compile sources into `webapp/WEB-INF/classes`:
```bash
mkdir -p webapp/WEB-INF/classes
javac -d webapp/WEB-INF/classes $(find src -name '*.java')
```
3. (Optional) Remove any stale compiled classes.
4. Start Tomcat:
```bash
/opt/tomcat10/bin/startup.sh
```
5. Deploy by copying the entire project directory into Tomcat `webapps/CarRentalSystem` OR build a WAR (requires Maven/IDE).
6. Access: http://localhost:8080/CarRentalSystem/

## Next Enhancements
- Add Dockerfile for containerized runs (Tomcat base image).
- Add integration tests for servlet endpoints.
- Externalize admin credentials with environment variables or context params.

## License
MIT — modify as you wish for learning/demos.

---
If you want, I can also:
- Add a small `build.xml` (Ant) or `pom.xml` (Maven) to make building and creating a WAR straightforward.
- Move admin credentials to `web.xml` context params and remove the hard-coded fallback (recommended).
- Add README run commands customized for your Tomcat installation (tell me your Tomcat path).
