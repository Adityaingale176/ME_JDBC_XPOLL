package com.crio.xpoll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        return null;
    }

    /**
     * Retrieves a poll by its ID.
     *
     * @param pollId The ID of the poll to retrieve.
     * @return The Poll object with its associated choices.
     * @throws SQLException If a database error occurs or the poll is not found.
     */
    public Poll getPoll(int pollId) throws SQLException {
        return null;
    }

    /**
     * Closes a poll by updating its status in the database.
     *
     * @param pollId The ID of the poll to close.
     * @throws SQLException If a database error occurs during the update.
     */
    public void closePoll(int pollId) throws SQLException {
        return;
    }

    /**
     * Retrieves a list of poll summaries for the specified poll.
     *
     * @param pollId The ID of the poll for which to retrieve summaries.
     * @return A list of PollSummary objects containing the poll question, choice text, and response count.
     * @throws SQLException If a database error occurs during the query.
     */
    public List<PollSummary> getPollSummaries(int pollId) throws SQLException {
        return null;
    }
}