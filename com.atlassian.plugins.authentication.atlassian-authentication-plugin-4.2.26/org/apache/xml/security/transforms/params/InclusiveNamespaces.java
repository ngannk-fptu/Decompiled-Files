/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.transforms.params;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.transforms.TransformParam;
import org.apache.xml.security.utils.ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class InclusiveNamespaces
extends ElementProxy
implements TransformParam {
    public static final String _TAG_EC_INCLUSIVENAMESPACES = "InclusiveNamespaces";
    public static final String _ATT_EC_PREFIXLIST = "PrefixList";
    public static final String ExclusiveCanonicalizationNamespace = "http://www.w3.org/2001/10/xml-exc-c14n#";

    public InclusiveNamespaces(Document doc, String prefixList) {
        this(doc, InclusiveNamespaces.prefixStr2Set(prefixList));
    }

    public InclusiveNamespaces(Document doc, Set<String> prefixes) {
        super(doc);
        TreeSet<String> prefixList = null;
        prefixList = prefixes instanceof SortedSet ? (TreeSet<String>)prefixes : new TreeSet<String>(prefixes);
        StringBuilder sb = new StringBuilder();
        for (String prefix : prefixList) {
            if ("xmlns".equals(prefix)) {
                sb.append("#default ");
                continue;
            }
            sb.append(prefix);
            sb.append(' ');
        }
        this.setLocalAttribute(_ATT_EC_PREFIXLIST, sb.toString().trim());
    }

    public InclusiveNamespaces(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public String getInclusiveNamespaces() {
        return this.getLocalAttribute(_ATT_EC_PREFIXLIST);
    }

    public static SortedSet<String> prefixStr2Set(String inclusiveNamespaces) {
        String[] tokens;
        TreeSet<String> prefixes = new TreeSet<String>();
        if (inclusiveNamespaces == null || inclusiveNamespaces.length() == 0) {
            return prefixes;
        }
        for (String prefix : tokens = inclusiveNamespaces.split("\\s")) {
            if ("#default".equals(prefix)) {
                prefixes.add("xmlns");
                continue;
            }
            prefixes.add(prefix);
        }
        return prefixes;
    }

    @Override
    public String getBaseNamespace() {
        return ExclusiveCanonicalizationNamespace;
    }

    @Override
    public String getBaseLocalName() {
        return _TAG_EC_INCLUSIVENAMESPACES;
    }
}

