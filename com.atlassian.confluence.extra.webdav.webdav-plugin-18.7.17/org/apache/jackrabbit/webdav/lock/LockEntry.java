/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

public interface LockEntry
extends XmlSerializable {
    public Type getType();

    public Scope getScope();
}

