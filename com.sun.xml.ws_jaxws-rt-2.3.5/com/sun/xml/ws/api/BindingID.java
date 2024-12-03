/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.BindingType
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.api;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.BindingIDFactory;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import com.sun.xml.ws.encoding.SOAPBindingCodec;
import com.sun.xml.ws.encoding.XMLHTTPBindingCodec;
import com.sun.xml.ws.util.ServiceFinder;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.soap.MTOMFeature;

public abstract class BindingID {
    public static final SOAPHTTPImpl X_SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/", true);
    public static final SOAPHTTPImpl SOAP12_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/", true);
    public static final SOAPHTTPImpl SOAP11_HTTP = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http", true);
    public static final SOAPHTTPImpl SOAP12_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_12, "http://www.w3.org/2003/05/soap/bindings/HTTP/?mtom=true", true, true);
    public static final SOAPHTTPImpl SOAP11_HTTP_MTOM = new SOAPHTTPImpl(SOAPVersion.SOAP_11, "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true", true, true);
    public static final BindingID XML_HTTP = new Impl(SOAPVersion.SOAP_11, "http://www.w3.org/2004/08/wsdl/http", false){

        @Override
        public Codec createEncoder(WSBinding binding) {
            return new XMLHTTPBindingCodec(binding.getFeatures());
        }
    };
    private static final BindingID REST_HTTP = new Impl(SOAPVersion.SOAP_11, "http://jax-ws.dev.java.net/rest", true){

        @Override
        public Codec createEncoder(WSBinding binding) {
            return new XMLHTTPBindingCodec(binding.getFeatures());
        }
    };

    @NotNull
    public final WSBinding createBinding() {
        return BindingImpl.create(this);
    }

    @NotNull
    public String getTransport() {
        return "http://schemas.xmlsoap.org/soap/http";
    }

    @NotNull
    public final WSBinding createBinding(WebServiceFeature ... features) {
        return BindingImpl.create(this, features);
    }

    @NotNull
    public final WSBinding createBinding(WSFeatureList features) {
        return this.createBinding(features.toArray());
    }

    public abstract SOAPVersion getSOAPVersion();

    @NotNull
    public abstract Codec createEncoder(@NotNull WSBinding var1);

    public abstract String toString();

    public WebServiceFeatureList createBuiltinFeatureList() {
        return new WebServiceFeatureList();
    }

    public boolean canGenerateWSDL() {
        return false;
    }

    public String getParameter(String parameterName, String defaultValue) {
        return defaultValue;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BindingID)) {
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    @NotNull
    public static BindingID parse(String lexical) {
        if (lexical.equals(XML_HTTP.toString())) {
            return XML_HTTP;
        }
        if (lexical.equals(REST_HTTP.toString())) {
            return REST_HTTP;
        }
        if (BindingID.belongsTo(lexical, SOAP11_HTTP.toString())) {
            return BindingID.customize(lexical, SOAP11_HTTP);
        }
        if (BindingID.belongsTo(lexical, SOAP12_HTTP.toString())) {
            return BindingID.customize(lexical, SOAP12_HTTP);
        }
        if (BindingID.belongsTo(lexical, "http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
            return BindingID.customize(lexical, X_SOAP12_HTTP);
        }
        for (BindingIDFactory f : ServiceFinder.find(BindingIDFactory.class)) {
            BindingID r = f.parse(lexical);
            if (r == null) continue;
            return r;
        }
        throw new WebServiceException("Wrong binding ID: " + lexical);
    }

    private static boolean belongsTo(String lexical, String id) {
        return lexical.equals(id) || lexical.startsWith(id + '?');
    }

    private static SOAPHTTPImpl customize(String lexical, SOAPHTTPImpl base) {
        if (lexical.equals(base.toString())) {
            return base;
        }
        SOAPHTTPImpl r = new SOAPHTTPImpl(base.getSOAPVersion(), lexical, base.canGenerateWSDL());
        try {
            if (lexical.indexOf(63) == -1) {
                return r;
            }
            String query = URLDecoder.decode(lexical.substring(lexical.indexOf(63) + 1), "UTF-8");
            for (String token : query.split("&")) {
                int idx = token.indexOf(61);
                if (idx < 0) {
                    throw new WebServiceException("Malformed binding ID (no '=' in " + token + ")");
                }
                r.parameters.put(token.substring(0, idx), token.substring(idx + 1));
            }
        }
        catch (UnsupportedEncodingException e) {
            throw new AssertionError((Object)e);
        }
        return r;
    }

    @NotNull
    public static BindingID parse(Class<?> implClass) {
        String bindingId;
        BindingType bindingType = implClass.getAnnotation(BindingType.class);
        if (bindingType != null && (bindingId = bindingType.value()).length() > 0) {
            return BindingID.parse(bindingId);
        }
        return SOAP11_HTTP;
    }

    private static final class SOAPHTTPImpl
    extends Impl
    implements Cloneable {
        Map<String, String> parameters = new HashMap<String, String>();
        static final String MTOM_PARAM = "mtom";

        public SOAPHTTPImpl(SOAPVersion version, String lexical, boolean canGenerateWSDL) {
            super(version, lexical, canGenerateWSDL);
        }

        public SOAPHTTPImpl(SOAPVersion version, String lexical, boolean canGenerateWSDL, boolean mtomEnabled) {
            this(version, lexical, canGenerateWSDL);
            String mtomStr = mtomEnabled ? "true" : "false";
            this.parameters.put(MTOM_PARAM, mtomStr);
        }

        @Override
        @NotNull
        public Codec createEncoder(WSBinding binding) {
            return new SOAPBindingCodec(binding.getFeatures());
        }

        private Boolean isMTOMEnabled() {
            String mtom = this.parameters.get(MTOM_PARAM);
            return mtom == null ? null : Boolean.valueOf(mtom);
        }

        @Override
        public WebServiceFeatureList createBuiltinFeatureList() {
            WebServiceFeatureList r = super.createBuiltinFeatureList();
            Boolean mtom = this.isMTOMEnabled();
            if (mtom != null) {
                r.add((WebServiceFeature)new MTOMFeature(mtom.booleanValue()));
            }
            return r;
        }

        @Override
        public String getParameter(String parameterName, String defaultValue) {
            if (this.parameters.get(parameterName) == null) {
                return super.getParameter(parameterName, defaultValue);
            }
            return this.parameters.get(parameterName);
        }

        public SOAPHTTPImpl clone() throws CloneNotSupportedException {
            return (SOAPHTTPImpl)super.clone();
        }
    }

    private static abstract class Impl
    extends BindingID {
        final SOAPVersion version;
        private final String lexical;
        private final boolean canGenerateWSDL;

        public Impl(SOAPVersion version, String lexical, boolean canGenerateWSDL) {
            this.version = version;
            this.lexical = lexical;
            this.canGenerateWSDL = canGenerateWSDL;
        }

        @Override
        public SOAPVersion getSOAPVersion() {
            return this.version;
        }

        @Override
        public String toString() {
            return this.lexical;
        }

        @Override
        @Deprecated
        public boolean canGenerateWSDL() {
            return this.canGenerateWSDL;
        }
    }
}

