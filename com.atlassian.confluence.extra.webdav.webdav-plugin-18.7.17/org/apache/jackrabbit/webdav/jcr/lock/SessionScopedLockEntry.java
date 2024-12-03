/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.lock;

import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.lock.AbstractLockEntry;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionScopedLockEntry
extends AbstractLockEntry {
    private static Logger log = LoggerFactory.getLogger(SessionScopedLockEntry.class);

    @Override
    public Type getType() {
        return Type.WRITE;
    }

    @Override
    public Scope getScope() {
        return ItemResourceConstants.EXCLUSIVE_SESSION;
    }
}

