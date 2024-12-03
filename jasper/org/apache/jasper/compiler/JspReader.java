/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.Jar
 */
package org.apache.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.JspUtil;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.runtime.ExceptionUtils;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.Jar;

class JspReader {
    private final Log log = LogFactory.getLog(JspReader.class);
    private Mark current;
    private final JspCompilationContext context;
    private final ErrorDispatcher err;

    JspReader(JspCompilationContext ctxt, String fname, String encoding, Jar jar, ErrorDispatcher err) throws JasperException, FileNotFoundException, IOException {
        this(ctxt, fname, JspUtil.getReader(fname, encoding, jar, ctxt, err), err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    JspReader(JspCompilationContext ctxt, String fname, InputStreamReader reader, ErrorDispatcher err) throws JasperException {
        this.context = ctxt;
        this.err = err;
        try {
            CharArrayWriter caw = new CharArrayWriter();
            char[] buf = new char[1024];
            int i = 0;
            while ((i = reader.read(buf)) != -1) {
                caw.write(buf, 0, i);
            }
            caw.close();
            this.current = new Mark(this, caw.toCharArray(), fname);
        }
        catch (Throwable ex) {
            ExceptionUtils.handleThrowable(ex);
            this.log.error((Object)Localizer.getMessage("jsp.error.file.cannot.read", fname), ex);
            err.jspError("jsp.error.file.cannot.read", fname);
        }
        finally {
            block14: {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception any) {
                        if (!this.log.isDebugEnabled()) break block14;
                        this.log.debug((Object)"Exception closing reader: ", (Throwable)any);
                    }
                }
            }
        }
    }

    JspCompilationContext getJspCompilationContext() {
        return this.context;
    }

    boolean hasMoreInput() {
        return this.current.cursor < this.current.stream.length;
    }

    int nextChar() {
        if (!this.hasMoreInput()) {
            return -1;
        }
        char ch = this.current.stream[this.current.cursor];
        ++this.current.cursor;
        if (ch == '\n') {
            ++this.current.line;
            this.current.col = 0;
        } else {
            ++this.current.col;
        }
        return ch;
    }

    private int nextChar(Mark mark) {
        if (!this.hasMoreInput()) {
            return -1;
        }
        char ch = this.current.stream[this.current.cursor];
        mark.init(this.current, true);
        ++this.current.cursor;
        if (ch == '\n') {
            ++this.current.line;
            this.current.col = 0;
        } else {
            ++this.current.col;
        }
        return ch;
    }

    private Boolean indexOf(char c, Mark mark) {
        int i;
        if (!this.hasMoreInput()) {
            return null;
        }
        int end = this.current.stream.length;
        int line = this.current.line;
        int col = this.current.col;
        for (i = this.current.cursor; i < end; ++i) {
            char ch = this.current.stream[i];
            if (ch == c) {
                mark.update(i, line, col);
            }
            if (ch == '\n') {
                ++line;
                col = 0;
            } else {
                ++col;
            }
            if (ch != c) continue;
            this.current.update(i + 1, line, col);
            return Boolean.TRUE;
        }
        this.current.update(i, line, col);
        return Boolean.FALSE;
    }

    void pushChar() {
        --this.current.cursor;
        --this.current.col;
    }

    String getText(Mark start, Mark stop) {
        Mark oldstart = this.mark();
        this.reset(start);
        CharArrayWriter caw = new CharArrayWriter();
        while (!this.markEquals(stop)) {
            caw.write(this.nextChar());
        }
        caw.close();
        this.setCurrent(oldstart);
        return caw.toString();
    }

    int peekChar() {
        return this.peekChar(0);
    }

    int peekChar(int readAhead) {
        int target = this.current.cursor + readAhead;
        if (target < this.current.stream.length) {
            return this.current.stream[target];
        }
        return -1;
    }

    Mark mark() {
        return new Mark(this.current);
    }

    private boolean markEquals(Mark another) {
        return another.equals(this.current);
    }

    void reset(Mark mark) {
        this.current = new Mark(mark);
    }

    private void setCurrent(Mark mark) {
        this.current = mark;
    }

    boolean matches(String string) {
        int streamSize;
        int cursor = this.current.cursor;
        int len = string.length();
        if (cursor + len < (streamSize = this.current.stream.length)) {
            int i;
            int line = this.current.line;
            int col = this.current.col;
            for (i = 0; i < len; ++i) {
                char ch = this.current.stream[i + cursor];
                if (string.charAt(i) != ch) {
                    return false;
                }
                if (ch == '\n') {
                    ++line;
                    col = 0;
                    continue;
                }
                ++col;
            }
            this.current.update(i + cursor, line, col);
        } else {
            Mark mark = this.mark();
            int ch = 0;
            int i = 0;
            do {
                if ((char)(ch = this.nextChar()) == string.charAt(i++)) continue;
                this.setCurrent(mark);
                return false;
            } while (i < len);
        }
        return true;
    }

    boolean matchesETag(String tagName) {
        Mark mark = this.mark();
        if (!this.matches("</" + tagName)) {
            return false;
        }
        this.skipSpaces();
        if (this.nextChar() == 62) {
            return true;
        }
        this.setCurrent(mark);
        return false;
    }

    boolean matchesETagWithoutLessThan(String tagName) {
        Mark mark = this.mark();
        if (!this.matches("/" + tagName)) {
            return false;
        }
        this.skipSpaces();
        if (this.nextChar() == 62) {
            return true;
        }
        this.setCurrent(mark);
        return false;
    }

    boolean matchesOptionalSpacesFollowedBy(String s) {
        Mark mark = this.mark();
        this.skipSpaces();
        boolean result = this.matches(s);
        if (!result) {
            this.setCurrent(mark);
        }
        return result;
    }

    int skipSpaces() {
        int i = 0;
        while (this.hasMoreInput() && this.isSpace()) {
            ++i;
            this.nextChar();
        }
        return i;
    }

    Mark skipUntil(String limit) {
        Mark ret = this.mark();
        int limlen = limit.length();
        char firstChar = limit.charAt(0);
        Boolean result = null;
        Mark restart = null;
        block0: while ((result = this.indexOf(firstChar, ret)) != null) {
            if (!result.booleanValue()) continue;
            if (restart != null) {
                restart.init(this.current, true);
            } else {
                restart = this.mark();
            }
            for (int i = 1; i < limlen; ++i) {
                if (this.peekChar() != limit.charAt(i)) {
                    this.current.init(restart, true);
                    continue block0;
                }
                this.nextChar();
            }
            return ret;
        }
        return null;
    }

    Mark skipUntilIgnoreEsc(String limit, boolean ignoreEL) {
        Mark ret = this.mark();
        int limlen = limit.length();
        int prev = 120;
        int firstChar = limit.charAt(0);
        int ch = this.nextChar(ret);
        while (ch != -1) {
            block9: {
                if (ch == 92 && prev == 92) {
                    ch = 0;
                } else if (prev != 92) {
                    if (!(ignoreEL || ch != 36 && ch != 35 || this.peekChar() != 123)) {
                        this.nextChar();
                        this.skipELExpression();
                    } else if (ch == firstChar) {
                        for (int i = 1; i < limlen; ++i) {
                            if (this.peekChar() == limit.charAt(i)) {
                                this.nextChar();
                                continue;
                            }
                            break block9;
                        }
                        return ret;
                    }
                }
            }
            prev = ch;
            ch = this.nextChar(ret);
        }
        return null;
    }

    Mark skipUntilETag(String tag) {
        Mark ret = this.skipUntil("</" + tag);
        if (ret != null) {
            this.skipSpaces();
            if (this.nextChar() != 62) {
                ret = null;
            }
        }
        return ret;
    }

    Mark skipELExpression() {
        int currentChar;
        Mark last = this.mark();
        boolean singleQuoted = false;
        boolean doubleQuoted = false;
        int nesting = 0;
        do {
            currentChar = this.nextChar(last);
            while (currentChar == 92 && (singleQuoted || doubleQuoted)) {
                this.nextChar();
                currentChar = this.nextChar();
            }
            if (currentChar == -1) {
                return null;
            }
            if (currentChar == 34 && !singleQuoted) {
                doubleQuoted = !doubleQuoted;
                continue;
            }
            if (currentChar == 39 && !doubleQuoted) {
                singleQuoted = !singleQuoted;
                continue;
            }
            if (currentChar == 123 && !doubleQuoted && !singleQuoted) {
                ++nesting;
                continue;
            }
            if (currentChar != 125 || doubleQuoted || singleQuoted) continue;
            --nesting;
        } while (currentChar != 125 || singleQuoted || doubleQuoted || nesting > -1);
        return last;
    }

    final boolean isSpace() {
        return this.peekChar() <= 32;
    }

    String parseToken(boolean quoted) throws JasperException {
        StringBuilder StringBuilder2 = new StringBuilder();
        this.skipSpaces();
        StringBuilder2.setLength(0);
        if (!this.hasMoreInput()) {
            return "";
        }
        int ch = this.peekChar();
        if (quoted) {
            if (ch == 34 || ch == 39) {
                int endQuote = ch == 34 ? 34 : 39;
                ch = this.nextChar();
                ch = this.nextChar();
                while (ch != -1 && ch != endQuote) {
                    if (ch == 92) {
                        ch = this.nextChar();
                    }
                    StringBuilder2.append((char)ch);
                    ch = this.nextChar();
                }
                if (ch == -1) {
                    this.err.jspError(this.mark(), "jsp.error.quotes.unterminated", new String[0]);
                }
            } else {
                this.err.jspError(this.mark(), "jsp.error.attr.quoted", new String[0]);
            }
        } else if (!this.isDelimiter()) {
            do {
                if ((ch = this.nextChar()) == 92 && (this.peekChar() == 34 || this.peekChar() == 39 || this.peekChar() == 62 || this.peekChar() == 37)) {
                    ch = this.nextChar();
                }
                StringBuilder2.append((char)ch);
            } while (!this.isDelimiter());
        }
        return StringBuilder2.toString();
    }

    private boolean isDelimiter() {
        if (!this.isSpace()) {
            int ch = this.peekChar();
            if (ch == 61 || ch == 62 || ch == 34 || ch == 39 || ch == 47) {
                return true;
            }
            if (ch == 45) {
                Mark mark = this.mark();
                ch = this.nextChar();
                if (ch == 62 || ch == 45 && this.nextChar() == 62) {
                    this.setCurrent(mark);
                    return true;
                }
                this.setCurrent(mark);
                return false;
            }
            return false;
        }
        return true;
    }
}

