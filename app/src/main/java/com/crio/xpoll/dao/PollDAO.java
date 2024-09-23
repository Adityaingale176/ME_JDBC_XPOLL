package com.crio.xpoll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.crio.xpoll.model.Choice;
import com.crio.xpoll.model.Poll;
import com.crio.xpoll.model.PollSummary;
import com.crio.xpoll.util.DatabaseConnection;

/**
 * Data Access Object (DAO) for managing polls in the XPoll application.
 * Provides methods for creating, retrieving, closing polls, and fetching poll summaries.
 */
public class PollDAO {

    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a PollDAO with the specified DatabaseConnection.
     *
     * @param databaseConnection The DatabaseConnection to be used for database operations.
     */
    public PollDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Creates a new poll with the specified question and choices.
     *
     * @param userId   The ID of the user creating the poll.
     * @param question The question for the poll.
     * @param choices  A list of choices for the poll.
     * @return The created Poll object with its associated choices.
     * @throws SQLException If a database error occurs during poll creation.
     */
    public Poll createPoll(int userId, String question, List<String> choices) throws SQLException {

        String pollSql = "INSERT INTO polls (user_Id, question, is_closed, created_at) VALUES (?, ?, ?, ?)";
        String choiceSql = "INSERT INTO choices (poll_id, choice_text) VALUES (? , ?)";
    
        // List to hold Choice objects that will be associated with the Poll
        List<Choice> choiceObjects = new ArrayList<>();
    
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement pollStmt = conn.prepareStatement(pollSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // Set poll parameters
            pollStmt.setInt(1, userId);
            pollStmt.setString(2, question);
            pollStmt.setBoolean(3, false); // Default value for is_closed
            pollStmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
    
            // Execute the poll insertion and get the generated poll ID
            pollStmt.executeUpdate();
    
            try (ResultSet generatedKeys = pollStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int pollId = generatedKeys.getInt(1);
    
                    // Insert each choice associated with the poll
                    try (PreparedStatement choiceStmt = conn.prepareStatement(choiceSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                        for (String choiceText : choices) {
                            choiceStmt.setInt(1, pollId);
                            choiceStmt.setString(2, choiceText);
                            choiceStmt.executeUpdate();
    
                            try (ResultSet choiceKeys = choiceStmt.getGeneratedKeys()) {
                                if (choiceKeys.next()) {
                                    int choiceId = choiceKeys.getInt(1); // Retrieve the generated choice ID
    
                                    // Create a Choice object with the retrieved choiceId, pollId, and choiceText
                                    Choice choice = new Choice(choiceId, pollId, choiceText);
                                    choiceObjects.add(choice); // Add the created Choice object to the list
                                } else {
                                    throw new SQLException("Creating choice failed, no ID obtained.");
                                }
                            }
                        }
                    }
                    // Return the Poll object with the associated choices
                    return new Poll(pollId, userId, question, choiceObjects);
                } else {
                    throw new SQLException("Creating poll failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Retrieves a poll by its ID.
     *
     * @param pollId The ID of the poll to retrieve.
     * @return The Poll object with its associated choices.
     * @throws SQLException If a database error occurs or the poll is not found.
     */
    public Poll getPoll(int pollId) throws SQLException {
       
        String sql = "SELECT * FROM polls WHERE id = ?";
        List<Choice> choices = new ArrayList<>();
    
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, pollId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int userId = rs.getInt("user_id");
                    String question = rs.getString("question");
                    boolean isClosed = rs.getBoolean("is_closed");
    
                    // Fetch associated choices
                    String choicesSql = "SELECT * FROM choices WHERE poll_id = ?";
                    try (PreparedStatement choicesStmt = conn.prepareStatement(choicesSql)) {
                        choicesStmt.setInt(1, id);
                        try (ResultSet choicesRs = choicesStmt.executeQuery()) {
                            while (choicesRs.next()) {
                                int choiceId = choicesRs.getInt("id");
                                int choicePollId = choicesRs.getInt("poll_id");
                                String choiceText = choicesRs.getString("choice_text"); // Updated to choice_text
                                choices.add(new Choice(choiceId, choicePollId, choiceText));
                            }
                        }
                    }
    
                    return new Poll(id, userId, question, choices, isClosed);
                } else {
                    throw new SQLException("Poll not found with ID: " + pollId);
                }
            }
        }
    }

    /**
     * Closes a poll by updating its status in the database.
     *
     * @param pollId The ID of the poll to close.
     * @throws SQLException If a database error occurs during the update.
     */
    public void closePoll(int pollId) throws SQLException {

        String sql = "UPDATE polls SET is_closed = TRUE WHERE id = ?";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, pollId);

            int affectedRows = stmt.executeUpdate();
                
            if (affectedRows==0){
                throw new SQLException("Closing poll failed, no rows affected.");
            }
        }
    }

    /**
     * Retrieves a list of poll summaries for the specified poll.
     *
     * @param pollId The ID of the poll for which to retrieve summaries.
     * @return A list of PollSummary objects containing the poll question, choice text, and response count.
     * @throws SQLException If a database error occurs during the query.
     */
    public List<PollSummary> getPollSummaries(int pollId) throws SQLException {
       
       String sql = "SELECT question, choice_text, response_count FROM poll_summaries WHERE poll_id = ? ";

        List<PollSummary> summeries = new ArrayList<>();

        try(Connection conn = databaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, pollId);

            try(ResultSet rs= stmt.executeQuery()){
                while(rs.next()){
                    String question = rs.getString("question");
                    String choiceText = rs.getString("choice_text");
                    int responseCount = rs.getInt("response_count");
                PollSummary summary = new PollSummary(question, choiceText, responseCount);
                summeries.add(summary);
                }
            }
        }
        return summeries;
    }
}