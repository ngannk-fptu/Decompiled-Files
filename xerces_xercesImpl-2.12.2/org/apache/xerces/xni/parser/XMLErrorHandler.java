/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;

public interface XMLErrorHandler {
    public void warning(String var1, String var2, XMLParseException var3) throws XNIException;

    public void error(String var1, String var2, XMLParseException var3) throws XNIException;

    public void fatalError(String var1, String var2, XMLParseException var3) throws XNIException;
}

