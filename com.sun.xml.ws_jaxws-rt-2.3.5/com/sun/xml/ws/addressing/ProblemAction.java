/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.xml.ws.addressing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProblemAction", namespace="http://www.w3.org/2005/08/addressing")
public class ProblemAction {
    @XmlElement(name="Action", namespace="http://www.w3.org/2005/08/addressing")
    private String action;
    @XmlElement(name="SoapAction", namespace="http://www.w3.org/2005/08/addressing")
    private String soapAction;

    public ProblemAction() {
    }

    public ProblemAction(String action) {
        this.action = action;
    }

    public ProblemAction(String action, String soapAction) {
        this.action = action;
        this.soapAction = soapAction;
    }

    public String getAction() {
        return this.action;
    }

    public String getSoapAction() {
        return this.soapAction;
    }
}

