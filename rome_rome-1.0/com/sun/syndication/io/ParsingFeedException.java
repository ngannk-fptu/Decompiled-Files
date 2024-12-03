/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.input.JDOMParseException
 */
package com.sun.syndication.io;

import com.sun.syndication.io.FeedException;
import org.jdom.input.JDOMParseException;

public class ParsingFeedException
extends FeedException {
    public ParsingFeedException(String msg) {
        super(msg);
    }

    public ParsingFeedException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public int getLineNumber() {
        return this.getCause() instanceof JDOMParseException ? ((JDOMParseException)this.getCause()).getLineNumber() : -1;
    }

    public int getColumnNumber() {
        return this.getCause() instanceof JDOMParseException ? ((JDOMParseException)this.getCause()).getColumnNumber() : -1;
    }
}

