/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

public class DefaultErrorHandler
implements ErrorHandler {
    public static final ErrorHandler INSTANCE = new DefaultErrorHandler();

    protected DefaultErrorHandler() {
    }

    @Override
    public void warning(CSSParseException e) {
    }

    @Override
    public void error(CSSParseException e) {
    }

    @Override
    public void fatalError(CSSParseException e) {
        throw e;
    }
}

