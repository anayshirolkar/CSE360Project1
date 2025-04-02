package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List; // Import the List interface
import java.util.Map;

import HW2.User;
import HW2.Review; // Import the Review class
import HW2.Message; // Import the Message class
import HW2.TrustedReviewer; // Import the TrustedReviewer class
import HW2.Question; // Import the Question class
import HW2.Answer; // Import the Answer class

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
			createReviewerTables(); // Create the necessary tables for reviewer functionality if they don't exist
			migrateDatabase(); // Ensure database schema is up to date
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    // Create the trusted reviewers table
	    String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trusted_reviewers ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "student_username VARCHAR(255), "
	            + "reviewer_username VARCHAR(255), "
	            + "weight INT DEFAULT 5, "
	            + "UNIQUE (student_username, reviewer_username))";
	    statement.execute(trustedReviewersTable);
	    
	    // Create the reviewer requests table - moved from createReviewerTables
	    String reviewerRequestsTable = "CREATE TABLE IF NOT EXISTS reviewer_requests ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userId VARCHAR(255), "
	            + "requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "status VARCHAR(20) DEFAULT 'PENDING')";  // PENDING, APPROVED, REJECTED
	    statement.execute(reviewerRequestsTable);
	    
	    // Create the questions table
	    String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
	            + "id VARCHAR(36) PRIMARY KEY, "
	            + "title VARCHAR(255), "
	            + "description TEXT, "
	            + "author VARCHAR(255), "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
	    statement.execute(questionsTable);
	    
	    // Create the answers table
	    String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
	            + "id VARCHAR(36) PRIMARY KEY, "
	            + "questionId VARCHAR(36), "
	            + "answerText TEXT, "
	            + "author VARCHAR(255), "
	            + "parentAnswerId VARCHAR(36), "
	            + "createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
	            + "updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
	    statement.execute(answersTable);
	}

    /**
     * Creates tables needed for reviewer functionality.
     */
    private void createReviewerTables() throws SQLException {
        // Create the reviews table
        String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews ("
                + "id VARCHAR(36) PRIMARY KEY, "
                + "reviewer VARCHAR(255), "
                + "content TEXT, "
                + "associatedId VARCHAR(36), "
                + "createdAt TIMESTAMP, "
                + "updatedAt TIMESTAMP)";
        statement.execute(reviewsTable);
        
        // Create the messages table
        String messagesTable = "CREATE TABLE IF NOT EXISTS messages ("
                + "id VARCHAR(36) PRIMARY KEY, "
                + "sender VARCHAR(255), "
                + "recipient VARCHAR(255), "
                + "content TEXT, "
                + "sentAt TIMESTAMP)";
        statement.execute(messagesTable);
    }

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

	/**
	 * Executes a database update query.
	 */
	public void executeUpdate(String sql) throws SQLException {
	    Statement stmt = connection.createStatement();
	    stmt.executeUpdate(sql);
	    stmt.close();
	}

	/**
	 * Gets the database connection.
	 */
	public Connection getConnection() {
	    return this.connection;
	}

	/**
	 * Requests reviewer status for a student.
	 */
    public void submitReviewerRequestById(String userId) throws SQLException {
        String query = "INSERT INTO reviewer_requests (userId) VALUES (?)";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, userId);
        stmt.executeUpdate();
        stmt.close();
    }

	/**
	 * Approves a reviewer request.
	 */
	public void approveReviewerRequest(String userId) throws SQLException {
	    // Update user role
	    String updateQuery = "UPDATE users SET role = 'REVIEWER' WHERE userId = ?";
	    PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
	    updateStmt.setString(1, userId);
	    updateStmt.executeUpdate();
	    updateStmt.close();
	    
	    // Remove from requests
	    String deleteQuery = "DELETE FROM reviewer_requests WHERE userId = ?";
	    PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
	    deleteStmt.setString(1, userId);
	    deleteStmt.executeUpdate();
	    deleteStmt.close();
	}

    /**
     * Gets all reviews by a specific reviewer.
     */
    public java.util.List<Review> getReviewsByReviewer(String reviewer) throws SQLException {
        java.util.List<Review> reviewList = new java.util.ArrayList<>();
        String query = "SELECT * FROM reviews WHERE reviewer = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewer);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getString("reviewer"),
                    rs.getString("content"),
                    rs.getString("associatedId")
                );
                
                // In a real implementation, you would also set the createdAt and updatedAt fields
                
                reviewList.add(review);
            }
        }
        
        return reviewList;
    }
    
    /**
     * Adds a new review to the database.
     */
    public void addReview(Review review) throws SQLException {
        String query = "INSERT INTO reviews (id, reviewer, content, associatedId, createdAt, updatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, review.getId());
            pstmt.setString(2, review.getReviewer());
            pstmt.setString(3, review.getContent());
            pstmt.setString(4, review.getAssociatedId());
            pstmt.setTimestamp(5, new java.sql.Timestamp(review.getCreatedAt().getTime()));
            pstmt.setTimestamp(6, new java.sql.Timestamp(review.getUpdatedAt().getTime()));
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Updates an existing review in the database.
     */
    public void updateReview(String reviewId, String newContent) throws SQLException {
        String query = "UPDATE reviews SET content = ?, updatedAt = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newContent);
            pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(3, reviewId);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a review from the database.
     */
    public void deleteReview(String reviewId) throws SQLException {
        String query = "DELETE FROM reviews WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Sends a message and stores it in the database.
     */
    public void sendMessage(Message message) throws SQLException {
        String query = "INSERT INTO messages (id, sender, recipient, content, sentAt) "
                + "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message.getId());
            pstmt.setString(2, message.getSender());
            pstmt.setString(3, message.getRecipient());
            pstmt.setString(4, message.getContent());
            pstmt.setTimestamp(5, new java.sql.Timestamp(message.getSentAt().getTime()));
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Gets all messages for a specific recipient.
     */
    public java.util.List<Message> getMessagesForRecipient(String recipient) throws SQLException {
        java.util.List<Message> messageList = new java.util.ArrayList<>();
        String query = "SELECT * FROM messages WHERE recipient = ? ORDER BY sentAt DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, recipient);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getString("sender"),
                    rs.getString("recipient"),
                    rs.getString("content")
                );
                
                messageList.add(message);
            }
        }
        
        return messageList;
    }

    /**
     * Adds a reviewer to the student's trusted reviewers list
     */
    public void addTrustedReviewer(String studentUsername, String reviewerUsername, int weight) throws SQLException {
        String query = "INSERT INTO trusted_reviewers (student_username, reviewer_username, weight) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            pstmt.setString(2, reviewerUsername);
            pstmt.setInt(3, weight);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // If it's a duplicate entry, update the weight instead
            if (e.getErrorCode() == 23505) { // Duplicate key violation
                updateTrustedReviewerWeight(studentUsername, reviewerUsername, weight);
            } else {
                throw e;
            }
        }
    }

    /**
     * Updates the weight of a trusted reviewer
     */
    public void updateTrustedReviewerWeight(String studentUsername, String reviewerUsername, int weight) throws SQLException {
        String query = "UPDATE trusted_reviewers SET weight = ? WHERE student_username = ? AND reviewer_username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, weight);
            pstmt.setString(2, studentUsername);
            pstmt.setString(3, reviewerUsername);
            pstmt.executeUpdate();
        }
    }

    /**
     * Removes a reviewer from the student's trusted reviewers list
     */
    public void removeTrustedReviewer(String studentUsername, String reviewerUsername) throws SQLException {
        String query = "DELETE FROM trusted_reviewers WHERE student_username = ? AND reviewer_username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            pstmt.setString(2, reviewerUsername);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets all trusted reviewers for a specific student
     */
    public java.util.List<TrustedReviewer> getTrustedReviewers(String studentUsername) throws SQLException {
        java.util.List<TrustedReviewer> reviewersList = new java.util.ArrayList<>();
        String query = "SELECT * FROM trusted_reviewers WHERE student_username = ? ORDER BY weight DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TrustedReviewer reviewer = new TrustedReviewer(
                    rs.getString("reviewer_username"),
                    studentUsername,
                    rs.getInt("weight")
                );
                reviewersList.add(reviewer);
            }
        }
        
        return reviewersList;
    }

    /**
     * Gets reviews from trusted reviewers only
     */
    public java.util.List<Review> getReviewsFromTrustedReviewers(String studentUsername, String associatedId) throws SQLException {
        java.util.List<Review> reviewsList = new java.util.ArrayList<>();
        String query = 
            "SELECT r.* FROM reviews r " +
            "JOIN trusted_reviewers tr ON r.reviewer = tr.reviewer_username " +
            "WHERE tr.student_username = ? AND r.associatedId = ? " +
            "ORDER BY tr.weight DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            pstmt.setString(2, associatedId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getString("reviewer"),
                    rs.getString("content"),
                    rs.getString("associatedId")
                );
                
                // Set additional properties if available
                if (rs.getObject("rating") != null) {
                    review.setRating(rs.getInt("rating"));
                }
                
                reviewsList.add(review);
            }
        }
        
        return reviewsList;
    }

    /**
     * Submit a request to become a reviewer
     */
    public void submitReviewerRequest(String username) throws SQLException {
        // First, check if the reviewer_requests table exists, and create it if not
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS reviewer_requests (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "userId VARCHAR(50) NOT NULL, " +
                "requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "status VARCHAR(20) DEFAULT 'PENDING')"
            );
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error creating reviewer_requests table: " + e.getMessage());
            // If first attempt fails, try an alternative syntax for different databases
            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS reviewer_requests (" +
                    "id INTEGER PRIMARY KEY, " +
                    "userId VARCHAR(50) NOT NULL, " +
                    "requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "status VARCHAR(20) DEFAULT 'PENDING')"
                );
                stmt.close();
            } catch (SQLException ex) {
                System.err.println("Second attempt failed: " + ex.getMessage());
                throw e; // Re-throw if both attempts fail
            }
        }

        // Now proceed with the insert/update logic
        // Check if a request already exists for the user
        String checkQuery = "SELECT * FROM reviewer_requests WHERE userId = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Request already exists
                try {
                    String status = rs.getString("status");
                    if ("PENDING".equals(status)) {
                        throw new SQLException("You already have a pending request");
                    } else if ("REJECTED".equals(status)) {
                        // Update the existing request to PENDING
                        String updateQuery = "UPDATE reviewer_requests SET status = 'PENDING', requestDate = CURRENT_TIMESTAMP WHERE userId = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, username);
                            updateStmt.executeUpdate();
                        }
                    } else if ("APPROVED".equals(status)) {
                        throw new SQLException("You are already a reviewer");
                    }
                } catch (SQLException e) {
                    // If there's an error getting the status column, try to create it
                    if (e.getMessage().contains("status") || e.getMessage().contains("Column")) {
                        // Add status column if it doesn't exist
                        try {
                            Statement alterStmt = connection.createStatement();
                            alterStmt.executeUpdate(
                                "ALTER TABLE reviewer_requests ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING'"
                            );
                            alterStmt.close();
                            
                            // Update the existing record to set the status
                            String updateQuery = "UPDATE reviewer_requests SET status = 'PENDING', requestDate = CURRENT_TIMESTAMP WHERE userId = ?";
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, username);
                                updateStmt.executeUpdate();
                            }
                        } catch (SQLException ex) {
                            System.err.println("Error updating table: " + ex.getMessage());
                            // If altering table fails, try with the original query without checking status
                            String updateQuery = "UPDATE reviewer_requests SET requestDate = CURRENT_TIMESTAMP WHERE userId = ?";
                            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, username);
                                updateStmt.executeUpdate();
                            }
                        }
                    } else {
                        throw e; // Re-throw if it's not a column issue
                    }
                }
            } else {
                // Create a new request
                try {
                    // Try with status column first
                    String insertQuery = "INSERT INTO reviewer_requests (userId, status, requestDate) VALUES (?, 'PENDING', CURRENT_TIMESTAMP)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, username);
                        insertStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    // If there's an error with the status column on insert
                    if (e.getMessage().contains("status") || e.getMessage().contains("Column")) {
                        // Try without specifying the status column
                        String insertQuery = "INSERT INTO reviewer_requests (userId, requestDate) VALUES (?, CURRENT_TIMESTAMP)";
                        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                            insertStmt.setString(1, username);
                            insertStmt.executeUpdate();
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    /**
     * Get all pending reviewer requests
     */
    public java.util.List<java.util.Map<String, Object>> getPendingReviewerRequests() throws SQLException {
        java.util.List<java.util.Map<String, Object>> requestsList = new java.util.ArrayList<>();
        
        try {
            // First try a simpler query without JOIN that will always work
            String query = "SELECT * FROM reviewer_requests WHERE status = 'PENDING' OR status IS NULL ORDER BY requestDate ASC";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    java.util.Map<String, Object> request = new java.util.HashMap<>();
                    request.put("id", rs.getInt("id"));
                    request.put("userId", rs.getString("userId"));
                    request.put("requestDate", rs.getTimestamp("requestDate"));
                    requestsList.add(request);
                    
                    // Print for debugging
                    System.out.println("Found pending request for: " + rs.getString("userId"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error in getPendingReviewerRequests: " + e.getMessage());
            e.printStackTrace();
            
            // Try fallback with no WHERE clause
            String fallbackQuery = "SELECT * FROM reviewer_requests";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(fallbackQuery)) {
                
                while (rs.next()) {
                    java.util.Map<String, Object> request = new java.util.HashMap<>();
                    request.put("id", rs.getInt("id"));
                    request.put("userId", rs.getString("userId"));
                    request.put("requestDate", rs.getTimestamp("requestDate"));
                    String status = rs.getString("status");
                    request.put("status", status != null ? status : "PENDING");
                    requestsList.add(request);
                }
            } catch (SQLException ex) {
                System.err.println("Fallback query also failed: " + ex.getMessage());
                throw ex;
            }
        }
        
        return requestsList;
    }

    /**
     * Approve a reviewer request
     */
    public void approveReviewerRequestByUsername(String username) throws SQLException {
        System.out.println("Attempting to approve reviewer request for: " + username);
        
        // First check if the user exists
        String checkUserQuery = "SELECT * FROM cse360users WHERE userName = ?";
        boolean userExists = false;
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery)) {
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            userExists = rs.next();
        }
        
        if (!userExists) {
            throw new SQLException("User not found: " + username);
        }
        
        // Update the user's role to reviewer
        String updateRoleQuery = "UPDATE cse360users SET role = 'reviewer' WHERE userName = ?";
        try (PreparedStatement updateRoleStmt = connection.prepareStatement(updateRoleQuery)) {
            updateRoleStmt.setString(1, username);
            int rowsAffected = updateRoleStmt.executeUpdate();
            
            if (rowsAffected == 0) {
                System.err.println("No rows affected when updating user role");
                throw new SQLException("Failed to update user role");
            } else {
                System.out.println("Updated role for user: " + username + ", rows affected: " + rowsAffected);
            }
        }
        
        // Update the request status
        String updateRequestQuery = "UPDATE reviewer_requests SET status = 'APPROVED' WHERE userId = ?";
        try (PreparedStatement updateRequestStmt = connection.prepareStatement(updateRequestQuery)) {
            updateRequestStmt.setString(1, username);
            int rowsAffected = updateRequestStmt.executeUpdate();
            System.out.println("Updated request status, rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
            // Don't throw error here, as the role was already updated
        }
    }

    /**
     * Reject a reviewer request
     */
    public void rejectReviewerRequest(String username) throws SQLException {
        String query = "UPDATE reviewer_requests SET status = 'REJECTED' WHERE userId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }

    /**
     * Gets all questions created by a specific user
     */
    public List<Question> getQuestionsByUser(String username) throws SQLException {
        List<Question> questionsList = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE author = ? ORDER BY createdAt DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getString("title"),
                    rs.getString("description"),
                    username
                );
                // Set the ID to match what's in the database
                question.setId(rs.getString("id"));
                
                // If your Question class stores timestamps, set them here
                // question.setCreatedAt(rs.getTimestamp("createdAt"));
                // question.setUpdatedAt(rs.getTimestamp("updatedAt"));
                
                questionsList.add(question);
            }
        }
        
        return questionsList;
    }

    /**
     * Gets all answers created by a specific user
     */
    public List<Answer> getAnswersByUser(String username) throws SQLException {
        List<Answer> answersList = new ArrayList<>();
        String query = "SELECT * FROM answers WHERE author = ? ORDER BY createdAt DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Answer answer;
                String parentId = rs.getString("parentAnswerId");
                
                if (parentId != null && !parentId.isEmpty()) {
                    // This is a reply to another answer
                    answer = new Answer(
                        rs.getString("questionId"),
                        rs.getString("answerText"),
                        username,
                        parentId
                    );
                } else {
                    // This is a direct answer to a question
                    answer = new Answer(
                        rs.getString("questionId"),
                        rs.getString("answerText"),
                        username
                    );
                }
                
                // Set the ID to match what's in the database
                answer.setId(rs.getString("id"));
                
                // If your Answer class stores timestamps, set them here
                // answer.setCreatedAt(rs.getTimestamp("createdAt"));
                // answer.setUpdatedAt(rs.getTimestamp("updatedAt"));
                
                answersList.add(answer);
            }
        }
        
        return answersList;
    }

    /**
     * Gets details about a pending reviewer request
     */
    public Map<String, Object> getReviewerRequestDetails(String username) throws SQLException {
        Map<String, Object> requestDetails = new HashMap<>();
        String query = "SELECT * FROM reviewer_requests WHERE userId = ? AND status = 'PENDING'";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                requestDetails.put("id", rs.getInt("id"));
                requestDetails.put("userId", rs.getString("userId"));
                requestDetails.put("requestDate", rs.getTimestamp("requestDate"));
                requestDetails.put("status", rs.getString("status"));
            }
        }
        
        return requestDetails;
    }

    /**
     * Saves a question to the database.
     */
    public void saveQuestion(Question question) throws SQLException {
        String query = "INSERT INTO questions (id, title, description, author, createdAt, updatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question.getId());
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, question.getDescription());
            pstmt.setString(4, question.getAuthor());
            pstmt.setTimestamp(5, new java.sql.Timestamp(question.getCreatedAt().getTime()));
            pstmt.setTimestamp(6, new java.sql.Timestamp(question.getUpdatedAt().getTime()));
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Saves an answer to the database.
     */
    public void saveAnswer(Answer answer) throws SQLException {
        String query = "INSERT INTO answers (id, questionId, answerText, author, parentAnswerId, createdAt, updatedAt) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answer.getId());
            pstmt.setString(2, answer.getQuestionId());
            pstmt.setString(3, answer.getAnswerText());
            pstmt.setString(4, answer.getAuthor());
            pstmt.setString(5, answer.getParentAnswerId());
            pstmt.setTimestamp(6, new java.sql.Timestamp(answer.getCreatedAt().getTime()));
            pstmt.setTimestamp(7, new java.sql.Timestamp(answer.getUpdatedAt().getTime()));
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Registers a new user in the database with the specified role.
     * 
     * @param username The username for the new account.
     * @param password The password for the new account.
     * @param role The role for the new account (student or instructor).
     * @return true if registration was successful, false if username already exists.
     * @throws SQLException If there's a database error.
     */
    public boolean registerUser(String username, String password, String role) throws SQLException {
        // Check if username already exists
        if (doesUserExist(username)) {
            return false;
        }
        
        // Hash the password (you should implement proper password hashing)
        String hashedPassword = hashPassword(password);
        
        // Insert the new user with the selected role
        String query = "INSERT INTO cse360users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
        }
        
        return true;
    }

    /**
     * Simple password hashing implementation (you should use a more secure method in production)
     */
    private String hashPassword(String password) {
        // This is a placeholder - use a proper password hashing algorithm
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return password; // Fallback if hashing fails
        }
    }

    // Add this method to the class
    private void migrateDatabase() throws SQLException {
        try {
            // Check if reviewer_requests table has status column
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reviewer_requests LIMIT 1");
            boolean hasStatusColumn = false;
            
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                if ("status".equalsIgnoreCase(meta.getColumnName(i))) {
                    hasStatusColumn = true;
                    break;
                }
            }
            
            if (!hasStatusColumn) {
                System.out.println("Adding missing status column to reviewer_requests table");
                try {
                    stmt.executeUpdate("ALTER TABLE reviewer_requests ADD COLUMN status VARCHAR(20) DEFAULT 'PENDING'");
                } catch (SQLException e) {
                    // Some databases have different syntax
                    stmt.executeUpdate("ALTER TABLE reviewer_requests ADD status VARCHAR(20) DEFAULT 'PENDING'");
                }
            }
            
            stmt.close();
        } catch (SQLException e) {
            // Table may not exist yet, which is fine
            System.err.println("Note: " + e.getMessage());
        }
    }

    /**
     * Helper method to diagnose reviewer request issues
     */
    public void printReviewerRequestsDiagnostics() {
        try {
            System.out.println("\n----- REVIEWER REQUESTS DIAGNOSTICS -----");
            
            // Check if table exists
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM reviewer_requests")) {
                rs.next();
                System.out.println("reviewer_requests table exists with " + rs.getInt(1) + " rows");
            } catch (SQLException e) {
                System.out.println("reviewer_requests table does not exist: " + e.getMessage());
                return;
            }
            
            // Print all requests
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM reviewer_requests")) {
                System.out.println("\nAll reviewer requests:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                       ", UserID: " + rs.getString("userId") + 
                                       ", Date: " + rs.getTimestamp("requestDate") +
                                       ", Status: " + (rs.getString("status") != null ? rs.getString("status") : "NULL"));
                }
            } catch (SQLException e) {
                System.out.println("Error querying reviewer_requests: " + e.getMessage());
            }
            
            // Print all users
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM cse360users")) {
                System.out.println("\nAll users:");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                       ", UserName: " + rs.getString("userName") + 
                                       ", Role: " + rs.getString("role"));
                }
            } catch (SQLException e) {
                System.out.println("Error querying cse360users: " + e.getMessage());
            }
            
            System.out.println("\n----- END OF DIAGNOSTICS -----\n");
        } catch (Exception e) {
            System.err.println("Error running diagnostics: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
