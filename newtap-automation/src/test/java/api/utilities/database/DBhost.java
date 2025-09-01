package api.utilities.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

public class DBhost {
    static PropertyFileReader property;
    public static Connection connection;
    public static Statement statement;
    private static final Logger log = LoggerFactory.getLogger(DBhost.class);
    static{
        try{
            property = new PropertyFileReader("config.properties");
        }
        catch (IOException E){
            E.printStackTrace();
        }
    }

   private static final String DB_USER_NAME =property.getProperty("db_user_name");
   private static final String DB_PASSWORD = property.getProperty("db_password");
//stage-master-db.stg.dreamplug.net
   public static Connection connectionToDB(String host,String dbName) throws SQLException {
       String hosturl ="jdbc:mysql://"+host+":3306/"+dbName+"?autoReconnect=true&useSSL=false&allowMultiQueries=true";
       return DriverManager.getConnection(hosturl,DB_USER_NAME,DB_PASSWORD);
   }
    synchronized public static String selectQuery(String dbHost, String query, String column, String DB) {
        Connection connection = null;
        Statement statement = null;
        ResultSet result = null;
        String value = null;
        try {
            connection = connectionToDB(dbHost, DB);
            statement = connection.createStatement();
            log.debug(String.format("Executing Query : %s", query));
            result = statement.executeQuery(query);

            while (result.next()) {
                value = result.getString(column);
                log.debug(String.format("Value is : %s", value));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, result);
        }
        return value;
    }


    public static ResultSet runQuery(String query) {
        String dbHost = "stage-master-db.stg.dreamplug.net";
        String dbName = "bureau_service";
        ResultSet resultSet = null;

        try {
            String fullUrl = "jdbc:mysql://" + dbHost + ":3306/" + dbName + "?autoReconnect=true&useSSL=false";
            connection = DriverManager.getConnection(fullUrl, DB_USER_NAME, DB_PASSWORD);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // Check if it's a SELECT query
            if (query.trim().toLowerCase().startsWith("select")) {
                resultSet = statement.executeQuery(query);
            } else {
                // Multi-statement support: split by semicolon and run each separately
                String[] queries = query.split(";");
                for (String q : queries) {
                    String trimmed = q.trim();
                    if (!trimmed.isEmpty()) {
                        statement.execute(trimmed);
                    }
                }
                System.out.println("Multi-statement query executed successfully.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultSet;
    }




    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
