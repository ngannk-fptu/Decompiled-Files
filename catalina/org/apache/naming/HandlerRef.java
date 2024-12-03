/*
 * Decompiled with CFR 0.152.
 */
package org.apache.naming;

import javax.naming.StringRefAddr;
import org.apache.naming.AbstractRef;

public class HandlerRef
extends AbstractRef {
    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.HandlerFactory";
    public static final String HANDLER_NAME = "handlername";
    public static final String HANDLER_CLASS = "handlerclass";
    public static final String HANDLER_LOCALPART = "handlerlocalpart";
    public static final String HANDLER_NAMESPACE = "handlernamespace";
    public static final String HANDLER_PARAMNAME = "handlerparamname";
    public static final String HANDLER_PARAMVALUE = "handlerparamvalue";
    public static final String HANDLER_SOAPROLE = "handlersoaprole";
    public static final String HANDLER_PORTNAME = "handlerportname";

    public HandlerRef(String refname, String handlerClass) {
        this(refname, handlerClass, null, null);
    }

    public HandlerRef(String refname, String handlerClass, String factory, String factoryLocation) {
        super(refname, factory, factoryLocation);
        StringRefAddr refAddr = null;
        if (refname != null) {
            refAddr = new StringRefAddr(HANDLER_NAME, refname);
            this.add(refAddr);
        }
        if (handlerClass != null) {
            refAddr = new StringRefAddr(HANDLER_CLASS, handlerClass);
            this.add(refAddr);
        }
    }

    @Override
    protected String getDefaultFactoryClassName() {
        return DEFAULT_FACTORY;
    }
}

