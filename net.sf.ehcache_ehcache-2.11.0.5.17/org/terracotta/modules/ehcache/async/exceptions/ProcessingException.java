/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.async.exceptions;

import org.terracotta.modules.ehcache.async.exceptions.AsyncException;

public class ProcessingException
extends AsyncException {
    public ProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

