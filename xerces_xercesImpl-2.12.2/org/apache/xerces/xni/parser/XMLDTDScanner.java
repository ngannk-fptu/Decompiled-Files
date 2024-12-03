/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import java.io.IOException;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLInputSource;

public interface XMLDTDScanner
extends XMLDTDSource,
XMLDTDContentModelSource {
    public void setInputSource(XMLInputSource var1) throws IOException;

    public boolean scanDTDInternalSubset(boolean var1, boolean var2, boolean var3) throws IOException, XNIException;

    public boolean scanDTDExternalSubset(boolean var1) throws IOException, XNIException;
}

