/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.streams.api.common.Fold
 *  com.atlassian.streams.spi.BoundedTreeSet
 *  com.atlassian.streams.spi.Evictor
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.streams.api.common.Fold;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.spi.BoundedTreeSet;
import com.atlassian.streams.spi.Evictor;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BoundedActivityItemTreeSet
extends BoundedTreeSet<ActivityItem> {
    private static final Logger log = LoggerFactory.getLogger(BoundedActivityItemTreeSet.class);
    private final Evictor<ConfluenceEntityObject> evictor;

    public BoundedActivityItemTreeSet(Evictor<ConfluenceEntityObject> evictor, int maxSize, Comparator<ActivityItem> activityItemComparator) {
        super(maxSize, activityItemComparator);
        this.evictor = (Evictor)Preconditions.checkNotNull(evictor, (Object)"evictor");
    }

    public boolean remove(Object o) {
        if (o instanceof ActivityItem) {
            return this.remove((ActivityItem)o);
        }
        log.warn("Attempting to remove a non-ActivityItem from the set");
        return super.remove(o);
    }

    public boolean remove(ActivityItem activityItem) {
        this.evictor.apply((Object)activityItem.getEntity());
        return super.remove((Object)activityItem);
    }

    public boolean removeAll(Collection<?> os) {
        return (Boolean)Fold.foldl((Iterable)ImmutableList.copyOf(os), (Object)false, (o, anyRemoved) -> this.remove(o) || anyRemoved != false);
    }

    public void clear() {
        this.removeAll((Collection<?>)ImmutableSet.copyOf((Collection)((Object)this)));
    }

    public boolean retainAll(Collection<?> os) {
        return (Boolean)Fold.foldl((Iterable)ImmutableList.copyOf((Collection)((Object)this)), (Object)false, (o, anyRemoved) -> {
            if (!os.contains(o)) {
                return this.remove(o) || anyRemoved != false;
            }
            return anyRemoved;
        });
    }
}

