/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.ws.policy.PolicyMap
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.client;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.BindingID;
import com.sun.xml.ws.api.EndpointAddress;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.policy.PolicyResolver;
import com.sun.xml.ws.api.policy.PolicyResolverFactory;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.jaxws.PolicyUtil;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceFeature;

public class PortInfo
implements WSPortInfo {
    @NotNull
    private final WSServiceDelegate owner;
    @NotNull
    public final QName portName;
    @NotNull
    public final EndpointAddress targetEndpoint;
    @NotNull
    public final BindingID bindingId;
    @NotNull
    public final PolicyMap policyMap;
    @Nullable
    public final WSDLPort portModel;

    public PortInfo(WSServiceDelegate owner, EndpointAddress targetEndpoint, QName name, BindingID bindingId) {
        this.owner = owner;
        this.targetEndpoint = targetEndpoint;
        this.portName = name;
        this.bindingId = bindingId;
        this.portModel = this.getPortModel(owner, name);
        this.policyMap = this.createPolicyMap();
    }

    public PortInfo(@NotNull WSServiceDelegate owner, @NotNull WSDLPort port) {
        this.owner = owner;
        this.targetEndpoint = port.getAddress();
        this.portName = port.getName();
        this.bindingId = port.getBinding().getBindingId();
        this.portModel = port;
        this.policyMap = this.createPolicyMap();
    }

    @Override
    public PolicyMap getPolicyMap() {
        return this.policyMap;
    }

    public PolicyMap createPolicyMap() {
        PolicyMap map = this.portModel != null ? this.portModel.getOwner().getParent().getPolicyMap() : PolicyResolverFactory.create().resolve(new PolicyResolver.ClientContext(null, this.owner.getContainer()));
        if (map == null) {
            map = PolicyMap.createPolicyMap(null);
        }
        return map;
    }

    public BindingImpl createBinding(WebServiceFeature[] webServiceFeatures, Class<?> portInterface) {
        return this.createBinding(new WebServiceFeatureList(webServiceFeatures), portInterface, null);
    }

    public BindingImpl createBinding(WebServiceFeatureList webServiceFeatures, Class<?> portInterface, BindingImpl existingBinding) {
        if (existingBinding != null) {
            webServiceFeatures.addAll(existingBinding.getFeatures());
        }
        Iterable<WebServiceFeature> configFeatures = this.portModel != null ? this.portModel.getFeatures() : PolicyUtil.getPortScopedFeatures(this.policyMap, this.owner.getServiceName(), this.portName);
        webServiceFeatures.mergeFeatures(configFeatures, false);
        webServiceFeatures.mergeFeatures(this.owner.serviceInterceptor.preCreateBinding(this, portInterface, webServiceFeatures), false);
        BindingImpl bindingImpl = BindingImpl.create(this.bindingId, webServiceFeatures.toArray());
        this.owner.getHandlerConfigurator().configureHandlers(this, bindingImpl);
        return bindingImpl;
    }

    private WSDLPort getPortModel(WSServiceDelegate owner, QName portName) {
        if (owner.getWsdlService() != null) {
            Iterable<? extends WSDLPort> ports = owner.getWsdlService().getPorts();
            for (WSDLPort wSDLPort : ports) {
                if (!wSDLPort.getName().equals(portName)) continue;
                return wSDLPort;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public WSDLPort getPort() {
        return this.portModel;
    }

    @Override
    @NotNull
    public WSService getOwner() {
        return this.owner;
    }

    @Override
    @NotNull
    public BindingID getBindingId() {
        return this.bindingId;
    }

    @Override
    @NotNull
    public EndpointAddress getEndpointAddress() {
        return this.targetEndpoint;
    }

    public QName getServiceName() {
        return this.owner.getServiceName();
    }

    public QName getPortName() {
        return this.portName;
    }

    public String getBindingID() {
        return this.bindingId.toString();
    }
}

