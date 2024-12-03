/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFilter;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierHandler;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public interface Verifier {
    public static final String FEATURE_HANDLER = "http://www.iso-relax.org/verifier/handler";
    public static final String FEATURE_FILTER = "http://www.iso-relax.org/verifier/filter";

    public boolean isFeature(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;

    public void setFeature(String var1, boolean var2) throws SAXNotRecognizedException, SAXNotSupportedException;

    public Object getProperty(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;

    public void setProperty(String var1, Object var2) throws SAXNotRecognizedException, SAXNotSupportedException;

    public void setErrorHandler(ErrorHandler var1);

    public void setEntityResolver(EntityResolver var1);

    public boolean verify(String var1) throws SAXException, IOException;

    public boolean verify(InputSource var1) throws SAXException, IOException;

    public boolean verify(File var1) throws SAXException, IOException;

    public boolean verify(Node var1) throws SAXException;

    public VerifierHandler getVerifierHandler() throws SAXException;

    public VerifierFilter getVerifierFilter() throws SAXException;
}

