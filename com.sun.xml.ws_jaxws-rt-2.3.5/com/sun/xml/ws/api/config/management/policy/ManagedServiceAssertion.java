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
import com.sun.xml.ws.api.config.management.policy.ManagementAssertion;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.ws.policy.spi.AssertionCreationException;
import com.sun.xml.ws.resources.ManagementMessages;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class ManagedServiceAssertion
extends ManagementAssertion {
    public static final QName MANAGED_SERVICE_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ManagedService");
    private static final QName COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementations");
    private static final QName COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "CommunicationServerImplementation");
    private static final QName CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfiguratorImplementation");
    private static final QName CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigSaverImplementation");
    private static final QName CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME = new QName("http://java.sun.com/xml/ns/metro/management", "ConfigReaderImplementation");
    private static final QName CLASS_NAME_ATTRIBUTE_QNAME = new QName("className");
    private static final QName ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME = new QName("endpointDisposeDelay");
    private static final Logger LOGGER = Logger.getLogger(ManagedServiceAssertion.class);

    public static ManagedServiceAssertion getAssertion(WSEndpoint endpoint) throws WebServiceException {
        LOGGER.entering(new Object[]{endpoint});
        PolicyMap policyMap = endpoint.getPolicyMap();
        ManagedServiceAssertion assertion = ManagementAssertion.getAssertion(MANAGED_SERVICE_QNAME, policyMap, endpoint.getServiceName(), endpoint.getPortName(), ManagedServiceAssertion.class);
        LOGGER.exiting((Object)assertion);
        return assertion;
    }

    public ManagedServiceAssertion(AssertionData data, Collection<PolicyAssertion> assertionParameters) throws AssertionCreationException {
        super(MANAGED_SERVICE_QNAME, data, assertionParameters);
    }

    @Override
    public boolean isManagementEnabled() {
        String management = this.getAttributeValue(MANAGEMENT_ATTRIBUTE_QNAME);
        boolean result = true;
        if (management != null) {
            result = management.trim().toLowerCase().equals("on") ? true : Boolean.parseBoolean(management);
        }
        return result;
    }

    public long getEndpointDisposeDelay(long defaultDelay) throws WebServiceException {
        long result = defaultDelay;
        String delayText = this.getAttributeValue(ENDPOINT_DISPOSE_DELAY_ATTRIBUTE_QNAME);
        if (delayText != null) {
            try {
                result = Long.parseLong(delayText);
            }
            catch (NumberFormatException e) {
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(ManagementMessages.WSM_1008_EXPECTED_INTEGER_DISPOSE_DELAY_VALUE(delayText), (Throwable)e));
            }
        }
        return result;
    }

    public Collection<ImplementationRecord> getCommunicationServerImplementations() {
        LinkedList<ImplementationRecord> result = new LinkedList<ImplementationRecord>();
        Iterator parameters = this.getParametersIterator();
        while (parameters.hasNext()) {
            PolicyAssertion parameter = (PolicyAssertion)parameters.next();
            if (!COMMUNICATION_SERVER_IMPLEMENTATIONS_PARAMETER_QNAME.equals(parameter.getName())) continue;
            Iterator implementations = parameter.getParametersIterator();
            if (!implementations.hasNext()) {
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(ManagementMessages.WSM_1005_EXPECTED_COMMUNICATION_CHILD()));
            }
            while (implementations.hasNext()) {
                PolicyAssertion implementation = (PolicyAssertion)implementations.next();
                if (COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME.equals(implementation.getName())) {
                    result.add(this.getImplementation(implementation));
                    continue;
                }
                throw (WebServiceException)LOGGER.logSevereException((Throwable)new WebServiceException(ManagementMessages.WSM_1004_EXPECTED_XML_TAG(COMMUNICATION_SERVER_IMPLEMENTATION_PARAMETER_QNAME, implementation.getName())));
            }
        }
        return result;
    }

    public ImplementationRecord getConfiguratorImplementation() {
        return this.findImplementation(CONFIGURATOR_IMPLEMENTATION_PARAMETER_QNAME);
    }

    public ImplementationRecord getConfigSaverImplementation() {
        return this.findImplementation(CONFIG_SAVER_IMPLEMENTATION_PARAMETER_QNAME);
    }

    public ImplementationRecord getConfigReaderImplementation() {
        return this.findImplementation(CONFIG_READER_IMPLEMENTATION_PARAMETER_QNAME);
    }

    private ImplementationRecord findImplementation(QName implementationName) {
        Iterator parameters = this.getParametersIterator();
        while (parameters.hasNext()) {
            PolicyAssertion parameter = (PolicyAssertion)parameters.next();
            if (!implementationName.equals(parameter.getName())) continue;
            return this.getImplementation(parameter);
        }
        return null;
    }

    private ImplementationRecord getImplementation(PolicyAssertion rootParameter) {
        String className = rootParameter.getAttributeValue(CLASS_NAME_ATTRIBUTE_QNAME);
        HashMap<QName, String> parameterMap = new HashMap<QName, String>();
        Iterator implementationParameters = rootParameter.getParametersIterator();
        LinkedList<NestedParameters> nestedParameters = new LinkedList<NestedParameters>();
        while (implementationParameters.hasNext()) {
            PolicyAssertion parameterAssertion = (PolicyAssertion)implementationParameters.next();
            QName parameterName = parameterAssertion.getName();
            if (parameterAssertion.hasParameters()) {
                HashMap<QName, String> nestedParameterMap = new HashMap<QName, String>();
                Iterator parameters = parameterAssertion.getParametersIterator();
                while (parameters.hasNext()) {
                    PolicyAssertion parameter = (PolicyAssertion)parameters.next();
                    String value = parameter.getValue();
                    if (value != null) {
                        value = value.trim();
                    }
                    nestedParameterMap.put(parameter.getName(), value);
                }
                nestedParameters.add(new NestedParameters(parameterName, nestedParameterMap));
                continue;
            }
            String value = parameterAssertion.getValue();
            if (value != null) {
                value = value.trim();
            }
            parameterMap.put(parameterName, value);
        }
        return new ImplementationRecord(className, parameterMap, nestedParameters);
    }

    public static class NestedParameters {
        private final QName name;
        private final Map<QName, String> parameters;

        private NestedParameters(QName name, Map<QName, String> parameters) {
            this.name = name;
            this.parameters = parameters;
        }

        public QName getName() {
            return this.name;
        }

        public Map<QName, String> getParameters() {
            return this.parameters;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            NestedParameters other = (NestedParameters)obj;
            if (this.name == null ? other.name != null : !this.name.equals(other.name)) {
                return false;
            }
            return this.parameters == other.parameters || this.parameters != null && this.parameters.equals(other.parameters);
        }

        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 59 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
            return hash;
        }

        public String toString() {
            StringBuilder text = new StringBuilder("NestedParameters: ");
            text.append("name = \"").append(this.name).append("\", ");
            text.append("parameters = \"").append(this.parameters).append("\"");
            return text.toString();
        }
    }

    public static class ImplementationRecord {
        private final String implementation;
        private final Map<QName, String> parameters;
        private final Collection<NestedParameters> nestedParameters;

        protected ImplementationRecord(String implementation, Map<QName, String> parameters, Collection<NestedParameters> nestedParameters) {
            this.implementation = implementation;
            this.parameters = parameters;
            this.nestedParameters = nestedParameters;
        }

        public String getImplementation() {
            return this.implementation;
        }

        public Map<QName, String> getParameters() {
            return this.parameters;
        }

        public Collection<NestedParameters> getNestedParameters() {
            return this.nestedParameters;
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ImplementationRecord other = (ImplementationRecord)obj;
            if (this.implementation == null ? other.implementation != null : !this.implementation.equals(other.implementation)) {
                return false;
            }
            if (!(this.parameters == other.parameters || this.parameters != null && this.parameters.equals(other.parameters))) {
                return false;
            }
            return this.nestedParameters == other.nestedParameters || this.nestedParameters != null && this.nestedParameters.equals(other.nestedParameters);
        }

        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.implementation != null ? this.implementation.hashCode() : 0);
            hash = 53 * hash + (this.parameters != null ? this.parameters.hashCode() : 0);
            hash = 53 * hash + (this.nestedParameters != null ? this.nestedParameters.hashCode() : 0);
            return hash;
        }

        public String toString() {
            StringBuilder text = new StringBuilder("ImplementationRecord: ");
            text.append("implementation = \"").append(this.implementation).append("\", ");
            text.append("parameters = \"").append(this.parameters).append("\", ");
            text.append("nested parameters = \"").append(this.nestedParameters).append("\"");
            return text.toString();
        }
    }
}

