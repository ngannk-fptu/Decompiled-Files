/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.scanner;

import com.hazelcast.org.snakeyaml.engine.v2.api.LoadSettings;
import com.hazelcast.org.snakeyaml.engine.v2.common.CharConstants;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.ReaderException;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Optional;

public final class StreamReader {
    private String name;
    private final Reader stream;
    private int[] dataWindow;
    private int dataLength;
    private int pointer = 0;
    private boolean eof;
    private int index = 0;
    private int line = 0;
    private int column = 0;
    private int bufferSize;
    private char[] buffer;
    private boolean useMarks;

    public StreamReader(Reader reader, LoadSettings loadSettings) {
        this.name = loadSettings.getLabel();
        this.dataWindow = new int[0];
        this.dataLength = 0;
        this.stream = reader;
        this.eof = false;
        this.bufferSize = loadSettings.getBufferSize();
        this.buffer = new char[this.bufferSize];
        this.useMarks = loadSettings.getUseMarks();
    }

    public StreamReader(String stream, LoadSettings loadSettings) {
        this(new StringReader(stream), loadSettings);
    }

    public static final boolean isPrintable(String data) {
        int codePoint;
        int length = data.length();
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = data.codePointAt(offset);
            if (StreamReader.isPrintable(codePoint)) continue;
            return false;
        }
        return true;
    }

    public static boolean isPrintable(int c) {
        return c >= 32 && c <= 126 || c == 9 || c == 10 || c == 13 || c == 133 || c >= 160 && c <= 55295 || c >= 57344 && c <= 65533 || c >= 65536 && c <= 0x10FFFF;
    }

    public Optional<Mark> getMark() {
        if (this.useMarks) {
            return Optional.of(new Mark(this.name, this.index, this.line, this.column, this.dataWindow, this.pointer));
        }
        return Optional.empty();
    }

    public void forward() {
        this.forward(1);
    }

    public void forward(int length) {
        for (int i = 0; i < length && this.ensureEnoughData(); ++i) {
            int c = this.dataWindow[this.pointer++];
            ++this.index;
            if (CharConstants.LINEBR.has(c) || c == 13 && this.ensureEnoughData() && this.dataWindow[this.pointer] != 10) {
                ++this.line;
                this.column = 0;
                continue;
            }
            if (c == 65279) continue;
            ++this.column;
        }
    }

    public int peek() {
        return this.ensureEnoughData() ? this.dataWindow[this.pointer] : 0;
    }

    public int peek(int index) {
        return this.ensureEnoughData(index) ? this.dataWindow[this.pointer + index] : 0;
    }

    public String prefix(int length) {
        if (length == 0) {
            return "";
        }
        if (this.ensureEnoughData(length)) {
            return new String(this.dataWindow, this.pointer, length);
        }
        return new String(this.dataWindow, this.pointer, Math.min(length, this.dataLength - this.pointer));
    }

    public String prefixForward(int length) {
        String prefix = this.prefix(length);
        this.pointer += length;
        this.index += length;
        this.column += length;
        return prefix;
    }

    private boolean ensureEnoughData() {
        return this.ensureEnoughData(0);
    }

    private boolean ensureEnoughData(int size) {
        if (!this.eof && this.pointer + size >= this.dataLength) {
            this.update();
        }
        return this.pointer + size < this.dataLength;
    }

    private void update() {
        try {
            int read = this.stream.read(this.buffer, 0, this.bufferSize - 1);
            if (read > 0) {
                int cpIndex = this.dataLength - this.pointer;
                this.dataWindow = Arrays.copyOfRange(this.dataWindow, this.pointer, this.dataLength + read);
                if (Character.isHighSurrogate(this.buffer[read - 1])) {
                    if (this.stream.read(this.buffer, read, 1) == -1) {
                        this.eof = true;
                    } else {
                        ++read;
                    }
                }
                int nonPrintable = 32;
                int i = 0;
                while (i < read) {
                    int codePoint;
                    this.dataWindow[cpIndex] = codePoint = Character.codePointAt(this.buffer, i);
                    if (StreamReader.isPrintable(codePoint)) {
                        i += Character.charCount(codePoint);
                    } else {
                        nonPrintable = codePoint;
                        i = read;
                    }
                    ++cpIndex;
                }
                this.dataLength = cpIndex;
                this.pointer = 0;
                if (nonPrintable != 32) {
                    throw new ReaderException(this.name, cpIndex - 1, nonPrintable, "special characters are not allowed");
                }
            } else {
                this.eof = true;
            }
        }
        catch (IOException ioe) {
            throw new YamlEngineException(ioe);
        }
    }

    public int getColumn() {
        return this.column;
    }

    public int getIndex() {
        return this.index;
    }

    public int getLine() {
        return this.line;
    }
}

