/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.util.Comparator;
import java.util.List;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Source;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Feed
extends Source {
    public List<Entry> getEntries();

    public Feed addEntry(Entry var1);

    public Entry addEntry();

    public Feed insertEntry(Entry var1);

    public Entry insertEntry();

    public Source getAsSource();

    public Feed sortEntriesByUpdated(boolean var1);

    public Feed sortEntriesByEdited(boolean var1);

    public Feed sortEntries(Comparator<Entry> var1);

    public Entry getEntry(String var1);
}

