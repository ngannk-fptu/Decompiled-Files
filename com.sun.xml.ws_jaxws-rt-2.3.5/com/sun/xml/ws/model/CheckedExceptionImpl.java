/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 */
package com.sun.xml.ws.model;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.addressing.WsaActionUtil;
import com.sun.xml.ws.api.model.CheckedException;
import com.sun.xml.ws.api.model.ExceptionType;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.lang.reflect.Method;

public final class CheckedExceptionImpl
implements CheckedException {
    private final Class exceptionClass;
    private final TypeInfo detail;
    private final ExceptionType exceptionType;
    private final JavaMethodImpl javaMethod;
    private String messageName;
    private String faultAction = "";
    private Method faultInfoGetter;

    public CheckedExceptionImpl(JavaMethodImpl jm, Class exceptionClass, TypeInfo detail, ExceptionType exceptionType) {
        this.detail = detail;
        this.exceptionType = exceptionType;
        this.exceptionClass = exceptionClass;
        this.javaMethod = jm;
    }

    @Override
    public AbstractSEIModelImpl getOwner() {
        return this.javaMethod.owner;
    }

    @Override
    public JavaMethod getParent() {
        return this.javaMethod;
    }

    @Override
    public Class getExceptionClass() {
        return this.exceptionClass;
    }

    @Override
    public Class getDetailBean() {
        return (Class)this.detail.type;
    }

    @Override
    public Bridge getBridge() {
        return null;
    }

    public XMLBridge getBond() {
        return this.getOwner().getXMLBridge(this.detail);
    }

    @Override
    public TypeInfo getDetailType() {
        return this.detail;
    }

    @Override
    public ExceptionType getExceptionType() {
        return this.exceptionType;
    }

    @Override
    public String getMessageName() {
        return this.messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getFaultAction() {
        return this.faultAction;
    }

    public void setFaultAction(String faultAction) {
        this.faultAction = faultAction;
    }

    public String getDefaultFaultAction() {
        return WsaActionUtil.getDefaultFaultAction(this.javaMethod, this);
    }

    public Method getFaultInfoGetter() {
        return this.faultInfoGetter;
    }

    public void setFaultInfoGetter(Method faultInfoGetter) {
        this.faultInfoGetter = faultInfoGetter;
    }
}

