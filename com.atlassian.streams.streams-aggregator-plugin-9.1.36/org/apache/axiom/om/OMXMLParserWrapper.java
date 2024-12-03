/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;

public interface OMXMLParserWrapper {
    public int next() throws OMException;

    public void discard(OMElement var1) throws OMException;

    public void setCache(boolean var1) throws OMException;

    public boolean isCache();

    public Object getParser();

    public boolean isCompleted();

    public OMDocument getDocument();

    public OMElement getDocumentElement();

    public OMElement getDocumentElement(boolean var1);

    public short getBuilderType();

    public void registerExternalContentHandler(Object var1);

    public Object getRegisteredContentHandler();

    public String getCharacterEncoding();

    public void close();
}

