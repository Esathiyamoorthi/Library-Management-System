                   Library Management System (REST API)

  A backend REST API for managing library users,books and book issuing, built with Java, 
  Spring Data JPA/Hibernate and MySQL.

------------------------------------------------------------------------------------------------------------------------

              Table of contents

   1 Project overview
   2 Technologies
   3 Database setup
   4 Application configuration
   5 How to run
   6 Project structure and architecture
   7 API list
   8 Sample requests and responses
   9 Validation and error handling
   10 Database design
   11 Business rules and design decisions
   12 Postman collection and Swagger

------------------------------------------------------------------------------------------------------------------------

                        1. Project overview

  The application lets a library:

    * manage users (members) with an ACTIVE / INACTIVE status,
    * manage books, including how many copies exist and how many are on the shelf,
    * search books by title, author, category and price range (filters can be combined),
    * paginate and sort book lists,
    * issue a book to a user and return it,
      keeping the number of available copies consistent through a database transaction,
    * list the books currently issued to a user,
    * list overdue books.
     
     The code follows the layering Controller → Service → Repository → Database. 
     Controllers never call repositories, and JPA entities are never exposed directly: 
     all requests and responses use DTOs.  

------------------------------------------------------------------------------------------------------------------------
                         
                         2. Technologies

    |-----------------------------------------------------------------------------|
    |  Technology               |	           Purpose                            | 
    |---------------------------|-------------------------------------------------|
    |   Java 17+                | 	    Language                                  |
    |   SpringBoot4.x           |	    Application framework                     |    
    |   Spring Web              |       REST controllers                          |
    |   SpringDataJPA/Hibernate |	    Database access and ORM                   |
    |   MYSQL                   |	    Database (no in-memory database is used)  |
    |   Jakarta Bean Validation |	    Request validation                        |
    |   Lombok                  |	    Removes getter/setter boilerplate         |
    |   springdoc-openapi       |	    Swagger UI / OpenAPI documentation        |
    |   Maven                   |	    Build tool                                |
    |   Postman                 |	    API testing                               |
    |---------------------------|-------------------------------------------------|

------------------------------------------------------------------------------------------------------------------------

                     3. Database setup

   a. Install PostgreSQL and make sure it is running (default port 5432).
   b. Create the database:
               |-----------------------------|
               |sql                          |
               |CREATE DATABASE library_db;  |
               |_____________________________|

   c. No tables need to be created by hand.Hibernate creates them on the first start 
      (spring.jpa.hibernate.ddl-auto=update). The created tables are users, 
       books and book_issues (see Database design).

------------------------------------------------------------------------------------------------------------------------

                   4. Application configuration

   File: src/main/resources/application.properties
     |------------------------------------------------------------------------|
     | properties                                                             |
     | spring.application.name=library                                        |
     |                                                                        |
     | # PostgreSQL connection (change the username and password to yours)    |
     | spring.datasource.url=jdbc:postgresql://localhost:5432/library_db      |
     | spring.datasource.username=postgres                                    |
     | spring.datasource.password=YOUR_PASSWORD                               |
     |                                                                        |
     | # Hibernate creates/updates the tables from the entities               |
     | spring.jpa.hibernate.ddl-auto=update                                   |
     | spring.jpa.show-sql=true                                               |
     | spring.jpa.open-in-view=false                                          |
     |                                                                        |
     | # Pagination limits                                                    |
     | spring.data.web.pageable.default-page-size=10                          |
     | spring.data.web.pageable.max-page-size=50                              |
     |________________________________________________________________________|

________________________________________________________________________________________________________________________


                      5. How to run

 Prerequisites: JDK 17 or newer, Maven, and a running PostgreSQL with the library_db database.

 |-------------------------------------------|
 |   bash                                    |
 |   # from the project root                 |
 |   mvn spring-boot:run                     |
 |___________________________________________|

  or build a jar and run it:
 
 |-----------------------------------------------|
 |  bash                                         |
 |  mvn clean package                            |
 |  java -jar target/library-0.0.1-SNAPSHOT.jar  |
 |_______________________________________________|

 Once started:

 |---------------|------------------------------------------------|
 | What	         |        URL                                     |
 |---------------|------------------------------------------------|
 | API base URL  | http://localhost:8080/api                      |
 | Swagger UI	 | http://localhost:8080/swagger-ui/index.html    |
 | OpenAPI JSON	 | http://localhost:8080/v3/api-docs              |
 |_______________|________________________________________________|

------------------------------------------------------------------------------------------------------------------------

            6. Project structure and architecture
 
 |-------------------------------------------------------------------------------------------|
 | com.example.library                                                                       |
 |   ├── controller      REST endpoints (HTTP in, HTTP out)                                  |
 |   ├── service         Service interfaces                                                  |
 |   │   └── impl        Business logic and transactions                                     |
 |   ├── repository      Spring Data JPA repositories, JPA specifications, custom queries    |
 |   ├── entity          JPA entities and enums (User, Book, BookIssue, statuses)            |
 |   ├── dto             Request and response objects, PageResponse                          |
 |   ├── mapper          Entity <-> DTO conversion                                           |
 |   ├── exception       Custom exceptions and the global exception handler                  |
 |   └── config          OpenAPI (Swagger) configuration                                     |
 |___________________________________________________________________________________________|

  Request flow

  |----------------------------------------------------------------------------------|
  | Client -> Controller (validation) -> Service (business rules, @Transactional)    |
  | -> Repository -> PostgreSQL                                                      |
  | <- Mapper converts the entity to a response DTO <-                               |
  |__________________________________________________________________________________|

------------------------------------------------------------------------------------------------------------------------

                         7.API list
   
   Users

|--------- |-------------------------|--------------------------------------------------------------|
|   Method |	URL                  |	Description                                                 |
|   POST   | /api/users              |	Create a user                                               |
|   GET    | /api/users              |	List all users                                              |
|   GET	   | /api/users/{id}         |	Get one user                                                |
|   PUT	   | /api/users/{id}         |	Replace a user (all required fields)                        |
|   PATCH  | /api/users/{id}         |	Update only the fields that are sent                        |
|   DELETE | /api/users/{id}         |	Delete a user (blocked if the user has borrowing history)   |
|   GET	   |/api/users/{userId}/books|	Books currently issued to the user                          |
|__________|_________________________|______________________________________________________________|

 Books

 |----------|------------------|----------------------------------------------------|
 |   Method |	URL            | 	Description                                     |
 |----------|------------------|----------------------------------------------------|
 |   POST   |	/api/books     |	Create a book                                   |
 |   GET    |	/api/books     |	Search, filter, paginate and sort books         |
 |   GET    |  /api/books/{id} |	Get one book                                    |
 |   PUT    |  /api/books/{id} |	Replace a book (all required fields)            |
 |   PATCH  |  /api/books/{id} |	Update only the fields that are sent            |
 |   DELETE | /api/books/{id}  | Delete a book (blocked if it has ever been issued) |
 |__________|__________________|____________________________________________________|

  Query parameters for GET /api/books (all optional and combinable)

 |----------|---------------------------------------------------------------|
 | Parameter|	Meaning	Example                                             |
 |----------|---------------------------------------------------------------|
 | title    |	Title contains (case-insensitive)	?title=java             |
 | author   |	Author contains (case-insensitive)	?author=martin          |
 | category |	Category equals (case-insensitive)	?category=programming   |
 | minPrice |	Price is at least	?minPrice=300                           |
 | maxPrice |	Price is at most	?maxPrice=800                           |
 | page     |	Page number, starting at 0	?page=0                         |
 | size     |	Items per page (default 10, maximum 50)	?size=10            |
 | sort     |	Sort field and direction	?sort=price,desc                |
 |__________|_______________________________________________________________|

 Example combining everything: 
 GET /api/books?title=java&category=programming&maxPrice=800&page=0&size=5&sort=price,desc
 Book issues:
 |---------|-----------------------------|---------------------------------------------| 
 | Method  |	URL                      |	Description                                |
 |---------|-----------------------------|---------------------------------------------|
 | POST    | /api/book-issues            |	Issue a book to a user                     |
 | PUT     | /api/book-issues/{id}/return|	Return a book                              |
 | GET     | /api/book-issues/overdue    |	Issues past their due date and not returned|
 |_________|_____________________________|_____________________________________________|

------------------------------------------------------------------------------------------------------------------------

      8. Sample requests and responses

  Create a user:
  POST /api/users
  |------------------------------| 
  | json:                        |
  | {                            |
  | "name": "Rahul Sharma",      | 
  | "email": "rahul@example.com",|
  | "phone": "9876543210",       |
  | "address": "Gurugram",       |
  | "dateOfBirth": "1995-06-15"  |
  | }                            |
  |______________________________|

 Response 201 Created:
  |--------------------------------------------|
  | json                                       |
  | {                                          |
  | "id": 1,                                   |
  | "name": "Rahul Sharma",                    |
  | "email": "rahul@example.com",              |
  | "phone": "9876543210",                     |   
  | "address": "Gurugram",                     |
  | "dateOfBirth": "1995-06-15",               |
  | "status": "ACTIVE",                        |
  | "createdAt": "2026-09-20T10:15:30.123456", |
  | "updatedAt": "2026-09-20T10:15:30.123456"  |
  | }                                          |
  |____________________________________________|

 Update only the address of a user:
 PATCH /api/users/1
  |-------------------------|
  | json                    |
  | { "address": "Delhi" }  |
  |_________________________|

Response 200 OK: the full user, with only address (and updatedAt) changed.

 Create a book:
 POST /api/books
  |------------------------------------------------------|
  | json                                                 |
  | {                                                    |
  | "title": "Clean Code",                               |
  | "author": "Robert Martin",                           |
  | "isbn": "9780132350884","category": "Programming",   |
  | "price": 450.00,                                     |
  | "totalCopies": 5                                     |
  | }                                                    |
  |______________________________________________________|


 Response 201 Created 
 (availableCopies is set by the system and equals totalCopies):
 |---------------------------------------------| 
 | json                                        |
 | {                                           | 
 | "id": 1,                                    |
 | "title": "Clean Code",                      |  
 | "author": "Robert Martin",                  |
 | "isbn": "9780132350884",                    |
 | "category": "Programming",                  |
 | "price": 450.00,                            |
 | "totalCopies": 5,                           |
 | "availableCopies": 5,                       |
 | "status": "ACTIVE",                         | 
 | "createdAt": "2026-09-20T10:20:00.000000",  |
 | "updatedAt": "2026-09-20T10:20:00.000000"   |
 | }                                           |
 |_____________________________________________|


 Search books with paging and sorting
 GET /api/books?category=programming&page=0&size=2&sort=price,desc
 Response 200 OK:

 |--------------------------------------------------------------------------|
 | json                                                                     |
 | {                                                                        |
 | "content": [                                                             |
 | { "id": 4, "title": "Java Basics", "price": 900.00, "...": "..." },      |
 | { "id": 2, "title": "Effective Java", "price": 600.00, "...": "..." }    |
 | ],                                                                       |
 | "page": 0,                                                               |
 | "size": 2,                                                               |
 | "totalElements": 3,                                                      |
 | "totalPages": 2,                                                         |
 | "last": false                                                            |
 | }                                                                        |
 |__________________________________________________________________________|


 Issue a book:
 POST /api/book-issues
 |------------------------------| 
 | json                         |
 | { "userId": 1, "bookId": 1 } |
 |______________________________|


 Response 201 Created 
 (the issue date is today, and the due date is calculated automatically, 14 days later):
 |-----------------------------|         
 | json                        | 
 | {                           |
 | "id": 1,                    |
 | "userId": 1,                |
 | "userName": "Rahul Sharma", |
 | "bookId": 1,                |
 | "bookTitle": "Clean Code",  |
 | "issueDate": "2026-09-20",  |
 | "dueDate": "2026-10-04",    |
 | "returnDate": null,         |
 | "status": "ISSUED"          |  
 | }                           |
 |_____________________________|
 The book's availableCopies decreases by 1.

 Return a book
 PUT /api/book-issues/1/return (no request body)
 Response 200 OK:
 |------------------------------|
 | json                         |
 | {                            |
 | "id": 1,                     |
 | "userId": 1,                 |
 | "userName": "Rahul Sharma",  |
 | "bookId": 1,                 |
 | "bookTitle": "Clean Code",   | 
 | "issueDate": "2026-09-20",   |
 | "dueDate": "2026-10-04",     | 
 | "returnDate": "2026-09-25",  |
 | "status": "RETURNED"         | 
 | }                            | 
 |______________________________|
The book's availableCopies increases by 1.


 Books currently issued to a user:
 GET /api/users/1/books 
 returns a list of issues with bookTitle, issueDate, dueDate and status, for books that have not been returned.


 Overdue books:
 GET /api/book-issues/overdue returns issues whose dueDate is before today and whose status is ISSUED. 
 It returns [] when nothing is overdue.

 Error response example
 GET /api/users/10 (user does not exist) returns 404 Not Found:
 |--------------------------------------------| 
 | json                                       |
 | {                                          |
 | "timestamp": "2026-09-20T10:30:00.000000", |
 | "status": 404,                             |
 | "message": "User not found with id: 10",   |
 | "path": "/api/users/10"                    |  
 |}                                           |
 |____________________________________________|

------------------------------------------------------------------------------------------------------------------------
   
                9.Validation and error handling

    Validation (Jakarta Bean Validation)
 |----------------------|------------------------------------|
 | Field                |	Rule                             |
 |----------------------|------------------------------------|
 | User name            |	Required                         |
 | User email           |	Required, valid email, unique    |
 | User phone           |	Required, exactly 10 digits      |
 | User dateOfBirth     |	Cannot be in the future          |
 | Book title           |	Required                         |
 | Book isbn            |	Required, unique                 |
 | Book price           |	Required, greater than 0         |
 | Book totalCopies     |	Required, greater than 0         |
 | Issue userId, bookId	|  Required                          |
 |______________________|____________________________________|
 For PATCH, every field is optional, but a value that is sent must still be valid.

 Global exception handling (@RestControllerAdvice)
 |----------------------------|------|------------------------------------------------------------------------------|
 | Exception                  |	HTTPstatus | 	When                                                                |
 |----------------------------|------------|------------------------------------------------------------------------|
 | UserNotFoundException	  | 404	       | User id does not exist                                                 |  
 | BookNotFoundException	  | 404	       | Book id does not exist                                                 |
 | BookIssueNotFoundException |	404	       | Issue id does not exist                                                |
 | BookNotAvailableException  |	409	       | No copies left, or the book is not ACTIVE                              |
 | DuplicateResourceException |	409	       | Email or ISBN already exists                                           |
 | ResourceInUseException	  | 409	       | Deleting a user or book that has borrowing history                     |
 | InvalidBookIssueException  | 400	       | Inactive user, or a book issue that is already returned                |  
 | InvalidRequestException    |	400	       | A business rule is broken (for example minPrice greater than maxPrice) |
 | Validation failure	      | 400	       | A @Valid rule failed                                                   |
 |____________________________|____________|________________________________________________________________________|
Malformed JSON / bad parameter type	400	Unreadable body, or a wrong type in the URL
Any other exception	500	Unexpected server error

------------------------------------------------------------------------------------------------------------------------

         10. Database design

  Relationships: 
   User 1 --- * BookIssue * --- 1 Book. One user can have many issues, 
  one book can appear in many issues, and each issue belongs to exactly one user and one book.

       |-------|                                   |---------|
       | users |                                   | books   |  
       |_______|                                   |_________|     
           |                                            |
           |                                            | 
          borrow                                  is borrowed in
           |         |--------------|                   |
           |<-_______| book_issue   |<-_________________|
                     |______________|



 Tables:

  * users: status is ACTIVE or INACTIVE. email is unique.
  * books: status is ACTIVE or INACTIVE. isbn is unique. available_copies is never greater than total_copies.
  * book_issues: status is ISSUED, RETURNED or OVERDUE. 
     return_date is empty until the book is returned. user_id and book_id are foreign keys.

 JPA mapping: BookIssue has @ManyToOne (lazy) to User and Book;
              User and Book have @OneToMany(mappedBy = ...) back to BookIssue.

 Indexes: book_issues(user_id), book_issues(book_id), book_issues(status, due_date)
          and books(category), in addition to the automatic indexes on primary keys and unique columns.

------------------------------------------------------------------------------------------------------------------------

                   11.Business rules and design decisions

   Issuing a book:
     * The user must exist and be ACTIVE.
     * The book must exist, be ACTIVE, and have availableCopies > 0.
     * availableCopies decreases by 1, issueDate is today, and dueDate is calculated automatically 
       (loan period: 14 days).
     * All of this happens in one @Transactional method, so either every change is saved or none is.

  Returning a book:
     * The issue must exist and must not already be RETURNED.
     * returnDate is set to today, status becomes RETURNED, and availableCopies increases by 1, in one transaction.

  Changing totalCopies:
    * The number of copies currently lent out is totalCopies - availableCopies.
    * The new availableCopies is newTotal - lentOut. If newTotal is lower than lentOut, the request is rejected.

  Other decisions:
    * Overdue is calculated from the dates (dueDate < today and status ISSUED),
       so it is always accurate without a scheduled job.
    * A user or book with borrowing history cannot be deleted,
      so the history is never lost. Set the status to INACTIVE instead.
    * Book search uses JPA Specifications,
      so any combination of filters works with paging and sorting.
    * JOIN FETCH queries avoid the N+1 query problem when listing issues.
    * Known limitation: two requests borrowing the very last copy at the same instant could race.
  A production version would add optimistic locking (@Version) or a database lock.

------------------------------------------------------------------------------------------------------------------------

               12.Postman collection and Swagger
   * Postman: import library-management-system.postman_collection.json. 
     Run the folders in order: Users, Books, Book Issues, Error Cases.
     Ids are stored automatically in collection variables, and each request contains a status-code check.
   * Swagger UI: http://localhost:8080/swagger-ui/index.html. Every endpoint can be tested from the browser.


   Content: Spring Boot + MySQL Project Assignment 
   Project: Library Management System 
   Objective: Build a complete REST API application from scratch using Java, Spring Boot,
              Spring Data JPA/Hibernate, MySQL, Maven, and Postman. 
   





