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
import HW2.Questions;
import HW2.Answer; // Import the Answer class
import HW2.Answers;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase;AUTO_SERVER=TRUE";

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
		// Create your existing tables first
		
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
		statement.execute(userTable);
		
		// Create the invitation codes table with a role column
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, "
				+ "role VARCHAR(20))"; 
		statement.execute(invitationCodesTable);

		// Alter the table to add the role column if it doesn't exist
		String addRoleColumn = "ALTER TABLE InvitationCodes ADD COLUMN IF NOT EXISTS role VARCHAR(20)";
		statement.execute(addRoleColumn);
		
		// Create all other tables first...
		
		// Create the reviews table before trying to alter it
		String reviewsTable = "CREATE TABLE IF NOT EXISTS reviews ("
				+ "id VARCHAR(36) PRIMARY KEY, "
				+ "reviewer VARCHAR(255), "
				+ "content TEXT, "
				+ "associatedId VARCHAR(36), "
				+ "rating INT DEFAULT 0, "
				+ "type VARCHAR(50), "
				+ "createdAt TIMESTAMP, "
				+ "updatedAt TIMESTAMP, "
				+ "original_review_id VARCHAR(36))";
		statement.execute(reviewsTable);

		// Now we can safely add columns if needed
		// Instead of ALTER TABLE, just ensure the column is included in the CREATE TABLE statement above
		
		// For reviewer experience
		String reviewerExperienceTable = "CREATE TABLE IF NOT EXISTS reviewer_experience ("
				+ "reviewer_username VARCHAR(255) PRIMARY KEY, "
				+ "experience TEXT, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(reviewerExperienceTable);

		// Create trusted_reviewers table
		String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS trusted_reviewers ("
				+ "student_username VARCHAR(255), "
				+ "reviewer_username VARCHAR(255), "
				+ "weight INT DEFAULT 5, "
				+ "PRIMARY KEY (student_username, reviewer_username))";
		statement.execute(trustedReviewersTable);

		// Add these lines to create tables needed for Discussion Page
		String questionsTable = "CREATE TABLE IF NOT EXISTS questions ("
				+ "id VARCHAR(36) PRIMARY KEY, "
				+ "title VARCHAR(255), "
				+ "description TEXT, "
				+ "author VARCHAR(255), "
				+ "flagged BOOLEAN DEFAULT FALSE, "
				+ "createdAt TIMESTAMP, "
				+ "updatedAt TIMESTAMP)";
		statement.execute(questionsTable);
		
		String answersTable = "CREATE TABLE IF NOT EXISTS answers ("
				+ "id VARCHAR(36) PRIMARY KEY, "
				+ "questionId VARCHAR(36), "
				+ "answerText TEXT, "
				+ "author VARCHAR(255), "
				+ "parentAnswerId VARCHAR(36), "
				+ "flagged BOOLEAN DEFAULT FALSE, "
				+ "createdAt TIMESTAMP, "
				+ "updatedAt TIMESTAMP)";
		statement.execute(answersTable);
		
		String answerLikesTable = "CREATE TABLE IF NOT EXISTS answer_likes ("
				+ "id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY, "
				+ "username VARCHAR(255) NOT NULL, "
				+ "answerId VARCHAR(36) NOT NULL, "
				+ "likedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "UNIQUE(username, answerId))";
		statement.execute(answerLikesTable);

		// Rest of your table creation statements...
	}

	/**
	 * Creates the necessary tables for reviewer functionality if they don't exist.
	 * This includes tables for reviewer requests, reviewer experiences, and other
	 * reviewer-related data.
	 * 
	 * @throws SQLException if a database error occurs
	 */
	private void createReviewerTables() throws SQLException {
		// Create reviewer_requests table
		String reviewerRequestsTable = "CREATE TABLE IF NOT EXISTS reviewer_requests ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userId VARCHAR(50) NOT NULL, "
				+ "requestDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "status VARCHAR(20) DEFAULT 'PENDING')";
		statement.execute(reviewerRequestsTable);
		
		// Create reviewer_experience table (if not already created in createTables)
		String reviewerExperienceTable = "CREATE TABLE IF NOT EXISTS reviewer_experience ("
				+ "reviewer_username VARCHAR(255) PRIMARY KEY, "
				+ "experience TEXT, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(reviewerExperienceTable);

		// Create notifications table for reviewer updates
		String notificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
				+ "id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY, "
				+ "user_id VARCHAR(255) NOT NULL, "
				+ "message TEXT NOT NULL, "
				+ "related_id VARCHAR(36), "
				+ "type VARCHAR(50), "
				+ "is_read BOOLEAN DEFAULT FALSE, "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(notificationsTable);
		
		// Create messages table if it doesn't exist
		String messagesTable = "CREATE TABLE IF NOT EXISTS messages ("
				+ "id VARCHAR(36) PRIMARY KEY, "
				+ "sender VARCHAR(255) NOT NULL, "
				+ "recipient VARCHAR(255) NOT NULL, "
				+ "content TEXT, "
				+ "related_id VARCHAR(36), "
				+ "sentAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(messagesTable);
		
		System.out.println("Successfully initialized reviewer tables");
	}

    /**
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
	
	/**
	 * Checks if a user already exists in the database based on their userName.
	 * @param userName The username to check
	 * @return true if the user exists, false otherwise
	 */
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
	
	/**
	 * Checks if the database is empty (no users exist).
	 * @return true if the database is empty, false otherwise
	 */
	public boolean isDatabaseEmpty() {
	    try {
	        String query = "SELECT COUNT(*) FROM cse360users";
	        try (Statement stmt = connection.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            if (rs.next()) {
	                return rs.getInt(1) == 0;
	            }
	        }
	    } catch (SQLException e) {
	        // If the table doesn't exist yet, the database is considered empty
	        System.err.println("Error checking if database is empty: " + e.getMessage());
	        return true;
	    }
	    return true; // Default to true if there's an issue
	}

	/**
	 * Retrieves the role of a user from the database using their userName.
	 * 
	 * @param userName The userName of the user whose role is to be retrieved.
	 * @return The role of the user, or null if the user doesn't exist or an error occurs.
	 */
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
	
	/**
	 * Checks if the given invitation code is valid for creating a staff account.
	 * @param code The invitation code to validate.
	 * @return true if the code is valid for staff, false otherwise.
	 * @throws SQLException If a database error occurs.
	 */
	public boolean isStaffInvitationCode(String code) throws SQLException {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND role = 'staff'";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next();
	    }
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
        // First, make sure the table exists
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS reviews (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "reviewer VARCHAR(255), " +
                "content TEXT, " +
                "associatedId VARCHAR(36), " +
                "rating INT, " +
                "type VARCHAR(50), " +
                "createdAt TIMESTAMP, " +
                "updatedAt TIMESTAMP)"
            );
        }
        
        // Insert the review
        String query = "INSERT INTO reviews (id, reviewer, content, associatedId, rating, type, createdAt, updatedAt) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, review.getId());
            pstmt.setString(2, review.getReviewer());
            pstmt.setString(3, review.getContent());
            pstmt.setString(4, review.getAssociatedId());
            pstmt.setInt(5, review.getRating());
            pstmt.setString(6, review.getType());
            pstmt.setTimestamp(7, new java.sql.Timestamp(review.getCreatedAt().getTime()));
            pstmt.setTimestamp(8, new java.sql.Timestamp(review.getUpdatedAt().getTime()));
            
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
        String query = "INSERT INTO messages (id, sender, recipient, content, related_id, sentAt) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, message.getId());
            pstmt.setString(2, message.getSender());
            pstmt.setString(3, message.getRecipient());
            pstmt.setString(4, message.getContent());
            pstmt.setString(5, message.getRelatedId());
            pstmt.setTimestamp(6, new java.sql.Timestamp(message.getSentAt().getTime()));
            
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
                
                // Set the message ID
                message.setId(rs.getString("id"));
                
                // Set the sent timestamp
                java.sql.Timestamp sentAt = rs.getTimestamp("sentAt");
                if (sentAt != null) {
                    message.setSentAt(new java.util.Date(sentAt.getTime()));
                }
                
                // Set related ID if present
                String relatedId = rs.getString("related_id");
                if (relatedId != null) {
                    message.setRelatedId(relatedId);
                }
                
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
        
        // Debug info to help diagnose the issue
        System.out.println("Getting trusted reviewers for: " + studentUsername);
        
        String query = "SELECT * FROM trusted_reviewers WHERE student_username = ? ORDER BY weight DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, studentUsername);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String reviewerUsername = rs.getString("reviewer_username");
                int weight = rs.getInt("weight");
                
                System.out.println("Found trusted reviewer: " + reviewerUsername + " with weight: " + weight);
                
                TrustedReviewer reviewer = new TrustedReviewer(
                    reviewerUsername,
                    studentUsername,
                    weight
                );
                reviewersList.add(reviewer);
            }
        }
        
        System.out.println("Total trusted reviewers found: " + reviewersList.size());
        return reviewersList;
    }

    /**
     * Gets reviews from trusted reviewers only
     */
    public java.util.List<Review> getReviewsFromTrustedReviewers(String studentUsername, String associatedId) throws SQLException {
        java.util.List<Review> reviewsList = new java.util.ArrayList<>();
        
        // Fixed query with consistent column names
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
                
                // Set the ID to match what's in the database
                review.setId(rs.getString("id"));
                
                // Set additional properties if available
                if (rs.getObject("rating") != null) {
                    review.setRating(rs.getInt("rating"));
                }
                
                if (rs.getString("type") != null) {
                    review.setType(rs.getString("type"));
                }
                
                // Add timestamps
                java.sql.Timestamp createdAt = rs.getTimestamp("createdAt");
                if (createdAt != null) {
                    review.setCreatedDate(new Date(createdAt.getTime()));
                }
                
                java.sql.Timestamp updatedAt = rs.getTimestamp("updatedAt");
                if (updatedAt != null) {
                    review.setUpdatedDate(new Date(updatedAt.getTime()));
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
        // First, make sure the table exists
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS questions (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "description TEXT, " +
                "author VARCHAR(255), " +
                "createdAt TIMESTAMP, " +
                "updatedAt TIMESTAMP)"
            );
        }
        
        // Check if question already exists
        String checkQuery = "SELECT COUNT(*) FROM questions WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, question.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Question exists, update it
                    String updateQuery = "UPDATE questions SET title = ?, description = ?, updatedAt = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, question.getTitle());
                        updateStmt.setString(2, question.getDescription());
                        updateStmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                        updateStmt.setString(4, question.getId());
                        updateStmt.executeUpdate();
                        System.out.println("Updated question: " + question.getId());
                    }
                    return;
                }
            }
        }
        
        // Question doesn't exist, insert it
        String query = "INSERT INTO questions (id, title, description, author, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, question.getId());
            pstmt.setString(2, question.getTitle());
            pstmt.setString(3, question.getDescription());
            pstmt.setString(4, question.getAuthor());
            pstmt.setTimestamp(5, new java.sql.Timestamp(question.getCreatedAt().getTime()));
            pstmt.setTimestamp(6, new java.sql.Timestamp(question.getUpdatedAt().getTime()));
            
            pstmt.executeUpdate();
            System.out.println("Saved new question: " + question.getId());
        }
    }

    /**
     * Saves an answer to the database.
     */
    public void saveAnswer(Answer answer) throws SQLException {
        // First, make sure the table exists
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS answers (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "questionId VARCHAR(36), " +
                "answerText TEXT, " +
                "author VARCHAR(255), " +
                "parentAnswerId VARCHAR(36), " +
                "createdAt TIMESTAMP, " +
                "updatedAt TIMESTAMP)"
            );
        }
        
        // Check if answer already exists
        String checkQuery = "SELECT COUNT(*) FROM answers WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, answer.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Answer exists, update it
                    String updateQuery = "UPDATE answers SET answerText = ?, updatedAt = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, answer.getAnswerText());
                        updateStmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
                        updateStmt.setString(3, answer.getId());
                        updateStmt.executeUpdate();
                        System.out.println("Updated answer: " + answer.getId());
                    }
                    return;
                }
            }
        }

        // Answer doesn't exist, insert it
        String query = "INSERT INTO answers (id, questionId, answerText, author, parentAnswerId, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answer.getId());
            pstmt.setString(2, answer.getQuestionId());
            pstmt.setString(3, answer.getAnswerText());
            pstmt.setString(4, answer.getAuthor());
            pstmt.setString(5, answer.getParentAnswerId());
            pstmt.setTimestamp(6, new java.sql.Timestamp(answer.getCreatedAt().getTime()));
            pstmt.setTimestamp(7, new java.sql.Timestamp(answer.getUpdatedAt().getTime()));
            
            pstmt.executeUpdate();
            System.out.println("Saved new answer: " + answer.getId());
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
        String hashedPassword = hashPassword1(password);
        
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
    private String hashPassword1(String password) {
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

        try {
            // Check if the reviews table has the rating column
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM reviews LIMIT 1");
            boolean hasRatingColumn = false;
            boolean hasTypeColumn = false;

            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                if ("rating".equalsIgnoreCase(meta.getColumnName(i))) {
                    hasRatingColumn = true;
                }
                if ("type".equalsIgnoreCase(meta.getColumnName(i))) {
                    hasTypeColumn = true;
                }
            }

            if (!hasRatingColumn) {
                System.out.println("Adding missing rating column to reviews table");
                stmt.executeUpdate("ALTER TABLE reviews ADD COLUMN rating INT DEFAULT 0");
            }

            if (!hasTypeColumn) {
                System.out.println("Adding missing type column to reviews table");
                stmt.executeUpdate("ALTER TABLE reviews ADD COLUMN type VARCHAR(50)");
            }

            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error during database migration: " + e.getMessage());
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

    /**
     * Deletes a question from the database.
     */
    public void deleteQuestion(String questionId) throws SQLException {
        // First, delete all answers associated with this question
        String deleteAnswersQuery = "DELETE FROM answers WHERE questionId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteAnswersQuery)) {
            pstmt.setString(1, questionId);
            int answersDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + answersDeleted + " answers for question: " + questionId);
        }
        
        // Then delete the question itself
        String deleteQuestionQuery = "DELETE FROM questions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteQuestionQuery)) {
            pstmt.setString(1, questionId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Deleted question: " + questionId);
            } else {
                System.out.println("Question not found: " + questionId);
            }
        }
    }

    /**
     * Deletes an answer from the database.
     */
    public void deleteAnswer(String answerId) throws SQLException {
        // First, delete all replies to this answer
        String deleteRepliesQuery = "DELETE FROM answers WHERE parentAnswerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteRepliesQuery)) {
            pstmt.setString(1, answerId);
            int repliesDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + repliesDeleted + " replies to answer: " + answerId);
        }
        
        // Then delete the answer itself
        String deleteAnswerQuery = "DELETE FROM answers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteAnswerQuery)) {
            pstmt.setString(1, answerId);
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("Deleted answer: " + answerId);
            } else {
                System.out.println("Answer not found: " + answerId);
            }
        }
    }

    /**
     * Get all reviews for a specific item (question, answer, etc.)
     * 
     * @param associatedId The ID of the item for which to retrieve reviews
     * @return List of reviews for the associated item
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsForAssociatedId(String associatedId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        
        String query = "SELECT * FROM reviews WHERE associatedId = ? ORDER BY createdAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, associatedId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                        rs.getString("reviewer"),
                        rs.getString("content"),
                        rs.getString("associatedId"),
                        rs.getInt("rating"),
                        rs.getString("type")
                    );
                    review.setId(rs.getString("id"));
                    review.setCreatedDate(new java.util.Date(rs.getTimestamp("createdAt").getTime()));
                    review.setUpdatedDate(new java.util.Date(rs.getTimestamp("updatedAt").getTime()));
                    reviews.add(review);
                }
            }
        }
        
        return reviews;
    }

    /**
     * Manually fixes the reviews table by adding any missing columns
     */
    public void fixReviewsTable() throws SQLException {
        try {
            Statement stmt = connection.createStatement();
            
            // Try to add the type column if it doesn't exist
            try {
                stmt.executeUpdate("ALTER TABLE reviews ADD COLUMN type VARCHAR(50)");
                System.out.println("Added 'type' column to reviews table");
            } catch (SQLException e) {
                // Column might already exist or table might not exist
                if (e.getMessage().contains("Duplicate column")) {
                    System.out.println("Column 'type' already exists in reviews table");
                } else {
                    throw e;
                }
            }
            
            // Try to add the rating column if it doesn't exist
            try {
                stmt.executeUpdate("ALTER TABLE reviews ADD COLUMN rating INT DEFAULT 0");
                System.out.println("Added 'rating' column to reviews table");
            } catch (SQLException e) {
                // Column might already exist
                if (e.getMessage().contains("Duplicate column")) {
                    System.out.println("Column 'rating' already exists in reviews table");
                } else {
                    throw e;
                }
            }
            
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error fixing reviews table: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Loads all discussion content (questions and answers) from the database
     */
    public void loadAllDiscussionContent(Questions questions, Answers answers) throws SQLException {
        // Clear existing content
        questions.clearAll();
        answers.clearAll();
        
        // Load all questions
        String questionsQuery = "SELECT * FROM questions ORDER BY createdAt DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(questionsQuery)) {
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("author")
                );
                question.setId(rs.getString("id"));
                questions.addQuestion(question);
            }
        }
        
        // Load all answers
        String answersQuery = "SELECT * FROM answers ORDER BY createdAt DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(answersQuery)) {
            
            while (rs.next()) {
                Answer answer;
                String parentId = rs.getString("parentAnswerId");
                
                if (parentId != null && !parentId.isEmpty()) {
                    answer = new Answer(
                        rs.getString("questionId"),
                        rs.getString("answerText"),
                        rs.getString("author"),
                        parentId
                    );
                } else {
                    answer = new Answer(
                        rs.getString("questionId"),
                        rs.getString("answerText"),
                        rs.getString("author")
                    );
                }
                
                answer.setId(rs.getString("id"));
                answers.addAnswer(answer);
            }
        }
    }

    /**
     * Gets all reviews by a specific reviewer.
     */
    public List<Review> getReviewsByReviewer1(String reviewer) throws SQLException {
        List<Review> results = new ArrayList<>();
        
        String query = "SELECT * FROM reviews WHERE reviewer = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewer);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Review review = new Review(
                        rs.getString("reviewer"),
                        rs.getString("content"),
                        rs.getString("associatedId")
                    );
                    
                    review.setId(rs.getString("id"));
                    
                    // Handle optional fields
                    int rating = rs.getInt("rating");
                    review.setRating(rating);
                    
                    String type = rs.getString("type");
                    if (type != null && !type.isEmpty()) {
                        review.setType(type);
                    }
                    
                    // Handle dates if present
                    java.sql.Timestamp createdAt = rs.getTimestamp("createdAt");
                    if (createdAt != null) {
                        review.setCreatedDate(new Date(createdAt.getTime()));
                    }
                    
                    java.sql.Timestamp updatedAt = rs.getTimestamp("updatedAt");
                    if (updatedAt != null) {
                        review.setUpdatedDate(new Date(updatedAt.getTime()));
                    }
                    
                    results.add(review);
                }
            }
        }
        
        return results;
    }

    public List<String> getAllQuestions() throws SQLException {
        List<String> questions = new ArrayList<>();
        String query = "SELECT title FROM questions";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                questions.add(rs.getString("title"));
            }
        }
        return questions;
    }

    public List<String> getAllAnswers() throws SQLException {
        List<String> answers = new ArrayList<>();
        String query = "SELECT answerText FROM answers";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                answers.add(rs.getString("answerText"));
            }
        }
        return answers;
    }

    public List<String> getAllFeedback() throws SQLException {
        List<String> feedback = new ArrayList<>();
        String query = "SELECT content FROM feedback";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                feedback.add(rs.getString("content"));
            }
        }
        return feedback;
    }

    public List<String> getAnswersForQuestion(String questionTitle) throws SQLException {
        List<String> answers = new ArrayList<>();
        String query = "SELECT answerText FROM answers WHERE questionId = " +
                       "(SELECT id FROM questions WHERE title = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, questionTitle);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    answers.add(rs.getString("answerText"));
                }
            }
        }
        return answers;
    }

    public List<String> getRepliesForAnswer(String answer) throws SQLException {
        List<String> replies = new ArrayList<>();
        String query = "SELECT reply FROM replies WHERE answer_id = " +
                       "(SELECT id FROM answers WHERE answer = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, answer);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    replies.add(rs.getString("reply")); // Ensure "reply" matches the column name in the database
                }
            }
        }
        return replies;
    }

    /**
     * Flags a question for admin review.
     * 
     * @param questionId the ID of the question to flag
     * @throws SQLException if a database error occurs
     */
    public void flagQuestion(String questionId) throws SQLException {
        String sql = "UPDATE questions SET flagged = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                throw new SQLException("Flagging question failed, no question found with ID: " + questionId);
            }
        }
    }

    /**
     * Flags an answer for admin review.
     * 
     * @param answerId the ID of the answer to flag
     * @throws SQLException if a database error occurs
     */
    public void flagAnswer(String answerId) throws SQLException {
        String sql = "UPDATE answers SET flagged = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, answerId);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                throw new SQLException("Flagging answer failed, no answer found with ID: " + answerId);
            }
        }
    }

    /**
     * Flags a review for admin review.
     * 
     * @param reviewId the ID of the review to flag
     * @throws SQLException if a database error occurs
     */
    public void flagReview(String reviewId) throws SQLException {
        // Ensure the table has the flagged column before trying to use it
        ensureReviewsFlaggingSchema();
        
        String sql = "UPDATE reviews SET flagged = TRUE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reviewId);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                throw new SQLException("Flagging review failed, no review found with ID: " + reviewId);
            }
            System.out.println("Successfully flagged review with ID: " + reviewId);
        }
    }

    /**
     * Retrieves all flagged questions.
     * 
     * @return list of flagged questions
     * @throws SQLException if a database error occurs
     */
    public List<Question> getFlaggedQuestions() throws SQLException {
        List<Question> flaggedQuestions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE flagged = TRUE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("author")
                );
                question.setId(rs.getString("id"));
                flaggedQuestions.add(question);
            }
        }
        
        return flaggedQuestions;
    }

    /**
     * Retrieves all flagged answers.
     * 
     * @return list of flagged answers
     * @throws SQLException if a database error occurs
     */
    public List<Answer> getFlaggedAnswers() throws SQLException {
        List<Answer> flaggedAnswers = new ArrayList<>();
        String sql = "SELECT * FROM answers WHERE flagged = TRUE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Answer answer = new Answer(
                    rs.getString("questionId"),
                    rs.getString("answerText"),
                    rs.getString("author")
                );
                answer.setId(rs.getString("id"));
                flaggedAnswers.add(answer);
            }
        }
        
        return flaggedAnswers;
    }

    /**
     * Retrieves all flagged reviews.
     * 
     * @return list of flagged reviews
     * @throws SQLException if a database error occurs
     */
    public List<Review> getFlaggedReviews() throws SQLException {
        List<Review> flaggedReviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE flagged = TRUE";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Review review = new Review(
                    rs.getString("reviewer"),
                    rs.getString("content"),
                    rs.getString("associatedId"),
                    rs.getInt("rating"),
                    rs.getString("type")
                );
                review.setId(rs.getString("id"));
                flaggedReviews.add(review);
            }
        }
        
        return flaggedReviews;
    }

    /**
     * Marks a flagged question as resolved.
     * 
     * @param questionId the ID of the question to mark as resolved
     * @throws SQLException if a database error occurs
     */
    public void markQuestionAsResolved(String questionId) throws SQLException {
        String sql = "UPDATE questions SET flagged = FALSE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Marks a flagged answer as resolved.
     * 
     * @param answerId the ID of the answer to mark as resolved
     * @throws SQLException if a database error occurs
     */
    public void markAnswerAsResolved(String answerId) throws SQLException {
        String sql = "UPDATE answers SET flagged = FALSE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, answerId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Marks a flagged review as resolved.
     * 
     * @param reviewId the ID of the review to mark as resolved
     * @throws SQLException if a database error occurs
     */
    public void markReviewAsResolved(String reviewId) throws SQLException {
        // Ensure the table has the flagged column before trying to use it
        ensureReviewsFlaggingSchema();
        
        String sql = "UPDATE reviews SET flagged = FALSE WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reviewId);
            int result = pstmt.executeUpdate();
            
            if (result == 0) {
                throw new SQLException("Resolving review failed, no review found with ID: " + reviewId);
            }
            System.out.println("Successfully resolved review with ID: " + reviewId);
        }
    }
    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param username The username to check for
     * @return true if the user exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean userExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        
        try (java.sql.PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    /**
     * Ensures that the database schema has the required columns for flagging functionality.
     * This method adds flagged columns to questions, answers, and reviews tables if they don't exist.
     * 
     * @throws SQLException if a database error occurs
     */
    public void initializeFlaggingSchema() throws SQLException {
        try (java.sql.Statement stmt = connection.createStatement()) {
            // Add flagged columns to questions and answers tables as before...
            
            // For reviews table, use the more robust method
            ensureReviewsFlaggingSchema();
        }
    }

    /**
     * Ensures the reviews table has a flagged column and fixes it if missing
     * This is called before any flagging operations
     *
     * @throws SQLException if a database error occurs
     */
    public void ensureReviewsFlaggingSchema() throws SQLException {
        // Check if reviews table exists first
        boolean tableExists = false;
        try (ResultSet tables = connection.getMetaData().getTables(
                null, null, "REVIEWS", new String[]{"TABLE"})) {
            tableExists = tables.next();
        }

        if (tableExists) {
            // Check if flagged column exists
            boolean columnExists = false;
            try (ResultSet columns = connection.getMetaData().getColumns(
                    null, null, "REVIEWS", "FLAGGED")) {
                columnExists = columns.next();
            }
            
            // If table exists but column doesn't, add it
            if (!columnExists) {
                System.out.println("Adding FLAGGED column to reviews table...");
                try {
                    // Try the standard SQL syntax
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute("ALTER TABLE reviews ADD COLUMN flagged BOOLEAN DEFAULT FALSE");
                    }
                    System.out.println("Successfully added FLAGGED column to reviews table");
                } catch (SQLException e) {
                    System.err.println("First attempt to add FLAGGED column failed: " + e.getMessage());
                    
                    // Try database-specific syntax variations
                    try (Statement stmt = connection.createStatement()) {
                        stmt.execute("ALTER TABLE reviews ADD flagged BOOLEAN DEFAULT FALSE");
                    }
                    System.out.println("Successfully added FLAGGED column with alternate syntax");
                }
            } else {
                System.out.println("FLAGGED column already exists in reviews table");
            }
        } else {
            // If table doesn't exist, we'll create it with the flagged column
            System.out.println("Creating reviews table with FLAGGED column...");
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(
                    "CREATE TABLE reviews (" +
                    "id VARCHAR(36) PRIMARY KEY, " +
                    "reviewer VARCHAR(255), " +
                    "content TEXT, " +
                    "associatedId VARCHAR(36), " +
                    "rating INT DEFAULT 0, " +
                    "type VARCHAR(50), " +
                    "flagged BOOLEAN DEFAULT FALSE, " +
                    "createdAt TIMESTAMP, " +
                    "updatedAt TIMESTAMP)"
                );
            }
            System.out.println("Created reviews table with FLAGGED column");
        }
    }

    /**
     * Saves an updated review while preserving the original
     */
    public void saveUpdatedReview(Review updatedReview) throws SQLException {
        // Insert the updated review
        String query = "INSERT INTO reviews (id, reviewer_username, content, associated_id, rating, type, created_at, original_review_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String newId = UUID.randomUUID().toString();
            updatedReview.setId(newId);
            
            pstmt.setString(1, newId);
            pstmt.setString(2, updatedReview.getReviewer());
            pstmt.setString(3, updatedReview.getContent());
            pstmt.setString(4, updatedReview.getAssociatedId());
            pstmt.setInt(5, updatedReview.getRating());
            pstmt.setString(6, updatedReview.getType());
            pstmt.setString(7, updatedReview.getOriginalReviewId());
            
            pstmt.executeUpdate();
        }
    }

    /**
     * Notifies students about a review update
     */
    public void notifyStudentsAboutReviewUpdate(Review updatedReview) throws SQLException {
        // Get all students who have this reviewer as trusted
        String query = "SELECT student_username FROM trusted_reviewers WHERE reviewer_username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, updatedReview.getReviewer());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String studentUsername = rs.getString("student_username");
                
                // Create notification
                String notificationQuery = "INSERT INTO notifications (user_id, message, related_id, type, is_read) VALUES (?, ?, ?, 'REVIEW_UPDATE', 0)";
                try (PreparedStatement notifyStmt = connection.prepareStatement(notificationQuery)) {
                    notifyStmt.setString(1, studentUsername);
                    notifyStmt.setString(2, "Reviewer " + updatedReview.getReviewer() + " has updated a review");
                    notifyStmt.setString(3, updatedReview.getId());
                    notifyStmt.executeUpdate();
                }
            }
        }
    }

    /**
     * Gets feedback counts for a reviewer's reviews
     */
    public Map<String, Integer> getFeedbackCountsForReviewer(String reviewerUsername) throws SQLException {
        Map<String, Integer> feedbackCounts = new HashMap<>();
        
        String query = "SELECT r.id, COUNT(m.id) as count FROM reviews r " +
                      "LEFT JOIN messages m ON m.related_id = r.id " +
                      "WHERE r.reviewer_username = ? " +
                      "GROUP BY r.id";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                feedbackCounts.put(rs.getString("id"), rs.getInt("count"));
            }
        }
        
        return feedbackCounts;
    }

    /**
     * Gets the reviewer's experience information
     */
    public String getReviewerExperience(String reviewerUsername) throws SQLException {
        String query = "SELECT experience FROM reviewer_experience WHERE reviewer_username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("experience");
            }
        }
        
        return "";
    }

    /**
     * Updates a reviewer's experience information
     */
    public void updateReviewerExperience(String reviewerUsername, String experience) throws SQLException {
        // Check if record exists first
        String checkQuery = "SELECT COUNT(*) FROM reviewer_experience WHERE reviewer_username = ?";
        boolean exists = false;
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, reviewerUsername);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        }
        
        if (exists) {
            // Update existing record
            String updateQuery = "UPDATE reviewer_experience SET experience = ?, updated_at = CURRENT_TIMESTAMP WHERE reviewer_username = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setString(1, experience);
                updateStmt.setString(2, reviewerUsername);
                updateStmt.executeUpdate();
            }
        } else {
            // Insert new record
            String insertQuery = "INSERT INTO reviewer_experience (reviewer_username, experience, created_at, updated_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, reviewerUsername);
                insertStmt.setString(2, experience);
                insertStmt.executeUpdate();
            }
        }
    }

    /**
     * Gets the total number of reviews by a reviewer
     * 
     * @param reviewerUsername The username of the reviewer
     * @return The count of reviews by this reviewer
     * @throws SQLException if a database error occurs
     */
    public int getReviewCountForReviewer(String reviewerUsername) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM reviews WHERE reviewer = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        
        return 0;
    }

    /**
     * Calculates the average rating of reviews created by a reviewer
     * 
     * @param reviewerUsername The username of the reviewer
     * @return The average rating or 0.0 if no rated reviews exist
     * @throws SQLException if a database error occurs
     */
    public double getAverageRatingForReviewer(String reviewerUsername) throws SQLException {
        String query = "SELECT AVG(rating) as average FROM reviews WHERE reviewer = ? AND rating > 0";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double avg = rs.getDouble("average");
                return avg;
            }
        }
        
        return 0.0;
    }

    /**
     * Gets feedback messages for a reviewer's reviews
     * 
     * @param reviewerUsername The username of the reviewer
     * @return List of feedback messages
     * @throws SQLException if a database error occurs
     */
    public List<Message> getFeedbackForReviewer(String reviewerUsername) throws SQLException {
        List<Message> feedbackMessages = new ArrayList<>();
        
        // Join messages with reviews to get messages related to reviews by this reviewer
        String query = 
            "SELECT m.* FROM messages m " +
            "JOIN reviews r ON m.related_id = r.id " +
            "WHERE r.reviewer = ? " +
            "ORDER BY m.sentAt DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, reviewerUsername);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Message message = new Message(
                    rs.getString("sender"),
                    rs.getString("recipient"),
                    rs.getString("content")
                );
                message.setId(rs.getString("id"));
                
                // Set sent date if available
                Timestamp sentAt = rs.getTimestamp("sentAt");
                if (sentAt != null) {
                    message.setSentAt(new Date(sentAt.getTime()));
                }
                
                feedbackMessages.add(message);
            }
        }
        
        return feedbackMessages;
    }

    /**
     * Ensures all tables required for the Discussion page exist
     */
    public void ensureDiscussionTablesExist() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create questions table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS questions (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "description TEXT, " +
                "author VARCHAR(255), " +
                "flagged BOOLEAN DEFAULT FALSE, " +
                "createdAt TIMESTAMP, " +
                "updatedAt TIMESTAMP)"
            );
            
            // Create answers table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS answers (" +
                "id VARCHAR(36) PRIMARY KEY, " +
                "questionId VARCHAR(36), " +
                "answerText TEXT, " +
                "author VARCHAR(255), " +
                "parentAnswerId VARCHAR(36), " +
                "flagged BOOLEAN DEFAULT FALSE, " +
                "createdAt TIMESTAMP, " +
                "updatedAt TIMESTAMP)"
            );
            
            // Create answer_likes table if it doesn't exist
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS answer_likes (" +
                "id VARCHAR(36) DEFAULT RANDOM_UUID() PRIMARY KEY, " +
                "username VARCHAR(255) NOT NULL, " +
                "answerId VARCHAR(36) NOT NULL, " +
                "likedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(username, answerId))"
            );
            
            System.out.println("Successfully verified all discussion tables exist");
        } catch (SQLException e) {
            System.err.println("Error ensuring discussion tables exist: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Gets the number of likes for an answer
     */
    public int getLikesCount(String answerId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM answer_likes WHERE answerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, answerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    /**
     * Checks if a user has already liked an answer
     */
    public boolean hasUserLikedAnswer(String username, String answerId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM answer_likes WHERE username = ? AND answerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, answerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        }
        return false;
    }

    /**
     * Adds a like to an answer
     */
    public void likeAnswer(String username, String answerId) throws SQLException {
        if (hasUserLikedAnswer(username, answerId)) {
            return; // Already liked
        }
        
        String query = "INSERT INTO answer_likes (username, answerId) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, answerId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Removes a like from an answer
     */
    public void unlikeAnswer(String username, String answerId) throws SQLException {
        String query = "DELETE FROM answer_likes WHERE username = ? AND answerId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, answerId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Verifies a user's password by comparing the hashed input with the stored hash.
     * @param username The username to verify
     * @param password The password to verify
     * @return true if credentials are valid, false otherwise
     */
    public boolean verifyPassword(String username, String password) throws SQLException {
        System.out.println("Verifying password for user: " + username); // Debug log
        
        String hashedPassword = hashPassword1(password);
        String query = "SELECT password FROM cse360users WHERE userName = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                boolean matches = storedHash.equals(hashedPassword);
                System.out.println("Password verification result: " + matches); // Debug log
                return matches;
            } else {
                System.out.println("No user found with username: " + username); // Debug log
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error during password verification: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Hash a password using SHA-256
     */
    private String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            return java.util.HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return password; // Fallback if hashing fails
        }
    }

    /**
     * Clears all messages for a specific recipient
     * @param recipient The username of the recipient
     * @throws SQLException if a database error occurs
     */
    public void clearMessagesForRecipient(String recipient) throws SQLException {
        String sql = "DELETE FROM messages WHERE recipient = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, recipient);
            stmt.executeUpdate();
        }
    }
}
