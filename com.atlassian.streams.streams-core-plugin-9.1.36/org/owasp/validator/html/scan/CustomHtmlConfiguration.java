/*
 * Decompiled with CFR 0.152.
 */
package org.owasp.validator.html.scan;

import org.htmlunit.cyberneko.HTMLComponent;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;

public class CustomHtmlConfiguration
extends HTMLConfiguration {
    @Override
    protected void reset() throws XMLConfigurationException {
        XMLDocumentFilter[] filters;
        this.getHtmlComponents().remove(this.getTagBalancer());
        int size = this.getHtmlComponents().size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = this.getHtmlComponents().get(i);
            component.reset(this);
        }
        XMLDocumentSource lastSource = this.getDocumentScanner();
        if (this.getFeature("http://xml.org/sax/features/namespaces")) {
            lastSource.setDocumentHandler(this.getDocumentHandler());
            this.getNamespaceBinder().setDocumentSource(this.getTagBalancer());
            lastSource = this.getNamespaceBinder();
        }
        if ((filters = (XMLDocumentFilter[])this.getProperty("http://cyberneko.org/html/properties/filters")) != null) {
            XMLDocumentFilter[] var4 = filters;
            int var5 = filters.length;
            for (int var6 = 0; var6 < var5; ++var6) {
                XMLDocumentFilter filter = var4[var6];
                filter.setDocumentSource(lastSource);
                lastSource.setDocumentHandler(filter);
                lastSource = filter;
            }
        }
        lastSource.setDocumentHandler(this.getDocumentHandler());
    }
}

