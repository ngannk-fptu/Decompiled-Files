/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.PolicyException
 *  com.sun.xml.ws.policy.PolicyMap
 *  com.sun.xml.ws.policy.PolicyMapExtender
 *  com.sun.xml.ws.policy.PolicyMapKey
 *  com.sun.xml.ws.policy.PolicySubject
 *  com.sun.xml.ws.policy.sourcemodel.PolicySourceModel
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.policy.PolicyException;
import com.sun.xml.ws.policy.PolicyMap;
import com.sun.xml.ws.policy.PolicyMapExtender;
import com.sun.xml.ws.policy.PolicyMapKey;
import com.sun.xml.ws.policy.PolicySubject;
import com.sun.xml.ws.policy.jaxws.BuilderHandler;
import com.sun.xml.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerMessageScope
extends BuilderHandler {
    private final QName service;
    private final QName port;
    private final QName operation;
    private final QName message;
    private final Scope scope;

    BuilderHandlerMessageScope(Collection<String> policyURIs, Map<String, PolicySourceModel> policyStore, Object policySubject, Scope scope, QName service, QName port, QName operation, QName message) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
        this.port = port;
        this.operation = operation;
        this.scope = scope;
        this.message = message;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderHandlerMessageScope)) {
            return false;
        }
        BuilderHandlerMessageScope that = (BuilderHandlerMessageScope)obj;
        boolean result = true;
        boolean bl = result && (this.policySubject == null ? that.policySubject == null : this.policySubject.equals(that.policySubject)) ? true : (result = false);
        boolean bl2 = result && (this.scope == null ? that.scope == null : this.scope.equals((Object)that.scope)) ? true : (result = false);
        boolean bl3 = result && (this.message == null ? that.message == null : this.message.equals(that.message)) ? true : (result = false);
        if (this.scope != Scope.FaultMessageScope) {
            boolean bl4 = result && (this.service == null ? that.service == null : this.service.equals(that.service)) ? true : (result = false);
            boolean bl5 = result && (this.port == null ? that.port == null : this.port.equals(that.port)) ? true : (result = false);
            result = result && (this.operation == null ? that.operation == null : this.operation.equals(that.operation));
        }
        return result;
    }

    public int hashCode() {
        int hashCode = 19;
        hashCode = 31 * hashCode + (this.policySubject == null ? 0 : this.policySubject.hashCode());
        hashCode = 31 * hashCode + (this.message == null ? 0 : this.message.hashCode());
        hashCode = 31 * hashCode + (this.scope == null ? 0 : this.scope.hashCode());
        if (this.scope != Scope.FaultMessageScope) {
            hashCode = 31 * hashCode + (this.service == null ? 0 : this.service.hashCode());
            hashCode = 31 * hashCode + (this.port == null ? 0 : this.port.hashCode());
            hashCode = 31 * hashCode + (this.operation == null ? 0 : this.operation.hashCode());
        }
        return hashCode;
    }

    @Override
    protected void doPopulate(PolicyMapExtender policyMapExtender) throws PolicyException {
        block4: {
            PolicyMapKey mapKey;
            block5: {
                block3: {
                    mapKey = Scope.FaultMessageScope == this.scope ? PolicyMap.createWsdlFaultMessageScopeKey((QName)this.service, (QName)this.port, (QName)this.operation, (QName)this.message) : PolicyMap.createWsdlMessageScopeKey((QName)this.service, (QName)this.port, (QName)this.operation);
                    if (Scope.InputMessageScope != this.scope) break block3;
                    for (PolicySubject subject : this.getPolicySubjects()) {
                        policyMapExtender.putInputMessageSubject(mapKey, subject);
                    }
                    break block4;
                }
                if (Scope.OutputMessageScope != this.scope) break block5;
                for (PolicySubject subject : this.getPolicySubjects()) {
                    policyMapExtender.putOutputMessageSubject(mapKey, subject);
                }
                break block4;
            }
            if (Scope.FaultMessageScope != this.scope) break block4;
            for (PolicySubject subject : this.getPolicySubjects()) {
                policyMapExtender.putFaultMessageSubject(mapKey, subject);
            }
        }
    }

    static enum Scope {
        InputMessageScope,
        OutputMessageScope,
        FaultMessageScope;

    }
}

