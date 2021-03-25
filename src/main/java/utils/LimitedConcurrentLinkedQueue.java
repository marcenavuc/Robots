package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LimitedConcurrentLinkedQueue<E> extends ConcurrentLinkedQueue<E> {

    private final int limit;

    public LimitedConcurrentLinkedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }

    public Iterable<E> subList(int startFrom, int count) {
        ArrayList<E> temp = new ArrayList<>();
        int i = 0;
        Iterator<E> t = super.iterator();
        while (t.hasNext() && i++ < this.size() && count > 0) {
            E value = t.next();
            if (i >= startFrom) {
                temp.add(value);
                count--;
            }
        }
        return temp;
    }
}
