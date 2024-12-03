/*
 * Decompiled with CFR 0.152.
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.jar.JarFile;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.JspUtil;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;

class JspReader {
    private Log log = LogFactory.getLog(JspReader.class);
    private Mark current;
    private String master;
    private List sourceFiles;
    private int currFileId;
    private int size;
    private JspCompilationContext context;
    private ErrorDispatcher err;
    private boolean singleFile;

    public JspReader(JspCompilationContext ctxt, String fname, String encoding, JarFile jarFile, ErrorDispatcher err) throws JasperException, FileNotFoundException, IOException {
        this(ctxt, fname, encoding, JspUtil.getReader(fname, encoding, jarFile, ctxt, err), err);
    }

    public JspReader(JspCompilationContext ctxt, String fname, String encoding, InputStreamReader reader, ErrorDispatcher err) throws JasperException, FileNotFoundException {
        this.context = ctxt;
        this.err = err;
        this.sourceFiles = new Vector();
        this.currFileId = 0;
        this.size = 0;
        this.singleFile = false;
        this.pushFile(fname, encoding, reader);
    }

    JspCompilationContext getJspCompilationContext() {
        return this.context;
    }

    String getFile(int fileid) {
        return (String)this.sourceFiles.get(fileid);
    }

    boolean hasMoreInput() throws JasperException {
        if (this.current.cursor >= this.current.stream.length) {
            if (this.singleFile) {
                return false;
            }
            while (this.popFile()) {
                if (this.current.cursor >= this.current.stream.length) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    int nextChar() throws JasperException {
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

    void pushChar() {
        --this.current.cursor;
        --this.current.col;
    }

    String getText(Mark start, Mark stop) throws JasperException {
        Mark oldstart = this.mark();
        this.reset(start);
        CharArrayWriter caw = new CharArrayWriter();
        while (!stop.equals(this.mark())) {
            caw.write(this.nextChar());
        }
        caw.close();
        this.reset(oldstart);
        return caw.toString();
    }

    int peekChar() throws JasperException {
        if (!this.hasMoreInput()) {
            return -1;
        }
        return this.current.stream[this.current.cursor];
    }

    Mark mark() {
        return new Mark(this.current);
    }

    void reset(Mark mark) {
        this.current = new Mark(mark);
    }

    boolean matchesIgnoreCase(String string) throws JasperException {
        Mark mark = this.mark();
        int ch = 0;
        int i = 0;
        do {
            if (Character.toLowerCase((char)(ch = this.nextChar())) == string.charAt(i++)) continue;
            this.reset(mark);
            return false;
        } while (i < string.length());
        this.reset(mark);
        return true;
    }

    boolean matches(String string) throws JasperException {
        Mark mark = this.mark();
        int ch = 0;
        int i = 0;
        do {
            if ((char)(ch = this.nextChar()) == string.charAt(i++)) continue;
            this.reset(mark);
            return false;
        } while (i < string.length());
        return true;
    }

    boolean matchesETag(String tagName) throws JasperException {
        Mark mark = this.mark();
        if (!this.matches("</" + tagName)) {
            return false;
        }
        this.skipSpaces();
        if (this.nextChar() == 62) {
            return true;
        }
        this.reset(mark);
        return false;
    }

    boolean matchesETagWithoutLessThan(String tagName) throws JasperException {
        Mark mark = this.mark();
        if (!this.matches("/" + tagName)) {
            return false;
        }
        this.skipSpaces();
        if (this.nextChar() == 62) {
            return true;
        }
        this.reset(mark);
        return false;
    }

    boolean matchesOptionalSpacesFollowedBy(String s) throws JasperException {
        Mark mark = this.mark();
        this.skipSpaces();
        boolean result = this.matches(s);
        if (!result) {
            this.reset(mark);
        }
        return result;
    }

    int skipSpaces() throws JasperException {
        int i = 0;
        while (this.hasMoreInput() && this.isSpace()) {
            ++i;
            this.nextChar();
        }
        return i;
    }

    Mark skipUntil(String limit) throws JasperException {
        Mark ret = null;
        int limlen = limit.length();
        ret = this.mark();
        int ch = this.nextChar();
        while (ch != -1) {
            block4: {
                if (ch == limit.charAt(0)) {
                    Mark restart = this.mark();
                    for (int i = 1; i < limlen; ++i) {
                        if (this.peekChar() != limit.charAt(i)) {
                            this.reset(restart);
                            break block4;
                        }
                        this.nextChar();
                    }
                    return ret;
                }
            }
            ret = this.mark();
            ch = this.nextChar();
        }
        return null;
    }

    Mark skipUntilIgnoreEsc(String limit) throws JasperException {
        Mark ret = null;
        int limlen = limit.length();
        char prev = 'x';
        ret = this.mark();
        char ch = this.nextChar();
        while (ch != '\uffffffff') {
            block6: {
                if (ch == '\\' && prev == '\\') {
                    ch = '\u0000';
                } else if (ch == limit.charAt(0) && prev != '\\') {
                    for (int i = 1; i < limlen; ++i) {
                        if (this.peekChar() == limit.charAt(i)) {
                            this.nextChar();
                            continue;
                        }
                        break block6;
                    }
                    return ret;
                }
            }
            ret = this.mark();
            prev = ch;
            ch = this.nextChar();
        }
        return null;
    }

    Mark skipUntilETag(String tag) throws JasperException {
        Mark ret = this.skipUntil("</" + tag);
        if (ret != null) {
            this.skipSpaces();
            if (this.nextChar() != 62) {
                ret = null;
            }
        }
        return ret;
    }

    final boolean isSpace() throws JasperException {
        return this.peekChar() <= 32;
    }

    String parseToken(boolean quoted) throws JasperException {
        StringBuffer stringBuffer = new StringBuffer();
        this.skipSpaces();
        stringBuffer.setLength(0);
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
                    stringBuffer.append((char)ch);
                    ch = this.nextChar();
                }
                if (ch == -1) {
                    this.err.jspError(this.mark(), "jsp.error.quotes.unterminated");
                }
            } else {
                this.err.jspError(this.mark(), "jsp.error.attr.quoted");
            }
        } else if (!this.isDelimiter()) {
            do {
                if ((ch = this.nextChar()) == 92 && (this.peekChar() == 34 || this.peekChar() == 39 || this.peekChar() == 62 || this.peekChar() == 37)) {
                    ch = this.nextChar();
                }
                stringBuffer.append((char)ch);
            } while (!this.isDelimiter());
        }
        return stringBuffer.toString();
    }

    void setSingleFile(boolean val) {
        this.singleFile = val;
    }

    URL getResource(String path) throws MalformedURLException {
        return this.context.getResource(path);
    }

    private boolean isDelimiter() throws JasperException {
        if (!this.isSpace()) {
            int ch = this.peekChar();
            if (ch == 61 || ch == 62 || ch == 34 || ch == 39 || ch == 47) {
                return true;
            }
            if (ch == 45) {
                Mark mark = this.mark();
                ch = this.nextChar();
                if (ch == 62 || ch == 45 && this.nextChar() == 62) {
                    this.reset(mark);
                    return true;
                }
                this.reset(mark);
                return false;
            }
            return false;
        }
        return true;
    }

    private int registerSourceFile(String file) {
        if (this.sourceFiles.contains(file)) {
            return -1;
        }
        this.sourceFiles.add(file);
        ++this.size;
        return this.sourceFiles.size() - 1;
    }

    private int unregisterSourceFile(String file) {
        if (!this.sourceFiles.contains(file)) {
            return -1;
        }
        this.sourceFiles.remove(file);
        --this.size;
        return this.sourceFiles.size() - 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void pushFile(String file, String encoding, InputStreamReader reader) throws JasperException, FileNotFoundException {
        String longName = file;
        int fileid = this.registerSourceFile(longName);
        if (fileid == -1) {
            block19: {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception any) {
                        if (!this.log.isDebugEnabled()) break block19;
                        this.log.debug("Exception closing reader: ", any);
                    }
                }
            }
            this.err.jspError("jsp.error.file.already.registered", file);
        }
        this.currFileId = fileid;
        try {
            CharArrayWriter caw = new CharArrayWriter();
            char[] buf = new char[1024];
            int i = 0;
            while ((i = reader.read(buf)) != -1) {
                caw.write(buf, 0, i);
            }
            caw.close();
            if (this.current == null) {
                this.current = new Mark(this, caw.toCharArray(), fileid, this.getFile(fileid), this.master, encoding);
            } else {
                this.current.pushStream(caw.toCharArray(), fileid, this.getFile(fileid), longName, encoding);
            }
        }
        catch (Throwable ex) {
            this.log.error("Exception parsing file ", ex);
            this.popFile();
            this.err.jspError("jsp.error.file.cannot.read", file);
        }
        finally {
            block21: {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception any) {
                        if (!this.log.isDebugEnabled()) break block21;
                        this.log.debug("Exception closing reader: ", any);
                    }
                }
            }
        }
    }

    private boolean popFile() throws JasperException {
        Mark previous;
        if (this.current == null || this.currFileId < 0) {
            return false;
        }
        String fName = this.getFile(this.currFileId);
        this.currFileId = this.unregisterSourceFile(fName);
        if (this.currFileId < -1) {
            this.err.jspError("jsp.error.file.not.registered", fName);
        }
        if ((previous = this.current.popStream()) != null) {
            this.master = this.current.baseDir;
            this.current = previous;
            return true;
        }
        return false;
    }
}

