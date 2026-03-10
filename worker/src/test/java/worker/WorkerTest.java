package worker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Créer une base de données H2 en mémoire pour les tests
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");

        // Créer la table votes
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS votes (id text PRIMARY KEY, vote text NOT NULL)");
        }
    }

    @Test
    void updateVote_shouldInsertWhenNoError() throws SQLException {
        // Test : insertion d'un nouveau vote
        assertDoesNotThrow(() -> Worker.updateVote(connection, "voter-1", "a"));

        // Vérifier que le vote a été inséré
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT vote FROM votes WHERE id = 'voter-1'")) {
            assertTrue(rs.next(), "Le vote devrait être présent dans la base de données");
            assertEquals("a", rs.getString("vote"), "Le vote devrait être 'a'");
            assertFalse(rs.next(), "Il ne devrait y avoir qu'un seul vote pour voter-1");
        }
    }

    @Test
    void updateVote_shouldUpdateWhenInsertFails() throws SQLException {
        // Insérer un vote initial
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO votes (id, vote) VALUES ('voter-2', 'a')");
        }

        // Test : mise à jour d'un vote existant (l'insertion échouera à cause de la clé primaire)
        assertDoesNotThrow(() -> Worker.updateVote(connection, "voter-2", "b"));

        // Vérifier que le vote a été mis à jour
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT vote FROM votes WHERE id = 'voter-2'")) {
            assertTrue(rs.next(), "Le vote devrait être présent dans la base de données");
            assertEquals("b", rs.getString("vote"), "Le vote devrait être mis à jour à 'b'");
            assertFalse(rs.next(), "Il ne devrait y avoir qu'un seul vote pour voter-2");
        }
    }

    @Test
    void updateVote_shouldHandleMultipleUpdates() throws SQLException {
        // Test : plusieurs mises à jour du même voteur
        Worker.updateVote(connection, "voter-3", "a");
        Worker.updateVote(connection, "voter-3", "b");
        Worker.updateVote(connection, "voter-3", "c");

        // Vérifier que seule la dernière valeur est conservée
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT vote FROM votes WHERE id = 'voter-3'")) {
            assertTrue(rs.next(), "Le vote devrait être présent dans la base de données");
            assertEquals("c", rs.getString("vote"), "Le vote devrait être 'c' (dernière valeur)");
        }
    }
}

