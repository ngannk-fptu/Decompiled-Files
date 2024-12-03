/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.hibernate.action.spi.Executable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;

public class ExecutableList<E extends Executable & Comparable>
implements Serializable,
Iterable<E>,
Externalizable {
    public static final int INIT_QUEUE_LIST_SIZE = 5;
    private final ArrayList<E> executables;
    private final Sorter<E> sorter;
    private final boolean requiresSorting;
    private boolean sorted;
    private transient Set<Serializable> querySpaces;

    public ExecutableList() {
        this(5);
    }

    public ExecutableList(int initialCapacity) {
        this(initialCapacity, true);
    }

    public ExecutableList(boolean requiresSorting) {
        this(5, requiresSorting);
    }

    public ExecutableList(int initialCapacity, boolean requiresSorting) {
        this.sorter = null;
        this.executables = new ArrayList(initialCapacity);
        this.querySpaces = null;
        this.requiresSorting = requiresSorting;
        this.sorted = requiresSorting;
    }

    public ExecutableList(Sorter<E> sorter) {
        this(5, sorter);
    }

    public ExecutableList(int initialCapacity, Sorter<E> sorter) {
        this.sorter = sorter;
        this.executables = new ArrayList(initialCapacity);
        this.querySpaces = null;
        this.requiresSorting = true;
        this.sorted = true;
    }

    public Set<Serializable> getQuerySpaces() {
        if (this.querySpaces == null) {
            for (Executable e : this.executables) {
                Serializable[] propertySpaces = e.getPropertySpaces();
                if (propertySpaces == null || propertySpaces.length <= 0) continue;
                if (this.querySpaces == null) {
                    this.querySpaces = new HashSet<Serializable>();
                }
                Collections.addAll(this.querySpaces, propertySpaces);
            }
            if (this.querySpaces == null) {
                return Collections.emptySet();
            }
        }
        return this.querySpaces;
    }

    public boolean isEmpty() {
        return this.executables.isEmpty();
    }

    public E remove(int index) {
        Executable e = (Executable)this.executables.remove(index);
        if (e.getPropertySpaces() != null && e.getPropertySpaces().length > 0) {
            this.querySpaces = null;
        }
        return (E)e;
    }

    public void clear() {
        this.executables.clear();
        this.querySpaces = null;
        this.sorted = this.requiresSorting;
    }

    public void removeLastN(int n) {
        if (n > 0) {
            int size = this.executables.size();
            for (Executable e : this.executables.subList(size - n, size)) {
                if (e.getPropertySpaces() == null || e.getPropertySpaces().length <= 0) continue;
                this.querySpaces = null;
                break;
            }
            this.executables.subList(size - n, size).clear();
        }
    }

    public boolean add(E executable) {
        Executable previousLast = this.sorter != null || this.executables.isEmpty() ? null : (Executable)this.executables.get(this.executables.size() - 1);
        boolean added = this.executables.add(executable);
        if (!added) {
            return false;
        }
        if (this.sorted) {
            if (this.sorter != null) {
                this.sorted = false;
            } else if (previousLast != null && ((Comparable)((Object)previousLast)).compareTo(executable) > 0) {
                this.sorted = false;
            }
        }
        Serializable[] querySpaces = executable.getPropertySpaces();
        if (this.querySpaces != null && querySpaces != null) {
            Collections.addAll(this.querySpaces, querySpaces);
        }
        return added;
    }

    public void sort() {
        if (this.sorted || !this.requiresSorting) {
            return;
        }
        if (this.sorter != null) {
            this.sorter.sort(this.executables);
        } else {
            Collections.sort(this.executables);
        }
        this.sorted = true;
    }

    public int size() {
        return this.executables.size();
    }

    public E get(int index) {
        return (E)((Executable)this.executables.get(index));
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(this.executables).iterator();
    }

    @Override
    public void writeExternal(ObjectOutput oos) throws IOException {
        oos.writeBoolean(this.sorted);
        oos.writeInt(this.executables.size());
        for (Executable e : this.executables) {
            oos.writeObject(e);
        }
        if (this.querySpaces == null) {
            oos.writeInt(-1);
        } else {
            oos.writeInt(this.querySpaces.size());
            for (Serializable querySpace : this.querySpaces) {
                oos.writeUTF(querySpace.toString());
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int numberOfQuerySpaces;
        this.sorted = in.readBoolean();
        int numberOfExecutables = in.readInt();
        this.executables.ensureCapacity(numberOfExecutables);
        if (numberOfExecutables > 0) {
            for (int i = 0; i < numberOfExecutables; ++i) {
                Executable e = (Executable)in.readObject();
                this.executables.add(e);
            }
        }
        if ((numberOfQuerySpaces = in.readInt()) < 0) {
            this.querySpaces = null;
        } else {
            this.querySpaces = new HashSet<Serializable>(CollectionHelper.determineProperSizing(numberOfQuerySpaces));
            for (int i = 0; i < numberOfQuerySpaces; ++i) {
                this.querySpaces.add((Serializable)((Object)in.readUTF()));
            }
        }
    }

    public void afterDeserialize(SessionImplementor session) {
        for (Executable e : this.executables) {
            e.afterDeserialize(session);
        }
    }

    public String toString() {
        return "ExecutableList{size=" + this.executables.size() + "}";
    }

    public static interface Sorter<E extends Executable> {
        public void sort(List<E> var1);
    }
}

