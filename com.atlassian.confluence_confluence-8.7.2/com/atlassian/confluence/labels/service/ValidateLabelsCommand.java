/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.labels.Label;
import java.util.Collection;

public interface ValidateLabelsCommand
extends ServiceCommand {
    public Collection<Label> getValidLabels();
}

