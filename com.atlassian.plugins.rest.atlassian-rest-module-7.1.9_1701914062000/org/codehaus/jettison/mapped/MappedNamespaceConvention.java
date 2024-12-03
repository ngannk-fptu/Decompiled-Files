/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import org.codehaus.jettison.Convention;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.TypeConverter;

public class MappedNamespaceConvention
implements Convention,
NamespaceContext {
    private static final String DOT_NAMESPACE_SEP = ".";
    private Map<Object, Object> xnsToJns = new HashMap<Object, Object>();
    private Map<String, Object> jnsToXns = new HashMap<String, Object>();
    private List<?> attributesAsElements;
    private List<?> ignoredElements;
    private List<String> jsonAttributesAsElements;
    private boolean supressAtAttributes;
    private boolean ignoreNamespaces;
    private String attributeKey = "@";
    private TypeConverter typeConverter;
    private Set<?> primitiveArrayKeys;
    private boolean dropRootElement;
    private boolean writeNullAsString = true;
    private boolean rootElementArrayWrapper = true;
    private boolean ignoreEmptyArrayValues;
    private boolean readNullAsString;
    private boolean escapeForwardSlashAlways;
    private String jsonNamespaceSeparator;

    public MappedNamespaceConvention() {
        this.typeConverter = Configuration.newDefaultConverterInstance();
    }

    public MappedNamespaceConvention(Configuration config) {
        this.xnsToJns = config.getXmlToJsonNamespaces();
        this.attributesAsElements = config.getAttributesAsElements();
        this.supressAtAttributes = config.isSupressAtAttributes();
        this.ignoreNamespaces = config.isIgnoreNamespaces();
        this.dropRootElement = config.isDropRootElement();
        this.rootElementArrayWrapper = config.isRootElementArrayWrapper();
        this.attributeKey = config.getAttributeKey();
        this.primitiveArrayKeys = config.getPrimitiveArrayKeys();
        this.ignoredElements = config.getIgnoredElements();
        this.ignoreEmptyArrayValues = config.isIgnoreEmptyArrayValues();
        this.escapeForwardSlashAlways = config.isEscapeForwardSlashAlways();
        this.jsonNamespaceSeparator = config.getJsonNamespaceSeparator();
        for (Map.Entry<Object, Object> entry : this.xnsToJns.entrySet()) {
            this.jnsToXns.put((String)entry.getValue(), entry.getKey());
        }
        this.jsonAttributesAsElements = new ArrayList<String>();
        if (this.attributesAsElements != null) {
            for (QName q : this.attributesAsElements) {
                this.jsonAttributesAsElements.add(this.createAttributeKey(q.getPrefix(), q.getNamespaceURI(), q.getLocalPart()));
            }
        }
        this.readNullAsString = config.isReadNullAsString();
        this.writeNullAsString = config.isWriteNullAsString();
        this.typeConverter = config.getTypeConverter();
        if (!this.writeNullAsString && this.typeConverter != null) {
            this.typeConverter = new NullStringConverter(this.typeConverter);
        }
    }

    @Override
    public void processAttributesAndNamespaces(Node n, JSONObject object) throws JSONException {
        Iterator itr = object.keys();
        while (itr.hasNext()) {
            String k = (String)itr.next();
            if (this.supressAtAttributes) {
                if (k.startsWith(this.attributeKey)) {
                    k = k.substring(1);
                }
                if (null == this.jsonAttributesAsElements) {
                    this.jsonAttributesAsElements = new ArrayList<String>();
                }
                if (!this.jsonAttributesAsElements.contains(k)) {
                    this.jsonAttributesAsElements.add(k);
                }
            }
            if (k.startsWith(this.attributeKey)) {
                QName name;
                Object o = object.opt(k);
                if ((k = k.substring(1)).equals("xmlns")) {
                    if (o instanceof JSONObject) {
                        JSONObject jo = (JSONObject)o;
                        Iterator pitr = jo.keys();
                        while (pitr.hasNext()) {
                            String prefix = (String)pitr.next();
                            String uri = jo.getString(prefix);
                            n.setNamespace(prefix, uri);
                        }
                    }
                    if (o instanceof String) {
                        String uri = o.toString();
                        name = new QName("", k);
                        n.setAttribute(name, uri);
                    }
                } else {
                    String strValue = o == null ? null : o.toString();
                    name = null;
                    name = k.contains(this.getNamespaceSeparator()) ? this.createQName(k, n) : new QName("", k);
                    n.setAttribute(name, strValue);
                }
                itr.remove();
                continue;
            }
            int dot = k.lastIndexOf(this.getNamespaceSeparator());
            if (dot == -1) continue;
            String jns = k.substring(0, dot);
            String xns = this.getNamespaceURI(jns);
            n.setNamespace("", xns);
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (this.ignoreNamespaces) {
            return "";
        }
        return (String)this.jnsToXns.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (this.ignoreNamespaces) {
            return "";
        }
        return (String)this.xnsToJns.get(namespaceURI);
    }

    @Override
    public Iterator<String> getPrefixes(String arg0) {
        if (this.ignoreNamespaces) {
            return Collections.emptySet().iterator();
        }
        return this.jnsToXns.keySet().iterator();
    }

    @Override
    public QName createQName(String rootName, Node node) {
        return this.createQName(rootName);
    }

    private void readAttribute(Node n, String k, JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); ++i) {
            this.readAttribute(n, k, array.getString(i));
        }
    }

    private void readAttribute(Node n, String name, String value) throws JSONException {
        QName qname = this.createQName(name);
        n.getAttributes().put(qname, value);
    }

    private QName createQName(String name) {
        String nsSeparator = this.getNamespaceSeparator();
        int dot = name.lastIndexOf(nsSeparator);
        QName qname = null;
        String local = name;
        if (dot == -1) {
            dot = 0;
        } else {
            local = local.substring(dot + nsSeparator.length());
        }
        String jns = name.substring(0, dot);
        String xns = this.getNamespaceURI(jns);
        qname = xns == null ? new QName(name) : new QName(xns, local);
        return qname;
    }

    public String createAttributeKey(String p, String ns, String local) {
        String jns;
        StringBuilder builder = new StringBuilder();
        if (!this.supressAtAttributes) {
            builder.append(this.attributeKey);
        }
        if ((jns = this.getJSONNamespace(p, ns)) != null && jns.length() != 0) {
            builder.append(jns).append(this.getNamespaceSeparator());
        }
        return builder.append(local).toString();
    }

    private String getJSONNamespace(String providedPrefix, String ns) {
        if (ns == null || ns.length() == 0 || this.ignoreNamespaces) {
            return "";
        }
        String jns = (String)this.xnsToJns.get(ns);
        if (jns == null && providedPrefix != null && providedPrefix.length() > 0) {
            jns = providedPrefix;
        }
        if (jns == null) {
            throw new IllegalStateException("Invalid JSON namespace: " + ns);
        }
        return jns;
    }

    public String createKey(String p, String ns, String local) {
        StringBuilder builder = new StringBuilder();
        String jns = this.getJSONNamespace(p, ns);
        if (jns != null && jns.length() != 0) {
            builder.append(jns).append(this.getNamespaceSeparator());
        }
        return builder.append(local).toString();
    }

    public boolean isElement(String p, String ns, String local) {
        if (this.attributesAsElements == null) {
            return false;
        }
        for (QName q : this.attributesAsElements) {
            if (!q.getNamespaceURI().equals(ns) || !q.getLocalPart().equals(local)) continue;
            return true;
        }
        return false;
    }

    public Object convertToJSONPrimitive(String text) {
        return this.typeConverter.convertToJSONPrimitive(text);
    }

    public Set<?> getPrimitiveArrayKeys() {
        return this.primitiveArrayKeys;
    }

    public boolean isDropRootElement() {
        return this.dropRootElement;
    }

    public boolean isRootElementArrayWrapper() {
        return this.rootElementArrayWrapper;
    }

    public List<?> getIgnoredElements() {
        return this.ignoredElements;
    }

    public boolean isWriteNullAsString() {
        return this.writeNullAsString;
    }

    public boolean isReadNullAsString() {
        return this.readNullAsString;
    }

    public boolean isIgnoreEmptyArrayValues() {
        return this.ignoreEmptyArrayValues;
    }

    public boolean isEscapeForwardSlashAlways() {
        return this.escapeForwardSlashAlways;
    }

    public void setEscapeForwardSlashAlways(boolean escapeForwardSlash) {
        this.escapeForwardSlashAlways = escapeForwardSlash;
    }

    public String getNamespaceSeparator() {
        return this.jsonNamespaceSeparator == null ? DOT_NAMESPACE_SEP : this.jsonNamespaceSeparator;
    }

    private static class NullStringConverter
    implements TypeConverter {
        private static final String NULL_STRING = "null";
        private TypeConverter converter;

        public NullStringConverter(TypeConverter converter) {
            this.converter = converter;
        }

        @Override
        public Object convertToJSONPrimitive(String text) {
            if (text != null && NULL_STRING.equals(text)) {
                return null;
            }
            return this.converter.convertToJSONPrimitive(text);
        }
    }
}

