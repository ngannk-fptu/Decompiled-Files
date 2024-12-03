/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import org.apache.abdera.i18n.text.Filter;

public class FilteredCharReader
extends FilterReader {
    private final Filter filter;
    private final char replacement;

    public FilteredCharReader(InputStream in, Filter filter) {
        this((Reader)new InputStreamReader(in), filter);
    }

    public FilteredCharReader(InputStream in, String charset, Filter filter) throws UnsupportedEncodingException {
        this((Reader)new InputStreamReader(in, charset), filter);
    }

    public FilteredCharReader(InputStream in, Filter filter, char replacement) {
        this(new InputStreamReader(in), filter, replacement);
    }

    public FilteredCharReader(InputStream in, String charset, Filter filter, char replacement) throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset), filter, replacement);
    }

    public FilteredCharReader(Reader in) {
        this(in, (Filter)new NonOpFilter(), '\u0000');
    }

    public FilteredCharReader(Reader in, Filter filter) {
        this(in, filter, '\u0000');
    }

    public FilteredCharReader(Reader in, char replacement) {
        this(in, (Filter)new NonOpFilter(), replacement);
    }

    public FilteredCharReader(Reader in, Filter filter, char replacement) {
        super(in);
        this.filter = filter;
        this.replacement = replacement;
        if (!(replacement == '\u0000' || Character.isValidCodePoint(replacement) && filter.accept(replacement))) {
            throw new IllegalArgumentException();
        }
    }

    public int read() throws IOException {
        int c = -1;
        if (this.replacement == '\u0000') {
            while ((c = super.read()) != -1 && !this.filter.accept(c)) {
            }
        } else {
            c = super.read();
            if (c != -1 && !this.filter.accept(c)) {
                c = this.replacement;
            }
        }
        return c;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        int r;
        int n;
        for (n = off; n < Math.min(len, cbuf.length - off) && (r = this.read()) != -1; ++n) {
            cbuf[n] = (char)r;
        }
        return (n -= off) <= 0 ? -1 : n;
    }

    public static Filter getUnacceptableFilter(int ... unacceptable) {
        return new CharArrayFilter(unacceptable);
    }

    private static class CharArrayFilter
    implements Filter {
        private final int[] chars;

        public CharArrayFilter(int[] chars) {
            this.chars = chars;
            Arrays.sort(this.chars);
        }

        public boolean accept(int c) {
            return Arrays.binarySearch(this.chars, c) < 0;
        }
    }

    private static class NonOpFilter
    implements Filter {
        private NonOpFilter() {
        }

        public boolean accept(int c) {
            return true;
        }
    }
}

