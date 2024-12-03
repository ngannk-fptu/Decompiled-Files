/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package com.atlassian.plugins.rest.doclet.generators.grammars;

import com.atlassian.plugins.rest.doclet.generators.grammars.Doc;
import com.atlassian.plugins.rest.doclet.generators.grammars.Include;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"doc", "include", "any"})
@XmlRootElement(name="grammars")
public class Grammars {
    protected List<Doc> doc;
    protected List<Include> include;
    @XmlAnyElement(lax=true)
    protected List<Object> any;

    public List<Doc> getDoc() {
        if (this.doc == null) {
            this.doc = new ArrayList<Doc>();
        }
        return this.doc;
    }

    public List<Include> getInclude() {
        if (this.include == null) {
            this.include = new ArrayList<Include>();
        }
        return this.include;
    }

    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<Object>();
        }
        return this.any;
    }
}

