package infinitedungeon.engine;

import java.util.ArrayDeque;

public abstract class TimedTask implements Runnable {

    private final ArrayDeque<TimedTask> chain;
    private long milliseconds;
    private boolean reschedule;
    private boolean finished;

    public TimedTask(long delay) {
        this.milliseconds = delay;
        chain = new ArrayDeque<>();
    }

    public final boolean toReschedule() {
        if (milliseconds == 0) {
            return false;
        }
        return reschedule;
    }

    public void setToReschedule(boolean toReschedule) {
        reschedule = toReschedule;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ArrayDeque<TimedTask> getChain() {
        return chain;
    }

    public void chain(TimedTask t) {
        chain.add(t);
    }
}
