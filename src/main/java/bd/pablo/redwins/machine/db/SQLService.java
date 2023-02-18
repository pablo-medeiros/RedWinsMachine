package bd.pablo.redwins.machine.db;

import bd.pablo.redwins.machine.api.Machine;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

public interface SQLService {

    void createTable() throws SQLException;
    void dropTable() throws SQLException;

    void select(int id, Consumer<ResultSet> consumer) throws SQLException;
    void select(UUID owner, Consumer<ResultSet> consumer) throws SQLException;
    void select(String owner, Consumer<ResultSet> consumer) throws SQLException;
    void select(World world, Consumer<ResultSet> consumer) throws SQLException;

    void update(Machine machine) throws SQLException;

    void insert(Machine machine) throws SQLException;

    void insertOrUpdate(Machine machine) throws SQLException;

    void delete(int id) throws SQLException;

    Connection getConnection() throws SQLException;
}
