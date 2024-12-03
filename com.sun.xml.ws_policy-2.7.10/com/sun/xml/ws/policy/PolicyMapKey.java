/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.PolicyMapKeyHandler;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public final class PolicyMapKey {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKey.class);
    private final QName service;
    private final QName port;
    private final QName operation;
    private final QName faultMessage;
    private PolicyMapKeyHandler handler;

    PolicyMapKey(QName service, QName port, QName operation, PolicyMapKeyHandler handler) {
        this(service, port, operation, null, handler);
    }

    PolicyMapKey(QName service, QName port, QName operation, QName faultMessage, PolicyMapKeyHandler handler) {
        if (handler == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
        }
        this.service = service;
        this.port = port;
        this.operation = operation;
        this.faultMessage = faultMessage;
        this.handler = handler;
    }

    PolicyMapKey(PolicyMapKey that) {
        this.service = that.service;
        this.port = that.port;
        this.operation = that.operation;
        this.faultMessage = that.faultMessage;
        this.handler = that.handler;
    }

    public QName getOperation() {
        return this.operation;
    }

    public QName getPort() {
        return this.port;
    }

    public QName getService() {
        return this.service;
    }

    void setHandler(PolicyMapKeyHandler handler) {
        if (handler == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET()));
        }
        this.handler = handler;
    }

    public QName getFaultMessage() {
        return this.faultMessage;
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (that instanceof PolicyMapKey) {
            return this.handler.areEqual(this, (PolicyMapKey)that);
        }
        return false;
    }

    public int hashCode() {
        return this.handler.generateHashCode(this);
    }

    public String toString() {
        StringBuffer result = new StringBuffer("PolicyMapKey(");
        result.append(this.service).append(", ").append(this.port).append(", ").append(this.operation).append(", ").append(this.faultMessage);
        return result.append(")").toString();
    }
}

