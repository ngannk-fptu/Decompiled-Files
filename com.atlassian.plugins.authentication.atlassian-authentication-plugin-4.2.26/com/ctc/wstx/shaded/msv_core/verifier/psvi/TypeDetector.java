/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.psvi;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.ErrorInfo;
import com.ctc.wstx.shaded.msv_core.verifier.ValidityViolation;
import com.ctc.wstx.shaded.msv_core.verifier.Verifier;
import com.ctc.wstx.shaded.msv_core.verifier.psvi.TypedContentHandler;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class TypeDetector
extends Verifier {
    private StringBuffer text = new StringBuffer();
    protected TypedContentHandler handler;
    private final DatatypeRef characterType = new DatatypeRef();

    public TypeDetector(DocumentDeclaration documentDecl, ErrorHandler errorHandler) {
        super(documentDecl, errorHandler);
    }

    public TypeDetector(DocumentDeclaration documentDecl, TypedContentHandler handler, ErrorHandler errorHandler) {
        this(documentDecl, errorHandler);
        this.setContentHandler(handler);
    }

    public void setContentHandler(TypedContentHandler handler) {
        this.handler = handler;
    }

    protected void verifyText() throws SAXException {
        if (this.text.length() != 0) {
            String txt = new String(this.text);
            if (!this.current.onText2(txt, this, null, this.characterType)) {
                StringRef err = new StringRef();
                this.current.onText2(txt, this, err, null);
                this.errorHandler.error(new ValidityViolation(this.locator, TypeDetector.localizeMessage("Verifier.Error.UnexpectedText", null), new ErrorInfo.BadText(txt)));
            }
            this.reportCharacterChunks(txt, this.characterType.types);
            this.text = new StringBuffer();
        }
    }

    private void reportCharacterChunks(String text, Datatype[] types) throws SAXException {
        if (types == null) {
            throw new AmbiguousDocumentException();
        }
        switch (types.length) {
            case 0: {
                return;
            }
            case 1: {
                this.handler.characterChunk(text, types[0]);
                return;
            }
        }
        StringTokenizer tokens = new StringTokenizer(text);
        for (int i = 0; i < types.length; ++i) {
            this.handler.characterChunk(tokens.nextToken(), types[i]);
        }
        if (tokens.hasMoreTokens()) {
            throw new Error();
        }
    }

    protected Datatype[] feedAttribute(Acceptor child, String uri, String localName, String qName, String value) throws SAXException {
        this.handler.startAttribute(uri, localName, qName);
        Datatype[] result = super.feedAttribute(child, uri, localName, qName, value);
        this.reportCharacterChunks(value, result);
        this.handler.endAttribute(uri, localName, qName, ((REDocumentDeclaration)this.docDecl).attToken.matchedExp);
        return result;
    }

    public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(namespaceUri, localName, qName, atts);
        this.handler.endAttributePart();
    }

    protected void onNextAcceptorReady(StartTagInfo sti, Acceptor nextAcceptor) throws SAXException {
        this.handler.startElement(sti.namespaceURI, sti.localName, sti.qName);
    }

    public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
        ElementExp type;
        Acceptor child = this.current;
        super.endElement(namespaceUri, localName, qName);
        if (child instanceof SimpleAcceptor) {
            type = ((SimpleAcceptor)child).owner;
        } else if (child instanceof ComplexAcceptor) {
            ElementExp[] exps = ((ComplexAcceptor)child).getSatisfiedOwners();
            if (exps.length != 1) {
                throw new AmbiguousDocumentException();
            }
            type = exps[0];
        } else {
            throw new Error();
        }
        this.handler.endElement(namespaceUri, localName, qName, type);
    }

    public void characters(char[] buf, int start, int len) throws SAXException {
        this.text.append(buf, start, len);
    }

    public void ignorableWhitespace(char[] buf, int start, int len) throws SAXException {
        this.text.append(buf, start, len);
    }

    public void startDocument() throws SAXException {
        super.startDocument();
        this.handler.startDocument(this);
    }

    public void endDocument() throws SAXException {
        super.endDocument();
        this.handler.endDocument();
    }

    public class AmbiguousDocumentException
    extends SAXException {
        public AmbiguousDocumentException() {
            super("");
        }

        Locator getLocation() {
            return TypeDetector.this.getLocator();
        }
    }
}

