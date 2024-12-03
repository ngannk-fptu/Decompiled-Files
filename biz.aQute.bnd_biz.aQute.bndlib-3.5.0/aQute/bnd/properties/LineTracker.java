/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.BadLocationException;
import aQute.bnd.properties.Document;
import aQute.bnd.properties.IRegion;
import aQute.bnd.properties.Line;
import aQute.bnd.properties.Region;
import java.util.ArrayList;
import java.util.List;

public class LineTracker {
    private final List<Line> fLines = new ArrayList<Line>();
    private int fTextLength;

    protected LineTracker() {
    }

    private int findLine(int offset) {
        if (this.fLines.size() == 0) {
            return -1;
        }
        int left = 0;
        int right = this.fLines.size() - 1;
        int mid = 0;
        Line line = null;
        while (left < right) {
            mid = (left + right) / 2;
            line = this.fLines.get(mid);
            if (offset < line.offset) {
                if (left == mid) {
                    right = left;
                    continue;
                }
                right = mid - 1;
                continue;
            }
            if (offset > line.offset) {
                if (right == mid) {
                    left = right;
                    continue;
                }
                left = mid + 1;
                continue;
            }
            if (offset != line.offset) continue;
            left = right = mid;
        }
        line = this.fLines.get(left);
        if (line.offset > offset) {
            --left;
        }
        return left;
    }

    private int getNumberOfLines(int startLine, int offset, int length) throws BadLocationException {
        if (length == 0) {
            return 1;
        }
        int target = offset + length;
        Line l = this.fLines.get(startLine);
        if (l.delimiter == null) {
            return 1;
        }
        if (l.offset + l.length > target) {
            return 1;
        }
        if (l.offset + l.length == target) {
            return 2;
        }
        return this.getLineNumberOfOffset(target) - startLine + 1;
    }

    public final int getLineLength(int line) throws BadLocationException {
        int lines = this.fLines.size();
        if (line < 0 || line > lines) {
            throw new BadLocationException();
        }
        if (lines == 0 || lines == line) {
            return 0;
        }
        Line l = this.fLines.get(line);
        return l.length;
    }

    public final int getLineNumberOfOffset(int position) throws BadLocationException {
        if (position < 0 || position > this.fTextLength) {
            throw new BadLocationException();
        }
        if (position == this.fTextLength) {
            int lastLine = this.fLines.size() - 1;
            if (lastLine < 0) {
                return 0;
            }
            Line l = this.fLines.get(lastLine);
            return l.delimiter != null ? lastLine + 1 : lastLine;
        }
        return this.findLine(position);
    }

    public final IRegion getLineInformationOfOffset(int position) throws BadLocationException {
        if (position > this.fTextLength) {
            throw new BadLocationException();
        }
        if (position == this.fTextLength) {
            int size = this.fLines.size();
            if (size == 0) {
                return new Region(0, 0);
            }
            Line l = this.fLines.get(size - 1);
            return l.delimiter != null ? new Line(this.fTextLength, 0) : new Line(this.fTextLength - l.length, l.length);
        }
        return this.getLineInformation(this.findLine(position));
    }

    public final IRegion getLineInformation(int line) throws BadLocationException {
        int lines = this.fLines.size();
        if (line < 0 || line > lines) {
            throw new BadLocationException();
        }
        if (lines == 0) {
            return new Line(0, 0);
        }
        if (line == lines) {
            Line l = this.fLines.get(line - 1);
            return new Line(l.offset + l.length, 0);
        }
        Line l = this.fLines.get(line);
        return l.delimiter != null ? new Line(l.offset, l.length - l.delimiter.length()) : l;
    }

    public final int getLineOffset(int line) throws BadLocationException {
        int lines = this.fLines.size();
        if (line < 0 || line > lines) {
            throw new BadLocationException();
        }
        if (lines == 0) {
            return 0;
        }
        if (line == lines) {
            Line l = this.fLines.get(line - 1);
            if (l.delimiter != null) {
                return l.offset + l.length;
            }
            throw new BadLocationException();
        }
        Line l = this.fLines.get(line);
        return l.offset;
    }

    public final int getNumberOfLines() {
        int lines = this.fLines.size();
        if (lines == 0) {
            return 1;
        }
        Line l = this.fLines.get(lines - 1);
        return l.delimiter != null ? lines + 1 : lines;
    }

    public final int getNumberOfLines(int position, int length) throws BadLocationException {
        if (position < 0 || position + length > this.fTextLength) {
            throw new BadLocationException();
        }
        if (length == 0) {
            return 1;
        }
        return this.getNumberOfLines(this.getLineNumberOfOffset(position), position, length);
    }

    public final int computeNumberOfLines(String text) {
        int count = 0;
        int start = 0;
        Document.DelimiterInfo delimiterInfo = this.nextDelimiterInfo(text, start);
        while (delimiterInfo != null && delimiterInfo.delimiterIndex > -1) {
            ++count;
            start = delimiterInfo.delimiterIndex + delimiterInfo.delimiterLength;
            delimiterInfo = this.nextDelimiterInfo(text, start);
        }
        return count;
    }

    public final String getLineDelimiter(int line) throws BadLocationException {
        int lines = this.fLines.size();
        if (line < 0 || line > lines) {
            throw new BadLocationException();
        }
        if (lines == 0) {
            return null;
        }
        if (line == lines) {
            return null;
        }
        Line l = this.fLines.get(line);
        return l.delimiter;
    }

    protected Document.DelimiterInfo nextDelimiterInfo(String text, int offset) {
        int length = text.length();
        for (int i = offset; i < length; ++i) {
            char ch = text.charAt(i);
            if (ch == '\r') {
                if (i + 1 < length && text.charAt(i + 1) == '\n') {
                    Document.DelimiterInfo fDelimiterInfo = new Document.DelimiterInfo();
                    fDelimiterInfo.delimiter = Document.DELIMITERS[2];
                    fDelimiterInfo.delimiterIndex = i;
                    fDelimiterInfo.delimiterLength = 2;
                    return fDelimiterInfo;
                }
                Document.DelimiterInfo fDelimiterInfo = new Document.DelimiterInfo();
                fDelimiterInfo.delimiter = Document.DELIMITERS[0];
                fDelimiterInfo.delimiterIndex = i;
                fDelimiterInfo.delimiterLength = 1;
                return fDelimiterInfo;
            }
            if (ch != '\n') continue;
            Document.DelimiterInfo fDelimiterInfo = new Document.DelimiterInfo();
            fDelimiterInfo.delimiter = Document.DELIMITERS[1];
            fDelimiterInfo.delimiterIndex = i;
            fDelimiterInfo.delimiterLength = 1;
            return fDelimiterInfo;
        }
        return null;
    }

    private int createLines(String text, int insertPosition, int offset) {
        int count = 0;
        int start = 0;
        Document.DelimiterInfo delimiterInfo = this.nextDelimiterInfo(text, 0);
        while (delimiterInfo != null && delimiterInfo.delimiterIndex > -1) {
            int index = delimiterInfo.delimiterIndex + (delimiterInfo.delimiterLength - 1);
            if (insertPosition + count >= this.fLines.size()) {
                this.fLines.add(new Line(offset + start, offset + index, delimiterInfo.delimiter));
            } else {
                this.fLines.add(insertPosition + count, new Line(offset + start, offset + index, delimiterInfo.delimiter));
            }
            ++count;
            start = index + 1;
            delimiterInfo = this.nextDelimiterInfo(text, start);
        }
        if (start < text.length()) {
            if (insertPosition + count < this.fLines.size()) {
                Line l = this.fLines.get(insertPosition + count);
                int delta = text.length() - start;
                l.offset -= delta;
                l.length += delta;
            } else {
                this.fLines.add(new Line(offset + start, offset + text.length() - 1, null));
                ++count;
            }
        }
        return count;
    }

    public final void replace(int position, int length, String text) throws BadLocationException {
        throw new UnsupportedOperationException();
    }

    public final void set(String text) {
        this.fLines.clear();
        if (text != null) {
            this.fTextLength = text.length();
            this.createLines(text, 0, 0);
        }
    }

    final List<Line> getLines() {
        return this.fLines;
    }
}

