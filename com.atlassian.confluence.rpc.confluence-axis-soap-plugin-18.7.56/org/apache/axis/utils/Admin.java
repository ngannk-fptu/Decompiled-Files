/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.client.AxisClient;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.server.AxisServer;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class Admin {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$Admin == null ? (class$org$apache$axis$utils$Admin = Admin.class$("org.apache.axis.utils.Admin")) : class$org$apache$axis$utils$Admin).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$Admin;

    public Element[] AdminService(Element[] xml) throws Exception {
        log.debug((Object)"Enter: Admin::AdminService");
        MessageContext msgContext = MessageContext.getCurrentContext();
        Document doc = this.process(msgContext, xml[0]);
        Element[] result = new Element[]{doc.getDocumentElement()};
        log.debug((Object)"Exit: Admin::AdminService");
        return result;
    }

    protected static Document processWSDD(MessageContext msgContext, AxisEngine engine, Element root) throws Exception {
        Document doc = null;
        String action = root.getLocalName();
        if (action.equals("passwd")) {
            String newPassword = root.getFirstChild().getNodeValue();
            engine.setAdminPassword(newPassword);
            doc = XMLUtils.newDocument();
            root = doc.createElementNS("", "Admin");
            doc.appendChild(root);
            root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
            return doc;
        }
        if (action.equals("quit")) {
            log.error((Object)Messages.getMessage("quitRequest00"));
            if (msgContext != null) {
                msgContext.setProperty("quit.requested", "true");
            }
            doc = XMLUtils.newDocument();
            root = doc.createElementNS("", "Admin");
            doc.appendChild(root);
            root.appendChild(doc.createTextNode(Messages.getMessage("quit00", "")));
            return doc;
        }
        if (action.equals("list")) {
            return Admin.listConfig(engine);
        }
        if (action.equals("clientdeploy")) {
            engine = engine.getClientEngine();
        }
        WSDDDocument wsddDoc = new WSDDDocument(root);
        EngineConfiguration config = engine.getConfig();
        if (config instanceof WSDDEngineConfiguration) {
            WSDDDeployment deployment = ((WSDDEngineConfiguration)config).getDeployment();
            wsddDoc.deploy(deployment);
        }
        engine.refreshGlobalOptions();
        engine.saveConfiguration();
        doc = XMLUtils.newDocument();
        root = doc.createElementNS("", "Admin");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode(Messages.getMessage("done00")));
        return doc;
    }

    public Document process(MessageContext msgContext, Element root) throws Exception {
        this.verifyHostAllowed(msgContext);
        String rootNS = root.getNamespaceURI();
        AxisEngine engine = msgContext.getAxisEngine();
        if (rootNS != null && rootNS.equals("http://xml.apache.org/axis/wsdd/")) {
            return Admin.processWSDD(msgContext, engine, root);
        }
        throw new Exception(Messages.getMessage("adminServiceNoWSDD"));
    }

    private void verifyHostAllowed(MessageContext msgContext) throws AxisFault {
        String remoteIP;
        SOAPService serviceHandler = msgContext.getService();
        if (serviceHandler != null && !JavaUtils.isTrueExplicitly(serviceHandler.getOption("enableRemoteAdmin")) && (remoteIP = msgContext.getStrProp("remoteaddr")) != null && !remoteIP.equals("127.0.0.1")) {
            try {
                InetAddress myAddr = InetAddress.getLocalHost();
                InetAddress remoteAddr = InetAddress.getByName(remoteIP);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Comparing remote caller " + remoteAddr + " to " + myAddr));
                }
                if (!myAddr.equals(remoteAddr)) {
                    log.error((Object)Messages.getMessage("noAdminAccess01", remoteAddr.toString()));
                    throw new AxisFault("Server.Unauthorized", Messages.getMessage("noAdminAccess00"), null, null);
                }
            }
            catch (UnknownHostException e) {
                throw new AxisFault("Server.UnknownHost", Messages.getMessage("unknownHost00"), null, null);
            }
        }
    }

    public static Document listConfig(AxisEngine engine) throws AxisFault {
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContext(writer, null);
        context.setPretty(true);
        try {
            EngineConfiguration config = engine.getConfig();
            if (config instanceof WSDDEngineConfiguration) {
                WSDDDeployment deployment = ((WSDDEngineConfiguration)config).getDeployment();
                deployment.writeToContext(context);
            }
        }
        catch (Exception e) {
            throw new AxisFault(Messages.getMessage("noEngineWSDD"));
        }
        try {
            writer.close();
            return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
        }
        catch (Exception e) {
            log.error((Object)"exception00", (Throwable)e);
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        int i = 0;
        if (args.length < 2 || !args[0].equals("client") && !args[0].equals("server")) {
            log.error((Object)Messages.getMessage("usage00", "Admin client|server <xml-file>"));
            log.error((Object)Messages.getMessage("where00", "<xml-file>"));
            log.error((Object)"<deploy>");
            log.error((Object)"  <handler name=a class=className/>");
            log.error((Object)"  <chain name=a flow=\"a,b,c\" />");
            log.error((Object)"  <chain name=a request=\"a,b,c\" pivot=\"d\"");
            log.error((Object)"                  response=\"e,f,g\" />");
            log.error((Object)"  <service name=a handler=b />");
            log.error((Object)"</deploy>");
            log.error((Object)"<undeploy>");
            log.error((Object)"  <handler name=a/>");
            log.error((Object)"  <chain name=a/>");
            log.error((Object)"  <service name=a/>");
            log.error((Object)"</undeploy>");
            log.error((Object)"<list/>");
            throw new IllegalArgumentException(Messages.getMessage("usage00", "Admin client|server <xml-file>"));
        }
        Admin admin = new Admin();
        AxisEngine engine = args[0].equals("client") ? new AxisClient() : new AxisServer();
        engine.setShouldSaveConfig(true);
        engine.init();
        MessageContext msgContext = new MessageContext(engine);
        try {
            for (i = 1; i < args.length; ++i) {
                Document doc;
                Document result;
                if (log.isDebugEnabled()) {
                    log.debug((Object)Messages.getMessage("process00", args[i]));
                }
                if ((result = admin.process(msgContext, (doc = XMLUtils.newDocument(new FileInputStream(args[i]))).getDocumentElement())) == null) continue;
                System.out.println(XMLUtils.DocumentToString(result));
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("errorProcess00", args[i]), (Throwable)e);
            throw e;
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

