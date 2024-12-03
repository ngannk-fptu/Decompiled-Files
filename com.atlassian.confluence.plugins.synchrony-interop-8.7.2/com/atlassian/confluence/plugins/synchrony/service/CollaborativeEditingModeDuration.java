/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="collaborativeEditingModeDuration")
public class CollaborativeEditingModeDuration {
    @VisibleForTesting
    static final long UNKNOWN_DURATION = -1L;
    @VisibleForTesting
    static final BandanaContext CONFLUENCE_BANDANA_CONTEXT = new ConfluenceBandanaContext();
    @VisibleForTesting
    static final String LAST_MODE_CHANGE_KEY = "atlassian.confluence.last.collaborative.editing.mode.change.time";
    private final BandanaManager bandanaManager;
    private final DateProvider dateProvider;

    @Autowired
    public CollaborativeEditingModeDuration(@ComponentImport BandanaManager bandanaManager) {
        this(bandanaManager, new DateProvider());
    }

    @VisibleForTesting
    CollaborativeEditingModeDuration(BandanaManager bandanaManager, DateProvider dateProvider) {
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
        this.dateProvider = Objects.requireNonNull(dateProvider);
    }

    public long currentModeDuration(TimeUnit timeUnit) {
        Optional<Long> lastModeChangeTime = this.getLastModeChangeTime();
        if (!lastModeChangeTime.isPresent()) {
            return -1L;
        }
        long differenceInMillis = this.dateProvider.getDate().getTime() - lastModeChangeTime.get();
        return timeUnit.convert(differenceInMillis, TimeUnit.MILLISECONDS);
    }

    public void storeModeChangeTime() {
        this.bandanaManager.setValue(CONFLUENCE_BANDANA_CONTEXT, LAST_MODE_CHANGE_KEY, (Object)this.dateProvider.getDate().getTime());
    }

    private Optional<Long> getLastModeChangeTime() {
        return Optional.ofNullable((Long)this.bandanaManager.getValue(CONFLUENCE_BANDANA_CONTEXT, LAST_MODE_CHANGE_KEY));
    }

    @VisibleForTesting
    static class DateProvider {
        DateProvider() {
        }

        Date getDate() {
            return new Date();
        }
    }
}

