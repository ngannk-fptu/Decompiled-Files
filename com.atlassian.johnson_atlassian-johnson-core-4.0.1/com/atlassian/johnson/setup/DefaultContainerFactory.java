/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.johnson.setup;

import com.atlassian.johnson.DefaultJohnsonEventContainer;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.setup.ContainerFactory;
import javax.annotation.Nonnull;

public class DefaultContainerFactory
implements ContainerFactory {
    @Override
    @Nonnull
    public JohnsonEventContainer create() {
        return new DefaultJohnsonEventContainer();
    }
}

