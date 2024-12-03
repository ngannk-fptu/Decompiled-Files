/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.JiraErrors;
import com.atlassian.integration.jira.JiraException;
import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;

public class JiraValidationException
extends JiraException {
    private final JiraErrors errors;

    public JiraValidationException(@Nonnull JiraErrors errors) {
        super("Remote validation errors occurred");
        this.errors = (JiraErrors)Preconditions.checkNotNull((Object)errors, (Object)"errors");
    }

    @Nonnull
    public JiraErrors getErrors() {
        return this.errors;
    }
}

