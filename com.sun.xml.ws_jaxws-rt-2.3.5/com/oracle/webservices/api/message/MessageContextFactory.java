/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceFeature
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.EnvelopeStyle;
import com.oracle.webservices.api.message.MessageContext;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.saaj.SAAJFactory;
import com.sun.xml.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;

public abstract class MessageContextFactory {
    private static final MessageContextFactory DEFAULT = new com.sun.xml.ws.api.message.MessageContextFactory(new WebServiceFeature[0]);
    protected SAAJFactory saajFactory = null;

    protected abstract MessageContextFactory newFactory(WebServiceFeature ... var1);

    public abstract MessageContext createContext();

    public abstract MessageContext createContext(SOAPMessage var1);

    public abstract MessageContext createContext(Source var1);

    public abstract MessageContext createContext(Source var1, EnvelopeStyle.Style var2);

    public abstract MessageContext createContext(InputStream var1, String var2) throws IOException;

    @Deprecated
    public abstract MessageContext createContext(InputStream var1, MimeHeaders var2) throws IOException;

    public static MessageContextFactory createFactory(WebServiceFeature ... f) {
        return MessageContextFactory.createFactory(null, f);
    }

    public static MessageContextFactory createFactory(ClassLoader cl, WebServiceFeature ... f) {
        for (MessageContextFactory factory : ServiceFinder.find(MessageContextFactory.class, cl)) {
            MessageContextFactory newfac = factory.newFactory(f);
            if (newfac == null) continue;
            return newfac;
        }
        return new com.sun.xml.ws.api.message.MessageContextFactory(f);
    }

    @Deprecated
    public abstract MessageContext doCreate();

    @Deprecated
    public abstract MessageContext doCreate(SOAPMessage var1);

    @Deprecated
    public abstract MessageContext doCreate(Source var1, SOAPVersion var2);

    @Deprecated
    public static MessageContext create(ClassLoader ... classLoader) {
        return MessageContextFactory.serviceFinder(classLoader, new Creator(){

            @Override
            public MessageContext create(MessageContextFactory f) {
                return f.doCreate();
            }
        });
    }

    @Deprecated
    public static MessageContext create(final SOAPMessage m, ClassLoader ... classLoader) {
        return MessageContextFactory.serviceFinder(classLoader, new Creator(){

            @Override
            public MessageContext create(MessageContextFactory f) {
                return f.doCreate(m);
            }
        });
    }

    @Deprecated
    public static MessageContext create(final Source m, final SOAPVersion v, ClassLoader ... classLoader) {
        return MessageContextFactory.serviceFinder(classLoader, new Creator(){

            @Override
            public MessageContext create(MessageContextFactory f) {
                return f.doCreate(m, v);
            }
        });
    }

    @Deprecated
    private static MessageContext serviceFinder(ClassLoader[] classLoader, Creator creator) {
        ClassLoader cl = classLoader.length == 0 ? null : classLoader[0];
        for (MessageContextFactory factory : ServiceFinder.find(MessageContextFactory.class, cl)) {
            MessageContext messageContext = creator.create(factory);
            if (messageContext == null) continue;
            return messageContext;
        }
        return creator.create(DEFAULT);
    }

    public void setSAAJFactory(SAAJFactory saajFactory) {
        this.saajFactory = saajFactory;
    }

    @Deprecated
    private static interface Creator {
        public MessageContext create(MessageContextFactory var1);
    }
}

