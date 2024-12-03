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
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.xml.ws.runtime.config;

import com.sun.xml.ws.runtime.config.TubelineDefinition;
import com.sun.xml.ws.runtime.config.TubelineMapping;
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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="tubelinesConfigCType", propOrder={"tubelineMappings", "tubelineDefinitions", "any"})
public class Tubelines {
    @XmlElement(name="tubeline-mapping")
    protected List<TubelineMapping> tubelineMappings;
    @XmlElement(name="tubeline")
    protected List<TubelineDefinition> tubelineDefinitions;
    @XmlAnyElement(lax=true)
    protected List<Object> any;
    @XmlAttribute(name="default")
    @XmlSchemaType(name="anyURI")
    protected String _default;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public List<TubelineMapping> getTubelineMappings() {
        if (this.tubelineMappings == null) {
            this.tubelineMappings = new ArrayList<TubelineMapping>();
        }
        return this.tubelineMappings;
    }

    public List<TubelineDefinition> getTubelineDefinitions() {
        if (this.tubelineDefinitions == null) {
            this.tubelineDefinitions = new ArrayList<TubelineDefinition>();
        }
        return this.tubelineDefinitions;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }

    public String getDefault() {
        return this._default;
    }

    public void setDefault(String value) {
        this._default = value;
    }

    public Map<QName, String> getOtherAttributes() {
        return this.otherAttributes;
    }
}

