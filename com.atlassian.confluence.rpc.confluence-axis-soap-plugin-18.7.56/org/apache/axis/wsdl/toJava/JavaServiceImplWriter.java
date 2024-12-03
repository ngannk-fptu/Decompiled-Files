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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.WSDLUtils;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaServiceImplWriter
extends JavaClassWriter {
    private ServiceEntry sEntry;
    private SymbolTable symbolTable;
    static /* synthetic */ Class class$javax$xml$rpc$ServiceException;

    protected JavaServiceImplWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable) {
        super(emitter, sEntry.getName() + "Locator", "service");
        this.sEntry = sEntry;
        this.symbolTable = symbolTable;
    }

    protected String getExtendsText() {
        return "extends org.apache.axis.client.Service ";
    }

    protected String getImplementsText() {
        return "implements " + this.sEntry.getName() + ' ';
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        Service service = this.sEntry.getService();
        this.writeComment(pw, service.getDocumentationElement(), false);
        Vector<String> getPortIfaces = new Vector<String>();
        Vector<String> getPortStubClasses = new Vector<String>();
        Vector<String> getPortPortNames = new Vector<String>();
        Vector<String> getPortPortXmlNames = new Vector<String>();
        boolean printGetPortNotice = false;
        Map portMap = service.getPorts();
        Iterator portIterator = portMap.values().iterator();
        this.writeConstructors(pw);
        while (portIterator.hasNext()) {
            String address;
            String bindingType;
            String stubClass;
            String portName;
            String portXmlName;
            Port p;
            block15: {
                p = (Port)portIterator.next();
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
                portXmlName = p.getName();
                portName = (String)bEntry.getDynamicVar("port name:" + p.getName());
                if (portName == null) {
                    portName = p.getName();
                }
                if (!JavaUtils.isJavaId(portName)) {
                    portName = Utils.xmlNameToJavaClass(portName);
                }
                stubClass = bEntry.getName() + "Stub";
                bindingType = (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
                if (getPortIfaces.contains(bindingType)) {
                    printGetPortNotice = true;
                }
                getPortIfaces.add(bindingType);
                getPortPortXmlNames.add(portXmlName);
                getPortStubClasses.add(stubClass);
                getPortPortNames.add(portName);
                address = WSDLUtils.getAddressFromPort(p);
                if (address == null) {
                    throw new IOException(Messages.getMessage("emitFail02", portName, this.className));
                }
                try {
                    new URL(address);
                }
                catch (MalformedURLException e) {
                    int protIndex;
                    URL url = null;
                    URLStreamHandler handler = null;
                    String handlerPkgs = System.getProperty("java.protocol.handler.pkgs");
                    if (handlerPkgs != null && (protIndex = address.indexOf(":")) > 0) {
                        String protocol = address.substring(0, protIndex);
                        StringTokenizer st = new StringTokenizer(handlerPkgs, "|");
                        while (st.hasMoreTokens()) {
                            String pkg = st.nextToken();
                            String handlerClass = pkg + "." + protocol + ".Handler";
                            try {
                                Class<?> c = Class.forName(handlerClass);
                                handler = (URLStreamHandler)c.newInstance();
                                url = new URL(null, address, handler);
                                break;
                            }
                            catch (Exception e2) {
                                url = null;
                            }
                        }
                    }
                    if (url != null) break block15;
                    if (this.emitter.isAllowInvalidURL()) {
                        System.err.println(Messages.getMessage("emitWarnInvalidURL01", new String[]{portName, this.className, address}));
                    }
                    throw new IOException(Messages.getMessage("emitFail03", new String[]{portName, this.className, address}));
                }
            }
            this.writeAddressInfo(pw, portName, address, p);
            String wsddServiceName = portName + "WSDDServiceName";
            this.writeWSDDServiceNameInfo(pw, wsddServiceName, portName, portXmlName);
            this.writeGetPortName(pw, bindingType, portName);
            this.writeGetPortNameURL(pw, bindingType, portName, stubClass, wsddServiceName);
            this.writeSetPortEndpointAddress(pw, portName);
        }
        this.writeGetPortClass(pw, getPortIfaces, getPortStubClasses, getPortPortNames, printGetPortNotice);
        this.writeGetPortQNameClass(pw, getPortPortNames, getPortPortXmlNames);
        this.writeGetServiceName(pw, this.sEntry.getQName());
        this.writeGetPorts(pw, this.sEntry.getQName().getNamespaceURI(), getPortPortXmlNames);
        this.writeSetEndpointAddress(pw, getPortPortNames);
    }

    protected void writeConstructors(PrintWriter pw) {
        pw.println();
        pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator() {");
        pw.println("    }");
        pw.println();
        pw.println();
        pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator(org.apache.axis.EngineConfiguration config) {");
        pw.println("        super(config);");
        pw.println("    }");
        pw.println();
        pw.println("    public " + Utils.getJavaLocalName(this.sEntry.getName()) + "Locator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) " + "throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("        super(wsdlLoc, sName);");
        pw.println("    }");
    }

    protected void writeAddressInfo(PrintWriter pw, String portName, String address, Port p) {
        pw.println();
        pw.println("    // " + Messages.getMessage("getProxy00", portName));
        this.writeComment(pw, p.getDocumentationElement(), true);
        pw.println("    private java.lang.String " + portName + "_address = \"" + address + "\";");
        pw.println();
        pw.println("    public java.lang.String get" + portName + "Address() {");
        pw.println("        return " + portName + "_address;");
        pw.println("    }");
        pw.println();
    }

    protected void writeWSDDServiceNameInfo(PrintWriter pw, String wsddServiceName, String portName, String portXmlName) {
        pw.println("    // " + Messages.getMessage("wsddServiceName00"));
        pw.println("    private java.lang.String " + wsddServiceName + " = \"" + portXmlName + "\";");
        pw.println();
        pw.println("    public java.lang.String get" + wsddServiceName + "() {");
        pw.println("        return " + wsddServiceName + ";");
        pw.println("    }");
        pw.println();
        pw.println("    public void set" + wsddServiceName + "(java.lang.String name) {");
        pw.println("        " + wsddServiceName + " = name;");
        pw.println("    }");
        pw.println();
    }

    protected void writeGetPortName(PrintWriter pw, String bindingType, String portName) {
        pw.println("    public " + bindingType + " get" + portName + "() throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("       java.net.URL endpoint;");
        pw.println("        try {");
        pw.println("            endpoint = new java.net.URL(" + portName + "_address);");
        pw.println("        }");
        pw.println("        catch (java.net.MalformedURLException e) {");
        pw.println("            throw new javax.xml.rpc.ServiceException(e);");
        pw.println("        }");
        pw.println("        return get" + portName + "(endpoint);");
        pw.println("    }");
        pw.println();
    }

    protected void writeGetPortNameURL(PrintWriter pw, String bindingType, String portName, String stubClass, String wsddServiceName) {
        pw.println("    public " + bindingType + " get" + portName + "(java.net.URL portAddress) throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("        try {");
        pw.println("            " + stubClass + " _stub = new " + stubClass + "(portAddress, this);");
        pw.println("            _stub.setPortName(get" + wsddServiceName + "());");
        pw.println("            return _stub;");
        pw.println("        }");
        pw.println("        catch (org.apache.axis.AxisFault e) {");
        pw.println("            return null;");
        pw.println("        }");
        pw.println("    }");
        pw.println();
    }

    protected void writeSetPortEndpointAddress(PrintWriter pw, String portName) {
        pw.println("    public void set" + portName + "EndpointAddress(java.lang.String address) {");
        pw.println("        " + portName + "_address = address;");
        pw.println("    }");
        pw.println();
    }

    protected void writeGetPortClass(PrintWriter pw, Vector getPortIfaces, Vector getPortStubClasses, Vector getPortPortNames, boolean printGetPortNotice) {
        pw.println("    /**");
        pw.println("     * " + Messages.getMessage("getPortDoc00"));
        pw.println("     * " + Messages.getMessage("getPortDoc01"));
        pw.println("     * " + Messages.getMessage("getPortDoc02"));
        if (printGetPortNotice) {
            pw.println("     * " + Messages.getMessage("getPortDoc03"));
            pw.println("     * " + Messages.getMessage("getPortDoc04"));
        }
        pw.println("     */");
        pw.println("    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        if (getPortIfaces.size() == 0) {
            pw.println("        throw new " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + "(\"" + Messages.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
        } else {
            pw.println("        try {");
            for (int i = 0; i < getPortIfaces.size(); ++i) {
                String iface = (String)getPortIfaces.get(i);
                String stubClass = (String)getPortStubClasses.get(i);
                String portName = (String)getPortPortNames.get(i);
                pw.println("            if (" + iface + ".class.isAssignableFrom(serviceEndpointInterface)) {");
                pw.println("                " + stubClass + " _stub = new " + stubClass + "(new java.net.URL(" + portName + "_address), this);");
                pw.println("                _stub.setPortName(get" + portName + "WSDDServiceName());");
                pw.println("                return _stub;");
                pw.println("            }");
            }
            pw.println("        }");
            pw.println("        catch (java.lang.Throwable t) {");
            pw.println("            throw new " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + "(t);");
            pw.println("        }");
            pw.println("        throw new " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + "(\"" + Messages.getMessage("noStub") + "  \" + (serviceEndpointInterface == null ? \"null\" : serviceEndpointInterface.getName()));");
        }
        pw.println("    }");
        pw.println();
    }

    protected void writeGetPortQNameClass(PrintWriter pw, Vector getPortPortNames, Vector getPortPortXmlNames) {
        pw.println("    /**");
        pw.println("     * " + Messages.getMessage("getPortDoc00"));
        pw.println("     * " + Messages.getMessage("getPortDoc01"));
        pw.println("     * " + Messages.getMessage("getPortDoc02"));
        pw.println("     */");
        pw.println("    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("        if (portName == null) {");
        pw.println("            return getPort(serviceEndpointInterface);");
        pw.println("        }");
        pw.println("        java.lang.String inputPortName = portName.getLocalPart();");
        pw.print("        ");
        for (int i = 0; i < getPortPortNames.size(); ++i) {
            String portName = (String)getPortPortNames.get(i);
            String portXmlName = (String)getPortPortXmlNames.get(i);
            pw.println("if (\"" + portXmlName + "\".equals(inputPortName)) {");
            pw.println("            return get" + portName + "();");
            pw.println("        }");
            pw.print("        else ");
        }
        pw.println(" {");
        pw.println("            java.rmi.Remote _stub = getPort(serviceEndpointInterface);");
        pw.println("            ((org.apache.axis.client.Stub) _stub).setPortName(portName);");
        pw.println("            return _stub;");
        pw.println("        }");
        pw.println("    }");
        pw.println();
    }

    protected void writeGetServiceName(PrintWriter pw, QName qname) {
        pw.println("    public javax.xml.namespace.QName getServiceName() {");
        pw.println("        return " + Utils.getNewQName(qname) + ";");
        pw.println("    }");
        pw.println();
    }

    protected void writeGetPorts(PrintWriter pw, String namespaceURI, Vector portNames) {
        pw.println("    private java.util.HashSet ports = null;");
        pw.println();
        pw.println("    public java.util.Iterator getPorts() {");
        pw.println("        if (ports == null) {");
        pw.println("            ports = new java.util.HashSet();");
        for (int i = 0; i < portNames.size(); ++i) {
            pw.println("            ports.add(new javax.xml.namespace.QName(\"" + namespaceURI + "\", \"" + portNames.get(i) + "\"));");
        }
        pw.println("        }");
        pw.println("        return ports.iterator();");
        pw.println("    }");
        pw.println();
    }

    protected void writeSetEndpointAddress(PrintWriter pw, Vector portNames) {
        if (portNames.isEmpty()) {
            return;
        }
        pw.println("    /**");
        pw.println("    * " + Messages.getMessage("setEndpointDoc00"));
        pw.println("    */");
        pw.println("    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("        ");
        Iterator p = portNames.iterator();
        while (p.hasNext()) {
            String name = (String)p.next();
            pw.println("if (\"" + name + "\".equals(portName)) {");
            pw.println("            set" + name + "EndpointAddress(address);");
            pw.println("        }");
            pw.println("        else ");
        }
        pw.println("{ // Unknown Port Name");
        pw.println("            throw new " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + "(\" " + Messages.getMessage("unknownPortName") + "\" + portName);");
        pw.println("        }");
        pw.println("    }");
        pw.println();
        pw.println("    /**");
        pw.println("    * " + Messages.getMessage("setEndpointDoc00"));
        pw.println("    */");
        pw.println("    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws " + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaServiceImplWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " {");
        pw.println("        setEndpointAddress(portName.getLocalPart(), address);");
        pw.println("    }");
        pw.println();
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

