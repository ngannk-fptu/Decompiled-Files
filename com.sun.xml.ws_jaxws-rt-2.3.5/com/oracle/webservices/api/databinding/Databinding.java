/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.oracle.webservices.api.databinding;

import com.oracle.webservices.api.databinding.JavaCallInfo;
import com.oracle.webservices.api.databinding.WSDLGenerator;
import com.oracle.webservices.api.message.MessageContext;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import org.xml.sax.EntityResolver;

public interface Databinding {
    public JavaCallInfo createJavaCallInfo(Method var1, Object[] var2);

    public MessageContext serializeRequest(JavaCallInfo var1);

    public JavaCallInfo deserializeResponse(MessageContext var1, JavaCallInfo var2);

    public JavaCallInfo deserializeRequest(MessageContext var1);

    public MessageContext serializeResponse(JavaCallInfo var1);

    public static interface Builder {
        public Builder targetNamespace(String var1);

        public Builder serviceName(QName var1);

        public Builder portName(QName var1);

        public Builder wsdlURL(URL var1);

        public Builder wsdlSource(Source var1);

        public Builder entityResolver(EntityResolver var1);

        public Builder classLoader(ClassLoader var1);

        public Builder feature(WebServiceFeature ... var1);

        public Builder property(String var1, Object var2);

        public Databinding build();

        public WSDLGenerator createWSDLGenerator();
    }
}

