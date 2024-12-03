/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.input.JDOMParseException
 */
package com.rometools.rome.io;

import com.rometools.rome.io.FeedException;
import org.jdom2.input.JDOMParseException;

public class ParsingFeedException
extends FeedException {
    private static final long serialVersionUID = 1L;

    public ParsingFeedException(String msg) {
        super(msg);
    }

    public ParsingFeedException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public int getLineNumber() {
        if (this.getCause() instanceof JDOMParseException) {
            return ((JDOMParseException)this.getCause()).getLineNumber();
        }
        return -1;
    }

    public int getColumnNumber() {
        if (this.getCause() instanceof JDOMParseException) {
            return ((JDOMParseException)this.getCause()).getColumnNumber();
        }
        return -1;
    }
}

