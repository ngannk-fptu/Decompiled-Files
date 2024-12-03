/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.BadLocationException;
import aQute.bnd.properties.CopyOnWriteTextStore;
import aQute.bnd.properties.GapTextStore;
import aQute.bnd.properties.IDocument;
import aQute.bnd.properties.IRegion;
import aQute.bnd.properties.ITextStore;
import aQute.bnd.properties.LineTracker;

public class Document
implements IDocument {
    public static final String[] DELIMITERS = new String[]{"\r", "\n", "\r\n"};
    private LineTracker lineTracker = new LineTracker();
    private ITextStore textStore = new CopyOnWriteTextStore(new GapTextStore());

    public Document(String text) {
        this.setText(text);
    }

    @Override
    public int getNumberOfLines() {
        return this.lineTracker.getNumberOfLines();
    }

    @Override
    public IRegion getLineInformation(int line) throws BadLocationException {
        return this.lineTracker.getLineInformation(line);
    }

    @Override
    public String get(int offset, int length) throws BadLocationException {
        return this.textStore.get(offset, length);
    }

    @Override
    public String getLineDelimiter(int line) throws BadLocationException {
        return this.lineTracker.getLineDelimiter(line);
    }

    @Override
    public int getLength() {
        return this.textStore.getLength();
    }

    @Override
    public void replace(int offset, int length, String text) throws BadLocationException {
        this.textStore.replace(offset, length, text);
        this.lineTracker.set(this.get());
    }

    @Override
    public char getChar(int pos) {
        return this.textStore.get(pos);
    }

    public void setText(String text) {
        this.textStore.set(text);
        this.lineTracker.set(text);
    }

    @Override
    public String get() {
        return this.textStore.get(0, this.textStore.getLength());
    }

    protected static class DelimiterInfo {
        public int delimiterIndex;
        public int delimiterLength;
        public String delimiter;

        protected DelimiterInfo() {
        }
    }
}

