/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.apache.axis.description.ParameterDesc;

public class FaultDesc
implements Serializable {
    private String name;
    private QName qname;
    private ArrayList parameters;
    private String className;
    private QName xmlType;
    private boolean complex;

    public FaultDesc() {
    }

    public FaultDesc(QName qname, String className, QName xmlType, boolean complex) {
        this.qname = qname;
        this.className = className;
        this.xmlType = xmlType;
        this.complex = complex;
    }

    public QName getQName() {
        return this.qname;
    }

    public void setQName(QName name) {
        this.qname = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList getParameters() {
        return this.parameters;
    }

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isComplex() {
        return this.complex;
    }

    public void setComplex(boolean complex) {
        this.complex = complex;
    }

    public QName getXmlType() {
        return this.xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public String toString() {
        return this.toString("");
    }

    public String toString(String indent) {
        String text = "";
        text = text + indent + "name: " + this.getName() + "\n";
        text = text + indent + "qname: " + this.getQName() + "\n";
        text = text + indent + "type: " + this.getXmlType() + "\n";
        text = text + indent + "Class: " + this.getClassName() + "\n";
        for (int i = 0; this.parameters != null && i < this.parameters.size(); ++i) {
            text = text + indent + " ParameterDesc[" + i + "]:\n";
            text = text + indent + ((ParameterDesc)this.parameters.get(i)).toString("  ") + "\n";
        }
        return text;
    }
}

