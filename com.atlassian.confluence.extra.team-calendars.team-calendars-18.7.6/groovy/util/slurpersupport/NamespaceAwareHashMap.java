/*
 * Decompiled with CFR 0.152.
 */
package groovy.util.slurpersupport;

import groovy.xml.QName;
import java.util.HashMap;
import java.util.Map;

public class NamespaceAwareHashMap
extends HashMap<String, String> {
    private Map namespaceTagHints = null;

    public void setNamespaceTagHints(Map namespaceTagHints) {
        this.namespaceTagHints = namespaceTagHints;
    }

    public Map getNamespaceTagHints() {
        return this.namespaceTagHints;
    }

    @Override
    public String get(Object key) {
        key = this.adjustForNamespaceIfNeeded(key);
        return (String)super.get(key);
    }

    @Override
    public String remove(Object key) {
        key = this.adjustForNamespaceIfNeeded(key).toString();
        return (String)super.remove(key);
    }

    @Override
    public boolean containsKey(Object key) {
        key = this.adjustForNamespaceIfNeeded(key).toString();
        return super.containsKey(key);
    }

    @Override
    public String put(String key, String value) {
        key = this.adjustForNamespaceIfNeeded(key).toString();
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        for (Map.Entry<? extends String, ? extends String> o : m.entrySet()) {
            if (!(o instanceof Map.Entry)) continue;
            Map.Entry<? extends String, ? extends String> e = o;
            this.put(e.getKey(), e.getValue());
        }
    }

    private Object adjustForNamespaceIfNeeded(Object key) {
        String keyString = key.toString();
        if (keyString.contains("{") || this.namespaceTagHints == null || this.namespaceTagHints.isEmpty() || !keyString.contains(":")) {
            return key;
        }
        int i = keyString.indexOf(":");
        return new QName(this.namespaceTagHints.get(keyString.substring(0, i)).toString(), keyString.substring(i + 1)).toString();
    }
}

