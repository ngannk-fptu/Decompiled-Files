/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;

public class OperationImpl
extends AbstractWSDLElement
implements Operation {
    protected String name = null;
    protected Input input = null;
    protected Output output = null;
    protected Map faults = new HashMap();
    protected OperationType style = null;
    protected List parameterOrder = null;
    protected List nativeAttributeNames = Arrays.asList(Constants.OPERATION_ATTR_NAMES);
    protected boolean isUndefined = true;
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return this.input;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public Output getOutput() {
        return this.output;
    }

    public void addFault(Fault fault) {
        this.faults.put(fault.getName(), fault);
    }

    public Fault getFault(String name) {
        return (Fault)this.faults.get(name);
    }

    public Fault removeFault(String name) {
        return (Fault)this.faults.remove(name);
    }

    public Map getFaults() {
        return this.faults;
    }

    public void setStyle(OperationType style) {
        this.style = style;
    }

    public OperationType getStyle() {
        return this.style;
    }

    public void setParameterOrdering(List parameterOrder) {
        this.parameterOrder = parameterOrder;
    }

    public List getParameterOrdering() {
        return this.parameterOrder;
    }

    public void setUndefined(boolean isUndefined) {
        this.isUndefined = isUndefined;
    }

    public boolean isUndefined() {
        return this.isUndefined;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("Operation: name=" + this.name);
        if (this.parameterOrder != null) {
            strBuf.append("\nparameterOrder=" + this.parameterOrder);
        }
        if (this.style != null) {
            strBuf.append("\nstyle=" + this.style);
        }
        if (this.input != null) {
            strBuf.append("\n" + this.input);
        }
        if (this.output != null) {
            strBuf.append("\n" + this.output);
        }
        if (this.faults != null) {
            Iterator faultIterator = this.faults.values().iterator();
            while (faultIterator.hasNext()) {
                strBuf.append("\n" + faultIterator.next());
            }
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }
}

