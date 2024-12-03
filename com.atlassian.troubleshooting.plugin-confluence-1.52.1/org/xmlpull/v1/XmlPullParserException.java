/*
 * Decompiled with CFR 0.152.
 */
package org.xmlpull.v1;

import java.io.PrintStream;
import org.xmlpull.v1.XmlPullParser;

public class XmlPullParserException
extends Exception {
    protected Throwable detail;
    protected int row = -1;
    protected int column = -1;

    public XmlPullParserException(String s) {
        super(s);
    }

    public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain) {
        super((msg == null ? "" : msg + " ") + (parser == null ? "" : "(position:" + parser.getPositionDescription() + ") ") + (chain == null ? "" : "caused by: " + chain));
        if (parser != null) {
            this.row = parser.getLineNumber();
            this.column = parser.getColumnNumber();
        }
        this.detail = chain;
    }

    public Throwable getDetail() {
        return this.detail;
    }

    public int getLineNumber() {
        return this.row;
    }

    public int getColumnNumber() {
        return this.column;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace() {
        if (this.detail == null) {
            super.printStackTrace();
        } else {
            PrintStream printStream = System.err;
            synchronized (printStream) {
                System.err.println(super.getMessage() + "; nested exception is:");
                this.detail.printStackTrace();
            }
        }
    }
}

