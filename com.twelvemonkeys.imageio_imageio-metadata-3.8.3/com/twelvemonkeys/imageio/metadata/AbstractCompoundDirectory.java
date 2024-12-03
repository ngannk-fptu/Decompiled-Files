/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.AbstractDirectory;
import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.lang.Validate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractCompoundDirectory
extends AbstractDirectory
implements CompoundDirectory {
    private final List<Directory> directories = new ArrayList<Directory>();

    protected AbstractCompoundDirectory(Collection<? extends Directory> collection) {
        super(null);
        if (collection != null) {
            this.directories.addAll(Validate.noNullElements(collection));
        }
    }

    @Override
    public Directory getDirectory(int n) {
        return this.directories.get(n);
    }

    @Override
    public int directoryCount() {
        return this.directories.size();
    }

    @Override
    public Entry getEntryById(Object object) {
        for (Directory directory : this.directories) {
            Entry entry = directory.getEntryById(object);
            if (entry == null) continue;
            return entry;
        }
        return null;
    }

    @Override
    public Entry getEntryByFieldName(String string) {
        for (Directory directory : this.directories) {
            Entry entry = directory.getEntryByFieldName(string);
            if (entry == null) continue;
            return entry;
        }
        return null;
    }

    @Override
    public Iterator<Entry> iterator() {
        return new Iterator<Entry>(){
            Iterator<Directory> directoryIterator;
            Iterator<Entry> current;
            {
                this.directoryIterator = AbstractCompoundDirectory.this.directories.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.current != null && this.current.hasNext() || this.directoryIterator.hasNext() && (this.current = this.directoryIterator.next().iterator()).hasNext();
            }

            @Override
            public Entry next() {
                this.hasNext();
                return this.current.next();
            }

            @Override
            public void remove() {
                this.current.remove();
            }
        };
    }

    @Override
    public boolean add(Entry entry) {
        throw new UnsupportedOperationException("Directory is read-only");
    }

    @Override
    public boolean remove(Object object) {
        throw new UnsupportedOperationException("Directory is read-only");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public int size() {
        int n = 0;
        for (Directory directory : this.directories) {
            n += directory.size();
        }
        return n;
    }

    @Override
    public String toString() {
        return String.format("%s%s", this.getClass().getSimpleName(), this.directories.toString());
    }

    @Override
    public int hashCode() {
        int n = 0;
        for (Directory directory : this.directories) {
            n ^= directory.hashCode();
        }
        return n;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        CompoundDirectory compoundDirectory = (CompoundDirectory)object;
        if (this.directoryCount() != compoundDirectory.directoryCount()) {
            return false;
        }
        for (int i = 0; i < this.directoryCount(); ++i) {
            if (this.getDirectory(i).equals(compoundDirectory.getDirectory(i))) continue;
            return false;
        }
        return true;
    }
}

