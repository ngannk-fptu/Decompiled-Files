/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletContext
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.EventCheck;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

public interface ApplicationEventCheck
extends EventCheck {
    public void check(@Nonnull JohnsonEventContainer var1, @Nonnull ServletContext var2);
}

