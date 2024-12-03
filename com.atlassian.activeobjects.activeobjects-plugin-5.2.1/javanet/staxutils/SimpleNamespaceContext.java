/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javanet.staxutils.ExtendedNamespaceContext;
import javanet.staxutils.StaticNamespaceContext;
import javax.xml.namespace.NamespaceContext;

public class SimpleNamespaceContext
implements ExtendedNamespaceContext,
StaticNamespaceContext {
    protected NamespaceContext parent;
    protected Map namespaces = new LinkedHashMap();

    public SimpleNamespaceContext() {
    }

    public SimpleNamespaceContext(Map namespaces) {
        if (namespaces != null) {
            this.namespaces.putAll(namespaces);
        }
    }

    public SimpleNamespaceContext(NamespaceContext parent) {
        this.parent = parent;
    }

    public SimpleNamespaceContext(NamespaceContext parent, Map namespaces) {
        this.parent = parent;
        if (namespaces != null) {
            this.namespaces.putAll(namespaces);
        }
    }

    public NamespaceContext getParent() {
        return this.parent;
    }

    public void setParent(NamespaceContext parent) {
        this.parent = parent;
    }

    public boolean isRootContext() {
        return this.parent == null;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("prefix argument was null");
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        if (this.namespaces.containsKey(prefix)) {
            String uri = (String)this.namespaces.get(prefix);
            if (uri.length() == 0) {
                return null;
            }
            return uri;
        }
        if (this.parent != null) {
            return this.parent.getNamespaceURI(prefix);
        }
        return null;
    }

    public String getPrefix(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("nsURI was null");
        }
        if (nsURI.length() == 0) {
            throw new IllegalArgumentException("nsURI was empty");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        Iterator iter = this.namespaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String uri = (String)entry.getValue();
            if (!uri.equals(nsURI)) continue;
            return (String)entry.getKey();
        }
        if (this.parent != null) {
            return this.parent.getPrefix(nsURI);
        }
        if (nsURI.length() == 0) {
            return "";
        }
        return null;
    }

    public boolean isPrefixDeclared(String prefix) {
        return this.namespaces.containsKey(prefix);
    }

    public Iterator getDeclaredPrefixes() {
        return Collections.unmodifiableCollection(this.namespaces.keySet()).iterator();
    }

    public int getDeclaredPrefixCount() {
        return this.namespaces.size();
    }

    public Iterator getPrefixes() {
        if (this.parent == null || !(this.parent instanceof ExtendedNamespaceContext)) {
            return this.getDeclaredPrefixes();
        }
        HashSet prefixes = new HashSet(this.namespaces.keySet());
        ExtendedNamespaceContext superCtx = (ExtendedNamespaceContext)this.parent;
        Iterator i = superCtx.getPrefixes();
        while (i.hasNext()) {
            String prefix = (String)i.next();
            prefixes.add(prefix);
        }
        return prefixes.iterator();
    }

    public Iterator getPrefixes(String nsURI) {
        if (nsURI == null) {
            throw new IllegalArgumentException("nsURI was null");
        }
        if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
            return Collections.singleton("xml").iterator();
        }
        if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
            return Collections.singleton("xmlns").iterator();
        }
        HashSet prefixes = null;
        Iterator iter = this.namespaces.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String uri = (String)entry.getValue();
            if (!uri.equals(nsURI)) continue;
            if (prefixes == null) {
                prefixes = new HashSet();
            }
            prefixes.add(entry.getKey());
        }
        if (this.parent != null) {
            Iterator<String> i = this.parent.getPrefixes(nsURI);
            while (i.hasNext()) {
                String prefix = i.next();
                if (prefixes == null) {
                    prefixes = new HashSet();
                }
                prefixes.add(prefix);
            }
        }
        if (prefixes != null) {
            return Collections.unmodifiableSet(prefixes).iterator();
        }
        if (nsURI.length() == 0) {
            return Collections.singleton("").iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public String setDefaultNamespace(String nsURI) {
        if (nsURI != null) {
            if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
                throw new IllegalArgumentException("Attempt to map 'xml' uri");
            }
            if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
                throw new IllegalArgumentException("Attempt to map 'xmlns' uri");
            }
            return this.namespaces.put("", nsURI);
        }
        return this.namespaces.put("", "");
    }

    public String setPrefix(String prefix, String nsURI) {
        if (prefix == null) {
            throw new NullPointerException("Namespace Prefix was null");
        }
        if (prefix.equals("")) {
            return this.setDefaultNamespace(nsURI);
        }
        if (prefix.equals("xml")) {
            throw new IllegalArgumentException("Attempt to map 'xml' prefix");
        }
        if (prefix.equals("xmlns")) {
            throw new IllegalArgumentException("Attempt to map 'xmlns' prefix");
        }
        if (nsURI != null) {
            if (nsURI.equals("http://www.w3.org/XML/1998/namespace")) {
                throw new IllegalArgumentException("Attempt to map 'xml' uri");
            }
            if (nsURI.equals("http://www.w3.org/2000/xmlns/")) {
                throw new IllegalArgumentException("Attempt to map 'xmlns' uri");
            }
            return this.namespaces.put(prefix, nsURI);
        }
        return this.namespaces.put(prefix, "");
    }
}

