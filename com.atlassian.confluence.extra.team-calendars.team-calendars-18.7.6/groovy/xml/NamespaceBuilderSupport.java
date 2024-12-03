/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.util.BuilderSupport;
import groovy.util.NodeBuilder;
import groovy.xml.QName;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.InvokerHelper;

public class NamespaceBuilderSupport
extends BuilderSupport {
    private boolean autoPrefix;
    private Map<String, String> nsMap = new HashMap<String, String>();
    private BuilderSupport builder;

    public NamespaceBuilderSupport(BuilderSupport builder) {
        super(builder);
        this.builder = builder;
    }

    public NamespaceBuilderSupport(BuilderSupport builder, String uri) {
        this(builder, uri, "");
    }

    public NamespaceBuilderSupport(BuilderSupport builder, String uri, String prefix) {
        this(builder, uri, prefix, true);
    }

    public NamespaceBuilderSupport(BuilderSupport builder, String uri, String prefix, boolean autoPrefix) {
        this(builder);
        this.nsMap.put(prefix, uri);
        this.autoPrefix = autoPrefix;
    }

    public NamespaceBuilderSupport(BuilderSupport builder, Map nsMap) {
        this(builder);
        this.nsMap = nsMap;
    }

    public NamespaceBuilderSupport namespace(String namespaceURI) {
        this.nsMap.put("", namespaceURI);
        return this;
    }

    public NamespaceBuilderSupport namespace(String namespaceURI, String prefix) {
        this.nsMap.put(prefix, namespaceURI);
        return this;
    }

    public NamespaceBuilderSupport declareNamespace(Map nsMap) {
        this.nsMap = nsMap;
        return this;
    }

    @Override
    protected Object getCurrent() {
        if (this.builder instanceof NodeBuilder) {
            return InvokerHelper.invokeMethod(this.builder, "getCurrent", null);
        }
        return super.getCurrent();
    }

    @Override
    protected void setCurrent(Object current) {
        if (this.builder instanceof NodeBuilder) {
            InvokerHelper.invokeMethod(this.builder, "setCurrent", current);
        } else {
            super.setCurrent(current);
        }
    }

    @Override
    protected void setParent(Object parent, Object child) {
    }

    @Override
    protected Object getName(String methodName) {
        String namespaceURI;
        String prefix = this.autoPrefix ? this.nsMap.keySet().iterator().next() : "";
        String localPart = methodName;
        int idx = methodName.indexOf(58);
        if (idx > 0) {
            prefix = methodName.substring(0, idx);
            localPart = methodName.substring(idx + 1);
        }
        if ((namespaceURI = this.nsMap.get(prefix)) == null) {
            return methodName;
        }
        return new QName(namespaceURI, localPart, prefix);
    }

    @Override
    public Object invokeMethod(String methodName, Object args) {
        Map attributes = NamespaceBuilderSupport.findAttributes(args);
        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String key = String.valueOf(entry.getKey());
            if (!key.startsWith("xmlns:")) continue;
            String prefix = key.substring(6);
            String uri = String.valueOf(entry.getValue());
            this.namespace(uri, prefix);
            iter.remove();
        }
        return super.invokeMethod(methodName, args);
    }

    private static Map findAttributes(Object args) {
        List list = InvokerHelper.asList(args);
        for (Object o : list) {
            if (!(o instanceof Map)) continue;
            return (Map)o;
        }
        return Collections.EMPTY_MAP;
    }

    @Override
    protected Object createNode(Object name) {
        return name;
    }

    @Override
    protected Object createNode(Object name, Object value) {
        return name;
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        return name;
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        return name;
    }
}

