/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDocumentProperties;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.GlobalLock;
import org.apache.xmlbeans.impl.common.XMLChar;
import org.apache.xmlbeans.impl.store.Bookmark;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.DomSaver;
import org.apache.xmlbeans.impl.store.Jsr173;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.Saver;
import org.apache.xmlbeans.impl.store.Xobj;
import org.apache.xmlbeans.impl.xpath.XPathEngine;
import org.apache.xmlbeans.impl.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class Cursor
implements XmlCursor,
Locale.ChangeListener {
    static final int ROOT = 1;
    static final int ELEM = 2;
    static final int ATTR = 3;
    static final int COMMENT = 4;
    static final int PROCINST = 5;
    static final int TEXT = 0;
    private Cur _cur;
    private XPathEngine _pathEngine;
    private int _currentSelection;
    private Locale.ChangeListener _nextChangeListener;
    private static final int MOVE_XML = 0;
    private static final int COPY_XML = 1;
    private static final int MOVE_XML_CONTENTS = 2;
    private static final int COPY_XML_CONTENTS = 3;
    private static final int MOVE_CHARS = 4;
    private static final int COPY_CHARS = 5;

    Cursor(Xobj x, int p) {
        this._cur = x._locale.weakCur(this);
        this._cur.moveTo(x, p);
        this._currentSelection = -1;
    }

    public Cursor(Cur c) {
        this(c._xobj, c._pos);
    }

    private static boolean isValid(Cur c) {
        if (c.kind() <= 0) {
            int pk;
            c.push();
            if (c.toParentRaw() && ((pk = c.kind()) == 4 || pk == 5 || pk == 3)) {
                return false;
            }
            c.pop();
        }
        return true;
    }

    private boolean isValid() {
        return Cursor.isValid(this._cur);
    }

    Locale locale() {
        return this._cur._locale;
    }

    Cur tempCur() {
        return this._cur.tempCur();
    }

    public void dump(PrintStream o) {
        this._cur.dump(o);
    }

    static void validateLocalName(QName name) {
        if (name == null) {
            throw new IllegalArgumentException("QName is null");
        }
        Cursor.validateLocalName(name.getLocalPart());
    }

    static void validateLocalName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Name is empty");
        }
        if (!XMLChar.isValidNCName(name)) {
            throw new IllegalArgumentException("Name is not valid");
        }
    }

    static void validatePrefix(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Prefix is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("Prefix is empty");
        }
        if (Locale.beginsWithXml(name)) {
            throw new IllegalArgumentException("Prefix begins with 'xml'");
        }
        if (!XMLChar.isValidNCName(name)) {
            throw new IllegalArgumentException("Prefix is not valid");
        }
    }

    private static void complain(String msg) {
        throw new IllegalArgumentException(msg);
    }

    private void checkInsertionValidity(Cur that) {
        int thisKind;
        int thatKind = that.kind();
        if (thatKind < 0) {
            Cursor.complain("Can't move/copy/insert an end token.");
        }
        if (thatKind == 1) {
            Cursor.complain("Can't move/copy/insert a whole document.");
        }
        if ((thisKind = this._cur.kind()) == 1) {
            Cursor.complain("Can't insert before the start of the document.");
        }
        if (thatKind == 3) {
            this._cur.push();
            this._cur.prevWithAttrs();
            int pk = this._cur.kind();
            this._cur.pop();
            if (pk != 2 && pk != 1 && pk != -3) {
                Cursor.complain("Can only insert attributes before other attributes or after containers.");
            }
        }
        if (thisKind == 3 && thatKind != 3) {
            Cursor.complain("Can only insert attributes before other attributes or after containers.");
        }
    }

    private void insertNode(Cur that, String text) {
        assert (!that.isRoot());
        assert (that.isNode());
        assert (Cursor.isValid(that));
        assert (this.isValid());
        if (text != null && text.length() > 0) {
            that.next();
            that.insertString(text);
            that.toParent();
        }
        this.checkInsertionValidity(that);
        that.moveNode(this._cur);
        this._cur.toEnd();
        this._cur.nextWithAttrs();
    }

    public void _dispose() {
        this._cur.release();
        this._cur = null;
    }

    public XmlCursor _newCursor() {
        return new Cursor(this._cur);
    }

    public QName _getName() {
        switch (this._cur.kind()) {
            case 3: {
                if (this._cur.isXmlns()) {
                    return this._cur._locale.makeQNameNoCheck(this._cur.getXmlnsUri(), this._cur.getXmlnsPrefix());
                }
            }
            case 2: 
            case 5: {
                return this._cur.getName();
            }
        }
        return null;
    }

    public void _setName(QName name) {
        if (name == null) {
            throw new IllegalArgumentException("Name is null");
        }
        switch (this._cur.kind()) {
            case 2: 
            case 3: {
                Cursor.validateLocalName(name.getLocalPart());
                break;
            }
            case 5: {
                Cursor.validatePrefix(name.getLocalPart());
                if (name.getNamespaceURI().length() > 0) {
                    throw new IllegalArgumentException("Procinst name must have no URI");
                }
                if (name.getPrefix().length() <= 0) break;
                throw new IllegalArgumentException("Procinst name must have no prefix");
            }
            default: {
                throw new IllegalStateException("Can set name on element, atrtribute and procinst only");
            }
        }
        this._cur.setName(name);
    }

    public XmlCursor.TokenType _currentTokenType() {
        assert (this.isValid());
        switch (this._cur.kind()) {
            case 1: {
                return XmlCursor.TokenType.STARTDOC;
            }
            case -1: {
                return XmlCursor.TokenType.ENDDOC;
            }
            case 2: {
                return XmlCursor.TokenType.START;
            }
            case -2: {
                return XmlCursor.TokenType.END;
            }
            case 0: {
                return XmlCursor.TokenType.TEXT;
            }
            case 3: {
                return this._cur.isXmlns() ? XmlCursor.TokenType.NAMESPACE : XmlCursor.TokenType.ATTR;
            }
            case 4: {
                return XmlCursor.TokenType.COMMENT;
            }
            case 5: {
                return XmlCursor.TokenType.PROCINST;
            }
        }
        throw new IllegalStateException();
    }

    public boolean _isStartdoc() {
        assert (this.isValid());
        return this._cur.isRoot();
    }

    public boolean _isEnddoc() {
        assert (this.isValid());
        return this._cur.isEndRoot();
    }

    public boolean _isStart() {
        assert (this.isValid());
        return this._cur.isElem();
    }

    public boolean _isEnd() {
        assert (this.isValid());
        return this._cur.isEnd();
    }

    public boolean _isText() {
        assert (this.isValid());
        return this._cur.isText();
    }

    public boolean _isAttr() {
        assert (this.isValid());
        return this._cur.isNormalAttr();
    }

    public boolean _isNamespace() {
        assert (this.isValid());
        return this._cur.isXmlns();
    }

    public boolean _isComment() {
        assert (this.isValid());
        return this._cur.isComment();
    }

    public boolean _isProcinst() {
        assert (this.isValid());
        return this._cur.isProcinst();
    }

    public boolean _isContainer() {
        assert (this.isValid());
        return this._cur.isContainer();
    }

    public boolean _isFinish() {
        assert (this.isValid());
        return this._cur.isFinish();
    }

    public boolean _isAnyAttr() {
        assert (this.isValid());
        return this._cur.isAttr();
    }

    public XmlCursor.TokenType _toNextToken() {
        assert (this.isValid());
        switch (this._cur.kind()) {
            case 1: 
            case 2: {
                if (this._cur.toFirstAttr()) break;
                this._cur.next();
                break;
            }
            case 3: {
                if (this._cur.toNextSibling()) break;
                this._cur.toParent();
                this._cur.next();
                break;
            }
            case 4: 
            case 5: {
                this._cur.skip();
                break;
            }
            default: {
                if (this._cur.next()) break;
                return XmlCursor.TokenType.NONE;
            }
        }
        return this._currentTokenType();
    }

    public XmlCursor.TokenType _toPrevToken() {
        assert (this.isValid());
        boolean wasText = this._cur.isText();
        if (!this._cur.prev()) {
            assert (this._cur.isRoot() || this._cur.isAttr());
            if (this._cur.isRoot()) {
                return XmlCursor.TokenType.NONE;
            }
            this._cur.toParent();
        } else {
            int k = this._cur.kind();
            if (k == -4 || k == -5 || k == -3) {
                this._cur.toParent();
            } else if (this._cur.isContainer()) {
                this._cur.toLastAttr();
            } else if (wasText && this._cur.isText()) {
                return this._toPrevToken();
            }
        }
        return this._currentTokenType();
    }

    public Object _monitor() {
        return this._cur._locale;
    }

    public boolean _toParent() {
        Cur c = this._cur.tempCur();
        if (!c.toParent()) {
            return false;
        }
        this._cur.moveToCur(c);
        c.release();
        return true;
    }

    public XmlCursor.ChangeStamp _getDocChangeStamp() {
        return new ChangeStampImpl(this._cur._locale);
    }

    public XMLStreamReader _newXMLStreamReader() {
        return this._newXMLStreamReader(null);
    }

    public Node _newDomNode() {
        return this._newDomNode(null);
    }

    public InputStream _newInputStream() {
        return this._newInputStream(null);
    }

    public String _xmlText() {
        return this._xmlText(null);
    }

    public Reader _newReader() {
        return this._newReader(null);
    }

    public void _save(File file) throws IOException {
        this._save(file, null);
    }

    public void _save(OutputStream os) throws IOException {
        this._save(os, null);
    }

    public void _save(Writer w) throws IOException {
        this._save(w, null);
    }

    public void _save(ContentHandler ch, LexicalHandler lh) throws SAXException {
        this._save(ch, lh, null);
    }

    public XmlDocumentProperties _documentProperties() {
        return Locale.getDocProps(this._cur, true);
    }

    public XMLStreamReader _newXMLStreamReader(XmlOptions options) {
        return Jsr173.newXmlStreamReader(this._cur, options);
    }

    public String _xmlText(XmlOptions options) {
        assert (this.isValid());
        return new Saver.TextSaver(this._cur, options, null).saveToString();
    }

    public InputStream _newInputStream(XmlOptions options) {
        return new Saver.InputStreamSaver(this._cur, options);
    }

    public Reader _newReader(XmlOptions options) {
        return new Saver.TextReader(this._cur, options);
    }

    public void _save(ContentHandler ch, LexicalHandler lh, XmlOptions options) throws SAXException {
        new Saver.SaxSaver(this._cur, options, ch, lh);
    }

    public void _save(File file, XmlOptions options) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Null file specified");
        }
        try (FileOutputStream os = new FileOutputStream(file);){
            this._save(os, options);
        }
    }

    public void _save(OutputStream os, XmlOptions options) throws IOException {
        if (os == null) {
            throw new IllegalArgumentException("Null OutputStream specified");
        }
        try (InputStream is = this._newInputStream(options);){
            int n;
            byte[] bytes = new byte[8192];
            while ((n = is.read(bytes)) >= 0) {
                os.write(bytes, 0, n);
            }
        }
    }

    public void _save(Writer w, XmlOptions options) throws IOException {
        if (w == null) {
            throw new IllegalArgumentException("Null Writer specified");
        }
        if (options != null && options.isSaveOptimizeForSpeed()) {
            Saver.OptimizedForSpeedSaver.save(this._cur, w);
            return;
        }
        try (Reader r = this._newReader(options);){
            int n;
            char[] chars = new char[8192];
            while ((n = r.read(chars)) >= 0) {
                w.write(chars, 0, n);
            }
        }
    }

    public Node _getDomNode() {
        return (Node)((Object)this._cur.getDom());
    }

    /*
     * Unable to fully structure code
     */
    private boolean isDomFragment() {
        block34: {
            if (!this.isStartdoc()) {
                return true;
            }
            seenElement = false;
            c = this.newCursor();
            var3_3 = null;
            try {
                token = c.toNextToken().intValue();
                block26: while (true) {
                    switch (token) {
                        case 3: {
                            if (seenElement) {
                                var5_7 = true;
                                return var5_7;
                            }
                            seenElement = true;
                            token = c.toEndToken().intValue();
                            ** GOTO lbl36
                        }
                        case 5: {
                            if (!Locale.isWhiteSpace(c.getChars())) {
                                var5_8 = true;
                                return var5_8;
                            }
                            token = c.toNextToken().intValue();
                            ** GOTO lbl36
                        }
                        case 0: 
                        case 2: {
                            ** break;
lbl25:
                            // 1 sources

                            break block34;
                        }
                        case 6: 
                        case 7: {
                            var5_9 = true;
                            return var5_9;
                        }
                        case 4: 
                        case 8: 
                        case 9: {
                            token = c.toNextToken().intValue();
                            ** GOTO lbl36
                        }
                        case 1: {
                            if (!Cursor.$assertionsDisabled) {
                                throw new AssertionError();
                            }
                            break block34;
                        }
lbl36:
                        // 4 sources

                        default: {
                            continue block26;
                        }
                    }
                    break;
                }
            }
            catch (Throwable var4_6) {
                var3_3 = var4_6;
                throw var4_6;
            }
            finally {
                if (c != null) {
                    if (var3_3 != null) {
                        try {
                            c.close();
                        }
                        catch (Throwable var6_10) {
                            var3_3.addSuppressed(var6_10);
                        }
                    } else {
                        c.close();
                    }
                }
            }
        }
        return seenElement == false;
    }

    public Node _newDomNode(XmlOptions options) {
        if (options != null && options.isSaveInner()) {
            options = new XmlOptions(options);
            options.setSaveInner(false);
        }
        return new DomSaver(this._cur, this.isDomFragment(), options).saveDom();
    }

    public boolean _toCursor(Cursor other) {
        assert (this._cur._locale == other._cur._locale);
        this._cur.moveToCur(other._cur);
        return true;
    }

    public void _push() {
        this._cur.push();
    }

    public boolean _pop() {
        return this._cur.pop();
    }

    @Override
    public void notifyChange() {
        if (this._cur != null) {
            this._getSelectionCount();
        }
    }

    @Override
    public void setNextChangeListener(Locale.ChangeListener listener) {
        this._nextChangeListener = listener;
    }

    @Override
    public Locale.ChangeListener getNextChangeListener() {
        return this._nextChangeListener;
    }

    public void _selectPath(String path) {
        this._selectPath(path, null);
    }

    public void _selectPath(String pathExpr, XmlOptions options) {
        this._clearSelections();
        assert (this._pathEngine == null);
        this._pathEngine = XPathFactory.getCompiledPath(pathExpr, options).execute(this._cur, options);
        this._cur._locale.registerForChange(this);
    }

    public boolean _hasNextSelection() {
        int curr = this._currentSelection;
        this.push();
        try {
            boolean bl = this._toNextSelection();
            return bl;
        }
        finally {
            this._currentSelection = curr;
            this.pop();
        }
    }

    public boolean _toNextSelection() {
        return this._toSelection(this._currentSelection + 1);
    }

    public boolean _toSelection(int i) {
        if (i < 0) {
            return false;
        }
        while (i >= this._cur.selectionCount()) {
            if (this._pathEngine == null) {
                return false;
            }
            if (this._pathEngine.next(this._cur)) continue;
            this._pathEngine.release();
            this._pathEngine = null;
            return false;
        }
        this._currentSelection = i;
        this._cur.moveToSelection(this._currentSelection);
        return true;
    }

    public int _getSelectionCount() {
        this._toSelection(Integer.MAX_VALUE);
        return this._cur.selectionCount();
    }

    public void _addToSelection() {
        this._toSelection(Integer.MAX_VALUE);
        this._cur.addToSelection();
    }

    public void _clearSelections() {
        if (this._pathEngine != null) {
            this._pathEngine.release();
            this._pathEngine = null;
        }
        this._cur.clearSelection();
        this._currentSelection = -1;
    }

    public String _namespaceForPrefix(String prefix) {
        if (!this._cur.isContainer()) {
            throw new IllegalStateException("Not on a container");
        }
        return this._cur.namespaceForPrefix(prefix, true);
    }

    public String _prefixForNamespace(String ns) {
        if (ns == null || ns.length() == 0) {
            throw new IllegalArgumentException("Must specify a namespace");
        }
        return this._cur.prefixForNamespace(ns, null, true);
    }

    public void _getAllNamespaces(Map<String, String> addToThis) {
        if (!this._cur.isContainer()) {
            throw new IllegalStateException("Not on a container");
        }
        if (addToThis != null) {
            Locale.getAllNamespaces(this._cur, addToThis);
        }
    }

    public XmlObject _getObject() {
        return this._cur.getObject();
    }

    public XmlCursor.TokenType _prevTokenType() {
        this._cur.push();
        XmlCursor.TokenType tt = this._toPrevToken();
        this._cur.pop();
        return tt;
    }

    public boolean _hasNextToken() {
        return this._cur._pos != -1 || this._cur._xobj.kind() != 1;
    }

    public boolean _hasPrevToken() {
        return this._cur.kind() != 1;
    }

    public XmlCursor.TokenType _toFirstContentToken() {
        if (!this._cur.isContainer()) {
            return XmlCursor.TokenType.NONE;
        }
        this._cur.next();
        return this.currentTokenType();
    }

    public XmlCursor.TokenType _toEndToken() {
        if (!this._cur.isContainer()) {
            return XmlCursor.TokenType.NONE;
        }
        this._cur.toEnd();
        return this.currentTokenType();
    }

    public boolean _toChild(String local) {
        return this._toChild(null, local);
    }

    public boolean _toChild(QName name) {
        return this._toChild(name, 0);
    }

    public boolean _toChild(int index) {
        return this._toChild(null, index);
    }

    public boolean _toChild(String uri, String local) {
        Cursor.validateLocalName(local);
        return this._toChild(this._cur._locale.makeQName(uri, local), 0);
    }

    public boolean _toChild(QName name, int index) {
        return Locale.toChild(this._cur, name, index);
    }

    public int _toNextChar(int maxCharacterCount) {
        return this._cur.nextChars(maxCharacterCount);
    }

    public int _toPrevChar(int maxCharacterCount) {
        return this._cur.prevChars(maxCharacterCount);
    }

    public boolean _toPrevSibling() {
        return Locale.toPrevSiblingElement(this._cur);
    }

    public boolean _toLastChild() {
        return Locale.toLastChildElement(this._cur);
    }

    public boolean _toFirstChild() {
        return Locale.toFirstChildElement(this._cur);
    }

    public boolean _toNextSibling(String name) {
        return this._toNextSibling(new QName(name));
    }

    public boolean _toNextSibling(String uri, String local) {
        Cursor.validateLocalName(local);
        return this._toNextSibling(this._cur._locale._qnameFactory.getQName(uri, local));
    }

    public boolean _toNextSibling(QName name) {
        this._cur.push();
        while (this.___toNextSibling()) {
            if (!this._cur.getName().equals(name)) continue;
            this._cur.popButStay();
            return true;
        }
        this._cur.pop();
        return false;
    }

    public boolean _toFirstAttribute() {
        return this._cur.isContainer() && Locale.toFirstNormalAttr(this._cur);
    }

    public boolean _toLastAttribute() {
        if (this._cur.isContainer()) {
            this._cur.push();
            this._cur.push();
            boolean foundAttr = false;
            while (this._cur.toNextAttr()) {
                if (!this._cur.isNormalAttr()) continue;
                this._cur.popButStay();
                this._cur.push();
                foundAttr = true;
            }
            this._cur.pop();
            if (foundAttr) {
                this._cur.popButStay();
                return true;
            }
            this._cur.pop();
        }
        return false;
    }

    public boolean _toNextAttribute() {
        return this._cur.isAttr() && Locale.toNextNormalAttr(this._cur);
    }

    public boolean _toPrevAttribute() {
        return this._cur.isAttr() && Locale.toPrevNormalAttr(this._cur);
    }

    public String _getAttributeText(QName attrName) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        if (!this._cur.isContainer()) {
            return null;
        }
        return this._cur.getAttrValue(attrName);
    }

    public boolean _setAttributeText(QName attrName, String value) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        Cursor.validateLocalName(attrName.getLocalPart());
        if (!this._cur.isContainer()) {
            return false;
        }
        this._cur.setAttrValue(attrName, value);
        return true;
    }

    public boolean _removeAttribute(QName attrName) {
        if (attrName == null) {
            throw new IllegalArgumentException("Attr name is null");
        }
        if (!this._cur.isContainer()) {
            return false;
        }
        return this._cur.removeAttr(attrName);
    }

    public String _getTextValue() {
        if (this._cur.isText()) {
            return this._getChars();
        }
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't get text value, current token can have no text value");
        }
        return this._cur.hasChildren() ? Locale.getTextValue(this._cur) : this._cur.getValueAsString();
    }

    public int _getTextValue(char[] chars, int offset, int max) {
        if (this._cur.isText()) {
            return this._getChars(chars, offset, max);
        }
        if (chars == null) {
            throw new IllegalArgumentException("char buffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset < 0");
        }
        if (offset >= chars.length) {
            throw new IllegalArgumentException("offset off end");
        }
        if (max < 0) {
            max = Integer.MAX_VALUE;
        }
        if (offset + max > chars.length) {
            max = chars.length - offset;
        }
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't get text value, current token can have no text value");
        }
        if (this._cur.hasChildren()) {
            return Locale.getTextValue(this._cur, chars, offset, max);
        }
        Object src = this._cur.getFirstChars();
        if (this._cur._cchSrc > max) {
            this._cur._cchSrc = max;
        }
        if (this._cur._cchSrc <= 0) {
            return 0;
        }
        CharUtil.getChars(chars, offset, src, this._cur._offSrc, this._cur._cchSrc);
        return this._cur._cchSrc;
    }

    private void setTextValue(Object src, int off, int cch) {
        if (!this._cur.isNode()) {
            throw new IllegalStateException("Can't set text value, current token can have no text value");
        }
        this._cur.moveNodeContents(null, false);
        this._cur.next();
        this._cur.insertChars(src, off, cch);
        this._cur.toParent();
    }

    public void _setTextValue(String text) {
        if (text == null) {
            text = "";
        }
        this.setTextValue(text, 0, text.length());
    }

    public void _setTextValue(char[] sourceChars, int offset, int length) {
        if (length < 0) {
            throw new IndexOutOfBoundsException("setTextValue: length < 0");
        }
        if (sourceChars == null) {
            if (length > 0) {
                throw new IllegalArgumentException("setTextValue: sourceChars == null");
            }
            this.setTextValue(null, 0, 0);
            return;
        }
        if (offset < 0 || offset >= sourceChars.length) {
            throw new IndexOutOfBoundsException("setTextValue: offset out of bounds");
        }
        if (offset + length > sourceChars.length) {
            length = sourceChars.length - offset;
        }
        CharUtil cu = this._cur._locale.getCharUtil();
        this.setTextValue(cu.saveChars(sourceChars, offset, length), cu._offSrc, cu._cchSrc);
    }

    public String _getChars() {
        return this._cur.getCharsAsString();
    }

    public int _getChars(char[] buf, int off, int cch) {
        int cchRight = this._cur.cchRight();
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        if (buf == null || off >= buf.length) {
            return 0;
        }
        if (buf.length - off < cch) {
            cch = buf.length - off;
        }
        Object src = this._cur.getChars(cch);
        CharUtil.getChars(buf, off, src, this._cur._offSrc, this._cur._cchSrc);
        return this._cur._cchSrc;
    }

    public void _toStartDoc() {
        this._cur.toRoot();
    }

    public void _toEndDoc() {
        this._toStartDoc();
        this._cur.toEnd();
    }

    public int _comparePosition(Cursor other) {
        int s = this._cur.comparePosition(other._cur);
        if (s == 2) {
            throw new IllegalArgumentException("Cursors not in same document");
        }
        assert (s >= -1 && s <= 1);
        return s;
    }

    public boolean _isLeftOf(Cursor other) {
        return this._comparePosition(other) < 0;
    }

    public boolean _isAtSamePositionAs(Cursor other) {
        return this._cur.isSamePos(other._cur);
    }

    public boolean _isRightOf(Cursor other) {
        return this._comparePosition(other) > 0;
    }

    public XmlCursor _execQuery(String query) {
        return this._execQuery(query, null);
    }

    public XmlCursor _execQuery(String query, XmlOptions options) {
        this.checkThisCursor();
        return XPathFactory.cursorExecQuery(this._cur, query, options);
    }

    public boolean _toBookmark(XmlCursor.XmlBookmark bookmark) {
        if (bookmark == null || !(bookmark._currentMark instanceof Bookmark)) {
            return false;
        }
        Bookmark m = (Bookmark)bookmark._currentMark;
        if (m._xobj == null || m._xobj._locale != this._cur._locale) {
            return false;
        }
        this._cur.moveTo(m._xobj, m._pos);
        return true;
    }

    public XmlCursor.XmlBookmark _toNextBookmark(Object key) {
        if (key == null) {
            return null;
        }
        this._cur.push();
        do {
            int cch;
            if ((cch = this._cur.cchRight()) > 1) {
                this._cur.nextChars(1);
                cch = this._cur.firstBookmarkInChars(key, cch - 1);
                this._cur.nextChars(cch >= 0 ? cch : -1);
            } else if (this._toNextToken().isNone()) {
                this._cur.pop();
                return null;
            }
            XmlCursor.XmlBookmark bm = Cursor.getBookmark(key, this._cur);
            if (bm == null) continue;
            this._cur.popButStay();
            return bm;
        } while (this._cur.kind() != -1);
        this._cur.pop();
        return null;
    }

    public XmlCursor.XmlBookmark _toPrevBookmark(Object key) {
        if (key == null) {
            return null;
        }
        this._cur.push();
        do {
            int cch;
            if ((cch = this._cur.cchLeft()) > 1) {
                this._cur.prevChars(1);
                cch = this._cur.firstBookmarkInCharsLeft(key, cch - 1);
                this._cur.prevChars(cch >= 0 ? cch : -1);
            } else if (cch == 1) {
                this._cur.prevChars(1);
            } else if (this._toPrevToken().isNone()) {
                this._cur.pop();
                return null;
            }
            XmlCursor.XmlBookmark bm = Cursor.getBookmark(key, this._cur);
            if (bm == null) continue;
            this._cur.popButStay();
            return bm;
        } while (this._cur.kind() != 1);
        this._cur.pop();
        return null;
    }

    public void _setBookmark(XmlCursor.XmlBookmark bookmark) {
        if (bookmark != null) {
            if (bookmark.getKey() == null) {
                throw new IllegalArgumentException("Annotation key is null");
            }
            bookmark._currentMark = this._cur.setBookmark(bookmark.getKey(), bookmark);
        }
    }

    static XmlCursor.XmlBookmark getBookmark(Object key, Cur c) {
        if (key == null) {
            return null;
        }
        Object bm = c.getBookmark(key);
        return bm instanceof XmlCursor.XmlBookmark ? (XmlCursor.XmlBookmark)bm : null;
    }

    public XmlCursor.XmlBookmark _getBookmark(Object key) {
        return key == null ? null : Cursor.getBookmark(key, this._cur);
    }

    public void _clearBookmark(Object key) {
        if (key != null) {
            this._cur.setBookmark(key, null);
        }
    }

    public void _getAllBookmarkRefs(Collection<Object> listToFill) {
        if (listToFill != null) {
            Bookmark b = this._cur._xobj._bookmarks;
            while (b != null) {
                if (b._value instanceof XmlCursor.XmlBookmark) {
                    listToFill.add(b._value);
                }
                b = b._next;
            }
        }
    }

    public boolean _removeXml() {
        if (this._cur.isRoot()) {
            throw new IllegalStateException("Can't remove a whole document.");
        }
        if (this._cur.isFinish()) {
            return false;
        }
        assert (this._cur.isText() || this._cur.isNode());
        if (this._cur.isText()) {
            this._cur.moveChars(null, -1);
        } else {
            this._cur.moveNode(null);
        }
        return true;
    }

    public boolean _moveXml(Cursor to) {
        to.checkInsertionValidity(this._cur);
        if (this._cur.isText()) {
            int cchRight = this._cur.cchRight();
            assert (cchRight > 0);
            if (this._cur.inChars(to._cur, cchRight, true)) {
                return false;
            }
            this._cur.moveChars(to._cur, cchRight);
            to._cur.nextChars(cchRight);
            return true;
        }
        if (this._cur.contains(to._cur)) {
            return false;
        }
        Cur c = to.tempCur();
        this._cur.moveNode(to._cur);
        to._cur.moveToCur(c);
        c.release();
        return true;
    }

    public boolean _copyXml(Cursor to) {
        to.checkInsertionValidity(this._cur);
        assert (this._cur.isText() || this._cur.isNode());
        Cur c = to.tempCur();
        if (this._cur.isText()) {
            to._cur.insertChars(this._cur.getChars(-1), this._cur._offSrc, this._cur._cchSrc);
        } else {
            this._cur.copyNode(to._cur);
        }
        to._cur.moveToCur(c);
        c.release();
        return true;
    }

    public boolean _removeXmlContents() {
        if (!this._cur.isContainer()) {
            return false;
        }
        this._cur.moveNodeContents(null, false);
        return true;
    }

    private boolean checkContentInsertionValidity(Cursor to) {
        this._cur.push();
        this._cur.next();
        if (this._cur.isFinish()) {
            this._cur.pop();
            return false;
        }
        try {
            to.checkInsertionValidity(this._cur);
        }
        catch (IllegalArgumentException e) {
            this._cur.pop();
            throw e;
        }
        this._cur.pop();
        return true;
    }

    public boolean _moveXmlContents(Cursor to) {
        if (!this._cur.isContainer() || this._cur.contains(to._cur)) {
            return false;
        }
        if (!this.checkContentInsertionValidity(to)) {
            return false;
        }
        Cur c = to.tempCur();
        this._cur.moveNodeContents(to._cur, false);
        to._cur.moveToCur(c);
        c.release();
        return true;
    }

    public boolean _copyXmlContents(Cursor to) {
        if (!this._cur.isContainer() || this._cur.contains(to._cur)) {
            return false;
        }
        if (!this.checkContentInsertionValidity(to)) {
            return false;
        }
        Cur c = this._cur._locale.tempCur();
        this._cur.copyNode(c);
        Cur c2 = to._cur.tempCur();
        c.moveNodeContents(to._cur, false);
        c.release();
        to._cur.moveToCur(c2);
        c2.release();
        return true;
    }

    public int _removeChars(int cch) {
        int cchRight = this._cur.cchRight();
        if (cchRight == 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        this._cur.moveChars(null, cch);
        return this._cur._cchSrc;
    }

    public int _moveChars(int cch, Cursor to) {
        int cchRight = this._cur.cchRight();
        if (cchRight <= 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        to.checkInsertionValidity(this._cur);
        this._cur.moveChars(to._cur, cch);
        to._cur.nextChars(this._cur._cchSrc);
        return this._cur._cchSrc;
    }

    public int _copyChars(int cch, Cursor to) {
        int cchRight = this._cur.cchRight();
        if (cchRight <= 0 || cch == 0) {
            return 0;
        }
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        to.checkInsertionValidity(this._cur);
        to._cur.insertChars(this._cur.getChars(cch), this._cur._offSrc, this._cur._cchSrc);
        to._cur.nextChars(this._cur._cchSrc);
        return this._cur._cchSrc;
    }

    public void _insertChars(String text) {
        int l;
        int n = l = text == null ? 0 : text.length();
        if (l > 0) {
            if (this._cur.isRoot() || this._cur.isAttr()) {
                throw new IllegalStateException("Can't insert before the document or an attribute.");
            }
            this._cur.insertChars(text, 0, l);
            this._cur.nextChars(l);
        }
    }

    public void _beginElement(String localName) {
        this._insertElementWithText(localName, null, null);
        this._toPrevToken();
    }

    public void _beginElement(String localName, String uri) {
        this._insertElementWithText(localName, uri, null);
        this._toPrevToken();
    }

    public void _beginElement(QName name) {
        this._insertElementWithText(name, null);
        this._toPrevToken();
    }

    public void _insertElement(String localName) {
        this._insertElementWithText(localName, null, null);
    }

    public void _insertElement(String localName, String uri) {
        this._insertElementWithText(localName, uri, null);
    }

    public void _insertElement(QName name) {
        this._insertElementWithText(name, null);
    }

    public void _insertElementWithText(String localName, String text) {
        this._insertElementWithText(localName, null, text);
    }

    public void _insertElementWithText(String localName, String uri, String text) {
        Cursor.validateLocalName(localName);
        this._insertElementWithText(this._cur._locale.makeQName(uri, localName), text);
    }

    public void _insertElementWithText(QName name, String text) {
        Cursor.validateLocalName(name.getLocalPart());
        Cur c = this._cur._locale.tempCur();
        c.createElement(name);
        this.insertNode(c, text);
        c.release();
    }

    public void _insertAttribute(String localName) {
        this._insertAttributeWithValue(localName, null);
    }

    public void _insertAttribute(String localName, String uri) {
        this._insertAttributeWithValue(localName, uri, null);
    }

    public void _insertAttribute(QName name) {
        this._insertAttributeWithValue(name, null);
    }

    public void _insertAttributeWithValue(String localName, String value) {
        this._insertAttributeWithValue(localName, null, value);
    }

    public void _insertAttributeWithValue(String localName, String uri, String value) {
        Cursor.validateLocalName(localName);
        this._insertAttributeWithValue(this._cur._locale.makeQName(uri, localName), value);
    }

    public void _insertAttributeWithValue(QName name, String text) {
        if (name == null) {
            throw new IllegalArgumentException("QName must not be null");
        }
        Cursor.validateLocalName(name.getLocalPart());
        Cur c = this._cur._locale.tempCur();
        c.createAttr(name);
        this.insertNode(c, text);
        c.release();
    }

    public void _insertNamespace(String prefix, String namespace) {
        this._insertAttributeWithValue(this._cur._locale.createXmlns(prefix), namespace);
    }

    public void _insertComment(String text) {
        Cur c = this._cur._locale.tempCur();
        c.createComment();
        this.insertNode(c, text);
        c.release();
    }

    public void _insertProcInst(String target, String text) {
        Cursor.validateLocalName(target);
        if (Locale.beginsWithXml(target) && target.length() == 3) {
            throw new IllegalArgumentException("Target is 'xml'");
        }
        Cur c = this._cur._locale.tempCur();
        c.createProcinst(target);
        this.insertNode(c, text);
        c.release();
    }

    public void _dump() {
        this._cur.dump();
    }

    private void checkThisCursor() {
        if (this._cur == null) {
            throw new IllegalStateException("This cursor has been disposed");
        }
    }

    private Cursor checkCursors(XmlCursor xOther) {
        this.checkThisCursor();
        if (xOther == null) {
            throw new IllegalArgumentException("Other cursor is <null>");
        }
        if (!(xOther instanceof Cursor)) {
            throw new IllegalArgumentException("Incompatible cursors: " + xOther);
        }
        Cursor other = (Cursor)xOther;
        if (other._cur == null) {
            throw new IllegalStateException("Other cursor has been disposed");
        }
        return other;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int twoLocaleOp(XmlCursor xOther, int op, int arg) {
        Cursor other = this.checkCursors(xOther);
        Locale locale = this._cur._locale;
        Locale otherLocale = other._cur._locale;
        if (locale == otherLocale) {
            return this.syncWrapNoEnter(() -> this.twoLocaleOp(other, op, arg));
        }
        if (locale.noSync()) {
            if (otherLocale.noSync()) {
                return this.twoLocaleOp(other, op, arg);
            }
            Locale locale2 = otherLocale;
            synchronized (locale2) {
                return this.twoLocaleOp(other, op, arg);
            }
        }
        if (otherLocale.noSync()) {
            Locale locale3 = locale;
            synchronized (locale3) {
                return this.twoLocaleOp(other, op, arg);
            }
        }
        boolean acquired = false;
        try {
            GlobalLock.acquire();
            acquired = true;
            Locale locale4 = locale;
            synchronized (locale4) {
                Locale locale5 = otherLocale;
                synchronized (locale5) {
                    try {
                        GlobalLock.release();
                        acquired = false;
                        int n = this.twoLocaleOp(other, op, arg);
                        return n;
                    }
                    catch (Throwable throwable) {
                        try {
                            throw throwable;
                        }
                        catch (InterruptedException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        finally {
            if (acquired) {
                GlobalLock.release();
            }
        }
    }

    private int twoLocaleOp(Cursor other, int op, int arg) {
        Locale locale = this._cur._locale;
        Locale otherLocale = other._cur._locale;
        locale.enter(otherLocale);
        try {
            switch (op) {
                case 0: {
                    int n = this._moveXml(other) ? 1 : 0;
                    return n;
                }
                case 1: {
                    int n = this._copyXml(other) ? 1 : 0;
                    return n;
                }
                case 2: {
                    int n = this._moveXmlContents(other) ? 1 : 0;
                    return n;
                }
                case 3: {
                    int n = this._copyXmlContents(other) ? 1 : 0;
                    return n;
                }
                case 4: {
                    int n = this._moveChars(arg, other);
                    return n;
                }
                case 5: {
                    int n = this._copyChars(arg, other);
                    return n;
                }
            }
            throw new RuntimeException("Unknown operation: " + op);
        }
        finally {
            locale.exit(otherLocale);
        }
    }

    @Override
    public boolean moveXml(XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 0, 0) == 1;
    }

    @Override
    public boolean copyXml(XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 1, 0) == 1;
    }

    @Override
    public boolean moveXmlContents(XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 2, 0) == 1;
    }

    @Override
    public boolean copyXmlContents(XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 3, 0) == 1;
    }

    @Override
    public int moveChars(int cch, XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 4, cch);
    }

    @Override
    public int copyChars(int cch, XmlCursor xTo) {
        return this.twoLocaleOp(xTo, 5, cch);
    }

    @Override
    public boolean toCursor(XmlCursor xOther) {
        Cursor other = this.checkCursors(xOther);
        return this._cur._locale == other._cur._locale && this.syncWrap(() -> this._toCursor(other)) != false;
    }

    @Override
    public boolean isInSameDocument(XmlCursor xOther) {
        return xOther != null && this._cur.isInSameTree(this.checkCursors((XmlCursor)xOther)._cur);
    }

    private Cursor preCheck(XmlCursor xOther) {
        Cursor other = this.checkCursors(xOther);
        if (this._cur._locale != other._cur._locale) {
            throw new IllegalArgumentException("Cursors not in same document");
        }
        return other;
    }

    @Override
    public int comparePosition(XmlCursor xOther) {
        Cursor other = this.preCheck(xOther);
        return this.syncWrap(() -> this._comparePosition(other));
    }

    @Override
    public boolean isLeftOf(XmlCursor xOther) {
        Cursor other = this.preCheck(xOther);
        return this.syncWrap(() -> this._isLeftOf(other));
    }

    @Override
    public boolean isAtSamePositionAs(XmlCursor xOther) {
        Cursor other = this.preCheck(xOther);
        return this.syncWrap(() -> this._isAtSamePositionAs(other));
    }

    @Override
    public boolean isRightOf(XmlCursor xOther) {
        Cursor other = this.preCheck(xOther);
        return this.syncWrap(() -> this._isRightOf(other));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XmlCursor newCursor(Xobj x, int p) {
        Locale l = x._locale;
        if (l.noSync()) {
            l.enter();
            try {
                Cursor cursor = new Cursor(x, p);
                return cursor;
            }
            finally {
                l.exit();
            }
        }
        Locale locale = l;
        synchronized (locale) {
            Cursor cursor;
            l.enter();
            try {
                cursor = new Cursor(x, p);
                l.exit();
            }
            catch (Throwable throwable) {
                l.exit();
                throw throwable;
            }
            return cursor;
        }
    }

    private boolean preCheck() {
        this.checkThisCursor();
        return this._cur._locale.noSync();
    }

    @Override
    public void close() {
        if (this._cur != null) {
            this.syncWrap(this::_dispose);
        }
    }

    @Override
    @Deprecated
    public void dispose() {
        this.close();
    }

    @Override
    public Object monitor() {
        return this.syncWrap(this::_monitor);
    }

    @Override
    public XmlDocumentProperties documentProperties() {
        return this.syncWrap(this::_documentProperties);
    }

    @Override
    public XmlCursor newCursor() {
        return this.syncWrap(this::_newCursor);
    }

    @Override
    public XMLStreamReader newXMLStreamReader() {
        return this.syncWrap(this::_newXMLStreamReader);
    }

    @Override
    public XMLStreamReader newXMLStreamReader(XmlOptions options) {
        return this.syncWrap(() -> this._newXMLStreamReader(options));
    }

    @Override
    public String xmlText() {
        return this.syncWrap(this::_xmlText);
    }

    @Override
    public InputStream newInputStream() {
        return this.syncWrap(this::_newInputStream);
    }

    @Override
    public Reader newReader() {
        return this.syncWrap(this::_newReader);
    }

    @Override
    public Node newDomNode() {
        return this.syncWrap(this::_newDomNode);
    }

    @Override
    public Node getDomNode() {
        return this.syncWrap(this::_getDomNode);
    }

    @Override
    public void save(ContentHandler ch, LexicalHandler lh) throws SAXException {
        this.syncWrapSAXEx(() -> this._save(ch, lh));
    }

    @Override
    public void save(File file) throws IOException {
        this.syncWrapIOEx(() -> this._save(file));
    }

    @Override
    public void save(OutputStream os) throws IOException {
        this.syncWrapIOEx(() -> this._save(os));
    }

    @Override
    public void save(Writer w) throws IOException {
        this.syncWrapIOEx(() -> this._save(w));
    }

    @Override
    public String xmlText(XmlOptions options) {
        return this.syncWrap(() -> this._xmlText(options));
    }

    @Override
    public InputStream newInputStream(XmlOptions options) {
        return this.syncWrap(() -> this._newInputStream(options));
    }

    @Override
    public Reader newReader(XmlOptions options) {
        return this.syncWrap(() -> this._newReader(options));
    }

    @Override
    public Node newDomNode(XmlOptions options) {
        return this.syncWrap(() -> this._newDomNode(options));
    }

    @Override
    public void save(ContentHandler ch, LexicalHandler lh, XmlOptions options) throws SAXException {
        this.syncWrapSAXEx(() -> this._save(ch, lh, options));
    }

    @Override
    public void save(File file, XmlOptions options) throws IOException {
        this.syncWrapIOEx(() -> this._save(file, options));
    }

    @Override
    public void save(OutputStream os, XmlOptions options) throws IOException {
        this.syncWrapIOEx(() -> this._save(os, options));
    }

    @Override
    public void save(Writer w, XmlOptions options) throws IOException {
        this.syncWrapIOEx(() -> this._save(w, options));
    }

    @Override
    public void push() {
        this.syncWrap(this::_push);
    }

    @Override
    public boolean pop() {
        return this.syncWrap(this::_pop);
    }

    @Override
    public void selectPath(String path) {
        this.syncWrap(() -> this._selectPath(path));
    }

    @Override
    public void selectPath(String path, XmlOptions options) {
        this.syncWrap(() -> this._selectPath(path, options));
    }

    @Override
    public boolean hasNextSelection() {
        return this.syncWrap(this::_hasNextSelection);
    }

    @Override
    public boolean toNextSelection() {
        return this.syncWrap(this::_toNextSelection);
    }

    @Override
    public boolean toSelection(int i) {
        return this.syncWrap(() -> this._toSelection(i));
    }

    @Override
    public int getSelectionCount() {
        return this.syncWrap(this::_getSelectionCount);
    }

    @Override
    public void addToSelection() {
        this.syncWrap(this::_addToSelection);
    }

    @Override
    public void clearSelections() {
        this.syncWrap(this::_clearSelections);
    }

    @Override
    public boolean toBookmark(XmlCursor.XmlBookmark bookmark) {
        return this.syncWrap(() -> this._toBookmark(bookmark));
    }

    @Override
    public XmlCursor.XmlBookmark toNextBookmark(Object key) {
        return this.syncWrap(() -> this._toNextBookmark(key));
    }

    @Override
    public XmlCursor.XmlBookmark toPrevBookmark(Object key) {
        return this.syncWrap(() -> this._toPrevBookmark(key));
    }

    @Override
    public QName getName() {
        return this.syncWrap(this::_getName);
    }

    @Override
    public void setName(QName name) {
        this.syncWrap(() -> this._setName(name));
    }

    @Override
    public String namespaceForPrefix(String prefix) {
        return this.syncWrap(() -> this._namespaceForPrefix(prefix));
    }

    @Override
    public String prefixForNamespace(String namespaceURI) {
        return this.syncWrap(() -> this._prefixForNamespace(namespaceURI));
    }

    @Override
    public void getAllNamespaces(Map<String, String> addToThis) {
        this.syncWrap(() -> this._getAllNamespaces(addToThis));
    }

    @Override
    public XmlObject getObject() {
        return this.syncWrap(this::_getObject);
    }

    @Override
    public XmlCursor.TokenType currentTokenType() {
        return this.syncWrapNoEnter(this::_currentTokenType);
    }

    @Override
    public boolean isStartdoc() {
        return this.syncWrapNoEnter(this::_isStartdoc);
    }

    @Override
    public boolean isEnddoc() {
        return this.syncWrapNoEnter(this::_isEnddoc);
    }

    @Override
    public boolean isStart() {
        return this.syncWrapNoEnter(this::_isStart);
    }

    @Override
    public boolean isEnd() {
        return this.syncWrapNoEnter(this::_isEnd);
    }

    @Override
    public boolean isText() {
        return this.syncWrapNoEnter(this::_isText);
    }

    @Override
    public boolean isAttr() {
        return this.syncWrapNoEnter(this::_isAttr);
    }

    @Override
    public boolean isNamespace() {
        return this.syncWrapNoEnter(this::_isNamespace);
    }

    @Override
    public boolean isComment() {
        return this.syncWrapNoEnter(this::_isComment);
    }

    @Override
    public boolean isProcinst() {
        return this.syncWrapNoEnter(this::_isProcinst);
    }

    @Override
    public boolean isContainer() {
        return this.syncWrapNoEnter(this::_isContainer);
    }

    @Override
    public boolean isFinish() {
        return this.syncWrapNoEnter(this::_isFinish);
    }

    @Override
    public boolean isAnyAttr() {
        return this.syncWrapNoEnter(this::_isAnyAttr);
    }

    @Override
    public XmlCursor.TokenType prevTokenType() {
        return this.syncWrap(this::_prevTokenType);
    }

    @Override
    public boolean hasNextToken() {
        return this.syncWrapNoEnter(this::_hasNextToken);
    }

    @Override
    public boolean hasPrevToken() {
        return this.syncWrap(this::_hasPrevToken);
    }

    @Override
    public XmlCursor.TokenType toNextToken() {
        return this.syncWrap(this::_toNextToken);
    }

    @Override
    public XmlCursor.TokenType toPrevToken() {
        return this.syncWrap(this::_toPrevToken);
    }

    @Override
    public XmlCursor.TokenType toFirstContentToken() {
        return this.syncWrap(this::_toFirstContentToken);
    }

    @Override
    public XmlCursor.TokenType toEndToken() {
        return this.syncWrap(this::_toEndToken);
    }

    @Override
    public int toNextChar(int cch) {
        return this.syncWrap(() -> this._toNextChar(cch));
    }

    @Override
    public int toPrevChar(int cch) {
        return this.syncWrap(() -> this._toPrevChar(cch));
    }

    public boolean ___toNextSibling() {
        if (!this._cur.hasParent()) {
            return false;
        }
        Xobj parent = this._cur.getParentNoRoot();
        if (parent == null) {
            this._cur._locale.enter();
            try {
                parent = this._cur.getParent();
            }
            finally {
                this._cur._locale.exit();
            }
        }
        return Locale.toNextSiblingElement(this._cur, parent);
    }

    @Override
    public boolean toNextSibling() {
        return this.syncWrapNoEnter(this::___toNextSibling);
    }

    @Override
    public boolean toPrevSibling() {
        return this.syncWrap(this::_toPrevSibling);
    }

    @Override
    public boolean toParent() {
        return this.syncWrap(this::_toParent);
    }

    @Override
    public boolean toFirstChild() {
        return this.syncWrapNoEnter(this::_toFirstChild);
    }

    @Override
    public boolean toLastChild() {
        return this.syncWrap(this::_toLastChild);
    }

    @Override
    public boolean toChild(String name) {
        return this.syncWrap(() -> this._toChild(name));
    }

    @Override
    public boolean toChild(String namespace, String name) {
        return this.syncWrap(() -> this._toChild(namespace, name));
    }

    @Override
    public boolean toChild(QName name) {
        return this.syncWrap(() -> this._toChild(name));
    }

    @Override
    public boolean toChild(int index) {
        return this.syncWrap(() -> this._toChild(index));
    }

    @Override
    public boolean toChild(QName name, int index) {
        return this.syncWrap(() -> this._toChild(name, index));
    }

    @Override
    public boolean toNextSibling(String name) {
        return this.syncWrap(() -> this._toNextSibling(name));
    }

    @Override
    public boolean toNextSibling(String namespace, String name) {
        return this.syncWrap(() -> this._toNextSibling(namespace, name));
    }

    @Override
    public boolean toNextSibling(QName name) {
        return this.syncWrap(() -> this._toNextSibling(name));
    }

    @Override
    public boolean toFirstAttribute() {
        return this.syncWrapNoEnter(this::_toFirstAttribute);
    }

    @Override
    public boolean toLastAttribute() {
        return this.syncWrap(this::_toLastAttribute);
    }

    @Override
    public boolean toNextAttribute() {
        return this.syncWrap(this::_toNextAttribute);
    }

    @Override
    public boolean toPrevAttribute() {
        return this.syncWrap(this::_toPrevAttribute);
    }

    @Override
    public String getAttributeText(QName attrName) {
        return this.syncWrap(() -> this._getAttributeText(attrName));
    }

    @Override
    public boolean setAttributeText(QName attrName, String value) {
        return this.syncWrap(() -> this._setAttributeText(attrName, value));
    }

    @Override
    public boolean removeAttribute(QName attrName) {
        return this.syncWrap(() -> this._removeAttribute(attrName));
    }

    @Override
    public String getTextValue() {
        return this.syncWrap(this::_getTextValue);
    }

    @Override
    public int getTextValue(char[] chars, int offset, int cch) {
        return this.syncWrap(() -> this._getTextValue(chars, offset, cch));
    }

    @Override
    public void setTextValue(String text) {
        this.syncWrap(() -> this._setTextValue(text));
    }

    @Override
    public void setTextValue(char[] sourceChars, int offset, int length) {
        this.syncWrap(() -> this._setTextValue(sourceChars, offset, length));
    }

    @Override
    public String getChars() {
        return this.syncWrap(this::_getChars);
    }

    @Override
    public int getChars(char[] chars, int offset, int cch) {
        return this.syncWrap(() -> this._getChars(chars, offset, cch));
    }

    @Override
    public void toStartDoc() {
        this.syncWrapNoEnter(this::_toStartDoc);
    }

    @Override
    public void toEndDoc() {
        this.syncWrapNoEnter(this::_toEndDoc);
    }

    @Override
    public XmlCursor execQuery(String query) {
        return this.syncWrap(() -> this._execQuery(query));
    }

    @Override
    public XmlCursor execQuery(String query, XmlOptions options) {
        return this.syncWrap(() -> this._execQuery(query, options));
    }

    @Override
    public XmlCursor.ChangeStamp getDocChangeStamp() {
        return this.syncWrap(this::_getDocChangeStamp);
    }

    @Override
    public void setBookmark(XmlCursor.XmlBookmark bookmark) {
        this.syncWrap(() -> this._setBookmark(bookmark));
    }

    @Override
    public XmlCursor.XmlBookmark getBookmark(Object key) {
        return this.syncWrap(() -> this._getBookmark(key));
    }

    @Override
    public void clearBookmark(Object key) {
        this.syncWrap(() -> this._clearBookmark(key));
    }

    @Override
    public void getAllBookmarkRefs(Collection<Object> listToFill) {
        this.syncWrap(() -> this._getAllBookmarkRefs(listToFill));
    }

    @Override
    public boolean removeXml() {
        return this.syncWrap(this::_removeXml);
    }

    @Override
    public boolean removeXmlContents() {
        return this.syncWrap(this::_removeXmlContents);
    }

    @Override
    public int removeChars(int cch) {
        return this.syncWrap(() -> this._removeChars(cch));
    }

    @Override
    public void insertChars(String text) {
        this.syncWrap(() -> this._insertChars(text));
    }

    @Override
    public void insertElement(QName name) {
        this.syncWrap(() -> this._insertElement(name));
    }

    @Override
    public void insertElement(String localName) {
        this.syncWrap(() -> this._insertElement(localName));
    }

    @Override
    public void insertElement(String localName, String uri) {
        this.syncWrap(() -> this._insertElement(localName, uri));
    }

    @Override
    public void beginElement(QName name) {
        this.syncWrap(() -> this._beginElement(name));
    }

    @Override
    public void beginElement(String localName) {
        this.syncWrap(() -> this._beginElement(localName));
    }

    @Override
    public void beginElement(String localName, String uri) {
        this.syncWrap(() -> this._beginElement(localName, uri));
    }

    @Override
    public void insertElementWithText(QName name, String text) {
        this.syncWrap(() -> this._insertElementWithText(name, text));
    }

    @Override
    public void insertElementWithText(String localName, String text) {
        this.syncWrap(() -> this._insertElementWithText(localName, text));
    }

    @Override
    public void insertElementWithText(String localName, String uri, String text) {
        this.syncWrap(() -> this._insertElementWithText(localName, uri, text));
    }

    @Override
    public void insertAttribute(String localName) {
        this.syncWrap(() -> this._insertAttribute(localName));
    }

    @Override
    public void insertAttribute(String localName, String uri) {
        this.syncWrap(() -> this._insertAttribute(localName, uri));
    }

    @Override
    public void insertAttribute(QName name) {
        this.syncWrap(() -> this._insertAttribute(name));
    }

    @Override
    public void insertAttributeWithValue(String name, String value) {
        this.syncWrap(() -> this._insertAttributeWithValue(name, value));
    }

    @Override
    public void insertAttributeWithValue(String name, String uri, String value) {
        this.syncWrap(() -> this._insertAttributeWithValue(name, uri, value));
    }

    @Override
    public void insertAttributeWithValue(QName name, String value) {
        this.syncWrap(() -> this._insertAttributeWithValue(name, value));
    }

    @Override
    public void insertNamespace(String prefix, String namespace) {
        this.syncWrap(() -> this._insertNamespace(prefix, namespace));
    }

    @Override
    public void insertComment(String text) {
        this.syncWrap(() -> this._insertComment(text));
    }

    @Override
    public void insertProcInst(String target, String text) {
        this.syncWrap(() -> this._insertProcInst(target, text));
    }

    @Override
    public void dump() {
        this.syncWrap(this::_dump);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void syncWrap(Runnable inner) {
        if (this.preCheck()) {
            this.syncWrapHelper(inner, true);
        } else {
            Locale locale = this._cur._locale;
            synchronized (locale) {
                this.syncWrapHelper(inner, true);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T syncWrap(Supplier<T> inner) {
        if (this.preCheck()) {
            return this.syncWrapHelper(inner, true);
        }
        Locale locale = this._cur._locale;
        synchronized (locale) {
            return this.syncWrapHelper(inner, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T syncWrapNoEnter(Supplier<T> inner) {
        if (this.preCheck()) {
            return this.syncWrapHelper(inner, false);
        }
        Locale locale = this._cur._locale;
        synchronized (locale) {
            return this.syncWrapHelper(inner, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void syncWrapNoEnter(Runnable inner) {
        if (this.preCheck()) {
            this.syncWrapHelper(inner, false);
        } else {
            Locale locale = this._cur._locale;
            synchronized (locale) {
                this.syncWrapHelper(inner, false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void syncWrapSAXEx(WrapSAXEx inner) throws SAXException {
        if (this.preCheck()) {
            this.syncWrapHelper(inner);
        } else {
            Locale locale = this._cur._locale;
            synchronized (locale) {
                this.syncWrapHelper(inner);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void syncWrapIOEx(WrapIOEx inner) throws IOException {
        if (this.preCheck()) {
            this.syncWrapHelper(inner);
        } else {
            Locale locale = this._cur._locale;
            synchronized (locale) {
                this.syncWrapHelper(inner);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void syncWrapHelper(Runnable inner, boolean enterLocale) {
        Locale l = this._cur._locale;
        if (enterLocale) {
            l.enter();
        }
        try {
            inner.run();
        }
        finally {
            if (enterLocale) {
                l.exit();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T syncWrapHelper(Supplier<T> inner, boolean enterLocale) {
        Locale l = this._cur._locale;
        if (enterLocale) {
            l.enter();
        }
        try {
            T t = inner.get();
            return t;
        }
        finally {
            if (enterLocale) {
                l.exit();
            }
        }
    }

    private void syncWrapHelper(WrapSAXEx inner) throws SAXException {
        Locale l = this._cur._locale;
        l.enter();
        try {
            inner.run();
        }
        finally {
            l.exit();
        }
    }

    private void syncWrapHelper(WrapIOEx inner) throws IOException {
        Locale l = this._cur._locale;
        l.enter();
        try {
            inner.run();
        }
        finally {
            l.exit();
        }
    }

    private static interface WrapIOEx {
        public void run() throws IOException;
    }

    private static interface WrapSAXEx {
        public void run() throws SAXException;
    }

    private static final class ChangeStampImpl
    implements XmlCursor.ChangeStamp {
        private final Locale _locale;
        private final long _versionStamp;

        ChangeStampImpl(Locale l) {
            this._locale = l;
            this._versionStamp = this._locale.version();
        }

        @Override
        public boolean hasChanged() {
            return this._versionStamp != this._locale.version();
        }
    }
}

