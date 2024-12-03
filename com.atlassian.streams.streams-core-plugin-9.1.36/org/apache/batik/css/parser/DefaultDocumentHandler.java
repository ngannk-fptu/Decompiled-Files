/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

public class DefaultDocumentHandler
implements DocumentHandler {
    public static final DocumentHandler INSTANCE = new DefaultDocumentHandler();

    protected DefaultDocumentHandler() {
    }

    @Override
    public void startDocument(InputSource source) throws CSSException {
    }

    @Override
    public void endDocument(InputSource source) throws CSSException {
    }

    @Override
    public void comment(String text) throws CSSException {
    }

    @Override
    public void ignorableAtRule(String atRule) throws CSSException {
    }

    @Override
    public void namespaceDeclaration(String prefix, String uri) throws CSSException {
    }

    @Override
    public void importStyle(String uri, SACMediaList media, String defaultNamespaceURI) throws CSSException {
    }

    @Override
    public void startMedia(SACMediaList media) throws CSSException {
    }

    @Override
    public void endMedia(SACMediaList media) throws CSSException {
    }

    @Override
    public void startPage(String name, String pseudo_page) throws CSSException {
    }

    @Override
    public void endPage(String name, String pseudo_page) throws CSSException {
    }

    @Override
    public void startFontFace() throws CSSException {
    }

    @Override
    public void endFontFace() throws CSSException {
    }

    @Override
    public void startSelector(SelectorList selectors) throws CSSException {
    }

    @Override
    public void endSelector(SelectorList selectors) throws CSSException {
    }

    @Override
    public void property(String name, LexicalUnit value, boolean important) throws CSSException {
    }
}

