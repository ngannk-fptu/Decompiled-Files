/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni.parser;

import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;

public interface XMLErrorHandler {
    public void warning(String var1, String var2, XMLParseException var3) throws XNIException;

    public void error(String var1, String var2, XMLParseException var3) throws XNIException;

    public void fatalError(String var1, String var2, XMLParseException var3) throws XNIException;
}

