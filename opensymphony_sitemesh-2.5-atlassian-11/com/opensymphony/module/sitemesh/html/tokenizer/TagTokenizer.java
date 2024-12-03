/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.html.tokenizer.Parser;
import com.opensymphony.module.sitemesh.html.tokenizer.TokenHandler;

public class TagTokenizer {
    private final char[] input;
    private final int length;

    public TagTokenizer(char[] input) {
        this(input, input.length);
    }

    public TagTokenizer(char[] input, int length) {
        this.input = input;
        this.length = length;
    }

    public TagTokenizer(String input) {
        this(input.toCharArray());
    }

    public void start(TokenHandler handler) {
        Parser parser = new Parser(this.input, this.length, handler);
        parser.start();
    }
}

