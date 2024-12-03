/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.profiling.ActivityMonitor
 *  com.atlassian.confluence.util.profiling.ActivitySnapshot
 *  com.atlassian.core.util.DateUtils
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.monitoring;

import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.confluence.util.profiling.ActivitySnapshot;
import com.atlassian.core.util.DateUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveActivitySupplier
implements Supplier<Table> {
    private static final String I18N_PREFIX = LiveActivitySupplier.class.getCanonicalName();
    private static final Logger log = LoggerFactory.getLogger(LiveActivitySupplier.class);
    private static final int MIN_THRESHOLD_MS = 1000;
    private final ActivityMonitor activityMonitor;
    private final I18NBean i18NBean;
    private static final Predicate<ActivitySnapshot> thresholdPredicate = snapshot -> System.currentTimeMillis() - snapshot.getStartTime() >= 1000L;

    public LiveActivitySupplier(ActivityMonitor activityMonitor, I18NBeanFactory i18NBeanFactory) {
        this.activityMonitor = (ActivityMonitor)Preconditions.checkNotNull((Object)activityMonitor);
        this.i18NBean = ((I18NBeanFactory)Preconditions.checkNotNull((Object)i18NBeanFactory)).getI18NBean();
    }

    @Override
    public Table get() {
        log.debug("Getting live activity");
        Collection allActivities = this.activityMonitor.snapshotCurrent();
        ArrayList passingThreshold = Lists.newArrayList((Iterable)Collections2.filter((Collection)allActivities, thresholdPredicate));
        ImmutableMap columns = ImmutableMap.builder().put((Object)Column.THREAD_ID.key, (Object)this.i18NBean.getText(Column.THREAD_ID.i18nKey)).put((Object)Column.THREAD_NAME.key, (Object)this.i18NBean.getText(Column.THREAD_NAME.i18nKey)).put((Object)Column.USER_ID.key, (Object)this.i18NBean.getText(Column.USER_ID.i18nKey)).put((Object)Column.TYPE.key, (Object)this.i18NBean.getText(Column.TYPE.i18nKey)).put((Object)Column.SUMMARY.key, (Object)this.i18NBean.getText(Column.SUMMARY.i18nKey)).put((Object)Column.DURATION.key, (Object)this.i18NBean.getText(Column.DURATION.i18nKey)).build();
        LinkedHashMap rows = Maps.newLinkedHashMap();
        for (ActivitySnapshot activitySnapshot : passingThreshold) {
            rows.put(String.valueOf(activitySnapshot.getThreadId()), ImmutableList.of((Object)String.valueOf(activitySnapshot.getThreadId()), (Object)activitySnapshot.getThreadName(), (Object)activitySnapshot.getUserId(), (Object)activitySnapshot.getType(), (Object)activitySnapshot.getSummary(), (Object)DateUtils.dateDifference((long)activitySnapshot.getStartTime(), (long)System.currentTimeMillis(), (long)4L, (ResourceBundle)this.i18NBean.getResourceBundle())));
        }
        return new Table((Map)columns, (Map)rows);
    }

    private static enum Column {
        THREAD_ID("threadId"),
        THREAD_NAME("threadName"),
        USER_ID("userId"),
        TYPE("type"),
        SUMMARY("summary"),
        DURATION("duration");

        private final String key;
        private final String i18nKey;

        private Column(String key) {
            this.key = (String)Preconditions.checkNotNull((Object)key);
            this.i18nKey = I18N_PREFIX + "." + this.key;
        }
    }
}

