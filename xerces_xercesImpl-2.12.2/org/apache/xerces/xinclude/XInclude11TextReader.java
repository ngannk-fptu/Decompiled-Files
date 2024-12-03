/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xinclude;

import java.io.IOException;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.xinclude.XIncludeHandler;
import org.apache.xerces.xinclude.XIncludeTextReader;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XInclude11TextReader
extends XIncludeTextReader {
    public XInclude11TextReader(XMLInputSource xMLInputSource, XIncludeHandler xIncludeHandler, int n) throws IOException {
        super(xMLInputSource, xIncludeHandler, n);
    }

    @Override
    protected boolean isValid(int n) {
        return XML11Char.isXML11Valid(n);
    }
}

