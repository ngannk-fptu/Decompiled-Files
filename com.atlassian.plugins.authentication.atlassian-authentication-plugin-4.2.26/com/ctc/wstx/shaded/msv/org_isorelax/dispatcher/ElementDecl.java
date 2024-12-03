/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public interface ElementDecl {
    public String getName();

    public boolean getFeature(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;

    public Object getProperty(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;
}

