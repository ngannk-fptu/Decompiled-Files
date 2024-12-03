/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.properties;

import com.atlassian.troubleshooting.stp.properties.PropertyStore;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;

public interface SupportDataAppenderManager {
    public void addSupportData(PropertyStore var1, SupportDataDetail var2);
}

