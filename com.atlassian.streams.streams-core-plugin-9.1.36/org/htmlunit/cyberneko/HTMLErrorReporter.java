/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko;

import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;

public interface HTMLErrorReporter {
    public String formatMessage(String var1, Object[] var2);

    public void reportWarning(String var1, Object[] var2) throws XMLParseException;

    public void reportError(String var1, Object[] var2) throws XMLParseException;
}

