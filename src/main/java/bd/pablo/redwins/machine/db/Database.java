package bd.pablo.redwins.machine.db;

import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.db.services.MySQLService;
import bd.pablo.redwins.machine.db.services.SQLiteService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class Database implements SQLService {

    private SQLService service;

    public Database(ConfigurationSection section) {
        if(section.getString("type","sqlite").equalsIgnoreCase("mysql")){
            try {
                service = new MySQLService(section.getConfigurationSection("mysql"));
                Connection con = service.getConnection();
                if(!con.isClosed()){
                    con.close();
                    return;
                }
            }catch (Exception e){
                Bukkit.getConsoleSender().sendMessage("§b[Machine] §cErro no MySQL, alterando para sqlite...");
            }
        }
        service = new SQLiteService();
    }

    @Override
    public void createTable() throws SQLException {
        service.createTable();
    }

    @Override
    public void dropTable() throws SQLException {
        service.dropTable();
    }

    @Override
    public void select(int id, Consumer<ResultSet> consumer) throws SQLException {
        service.select(id,consumer);
    }

    @Override
    public void select(UUID owner, Consumer<ResultSet> consumer) throws SQLException {
        service.select(owner,consumer);
    }

    @Override
    public void select(String owner, Consumer<ResultSet> consumer) throws SQLException {
        service.select(owner,consumer);
    }

    @Override
    public void select(World world, Consumer<ResultSet> consumer) throws SQLException {
        service.select(world,consumer);
    }

    @Override
    public void update(Machine machine) throws SQLException {
        service.update(machine);
    }

    @Override
    public void insert(Machine machine) throws SQLException {
        service.insert(machine);
    }

    @Override
    public void insertOrUpdate(Machine machine) throws SQLException {
        service.insertOrUpdate(machine);
    }

    @Override
    public void delete(int id) throws SQLException {
        service.delete(id);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return service.getConnection();
    }
}
