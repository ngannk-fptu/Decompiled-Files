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
import javax.wsdl.Input;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

public class PortTypeImpl
extends AbstractWSDLElement
implements PortType {
    protected QName name = null;
    protected List operations = new Vector();
    protected List nativeAttributeNames = Arrays.asList(Constants.PORT_TYPE_ATTR_NAMES);
    protected boolean isUndefined = true;
    public static final long serialVersionUID = 1L;

    public void setQName(QName name) {
        this.name = name;
    }

    public QName getQName() {
        return this.name;
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

    public Operation getOperation(String name, String inputName, String outputName) {
        boolean found = false;
        Operation ret = null;
        for (Operation op : this.operations) {
            boolean specifiedDefault;
            OperationType opStyle;
            String opName = op.getName();
            if (name != null && opName != null) {
                if (!name.equals(opName)) {
                    op = null;
                }
            } else if (name != null || opName != null) {
                op = null;
            }
            if (op != null && inputName != null) {
                opStyle = op.getStyle();
                String defaultInputName = opName;
                if (opStyle == OperationType.REQUEST_RESPONSE) {
                    defaultInputName = opName + "Request";
                } else if (opStyle == OperationType.SOLICIT_RESPONSE) {
                    defaultInputName = opName + "Solicit";
                }
                specifiedDefault = inputName.equals(defaultInputName);
                Input input = op.getInput();
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
                opStyle = op.getStyle();
                String defaultOutputName = opName;
                if (opStyle == OperationType.REQUEST_RESPONSE || opStyle == OperationType.SOLICIT_RESPONSE) {
                    defaultOutputName = opName + "Response";
                }
                specifiedDefault = outputName.equals(defaultOutputName);
                Output output = op.getOutput();
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
                throw new IllegalArgumentException("Duplicate operation with name=" + name + (inputName != null ? ", inputName=" + inputName : "") + (outputName != null ? ", outputName=" + outputName : "") + ", found in portType '" + this.getQName() + "'.");
            }
            found = true;
            ret = op;
        }
        return ret;
    }

    public List getOperations() {
        return this.operations;
    }

    public Operation removeOperation(String name, String inputName, String outputName) {
        Operation op = this.getOperation(name, inputName, outputName);
        if (this.operations.remove(op)) {
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

    public List getNativeAttributeNames() {
        return this.nativeAttributeNames;
    }

    public String toString() {
        String superString;
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("PortType: name=" + this.name);
        if (this.operations != null) {
            Iterator opIterator = this.operations.iterator();
            while (opIterator.hasNext()) {
                strBuf.append("\n" + opIterator.next());
            }
        }
        if (!(superString = super.toString()).equals("")) {
            strBuf.append("\n");
            strBuf.append(superString);
        }
        return strBuf.toString();
    }
}

