/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleProtectionDomain;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;

class WovenClassImpl
implements WovenClass,
List<String> {
    private final String m_className;
    private final BundleWiring m_wiring;
    private byte[] m_bytes;
    private List<String> m_imports = new ArrayList<String>();
    private Class m_definedClass = null;
    private boolean m_isComplete = false;
    private int m_state;

    WovenClassImpl(String className, BundleWiring wiring, byte[] bytes) {
        this.m_className = className;
        this.m_wiring = wiring;
        this.m_bytes = bytes;
        this.m_state = 1;
    }

    synchronized void complete() {
        this.completeDefine(null);
        this.completeImports(null);
    }

    synchronized void completeImports(List<String> imports) {
        this.m_imports = imports == null ? Util.newImmutableList(this.m_imports) : Util.newImmutableList(imports);
    }

    synchronized void completeDefine(Class<?> definedClass) {
        this.m_definedClass = definedClass;
    }

    @Override
    public synchronized byte[] getBytes() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        byte[] bytes = this.m_bytes;
        if (this.m_isComplete) {
            bytes = new byte[this.m_bytes.length];
            System.arraycopy(this.m_bytes, 0, bytes, 0, this.m_bytes.length);
        }
        return bytes;
    }

    @Override
    public synchronized void setBytes(byte[] bytes) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        if (this.m_state >= 2) {
            throw new IllegalStateException("Cannot change bytes after class weaving is completed.");
        }
        this.m_bytes = bytes;
    }

    synchronized List<String> getDynamicImportsInternal() {
        return this.m_imports;
    }

    @Override
    public synchronized List<String> getDynamicImports() {
        return this;
    }

    @Override
    public synchronized boolean isWeavingComplete() {
        return this.m_isComplete;
    }

    @Override
    public String getClassName() {
        return this.m_className;
    }

    @Override
    public ProtectionDomain getProtectionDomain() {
        return ((BundleImpl)this.m_wiring.getRevision().getBundle()).getProtectionDomain();
    }

    @Override
    public synchronized Class<?> getDefinedClass() {
        return this.m_definedClass;
    }

    @Override
    public BundleWiring getBundleWiring() {
        return this.m_wiring;
    }

    @Override
    public synchronized int size() {
        return this.m_imports.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return this.m_imports.isEmpty();
    }

    @Override
    public synchronized boolean contains(Object o) {
        return this.m_imports.contains(o);
    }

    @Override
    public synchronized Iterator<String> iterator() {
        return this.m_imports.iterator();
    }

    @Override
    public synchronized Object[] toArray() {
        return this.m_imports.toArray();
    }

    @Override
    public synchronized <T> T[] toArray(T[] ts) {
        return this.m_imports.toArray(ts);
    }

    @Override
    public synchronized boolean add(String s) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        if (s != null) {
            try {
                ManifestParser.parseDynamicImportHeader(null, null, s);
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Unable to parse dynamic import.", ex);
            }
            this.checkImport(s);
            return this.m_imports.add(s);
        }
        return false;
    }

    private void checkImport(String s) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new PackagePermission(s, "import"));
        }
    }

    @Override
    public synchronized boolean remove(Object o) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        return this.m_imports.remove(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> collection) {
        return this.m_imports.containsAll(collection);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends String> collection) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        for (String string : collection) {
            try {
                ManifestParser.parseDynamicImportHeader(null, null, string);
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Unable to parse dynamic import.", ex);
            }
            this.checkImport(string);
        }
        return this.m_imports.addAll(collection);
    }

    @Override
    public synchronized boolean addAll(int i, Collection<? extends String> collection) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        for (String string : collection) {
            try {
                ManifestParser.parseDynamicImportHeader(null, null, string);
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Unable to parse dynamic import.", ex);
            }
            this.checkImport(string);
        }
        return this.m_imports.addAll(i, collection);
    }

    @Override
    public synchronized boolean removeAll(Collection<?> collection) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        return this.m_imports.removeAll(collection);
    }

    @Override
    public synchronized boolean retainAll(Collection<?> collection) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        return this.m_imports.retainAll(collection);
    }

    @Override
    public synchronized void clear() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        this.m_imports.clear();
    }

    @Override
    public synchronized String get(int i) {
        return this.m_imports.get(i);
    }

    @Override
    public synchronized String set(int i, String s) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        try {
            ManifestParser.parseDynamicImportHeader(null, null, s);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse dynamic import.", ex);
        }
        this.checkImport(s);
        return this.m_imports.set(i, s);
    }

    @Override
    public synchronized void add(int i, String s) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        try {
            ManifestParser.parseDynamicImportHeader(null, null, s);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unable to parse dynamic import.", ex);
        }
        this.checkImport(s);
        this.m_imports.add(i, s);
    }

    @Override
    public synchronized String remove(int i) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(this.m_wiring.getBundle(), "weave"));
        }
        return this.m_imports.remove(i);
    }

    @Override
    public synchronized int indexOf(Object o) {
        return this.m_imports.indexOf(o);
    }

    @Override
    public synchronized int lastIndexOf(Object o) {
        return this.m_imports.lastIndexOf(o);
    }

    @Override
    public synchronized ListIterator<String> listIterator() {
        return this.m_imports.listIterator();
    }

    @Override
    public synchronized ListIterator<String> listIterator(int i) {
        return this.m_imports.listIterator(i);
    }

    @Override
    public synchronized List<String> subList(int i, int i1) {
        return this.m_imports.subList(i, i1);
    }

    byte[] _getBytes() {
        byte[] bytes = this.m_bytes;
        if (this.m_isComplete) {
            bytes = new byte[this.m_bytes.length];
            System.arraycopy(this.m_bytes, 0, bytes, 0, this.m_bytes.length);
        }
        return bytes;
    }

    @Override
    public synchronized int getState() {
        return this.m_state;
    }

    public synchronized void setState(int state) {
        if (!(this.m_isComplete || state != 4 && state != 16 && state != 8)) {
            this.m_isComplete = true;
            if (state == 4 || state == 16) {
                BundleProtectionDomain pd = (BundleProtectionDomain)((BundleRevisionImpl)this.m_wiring.getRevision()).getProtectionDomain();
                for (String s : this.m_imports) {
                    pd.addWoven(s);
                }
            }
        }
        if (state == 2) {
            this.completeImports(null);
        }
        this.m_state = state;
    }
}

