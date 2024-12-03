/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.model;

import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.namespace.QName;

public interface WSDLOperationMapping {
    public WSDLBoundOperation getWSDLBoundOperation();

    public JavaMethod getJavaMethod();

    public QName getOperationName();
}

