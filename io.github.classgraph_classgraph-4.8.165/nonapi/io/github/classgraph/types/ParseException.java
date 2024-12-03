/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.types;

import nonapi.io.github.classgraph.types.Parser;

public class ParseException
extends Exception {
    static final long serialVersionUID = 1L;

    public ParseException(Parser parser, String msg) {
        super(parser == null ? msg : msg + " (" + parser.getPositionInfo() + ")");
    }
}

