/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import javax.transaction.xa.XAResource;
import org.apache.jackrabbit.spi.SessionInfo;

public interface XASessionInfo
extends SessionInfo {
    public XAResource getXAResource();
}

