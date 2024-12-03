/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Fault
 *  javax.wsdl.Message
 *  javax.wsdl.Part
 *  javax.wsdl.extensions.soap.SOAPHeaderFault
 */
package org.apache.axis.wsdl.symbolTable;

import java.io.IOException;
import java.util.Map;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Part;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.xml.namespace.QName;
import org.apache.axis.constants.Use;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;

public class FaultInfo {
    private Message message;
    private QName xmlType;
    private Use use;
    private QName qName;
    private String name;

    public FaultInfo(Fault fault, Use use, String namespace, SymbolTable symbolTable) {
        this.message = fault.getMessage();
        this.xmlType = this.getFaultType(symbolTable, this.getFaultPart());
        this.use = use != null ? use : Use.LITERAL;
        this.name = fault.getName();
        Part part = this.getFaultPart();
        this.qName = part == null ? null : (part.getTypeName() != null ? new QName(namespace, part.getName()) : part.getElementName());
    }

    public FaultInfo(SOAPHeaderFault fault, SymbolTable symbolTable) throws IOException {
        MessageEntry mEntry = symbolTable.getMessageEntry(fault.getMessage());
        if (mEntry == null) {
            throw new IOException(Messages.getMessage("noMsg", fault.getMessage().toString()));
        }
        this.message = mEntry.getMessage();
        Part part = this.message.getPart(fault.getPart());
        this.xmlType = this.getFaultType(symbolTable, part);
        this.use = Use.getUse(fault.getUse());
        this.qName = part == null ? null : (part.getTypeName() != null ? new QName(fault.getNamespaceURI(), part.getName()) : part.getElementName());
        this.name = this.qName.getLocalPart();
    }

    public FaultInfo(QName faultMessage, String faultPart, String faultUse, String faultNamespaceURI, SymbolTable symbolTable) throws IOException {
        MessageEntry mEntry = symbolTable.getMessageEntry(faultMessage);
        if (mEntry == null) {
            throw new IOException(Messages.getMessage("noMsg", faultMessage.toString()));
        }
        this.message = mEntry.getMessage();
        Part part = this.message.getPart(faultPart);
        this.xmlType = this.getFaultType(symbolTable, part);
        this.use = Use.getUse(faultUse);
        this.qName = part == null ? null : (part.getTypeName() != null ? new QName(faultNamespaceURI, part.getName()) : part.getElementName());
        this.name = this.qName.getLocalPart();
    }

    public Message getMessage() {
        return this.message;
    }

    public QName getXMLType() {
        return this.xmlType;
    }

    public Use getUse() {
        return this.use;
    }

    public QName getQName() {
        return this.qName;
    }

    public String getName() {
        return this.name;
    }

    private Part getFaultPart() {
        Map parts = this.message.getParts();
        if (parts.size() == 0) {
            return null;
        }
        return (Part)parts.values().iterator().next();
    }

    private QName getFaultType(SymbolTable st, Part part) {
        if (part != null) {
            if (part.getTypeName() != null) {
                return part.getTypeName();
            }
            Element entry = st.getElement(part.getElementName());
            if (entry != null && entry.getRefType() != null) {
                return entry.getRefType().getQName();
            }
        }
        return null;
    }
}

