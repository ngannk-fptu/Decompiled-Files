/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.transform;

import com.amazonaws.services.s3.internal.XmlWriter;
import com.amazonaws.services.s3.model.RequestPaymentConfiguration;

public class RequestPaymentConfigurationXmlFactory {
    public byte[] convertToXmlByteArray(RequestPaymentConfiguration requestPaymentConfiguration) {
        XmlWriter xml = new XmlWriter();
        xml.start("RequestPaymentConfiguration", "xmlns", "http://s3.amazonaws.com/doc/2006-03-01/");
        RequestPaymentConfiguration.Payer payer = requestPaymentConfiguration.getPayer();
        if (payer != null) {
            XmlWriter payerDocumentElement = xml.start("Payer");
            payerDocumentElement.value(payer.toString());
            payerDocumentElement.end();
        }
        xml.end();
        return xml.getBytes();
    }
}

