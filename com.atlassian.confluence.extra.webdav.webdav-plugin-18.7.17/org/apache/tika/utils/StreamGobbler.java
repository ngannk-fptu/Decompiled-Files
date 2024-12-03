/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class StreamGobbler
implements Runnable {
    private final InputStream is;
    private final int maxBufferLength;
    List<String> lines = new ArrayList<String>();
    long streamLength = 0L;
    boolean isTruncated = false;

    public StreamGobbler(InputStream is, int maxBufferLength) {
        this.is = is;
        this.maxBufferLength = maxBufferLength;
    }

    @Override
    public void run() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(this.is, StandardCharsets.UTF_8));){
            String line = r.readLine();
            while (line != null) {
                if (this.maxBufferLength >= 0) {
                    if (this.streamLength + (long)line.length() > (long)this.maxBufferLength) {
                        int len = this.maxBufferLength - (int)this.streamLength;
                        if (len > 0) {
                            this.isTruncated = true;
                            String truncatedLine = line.substring(0, Math.min(line.length(), len));
                            this.lines.add(truncatedLine);
                        }
                    } else {
                        this.lines.add(line);
                    }
                }
                this.streamLength += (long)line.length();
                line = r.readLine();
            }
        }
        catch (IOException e) {
            return;
        }
    }

    public List<String> getLines() {
        return this.lines;
    }

    public long getStreamLength() {
        return this.streamLength;
    }

    public boolean getIsTruncated() {
        return this.isTruncated;
    }
}

