/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlID
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.sun.research.ws.wadl;

import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Method;
import com.sun.research.ws.wadl.Param;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"doc", "param", "methodOrResource", "any"})
@XmlRootElement(name="resource")
public class Resource {
    protected List<Doc> doc;
    protected List<Param> param;
    @XmlElements(value={@XmlElement(name="method", type=Method.class), @XmlElement(name="resource", type=Resource.class)})
    protected List<Object> methodOrResource;
    @XmlAnyElement(lax=true)
    protected List<Object> any;
    @XmlAttribute
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;
    @XmlAttribute
    protected List<String> type;
    @XmlAttribute
    protected String queryType;
    @XmlAttribute
    protected String path;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }

    public List<Param> getParam() {
        if (this.param == null) {
            this.param = new ArrayList<Param>();
        }
        return this.param;
    }

    public List<Object> getMethodOrResource() {
        if (this.methodOrResource == null) {
            this.methodOrResource = new ArrayList<Object>();
        }
        return this.methodOrResource;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public List<String> getType() {
        if (this.type == null) {
            this.type = new ArrayList<String>();
        }
        return this.type;
    }

    public String getQueryType() {
        if (this.queryType == null) {
            return "application/x-www-form-urlencoded";
        }
        return this.queryType;
    }

    public void setQueryType(String value) {
        this.queryType = value;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String value) {
        this.path = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}

