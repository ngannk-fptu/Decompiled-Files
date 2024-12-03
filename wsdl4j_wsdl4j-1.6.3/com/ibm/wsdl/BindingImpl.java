/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl;

import com.ibm.wsdl.AbstractWSDLElement;
import com.ibm.wsdl.Constants;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

public class BindingImpl
extends AbstractWSDLElement
implements Binding {
    protected QName name = null;
    protected PortType portType = null;
    protected List bindingOperations = new Vector();
    protected List nativeAttributeNames = Arrays.asList(Constants.BINDING_ATTR_NAMES);
    protected boolean isUndefined = true;
    public static final long serialVersionUID = 1L;

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getQName() {
        return this.name;
    }

    public void setPortType(PortType portType) {
        this.portType = portType;
    }

    public PortType getPortType() {
        return this.portType;
    }

    public void addBindingOperation(BindingOperation bindingOperation) {
        this.bindingOperations.add(bindingOperation);
    }

    public BindingOperation getBindingOperation(String name, String inputName, String outputName) {
        boolean found = false;
        BindingOperation ret = null;
        for (BindingOperation op : this.bindingOperations) {
            boolean specifiedDefault;
            Operation tempOp;
            OperationType opStyle;
            PortType pt;
            String opName = op.getName();
            if (name != null && opName != null) {
                if (!name.equals(opName)) {
                    op = null;
                }
            } else if (name != null || opName != null) {
                op = null;
            }
            if (op != null && inputName != null) {
                pt = this.getPortType();
                opStyle = null;
                if (pt != null && (tempOp = pt.getOperation(name, inputName, outputName)) != null) {
                    opStyle = tempOp.getStyle();
                }
                String defaultInputName = opName;
                if (opStyle == OperationType.REQUEST_RESPONSE) {
                    defaultInputName = opName + "Request";
                } else if (opStyle == OperationType.SOLICIT_RESPONSE) {
                    defaultInputName = opName + "Solicit";
                }
                specifiedDefault = inputName.equals(defaultInputName);
                BindingInput input = op.getBindingInput();
                if (input != null) {
                    String opInputName = input.getName();
                    if (opInputName == null) {
                        if (!specifiedDefault && !inputName.equals(":none")) {
                            op = null;
                        }
                    } else if (!opInputName.equals(inputName)) {
                        op = null;
                    }
                } else {
                    op = null;
                }
            }
            if (op != null && outputName != null) {
                pt = this.getPortType();
                opStyle = null;
                if (pt != null && (tempOp = pt.getOperation(name, inputName, outputName)) != null) {
                    opStyle = tempOp.getStyle();
                }
                String defaultOutputName = opName;
                if (opStyle == OperationType.REQUEST_RESPONSE || opStyle == OperationType.SOLICIT_RESPONSE) {
                    defaultOutputName = opName + "Response";
                }
                specifiedDefault = outputName.equals(defaultOutputName);
                BindingOutput output = op.getBindingOutput();
                if (output != null) {
                    String opOutputName = output.getName();
                    if (opOutputName == null) {
                        if (!specifiedDefault && !outputName.equals(":none")) {
                            op = null;
                        }
                    } else if (!opOutputName.equals(outputName)) {
                        op = null;
                    }
                } else {
                    op = null;
                }
            }
            if (op == null) continue;
            if (found) {
                throw new IllegalArgumentException("Duplicate operation with name=" + name + (inputName != null ? ", inputName=" + inputName : "") + (outputName != null ? ", outputName=" + outputName : "") + ", found in binding '" + this.getQName() + "'.");
            }
            found = true;
            ret = op;
        }
        return ret;
    }

    public List getBindingOperations() {
        return this.bindingOperations;
    }

    public BindingOperation removeBindingOperation(String name, String inputName, String outputName) {
        BindingOperation op = this.getBindingOperation(name, inputName, outputName);
        if (this.bindingOperations.remove(op)) {
            return op;
        }
        return null;
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
        strBuf.append("Binding: name=");
        strBuf.append(this.name);
        if (this.portType != null) {
            strBuf.append("\n");
            strBuf.append(this.portType);
        }
        if (this.bindingOperations != null) {
            Iterator bindingOperationIterator = this.bindingOperations.iterator();
            while (bindingOperationIterator.hasNext()) {
                strBuf.append("\n");
                strBuf.append(bindingOperationIterator.next());
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

