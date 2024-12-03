/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.sourcemodel.AssertionData
 *  com.sun.xml.ws.policy.spi.AssertionCreationException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.config.management.policy;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.config.management.policy.ManagementAssertion;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.resources.ManagementMessages;
import java.util.Collection;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedClientAssertion
extends ManagementAssertion {
    public static final QName MANAGED_CLIENT_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedClient");
    private static final Logger LOGGER = Logger.getLogger(ManagedClientAssertion.class);

    public static ManagedClientAssertion getAssertion(WSPortInfo portInfo) throws WebServiceException {
        if (portInfo == null) {
            return null;
        }
        LOGGER.entering(new Object[]{portInfo});
        PolicyMap policyMap = portInfo.getPolicyMap();
        ManagedClientAssertion assertion = ManagementAssertion.getAssertion(MANAGED_CLIENT_QNAME, policyMap, portInfo.getServiceName(), portInfo.getPortName(), ManagedClientAssertion.class);
        LOGGER.exiting((Object)assertion);
        return assertion;
    }

    public ManagedClientAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(MANAGED_CLIENT_QNAME, data, assertionParameters);
    }

    @Override
    public boolean isManagementEnabled() {
        String management = this.getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
        if (management != null && (management.trim().toLowerCase().equals("on") || Boolean.parseBoolean(management))) {
            LOGGER.warning(ManagementMessages.WSM_1006_CLIENT_MANAGEMENT_ENABLED());
        }
        return false;
    }
}

