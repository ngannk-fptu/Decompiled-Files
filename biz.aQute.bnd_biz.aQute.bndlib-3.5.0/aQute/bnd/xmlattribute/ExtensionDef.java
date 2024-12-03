/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.xmlattribute;

import aQute.bnd.annotation.xml.XMLAttribute;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.xmlattribute.Namespaces;
import aQute.bnd.xmlattribute.XMLAttributeFinder;
import aQute.lib.tag.Tag;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtensionDef {
    protected final XMLAttributeFinder finder;
    protected final Map<XMLAttribute, Annotation> attributes = new LinkedHashMap<XMLAttribute, Annotation>();

    public ExtensionDef(XMLAttributeFinder finder) {
        this.finder = finder;
    }

    public void addExtensionAttribute(XMLAttribute xmlAttr, Annotation a) {
        this.attributes.put(xmlAttr, a);
    }

    public void addNamespaces(Namespaces namespaces, String docNS) {
        Iterator<XMLAttribute> i = this.attributes.keySet().iterator();
        while (i.hasNext()) {
            XMLAttribute xmlAttr = i.next();
            if (this.matches(xmlAttr, docNS)) {
                namespaces.registerNamespace(xmlAttr.prefix(), xmlAttr.namespace());
                continue;
            }
            i.remove();
        }
    }

    private boolean matches(XMLAttribute xmlAttr, String docNS) {
        String[] embedIn = xmlAttr.embedIn();
        if (embedIn == null) {
            return true;
        }
        for (String match : embedIn) {
            if (!this.matches(match, docNS)) continue;
            return true;
        }
        return false;
    }

    private boolean matches(String match, String docNS) {
        if (match.equals(docNS)) {
            return true;
        }
        if (match.endsWith("*")) {
            match = match.substring(0, match.length() - 1);
            return docNS.startsWith(match);
        }
        return false;
    }

    public void addAttributes(Tag tag, Namespaces namespaces) {
        if (namespaces != null) {
            for (Map.Entry<XMLAttribute, Annotation> entry : this.attributes.entrySet()) {
                String prefix = namespaces.getPrefix(entry.getKey().namespace());
                Annotation a = entry.getValue();
                Map<String, String> props = this.finder.getDefaults(a);
                for (String key : entry.getValue().keySet()) {
                    String value;
                    Object obj = entry.getValue().get(key);
                    if (obj.getClass().isArray()) {
                        StringBuilder sb = new StringBuilder();
                        String sep = "";
                        for (int i = 0; i < Array.getLength(obj); ++i) {
                            Object el = Array.get(obj, i);
                            sb.append(sep).append(String.valueOf(el));
                            sep = " ";
                        }
                        value = sb.toString();
                    } else {
                        value = String.valueOf(obj);
                    }
                    props.put(key, value);
                }
                String[] mapping = entry.getKey().mapping();
                for (Map.Entry<String, String> prop : props.entrySet()) {
                    String key = prop.getKey();
                    if (mapping != null && mapping.length > 0) {
                        String match = key + "=";
                        for (String map : mapping) {
                            if (!map.startsWith(match)) continue;
                            key = map.substring(match.length());
                        }
                    }
                    tag.addAttribute(prefix + ":" + key, prop.getValue());
                }
            }
        }
    }
}

