/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.axis.ConfigurationException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDUndeployment;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class WSDDDocument
extends WSDDConstants {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$deployment$wsdd$WSDDDocument == null ? (class$org$apache$axis$deployment$wsdd$WSDDDocument = WSDDDocument.class$("org.apache.axis.deployment.wsdd.WSDDDocument")) : class$org$apache$axis$deployment$wsdd$WSDDDocument).getName());
    private Document doc;
    private WSDDDeployment deployment;
    private WSDDUndeployment undeployment;
    static /* synthetic */ Class class$org$apache$axis$deployment$wsdd$WSDDDocument;

    public WSDDDocument() {
    }

    public WSDDDocument(Document document) throws WSDDException {
        this.setDocument(document);
    }

    public WSDDDocument(Element e) throws WSDDException {
        this.doc = e.getOwnerDocument();
        if ("undeployment".equals(e.getLocalName())) {
            this.undeployment = new WSDDUndeployment(e);
        } else {
            this.deployment = new WSDDDeployment(e);
        }
    }

    public WSDDDeployment getDeployment() {
        if (this.deployment == null) {
            this.deployment = new WSDDDeployment();
        }
        return this.deployment;
    }

    public Document getDOMDocument() throws ConfigurationException {
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContext(writer, null);
        context.setPretty(true);
        try {
            this.deployment.writeToContext(context);
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("exception00"), (Throwable)e);
        }
        try {
            writer.close();
            return XMLUtils.newDocument(new InputSource(new StringReader(writer.getBuffer().toString())));
        }
        catch (Exception e) {
            return null;
        }
    }

    public void writeToContext(SerializationContext context) throws IOException {
        this.getDeployment().writeToContext(context);
    }

    public void setDocument(Document document) throws WSDDException {
        this.doc = document;
        Element docEl = this.doc.getDocumentElement();
        if ("undeployment".equals(docEl.getLocalName())) {
            this.undeployment = new WSDDUndeployment(docEl);
        } else {
            this.deployment = new WSDDDeployment(docEl);
        }
    }

    public void deploy(WSDDDeployment registry) throws ConfigurationException {
        if (this.deployment != null) {
            this.deployment.deployToRegistry(registry);
        }
        if (this.undeployment != null) {
            this.undeployment.undeployFromRegistry(registry);
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

