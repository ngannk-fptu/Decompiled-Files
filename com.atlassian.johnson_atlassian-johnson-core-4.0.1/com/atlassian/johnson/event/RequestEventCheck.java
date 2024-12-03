/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.johnson.event;

import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.EventCheck;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public interface RequestEventCheck
extends EventCheck {
    public void check(@Nonnull JohnsonEventContainer var1, @Nonnull HttpServletRequest var2);
}

