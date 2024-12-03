/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.Elisions;
import java.util.Optional;
import javax.annotation.Nonnull;

public interface AlertWithElisions
extends Alert {
    @Nonnull
    public Optional<Elisions> getElisions();
}

