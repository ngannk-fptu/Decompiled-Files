/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.joda.time.Period
 *  org.joda.time.ReadablePeriod
 *  org.joda.time.format.PeriodFormatter
 *  org.joda.time.format.PeriodFormatterBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cluster.monitoring.supplier;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class RuntimeInformationSupplier
implements Supplier<Table> {
    private static final Logger log = LoggerFactory.getLogger(RuntimeInformationSupplier.class);
    private static final String MODULE_KEY = RuntimeInformationSupplier.class.getCanonicalName();
    private static final String NOT_AVAILABLE_KEY = MODULE_KEY + ".notAvailable";
    private final LocaleResolver localeResolver;
    private final I18nResolver i18n;

    public RuntimeInformationSupplier(LocaleResolver localeResolver, I18nResolver i18n) {
        this.localeResolver = Objects.requireNonNull(localeResolver);
        this.i18n = Objects.requireNonNull(i18n);
    }

    @Override
    public Table get() {
        log.debug("Capturing runtime information");
        NumberFormat percentFormat = NumberFormat.getNumberInstance(this.localeResolver.getLocale());
        percentFormat.setMaximumFractionDigits(2);
        Runtime runtimeInstance = Runtime.getRuntime();
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
        Long currentHeap = (runtimeInstance.totalMemory() - runtimeInstance.freeMemory()) / 1024L / 1024L;
        Long maxHeap = runtimeInstance.maxMemory() / 1024L / 1024L;
        String percentHeap = percentFormat.format(currentHeap.doubleValue() / maxHeap.doubleValue() * 100.0) + '%';
        Double systemLoadAverage = operatingSystem.getSystemLoadAverage();
        String systemLoadAverageValue = systemLoadAverage >= 0.0 ? String.valueOf(systemLoadAverage) : this.i18n.getText(NOT_AVAILABLE_KEY);
        Integer availableProcessors = operatingSystem.getAvailableProcessors();
        String percentLoad = systemLoadAverage >= 0.0 ? percentFormat.format(systemLoadAverage / (double)availableProcessors.intValue() * 100.0) + "%" : this.i18n.getText(NOT_AVAILABLE_KEY);
        long uptime = runtimeMXBean.getUptime();
        String formattedUptime = this.getDurationFormatter().print((ReadablePeriod)new Period(uptime));
        ImmutableMap columns = ImmutableMap.of((Object)Column.NAME.key, (Object)this.i18n.getText(Column.NAME.i18nKey), (Object)Column.VALUE.key, (Object)this.i18n.getText(Column.VALUE.i18nKey));
        ImmutableMap rows = ImmutableMap.builder().put((Object)Column.CURRENT_HEAP.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.CURRENT_HEAP.i18nKey), (Object)(currentHeap + "Mb"))).put((Object)Column.MAX_HEAP.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.MAX_HEAP.i18nKey), (Object)(maxHeap + "Mb"))).put((Object)Column.PERCENT_HEAP.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.PERCENT_HEAP.i18nKey), (Object)percentHeap)).put((Object)Column.SYSTEM_LOAD_AVERAGE.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.SYSTEM_LOAD_AVERAGE.i18nKey), (Object)systemLoadAverageValue)).put((Object)Column.AVAILABLE_PROCESSORS.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.AVAILABLE_PROCESSORS.i18nKey), (Object)availableProcessors.toString())).put((Object)Column.PERCENT_LOAD.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.PERCENT_LOAD.i18nKey), (Object)percentLoad)).put((Object)Column.UPTIME.key, (Object)ImmutableList.of((Object)this.i18n.getText(Column.UPTIME.i18nKey), (Object)formattedUptime)).build();
        return new Table((Map)columns, (Map)rows);
    }

    PeriodFormatter getDurationFormatter() {
        ResourceBundle b = ResourceBundle.getBundle("org.joda.time.format.messages", this.localeResolver.getLocale());
        PeriodFormatter pf = new PeriodFormatterBuilder().appendYears().appendSuffix(b.getString("PeriodFormat.year"), b.getString("PeriodFormat.years")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).appendMonths().appendSuffix(b.getString("PeriodFormat.month"), b.getString("PeriodFormat.months")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).appendWeeks().appendSuffix(b.getString("PeriodFormat.week"), b.getString("PeriodFormat.weeks")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).appendDays().appendSuffix(b.getString("PeriodFormat.day"), b.getString("PeriodFormat.days")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).appendHours().appendSuffix(b.getString("PeriodFormat.hour"), b.getString("PeriodFormat.hours")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).appendMinutes().appendSuffix(b.getString("PeriodFormat.minute"), b.getString("PeriodFormat.minutes")).appendSeparator(b.getString("PeriodFormat.commaspace"), b.getString("PeriodFormat.spaceandspace")).toFormatter();
        return pf;
    }

    private static enum Column {
        NAME("name"),
        VALUE("value"),
        CURRENT_HEAP("currentHeap"),
        MAX_HEAP("maxHeap"),
        PERCENT_HEAP("percentHeap"),
        SYSTEM_LOAD_AVERAGE("systemLoadAverage"),
        AVAILABLE_PROCESSORS("availableProcessors"),
        PERCENT_LOAD("percentLoad"),
        UPTIME("uptime");

        private final String key;
        private final String i18nKey;

        private Column(String key) {
            this.key = Objects.requireNonNull(key);
            this.i18nKey = MODULE_KEY + '.' + this.key;
        }
    }
}

