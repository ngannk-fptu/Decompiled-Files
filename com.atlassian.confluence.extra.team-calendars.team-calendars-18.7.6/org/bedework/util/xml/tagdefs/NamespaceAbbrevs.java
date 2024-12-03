/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml.tagdefs;

import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;

public class NamespaceAbbrevs {
    private static final Map<String, String> namespacesToAbbrevs = new HashMap<String, String>();

    public static String namespaceToAbbrev(String ns) {
        if (ns == null) {
            return null;
        }
        return namespacesToAbbrevs.get(ns);
    }

    public static String qnameToAbbrev(QName qn) {
        return NamespaceAbbrevs.namespaceToAbbrev(qn.getNamespaceURI());
    }

    public static String prefixed(QName qn) {
        return NamespaceAbbrevs.prefixed(qn.getNamespaceURI(), qn.getLocalPart());
    }

    public static String prefixed(String namespace, String localPart) {
        String nsAbbrev = NamespaceAbbrevs.namespaceToAbbrev(namespace);
        if (nsAbbrev != null) {
            return nsAbbrev + ":" + localPart;
        }
        return localPart;
    }

    static {
        namespacesToAbbrevs.put("http://apple.com/ns/ical/", "AICAL");
        namespacesToAbbrevs.put("http://calendarserver.org/ns/", "CS");
        namespacesToAbbrevs.put("http://bedeworkcalserver.org/ns/", "BWS");
        namespacesToAbbrevs.put("urn:ietf:params:xml:ns:caldav", "C");
        namespacesToAbbrevs.put("http://www.w3.org/2002/12/cal/ical#", "IC");
        namespacesToAbbrevs.put("urn:ietf:params:xml:ns:carddav", "CARD");
        namespacesToAbbrevs.put("urn:ietf:params:xml:ns:ischedule", "ISCH");
        namespacesToAbbrevs.put("DAV:", "DAV");
    }
}

