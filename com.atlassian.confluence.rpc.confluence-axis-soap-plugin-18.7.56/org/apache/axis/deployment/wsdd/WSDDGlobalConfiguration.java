/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.deployment.wsdd.WSDDConstants;
import org.apache.axis.deployment.wsdd.WSDDDeployableItem;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDFaultFlow;
import org.apache.axis.deployment.wsdd.WSDDRequestFlow;
import org.apache.axis.deployment.wsdd.WSDDResponseFlow;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

public class WSDDGlobalConfiguration
extends WSDDDeployableItem {
    private WSDDRequestFlow requestFlow;
    private WSDDResponseFlow responseFlow;
    private ArrayList roles = new ArrayList();

    public WSDDGlobalConfiguration() {
    }

    public WSDDGlobalConfiguration(Element e) throws WSDDException {
        super(e);
        Element respEl;
        Element reqEl = this.getChildElement(e, "requestFlow");
        if (reqEl != null && reqEl.getElementsByTagName("*").getLength() > 0) {
            this.requestFlow = new WSDDRequestFlow(reqEl);
        }
        if ((respEl = this.getChildElement(e, "responseFlow")) != null && respEl.getElementsByTagName("*").getLength() > 0) {
            this.responseFlow = new WSDDResponseFlow(respEl);
        }
        Element[] roleElements = this.getChildElements(e, "role");
        for (int i = 0; i < roleElements.length; ++i) {
            String role = XMLUtils.getChildCharacterData(roleElements[i]);
            this.roles.add(role);
        }
    }

    protected QName getElementName() {
        return WSDDConstants.QNAME_GLOBAL;
    }

    public WSDDRequestFlow getRequestFlow() {
        return this.requestFlow;
    }

    public void setRequestFlow(WSDDRequestFlow reqFlow) {
        this.requestFlow = reqFlow;
    }

    public WSDDResponseFlow getResponseFlow() {
        return this.responseFlow;
    }

    public void setResponseFlow(WSDDResponseFlow responseFlow) {
        this.responseFlow = responseFlow;
    }

    public WSDDFaultFlow[] getFaultFlows() {
        return null;
    }

    public WSDDFaultFlow getFaultFlow(QName name) {
        WSDDFaultFlow[] t = this.getFaultFlows();
        for (int n = 0; n < t.length; ++n) {
            if (!t[n].getQName().equals(name)) continue;
            return t[n];
        }
        return null;
    }

    public QName getType() {
        return null;
    }

    public void setType(String type) throws WSDDException {
        throw new WSDDException(Messages.getMessage("noTypeOnGlobalConfig00"));
    }

    public Handler makeNewInstance(EngineConfiguration registry) {
        return null;
    }

    public void writeToContext(SerializationContext context) throws IOException {
        context.startElement(QNAME_GLOBAL, null);
        this.writeParamsToContext(context);
        if (this.requestFlow != null) {
            this.requestFlow.writeToContext(context);
        }
        if (this.responseFlow != null) {
            this.responseFlow.writeToContext(context);
        }
        context.endElement();
    }

    public void deployToRegistry(WSDDDeployment registry) throws ConfigurationException {
        if (this.requestFlow != null) {
            this.requestFlow.deployToRegistry(registry);
        }
        if (this.responseFlow != null) {
            this.responseFlow.deployToRegistry(registry);
        }
    }

    public List getRoles() {
        return (List)this.roles.clone();
    }
}

