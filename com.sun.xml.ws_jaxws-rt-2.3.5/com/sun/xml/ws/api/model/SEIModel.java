/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.bind.JAXBContext
 */
package com.sun.xml.ws.api.model;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.JavaMethod;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.util.Pool;
import java.lang.reflect.Method;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;

public interface SEIModel {
    public Pool.Marshaller getMarshallerPool();

    public JAXBContext getJAXBContext();

    public JavaMethod getJavaMethod(Method var1);

    public JavaMethod getJavaMethod(QName var1);

    public JavaMethod getJavaMethodForWsdlOperation(QName var1);

    public Collection<? extends JavaMethod> getJavaMethods();

    @NotNull
    public String getWSDLLocation();

    @NotNull
    public QName getServiceQName();

    @NotNull
    public WSDLPort getPort();

    @NotNull
    public QName getPortName();

    @NotNull
    public QName getPortTypeName();

    @NotNull
    public QName getBoundPortTypeName();

    @NotNull
    public String getTargetNamespace();
}

