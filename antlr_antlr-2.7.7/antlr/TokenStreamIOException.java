/*
 * Decompiled with CFR 0.152.
 */
package antlr;

import antlr.TokenStreamException;
import java.io.IOException;

public class TokenStreamIOException
extends TokenStreamException {
    public IOException io;

    public TokenStreamIOException(IOException iOException) {
        super(iOException.getMessage());
        this.io = iOException;
    }
}

