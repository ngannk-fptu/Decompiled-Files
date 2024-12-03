/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.impl.StringEnumerationHelperBase;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class QuotesAndWhitespaceTokenizer
extends StringEnumerationHelperBase {
    Object current;
    LinkedList list = new LinkedList();

    public QuotesAndWhitespaceTokenizer(String string) throws IllegalArgumentException {
        int n = 0;
        int n2 = string.length();
        while (n < n2) {
            StringTokenizer stringTokenizer;
            int n3 = string.indexOf(34, n);
            if (n3 >= 0) {
                int n4;
                stringTokenizer = new StringTokenizer(string.substring(n, n3));
                if (stringTokenizer.hasMoreTokens()) {
                    this.list.add(stringTokenizer);
                }
                if ((n4 = string.indexOf(34, n3 + 1)) == -1) {
                    throw new IllegalArgumentException("Badly quoted string: " + string);
                }
                this.list.add(string.substring(n3 + 1, n4));
                n = n4 + 1;
                continue;
            }
            stringTokenizer = new StringTokenizer(string.substring(n));
            if (!stringTokenizer.hasMoreTokens()) break;
            this.list.add(stringTokenizer);
            break;
        }
        this.advance();
    }

    @Override
    public synchronized boolean hasMoreStrings() {
        return this.current != null;
    }

    @Override
    public synchronized String nextString() {
        if (this.current instanceof String) {
            String string = (String)this.current;
            this.advance();
            return string;
        }
        StringTokenizer stringTokenizer = (StringTokenizer)this.current;
        String string = stringTokenizer.nextToken();
        if (!stringTokenizer.hasMoreTokens()) {
            this.advance();
        }
        return string;
    }

    private void advance() {
        if (this.list.isEmpty()) {
            this.current = null;
        } else {
            this.current = this.list.getFirst();
            this.list.removeFirst();
        }
    }

    public static void main(String[] stringArray) {
        String string = "\t  \n\r";
        QuotesAndWhitespaceTokenizer quotesAndWhitespaceTokenizer = new QuotesAndWhitespaceTokenizer(string);
        while (quotesAndWhitespaceTokenizer.hasMoreStrings()) {
            System.out.println(quotesAndWhitespaceTokenizer.nextString());
        }
    }
}

