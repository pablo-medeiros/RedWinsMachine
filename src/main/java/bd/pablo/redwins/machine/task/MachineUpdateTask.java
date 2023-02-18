package bd.pablo.redwins.machine.task;

import bd.pablo.redwins.machine.Main;
import bd.pablo.redwins.machine.api.Machine;
import bd.pablo.redwins.machine.api.Manager;

import java.sql.SQLException;

public class MachineUpdateTask extends TaskBase{

    public MachineUpdateTask() {
        super(true);
        ticks = 200;
    }

    @Override
    public void run() {
        for(Machine machine : Manager.get().machines()){
            if(machine.temp>0){
                machine.temp = 0;
                try {
                    Main.getInstance().db().insertOrUpdate(machine);
                    Main.getInstance().debug().println(machine,"§aInserida ou salva no banco de dados");
                } catch (SQLException e) {
                    Main.getInstance().debug().criticalPrintln(machine,"§cOcorreu um erro ao inserir ou atualizar a maquina no banco de dados");
                    Main.getInstance().debug().exception(e);
                }
            }
        }
    }
}
