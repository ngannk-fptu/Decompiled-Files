/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.deployment.wsdd;

import java.io.IOException;
import javax.xml.namespace.QName;
import org.apache.axis.ConfigurationException;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.Handler;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.deployment.wsdd.WSDDDeployableItem;
import org.apache.axis.deployment.wsdd.WSDDDeployment;
import org.apache.axis.deployment.wsdd.WSDDException;
import org.apache.axis.deployment.wsdd.WSDDFaultFlow;
import org.apache.axis.deployment.wsdd.WSDDRequestFlow;
import org.apache.axis.deployment.wsdd.WSDDResponseFlow;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Element;

public abstract class WSDDTargetedChain
extends WSDDDeployableItem {
    private WSDDRequestFlow requestFlow;
    private WSDDResponseFlow responseFlow;
    private QName pivotQName;

    protected WSDDTargetedChain() {
    }

    protected WSDDTargetedChain(Element e) throws WSDDException {
        super(e);
        String pivotStr;
        Element respEl;
        Element reqEl = this.getChildElement(e, "requestFlow");
        if (reqEl != null && reqEl.getElementsByTagName("*").getLength() > 0) {
            this.requestFlow = new WSDDRequestFlow(reqEl);
        }
        if ((respEl = this.getChildElement(e, "responseFlow")) != null && respEl.getElementsByTagName("*").getLength() > 0) {
            this.responseFlow = new WSDDResponseFlow(respEl);
        }
        if ((pivotStr = e.getAttribute("pivot")) != null && !pivotStr.equals("")) {
            this.pivotQName = XMLUtils.getQNameFromString(pivotStr, e);
        }
    }

    public WSDDRequestFlow getRequestFlow() {
        return this.requestFlow;
    }

    public void setRequestFlow(WSDDRequestFlow flow) {
        this.requestFlow = flow;
    }

    public WSDDResponseFlow getResponseFlow() {
        return this.responseFlow;
    }

    public void setResponseFlow(WSDDResponseFlow flow) {
        this.responseFlow = flow;
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

    public void setType(String type) throws WSDDException {
        throw new WSDDException(Messages.getMessage("noTypeSetting", this.getElementName().getLocalPart()));
    }

    public QName getPivotQName() {
        return this.pivotQName;
    }

    public void setPivotQName(QName pivotQName) {
        this.pivotQName = pivotQName;
    }

    public Handler makeNewInstance(EngineConfiguration registry) throws ConfigurationException {
        Handler reqHandler = null;
        WSDDRequestFlow req = this.getRequestFlow();
        if (req != null) {
            reqHandler = req.getInstance(registry);
        }
        Handler pivot = null;
        if (this.pivotQName != null) {
            if ("http://xml.apache.org/axis/wsdd/providers/java".equals(this.pivotQName.getNamespaceURI())) {
                try {
                    pivot = (Handler)ClassUtils.forName(this.pivotQName.getLocalPart()).newInstance();
                }
                catch (InstantiationException e) {
                    throw new ConfigurationException(e);
                }
                catch (IllegalAccessException e) {
                    throw new ConfigurationException(e);
                }
                catch (ClassNotFoundException e) {
                    throw new ConfigurationException(e);
                }
            } else {
                pivot = registry.getHandler(this.pivotQName);
            }
        }
        Handler respHandler = null;
        WSDDResponseFlow resp = this.getResponseFlow();
        if (resp != null) {
            respHandler = resp.getInstance(registry);
        }
        SimpleTargetedChain retVal = new SimpleTargetedChain(reqHandler, pivot, respHandler);
        retVal.setOptions(this.getParametersTable());
        return retVal;
    }

    public final void writeFlowsToContext(SerializationContext context) throws IOException {
        if (this.requestFlow != null) {
            this.requestFlow.writeToContext(context);
        }
        if (this.responseFlow != null) {
            this.responseFlow.writeToContext(context);
        }
    }

    public void deployToRegistry(WSDDDeployment registry) {
        if (this.requestFlow != null) {
            this.requestFlow.deployToRegistry(registry);
        }
        if (this.responseFlow != null) {
            this.responseFlow.deployToRegistry(registry);
        }
    }
}

