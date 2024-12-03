/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.xalan.processor.XSLTAttributeDef;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.processor.XSLTSchema;
import org.apache.xml.utils.QName;

public class XSLTElementDef {
    static final int T_ELEMENT = 1;
    static final int T_PCDATA = 2;
    static final int T_ANY = 3;
    private int m_type = 1;
    private String m_namespace;
    private String m_name;
    private String m_nameAlias;
    private XSLTElementDef[] m_elements;
    private XSLTAttributeDef[] m_attributes;
    private XSLTElementProcessor m_elementProcessor;
    private Class m_classObject;
    private boolean m_has_required = false;
    private boolean m_required = false;
    Hashtable m_requiredFound;
    boolean m_isOrdered = false;
    private int m_order = -1;
    private int m_lastOrder = -1;
    private boolean m_multiAllowed = true;

    XSLTElementDef() {
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject) {
        this.build(namespace, name, nameAlias, elements, attributes, contentHandler, classObject);
        if (null != namespace && (namespace.equals("http://www.w3.org/1999/XSL/Transform") || namespace.equals("http://xml.apache.org/xalan") || namespace.equals("http://xml.apache.org/xslt"))) {
            schema.addAvailableElement(new QName(namespace, name));
            if (null != nameAlias) {
                schema.addAvailableElement(new QName(namespace, nameAlias));
            }
        }
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, boolean has_required) {
        this.m_has_required = has_required;
        this.build(namespace, name, nameAlias, elements, attributes, contentHandler, classObject);
        if (null != namespace && (namespace.equals("http://www.w3.org/1999/XSL/Transform") || namespace.equals("http://xml.apache.org/xalan") || namespace.equals("http://xml.apache.org/xslt"))) {
            schema.addAvailableElement(new QName(namespace, name));
            if (null != nameAlias) {
                schema.addAvailableElement(new QName(namespace, nameAlias));
            }
        }
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, boolean has_required, boolean required) {
        this(schema, namespace, name, nameAlias, elements, attributes, contentHandler, classObject, has_required);
        this.m_required = required;
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, boolean has_required, boolean required, int order, boolean multiAllowed) {
        this(schema, namespace, name, nameAlias, elements, attributes, contentHandler, classObject, has_required, required);
        this.m_order = order;
        this.m_multiAllowed = multiAllowed;
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, boolean has_required, boolean required, boolean has_order, int order, boolean multiAllowed) {
        this(schema, namespace, name, nameAlias, elements, attributes, contentHandler, classObject, has_required, required);
        this.m_order = order;
        this.m_multiAllowed = multiAllowed;
        this.m_isOrdered = has_order;
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, boolean has_order, int order, boolean multiAllowed) {
        this(schema, namespace, name, nameAlias, elements, attributes, contentHandler, classObject, order, multiAllowed);
        this.m_isOrdered = has_order;
    }

    XSLTElementDef(XSLTSchema schema, String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject, int order, boolean multiAllowed) {
        this(schema, namespace, name, nameAlias, elements, attributes, contentHandler, classObject);
        this.m_order = order;
        this.m_multiAllowed = multiAllowed;
    }

    XSLTElementDef(Class classObject, XSLTElementProcessor contentHandler, int type) {
        this.m_classObject = classObject;
        this.m_type = type;
        this.setElementProcessor(contentHandler);
    }

    void build(String namespace, String name, String nameAlias, XSLTElementDef[] elements, XSLTAttributeDef[] attributes, XSLTElementProcessor contentHandler, Class classObject) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_nameAlias = nameAlias;
        this.m_elements = elements;
        this.m_attributes = attributes;
        this.setElementProcessor(contentHandler);
        this.m_classObject = classObject;
        if (this.hasRequired() && this.m_elements != null) {
            for (XSLTElementDef def : this.m_elements) {
                if (def == null || !def.getRequired()) continue;
                if (this.m_requiredFound == null) {
                    this.m_requiredFound = new Hashtable();
                }
                this.m_requiredFound.put(def.getName(), "xsl:" + def.getName());
            }
        }
    }

    private static boolean equalsMayBeNull(Object obj1, Object obj2) {
        return obj2 == obj1 || null != obj1 && null != obj2 && obj2.equals(obj1);
    }

    private static boolean equalsMayBeNullOrZeroLen(String s1, String s2) {
        int len2;
        int len1 = s1 == null ? 0 : s1.length();
        int n = len2 = s2 == null ? 0 : s2.length();
        return len1 != len2 ? false : (len1 == 0 ? true : s1.equals(s2));
    }

    int getType() {
        return this.m_type;
    }

    void setType(int t) {
        this.m_type = t;
    }

    String getNamespace() {
        return this.m_namespace;
    }

    String getName() {
        return this.m_name;
    }

    String getNameAlias() {
        return this.m_nameAlias;
    }

    public XSLTElementDef[] getElements() {
        return this.m_elements;
    }

    void setElements(XSLTElementDef[] defs) {
        this.m_elements = defs;
    }

    private boolean QNameEquals(String uri, String localName) {
        return XSLTElementDef.equalsMayBeNullOrZeroLen(this.m_namespace, uri) && (XSLTElementDef.equalsMayBeNullOrZeroLen(this.m_name, localName) || XSLTElementDef.equalsMayBeNullOrZeroLen(this.m_nameAlias, localName));
    }

    XSLTElementProcessor getProcessorFor(String uri, String localName) {
        XSLTElementProcessor elemDef = null;
        if (null == this.m_elements) {
            return null;
        }
        int n = this.m_elements.length;
        int order = -1;
        boolean multiAllowed = true;
        for (int i = 0; i < n; ++i) {
            XSLTElementDef def = this.m_elements[i];
            if (def.m_name.equals("*")) {
                if (XSLTElementDef.equalsMayBeNullOrZeroLen(uri, "http://www.w3.org/1999/XSL/Transform")) continue;
                elemDef = def.m_elementProcessor;
                order = def.getOrder();
                multiAllowed = def.getMultiAllowed();
                continue;
            }
            if (!def.QNameEquals(uri, localName)) continue;
            if (def.getRequired()) {
                this.setRequiredFound(def.getName(), true);
            }
            order = def.getOrder();
            multiAllowed = def.getMultiAllowed();
            elemDef = def.m_elementProcessor;
            break;
        }
        if (elemDef != null && this.isOrdered()) {
            int lastOrder = this.getLastOrder();
            if (order > lastOrder) {
                this.setLastOrder(order);
            } else {
                if (order == lastOrder && !multiAllowed) {
                    return null;
                }
                if (order < lastOrder && order > 0) {
                    return null;
                }
            }
        }
        return elemDef;
    }

    XSLTElementProcessor getProcessorForUnknown(String uri, String localName) {
        if (null == this.m_elements) {
            return null;
        }
        for (XSLTElementDef def : this.m_elements) {
            if (!def.m_name.equals("unknown") || uri.length() <= 0) continue;
            return def.m_elementProcessor;
        }
        return null;
    }

    XSLTAttributeDef[] getAttributes() {
        return this.m_attributes;
    }

    XSLTAttributeDef getAttributeDef(String uri, String localName) {
        XSLTAttributeDef defaultDef = null;
        for (XSLTAttributeDef attrDef : this.getAttributes()) {
            String uriDef = attrDef.getNamespace();
            String nameDef = attrDef.getName();
            if (nameDef.equals("*") && (XSLTElementDef.equalsMayBeNullOrZeroLen(uri, uriDef) || uriDef != null && uriDef.equals("*") && uri != null && uri.length() > 0)) {
                return attrDef;
            }
            if (nameDef.equals("*") && uriDef == null) {
                defaultDef = attrDef;
                continue;
            }
            if (!XSLTElementDef.equalsMayBeNullOrZeroLen(uri, uriDef) || !localName.equals(nameDef)) continue;
            return attrDef;
        }
        if (null == defaultDef && uri.length() > 0 && !XSLTElementDef.equalsMayBeNullOrZeroLen(uri, "http://www.w3.org/1999/XSL/Transform")) {
            return XSLTAttributeDef.m_foreignAttr;
        }
        return defaultDef;
    }

    public XSLTElementProcessor getElementProcessor() {
        return this.m_elementProcessor;
    }

    public void setElementProcessor(XSLTElementProcessor handler) {
        if (handler != null) {
            this.m_elementProcessor = handler;
            this.m_elementProcessor.setElemDef(this);
        }
    }

    Class getClassObject() {
        return this.m_classObject;
    }

    boolean hasRequired() {
        return this.m_has_required;
    }

    boolean getRequired() {
        return this.m_required;
    }

    void setRequiredFound(String elem, boolean found) {
        if (this.m_requiredFound.get(elem) != null) {
            this.m_requiredFound.remove(elem);
        }
    }

    boolean getRequiredFound() {
        if (this.m_requiredFound == null) {
            return true;
        }
        return this.m_requiredFound.isEmpty();
    }

    String getRequiredElem() {
        if (this.m_requiredFound == null) {
            return null;
        }
        Enumeration elems = this.m_requiredFound.elements();
        String s = "";
        boolean first = true;
        while (elems.hasMoreElements()) {
            if (first) {
                first = false;
            } else {
                s = s + ", ";
            }
            s = s + (String)elems.nextElement();
        }
        return s;
    }

    boolean isOrdered() {
        return this.m_isOrdered;
    }

    int getOrder() {
        return this.m_order;
    }

    int getLastOrder() {
        return this.m_lastOrder;
    }

    void setLastOrder(int order) {
        this.m_lastOrder = order;
    }

    boolean getMultiAllowed() {
        return this.m_multiAllowed;
    }
}

