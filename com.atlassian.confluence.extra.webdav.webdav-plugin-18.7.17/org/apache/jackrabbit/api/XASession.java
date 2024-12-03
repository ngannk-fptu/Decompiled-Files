/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api;

import javax.jcr.Session;
import javax.transaction.xa.XAResource;

public interface XASession
extends Session {
    public XAResource getXAResource();
}

