/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.jackrabbit.webdav.lock.AbstractLockEntry;
import org.apache.jackrabbit.webdav.lock.LockEntry;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SupportedLock
extends AbstractDavProperty<List<LockEntry>> {
    private final List<LockEntry> entries = new ArrayList<LockEntry>();

    public SupportedLock() {
        super(DavPropertyName.SUPPORTEDLOCK, false);
    }

    public void addEntry(Type type, Scope scope) {
        this.entries.add(new WriteLockEntry(type, scope));
    }

    public void addEntry(LockEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("The lock entry cannot be null.");
        }
        this.entries.add(entry);
    }

    public boolean isSupportedLock(Type type, Scope scope) {
        for (LockEntry le : this.entries) {
            if (!le.getType().equals(type) || !le.getScope().equals(scope)) continue;
            return true;
        }
        return false;
    }

    public Iterator<LockEntry> getSupportedLocks() {
        return this.entries.iterator();
    }

    @Override
    public Element toXml(Document document) {
        Element support = this.getName().toXml(document);
        for (LockEntry le : this.entries) {
            support.appendChild(le.toXml(document));
        }
        return support;
    }

    @Override
    public List<LockEntry> getValue() {
        return this.entries;
    }

    private static final class WriteLockEntry
    extends AbstractLockEntry {
        private final Scope scope;

        WriteLockEntry(Type type, Scope scope) {
            if (!Type.WRITE.equals(type)) {
                throw new IllegalArgumentException("Invalid Type:" + type);
            }
            if (!Scope.EXCLUSIVE.equals(scope) && !Scope.SHARED.equals(scope)) {
                throw new IllegalArgumentException("Invalid scope:" + scope);
            }
            this.scope = scope;
        }

        @Override
        public Type getType() {
            return Type.WRITE;
        }

        @Override
        public Scope getScope() {
            return this.scope;
        }
    }
}

