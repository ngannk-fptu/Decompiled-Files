/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.CSSParseException
 *  org.w3c.css.sac.ErrorHandler
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

public class DefaultErrorHandler
implements ErrorHandler {
    public static final ErrorHandler INSTANCE = new DefaultErrorHandler();

    protected DefaultErrorHandler() {
    }

    public void warning(CSSParseException e) {
    }

    public void error(CSSParseException e) {
    }

    public void fatalError(CSSParseException e) {
        throw e;
    }
}

