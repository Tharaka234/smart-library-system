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
src/
├── main/
│   ├── java/com/example/sampleproject/
│   │   ├── config/          # Configuration files
│   │   ├── controller/      # REST API controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entity models
│   │   ├── repository/      # Database repositories
│   │   ├── security/        # Security configurations
│   │   ├── service/         # Business logic services
│   │   └── sampleprojectApplication.java  # Main application class
│   └── resources/
│       ├── static/          # Static web assets (HTML, CSS, JS)
│       └── application.properties  # Application configuration
└── test/                    # Unit and integration tests

# 👨‍💻 Team Members

| Member | Role / Task | Feature Branch |
|---------|--------------|----------------|
| *Tharaka (Leader)* | Authentication & Security | `feature-auth` |
| *Lakshan* | Book CRUD APIs | `feature-books` |
| *Teshani* | Frontend (HTML, CSS, JS) | `feature-frontend` |
| *Saranga* | Borrow/Return & Postman Tests | `feature-borrow` |

# 🔐 Authentication Flow

1. User registers → data stored in DB.  
2. On login → backend returns a **JWT token**.  
3. Token saved in browser `localStorage`.  
4. Each API call sends `Authorization: Bearer <token>` header.  
5. Admin-only endpoints validated by role:  
   `@PreAuthorize("hasRole('ADMIN')")`.

# 🧪 Testing APIs (Postman)
1. Run backend:  
   `mvn spring-boot:run`
2. Use these endpoints:

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/auth/register` | Register new user |
| `POST` | `/api/auth/login` | Login and get JWT |
| `GET` | `/api/books` | Get all books |
| `POST` | `/api/admin/books` | Admin create book |
| `POST` | `/api/borrows/borrow/{id}` | Borrow a book |
| `POST` | `/api/borrows/return/{id}` | Return a book |

⚙️ How to Run the Project
#1️⃣ Clone Repository
```bash
git clone https://github.com/Tharaka234/smart-library-system.git
#Database Tabel
https://nsbm365-my.sharepoint.com/:w:/g/personal/gylayapa_students_nsbm_ac_lk/IQAe3T13Y9EvT4cZkTqjJYs4Ad8jnjhicV4ZyfJuygrB350?e=5NOBJG

# 🔮 Future Improvements

- ⭐ **Book Reviews & Ratings** — Allow users to rate and review books.  
- 🔄 **Real-time Availability Updates** — Automatically update when books are borrowed or returned.  
- 🔐 **Forgot Password with Email OTP** — Add password recovery using email-based OTP verification.  
- 📖 **Interactive Book Preview (Popup Reader)** — When a user clicks a book, open a popup window that shows the book’s content or a preview for online reading.  
- 🆔 **View User ID Option** — Allow users to view their own User ID in a dedicated section. This feature was planned but not implemented due to time limitations.  


# 💬 Acknowledgements

Special thanks to my team members for their valuable guidance, support,  
and collaboration throughout the development of this full-stack project.

*Team Members:*
- 👨‍💻 Tharaka Isuru (Team Leader – Authentication & Security)
- 📚 Lakshan Aroshana (Book Management)
- 💻 Teshani (Frontend Development)
- 📦 Saranga (Borrow/Return & Postman Testing)
