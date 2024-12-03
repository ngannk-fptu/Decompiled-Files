/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.history;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.history.Revision;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

public class Revisions<N extends Number, T>
implements Streamable<Revision<N, T>> {
    private final Comparator<Revision<N, T>> NATURAL_ORDER = Comparator.naturalOrder();
    private final List<Revision<N, T>> revisions;
    private final boolean latestLast;

    private Revisions(List<? extends Revision<N, T>> revisions) {
        this(revisions, true);
    }

    private Revisions(List<? extends Revision<N, T>> revisions, boolean latestLast) {
        Assert.notNull(revisions, (String)"Revisions must not be null!");
        this.revisions = revisions.stream().sorted(latestLast ? this.NATURAL_ORDER : this.NATURAL_ORDER.reversed()).collect(StreamUtils.toUnmodifiableList());
        this.latestLast = latestLast;
    }

    public static <N extends Number, T> Revisions<N, T> of(List<? extends Revision<N, T>> revisions) {
        return new Revisions<N, T>(revisions);
    }

    public static <N extends Number, T> Revisions<N, T> none() {
        return new Revisions<N, T>(Collections.emptyList());
    }

    public Revision<N, T> getLatestRevision() {
        int index = this.latestLast ? this.revisions.size() - 1 : 0;
        return this.revisions.get(index);
    }

    public Revisions<N, T> reverse() {
        return new Revisions<N, T>(this.revisions, !this.latestLast);
    }

    @Override
    public Iterator<Revision<N, T>> iterator() {
        return this.revisions.iterator();
    }

    public List<Revision<N, T>> getContent() {
        return Collections.unmodifiableList(this.revisions);
    }
}

