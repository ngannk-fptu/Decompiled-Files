/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes.events;

import com.atlassian.confluence.core.PersistentDecorator;
import com.atlassian.confluence.themes.events.LookAndFeelEvent;

public class DecoratorChangedEvent
extends LookAndFeelEvent {
    private static final long serialVersionUID = -9208588729759597716L;
    private final PersistentDecorator oldDecorator;
    private final PersistentDecorator newDecorator;

    public DecoratorChangedEvent(Object src, String spaceKey, PersistentDecorator oldDecorator, PersistentDecorator newDecorator) {
        super(src, spaceKey);
        this.oldDecorator = oldDecorator;
        this.newDecorator = newDecorator;
    }

    public PersistentDecorator getOldDecorator() {
        return this.oldDecorator;
    }

    public PersistentDecorator getNewDecorator() {
        return this.newDecorator;
    }
}

