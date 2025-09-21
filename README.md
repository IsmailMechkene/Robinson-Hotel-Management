# Robinson Hotel Management

Robinson Hotel Management is a desktop application built in Java (using JavaFX) with MySQL for data persistence. The system is designed to help hotel administrators manage guests, rooms, bookings and various operations efficiently via a user-friendly graphical interface.

---

## 🧭 Table of Contents

1. [Features](#features)  
2. [Tech Stack](#tech-stack)  
3. [Architecture & Structure](#architecture--structure)  
4. [Setup & Installation](#setup--installation)  
5. [Usage](#usage)  
6. [Screenshots / UI Flow](#screenshots--ui-flow)  
7. [Contributing](#contributing)  
8. [Author](#author)  
9. [License](#license)  

---

## Features

- **Room Management** — View, add, edit, and remove rooms. Keep track of room types, status (available / occupied / under maintenance), pricing.  
- **Guest / Customer Management** — Register guests, edit guest profiles, store contact and personal information.  
- **Booking / Reservation System** — Book available rooms, manage check-ins and check-outs, track booking history.  
- **Database Integration** — Persistence via MySQL; relational schema for rooms, guests, bookings, etc.  
- **User Interface** — Built with JavaFX; desktop GUI with clear navigation and input forms.  
- **Validation & Error Handling** — Basic input validation, error dialogs, handling invalid operations.  

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17+ |
| UI Framework | JavaFX |
| Build / Dependency Management | Maven |
| Database | MySQL |
| Database Connector | mysql-connector-java |
| SDKs / Libraries | JavaFX SDK, MySQL JDBC driver |

---

## Architecture & Structure

/
├── .idea/                    # IDE configuration files (excluded from version control)
├── mvnw/                    # Maven wrapper (Unix/Linux script)
├── mvnw.cmd                 # Maven wrapper (Windows script)
├── javafx-sdk-24/           # Bundled JavaFX SDK
├── mysql-connector-j-9.2.0/ # JDBC driver for MySQL
├── src/
│   └── main/
│       ├── java/            # Java source code
│       └── resources/       # Static resources (FXML, CSS, images, etc.)
├── pom.xml                  # Maven build and dependency configuration
├── .gitignore              # Specifies files/folders ignored by Git
└── .gitattributes          # Git attributes configuration


- **Java packages / classes** handle business logic (controllers), data models (entities), persistence (DAO layer), and UI (FXML / JavaFX controllers).  
- **Resources** include FXML layouts, CSS styling, and other UI assets.  
- **pom.xml** defines dependencies, build lifecycle, Java version, etc.  

---

## Setup & Installation

1. **Prerequisites**  
   - Java Development Kit (JDK) 17+  
   - MySQL server running  
   - Maven installed (or use the provided Maven wrapper)  

2. **Clone the repository**

   ```bash
   git clone https://github.com/IsmailMechkene/Robinson-Hotel-Management.git
   cd Robinson-Hotel-Management

3. **Configure the database**

- Create a MySQL database, e.g. robinson_hotel_db.
- Set up tables (rooms, guests, bookings, etc.).
- Update database credentials in the code (username/password and JDBC URL).

4. **Build the project**

   ```bash
   mvn clean install

5. **Run the application**

- From IDE: Run the main class.
- Or via command line:
   ```bash
   mvn exec:java -Dexec.mainClass="your.main.ClassName"

## Usage

### User Roles

- **Admin**  
  - Manage **clients** (add, edit, delete).  
  - Manage **rooms** (add, edit, delete, update status).  
  - Manage **reservations** (create, edit, cancel).  
  - Handles day-to-day hotel operations.  

- **Super Admin**  
  - Has all Admin permissions.  
  - Additionally, can **create, manage, and remove Admin accounts**.  
  - Responsible for high-level user management and overall control of the system.  

---

### Common Workflows

#### Managing Clients
1. Log in as **Admin** or **Super Admin**.  
2. Navigate to the **Clients** section.  
3. Add a new client or update existing details.  

#### Managing Rooms
1. Go to the **Rooms** section.  
2. Add new rooms with number, type, and price.  
3. Update availability or modify existing room details.  

#### Managing Reservations
1. Navigate to the **Reservations** section.  
2. Create a reservation by selecting a client and an available room.  
3. Modify or cancel reservations as needed.  

#### Managing Admins (Super Admin only)
1. Log in as **Super Admin**.  
2. Navigate to the **Admin Management** section.  
3. Add new Admin accounts, edit their details, or remove them.


