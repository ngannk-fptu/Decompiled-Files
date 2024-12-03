/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.Directory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.lang.Validate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractDirectory
implements Directory {
    private final List<Entry> entries = new ArrayList<Entry>();
    private final List<Entry> unmodifiable = Collections.unmodifiableList(this.entries);

    protected AbstractDirectory(Collection<? extends Entry> collection) {
        if (collection != null) {
            this.entries.addAll(Validate.noNullElements(collection));
        }
    }

    @Override
    public Entry getEntryById(Object object) {
        for (Entry entry : this) {
            if (!entry.getIdentifier().equals(object)) continue;
            return entry;
        }
        return null;
    }

    @Override
    public Entry getEntryByFieldName(String string) {
        for (Entry entry : this) {
            if (entry.getFieldName() == null || !entry.getFieldName().equals(string)) continue;
            return entry;
        }
        return null;
    }

    @Override
    public Iterator<Entry> iterator() {
        return this.isReadOnly() ? this.unmodifiable.iterator() : this.entries.iterator();
    }

    protected final void assertMutable() {
        if (this.isReadOnly()) {
            throw new UnsupportedOperationException("Directory is read-only");
        }
    }

    @Override
    public boolean add(Entry entry) {
        this.assertMutable();
        return this.entries.add(entry);
    }

    @Override
    public boolean remove(Object object) {
        this.assertMutable();
        return this.entries.remove(object);
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    public int hashCode() {
        return this.entries.hashCode();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        AbstractDirectory abstractDirectory = (AbstractDirectory)object;
        return this.entries.equals(abstractDirectory.entries);
    }

    public String toString() {
        return String.format("%s%s", this.getClass().getSimpleName(), this.entries.toString());
    }
}

