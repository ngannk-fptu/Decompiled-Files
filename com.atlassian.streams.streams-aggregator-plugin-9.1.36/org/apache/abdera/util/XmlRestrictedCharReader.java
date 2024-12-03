/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.apache.abdera.i18n.text.io.FilteredCharReader;
import org.apache.abdera.util.XmlUtil;

public class XmlRestrictedCharReader
extends FilteredCharReader {
    public XmlRestrictedCharReader(InputStream in) {
        this(new InputStreamReader(in));
    }

    public XmlRestrictedCharReader(InputStream in, String charset) throws UnsupportedEncodingException {
        this(new InputStreamReader(in, charset));
    }

    public XmlRestrictedCharReader(InputStream in, XmlUtil.XMLVersion version) {
        this((Reader)new InputStreamReader(in), version);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, XmlUtil.XMLVersion version) throws UnsupportedEncodingException {
        this((Reader)new InputStreamReader(in, charset), version);
    }

    public XmlRestrictedCharReader(InputStream in, char replacement) {
        this((Reader)new InputStreamReader(in), replacement);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, char replacement) throws UnsupportedEncodingException {
        this((Reader)new InputStreamReader(in, charset), replacement);
    }

    public XmlRestrictedCharReader(InputStream in, XmlUtil.XMLVersion version, char replacement) {
        this((Reader)new InputStreamReader(in), version, replacement);
    }

    public XmlRestrictedCharReader(InputStream in, String charset, XmlUtil.XMLVersion version, char replacement) throws UnsupportedEncodingException {
        this((Reader)new InputStreamReader(in, charset), version, replacement);
    }

    public XmlRestrictedCharReader(Reader in) {
        this(in, XmlUtil.XMLVersion.XML10, '\u0000');
    }

    public XmlRestrictedCharReader(Reader in, XmlUtil.XMLVersion version) {
        this(in, version, '\u0000');
    }

    public XmlRestrictedCharReader(Reader in, char replacement) {
        this(in, XmlUtil.XMLVersion.XML10, replacement);
    }

    public XmlRestrictedCharReader(Reader in, XmlUtil.XMLVersion version, char replacement) {
        super(in, version.filter(), replacement);
    }
}

