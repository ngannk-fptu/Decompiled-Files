/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.publisher.internal.GadgetSpecValidator;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
public final class GadgetSpecValidatorImpl
implements GadgetSpecValidator {
    private final Logger log = LoggerFactory.getLogger(GadgetSpecValidatorImpl.class);

    @Override
    public boolean isValid(InputStream spec) {
        Preconditions.checkNotNull((Object)spec);
        try {
            DocumentBuilderFactory documentBuilderFactory = this.getSafeDocumentBuildFactory();
            documentBuilderFactory.newDocumentBuilder().parse(new InputSource(spec));
            return true;
        }
        catch (ParserConfigurationException pce) {
            throw new Error("couldn't create XML parser", pce);
        }
        catch (SAXException saxe) {
            this.log.debug("couldn't parse gadget spec", (Throwable)saxe);
        }
        catch (IOException ioe) {
            this.log.debug("couldn't read from spec InputStream", (Throwable)ioe);
        }
        return false;
    }

    private DocumentBuilderFactory getSafeDocumentBuildFactory() throws ParserConfigurationException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        documentBuilderFactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        return documentBuilderFactory;
    }
}

