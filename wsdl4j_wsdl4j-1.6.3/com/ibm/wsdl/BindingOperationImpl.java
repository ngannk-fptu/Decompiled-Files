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
import javax.wsdl.BindingFault;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;

public class BindingOperationImpl
extends AbstractWSDLElement
implements BindingOperation {
    protected String name = null;
    protected Operation operation = null;
    protected BindingInput bindingInput = null;
    protected BindingOutput bindingOutput = null;
    protected Map bindingFaults = new HashMap();
    protected List nativeAttributeNames = Arrays.asList(Constants.BINDING_OPERATION_ATTR_NAMES);
    public static final long serialVersionUID = 1L;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public void setBindingInput(BindingInput bindingInput) {
        this.bindingInput = bindingInput;
    }

    public BindingInput getBindingInput() {
        return this.bindingInput;
    }

    public void setBindingOutput(BindingOutput bindingOutput) {
        this.bindingOutput = bindingOutput;
    }

    public BindingOutput getBindingOutput() {
        return this.bindingOutput;
    }

    public void addBindingFault(BindingFault bindingFault) {
        this.bindingFaults.put(bindingFault.getName(), bindingFault);
    }

    public BindingFault getBindingFault(String name) {
        return (BindingFault)this.bindingFaults.get(name);
    }

    public BindingFault removeBindingFault(String name) {
        return (BindingFault)this.bindingFaults.remove(name);
    }

    public Map getBindingFaults() {
        return this.bindingFaults;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("BindingOperation: name=" + this.name);
        if (this.bindingInput != null) {
            strBuf.append("\n" + this.bindingInput);
        }
        if (this.bindingOutput != null) {
            strBuf.append("\n" + this.bindingOutput);
        }
        if (this.bindingFaults != null) {
            Iterator faultIterator = this.bindingFaults.values().iterator();
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

