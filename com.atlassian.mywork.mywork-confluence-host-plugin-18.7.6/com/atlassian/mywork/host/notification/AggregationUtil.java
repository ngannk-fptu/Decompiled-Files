/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Notification
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.Multimaps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.mywork.host.notification;

import com.atlassian.mywork.model.Notification;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AggregationUtil {
    public static ImmutableListMultimap<AggregateKey, Notification> aggregate(Iterable<Notification> notifications) {
        return Multimaps.index(notifications, (Function)new Function<Notification, AggregateKey>(){

            public AggregateKey apply(Notification from) {
                return new AggregateKey(AggregationUtil.or(AggregationUtil.or(from.getGroupingId(), from.getGlobalId()), String.valueOf(from.getId())), from.getAction());
            }
        });
    }

    public static int aggregateCount(Iterable<Notification> notifications) {
        HashSet objects = Sets.newHashSet();
        for (Notification notification : notifications) {
            int hashCode = AggregationUtil.or(AggregationUtil.or(notification.getGroupingId(), notification.getGlobalId()), String.valueOf(notification.getId())).hashCode();
            hashCode = hashCode * 739 + (notification.getAction() != null ? notification.getAction().hashCode() : 0);
            objects.add(hashCode);
        }
        return objects.size();
    }

    private static <T> T or(T a, T b) {
        return a != null ? a : b;
    }

    public static class AggregateKey {
        public final String id;
        public final String action;

        public AggregateKey(String id, String action) {
            this.id = id;
            this.action = action;
        }

        public String toString() {
            return this.id + "-" + this.action;
        }

        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals((Object)this, (Object)o, (String[])new String[0]);
        }

        public int hashCode() {
            return new HashCodeBuilder().append((Object)this.id).append((Object)this.action).toHashCode();
        }
    }
}

