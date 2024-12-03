/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import org.apache.xml.utils.StopParseException;
import org.apache.xml.utils.SystemIDResolver;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StylesheetPIHandler
extends DefaultHandler {
    String m_baseID;
    String m_media;
    String m_title;
    String m_charset;
    Vector m_stylesheets = new Vector();
    URIResolver m_uriResolver;

    public void setURIResolver(URIResolver resolver) {
        this.m_uriResolver = resolver;
    }

    public URIResolver getURIResolver() {
        return this.m_uriResolver;
    }

    public StylesheetPIHandler(String baseID, String media, String title, String charset) {
        this.m_baseID = baseID;
        this.m_media = media;
        this.m_title = title;
        this.m_charset = charset;
    }

    public Source getAssociatedStylesheet() {
        int sz = this.m_stylesheets.size();
        if (sz > 0) {
            Source source = (Source)this.m_stylesheets.elementAt(sz - 1);
            return source;
        }
        return null;
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if (target.equals("xml-stylesheet")) {
            String href = null;
            String type = null;
            String title = null;
            String media = null;
            String charset = null;
            boolean alternate = false;
            StringTokenizer tokenizer = new StringTokenizer(data, " \t=\n", true);
            boolean lookedAhead = false;
            Source source = null;
            String token = "";
            while (tokenizer.hasMoreTokens()) {
                if (!lookedAhead) {
                    token = tokenizer.nextToken();
                } else {
                    lookedAhead = false;
                }
                if (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) continue;
                String name = token;
                if (name.equals("type")) {
                    token = tokenizer.nextToken();
                    while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                        token = tokenizer.nextToken();
                    }
                    type = token.substring(1, token.length() - 1);
                    continue;
                }
                if (name.equals("href")) {
                    token = tokenizer.nextToken();
                    while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                        token = tokenizer.nextToken();
                    }
                    href = token;
                    if (tokenizer.hasMoreTokens()) {
                        token = tokenizer.nextToken();
                        while (token.equals("=") && tokenizer.hasMoreTokens()) {
                            href = href + token + tokenizer.nextToken();
                            if (!tokenizer.hasMoreTokens()) break;
                            token = tokenizer.nextToken();
                            lookedAhead = true;
                        }
                    }
                    href = href.substring(1, href.length() - 1);
                    try {
                        if (this.m_uriResolver != null) {
                            source = this.m_uriResolver.resolve(href, this.m_baseID);
                            continue;
                        }
                        href = SystemIDResolver.getAbsoluteURI(href, this.m_baseID);
                        source = new SAXSource(new InputSource(href));
                        continue;
                    }
                    catch (TransformerException te) {
                        throw new SAXException(te);
                    }
                }
                if (name.equals("title")) {
                    token = tokenizer.nextToken();
                    while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                        token = tokenizer.nextToken();
                    }
                    title = token.substring(1, token.length() - 1);
                    continue;
                }
                if (name.equals("media")) {
                    token = tokenizer.nextToken();
                    while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                        token = tokenizer.nextToken();
                    }
                    media = token.substring(1, token.length() - 1);
                    continue;
                }
                if (name.equals("charset")) {
                    token = tokenizer.nextToken();
                    while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                        token = tokenizer.nextToken();
                    }
                    charset = token.substring(1, token.length() - 1);
                    continue;
                }
                if (!name.equals("alternate")) continue;
                token = tokenizer.nextToken();
                while (tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("="))) {
                    token = tokenizer.nextToken();
                }
                alternate = token.substring(1, token.length() - 1).equals("yes");
            }
            if (null != type && (type.equals("text/xsl") || type.equals("text/xml") || type.equals("application/xml+xslt")) && null != href) {
                if (null != this.m_media) {
                    if (null != media) {
                        if (!media.equals(this.m_media)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (null != this.m_charset) {
                    if (null != charset) {
                        if (!charset.equals(this.m_charset)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (null != this.m_title) {
                    if (null != title) {
                        if (!title.equals(this.m_title)) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                this.m_stylesheets.addElement(source);
            }
        }
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        throw new StopParseException();
    }

    public void setBaseId(String baseId) {
        this.m_baseID = baseId;
    }

    public String getBaseId() {
        return this.m_baseID;
    }
}

