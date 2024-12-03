/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.plugin.util.validation;

import com.atlassian.plugin.PluginParseException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;

public class ValidationException
extends PluginParseException {
    private final List<String> errors;

    public ValidationException(String msg, List<String> errors) {
        super(msg);
        this.errors = ImmutableList.copyOf((Collection)((Collection)Preconditions.checkNotNull(errors)));
    }

    public List<String> getErrors() {
        return this.errors;
    }
}

