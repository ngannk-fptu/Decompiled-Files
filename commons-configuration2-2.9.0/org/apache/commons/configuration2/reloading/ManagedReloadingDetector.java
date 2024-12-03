/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.reloading.ManagedReloadingDetectorMBean;
import org.apache.commons.configuration2.reloading.ReloadingDetector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManagedReloadingDetector
implements ReloadingDetector,
ManagedReloadingDetectorMBean {
    private final Log log = LogFactory.getLog(ManagedReloadingDetector.class);
    private volatile boolean reloadingRequired;

    @Override
    public void reloadingPerformed() {
        this.reloadingRequired = false;
    }

    @Override
    public boolean isReloadingRequired() {
        return this.reloadingRequired;
    }

    @Override
    public void refresh() {
        this.log.info((Object)"Reloading configuration.");
        this.reloadingRequired = true;
    }
}

