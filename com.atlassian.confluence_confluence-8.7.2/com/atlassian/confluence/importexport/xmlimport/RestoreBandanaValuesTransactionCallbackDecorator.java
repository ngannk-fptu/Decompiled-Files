/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.bandana.BandanaPersister
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

@Deprecated
public class RestoreBandanaValuesTransactionCallbackDecorator<T>
implements TransactionCallback<T> {
    private static final Logger log = LoggerFactory.getLogger(RestoreBandanaValuesTransactionCallbackDecorator.class);
    private static final Collection<BandanaIdentifier> BANDANA_KEYS_TO_RESTORE = ImmutableList.of((Object)new BandanaIdentifier(ConfluenceBandanaContext.GLOBAL_CONTEXT, "confluence.server.installation.date"));
    private static final Collection<PropertyDescriptor> SETTING_PROPERTIES_TO_RESTORE = ImmutableList.of((Object)RestoreBandanaValuesTransactionCallbackDecorator.createSettingsProperty("baseUrl"));
    private final TransactionCallback<T> delegate;
    private final BandanaManager bandanaManager;
    private final BandanaPersister bandanaPersister;

    public RestoreBandanaValuesTransactionCallbackDecorator(BandanaManager bandanaManager, BandanaPersister bandanaPersister, TransactionCallback<T> delegate) {
        this.bandanaManager = bandanaManager;
        this.bandanaPersister = bandanaPersister;
        this.delegate = delegate;
    }

    public T doInTransaction(TransactionStatus status) {
        log.info("Recording Bandana entries to be preserved");
        Map<BandanaIdentifier, Object> originalBandanaEntries = this.saveBandanaEntries();
        Settings originalSettings = (Settings)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "atlassian.confluence.settings");
        Object result = this.delegate.doInTransaction(status);
        log.info("Restoring preserved Bandana entries");
        this.bandanaPersister.flushCaches();
        this.restoreBandanaEntries(originalBandanaEntries);
        this.restoreSettings(originalSettings);
        log.info("Bandana restoration complete");
        return (T)result;
    }

    private Map<BandanaIdentifier, Object> saveBandanaEntries() {
        HashMap originalBandanaEntries = Maps.newHashMap();
        for (BandanaIdentifier bandanaId : BANDANA_KEYS_TO_RESTORE) {
            log.debug("Backing up Bandana entry {}", (Object)bandanaId);
            originalBandanaEntries.put(bandanaId, this.bandanaManager.getValue(bandanaId.getBandanaContext(), bandanaId.getBandanaKey()));
        }
        return originalBandanaEntries;
    }

    private void restoreBandanaEntries(Map<BandanaIdentifier, Object> originalBandanaEntries) {
        for (Map.Entry<BandanaIdentifier, Object> bandanaEntry : originalBandanaEntries.entrySet()) {
            BandanaIdentifier bandanaId = bandanaEntry.getKey();
            BandanaContext bandanaContext = bandanaId.getBandanaContext();
            String bandanaKey = bandanaId.getBandanaKey();
            log.debug("Restoring Bandana entry {}", (Object)bandanaId);
            this.bandanaManager.setValue(bandanaContext, bandanaKey, bandanaEntry.getValue());
        }
    }

    private void restoreSettings(Settings originalSettings) {
        if (originalSettings == null) {
            return;
        }
        Settings settings = (Settings)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "atlassian.confluence.settings");
        if (settings == null) {
            return;
        }
        for (PropertyDescriptor property : SETTING_PROPERTIES_TO_RESTORE) {
            try {
                property.getWriteMethod().invoke((Object)settings, property.getReadMethod().invoke((Object)originalSettings, new Object[0]));
            }
            catch (ReflectiveOperationException ex) {
                throw Throwables.propagate((Throwable)ex);
            }
        }
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "atlassian.confluence.settings", (Object)settings);
    }

    private static PropertyDescriptor createSettingsProperty(String propertyName) {
        try {
            return new PropertyDescriptor(propertyName, Settings.class);
        }
        catch (IntrospectionException ex) {
            throw Throwables.propagate((Throwable)ex);
        }
    }

    private static class BandanaIdentifier {
        private final BandanaContext bandanaContext;
        private final String bandanaKey;

        private BandanaIdentifier(BandanaContext bandanaContext, String bandanaKey) {
            this.bandanaContext = bandanaContext;
            this.bandanaKey = bandanaKey;
        }

        public BandanaContext getBandanaContext() {
            return this.bandanaContext;
        }

        public String getBandanaKey() {
            return this.bandanaKey;
        }

        public String toString() {
            return "{Context: " + this.bandanaContext + ", Key: " + this.bandanaKey + "}";
        }
    }
}

