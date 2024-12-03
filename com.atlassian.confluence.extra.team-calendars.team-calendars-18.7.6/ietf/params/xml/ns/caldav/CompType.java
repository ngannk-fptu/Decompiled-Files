/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.caldav;

import ietf.params.xml.ns.caldav.AllcompType;
import ietf.params.xml.ns.caldav.AllpropType;
import ietf.params.xml.ns.caldav.PropType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CompType", propOrder={"allprop", "prop", "allcomp", "comp"})
public class CompType {
    protected AllpropType allprop;
    protected List<PropType> prop;
    protected AllcompType allcomp;
    protected List<CompType> comp;
    @XmlAttribute(required=true)
    protected String name;

    public AllpropType getAllprop() {
        return this.allprop;
    }

    public void setAllprop(AllpropType value) {
        this.allprop = value;
    }

    public List<PropType> getProp() {
        if (this.prop == null) {
            this.prop = new ArrayList<PropType>();
        }
        return this.prop;
    }

    public AllcompType getAllcomp() {
        return this.allcomp;
    }

    public void setAllcomp(AllcompType value) {
        this.allcomp = value;
    }

    public List<CompType> getComp() {
        if (this.comp == null) {
            this.comp = new ArrayList<CompType>();
        }
        return this.comp;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }
}

