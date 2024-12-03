/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.util.XMLChar;

public class NamespaceHelper {
    public static final String JCR = "http://www.jcp.org/jcr/1.0";
    public static final String NT = "http://www.jcp.org/jcr/nt/1.0";
    public static final String MIX = "http://www.jcp.org/jcr/mix/1.0";
    private final Session session;

    public NamespaceHelper(Session session) {
        this.session = session;
    }

    public Map<String, String> getNamespaces() throws RepositoryException {
        String[] prefixes;
        HashMap<String, String> namespaces = new HashMap<String, String>();
        for (String prefixe : prefixes = this.session.getNamespacePrefixes()) {
            namespaces.put(prefixe, this.session.getNamespaceURI(prefixe));
        }
        return namespaces;
    }

    public String getPrefix(String uri) throws RepositoryException {
        try {
            return this.session.getNamespacePrefix(uri);
        }
        catch (NamespaceException e) {
            return null;
        }
    }

    public String getURI(String prefix) throws RepositoryException {
        try {
            return this.session.getNamespaceURI(prefix);
        }
        catch (NamespaceException e) {
            return null;
        }
    }

    public String getJcrName(String uri, String name) throws NamespaceException, RepositoryException {
        if (uri != null && uri.length() > 0) {
            return this.session.getNamespacePrefix(uri) + ":" + name;
        }
        return name;
    }

    public String getJcrName(String name) throws IllegalArgumentException, RepositoryException {
        String currentPrefix;
        String standardPrefix;
        if (name.startsWith("jcr:")) {
            standardPrefix = "jcr";
            currentPrefix = this.session.getNamespacePrefix(JCR);
        } else if (name.startsWith("nt:")) {
            standardPrefix = "nt";
            currentPrefix = this.session.getNamespacePrefix(NT);
        } else if (name.startsWith("mix:")) {
            standardPrefix = "mix";
            currentPrefix = this.session.getNamespacePrefix(MIX);
        } else {
            throw new IllegalArgumentException("Unknown prefix: " + name);
        }
        if (currentPrefix.equals(standardPrefix)) {
            return name;
        }
        return currentPrefix + name.substring(standardPrefix.length());
    }

    public String registerNamespace(String prefix, String uri) throws RepositoryException {
        NamespaceRegistry registry = this.session.getWorkspace().getNamespaceRegistry();
        try {
            registry.getPrefix(uri);
        }
        catch (NamespaceException e1) {
            if (prefix == null || prefix.length() == 0 || prefix.toLowerCase().startsWith("xml") || !XMLChar.isValidNCName(prefix)) {
                prefix = "ns";
            }
            try {
                String base = prefix;
                int i = 2;
                while (true) {
                    registry.getURI(prefix);
                    prefix = base + i;
                    ++i;
                }
            }
            catch (NamespaceException namespaceException) {
                registry.registerNamespace(prefix, uri);
            }
        }
        return this.session.getNamespacePrefix(uri);
    }

    public void registerNamespaces(Map<String, String> namespaces) throws RepositoryException {
        Iterator<Map.Entry<String, String>> iterator = namespaces.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> stringStringEntry;
            Map.Entry<String, String> entry = stringStringEntry = iterator.next();
            this.registerNamespace(entry.getKey(), entry.getValue());
        }
    }
}

