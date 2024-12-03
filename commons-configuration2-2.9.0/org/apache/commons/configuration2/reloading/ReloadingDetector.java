/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.reloading;

public interface ReloadingDetector {
    public boolean isReloadingRequired();

    public void reloadingPerformed();
}

