/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.io.CharSource;
import com.google.common.io.ElementTypesAreNonnullByDefault;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
class MultiReader
extends Reader {
    private final Iterator<? extends CharSource> it;
    @CheckForNull
    private Reader current;

    MultiReader(Iterator<? extends CharSource> readers) throws IOException {
        this.it = readers;
        this.advance();
    }

    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.current = this.it.next().openStream();
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        Preconditions.checkNotNull(cbuf);
        if (this.current == null) {
            return -1;
        }
        int result = this.current.read(cbuf, off, len);
        if (result == -1) {
            this.advance();
            return this.read(cbuf, off, len);
        }
        return result;
    }

    @Override
    public long skip(long n) throws IOException {
        Preconditions.checkArgument(n >= 0L, "n is negative");
        if (n > 0L) {
            while (this.current != null) {
                long result = this.current.skip(n);
                if (result > 0L) {
                    return result;
                }
                this.advance();
            }
        }
        return 0L;
    }

    @Override
    public boolean ready() throws IOException {
        return this.current != null && this.current.ready();
    }

    @Override
    public void close() throws IOException {
        if (this.current != null) {
            try {
                this.current.close();
            }
            finally {
                this.current = null;
            }
        }
    }
}

