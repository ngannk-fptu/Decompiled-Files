/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.integration.jira;

import com.atlassian.integration.jira.JiraCommunicationException;
import com.atlassian.integration.jira.JiraException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public class JiraMultipleCommunicationException
extends JiraException
implements Iterable<JiraCommunicationException> {
    private final List<JiraCommunicationException> exceptions;

    public JiraMultipleCommunicationException(@Nonnull String message, @Nonnull List<JiraCommunicationException> exceptions) {
        super(message);
        this.exceptions = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(exceptions, (Object)"exceptions")));
    }

    @Nonnull
    public List<JiraCommunicationException> getExceptions() {
        return this.exceptions;
    }

    @Override
    @Nonnull
    public Iterator<JiraCommunicationException> iterator() {
        return this.exceptions.iterator();
    }
}

