/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaServiceIfaceWriter
extends JavaClassWriter {
    private Service service;
    private SymbolTable symbolTable;
    static /* synthetic */ Class class$javax$xml$rpc$ServiceException;

    protected JavaServiceIfaceWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable) {
        super(emitter, sEntry.getName(), "service");
        this.service = sEntry.getService();
        this.symbolTable = symbolTable;
    }

    protected String getClassText() {
        return "interface ";
    }

    protected String getExtendsText() {
        return "extends javax.xml.rpc.Service ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        this.writeComment(pw, this.service.getDocumentationElement(), false);
        Map portMap = this.service.getPorts();
        Iterator portIterator = portMap.values().iterator();
        while (portIterator.hasNext()) {
            Port p = (Port)portIterator.next();
            Binding binding = p.getBinding();
            if (binding == null) {
                throw new IOException(Messages.getMessage("emitFailNoBinding01", new String[]{p.getName()}));
            }
            BindingEntry bEntry = this.symbolTable.getBindingEntry(binding.getQName());
            if (bEntry == null) {
                throw new IOException(Messages.getMessage("emitFailNoBindingEntry01", new String[]{binding.getQName().toString()}));
            }
            PortTypeEntry ptEntry = this.symbolTable.getPortTypeEntry(binding.getPortType().getQName());
            if (ptEntry == null) {
                throw new IOException(Messages.getMessage("emitFailNoPortType01", new String[]{binding.getPortType().getQName().toString()}));
            }
            if (bEntry.getBindingType() != 0) continue;
            String portName = (String)bEntry.getDynamicVar("port name:" + p.getName());
            if (portName == null) {
                portName = p.getName();
            }
            if (!JavaUtils.isJavaId(portName)) {
                portName = Utils.xmlNameToJavaClass(portName);
            }
            String bindingType = (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
            pw.println("    public java.lang.String get" + portName + "Address();");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName + "() throws " + (class$javax$xml$rpc$ServiceException == null ? JavaServiceIfaceWriter.class$("javax.xml.rpc.ServiceException") : class$javax$xml$rpc$ServiceException).getName() + ";");
            pw.println();
            pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) throws " + (class$javax$xml$rpc$ServiceException == null ? JavaServiceIfaceWriter.class$("javax.xml.rpc.ServiceException") : class$javax$xml$rpc$ServiceException).getName() + ";");
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

