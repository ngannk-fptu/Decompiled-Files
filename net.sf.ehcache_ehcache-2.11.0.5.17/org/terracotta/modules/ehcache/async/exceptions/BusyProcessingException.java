/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async.exceptions;

import org.terracotta.modules.ehcache.async.exceptions.AsyncException;

public class BusyProcessingException
extends AsyncException {
    public BusyProcessingException() {
        super("Already busy processing.");
    }
}

