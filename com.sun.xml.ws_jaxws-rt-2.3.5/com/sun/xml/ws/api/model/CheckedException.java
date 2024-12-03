/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 */
package com.sun.xml.ws.api.model;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.model.ExceptionType;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.spi.db.TypeInfo;

public interface CheckedException {
    public SEIModel getOwner();

    public JavaMethod getParent();

    public Class getExceptionClass();

    public Class getDetailBean();

    public Bridge getBridge();

    public ExceptionType getExceptionType();

    public String getMessageName();

    public TypeInfo getDetailType();
}

