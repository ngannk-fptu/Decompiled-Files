/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.spi.link.ReciprocalActionException;
import java.util.List;

public class RemoteErrorListException
extends ReciprocalActionException {
    private final List<String> errors;

    public RemoteErrorListException(List<String> errors) {
        super("The remote application reported one or more errors.");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return this.errors;
    }
}

