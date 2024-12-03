/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sourceforge.htmlunit.cyberneko.HTMLComponent
 *  net.sourceforge.htmlunit.cyberneko.HTMLConfiguration
 *  net.sourceforge.htmlunit.cyberneko.HTMLScanner
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 */
package org.owasp.validator.html.scan;

import net.sourceforge.htmlunit.cyberneko.HTMLComponent;
import net.sourceforge.htmlunit.cyberneko.HTMLConfiguration;
import net.sourceforge.htmlunit.cyberneko.HTMLScanner;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class CustomHtmlConfiguration
extends HTMLConfiguration {
    protected void reset() throws XMLConfigurationException {
        XMLDocumentFilter[] filters;
        this.fHTMLComponents.remove(this.fTagBalancer);
        int size = this.fHTMLComponents.size();
        for (int i = 0; i < size; ++i) {
            HTMLComponent component = (HTMLComponent)this.fHTMLComponents.get(i);
            component.reset((XMLComponentManager)this);
        }
        HTMLScanner lastSource = this.fDocumentScanner;
        if (this.getFeature("http://xml.org/sax/features/namespaces")) {
            lastSource.setDocumentHandler((XMLDocumentHandler)this.fNamespaceBinder);
            this.fNamespaceBinder.setDocumentSource((XMLDocumentSource)this.fTagBalancer);
            lastSource = this.fNamespaceBinder;
        }
        if ((filters = (XMLDocumentFilter[])this.getProperty("http://cyberneko.org/html/properties/filters")) != null) {
            XMLDocumentFilter[] var4 = filters;
            int var5 = filters.length;
            for (int var6 = 0; var6 < var5; ++var6) {
                XMLDocumentFilter filter = var4[var6];
                filter.setDocumentSource((XMLDocumentSource)lastSource);
                lastSource.setDocumentHandler((XMLDocumentHandler)filter);
                lastSource = filter;
            }
        }
        lastSource.setDocumentHandler(this.fDocumentHandler);
    }
}

