/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.ws.Action
 *  javax.xml.ws.FaultAction
 */
package com.oracle.xmlns.webservices.jaxws_databinding;

import com.oracle.xmlns.webservices.jaxws_databinding.Util;
import com.oracle.xmlns.webservices.jaxws_databinding.XmlFaultAction;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"faultAction"})
@XmlRootElement(name="action")
public class XmlAction
implements Action {
    @XmlElement(name="fault-action")
    protected List<XmlFaultAction> faultAction;
    @XmlAttribute(name="input")
    protected String input;
    @XmlAttribute(name="output")
    protected String output;

    public List<XmlFaultAction> getFaultAction() {
        if (this.faultAction == null) {
            this.faultAction = new ArrayList<XmlFaultAction>();
        }
        return this.faultAction;
    }

    public String getInput() {
        return Util.nullSafe(this.input);
    }

    public void setInput(String value) {
        this.input = value;
    }

    public String getOutput() {
        return Util.nullSafe(this.output);
    }

    public void setOutput(String value) {
        this.output = value;
    }

    public String input() {
        return Util.nullSafe(this.input);
    }

    public String output() {
        return Util.nullSafe(this.output);
    }

    public FaultAction[] fault() {
        return new FaultAction[0];
    }

    public Class<? extends Annotation> annotationType() {
        return Action.class;
    }
}

