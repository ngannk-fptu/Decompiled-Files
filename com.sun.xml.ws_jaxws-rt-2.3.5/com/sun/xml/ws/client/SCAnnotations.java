/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebEndpoint
 *  javax.xml.ws.WebServiceClient
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client;

import com.sun.xml.ws.util.JAXWSUtils;
import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;

final class SCAnnotations {
    final ArrayList<QName> portQNames = new ArrayList();
    final ArrayList<Class> classes = new ArrayList();

    SCAnnotations(final Class<?> sc) {
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                WebServiceClient wsc = sc.getAnnotation(WebServiceClient.class);
                if (wsc == null) {
                    throw new WebServiceException("Service Interface Annotations required, exiting...");
                }
                String tns = wsc.targetNamespace();
                try {
                    JAXWSUtils.getFileOrURL(wsc.wsdlLocation());
                }
                catch (IOException e) {
                    throw new WebServiceException((Throwable)e);
                }
                for (Method method : sc.getDeclaredMethods()) {
                    Class<?> seiClazz;
                    WebEndpoint webEndpoint = method.getAnnotation(WebEndpoint.class);
                    if (webEndpoint != null) {
                        String endpointName = webEndpoint.name();
                        QName portQName = new QName(tns, endpointName);
                        SCAnnotations.this.portQNames.add(portQName);
                    }
                    if ((seiClazz = method.getReturnType()) == Void.TYPE) continue;
                    SCAnnotations.this.classes.add(seiClazz);
                }
                return null;
            }
        });
    }
}

