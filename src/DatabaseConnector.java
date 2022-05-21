import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnector {
    Connection conn;

    // Should take all data as input instead
    DatabaseConnector() {
        //JPasswordField pf = new JPasswordField();
        //JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        //String password = new String(pf.getPassword());

        try {
            // Set up connection to database
            conn = DriverManager.getConnection(
                    "jdbc:mysql://bakiasxjp6gwmcao7sr7-mysql.services.clever-cloud.com/bakiasxjp6gwmcao7sr7? " +
                            "allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
                    "uuqsttecqided9gz",
                    "6WeAwhPt1ggcnzvxh8AK");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to log in, check your database and credentials and try again. Shutting down...");
            System.exit(0);
        }
    }

      public ArrayList<leaderboardPlayer> getDatabaseContent() {
        ArrayList<leaderboardPlayer> leaderbord = new ArrayList<>();
        try {
            // Setup statement
            Statement stmt = conn.createStatement();

            // Create query and execute
            String SQLQuery = "select * from leaderboard ORDER BY score DESC";
            ResultSet rset = stmt.executeQuery(SQLQuery);

            // Loop through the result set and convert to String
            // Need to know the table-structure
            while (rset.next()) {
                leaderbord.add(new leaderboardPlayer(rset.getInt("score"), rset.getString("playerName")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Something went wrong, check your tablestructure...");
        }
        return leaderbord;
    }

    public void terminateConnection() {
        conn = null;
    }

    public void insertData(int score,String playerName) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO leaderboard (score,playerName) VALUES ( '" + score + "','" + playerName + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateData(String meepBody, int id) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO emlasr_meeps (body) VALUES ('" + meepBody + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(int id) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM emlasr_meeps WHERE (id) = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
