/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.exception;

import org.apache.tika.parser.RecursiveParserWrapper;
import org.xml.sax.SAXException;

public class WriteLimitReachedException
extends SAXException {
    private static final int MAX_DEPTH = 100;

    public WriteLimitReachedException(String msg) {
        super(msg);
    }

    public static boolean isWriteLimitReached(Throwable t) {
        return WriteLimitReachedException.isWriteLimitReached(t, 0);
    }

    private static boolean isWriteLimitReached(Throwable t, int depth) {
        if (t == null) {
            return false;
        }
        if (depth > 100) {
            return false;
        }
        if (t instanceof WriteLimitReachedException || t instanceof RecursiveParserWrapper.WriteLimitReached) {
            return true;
        }
        if (t instanceof SAXException && t.getClass().getName().equals("org.apache.tika.sax.WriteOutContentHandler$WriteLimitReachedException")) {
            return true;
        }
        return WriteLimitReachedException.isWriteLimitReached(t.getCause(), depth + 1);
    }

    public static void throwIfWriteLimitReached(Exception ex) throws SAXException {
        WriteLimitReachedException.throwIfWriteLimitReached(ex, 0);
    }

    private static void throwIfWriteLimitReached(Exception ex, int depth) throws SAXException {
        if (ex == null) {
            return;
        }
        if (depth > 100) {
            return;
        }
        if (ex instanceof WriteLimitReachedException || ex instanceof RecursiveParserWrapper.WriteLimitReached) {
            throw (SAXException)ex;
        }
        if (ex instanceof SAXException && ex.getClass().getName().equals("org.apache.tika.sax.WriteOutContentHandler$WriteLimitReachedException")) {
            throw (SAXException)ex;
        }
        WriteLimitReachedException.isWriteLimitReached(ex.getCause(), depth + 1);
    }
}

