/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 *  com.sun.xml.ws.policy.AssertionSet
 *  com.sun.xml.ws.policy.Policy
 *  com.sun.xml.ws.policy.PolicyAssertion
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  com.sun.xml.ws.policy.SimpleAssertion
 *  com.sun.xml.ws.policy.sourcemodel.AssertionData
 *  com.sun.xml.ws.policy.spi.AssertionCreationException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.config.management.policy;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.SimpleAssertion;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class ManagementAssertion
extends SimpleAssertion {
    protected static final QName MANAGEMENT_ATTRIBUTE_QNAME = new QName("management");
    protected static final QName MONITORING_ATTRIBUTE_QNAME = new QName("monitoring");
    private static final QName ID_ATTRIBUTE_QNAME = new QName("id");
    private static final QName START_ATTRIBUTE_QNAME = new QName("start");
    private static final Logger LOGGER = Logger.getLogger(ManagementAssertion.class);

    protected static <T extends ManagementAssertion> T getAssertion(QName name, PolicyMap policyMap, QName serviceName, QName portName, Class<T> type) throws WebServiceException {
        try {
            AssertionSet assertionSet;
            Iterator assertions;
            Iterator assertionSets;
            PolicyMapKey key;
            Policy policy;
            PolicyAssertion assertion = null;
            if (policyMap != null && (policy = policyMap.getEndpointEffectivePolicy(key = PolicyMap.createWsdlEndpointScopeKey((QName)serviceName, (QName)portName))) != null && (assertionSets = policy.iterator()).hasNext() && (assertions = (assertionSet = (AssertionSet)assertionSets.next()).get(name).iterator()).hasNext()) {
                assertion = (PolicyAssertion)assertions.next();
            }
            return (T)((Object)(assertion == null ? null : (ManagementAssertion)assertion.getImplementation(type)));
        }
        catch (PolicyException ex) {
            throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(ManagementMessages.WSM_1001_FAILED_ASSERTION(name), (Throwable)ex));
        }
    }

    protected ManagementAssertion(QName name, AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(data, assertionParameters);
        if (!name.equals(data.getName())) {
            throw (AssertionCreationException)LOGGER.logSevereException((Throwable)new AssertionCreationException(data, ManagementMessages.WSM_1002_EXPECTED_MANAGEMENT_ASSERTION(name)));
        }
        if (this.isManagementEnabled() && !data.containsAttribute(ID_ATTRIBUTE_QNAME)) {
            throw (AssertionCreationException)LOGGER.logSevereException((Throwable)new AssertionCreationException(data, ManagementMessages.WSM_1003_MANAGEMENT_ASSERTION_MISSING_ID(name)));
        }
    }

    public String getId() {
        return this.getAttributeValue(ID_ATTRIBUTE_QNAME);
    }

    public String getStart() {
        return this.getAttributeValue(START_ATTRIBUTE_QNAME);
    }

    public abstract boolean isManagementEnabled();

    public Setting monitoringAttribute() {
        String monitoring = this.getAttributeValue(MONITORING_ATTRIBUTE_QNAME);
        Setting result = Setting.NOT_SET;
        if (monitoring != null) {
            result = monitoring.trim().toLowerCase().equals("on") || Boolean.parseBoolean(monitoring) ? Setting.ON : Setting.OFF;
        }
        return result;
    }

    public static enum Setting {
        NOT_SET,
        OFF,
        ON;

    }
}

