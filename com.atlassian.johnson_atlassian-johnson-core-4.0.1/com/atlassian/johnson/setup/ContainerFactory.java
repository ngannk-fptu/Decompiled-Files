/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.setup;

import com.atlassian.johnson.JohnsonEventContainer;
import javax.annotation.Nonnull;

public interface ContainerFactory {
    @Nonnull
    public JohnsonEventContainer create();
}

