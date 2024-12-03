/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.servlet;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdminUi {
    private static final Logger log = LoggerFactory.getLogger(AdminUi.class);
    private final Map<String, Object> essentials;

    public AdminUi(Map<String, Object> essentials) {
        this.essentials = (Map)Preconditions.checkNotNull(essentials);
    }

    boolean isEnabled() {
        Map unavailable = Maps.filterEntries(this.essentials, (Predicate)new UnavailableServicePredicate());
        if (!unavailable.isEmpty()) {
            log.debug("The admin UI is disabled because of the following services not being available:\n{}", unavailable.keySet());
        }
        return unavailable.isEmpty();
    }

    static boolean isDevModeEnabled() {
        return Boolean.getBoolean("atlassian.dev.mode");
    }

    private static class UnavailableServicePredicate
    implements Predicate<Map.Entry<String, Object>> {
        private UnavailableServicePredicate() {
        }

        public boolean apply(Map.Entry<String, Object> entry) {
            try {
                entry.getValue().toString();
                return false;
            }
            catch (RuntimeException e) {
                if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) {
                    if (AdminUi.isDevModeEnabled()) {
                        log.warn("Service is unavailable, admin UI will be disabled.", (Throwable)e);
                    }
                    return true;
                }
                throw e;
            }
        }
    }
}

