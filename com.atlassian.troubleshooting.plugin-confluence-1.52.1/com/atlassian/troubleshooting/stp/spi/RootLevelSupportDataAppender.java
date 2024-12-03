/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.spi;

import com.atlassian.troubleshooting.stp.spi.SupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;

public abstract class RootLevelSupportDataAppender
implements SupportDataAppender<Void> {
    @Override
    public void addSupportData(SupportDataBuilder builder, Void context) {
        this.addSupportData(builder);
    }

    protected abstract void addSupportData(SupportDataBuilder var1);
}

