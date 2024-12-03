/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.lock;

import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LockDiscovery
extends AbstractDavProperty<List<ActiveLock>> {
    private List<ActiveLock> activeLocks = new ArrayList<ActiveLock>();

    public LockDiscovery() {
        super(DavPropertyName.LOCKDISCOVERY, false);
    }

    public LockDiscovery(ActiveLock lock) {
        super(DavPropertyName.LOCKDISCOVERY, false);
        this.addActiveLock(lock);
    }

    public LockDiscovery(ActiveLock[] locks) {
        super(DavPropertyName.LOCKDISCOVERY, false);
        for (ActiveLock lock : locks) {
            this.addActiveLock(lock);
        }
    }

    private void addActiveLock(ActiveLock lock) {
        if (lock != null) {
            this.activeLocks.add(lock);
        }
    }

    @Override
    public List<ActiveLock> getValue() {
        return this.activeLocks;
    }

    @Override
    public Element toXml(Document document) {
        Element lockdiscovery = this.getName().toXml(document);
        for (ActiveLock lock : this.activeLocks) {
            lockdiscovery.appendChild(lock.toXml(document));
        }
        return lockdiscovery;
    }

    public static LockDiscovery createFromXml(Element lockDiscoveryElement) {
        if (!DomUtil.matches(lockDiscoveryElement, "lockdiscovery", NAMESPACE)) {
            throw new IllegalArgumentException("DAV:lockdiscovery element expected.");
        }
        ArrayList<ALockImpl> activeLocks = new ArrayList<ALockImpl>();
        ElementIterator it = DomUtil.getChildren(lockDiscoveryElement, "activelock", NAMESPACE);
        while (it.hasNext()) {
            Element al = it.nextElement();
            activeLocks.add(new ALockImpl(al));
        }
        return new LockDiscovery(activeLocks.toArray(new ActiveLock[activeLocks.size()]));
    }

    private static class ALockImpl
    implements ActiveLock {
        private final Element alElement;

        private ALockImpl(Element alElement) {
            if (!DomUtil.matches(alElement, "activelock", DavConstants.NAMESPACE)) {
                throw new IllegalArgumentException("DAV:activelock element expected.");
            }
            this.alElement = alElement;
        }

        @Override
        public boolean isLockedByToken(String lockToken) {
            String lt = this.getToken();
            if (lt == null) {
                return false;
            }
            return lt.equals(lockToken);
        }

        @Override
        public boolean isExpired() {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getToken() {
            Element ltEl = DomUtil.getChildElement(this.alElement, "locktoken", DavConstants.NAMESPACE);
            if (ltEl != null) {
                return DomUtil.getChildText(ltEl, "href", DavConstants.NAMESPACE);
            }
            return null;
        }

        @Override
        public String getOwner() {
            String owner = null;
            Element ow = DomUtil.getChildElement(this.alElement, "owner", DavConstants.NAMESPACE);
            if (ow != null) {
                owner = DomUtil.hasChildElement(ow, "href", DavConstants.NAMESPACE) ? DomUtil.getChildTextTrim(ow, "href", DavConstants.NAMESPACE) : DomUtil.getTextTrim(ow);
            }
            return owner;
        }

        @Override
        public void setOwner(String owner) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public long getTimeout() {
            String t = DomUtil.getChildTextTrim(this.alElement, "timeout", DavConstants.NAMESPACE);
            return TimeoutHeader.parse(t, Integer.MIN_VALUE);
        }

        @Override
        public void setTimeout(long timeout) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public boolean isDeep() {
            String depth = DomUtil.getChildTextTrim(this.alElement, "depth", DavConstants.NAMESPACE);
            return "infinity".equalsIgnoreCase(depth);
        }

        @Override
        public void setIsDeep(boolean isDeep) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public String getLockroot() {
            Element root = DomUtil.getChildElement(this.alElement, "lockroot", DavConstants.NAMESPACE);
            if (root != null) {
                return DomUtil.getChildTextTrim(root, "href", DavConstants.NAMESPACE);
            }
            return null;
        }

        @Override
        public void setLockroot(String lockroot) {
            throw new UnsupportedOperationException("Not implemented");
        }

        @Override
        public Type getType() {
            return Type.createFromXml(DomUtil.getChildElement(this.alElement, "locktype", DavConstants.NAMESPACE));
        }

        @Override
        public Scope getScope() {
            return Scope.createFromXml(DomUtil.getChildElement(this.alElement, "lockscope", DavConstants.NAMESPACE));
        }

        @Override
        public Element toXml(Document document) {
            return (Element)document.importNode(this.alElement, true);
        }
    }
}

