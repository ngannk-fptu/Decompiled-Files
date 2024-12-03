/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import javax.xml.namespace.QName;
import org.apache.axis.wsdl.symbolTable.ContainedEntry;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class ElementDecl
extends ContainedEntry {
    private String documentation;
    private boolean minOccursIs0 = false;
    private boolean nillable = false;
    private boolean optional = false;
    private boolean anyElement = false;
    private boolean maxOccursIsUnbounded = false;
    private boolean maxOccursExactOne;

    public ElementDecl(TypeEntry type, QName name) {
        super(type, name);
    }

    public boolean getMinOccursIs0() {
        return this.minOccursIs0;
    }

    public void setMinOccursIs0(boolean minOccursIs0) {
        this.minOccursIs0 = minOccursIs0;
    }

    public boolean getMaxOccursIsUnbounded() {
        return this.maxOccursIsUnbounded;
    }

    public void setMaxOccursIsUnbounded(boolean maxOccursIsUnbounded) {
        this.maxOccursIsUnbounded = maxOccursIsUnbounded;
    }

    public boolean getMaxOccursIsExactlyOne() {
        return this.maxOccursExactOne;
    }

    public void setMaxOccursIsExactlyOne(boolean exactOne) {
        this.maxOccursExactOne = exactOne;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public boolean getNillable() {
        return this.nillable;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean getOptional() {
        return this.optional;
    }

    public boolean getAnyElement() {
        return this.anyElement;
    }

    public void setAnyElement(boolean anyElement) {
        this.anyElement = anyElement;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}

