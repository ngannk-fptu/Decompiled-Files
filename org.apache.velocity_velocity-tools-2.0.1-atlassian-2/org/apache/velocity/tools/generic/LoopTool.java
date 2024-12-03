/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools.generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import org.apache.velocity.tools.ClassUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.config.ValidScope;

@DefaultKey(value="loop")
@ValidScope(value={"request"})
public class LoopTool {
    private Stack<ManagedIterator> iterators = new Stack();
    private ManagedIterator last;
    private Map<String, Object> lastSyncedValues;

    public ManagedIterator watch(Object obj) {
        Iterator iterator = LoopTool.getIterator(obj);
        if (iterator == null) {
            return null;
        }
        ManagedIterator managed = this.manage(iterator, null);
        this.iterators.push(managed);
        this.last = managed;
        return managed;
    }

    public ManagedIterator watch(Object obj, String name) {
        if (name == null) {
            return null;
        }
        Iterator iterator = LoopTool.getIterator(obj);
        if (iterator == null) {
            return null;
        }
        ManagedIterator managed = this.manage(iterator, name);
        this.iterators.push(managed);
        this.last = managed;
        return managed;
    }

    public ManagedIterator sync(Object main, Object synced) {
        return this.watch(main).sync(synced);
    }

    protected ManagedIterator manage(Iterator iterator, String name) {
        return new ManagedIterator(name, iterator, this);
    }

    public void stop() {
        if (!this.iterators.empty()) {
            this.iterators.peek().stop();
        }
    }

    public void stop(String name) {
        for (ManagedIterator iterator : this.iterators) {
            if (!iterator.getName().equals(name)) continue;
            iterator.stop();
            break;
        }
    }

    public void stopTo(String name) {
        if (!this.iterators.empty()) {
            ManagedIterator iterator;
            Stack<ManagedIterator> backup = new Stack<ManagedIterator>();
            boolean found = false;
            while (!found && !this.iterators.empty()) {
                iterator = this.iterators.pop();
                if (iterator.getName().equals(name)) {
                    found = true;
                    iterator.stop();
                    continue;
                }
                backup.push(iterator);
            }
            while (!backup.empty()) {
                iterator = (ManagedIterator)backup.pop();
                this.iterators.push(iterator);
                if (!found) continue;
                iterator.stop();
            }
        }
    }

    public void stopAll() {
        for (ManagedIterator iterator : this.iterators) {
            iterator.stop();
        }
    }

    public void skip(int number) {
        if (!this.iterators.empty()) {
            this.skip(number, this.iterators.peek());
        }
    }

    public void skip(int number, String name) {
        ManagedIterator iterator = this.findIterator(name);
        if (iterator != null) {
            this.skip(number, iterator);
        }
    }

    private void skip(int number, ManagedIterator iterator) {
        for (int i = 0; i < number && iterator.hasNext(); ++i) {
            iterator.next();
        }
    }

    public Boolean isFirst() {
        if (this.last != null) {
            return this.last.isFirst();
        }
        return null;
    }

    public Boolean isFirst(String name) {
        ManagedIterator iterator = this.findIterator(name);
        if (iterator != null) {
            return iterator.isFirst();
        }
        return null;
    }

    public Boolean getFirst() {
        return this.isFirst();
    }

    public Boolean isLast() {
        if (this.last != null) {
            return this.last.isLast();
        }
        return null;
    }

    public Boolean isLast(String name) {
        ManagedIterator iterator = this.findIterator(name);
        if (iterator != null) {
            return iterator.isLast();
        }
        return null;
    }

    public Boolean getLast() {
        return this.isLast();
    }

    public Object get(String key) {
        Object syncedValue;
        for (int i = this.iterators.size() - 1; i >= 0; --i) {
            ManagedIterator iterator = (ManagedIterator)this.iterators.get(i);
            if (!iterator.isSyncedWith(key)) continue;
            return iterator.get(key);
        }
        if (this.lastSyncedValues != null && (syncedValue = this.lastSyncedValues.get(key)) != null) {
            return syncedValue;
        }
        if (key == null || key.length() < 6) {
            return null;
        }
        if (key.startsWith("last_")) {
            return this.isLast(key.substring(5, key.length()));
        }
        if (key.startsWith("count_")) {
            return this.getCount(key.substring(6, key.length()));
        }
        if (key.startsWith("index_")) {
            return this.getIndex(key.substring(6, key.length()));
        }
        if (key.startsWith("first_")) {
            return this.isFirst(key.substring(6, key.length()));
        }
        return null;
    }

    public Object get(String name, String synced) {
        ManagedIterator iterator = this.findIterator(name);
        if (iterator != null) {
            return iterator.get(synced);
        }
        return null;
    }

    public Integer getIndex() {
        Integer count = this.getCount();
        if (count == null || count == 0) {
            return null;
        }
        return count - 1;
    }

    public Integer getIndex(String name) {
        Integer count = this.getCount(name);
        if (count == null || count == 0) {
            return null;
        }
        return count - 1;
    }

    public Integer getCount() {
        if (this.last != null) {
            return this.last.getCount();
        }
        return null;
    }

    public Integer getCount(String name) {
        ManagedIterator iterator = this.findIterator(name);
        if (iterator != null) {
            return iterator.getCount();
        }
        return null;
    }

    public ManagedIterator getThis() {
        return this.last;
    }

    public int getDepth() {
        return this.iterators.size();
    }

    protected ManagedIterator findIterator(String name) {
        for (ManagedIterator iterator : this.iterators) {
            if (!iterator.getName().equals(name)) continue;
            return iterator;
        }
        return null;
    }

    protected ManagedIterator pop() {
        ManagedIterator i = this.iterators.pop();
        this.lastSyncedValues = i.getLastSyncedValues();
        return i;
    }

    protected static Iterator getIterator(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return ClassUtils.getIterator(obj);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public static class SyncedIterator {
        private Iterator iterator;
        private Object current;

        public SyncedIterator(Iterator iterator) {
            if (iterator == null) {
                throw new NullPointerException("Cannot synchronize a null Iterator");
            }
            this.iterator = iterator;
        }

        public void shift() {
            this.current = this.iterator.hasNext() ? this.iterator.next() : null;
        }

        public Object get() {
            return this.current;
        }
    }

    public static class Equals
    extends Comparison {
        public Equals(Object compare) {
            super(compare);
        }

        @Override
        public boolean test(Object value) {
            if (value == null) {
                return false;
            }
            if (this.compare.equals(value)) {
                return true;
            }
            if (value.getClass().equals(this.compare.getClass())) {
                return false;
            }
            return String.valueOf(value).equals(String.valueOf(this.compare));
        }
    }

    public static abstract class Comparison
    implements Condition {
        protected Object compare;

        public Comparison(Object compare) {
            if (compare == null) {
                throw new IllegalArgumentException("Condition must have something to compare to");
            }
            this.compare = compare;
        }
    }

    public static interface Condition {
        public boolean test(Object var1);
    }

    public static class ActionCondition {
        protected Condition condition;
        protected Action action;

        public ActionCondition(Action action, Condition condition) {
            if (condition == null || action == null) {
                throw new IllegalArgumentException("Condition and Action must both not be null");
            }
            this.condition = condition;
            this.action = action;
        }

        public boolean matches(Object value) {
            return this.condition.test(value);
        }
    }

    public static enum Action {
        EXCLUDE,
        STOP;

    }

    public static class ManagedIterator
    implements Iterator {
        private String name;
        private Iterator iterator;
        private LoopTool owner;
        private boolean stopped = false;
        private Boolean first = null;
        private int count = 0;
        private Object next;
        private List<ActionCondition> conditions;
        private Map<String, SyncedIterator> synced;

        public ManagedIterator(String name, Iterator iterator, LoopTool owner) {
            this.name = name == null ? "loop" + owner.getDepth() : name;
            this.iterator = iterator;
            this.owner = owner;
        }

        public String getName() {
            return this.name;
        }

        public boolean isFirst() {
            return this.first == null || this.first != false;
        }

        public boolean isLast() {
            return !this.hasNext(false);
        }

        public boolean getFirst() {
            return this.isFirst();
        }

        public boolean getLast() {
            return this.isLast();
        }

        @Override
        public boolean hasNext() {
            return this.hasNext(true);
        }

        public boolean getHasNext() {
            return this.hasNext(false);
        }

        private boolean hasNext(boolean popWhenDone) {
            if (this.stopped) {
                return false;
            }
            if (this.next != null) {
                return true;
            }
            return this.cacheNext(popWhenDone);
        }

        private boolean cacheNext(boolean popWhenDone) {
            if (!this.iterator.hasNext()) {
                if (popWhenDone) {
                    this.owner.pop();
                    this.stop();
                }
                return false;
            }
            this.next = this.iterator.next();
            if (this.conditions != null) {
                for (ActionCondition condition : this.conditions) {
                    if (!condition.matches(this.next)) continue;
                    switch (condition.action) {
                        case EXCLUDE: {
                            return this.cacheNext(popWhenDone);
                        }
                        case STOP: {
                            this.stop();
                            return false;
                        }
                    }
                    throw new IllegalStateException("ActionConditions should never have a null Action");
                }
            }
            return true;
        }

        private void shiftSynced() {
            if (this.synced != null) {
                for (SyncedIterator parallel : this.synced.values()) {
                    parallel.shift();
                }
            }
        }

        public boolean isSyncedWith(String name) {
            if (this.synced == null) {
                return false;
            }
            return this.synced.containsKey(name);
        }

        public Object get(String name) {
            if (this.synced == null) {
                return null;
            }
            SyncedIterator parallel = this.synced.get(name);
            if (parallel == null) {
                return null;
            }
            return parallel.get();
        }

        public int getCount() {
            return this.count;
        }

        public int getIndex() {
            return this.count - 1;
        }

        public Object next() {
            if (this.next == null && !this.cacheNext(true)) {
                throw new NoSuchElementException("There are no more valid elements in this iterator");
            }
            if (this.first == null) {
                this.first = Boolean.TRUE;
            } else if (this.first.booleanValue()) {
                this.first = Boolean.FALSE;
            }
            ++this.count;
            Object value = this.next;
            this.next = null;
            this.shiftSynced();
            return value;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove is not currently supported");
        }

        public void stop() {
            this.stopped = true;
            this.next = null;
        }

        public ManagedIterator exclude(Object compare) {
            return this.condition(new ActionCondition(Action.EXCLUDE, new Equals(compare)));
        }

        public ManagedIterator stop(Object compare) {
            return this.condition(new ActionCondition(Action.STOP, new Equals(compare)));
        }

        public ManagedIterator condition(ActionCondition condition) {
            if (condition == null) {
                return null;
            }
            if (this.conditions == null) {
                this.conditions = new ArrayList<ActionCondition>();
            }
            this.conditions.add(condition);
            return this;
        }

        public ManagedIterator sync(Object iterable) {
            return this.sync(iterable, "synced");
        }

        public ManagedIterator sync(Object iterable, String name) {
            Iterator parallel = LoopTool.getIterator(iterable);
            if (parallel == null) {
                return null;
            }
            if (this.synced == null) {
                this.synced = new HashMap<String, SyncedIterator>();
            }
            this.synced.put(name, new SyncedIterator(parallel));
            return this;
        }

        public Map<String, Object> getLastSyncedValues() {
            if (this.synced == null) {
                return null;
            }
            HashMap<String, Object> syncs = new HashMap<String, Object>();
            for (String key : this.synced.keySet()) {
                syncs.put(key, this.synced.get(key).get());
            }
            return syncs;
        }

        public String toString() {
            return ManagedIterator.class.getSimpleName() + ':' + this.getName();
        }
    }
}

