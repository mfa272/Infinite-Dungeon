package infinitedungeon.engine;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

class TaskQueue {

    private long taskTime;
    private final PriorityQueue<PriorityQueueEntry<TimedTask, Long>> queue;
    private boolean stop;

    public class PriorityQueueEntry<K extends TimedTask, V> extends AbstractMap.SimpleEntry<K, Long>
            implements Comparable<PriorityQueueEntry<K, V>> {

        public PriorityQueueEntry(K key, Long value) {
            super(key, value);
        }

        @Override
        public K getKey() {
            return super.getKey();
        }

        @Override
        public Long getValue() {
            return super.getValue();
        }

        @Override
        public int compareTo(PriorityQueueEntry<K, V> o) {
            return getValue().compareTo(o.getValue());
        }
    }

    public TaskQueue(long taskTime, TimeUnit unit) {
        this();
        this.taskTime = TimeUnit.MILLISECONDS.convert(taskTime, unit);
    }

    public TaskQueue() {
        queue = new PriorityQueue<>();
    }

    public void setTaskTime(long taskTime, TimeUnit unit) {
        long delay = TimeUnit.MILLISECONDS.convert(taskTime, unit) - this.taskTime;
        this.taskTime = TimeUnit.MILLISECONDS.convert(taskTime, unit);
        for (PriorityQueueEntry<TimedTask, Long> e : queue) {
            e.setValue(e.getValue() + delay);
        }
    }

    public void add(TimedTask t) {
        t.setFinished(false);
        PriorityQueueEntry<TimedTask, Long> e = new PriorityQueueEntry<>(t, taskTime + t.getMilliseconds());
        queue.add(e);
    }

    public void cancelTasks() {
        stop = true;
    }

    public void performTasks(long timestamp, TimeUnit unit) {
        timestamp = TimeUnit.MILLISECONDS.convert(timestamp, unit);
        PriorityQueueEntry<TimedTask, Long> e = queue.peek();
        while (e != null) {
            TimedTask task = e.getKey();
            if (timestamp < e.getValue()) {
                break;
            }
            task.run();
            queue.remove(e);

            if (task.toReschedule()) {
                add(e.getKey());
            } else {
                e.getKey().setFinished(true);
                ArrayDeque<TimedTask> chain = e.getKey().getChain();
                TimedTask t = chain.poll();
                while (t != null) {
                    add(t);
                    t = chain.poll();
                }
            }
            if (stop) {
                queue.clear();
                stop = false;
            }
            e = queue.peek();
        }
        taskTime = timestamp;
    }
}
