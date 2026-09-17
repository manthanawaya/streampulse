# StreamPulse - Problem Statement

## 1. Problem Statement
University course evaluation in "Programming in Java" requires building a solid understanding of fundamental and advanced Java concepts. Students need a comprehensive project to demonstrate proficiency in Object-Oriented Programming (OOP), Collections Framework, Concurrency, Exception Handling, File I/O, Database connectivity (JDBC), and modern Java features (Streams API). 

StreamPulse serves as this capstone project. It is a media catalog and recommendation engine simulating real-world streaming platform backends, designed to evaluate the practical application of these Java concepts.

## 2. Project Scope
The project focuses exclusively on backend architecture, logic, and a CLI interface.

**In-Scope:**
- Modeling a domain using OOP principles (Inheritance, Polymorphism, Abstraction, Encapsulation).
- Managing data in memory using appropriate data structures (Lists, Maps, Sets).
- Parsing external data from CSV (File I/O) and persisting user state/watchlists in a relational database (SQLite via JDBC).
- Leveraging multithreading (`ExecutorService`) for computationally heavy operations (Cosine Similarity for recommendations).
- Implementing robust exception handling for I/O and database operations.
- Exposing functionality through an interactive command-line interface (CLI).

**Out of Scope:**
- Graphical User Interface (GUI) or Web Frontend.
- Network-based client-server architecture (REST APIs).
- Advanced user authentication and authorization.

## 3. Target Users
- **Instructors/Evaluators:** To assess and grade Java programming skills based on code structure, efficiency, and adherence to requirements.
- **End Users (Simulated):** Users who interact with the CLI to browse a media catalog, rate items, manage a watchlist, and generate personalized recommendations.

## 4. High-Level Features
1. **Catalog Management:** Read and parse media items (Movies and Series) from a CSV seed file into memory using proper File I/O and Exception Handling.
2. **Watchlist Persistence:** Add and remove media items to a personal, ordered watchlist, persisting the state to an SQLite database.
3. **Rating System:** Allow users to rate media items from 1.0 to 10.0, storing ratings in memory using Collections (Maps).
4. **Recommendation Engine:** Multithreaded recommendation system calculating Cosine Similarity between user preferences (based on genres of highly rated items) and the unrated catalog.
5. **Analytics:** Java Streams API based metrics including total watch time, favorite genres, and average ratings.

## 5. Non-Functional Requirements & Constraints
- **Performance:** The recommendation engine must calculate similarities in parallel to minimize wait times.
- **Reliability:** The application must not crash on invalid user input or missing files; it must use comprehensive `try-catch` blocks and custom exceptions.
- **Design:** Code must strictly follow SOLID principles and make use of appropriate design patterns (e.g., Singleton for database connections).
- **Dependencies:** Core logic must be implemented using standard Java libraries (`java.util`, `java.io`, `java.sql`, `java.util.concurrent`). External dependencies should be limited to SQLite JDBC driver and JUnit for testing.

## 6. Success Criteria / Deliverables
- A fully functional CLI application meeting all high-level features.
- Clean, well-documented code with appropriate use of comments and JavaDoc.
- Passing unit tests demonstrating code correctness.
- Proper application of all required Java concepts (OOP, Collections, Concurrency, File I/O, DB, Exceptions).
