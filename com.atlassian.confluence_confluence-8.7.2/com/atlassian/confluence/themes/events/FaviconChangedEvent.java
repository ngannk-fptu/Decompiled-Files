/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.themes.events.LookAndFeelEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FaviconChangedEvent
extends LookAndFeelEvent {
    private static final long serialVersionUID = 1720743159414924639L;
    private Action action;

    public FaviconChangedEvent(Object src, @NonNull Action action) {
        super(src, null);
        this.action = action;
    }

    public @NonNull Action getAction() {
        return this.action;
    }

    public static enum Action {
        UPLOADED,
        RESET;

    }
}

