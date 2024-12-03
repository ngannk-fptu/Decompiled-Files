/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy.subject;

import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class WsdlBindingSubject {
    private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
    private final QName name;
    private final WsdlMessageType messageType;
    private final WsdlNameScope nameScope;
    private final WsdlBindingSubject parent;

    WsdlBindingSubject(QName name, WsdlNameScope scope, WsdlBindingSubject parent) {
        this(name, WsdlMessageType.NO_MESSAGE, scope, parent);
    }

    WsdlBindingSubject(QName name, WsdlMessageType messageType, WsdlNameScope scope, WsdlBindingSubject parent) {
        this.name = name;
        this.messageType = messageType;
        this.nameScope = scope;
        this.parent = parent;
    }

    public static WsdlBindingSubject createBindingSubject(QName bindingName) {
        return new WsdlBindingSubject(bindingName, WsdlNameScope.ENDPOINT, null);
    }

    public static WsdlBindingSubject createBindingOperationSubject(QName bindingName, QName operationName) {
        WsdlBindingSubject bindingSubject = WsdlBindingSubject.createBindingSubject(bindingName);
        return new WsdlBindingSubject(operationName, WsdlNameScope.OPERATION, bindingSubject);
    }

    public static WsdlBindingSubject createBindingMessageSubject(QName bindingName, QName operationName, QName messageName, WsdlMessageType messageType) {
        if (messageType == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL()));
        }
        if (messageType == WsdlMessageType.NO_MESSAGE) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE()));
        }
        if (messageType == WsdlMessageType.FAULT && messageName == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME()));
        }
        WsdlBindingSubject operationSubject = WsdlBindingSubject.createBindingOperationSubject(bindingName, operationName);
        return new WsdlBindingSubject(messageName, messageType, WsdlNameScope.MESSAGE, operationSubject);
    }

    public QName getName() {
        return this.name;
    }

    public WsdlMessageType getMessageType() {
        return this.messageType;
    }

    public WsdlBindingSubject getParent() {
        return this.parent;
    }

    public boolean isBindingSubject() {
        if (this.nameScope == WsdlNameScope.ENDPOINT) {
            return this.parent == null;
        }
        return false;
    }

    public boolean isBindingOperationSubject() {
        if (this.nameScope == WsdlNameScope.OPERATION && this.parent != null) {
            return this.parent.isBindingSubject();
        }
        return false;
    }

    public boolean isBindingMessageSubject() {
        if (this.nameScope == WsdlNameScope.MESSAGE && this.parent != null) {
            return this.parent.isBindingOperationSubject();
        }
        return false;
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || !(that instanceof WsdlBindingSubject)) {
            return false;
        }
        WsdlBindingSubject thatSubject = (WsdlBindingSubject)that;
        boolean isEqual = true;
        isEqual = isEqual && (this.name == null ? thatSubject.name == null : this.name.equals(thatSubject.name));
        isEqual = isEqual && this.messageType.equals((Object)thatSubject.messageType);
        boolean bl = isEqual = isEqual && this.nameScope.equals((Object)thatSubject.nameScope);
        isEqual = isEqual && (this.parent == null ? thatSubject.parent == null : this.parent.equals(thatSubject.parent));
        return isEqual;
    }

    public int hashCode() {
        int result = 23;
        result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 31 * result + this.messageType.hashCode();
        result = 31 * result + this.nameScope.hashCode();
        result = 31 * result + (this.parent == null ? 0 : this.parent.hashCode());
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("WsdlBindingSubject[");
        result.append(this.name).append(", ").append((Object)this.messageType);
        result.append(", ").append((Object)this.nameScope).append(", ").append(this.parent);
        return result.append("]").toString();
    }

    public static enum WsdlNameScope {
        SERVICE,
        ENDPOINT,
        OPERATION,
        MESSAGE;

    }

    public static enum WsdlMessageType {
        NO_MESSAGE,
        INPUT,
        OUTPUT,
        FAULT;

    }
}

