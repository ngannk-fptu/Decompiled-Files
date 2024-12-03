/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Operation
 *  javax.wsdl.extensions.soap.SOAPFault
 */
package org.apache.axis.wsdl.symbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.wsdl.Binding;
import javax.wsdl.Operation;
import javax.wsdl.extensions.soap.SOAPFault;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.wsdl.symbolTable.MimeInfo;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;

public class BindingEntry
extends SymTabEntry {
    public static final int TYPE_SOAP = 0;
    public static final int TYPE_HTTP_GET = 1;
    public static final int TYPE_HTTP_POST = 2;
    public static final int TYPE_UNKNOWN = 3;
    public static final int USE_ENCODED = 0;
    public static final int USE_LITERAL = 1;
    private Binding binding;
    private int bindingType;
    private Style bindingStyle;
    private boolean hasLiteral;
    private HashMap attributes;
    private HashMap parameters = new HashMap();
    private HashMap faults = new HashMap();
    private Map mimeTypes;
    private Map headerParts;
    private ArrayList dimeOps = new ArrayList();
    public static final int NO_HEADER = 0;
    public static final int IN_HEADER = 1;
    public static final int OUT_HEADER = 2;

    public BindingEntry(Binding binding, int bindingType, Style bindingStyle, boolean hasLiteral, HashMap attributes, Map mimeTypes, Map headerParts) {
        super(binding.getQName());
        this.binding = binding;
        this.bindingType = bindingType;
        this.bindingStyle = bindingStyle;
        this.hasLiteral = hasLiteral;
        this.attributes = attributes == null ? new HashMap() : attributes;
        this.mimeTypes = mimeTypes == null ? new HashMap() : mimeTypes;
        this.headerParts = headerParts == null ? new HashMap() : headerParts;
    }

    public BindingEntry(Binding binding) {
        super(binding.getQName());
        this.binding = binding;
        this.bindingType = 3;
        this.bindingStyle = Style.DOCUMENT;
        this.hasLiteral = false;
        this.attributes = new HashMap();
        this.mimeTypes = new HashMap();
        this.headerParts = new HashMap();
    }

    public Parameters getParameters(Operation operation) {
        return (Parameters)this.parameters.get(operation);
    }

    public HashMap getParameters() {
        return this.parameters;
    }

    public void setParameters(HashMap parameters) {
        this.parameters = parameters;
    }

    public MimeInfo getMIMEInfo(String operationName, String parameterName) {
        Map opMap = (Map)this.mimeTypes.get(operationName);
        if (opMap == null) {
            return null;
        }
        return (MimeInfo)opMap.get(parameterName);
    }

    public Map getMIMETypes() {
        return this.mimeTypes;
    }

    public void setMIMEInfo(String operationName, String parameterName, String type, String dims) {
        HashMap<String, MimeInfo> opMap = (HashMap<String, MimeInfo>)this.mimeTypes.get(operationName);
        if (opMap == null) {
            opMap = new HashMap<String, MimeInfo>();
            this.mimeTypes.put(operationName, opMap);
        }
        opMap.put(parameterName, new MimeInfo(type, dims));
    }

    public void setOperationDIME(String operationName) {
        if (this.dimeOps.indexOf(operationName) == -1) {
            this.dimeOps.add(operationName);
        }
    }

    public boolean isOperationDIME(String operationName) {
        return this.dimeOps.indexOf(operationName) >= 0;
    }

    public boolean isInHeaderPart(String operationName, String partName) {
        return (this.headerPart(operationName, partName) & 1) > 0;
    }

    public boolean isOutHeaderPart(String operationName, String partName) {
        return (this.headerPart(operationName, partName) & 2) > 0;
    }

    private int headerPart(String operationName, String partName) {
        Map opMap = (Map)this.headerParts.get(operationName);
        if (opMap == null) {
            return 0;
        }
        Integer I = (Integer)opMap.get(partName);
        return I == null ? 0 : I;
    }

    public Map getHeaderParts() {
        return this.headerParts;
    }

    public void setHeaderPart(String operationName, String partName, int headerFlags) {
        Integer I;
        HashMap<String, Integer> opMap = (HashMap<String, Integer>)this.headerParts.get(operationName);
        if (opMap == null) {
            opMap = new HashMap<String, Integer>();
            this.headerParts.put(operationName, opMap);
        }
        int i = (I = (Integer)opMap.get(partName)) == null ? headerFlags : I | headerFlags;
        opMap.put(partName, new Integer(i));
    }

    public Binding getBinding() {
        return this.binding;
    }

    public int getBindingType() {
        return this.bindingType;
    }

    protected void setBindingType(int bindingType) {
        if (bindingType < 0 || bindingType <= 3) {
            // empty if block
        }
        this.bindingType = bindingType;
    }

    public Style getBindingStyle() {
        return this.bindingStyle;
    }

    protected void setBindingStyle(Style bindingStyle) {
        this.bindingStyle = bindingStyle;
    }

    public boolean hasLiteral() {
        return this.hasLiteral;
    }

    protected void setHasLiteral(boolean hasLiteral) {
        this.hasLiteral = hasLiteral;
    }

    public Use getInputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED;
        }
        return attr.getInputBodyType();
    }

    protected void setInputBodyType(Operation operation, Use inputBodyType) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            attr = new OperationAttr();
            this.attributes.put(operation, attr);
        }
        attr.setInputBodyType(inputBodyType);
        if (inputBodyType == Use.LITERAL) {
            this.setHasLiteral(true);
        }
    }

    public Use getOutputBodyType(Operation operation) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED;
        }
        return attr.getOutputBodyType();
    }

    protected void setOutputBodyType(Operation operation, Use outputBodyType) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            attr = new OperationAttr();
            this.attributes.put(operation, attr);
        }
        attr.setOutputBodyType(outputBodyType);
        if (outputBodyType == Use.LITERAL) {
            this.setHasLiteral(true);
        }
    }

    protected void setBodyType(Operation operation, Use bodyType, boolean input) {
        if (input) {
            this.setInputBodyType(operation, bodyType);
        } else {
            this.setOutputBodyType(operation, bodyType);
        }
    }

    public Use getFaultBodyType(Operation operation, String faultName) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            return Use.ENCODED;
        }
        HashMap m = attr.getFaultBodyTypeMap();
        SOAPFault soapFault = (SOAPFault)m.get(faultName);
        if (soapFault == null) {
            return Use.ENCODED;
        }
        String use = soapFault.getUse();
        if ("literal".equals(use)) {
            return Use.LITERAL;
        }
        return Use.ENCODED;
    }

    public HashMap getFaults() {
        return this.faults;
    }

    public void setFaults(HashMap faults) {
        this.faults = faults;
    }

    public Set getOperations() {
        return this.attributes.keySet();
    }

    protected void setFaultBodyTypeMap(Operation operation, HashMap faultBodyTypeMap) {
        OperationAttr attr = (OperationAttr)this.attributes.get(operation);
        if (attr == null) {
            attr = new OperationAttr();
            this.attributes.put(operation, attr);
        }
        attr.setFaultBodyTypeMap(faultBodyTypeMap);
    }

    protected static class OperationAttr {
        private Use inputBodyType;
        private Use outputBodyType;
        private HashMap faultBodyTypeMap;

        public OperationAttr(Use inputBodyType, Use outputBodyType, HashMap faultBodyTypeMap) {
            this.inputBodyType = inputBodyType;
            this.outputBodyType = outputBodyType;
            this.faultBodyTypeMap = faultBodyTypeMap;
        }

        public OperationAttr() {
            this.inputBodyType = Use.ENCODED;
            this.outputBodyType = Use.ENCODED;
            this.faultBodyTypeMap = null;
        }

        public Use getInputBodyType() {
            return this.inputBodyType;
        }

        protected void setInputBodyType(Use inputBodyType) {
            this.inputBodyType = inputBodyType;
        }

        public Use getOutputBodyType() {
            return this.outputBodyType;
        }

        protected void setOutputBodyType(Use outputBodyType) {
            this.outputBodyType = outputBodyType;
        }

        public HashMap getFaultBodyTypeMap() {
            return this.faultBodyTypeMap;
        }

        protected void setFaultBodyTypeMap(HashMap faultBodyTypeMap) {
            this.faultBodyTypeMap = faultBodyTypeMap;
        }
    }
}

