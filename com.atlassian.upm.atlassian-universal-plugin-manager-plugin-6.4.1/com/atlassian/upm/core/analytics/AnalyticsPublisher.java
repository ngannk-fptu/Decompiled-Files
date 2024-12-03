/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.core.analytics;

import com.atlassian.upm.core.analytics.AnalyticsEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public interface AnalyticsPublisher
extends InitializingBean,
DisposableBean {
    public void publish(AnalyticsEvent var1) throws Exception;
}

