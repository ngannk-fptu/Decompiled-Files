/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.regex;

import com.twelvemonkeys.util.AbstractTokenIterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTokenIterator
extends AbstractTokenIterator {
    private final Matcher matcher;
    private boolean next = false;

    public RegExTokenIterator(String string) {
        this(string, "\\S+");
    }

    public RegExTokenIterator(String string, String string2) {
        if (string == null) {
            throw new IllegalArgumentException("string == null");
        }
        if (string2 == null) {
            throw new IllegalArgumentException("pattern == null");
        }
        this.matcher = Pattern.compile(string2).matcher(string);
    }

    @Override
    public void reset() {
        this.matcher.reset();
    }

    @Override
    public boolean hasNext() {
        return this.next || (this.next = this.matcher.find());
    }

    @Override
    public String next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.next = false;
        return this.matcher.group();
    }
}

