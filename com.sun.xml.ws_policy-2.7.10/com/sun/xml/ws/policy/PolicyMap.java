/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.Policy;
import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicyMapKeyHandler;
import com.sun.xml.ws.policy.PolicyMapMutator;
import com.sun.xml.ws.policy.PolicyMerger;
import com.sun.xml.ws.policy.PolicyScope;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class PolicyMap
implements Iterable<Policy> {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMap.class);
    private static final PolicyMapKeyHandler serviceKeyHandler = new PolicyMapKeyHandler(){

        @Override
        public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
            return key1.getService().equals(key2.getService());
        }

        @Override
        public int generateHashCode(PolicyMapKey key) {
            int result = 17;
            result = 37 * result + key.getService().hashCode();
            return result;
        }
    };
    private static final PolicyMapKeyHandler endpointKeyHandler = new PolicyMapKeyHandler(){

        @Override
        public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
            boolean retVal = true;
            boolean bl = retVal = retVal && key1.getService().equals(key2.getService());
            retVal = retVal && (key1.getPort() == null ? key2.getPort() == null : key1.getPort().equals(key2.getPort()));
            return retVal;
        }

        @Override
        public int generateHashCode(PolicyMapKey key) {
            int result = 17;
            result = 37 * result + key.getService().hashCode();
            result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
            return result;
        }
    };
    private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler(){

        @Override
        public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
            boolean retVal = true;
            boolean bl = retVal = retVal && key1.getService().equals(key2.getService());
            boolean bl2 = retVal && (key1.getPort() == null ? key2.getPort() == null : key1.getPort().equals(key2.getPort())) ? true : (retVal = false);
            retVal = retVal && (key1.getOperation() == null ? key2.getOperation() == null : key1.getOperation().equals(key2.getOperation()));
            return retVal;
        }

        @Override
        public int generateHashCode(PolicyMapKey key) {
            int result = 17;
            result = 37 * result + key.getService().hashCode();
            result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
            result = 37 * result + (key.getOperation() == null ? 0 : key.getOperation().hashCode());
            return result;
        }
    };
    private static final PolicyMapKeyHandler faultMessageHandler = new PolicyMapKeyHandler(){

        @Override
        public boolean areEqual(PolicyMapKey key1, PolicyMapKey key2) {
            boolean retVal = true;
            boolean bl = retVal = retVal && key1.getService().equals(key2.getService());
            boolean bl2 = retVal && (key1.getPort() == null ? key2.getPort() == null : key1.getPort().equals(key2.getPort())) ? true : (retVal = false);
            boolean bl3 = retVal && (key1.getOperation() == null ? key2.getOperation() == null : key1.getOperation().equals(key2.getOperation())) ? true : (retVal = false);
            retVal = retVal && (key1.getFaultMessage() == null ? key2.getFaultMessage() == null : key1.getFaultMessage().equals(key2.getFaultMessage()));
            return retVal;
        }

        @Override
        public int generateHashCode(PolicyMapKey key) {
            int result = 17;
            result = 37 * result + key.getService().hashCode();
            result = 37 * result + (key.getPort() == null ? 0 : key.getPort().hashCode());
            result = 37 * result + (key.getOperation() == null ? 0 : key.getOperation().hashCode());
            result = 37 * result + (key.getFaultMessage() == null ? 0 : key.getFaultMessage().hashCode());
            return result;
        }
    };
    private static final PolicyMerger merger = PolicyMerger.getMerger();
    private final ScopeMap serviceMap = new ScopeMap(merger, serviceKeyHandler);
    private final ScopeMap endpointMap = new ScopeMap(merger, endpointKeyHandler);
    private final ScopeMap operationMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
    private final ScopeMap inputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
    private final ScopeMap outputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
    private final ScopeMap faultMessageMap = new ScopeMap(merger, faultMessageHandler);

    private PolicyMap() {
    }

    public static PolicyMap createPolicyMap(Collection<? extends PolicyMapMutator> mutators) {
        PolicyMap result = new PolicyMap();
        if (mutators != null && !mutators.isEmpty()) {
            for (PolicyMapMutator policyMapMutator : mutators) {
                policyMapMutator.connect(result);
            }
        }
        return result;
    }

    public Policy getServiceEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.serviceMap.getEffectivePolicy(key);
    }

    public Policy getEndpointEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.endpointMap.getEffectivePolicy(key);
    }

    public Policy getOperationEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.operationMap.getEffectivePolicy(key);
    }

    public Policy getInputMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.inputMessageMap.getEffectivePolicy(key);
    }

    public Policy getOutputMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.outputMessageMap.getEffectivePolicy(key);
    }

    public Policy getFaultMessageEffectivePolicy(PolicyMapKey key) throws PolicyException {
        return this.faultMessageMap.getEffectivePolicy(key);
    }

    public Collection<PolicyMapKey> getAllServiceScopeKeys() {
        return this.serviceMap.getAllKeys();
    }

    public Collection<PolicyMapKey> getAllEndpointScopeKeys() {
        return this.endpointMap.getAllKeys();
    }

    public Collection<PolicyMapKey> getAllOperationScopeKeys() {
        return this.operationMap.getAllKeys();
    }

    public Collection<PolicyMapKey> getAllInputMessageScopeKeys() {
        return this.inputMessageMap.getAllKeys();
    }

    public Collection<PolicyMapKey> getAllOutputMessageScopeKeys() {
        return this.outputMessageMap.getAllKeys();
    }

    public Collection<PolicyMapKey> getAllFaultMessageScopeKeys() {
        return this.faultMessageMap.getAllKeys();
    }

    void putSubject(ScopeType scopeType, PolicyMapKey key, PolicySubject subject) {
        switch (scopeType) {
            case SERVICE: {
                this.serviceMap.putSubject(key, subject);
                break;
            }
            case ENDPOINT: {
                this.endpointMap.putSubject(key, subject);
                break;
            }
            case OPERATION: {
                this.operationMap.putSubject(key, subject);
                break;
            }
            case INPUT_MESSAGE: {
                this.inputMessageMap.putSubject(key, subject);
                break;
            }
            case OUTPUT_MESSAGE: {
                this.outputMessageMap.putSubject(key, subject);
                break;
            }
            case FAULT_MESSAGE: {
                this.faultMessageMap.putSubject(key, subject);
                break;
            }
            default: {
                throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE((Object)scopeType)));
            }
        }
    }

    void setNewEffectivePolicyForScope(ScopeType scopeType, PolicyMapKey key, Policy newEffectivePolicy) throws IllegalArgumentException {
        if (scopeType == null || key == null || newEffectivePolicy == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
        }
        switch (scopeType) {
            case SERVICE: {
                this.serviceMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case ENDPOINT: {
                this.endpointMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case OPERATION: {
                this.operationMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case INPUT_MESSAGE: {
                this.inputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case OUTPUT_MESSAGE: {
                this.outputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case FAULT_MESSAGE: {
                this.faultMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            default: {
                throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE((Object)scopeType)));
            }
        }
    }

    public Collection<PolicySubject> getPolicySubjects() {
        LinkedList<PolicySubject> subjects = new LinkedList<PolicySubject>();
        this.addSubjects(subjects, this.serviceMap);
        this.addSubjects(subjects, this.endpointMap);
        this.addSubjects(subjects, this.operationMap);
        this.addSubjects(subjects, this.inputMessageMap);
        this.addSubjects(subjects, this.outputMessageMap);
        this.addSubjects(subjects, this.faultMessageMap);
        return subjects;
    }

    public boolean isInputMessageSubject(PolicySubject subject) {
        for (PolicyScope scope : this.inputMessageMap.getStoredScopes()) {
            if (!scope.getPolicySubjects().contains(subject)) continue;
            return true;
        }
        return false;
    }

    public boolean isOutputMessageSubject(PolicySubject subject) {
        for (PolicyScope scope : this.outputMessageMap.getStoredScopes()) {
            if (!scope.getPolicySubjects().contains(subject)) continue;
            return true;
        }
        return false;
    }

    public boolean isFaultMessageSubject(PolicySubject subject) {
        for (PolicyScope scope : this.faultMessageMap.getStoredScopes()) {
            if (!scope.getPolicySubjects().contains(subject)) continue;
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.serviceMap.isEmpty() && this.endpointMap.isEmpty() && this.operationMap.isEmpty() && this.inputMessageMap.isEmpty() && this.outputMessageMap.isEmpty() && this.faultMessageMap.isEmpty();
    }

    private void addSubjects(Collection<PolicySubject> subjects, ScopeMap scopeMap) {
        for (PolicyScope scope : scopeMap.getStoredScopes()) {
            Collection<PolicySubject> scopedSubjects = scope.getPolicySubjects();
            subjects.addAll(scopedSubjects);
        }
    }

    public static PolicyMapKey createWsdlServiceScopeKey(QName service) throws IllegalArgumentException {
        if (service == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL()));
        }
        return new PolicyMapKey(service, null, null, serviceKeyHandler);
    }

    public static PolicyMapKey createWsdlEndpointScopeKey(QName service, QName port) throws IllegalArgumentException {
        if (service == null || port == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(service, port)));
        }
        return new PolicyMapKey(service, port, null, endpointKeyHandler);
    }

    public static PolicyMapKey createWsdlOperationScopeKey(QName service, QName port, QName operation) throws IllegalArgumentException {
        return PolicyMap.createOperationOrInputOutputMessageKey(service, port, operation);
    }

    public static PolicyMapKey createWsdlMessageScopeKey(QName service, QName port, QName operation) throws IllegalArgumentException {
        return PolicyMap.createOperationOrInputOutputMessageKey(service, port, operation);
    }

    public static PolicyMapKey createWsdlFaultMessageScopeKey(QName service, QName port, QName operation, QName fault) throws IllegalArgumentException {
        if (service == null || port == null || operation == null || fault == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(service, port, operation, fault)));
        }
        return new PolicyMapKey(service, port, operation, fault, faultMessageHandler);
    }

    private static PolicyMapKey createOperationOrInputOutputMessageKey(QName service, QName port, QName operation) {
        if (service == null || port == null || operation == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(service, port, operation)));
        }
        return new PolicyMapKey(service, port, operation, operationAndInputOutputMessageKeyHandler);
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        if (null != this.serviceMap) {
            result.append("\nServiceMap=").append(this.serviceMap);
        }
        if (null != this.endpointMap) {
            result.append("\nEndpointMap=").append(this.endpointMap);
        }
        if (null != this.operationMap) {
            result.append("\nOperationMap=").append(this.operationMap);
        }
        if (null != this.inputMessageMap) {
            result.append("\nInputMessageMap=").append(this.inputMessageMap);
        }
        if (null != this.outputMessageMap) {
            result.append("\nOutputMessageMap=").append(this.outputMessageMap);
        }
        if (null != this.faultMessageMap) {
            result.append("\nFaultMessageMap=").append(this.faultMessageMap);
        }
        return result.toString();
    }

    @Override
    public Iterator<Policy> iterator() {
        return new Iterator<Policy>(){
            private final Iterator<Iterator<Policy>> mainIterator;
            private Iterator<Policy> currentScopeIterator;
            {
                ArrayList<Iterator<Policy>> scopeIterators = new ArrayList<Iterator<Policy>>(6);
                scopeIterators.add(PolicyMap.this.serviceMap.iterator());
                scopeIterators.add(PolicyMap.this.endpointMap.iterator());
                scopeIterators.add(PolicyMap.this.operationMap.iterator());
                scopeIterators.add(PolicyMap.this.inputMessageMap.iterator());
                scopeIterators.add(PolicyMap.this.outputMessageMap.iterator());
                scopeIterators.add(PolicyMap.this.faultMessageMap.iterator());
                this.mainIterator = scopeIterators.iterator();
                this.currentScopeIterator = this.mainIterator.next();
            }

            @Override
            public boolean hasNext() {
                while (!this.currentScopeIterator.hasNext()) {
                    if (this.mainIterator.hasNext()) {
                        this.currentScopeIterator = this.mainIterator.next();
                        continue;
                    }
                    return false;
                }
                return true;
            }

            @Override
            public Policy next() {
                if (this.hasNext()) {
                    return this.currentScopeIterator.next();
                }
                throw (NoSuchElementException)LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP()));
            }

            @Override
            public void remove() {
                throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
            }
        };
    }

    private static final class ScopeMap
    implements Iterable<Policy> {
        private final Map<PolicyMapKey, PolicyScope> internalMap = new HashMap<PolicyMapKey, PolicyScope>();
        private final PolicyMapKeyHandler scopeKeyHandler;
        private final PolicyMerger merger;

        ScopeMap(PolicyMerger merger, PolicyMapKeyHandler scopeKeyHandler) {
            this.merger = merger;
            this.scopeKeyHandler = scopeKeyHandler;
        }

        Policy getEffectivePolicy(PolicyMapKey key) throws PolicyException {
            PolicyScope scope = this.internalMap.get(this.createLocalCopy(key));
            return scope == null ? null : scope.getEffectivePolicy(this.merger);
        }

        void putSubject(PolicyMapKey key, PolicySubject subject) {
            PolicyMapKey localKey = this.createLocalCopy(key);
            PolicyScope scope = this.internalMap.get(localKey);
            if (scope == null) {
                LinkedList<PolicySubject> list = new LinkedList<PolicySubject>();
                list.add(subject);
                this.internalMap.put(localKey, new PolicyScope(list));
            } else {
                scope.attach(subject);
            }
        }

        void setNewEffectivePolicy(PolicyMapKey key, Policy newEffectivePolicy) {
            PolicySubject subject = new PolicySubject((Object)key, newEffectivePolicy);
            PolicyMapKey localKey = this.createLocalCopy(key);
            PolicyScope scope = this.internalMap.get(localKey);
            if (scope == null) {
                LinkedList<PolicySubject> list = new LinkedList<PolicySubject>();
                list.add(subject);
                this.internalMap.put(localKey, new PolicyScope(list));
            } else {
                scope.dettachAllSubjects();
                scope.attach(subject);
            }
        }

        Collection<PolicyScope> getStoredScopes() {
            return this.internalMap.values();
        }

        Set<PolicyMapKey> getAllKeys() {
            return this.internalMap.keySet();
        }

        private PolicyMapKey createLocalCopy(PolicyMapKey key) {
            if (key == null) {
                throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL()));
            }
            PolicyMapKey localKeyCopy = new PolicyMapKey(key);
            localKeyCopy.setHandler(this.scopeKeyHandler);
            return localKeyCopy;
        }

        @Override
        public Iterator<Policy> iterator() {
            return new Iterator<Policy>(){
                private final Iterator<PolicyMapKey> keysIterator;
                {
                    this.keysIterator = internalMap.keySet().iterator();
                }

                @Override
                public boolean hasNext() {
                    return this.keysIterator.hasNext();
                }

                @Override
                public Policy next() {
                    PolicyMapKey key = this.keysIterator.next();
                    try {
                        return this.getEffectivePolicy(key);
                    }
                    catch (PolicyException e) {
                        throw (IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(key), e));
                    }
                }

                @Override
                public void remove() {
                    throw (UnsupportedOperationException)LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
                }
            };
        }

        public boolean isEmpty() {
            return this.internalMap.isEmpty();
        }

        public String toString() {
            return this.internalMap.toString();
        }
    }

    static enum ScopeType {
        SERVICE,
        ENDPOINT,
        OPERATION,
        INPUT_MESSAGE,
        OUTPUT_MESSAGE,
        FAULT_MESSAGE;

    }
}

