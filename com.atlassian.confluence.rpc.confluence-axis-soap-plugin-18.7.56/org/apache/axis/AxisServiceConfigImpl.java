/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.AxisServiceConfig;

public class AxisServiceConfigImpl
implements AxisServiceConfig {
    private String methods;

    public void setAllowedMethods(String methods) {
        this.methods = methods;
    }

    public String getAllowedMethods() {
        return this.methods;
    }
}

