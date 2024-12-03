/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.LocatorImpl;
import org.xml.sax.helpers.NamespaceSupport;

public abstract class AbstractVerifier
implements ContentHandler,
DTDHandler,
IDContextProvider2 {
    protected Locator locator = dummyLocator;
    protected static final Locator dummyLocator = new LocatorImpl();
    protected boolean performIDcheck = true;
    protected final Map ids = new HashMap();
    protected final Set idrefs = new HashSet();
    private boolean contextPushed = false;
    protected final NamespaceSupport namespaceSupport = new NamespaceSupport();
    private final Set unparsedEntities = new HashSet();
    private final Set notations = new HashSet();

    public final Locator getLocator() {
        return this.locator;
    }

    public void setDocumentLocator(Locator loc) {
        this.locator = loc;
    }

    public void skippedEntity(String p) {
    }

    public void processingInstruction(String name, String data) {
    }

    public void startPrefixMapping(String prefix, String uri) {
        if (!this.contextPushed) {
            this.namespaceSupport.pushContext();
            this.contextPushed = true;
        }
        this.namespaceSupport.declarePrefix(prefix, uri);
    }

    public void endPrefixMapping(String prefix) {
    }

    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        if (!this.contextPushed) {
            this.namespaceSupport.pushContext();
        }
        this.contextPushed = false;
    }

    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        this.namespaceSupport.popContext();
    }

    protected void init() {
        this.ids.clear();
        this.idrefs.clear();
    }

    public void notationDecl(String name, String publicId, String systemId) {
        this.notations.add(name);
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) {
        this.unparsedEntities.add(name);
    }

    public String resolveNamespacePrefix(String prefix) {
        String uri = this.namespaceSupport.getURI(prefix);
        if (uri == null && prefix.length() == 0) {
            return "";
        }
        return uri;
    }

    public boolean isUnparsedEntity(String entityName) {
        return this.unparsedEntities.contains(entityName);
    }

    public boolean isNotation(String notationName) {
        return this.notations.contains(notationName);
    }

    public String getBaseUri() {
        return null;
    }

    protected abstract void onDuplicateId(String var1);

    public void onID(Datatype dt, StringToken token) {
        if (!this.performIDcheck) {
            return;
        }
        int idType = dt.getIdType();
        if (idType == 1) {
            String literal = token.literal.trim();
            StringToken existing = (StringToken)this.ids.get(literal);
            if (existing == null) {
                this.ids.put(literal, token);
            } else if (existing != token) {
                this.onDuplicateId(literal);
            }
            return;
        }
        if (idType == 2) {
            this.idrefs.add(token.literal.trim());
            return;
        }
        if (idType == 3) {
            StringTokenizer tokens = new StringTokenizer(token.literal);
            while (tokens.hasMoreTokens()) {
                this.idrefs.add(tokens.nextToken());
            }
            return;
        }
        throw new Error();
    }
}

