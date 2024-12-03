/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import java.util.Collection;

public interface AddLabelsCommand
extends ServiceCommand {
    public Labelable getEntity();

    public Collection<Label> getAddedLabels();
}

