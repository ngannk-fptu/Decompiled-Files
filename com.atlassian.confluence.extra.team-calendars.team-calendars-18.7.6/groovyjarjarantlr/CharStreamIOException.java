/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.CharStreamException;
import java.io.IOException;

public class CharStreamIOException
extends CharStreamException {
    public IOException io;

    public CharStreamIOException(IOException iOException) {
        super(iOException.getMessage());
        this.io = iOException;
    }
}

