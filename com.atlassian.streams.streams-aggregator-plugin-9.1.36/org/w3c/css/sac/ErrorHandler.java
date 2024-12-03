/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;

public interface ErrorHandler {
    public void warning(CSSParseException var1) throws CSSException;

    public void error(CSSParseException var1) throws CSSException;

    public void fatalError(CSSParseException var1) throws CSSException;
}

