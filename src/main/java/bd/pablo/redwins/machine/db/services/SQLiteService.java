package bd.pablo.redwins.machine.db.services;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.db.SQLService;
import org.bukkit.World;
import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class SQLiteService implements SQLService {

    private final SQLiteDataSource dataSource = new SQLiteDataSource();

    public SQLiteService() {
        File file = new File(Main.getInstance().getDataFolder(), "data.db");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        dataSource.setUrl("jdbc:sqlite:"+ file.getAbsolutePath());
    }

    @Override
    public void createTable() throws SQLException {
        try(Connection connection = getConnection()){
            String sql = "CREATE TABLE IF NOT EXISTS machine (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "owner_uuid CHAR(36)," +
                    "owner_name VARCHAR(255)," +
                    "location VARCHAR(255)," +
                    "type VARCHAR(255)," +
                    "fuel BIGINT," +
                    "stack BIGINT" +
                    ");";
            try(PreparedStatement statement = connection.prepareStatement(sql)){
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void dropTable() throws SQLException {
        try(Connection connection = getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("DROP TABLE machine")){
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void select(int id, Consumer<ResultSet> consumer) throws SQLException {
        try(Connection connection = getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM machine WHERE id=?;")){
                statement.setInt(1,id);
                try(ResultSet result = statement.executeQuery()){
                    consumer.accept(result);
                }
            }
        }
    }

    @Override
    public void select(UUID owner, Consumer<ResultSet> consumer) throws SQLException {
        try(Connection connection = getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM machine WHERE owner_uuid=?;")){
                statement.setString(1,owner.toString());
                try(ResultSet result = statement.executeQuery()){
                    consumer.accept(result);
                }
            }
        }
    }

    @Override
    public void select(String owner, Consumer<ResultSet> consumer) throws SQLException {
        try(Connection connection = getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM machine WHERE owner_name=?;")){
                statement.setString(1,owner);
                try(ResultSet result = statement.executeQuery()){
                    consumer.accept(result);
                }
            }
        }
    }

    @Override
    public void select(World world, Consumer<ResultSet> consumer) throws SQLException {
        try(Connection connection = getConnection()){
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM machine WHERE location LIKE ?;")){
                statement.setString(1,world.getName()+"@%");
                try(ResultSet result = statement.executeQuery()){
                    consumer.accept(result);
                }
            }
        }
    }

    @Override
    public void update(Machine turret) throws SQLException {
        if(turret.id()<1)throw new SQLException("Invalid id");
        try(Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE machine SET fuel=?, stack=? WHERE id=?;")) {
                statement.setLong(1,turret.fuel());
                statement.setLong(2, turret.stack());
                statement.setInt(3,turret.id());
                statement.executeUpdate();
            }
        }
    }

    @Override
    public void insert(Machine machine) throws SQLException {
        try(Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO machine (owner_uuid, owner_name,location,type,fuel,stack) VALUES (?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, machine.ownerUUID().toString());
                statement.setString(2, machine.ownerName());
                statement.setString(3, machine.location().getWorld().getName()+"@"+machine.location().getBlockX()+"@"+machine.location().getBlockY()+"@"+machine.location().getBlockZ());
                statement.setString(4,machine.type().id());
                statement.setLong(5, machine.fuel());
                statement.setLong(6, machine.stack());
                statement.execute();
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    try {
                        Field idField = Machine.class.getDeclaredField("id");
                        idField.setAccessible(true);
                        idField.set(machine,rs.getInt(1));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        }
    }

    @Override
    public void insertOrUpdate(Machine machine) throws SQLException {
        if(machine.id()>0)update(machine);
        else if(machine.id()>-2)insert(machine);
    }

    @Override
    public void delete(int id) throws SQLException {
        try(Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM machine WHERE id=?;")) {
                statement.setInt(1,id);
                statement.executeUpdate();
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
