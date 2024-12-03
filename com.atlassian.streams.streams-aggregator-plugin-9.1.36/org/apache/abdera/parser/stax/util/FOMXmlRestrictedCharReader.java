/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.apache.abdera.parser.stax.util.FOMXmlVersionInputStream;
import org.apache.abdera.parser.stax.util.FOMXmlVersionReader;
import org.apache.abdera.util.XmlRestrictedCharReader;
import org.apache.abdera.util.XmlUtil;

public final class FOMXmlRestrictedCharReader
extends XmlRestrictedCharReader {
    public FOMXmlRestrictedCharReader(Reader in) {
        this(new FOMXmlVersionReader(in));
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionReader in) {
        super((Reader)in, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(Reader in, char replacement) {
        this(new FOMXmlVersionReader(in), replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionReader in, char replacement) {
        super((Reader)in, XmlUtil.getVersion(in.getVersion()), replacement);
    }

    public FOMXmlRestrictedCharReader(InputStream in) {
        this(new FOMXmlVersionInputStream(in));
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in) {
        super((InputStream)in, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(InputStream in, char replacement) {
        this(new FOMXmlVersionInputStream(in), replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, char replacement) {
        super((InputStream)in, XmlUtil.getVersion(in.getVersion()), replacement);
    }

    public FOMXmlRestrictedCharReader(InputStream in, String charset) throws UnsupportedEncodingException {
        this(new FOMXmlVersionInputStream(in), charset);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, String charset) throws UnsupportedEncodingException {
        super((InputStream)in, charset, XmlUtil.getVersion(in.getVersion()));
    }

    public FOMXmlRestrictedCharReader(InputStream in, String charset, char replacement) throws UnsupportedEncodingException {
        this(new FOMXmlVersionInputStream(in), charset, replacement);
    }

    public FOMXmlRestrictedCharReader(FOMXmlVersionInputStream in, String charset, char replacement) throws UnsupportedEncodingException {
        super((InputStream)in, charset, XmlUtil.getVersion(in.getVersion()), replacement);
    }
}

