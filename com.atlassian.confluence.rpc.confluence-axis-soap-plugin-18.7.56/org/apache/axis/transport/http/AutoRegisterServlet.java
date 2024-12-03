/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.transport.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.AxisFault;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.WSDDEngineConfiguration;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDDocument;
import org.apache.axis.i18n.Messages;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.AxisServletBase;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class AutoRegisterServlet
extends AxisServletBase {
    private static Log log = LogFactory.getLog((class$org$apache$axis$transport$http$AutoRegisterServlet == null ? (class$org$apache$axis$transport$http$AutoRegisterServlet = AutoRegisterServlet.class$("org.apache.axis.transport.http.AutoRegisterServlet")) : class$org$apache$axis$transport$http$AutoRegisterServlet).getName());
    static /* synthetic */ Class class$org$apache$axis$transport$http$AutoRegisterServlet;

    public void init() throws ServletException {
        log.debug((Object)Messages.getMessage("autoRegServletInit00"));
        this.autoRegister();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerStream(InputStream instream) throws SAXException, ParserConfigurationException, IOException {
        try {
            Document doc = XMLUtils.newDocument(instream);
            WSDDDocument wsddDoc = new WSDDDocument(doc);
            WSDDDeployment deployment = this.getDeployment();
            if (deployment != null) {
                wsddDoc.deploy(deployment);
            }
        }
        finally {
            instream.close();
        }
    }

    public void registerResource(String resourcename) throws SAXException, ParserConfigurationException, IOException {
        InputStream in = this.getServletContext().getResourceAsStream(resourcename);
        if (in == null) {
            throw new FileNotFoundException(resourcename);
        }
        this.registerStream(in);
    }

    public void registerFile(File file) throws IOException, SAXException, ParserConfigurationException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        this.registerStream(in);
    }

    public String[] getResourcesToRegister() {
        return null;
    }

    private WSDDDeployment getDeployment() throws AxisFault {
        AxisServer engine = this.getEngine();
        EngineConfiguration config = engine.getConfig();
        WSDDDeployment deployment = config instanceof WSDDEngineConfiguration ? ((WSDDEngineConfiguration)config).getDeployment() : null;
        return deployment;
    }

    protected void logSuccess(String item) {
        log.debug((Object)Messages.getMessage("autoRegServletLoaded01", item));
    }

    protected void autoRegister() {
        String[] resources = this.getResourcesToRegister();
        if (resources == null || resources.length == 0) {
            return;
        }
        for (int i = 0; i < resources.length; ++i) {
            String resource = resources[i];
            this.registerAndLogResource(resource);
        }
        this.registerAnythingElse();
        try {
            this.applyAndSaveSettings();
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("autoRegServletApplyAndSaveSettings00"), (Throwable)e);
        }
    }

    protected void registerAnythingElse() {
    }

    public void registerAndLogResource(String resource) {
        try {
            this.registerResource(resource);
            this.logSuccess(resource);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("autoRegServletLoadFailed01", resource), (Throwable)e);
        }
    }

    protected void applyAndSaveSettings() throws AxisFault, ConfigurationException {
        AxisServer engine = this.getEngine();
        engine.refreshGlobalOptions();
        engine.saveConfiguration();
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

