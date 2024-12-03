/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlValue
 */
package com.sun.xml.ws.addressing;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlRootElement(name="ProblemHeaderQName", namespace="http://www.w3.org/2005/08/addressing")
public class ProblemHeaderQName {
    @XmlValue
    private QName value;

    public ProblemHeaderQName() {
    }

    public ProblemHeaderQName(QName name) {
        this.value = name;
    }
}

