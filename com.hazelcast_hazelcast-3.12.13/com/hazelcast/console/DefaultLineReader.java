/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.console;

import com.hazelcast.console.LineReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

class DefaultLineReader
implements LineReader {
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

    DefaultLineReader() throws UnsupportedEncodingException {
    }

    @Override
    public String readLine() throws Exception {
        return this.in.readLine();
    }
}

