package bd.pablo.redwins.machine.task;

import bd.pablo.redwins.machine.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class TaskBase {

    private BukkitTask task;
    protected boolean async;
    protected long ticks = 1L;

    protected TaskBase(boolean async) {
        this.async = async;
    }

    public void start(){
        stop();
        BukkitRunnable runnable = new BukkitRunnable(){

            @Override
            public void run() {
                TaskBase.this.run();
            }

        };

        if(async){
            task = runnable.runTaskTimerAsynchronously(Main.getInstance(),ticks, ticks);
        }else {
            task = runnable.runTaskTimer(Main.getInstance(),ticks, ticks);
        }
    }

    protected abstract void run();

    public void stop(){
        if(task==null)return;
        task.cancel();
        task = null;
    }
}
