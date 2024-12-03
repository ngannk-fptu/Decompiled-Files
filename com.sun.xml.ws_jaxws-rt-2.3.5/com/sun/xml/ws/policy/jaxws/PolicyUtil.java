/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  com.sun.xml.ws.policy.privateutil.PolicyLogger
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.addressing.policy.AddressingFeatureConfigurator;
import com.sun.xml.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.api.model.wsdl.WSDLService;
import com.sun.xml.ws.encoding.policy.FastInfosetFeatureConfigurator;
import com.sun.xml.ws.encoding.policy.MtomFeatureConfigurator;
import com.sun.xml.ws.encoding.policy.SelectOptimalEncodingFeatureConfigurator;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.jaxws.spi.PolicyFeatureConfigurator;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.ws.util.ServiceFinder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class PolicyUtil {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyUtil.class);
    private static final Collection<PolicyFeatureConfigurator> CONFIGURATORS = new LinkedList<PolicyFeatureConfigurator>();

    public static <T> void addServiceProviders(Collection<T> providers, Class<T> service) {
        Iterator<T> foundProviders = ServiceFinder.find(service).iterator();
        while (foundProviders.hasNext()) {
            providers.add(foundProviders.next());
        }
    }

    public static void configureModel(WSDLModel model, PolicyMap policyMap) throws PolicyException {
        LOGGER.entering(new Object[]{model, policyMap});
        for (WSDLService wSDLService : model.getServices().values()) {
            for (WSDLPort wSDLPort : wSDLService.getPorts()) {
                Collection<WebServiceFeature> features = PolicyUtil.getPortScopedFeatures(policyMap, wSDLService.getName(), wSDLPort.getName());
                for (WebServiceFeature feature : features) {
                    wSDLPort.addFeature(feature);
                    wSDLPort.getBinding().addFeature(feature);
                }
            }
        }
        LOGGER.exiting();
    }

    public static Collection<WebServiceFeature> getPortScopedFeatures(PolicyMap policyMap, QName serviceName, QName portName) {
        LOGGER.entering(new Object[]{policyMap, serviceName, portName});
        ArrayList<WebServiceFeature> features = new ArrayList<WebServiceFeature>();
        try {
            PolicyMapKey key = PolicyMap.createWsdlEndpointScopeKey((QName)serviceName, (QName)portName);
            for (PolicyFeatureConfigurator configurator : CONFIGURATORS) {
                Collection<WebServiceFeature> additionalFeatures = configurator.getFeatures(key, policyMap);
                if (additionalFeatures == null) continue;
                features.addAll(additionalFeatures);
            }
        }
        catch (PolicyException e) {
            throw new WebServiceException((Throwable)e);
        }
        LOGGER.exiting(features);
        return features;
    }

    static {
        CONFIGURATORS.add(new AddressingFeatureConfigurator());
        CONFIGURATORS.add(new MtomFeatureConfigurator());
        CONFIGURATORS.add(new FastInfosetFeatureConfigurator());
        CONFIGURATORS.add(new SelectOptimalEncodingFeatureConfigurator());
        PolicyUtil.addServiceProviders(CONFIGURATORS, PolicyFeatureConfigurator.class);
    }
}

