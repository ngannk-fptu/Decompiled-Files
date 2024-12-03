/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.JiraAuthenticationRequiredException;
import com.atlassian.integration.jira.JiraException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public class JiraMultipleAuthenticationException
extends JiraException
implements Iterable<JiraAuthenticationRequiredException> {
    private final List<JiraAuthenticationRequiredException> exceptions;

    public JiraMultipleAuthenticationException(@Nonnull String message, @Nonnull List<JiraAuthenticationRequiredException> exceptions) {
        super(message);
        this.exceptions = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(exceptions, (Object)"exceptions")));
    }

    @Nonnull
    public List<JiraAuthenticationRequiredException> getExceptions() {
        return this.exceptions;
    }

    @Override
    @Nonnull
    public Iterator<JiraAuthenticationRequiredException> iterator() {
        return this.exceptions.iterator();
    }
}

