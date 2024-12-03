/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.DocListener;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.OutputStreamCounter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

public abstract class DocWriter
implements DocListener {
    public static final byte NEWLINE = 10;
    public static final byte TAB = 9;
    public static final byte LT = 60;
    public static final byte SPACE = 32;
    public static final byte EQUALS = 61;
    public static final byte QUOTE = 34;
    public static final byte GT = 62;
    public static final byte FORWARD = 47;
    protected Rectangle pageSize;
    protected Document document;
    protected OutputStreamCounter os;
    protected boolean open = false;
    protected boolean pause = false;
    protected boolean closeStream = true;

    protected DocWriter() {
    }

    protected DocWriter(Document document, OutputStream os) {
        this.document = document;
        this.os = new OutputStreamCounter(new BufferedOutputStream(os));
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        return false;
    }

    @Override
    public void open() {
        this.open = true;
    }

    @Override
    public boolean setPageSize(Rectangle pageSize) {
        this.pageSize = pageSize;
        return true;
    }

    @Override
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return false;
    }

    @Override
    public boolean newPage() {
        return this.open;
    }

    @Override
    public void setHeader(HeaderFooter header) {
    }

    @Override
    public void resetHeader() {
    }

    @Override
    public void setFooter(HeaderFooter footer) {
    }

    @Override
    public void resetFooter() {
    }

    @Override
    public void resetPageCount() {
    }

    @Override
    public void setPageCount(int pageN) {
    }

    @Override
    public void close() {
        if (!this.open) {
            return;
        }
        this.open = false;
        try {
            this.os.flush();
            if (this.closeStream) {
                this.os.close();
            }
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public static final byte[] getISOBytes(String text) {
        if (text == null) {
            return null;
        }
        int len = text.length();
        byte[] b = new byte[len];
        for (int k = 0; k < len; ++k) {
            b[k] = (byte)text.charAt(k);
        }
        return b;
    }

    public void pause() {
        this.pause = true;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public void resume() {
        this.pause = false;
    }

    public void flush() {
        try {
            this.os.flush();
        }
        catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected void write(String string) throws IOException {
        this.os.write(DocWriter.getISOBytes(string));
    }

    protected void addTabs(int indent) throws IOException {
        this.os.write(10);
        for (int i = 0; i < indent; ++i) {
            this.os.write(9);
        }
    }

    protected void write(String key, String value) throws IOException {
        this.os.write(32);
        this.write(key);
        this.os.write(61);
        this.os.write(34);
        this.write(value);
        this.os.write(34);
    }

    protected void writeStart(String tag) throws IOException {
        this.os.write(60);
        this.write(tag);
    }

    protected void writeEnd(String tag) throws IOException {
        this.os.write(60);
        this.os.write(47);
        this.write(tag);
        this.os.write(62);
    }

    protected void writeEnd() throws IOException {
        this.os.write(32);
        this.os.write(47);
        this.os.write(62);
    }

    protected boolean writeMarkupAttributes(Properties markup) throws IOException {
        if (markup == null) {
            return false;
        }
        Iterator<Object> attributeIterator = markup.keySet().iterator();
        while (attributeIterator.hasNext()) {
            String name = String.valueOf(attributeIterator.next());
            this.write(name, markup.getProperty(name));
        }
        markup.clear();
        return true;
    }

    public boolean isCloseStream() {
        return this.closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    @Override
    public boolean setMarginMirroring(boolean MarginMirroring) {
        return false;
    }

    @Override
    public boolean setMarginMirroringTopBottom(boolean MarginMirroring) {
        return false;
    }
}

