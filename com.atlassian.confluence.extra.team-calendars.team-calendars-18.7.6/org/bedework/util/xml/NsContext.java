/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

public class NsContext
implements NamespaceContext {
    private static Map<String, String> keyPrefix = new HashMap<String, String>();
    private static Map<String, String> keyUri = new HashMap<String, String>();
    private String defaultNS;

    public NsContext(String defaultNS) {
        this.defaultNS = defaultNS;
    }

    public String getDefaultNS() {
        return this.defaultNS;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix != null && prefix.equals("")) {
            return this.defaultNS;
        }
        String uri = keyPrefix.get(prefix);
        if (uri == null) {
            return "";
        }
        return uri;
    }

    @Override
    public Iterator<String> getPrefixes(String val) {
        String prefix = keyUri.get(val);
        TreeSet<String> pfxs = new TreeSet<String>();
        if (prefix != null) {
            pfxs.add(prefix);
        }
        return pfxs.iterator();
    }

    public Set<String> getPrefixes() {
        return keyPrefix.keySet();
    }

    public void clear() {
        keyPrefix.clear();
        keyUri.clear();
        this.defaultNS = null;
    }

    public void add(String prefix, String uri) {
        if (prefix == null) {
            this.defaultNS = uri;
        }
        NsContext.addToMap(prefix, uri);
    }

    @Override
    public String getPrefix(String uri) {
        if (this.defaultNS != null && uri.equals(this.defaultNS)) {
            return "";
        }
        return keyUri.get(uri);
    }

    public void appendNsName(StringBuilder sb, QName nm) {
        String abbr;
        String uri = nm.getNamespaceURI();
        if (this.defaultNS != null && uri.equals(this.defaultNS)) {
            abbr = null;
        } else {
            abbr = keyUri.get(uri);
            if (abbr == null) {
                abbr = uri;
            }
        }
        if (abbr != null) {
            sb.append(abbr);
            sb.append(":");
        }
        sb.append(nm.getLocalPart());
    }

    private static void addToMap(String prefix, String uri) {
        if (keyPrefix.get(prefix) != null) {
            throw new RuntimeException("Attempt to replace namespace prefix");
        }
        if (keyUri.get(uri) != null) {
            throw new RuntimeException("Attempt to replace namespace");
        }
        keyPrefix.put(prefix, uri);
        keyUri.put(uri, prefix);
    }

    static {
        NsContext.addToMap("D", "DAV");
        NsContext.addToMap("AS", "http://calendarserver.org/ns/");
        NsContext.addToMap("BW", "http://bedework.org/ns/");
        NsContext.addToMap("BWC", "http://bedeworkcalserver.org/ns/");
        NsContext.addToMap("BWCD", "http://bedeworkcardserver.org/ns/");
        NsContext.addToMap("C", "urn:ietf:params:xml:ns:caldav");
        NsContext.addToMap("CD", "urn:ietf:params:xml:ns:carddav");
        NsContext.addToMap("IS", "urn:ietf:params:xml:ns:ischedule");
        NsContext.addToMap("X", "urn:ietf:params:xml:ns:icalendar-2.0");
        NsContext.addToMap("df", "urn:ietf:params:xml:ns:pidf-diff");
        NsContext.addToMap("xml", "http://www.w3.org/XML/1998/namespace");
        NsContext.addToMap("xmlns", "http://www.w3.org/2000/xmlns/");
    }
}

