/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.parser.XMLParseException
 */
package org.cyberneko.html;

import org.apache.xerces.xni.parser.XMLParseException;

public interface HTMLErrorReporter {
    public String formatMessage(String var1, Object[] var2);

    public void reportWarning(String var1, Object[] var2) throws XMLParseException;

    public void reportError(String var1, Object[] var2) throws XMLParseException;
}

