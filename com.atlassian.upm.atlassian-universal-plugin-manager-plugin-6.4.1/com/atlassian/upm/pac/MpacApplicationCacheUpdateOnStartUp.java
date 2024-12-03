/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.pac;

import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.pac.MpacApplicationCacheManager;
import org.springframework.beans.factory.DisposableBean;

public class MpacApplicationCacheUpdateOnStartUp
implements UpmProductDataStartupComponent,
DisposableBean {
    private final MpacApplicationCacheManager mpacApplicationCacheManager;

    public MpacApplicationCacheUpdateOnStartUp(MpacApplicationCacheManager mpacApplicationCacheManager) {
        this.mpacApplicationCacheManager = mpacApplicationCacheManager;
    }

    @Override
    public void onStartupWithProductData() {
        this.mpacApplicationCacheManager.populateCache();
    }

    public void destroy() {
        this.mpacApplicationCacheManager.reset();
    }
}

