/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyAttribute
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlID
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.sun.research.ws.wadl;

import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Link;
import com.sun.research.ws.wadl.Option;
import com.sun.research.ws.wadl.ParamStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"doc", "option", "link", "any"})
@XmlRootElement(name="param")
public class Param {
    protected List<Doc> doc;
    protected List<Option> option;
    protected Link link;
    @XmlAnyElement(lax=true)
    protected List<Object> any;
    @XmlAttribute
    @XmlSchemaType(name="anyURI")
    protected String href;
    @XmlAttribute
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlSchemaType(name="NMTOKEN")
    protected String name;
    @XmlAttribute
    protected ParamStyle style;
    @XmlAttribute
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;
    @XmlAttribute
    protected QName type;
    @XmlAttribute(name="default")
    protected String _default;
    @XmlAttribute
    protected Boolean required;
    @XmlAttribute
    protected Boolean repeating;
    @XmlAttribute
    protected String fixed;
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

    public List<Option> getOption() {
        if (this.option == null) {
            this.option = new ArrayList<Option>();
        }
        return this.option;
    }

    public Link getLink() {
        return this.link;
    }

    public void setLink(Link value) {
        this.link = value;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public ParamStyle getStyle() {
        return this.style;
    }

    public void setStyle(ParamStyle value) {
        this.style = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public QName getType() {
        if (this.type == null) {
            return new QName("http://www.w3.org/2001/XMLSchema", "string", "xs");
        }
        return this.type;
    }

    public void setType(QName value) {
        this.type = value;
    }

    public String getDefault() {
        return this._default;
    }

    public void setDefault(String value) {
        this._default = value;
    }

    public boolean getRequired() {
        if (this.required == null) {
            return false;
        }
        return this.required;
    }

    public void setRequired(Boolean value) {
        this.required = value;
    }

    public boolean getRepeating() {
        if (this.repeating == null) {
            return false;
        }
        return this.repeating;
    }

    public void setRepeating(Boolean value) {
        this.repeating = value;
    }

    public String getFixed() {
        return this.fixed;
    }

    public void setFixed(String value) {
        this.fixed = value;
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

