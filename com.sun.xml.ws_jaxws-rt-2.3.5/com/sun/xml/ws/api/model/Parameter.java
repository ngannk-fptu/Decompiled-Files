/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.api.Bridge
 *  javax.jws.WebParam$Mode
 */
package com.sun.xml.ws.api.model;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.ParameterBinding;
import com.sun.xml.ws.api.model.SEIModel;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public interface Parameter {
    public SEIModel getOwner();

    public JavaMethod getParent();

    public QName getName();

    public Bridge getBridge();

    public WebParam.Mode getMode();

    public int getIndex();

    public boolean isWrapperStyle();

    public boolean isReturnValue();

    public ParameterBinding getBinding();

    public ParameterBinding getInBinding();

    public ParameterBinding getOutBinding();

    public boolean isIN();

    public boolean isOUT();

    public boolean isINOUT();

    public boolean isResponse();

    public Object getHolderValue(Object var1);

    public String getPartName();
}

