/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.api.policy.subject;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.resources.BindingApiMessages;
import javax.xml.namespace.QName;

public class BindingSubject {
    private static final Logger LOGGER = Logger.getLogger(BindingSubject.class);
    private final QName name;
    private final WsdlMessageType messageType;
    private final WsdlNameScope nameScope;
    private final BindingSubject parent;

    BindingSubject(QName name, WsdlNameScope scope, BindingSubject parent) {
        this(name, WsdlMessageType.NO_MESSAGE, scope, parent);
    }

    BindingSubject(QName name, WsdlMessageType messageType, WsdlNameScope scope, BindingSubject parent) {
        this.name = name;
        this.messageType = messageType;
        this.nameScope = scope;
        this.parent = parent;
    }

    public static BindingSubject createBindingSubject(QName bindingName) {
        return new BindingSubject(bindingName, WsdlNameScope.ENDPOINT, null);
    }

    public static BindingSubject createOperationSubject(QName bindingName, QName operationName) {
        BindingSubject bindingSubject = BindingSubject.createBindingSubject(bindingName);
        return new BindingSubject(operationName, WsdlNameScope.OPERATION, bindingSubject);
    }

    public static BindingSubject createInputMessageSubject(QName bindingName, QName operationName, QName messageName) {
        BindingSubject operationSubject = BindingSubject.createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.INPUT, WsdlNameScope.MESSAGE, operationSubject);
    }

    public static BindingSubject createOutputMessageSubject(QName bindingName, QName operationName, QName messageName) {
        BindingSubject operationSubject = BindingSubject.createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.OUTPUT, WsdlNameScope.MESSAGE, operationSubject);
    }

    public static BindingSubject createFaultMessageSubject(QName bindingName, QName operationName, QName messageName) {
        if (messageName == null) {
            throw (IllegalArgumentException)LOGGER.logSevereException((Throwable)new IllegalArgumentException(BindingApiMessages.BINDING_API_NO_FAULT_MESSAGE_NAME()));
        }
        BindingSubject operationSubject = BindingSubject.createOperationSubject(bindingName, operationName);
        return new BindingSubject(messageName, WsdlMessageType.FAULT, WsdlNameScope.MESSAGE, operationSubject);
    }

    public QName getName() {
        return this.name;
    }

    public BindingSubject getParent() {
        return this.parent;
    }

    public boolean isBindingSubject() {
        if (this.nameScope == WsdlNameScope.ENDPOINT) {
            return this.parent == null;
        }
        return false;
    }

    public boolean isOperationSubject() {
        if (this.nameScope == WsdlNameScope.OPERATION && this.parent != null) {
            return this.parent.isBindingSubject();
        }
        return false;
    }

    public boolean isMessageSubject() {
        if (this.nameScope == WsdlNameScope.MESSAGE && this.parent != null) {
            return this.parent.isOperationSubject();
        }
        return false;
    }

    public boolean isInputMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.INPUT;
    }

    public boolean isOutputMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.OUTPUT;
    }

    public boolean isFaultMessageSubject() {
        return this.isMessageSubject() && this.messageType == WsdlMessageType.FAULT;
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null || !(that instanceof BindingSubject)) {
            return false;
        }
        BindingSubject thatSubject = (BindingSubject)that;
        boolean isEqual = true;
        isEqual = isEqual && (this.name == null ? thatSubject.name == null : this.name.equals(thatSubject.name));
        isEqual = isEqual && this.messageType.equals((Object)thatSubject.messageType);
        boolean bl = isEqual = isEqual && this.nameScope.equals((Object)thatSubject.nameScope);
        isEqual = isEqual && (this.parent == null ? thatSubject.parent == null : this.parent.equals(thatSubject.parent));
        return isEqual;
    }

    public int hashCode() {
        int result = 23;
        result = 29 * result + (this.name == null ? 0 : this.name.hashCode());
        result = 29 * result + this.messageType.hashCode();
        result = 29 * result + this.nameScope.hashCode();
        result = 29 * result + (this.parent == null ? 0 : this.parent.hashCode());
        return result;
    }

    public String toString() {
        StringBuilder result = new StringBuilder("BindingSubject[");
        result.append(this.name).append(", ").append((Object)this.messageType);
        result.append(", ").append((Object)this.nameScope).append(", ").append(this.parent);
        return result.append("]").toString();
    }

    private static enum WsdlNameScope {
        SERVICE,
        ENDPOINT,
        OPERATION,
        MESSAGE;

    }

    private static enum WsdlMessageType {
        NO_MESSAGE,
        INPUT,
        OUTPUT,
        FAULT;

    }
}

