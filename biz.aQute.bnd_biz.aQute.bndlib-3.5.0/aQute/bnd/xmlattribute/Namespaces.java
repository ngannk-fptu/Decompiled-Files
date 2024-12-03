/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.xmlattribute;

import aQute.lib.tag.Tag;
import java.util.LinkedHashMap;
import java.util.Map;

public class Namespaces {
    final Map<String, String> namespaces = new LinkedHashMap<String, String>();

    public void registerNamespace(String prefix, String namespace) {
        if (this.namespaces.containsKey(namespace)) {
            return;
        }
        String prefix2 = prefix = prefix == null ? "ns" : prefix;
        int i = 1;
        while (this.namespaces.containsValue(prefix2)) {
            if (prefix2.equals(this.namespaces.get(namespace))) {
                return;
            }
            prefix2 = prefix + i++;
        }
        this.namespaces.put(namespace, prefix2);
    }

    public String getPrefix(String namespace) {
        return this.namespaces.get(namespace);
    }

    public void addNamespaces(Tag tag) {
        for (Map.Entry<String, String> entry : this.namespaces.entrySet()) {
            tag.addAttribute("xmlns:" + entry.getValue(), entry.getKey());
        }
    }
}

