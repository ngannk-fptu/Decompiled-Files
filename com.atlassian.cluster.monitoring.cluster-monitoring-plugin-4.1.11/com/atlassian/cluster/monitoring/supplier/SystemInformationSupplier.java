/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cluster.monitoring.supplier;

import com.atlassian.annotations.Internal;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class SystemInformationSupplier
implements Supplier<Table> {
    private static final Logger log = LoggerFactory.getLogger(SystemInformationSupplier.class);
    private static final String MODULE_KEY = SystemInformationSupplier.class.getCanonicalName();
    private final I18nResolver i18n;

    public SystemInformationSupplier(I18nResolver i18n) {
        this.i18n = Objects.requireNonNull(i18n);
    }

    @Override
    public Table get() {
        log.debug("Capturing system information");
        Properties systemProperties = System.getProperties();
        ImmutableMap headers = ImmutableMap.of((Object)Column.KEY.key, (Object)this.i18n.getText(Column.KEY.i18nKey), (Object)Column.VALUE.key, (Object)this.i18n.getText(Column.VALUE.i18nKey));
        LinkedHashMap data = Maps.newLinkedHashMap();
        for (Map.Entry<Object, Object> entry : systemProperties.entrySet()) {
            data.put(entry.getKey().toString(), ImmutableList.of((Object)entry.getKey().toString(), (Object)entry.getValue().toString()));
        }
        return new Table((Map)headers, (Map)data);
    }

    private static enum Column {
        KEY("key"),
        VALUE("value");

        private final String key;
        private final String i18nKey;

        private Column(String key) {
            this.key = Objects.requireNonNull(key);
            this.i18nKey = MODULE_KEY + '.' + this.key;
        }
    }
}

