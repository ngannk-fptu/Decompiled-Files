/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ErrorHandler;
import org.apache.batik.parser.ParseException;

public class DefaultErrorHandler
implements ErrorHandler {
    @Override
    public void error(ParseException e) throws ParseException {
        throw e;
    }
}

