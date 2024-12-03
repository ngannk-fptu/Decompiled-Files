/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.MimeHeader
 *  javax.xml.soap.MimeHeaders
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.api.message;

import com.oracle.webservices.api.EnvelopeStyle;
import com.oracle.webservices.api.EnvelopeStyleFeature;
import com.oracle.webservices.api.message.MessageContext;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.pipe.Codecs;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.transport.http.HttpAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public class MessageContextFactory
extends com.oracle.webservices.api.message.MessageContextFactory {
    private WSFeatureList features;
    private Codec soapCodec;
    private Codec xmlCodec;
    private EnvelopeStyleFeature envelopeStyle;
    private EnvelopeStyle.Style singleSoapStyle;

    public MessageContextFactory(WebServiceFeature[] wsf) {
        this(new WebServiceFeatureList(wsf));
    }

    public MessageContextFactory(WSFeatureList wsf) {
        this.features = wsf;
        this.envelopeStyle = this.features.get(EnvelopeStyleFeature.class);
        if (this.envelopeStyle == null) {
            this.envelopeStyle = new EnvelopeStyleFeature(EnvelopeStyle.Style.SOAP11);
            this.features.mergeFeatures(new WebServiceFeature[]{this.envelopeStyle}, false);
        }
        for (EnvelopeStyle.Style s : this.envelopeStyle.getStyles()) {
            if (s.isXML()) {
                if (this.xmlCodec != null) continue;
                this.xmlCodec = Codecs.createXMLCodec(this.features);
                continue;
            }
            if (this.soapCodec == null) {
                this.soapCodec = Codecs.createSOAPBindingCodec(this.features);
            }
            this.singleSoapStyle = s;
        }
    }

    @Override
    protected com.oracle.webservices.api.message.MessageContextFactory newFactory(WebServiceFeature ... f) {
        return new MessageContextFactory(f);
    }

    @Override
    public MessageContext createContext() {
        return this.packet(null);
    }

    @Override
    public MessageContext createContext(SOAPMessage soap) {
        this.throwIfIllegalMessageArgument(soap);
        if (this.saajFactory != null) {
            return this.packet(this.saajFactory.createMessage(soap));
        }
        return this.packet(Messages.create(soap));
    }

    @Override
    public MessageContext createContext(Source m, EnvelopeStyle.Style envelopeStyle) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(Messages.create(m, SOAPVersion.from(envelopeStyle)));
    }

    @Override
    public MessageContext createContext(Source m) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(Messages.create(m, SOAPVersion.from(this.singleSoapStyle)));
    }

    @Override
    public MessageContext createContext(InputStream in, String contentType) throws IOException {
        this.throwIfIllegalMessageArgument(in);
        Packet p = this.packet(null);
        this.soapCodec.decode(in, contentType, p);
        return p;
    }

    @Override
    @Deprecated
    public MessageContext createContext(InputStream in, MimeHeaders headers) throws IOException {
        String contentType = MessageContextFactory.getHeader(headers, "Content-Type");
        Packet packet = (Packet)this.createContext(in, contentType);
        packet.acceptableMimeTypes = MessageContextFactory.getHeader(headers, "Accept");
        packet.soapAction = HttpAdapter.fixQuotesAroundSoapAction(MessageContextFactory.getHeader(headers, "SOAPAction"));
        return packet;
    }

    static String getHeader(MimeHeaders headers, String name) {
        String[] values = headers.getHeader(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    static Map<String, List<String>> toMap(MimeHeaders headers) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        Iterator i = headers.getAllHeaders();
        while (i.hasNext()) {
            MimeHeader mh = (MimeHeader)i.next();
            List<String> values = map.get(mh.getName());
            if (values == null) {
                values = new ArrayList<String>();
                map.put(mh.getName(), values);
            }
            values.add(mh.getValue());
        }
        return map;
    }

    public MessageContext createContext(Message m) {
        this.throwIfIllegalMessageArgument(m);
        return this.packet(m);
    }

    private Packet packet(Message m) {
        MTOMFeature mf;
        Packet p = new Packet();
        p.codec = this.soapCodec;
        if (m != null) {
            p.setMessage(m);
        }
        if ((mf = this.features.get(MTOMFeature.class)) != null) {
            p.setMtomFeature(mf);
        }
        p.setSAAJFactory(this.saajFactory);
        return p;
    }

    private void throwIfIllegalMessageArgument(Object message) throws IllegalArgumentException {
        if (message == null) {
            throw new IllegalArgumentException("null messages are not allowed.  Consider using MessageContextFactory.createContext()");
        }
    }

    @Override
    @Deprecated
    public MessageContext doCreate() {
        return this.packet(null);
    }

    @Override
    @Deprecated
    public MessageContext doCreate(SOAPMessage m) {
        return this.createContext(m);
    }

    @Override
    @Deprecated
    public MessageContext doCreate(Source x, SOAPVersion soapVersion) {
        return this.packet(Messages.create(x, soapVersion));
    }
}

