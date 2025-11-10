# smart-library-system
Smart Library Book Borrowing System using Spring Boot (Group Project)

#Project Overview
The *Smart Library Book Borrowing & User Portal* is a web-based system that allows users to register, log in, browse and search books, reserve or borrow them online, and view their borrowing history.  
Administrators can manage books, monitor borrow records, and send notifications.  
This project demonstrates **Spring Boot (REST API)** integration with a frontend built using **HTML, CSS, and JavaScript**.

#Objectives
- Create a service-based book borrowing system (not a management system).
- Provide secure authentication with JWT (JSON Web Token).
- Enable borrowing and returning books via API.
- Include admin role for book and user management.
- Integrate email notifications for borrow confirmations.
- Deliver a complete full-stack project using GitHub collaboration.

# Key Features
| Feature | Description | Status |
|----------|--------------|--------|
| 👥 User Registration & Login | Secure authentication using JWT |  Completed |
| 🔐 Admin Role | Admin-only access to protected routes |  Completed |
| 📖 Book Management | Add, edit, delete, and search books |  Completed |
| 🔍 Search & Pagination | Find books by title, author, or category |  Completed |
| 📦 Borrow & Reserve | Borrow/return books with history tracking |  Completed |
| 📧 Email Notification | Send email when user borrows a book |  Completed |
| 📊 Admin Dashboard | Manage users, books, and borrow history |  Completed |
| 🌐 REST API | Backend API integration for frontend |  Completed |
| 🧩 Postman + GitHub + Documentation | For testing and version control |  Completed |


#Tech Stack
 🖥️ Backend
- Spring Boot 3.4.11
- Spring Data JPA / Hibernate
- Spring Security with JWT
- MySQL Database


# 💻 Frontend
- HTML, CSS, JavaScript
- Fetch API (REST calls)
- LocalStorage for token management

# 🧰 Tools
- IntelliJ IDEA
- GitHub (Dev, Feature, Production branches)
- Postman for API testing

#Project Structure
smart-library-system/
│
├── src/
│   ├── main/
│   │   ├── java/com/example/SmartLibrary/
│   │   │
│   │   │── SmartLibraryApplication.java
│   │   │
│   │   ├── config/
│   │   │   ├── PasswordConfig.java
│   │   │   └── SecurityConfig.java
│   │   │
│   │   ├── security/
│   │   │   ├── JwtUtil.java
│   │   │   ├── JwtAuthFilter.java
│   │   │   
│   │   │
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Book.java
│   │   │   └── Borrow.java
│   │   │   └── Admin.java
│   │   │       └── Notification.java
│   │   │
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── BookRepository.java
│   │   │   └── BorrowRepository.java
│   │   │   └── NotificationRepository.java
│   │   │   └── UserRepository.java
│   │   │   └── AdminRepository.java
│   │   │       
│   │   ├── service/
│   │   │   ├── NotificationBroadcastService.java
│   │   │   ├── BookService.java
│   │   │   ├── BorrowService.java
│   │   │   └── NotificationService.java            
│   │   │   └── UserService.java
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── BookController.java
│   │   │   ├── BorrowController.java
│   │   │   └── admin/
│   │   │       ├── AdminController.java
│   │   │       
│   │   │
│   │   └── util/
│   │       └── EmailService.java
│   │
│   └── resources/
│       ├── application.properties
│       ├── static/
│       │   ├── css/
│       │   │   └── style.css
│       │   ├── js/
│       │   │   ├── auth.js
│       │   │   ├── books.js
│       │   │   ├── admin.js
│       │   │   └── borrow.js
│       │   ├── login.html
│       │   ├── register.html
│       │   ├── books.html
│       │   ├── admin.html
│       │   └── add-book.html
│       └── templates/ (optional for Thymeleaf)
│
├── pom.xml
├── README.md
└── .gitignore


