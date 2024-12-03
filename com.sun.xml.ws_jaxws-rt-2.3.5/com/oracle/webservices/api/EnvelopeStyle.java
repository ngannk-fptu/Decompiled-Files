/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.spi.WebServiceFeatureAnnotation
 */
package com.oracle.webservices.api;

import com.oracle.webservices.api.EnvelopeStyleFeature;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id="", bean=EnvelopeStyleFeature.class)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface EnvelopeStyle {
    public Style[] style() default {Style.SOAP11};

    public static enum Style {
        SOAP11("http://schemas.xmlsoap.org/wsdl/soap/http"),
        SOAP12("http://www.w3.org/2003/05/soap/bindings/HTTP/"),
        XML("http://www.w3.org/2004/08/wsdl/http");

        public final String bindingId;

        private Style(String id) {
            this.bindingId = id;
        }

        public boolean isSOAP11() {
            return this.equals((Object)SOAP11);
        }

        public boolean isSOAP12() {
            return this.equals((Object)SOAP12);
        }

        public boolean isXML() {
            return this.equals((Object)XML);
        }
    }
}

