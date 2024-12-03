/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Definition
 *  javax.wsdl.Port
 *  javax.wsdl.Service
 */
package org.apache.axis.wsdl.toJava;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaWriter;

public class JavaUndeployWriter
extends JavaWriter {
    protected Definition definition;

    public JavaUndeployWriter(Emitter emitter, Definition definition, SymbolTable notUsed) {
        super(emitter, "undeploy");
        this.definition = definition;
    }

    public void generate() throws IOException {
        if (this.emitter.isServerSide()) {
            super.generate();
        }
    }

    protected String getFileName() {
        String dir = this.emitter.getNamespaces().getAsDir(this.definition.getTargetNamespace());
        return dir + "undeploy.wsdd";
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
        pw.println(Messages.getMessage("deploy01"));
        pw.println(Messages.getMessage("deploy02"));
        pw.println(Messages.getMessage("deploy04"));
        pw.println(Messages.getMessage("deploy05"));
        pw.println(Messages.getMessage("deploy06"));
        pw.println(Messages.getMessage("deploy08"));
        pw.println(Messages.getMessage("deploy09"));
        pw.println();
        pw.println("<undeployment");
        pw.println("    xmlns=\"http://xml.apache.org/axis/wsdd/\">");
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        this.writeDeployServices(pw);
        pw.println("</undeployment>");
    }

    protected void writeDeployServices(PrintWriter pw) throws IOException {
        Map serviceMap = this.definition.getServices();
        Iterator mapIterator = serviceMap.values().iterator();
        while (mapIterator.hasNext()) {
            Service myService = (Service)mapIterator.next();
            pw.println();
            pw.println("  <!-- " + Messages.getMessage("wsdlService00", myService.getQName().getLocalPart()) + " -->");
            pw.println();
            Iterator portIterator = myService.getPorts().values().iterator();
            while (portIterator.hasNext()) {
                Port myPort = (Port)portIterator.next();
                this.writeDeployPort(pw, myPort);
            }
        }
    }

    protected void writeDeployPort(PrintWriter pw, Port port) throws IOException {
        String serviceName = port.getName();
        pw.println("  <service name=\"" + serviceName + "\"/>");
    }

    protected PrintWriter getPrintWriter(String filename) throws IOException {
        File file = new File(filename);
        File parent = new File(file.getParent());
        parent.mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter((OutputStream)out, "UTF-8");
        return new PrintWriter(writer);
    }
}

