/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.bedework.util.xml.XmlEmitNamespaces;

public class XmlEmit {
    private Writer wtr;
    private boolean forHtml = false;
    private boolean noHeaders = false;
    private String dtd;
    private boolean started;
    private XmlEmitNamespaces nameSpaces = new XmlEmitNamespaces();
    private Notifier notifier;
    private Properties props;
    int indent;
    private String blank = "                                                                              ";
    private int blankLen = this.blank.length();
    private static final int bufferSize = 4096;

    public XmlEmit() {
        this(false);
    }

    public XmlEmit(boolean noHeaders) {
        this.noHeaders = noHeaders;
    }

    public static XmlEmit getHtmlEmitter() {
        XmlEmit xml = new XmlEmit();
        xml.forHtml = true;
        return xml;
    }

    public void setProperty(String name, String val) {
        if (this.props == null) {
            this.props = new Properties();
        }
        this.props.setProperty(name, val);
    }

    public String getProperty(String name) {
        if (this.props == null) {
            return null;
        }
        return this.props.getProperty(name);
    }

    public void startEmit(Writer wtr) throws IOException {
        this.wtr = wtr;
    }

    public void startEmit(Writer wtr, String dtd) throws IOException {
        this.wtr = wtr;
        this.dtd = dtd;
    }

    public void setNotifier(Notifier n) {
        this.notifier = n;
    }

    public void openTag(QName tag) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.newline();
        this.indent += 2;
    }

    public void openTag(QName tag, String attrName, String attrVal) throws IOException {
        this.blanks();
        this.openTagSameLine(tag, attrName, attrVal);
        this.newline();
        this.indent += 2;
    }

    public void openTagNoNewline(QName tag) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.indent += 2;
    }

    public void openTagNoNewline(QName tag, String attrName, String attrVal) throws IOException {
        this.blanks();
        this.openTagSameLine(tag, attrName, attrVal);
        this.indent += 2;
    }

    public void openTagSameLine(QName tag) throws IOException {
        this.lb();
        this.emitQName(tag);
        this.endOpeningTag();
    }

    public void openTagSameLine(QName tag, String attrName, String attrVal) throws IOException {
        this.lb();
        this.emitQName(tag);
        this.attribute(attrName, attrVal);
        this.endOpeningTag();
    }

    public void startTag(QName tag) throws IOException {
        this.blanks();
        this.startTagSameLine(tag);
    }

    public void startTagIndent(QName tag) throws IOException {
        this.blanks();
        this.startTagSameLine(tag);
        this.indent += 2;
    }

    public void startTagSameLine(QName tag) throws IOException {
        this.lb();
        this.emitQName(tag);
    }

    public void endOpeningTag() throws IOException {
        this.scopeIn();
        this.rb();
    }

    public void attribute(String attrName, String attrVal) throws IOException {
        this.out(" ");
        this.out(attrName);
        this.out("=");
        this.quote(attrVal);
    }

    public void attribute(QName attr, String attrVal) throws IOException {
        this.out(" ");
        this.emitQName(attr);
        this.out("=");
        this.quote(attrVal);
        this.emitNs();
    }

    public void closeTag(QName tag) throws IOException {
        this.indent -= 2;
        if (this.indent < 0) {
            this.indent = 0;
        }
        this.blanks();
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void closeTagNoblanks(QName tag) throws IOException {
        this.indent -= 2;
        if (this.indent < 0) {
            this.indent = 0;
        }
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void closeTagSameLine(QName tag) throws IOException {
        this.lb();
        this.out("/");
        this.emitQName(tag);
        this.rb();
        this.scopeOut();
    }

    public void endEmptyTag() throws IOException {
        this.out(" /");
        this.rb();
    }

    public void emptyTag(QName tag) throws IOException {
        this.blanks();
        this.emptyTagSameLine(tag);
        this.newline();
    }

    public void emptyTag(QName tag, String attrName, String attrVal) throws IOException {
        this.blanks();
        this.lb();
        this.emitQName(tag);
        this.attribute(attrName, attrVal);
        this.out("/");
        this.rb();
        this.newline();
    }

    public void emptyTagSameLine(QName tag) throws IOException {
        this.lb();
        this.emitQName(tag);
        this.out("/");
        this.rb();
    }

    public void property(QName tag, String val) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.value(val);
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void cdataProperty(QName tag, String val) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.cdataValue(val);
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void property(QName tag, Reader val) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.writeContent(val, this.wtr);
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void propertyTagVal(QName tag, QName tagVal) throws IOException {
        this.blanks();
        this.openTagSameLine(tag);
        this.emptyTagSameLine(tagVal);
        this.closeTagSameLine(tag);
        this.newline();
    }

    public void cdataValue(String val) throws IOException {
        if (val == null) {
            return;
        }
        int start = 0;
        while (start < val.length()) {
            int end = val.indexOf("]]", start);
            boolean lastSeg = end < 0;
            String seg = lastSeg ? val.substring(start) : val.substring(start, end);
            this.out("<![CDATA[");
            this.out(seg);
            this.out("]]>");
            if (lastSeg) break;
            this.out("]]");
            start = end + 2;
        }
    }

    public void value(String val) throws IOException {
        this.value(val, null);
    }

    private void value(String val, String quoteChar) throws IOException {
        if (val == null) {
            return;
        }
        String q = quoteChar;
        if (q == null) {
            q = "";
        }
        if (val.indexOf(38) >= 0 || val.indexOf(60) >= 0) {
            this.out("<![CDATA[");
            this.out(q);
            this.out(val);
            this.out(q);
            this.out("]]>");
        } else {
            this.out(q);
            this.out(val);
            this.out(q);
        }
    }

    public Writer getWriter() {
        return this.wtr;
    }

    public void flush() throws IOException {
        this.wtr.flush();
    }

    public void addNs(NameSpace val, boolean makeDefaultNs) throws IOException {
        this.nameSpaces.addNs(val, makeDefaultNs);
    }

    public NameSpace getNameSpace(String ns) {
        return this.nameSpaces.getNameSpace(ns);
    }

    public String getNsAbbrev(String ns) {
        return this.nameSpaces.getNsAbbrev(ns);
    }

    public void newline() throws IOException {
        this.out("\n");
    }

    private void quote(String val) throws IOException {
        if (val.indexOf("\"") < 0) {
            this.value(val, "\"");
        } else {
            this.value(val, "'");
        }
    }

    private void emitQName(QName tag) throws IOException {
        this.nameSpaces.emitNsAbbr(tag.getNamespaceURI(), this.wtr);
        this.out(tag.getLocalPart());
        this.emitNs();
    }

    private void emitNs() throws IOException {
        if (this.forHtml) {
            return;
        }
        this.nameSpaces.emitNs(this.wtr);
    }

    private void blanks() throws IOException {
        if (this.indent >= this.blankLen) {
            this.out(this.blank);
        } else {
            this.out(this.blank.substring(0, this.indent));
        }
    }

    private void lb() throws IOException {
        this.out("<");
    }

    private void rb() throws IOException {
        this.out(">");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeContent(Reader in, Writer out) throws IOException {
        try {
            int len;
            char[] buff = new char[4096];
            while ((len = in.read(buff)) >= 0) {
                out.write(buff, 0, len);
            }
        }
        finally {
            try {
                in.close();
            }
            catch (Throwable throwable) {}
            try {
                out.close();
            }
            catch (Throwable throwable) {}
        }
    }

    private void scopeIn() {
        this.nameSpaces.startScope();
    }

    private void scopeOut() {
        this.nameSpaces.endScope();
    }

    private void out(String val) throws IOException {
        if (this.notifier != null && this.notifier.isEnabled()) {
            try {
                this.notifier.doNotification();
            }
            catch (IOException ioe) {
                throw ioe;
            }
            catch (Throwable t) {
                throw new IOException(t);
            }
        }
        if (!this.started) {
            this.started = true;
            if (!this.noHeaders) {
                this.writeHeader(this.dtd);
                this.wtr.write("\n");
            }
        }
        this.wtr.write(val);
    }

    private void writeHeader(String dtd) throws IOException {
        if (this.forHtml) {
            this.wtr.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            return;
        }
        this.wtr.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        if (dtd == null) {
            return;
        }
        this.wtr.write("<!DOCTYPE properties SYSTEM \"");
        this.wtr.write(dtd);
        this.wtr.write("\">\n");
    }

    public static class NameSpace {
        String ns;
        String abbrev;
        int level;
        boolean defaultNs;

        public NameSpace(String ns, String abbrev) {
            this.ns = ns;
            this.abbrev = abbrev;
        }

        public int hashCode() {
            return this.ns.hashCode();
        }

        public boolean equals(Object o) {
            NameSpace that = (NameSpace)o;
            return this.ns.equals(that.ns);
        }
    }

    public static abstract class Notifier {
        public abstract void doNotification() throws Throwable;

        public abstract boolean isEnabled();
    }
}

