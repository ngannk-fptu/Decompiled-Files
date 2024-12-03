/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.CDataBookmark;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.common.ValidatorListener;
import org.apache.xmlbeans.impl.common.XmlLocale;
import org.apache.xmlbeans.impl.store.Bookmark;
import org.apache.xmlbeans.impl.store.CharNode;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Cursor;
import org.apache.xmlbeans.impl.store.DocumentFragXobj;
import org.apache.xmlbeans.impl.store.DocumentXobj;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NamedNodeXobj;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.Validate;
import org.apache.xmlbeans.impl.values.TypeStore;
import org.apache.xmlbeans.impl.values.TypeStoreUser;
import org.apache.xmlbeans.impl.values.TypeStoreUserFactory;
import org.apache.xmlbeans.impl.values.TypeStoreVisitor;
import org.apache.xmlbeans.impl.xpath.XPathFactory;

abstract class Xobj
implements TypeStore {
    static final int VACANT = 256;
    static final int STABLE_USER = 512;
    static final int INHIBIT_DISCONNECT = 1024;
    Locale _locale;
    QName _name;
    Cur _embedded;
    Bookmark _bookmarks;
    int _bits;
    Xobj _parent;
    Xobj _nextSibling;
    Xobj _prevSibling;
    Xobj _firstChild;
    Xobj _lastChild;
    Object _srcValue;
    Object _srcAfter;
    int _offValue;
    int _offAfter;
    int _cchValue;
    int _cchAfter;
    CharNode _charNodesValue;
    CharNode _charNodesAfter;
    TypeStoreUser _user;

    Xobj(Locale l, int kind, int domType) {
        assert (kind == 1 || kind == 2 || kind == 3 || kind == 4 || kind == 5);
        this._locale = l;
        this._bits = (domType << 4) + kind;
    }

    final int kind() {
        return this._bits & 0xF;
    }

    final int domType() {
        return (this._bits & 0xF0) >> 4;
    }

    final boolean isRoot() {
        return this.kind() == 1;
    }

    final boolean isAttr() {
        return this.kind() == 3;
    }

    final boolean isElem() {
        return this.kind() == 2;
    }

    final boolean isProcinst() {
        return this.kind() == 5;
    }

    final boolean isComment() {
        return this.kind() == 4;
    }

    final boolean isContainer() {
        return Cur.kindIsContainer(this.kind());
    }

    final boolean isUserNode() {
        int k = this.kind();
        return k == 2 || k == 1 || k == 3 && !this.isXmlns();
    }

    final boolean isNormalAttr() {
        return this.isAttr() && !Locale.isXmlns(this._name);
    }

    final boolean isXmlns() {
        return this.isAttr() && Locale.isXmlns(this._name);
    }

    final int cchAfter() {
        return this._cchAfter;
    }

    final int posAfter() {
        return 2 + this._cchValue;
    }

    final int posMax() {
        return 2 + this._cchValue + this._cchAfter;
    }

    final String getXmlnsPrefix() {
        return Locale.xmlnsPrefix(this._name);
    }

    final String getXmlnsUri() {
        return this.getValueAsString();
    }

    final boolean hasTextEnsureOccupancy() {
        this.ensureOccupancy();
        return this.hasTextNoEnsureOccupancy();
    }

    final boolean hasTextNoEnsureOccupancy() {
        if (this._cchValue > 0) {
            return true;
        }
        Xobj lastAttr = this.lastAttr();
        return lastAttr != null && lastAttr._cchAfter > 0;
    }

    final boolean hasAttrs() {
        return this._firstChild != null && this._firstChild.isAttr();
    }

    final boolean hasChildren() {
        return this._lastChild != null && !this._lastChild.isAttr();
    }

    protected final int getDomZeroOneChildren() {
        Xobj node;
        if (this._firstChild == null && this._srcValue == null && this._charNodesValue == null) {
            return 0;
        }
        if (this._lastChild != null && this._lastChild.isAttr() && this._lastChild._charNodesAfter == null && this._lastChild._srcAfter == null && this._srcValue == null && this._charNodesValue == null) {
            return 0;
        }
        if (this._firstChild == this._lastChild && this._firstChild != null && !this._firstChild.isAttr() && this._srcValue == null && this._charNodesValue == null && this._firstChild._srcAfter == null) {
            return 1;
        }
        if (this._firstChild == null && this._srcValue != null && (this._charNodesValue == null || this._charNodesValue._next == null && this._charNodesValue._cch == this._cchValue)) {
            return 1;
        }
        Xobj lastAttr = this.lastAttr();
        Xobj xobj = node = lastAttr == null ? null : lastAttr._nextSibling;
        if (lastAttr != null && lastAttr._srcAfter == null && node != null && node._srcAfter == null && node._nextSibling == null) {
            return 1;
        }
        return 2;
    }

    protected final boolean isFirstChildPtrDomUsable() {
        if (this._firstChild == null && this._srcValue == null && this._charNodesValue == null) {
            return true;
        }
        if (this._firstChild != null && !this._firstChild.isAttr() && this._srcValue == null && this._charNodesValue == null) {
            assert (this._firstChild instanceof NodeXobj) : "wrong node type";
            return true;
        }
        return false;
    }

    protected final boolean isNextSiblingPtrDomUsable() {
        if (this._charNodesAfter == null && this._srcAfter == null) {
            assert (this._nextSibling == null || this._nextSibling instanceof NodeXobj) : "wrong node type";
            return true;
        }
        return false;
    }

    protected final boolean isExistingCharNodesValueUsable() {
        if (this._srcValue == null) {
            return false;
        }
        return this._charNodesValue != null && this._charNodesValue._next == null && this._charNodesValue._cch == this._cchValue;
    }

    protected final boolean isCharNodesValueUsable() {
        return this.isExistingCharNodesValueUsable() || (this._charNodesValue = Cur.updateCharNodes(this._locale, this, this._charNodesValue, this._cchValue)) != null;
    }

    protected final boolean isCharNodesAfterUsable() {
        if (this._srcAfter == null) {
            return false;
        }
        if (this._charNodesAfter != null && this._charNodesAfter._next == null && this._charNodesAfter._cch == this._cchAfter) {
            return true;
        }
        this._charNodesAfter = Cur.updateCharNodes(this._locale, this, this._charNodesAfter, this._cchAfter);
        return this._charNodesAfter != null;
    }

    final Xobj lastAttr() {
        if (this._firstChild == null || !this._firstChild.isAttr()) {
            return null;
        }
        Xobj lastAttr = this._firstChild;
        while (lastAttr._nextSibling != null && lastAttr._nextSibling.isAttr()) {
            lastAttr = lastAttr._nextSibling;
        }
        return lastAttr;
    }

    abstract DomImpl.Dom getDom();

    abstract Xobj newNode(Locale var1);

    final int cchLeft(int p) {
        int pa;
        if (this.isRoot() && p == 0) {
            return 0;
        }
        Xobj x = this.getDenormal(p);
        return p - ((p = this.posTemp()) < (pa = x.posAfter()) ? 1 : pa);
    }

    final int cchRight(int p) {
        assert (p < this.posMax());
        if (p <= 0) {
            return 0;
        }
        int pa = this.posAfter();
        return p < pa ? pa - p - 1 : this.posMax() - p;
    }

    public final Locale locale() {
        return this._locale;
    }

    public final int nodeType() {
        return this.domType();
    }

    public final QName getQName() {
        return this._name;
    }

    public final Cur tempCur() {
        Cur c = this._locale.tempCur();
        c.moveTo(this);
        return c;
    }

    public void dump(PrintStream o, Object ref) {
        Cur.dump(o, this, ref);
    }

    public void dump(PrintStream o) {
        Cur.dump(o, this, this);
    }

    public void dump() {
        this.dump(System.out);
    }

    final Cur getEmbedded() {
        this._locale.embedCurs();
        return this._embedded;
    }

    final boolean inChars(int p, Xobj xIn, int pIn, int cch, boolean includeEnd) {
        int offset;
        assert (p > 0 && p < this.posMax() && p != this.posAfter() - 1 && cch > 0);
        assert (xIn.isNormal(pIn));
        if (includeEnd) {
            if (xIn.isRoot() && pIn == 0) {
                return false;
            }
            xIn = xIn.getDenormal(pIn);
            pIn = xIn.posTemp();
            offset = 1;
        } else {
            offset = 0;
        }
        return xIn == this && pIn >= p && pIn < p + cch + offset;
    }

    final boolean isJustAfterEnd(Xobj x, int p) {
        assert (x.isNormal(p));
        if (x.isRoot() && p == 0) {
            return false;
        }
        return x == this ? p == this.posAfter() : x.getDenormal(p) == this && x.posTemp() == this.posAfter();
    }

    final boolean isInSameTree(Xobj x) {
        if (this._locale != x._locale) {
            return false;
        }
        Xobj y = this;
        while (y != x) {
            if (y._parent == null) {
                while (true) {
                    if (x == this) {
                        return true;
                    }
                    if (x._parent == null) {
                        return x == y;
                    }
                    x = x._parent;
                }
            }
            y = y._parent;
        }
        return true;
    }

    final boolean contains(Cur c) {
        assert (c.isNormal());
        return this.contains(c._xobj, c._pos);
    }

    final boolean contains(Xobj x, int p) {
        assert (x.isNormal(p));
        if (this == x) {
            return p == -1 || p > 0 && p < this.posAfter();
        }
        if (this._firstChild == null) {
            return false;
        }
        while (x != null) {
            if (x == this) {
                return true;
            }
            x = x._parent;
        }
        return false;
    }

    final Bookmark setBookmark(int p, Object key, Object value) {
        assert (this.isNormal(p));
        Bookmark b = this._bookmarks;
        while (b != null) {
            if (p == b._pos && key == b._key) {
                if (value == null) {
                    this._bookmarks = b.listRemove(this._bookmarks);
                    return null;
                }
                b._value = value;
                return b;
            }
            b = b._next;
        }
        if (value == null) {
            return null;
        }
        b = new Bookmark();
        b._xobj = this;
        b._pos = p;
        b._key = key;
        b._value = value;
        this._bookmarks = b.listInsert(this._bookmarks);
        return b;
    }

    final boolean hasBookmark(Object key, int pos) {
        Bookmark b = this._bookmarks;
        while (b != null) {
            if (b._pos == pos && key == b._key) {
                return true;
            }
            b = b._next;
        }
        return false;
    }

    final Xobj findXmlnsForPrefix(String prefix) {
        assert (this.isContainer() && prefix != null);
        Xobj c = this;
        while (c != null) {
            for (Xobj a = c.firstAttr(); a != null; a = a.nextAttr()) {
                if (!a.isXmlns() || !a.getXmlnsPrefix().equals(prefix)) continue;
                return a;
            }
            c = c._parent;
        }
        return null;
    }

    final boolean removeAttr(QName name) {
        assert (this.isContainer());
        Xobj a = this.getAttr(name);
        if (a == null) {
            return false;
        }
        Cur c = a.tempCur();
        while (true) {
            c.moveNode(null);
            a = this.getAttr(name);
            if (a == null) break;
            c.moveTo(a);
        }
        c.release();
        return true;
    }

    final Xobj setAttr(QName name, String value) {
        assert (this.isContainer());
        Cur c = this.tempCur();
        if (c.toAttr(name)) {
            c.removeFollowingAttrs();
        } else {
            c.next();
            c.createAttr(name);
        }
        c.setValue(value);
        Xobj a = c._xobj;
        c.release();
        return a;
    }

    final void setName(QName newName) {
        assert (this.isAttr() || this.isElem() || this.isProcinst());
        assert (newName != null);
        if (!this._name.equals(newName) || !this._name.getPrefix().equals(newName.getPrefix())) {
            this._locale.notifyChange();
            QName oldName = this._name;
            this._name = newName;
            if (this instanceof NamedNodeXobj) {
                NamedNodeXobj me = (NamedNodeXobj)this;
                me._canHavePrefixUri = true;
            }
            if (!this.isProcinst()) {
                Xobj disconnectFromHere = this;
                if (this.isAttr() && this._parent != null) {
                    if (oldName.equals(Locale._xsiType) || newName.equals(Locale._xsiType)) {
                        disconnectFromHere = this._parent;
                    }
                    if (oldName.equals(Locale._xsiNil) || newName.equals(Locale._xsiNil)) {
                        this._parent.invalidateNil();
                    }
                }
                disconnectFromHere.disconnectNonRootUsers();
            }
            ++this._locale._versionAll;
            ++this._locale._versionSansText;
        }
    }

    final Xobj ensureParent() {
        assert (this._parent != null || !this.isRoot() && this.cchAfter() == 0);
        return this._parent == null ? new DocumentFragXobj(this._locale).appendXobj(this) : this._parent;
    }

    final Xobj firstAttr() {
        return this._firstChild == null || !this._firstChild.isAttr() ? null : this._firstChild;
    }

    final Xobj nextAttr() {
        if (this._firstChild != null && this._firstChild.isAttr()) {
            return this._firstChild;
        }
        if (this._nextSibling != null && this._nextSibling.isAttr()) {
            return this._nextSibling;
        }
        return null;
    }

    final boolean isValid() {
        return !this.isVacant() || this._cchValue == 0 && this._user != null;
    }

    final int posTemp() {
        return this._locale._posTemp;
    }

    final Xobj getNormal(int p) {
        assert (p == -1 || p >= 0 && p <= this.posMax());
        Xobj x = this;
        if (p == x.posMax()) {
            if (x._nextSibling != null) {
                x = x._nextSibling;
                p = 0;
            } else {
                x = x.ensureParent();
                p = -1;
            }
        } else if (p == x.posAfter() - 1) {
            p = -1;
        }
        this._locale._posTemp = p;
        return x;
    }

    final Xobj getDenormal(int p) {
        assert (!this.isRoot() || p == -1 || p > 0);
        Xobj x = this;
        if (p == 0) {
            if (x._prevSibling == null) {
                x = x.ensureParent();
                p = x.posAfter() - 1;
            } else {
                x = x._prevSibling;
                p = x.posMax();
            }
        } else if (p == -1) {
            if (x._lastChild == null) {
                p = x.posAfter() - 1;
            } else {
                x = x._lastChild;
                p = x.posMax();
            }
        }
        this._locale._posTemp = p;
        return x;
    }

    final boolean isNormal(int p) {
        if (!this.isValid()) {
            return false;
        }
        if (p == -1 || p == 0) {
            return true;
        }
        if (p < 0 || p >= this.posMax()) {
            return false;
        }
        if (p >= this.posAfter()) {
            if (this.isRoot()) {
                return false;
            }
            if (this._nextSibling != null && this._nextSibling.isAttr()) {
                return false;
            }
            if (this._parent == null || !this._parent.isContainer()) {
                return false;
            }
        }
        return p != this.posAfter() - 1;
    }

    final Xobj walk(Xobj root, boolean walkChildren) {
        if (this._firstChild != null && walkChildren) {
            return this._firstChild;
        }
        Xobj x = this;
        while (x != root) {
            if (x._nextSibling != null) {
                return x._nextSibling;
            }
            x = x._parent;
        }
        return null;
    }

    final void removeXobj() {
        if (this._parent != null) {
            if (this._parent._firstChild == this) {
                this._parent._firstChild = this._nextSibling;
            }
            if (this._parent._lastChild == this) {
                this._parent._lastChild = this._prevSibling;
            }
            if (this._prevSibling != null) {
                this._prevSibling._nextSibling = this._nextSibling;
            }
            if (this._nextSibling != null) {
                this._nextSibling._prevSibling = this._prevSibling;
            }
            this._parent = null;
            this._prevSibling = null;
            this._nextSibling = null;
        }
    }

    final void insertXobj(Xobj s) {
        assert (this._locale == s._locale);
        assert (!s.isRoot() && !this.isRoot());
        assert (s._parent == null);
        assert (s._prevSibling == null);
        assert (s._nextSibling == null);
        this.ensureParent();
        s._parent = this._parent;
        s._prevSibling = this._prevSibling;
        s._nextSibling = this;
        if (this._prevSibling != null) {
            this._prevSibling._nextSibling = s;
        } else {
            this._parent._firstChild = s;
        }
        this._prevSibling = s;
    }

    final Xobj appendXobj(Xobj c) {
        assert (this._locale == c._locale);
        assert (!c.isRoot());
        assert (c._parent == null);
        assert (c._prevSibling == null);
        assert (c._nextSibling == null);
        assert (this._lastChild == null || this._firstChild != null);
        c._parent = this;
        c._prevSibling = this._lastChild;
        if (this._lastChild == null) {
            this._firstChild = c;
        } else {
            this._lastChild._nextSibling = c;
        }
        this._lastChild = c;
        return this;
    }

    final void removeXobjs(Xobj first, Xobj last) {
        assert (last._locale == first._locale);
        assert (first._parent == this);
        assert (last._parent == this);
        if (this._firstChild == first) {
            this._firstChild = last._nextSibling;
        }
        if (this._lastChild == last) {
            this._lastChild = first._prevSibling;
        }
        if (first._prevSibling != null) {
            first._prevSibling._nextSibling = last._nextSibling;
        }
        if (last._nextSibling != null) {
            last._nextSibling._prevSibling = first._prevSibling;
        }
        first._prevSibling = null;
        last._nextSibling = null;
        while (first != null) {
            first._parent = null;
            first = first._nextSibling;
        }
    }

    final void insertXobjs(Xobj first, Xobj last) {
        assert (this._locale == first._locale);
        assert (last._locale == first._locale);
        assert (first._parent == null && last._parent == null);
        assert (first._prevSibling == null);
        assert (last._nextSibling == null);
        first._prevSibling = this._prevSibling;
        last._nextSibling = this;
        if (this._prevSibling != null) {
            this._prevSibling._nextSibling = first;
        } else {
            this._parent._firstChild = first;
        }
        this._prevSibling = last;
        while (first != this) {
            first._parent = this._parent;
            first = first._nextSibling;
        }
    }

    final void appendXobjs(Xobj first, Xobj last) {
        assert (this._locale == first._locale);
        assert (last._locale == first._locale);
        assert (first._parent == null && last._parent == null);
        assert (first._prevSibling == null);
        assert (last._nextSibling == null);
        assert (!first.isRoot());
        first._prevSibling = this._lastChild;
        if (this._lastChild == null) {
            this._firstChild = first;
        } else {
            this._lastChild._nextSibling = first;
        }
        this._lastChild = last;
        while (first != null) {
            first._parent = this;
            first = first._nextSibling;
        }
    }

    final void invalidateSpecialAttr(Xobj newParent) {
        if (this.isAttr()) {
            if (this._name.equals(Locale._xsiType)) {
                if (this._parent != null) {
                    this._parent.disconnectNonRootUsers();
                }
                if (newParent != null) {
                    newParent.disconnectNonRootUsers();
                }
            }
            if (this._name.equals(Locale._xsiNil)) {
                if (this._parent != null) {
                    this._parent.invalidateNil();
                }
                if (newParent != null) {
                    newParent.invalidateNil();
                }
            }
        }
    }

    final void removeCharsHelper(int p, int cchRemove, Xobj xTo, int pTo, boolean moveCurs, boolean invalidate) {
        assert (p > 0 && p < this.posMax() && p != this.posAfter() - 1);
        assert (cchRemove > 0);
        assert (this.cchRight(p) >= cchRemove);
        assert (!moveCurs || xTo != null);
        Cur c = this.getEmbedded();
        while (c != null) {
            Cur next = c._next;
            assert (c._xobj == this);
            if (c._pos >= p && c._pos < p + cchRemove) {
                if (moveCurs) {
                    c.moveToNoCheck(xTo, pTo + c._pos - p);
                } else {
                    c.nextChars(cchRemove - c._pos + p);
                }
            }
            if (c._xobj == this && c._pos >= p + cchRemove) {
                c._pos -= cchRemove;
            }
            c = next;
        }
        Bookmark b = this._bookmarks;
        while (b != null) {
            assert (b._xobj == this);
            if (b._pos >= p && b._pos < p + cchRemove) {
                assert (xTo != null);
                b.moveTo(xTo, pTo + b._pos - p);
            }
            if (b._xobj == this && b._pos >= p + cchRemove) {
                b._pos -= cchRemove;
            }
            b = b._next;
        }
        int pa = this.posAfter();
        CharUtil cu = this._locale.getCharUtil();
        if (p < pa) {
            this._srcValue = cu.removeChars(p - 1, cchRemove, this._srcValue, this._offValue, this._cchValue);
            this._offValue = cu._offSrc;
            this._cchValue = cu._cchSrc;
            if (invalidate) {
                this.invalidateUser();
                this.invalidateSpecialAttr(null);
            }
        } else {
            this._srcAfter = cu.removeChars(p - pa, cchRemove, this._srcAfter, this._offAfter, this._cchAfter);
            this._offAfter = cu._offSrc;
            this._cchAfter = cu._cchSrc;
            if (invalidate && this._parent != null) {
                this._parent.invalidateUser();
            }
        }
    }

    final void insertCharsHelper(int p, Object src, int off, int cch, boolean invalidate) {
        assert (p > 0);
        assert (p >= this.posAfter() || this.isOccupied());
        int pa = this.posAfter();
        if (p - (p < pa ? 1 : 2) < this._cchValue + this._cchAfter) {
            Cur c = this.getEmbedded();
            while (c != null) {
                if (c._pos >= p) {
                    c._pos += cch;
                }
                c = c._next;
            }
            Bookmark b = this._bookmarks;
            while (b != null) {
                if (b._pos >= p) {
                    b._pos += cch;
                }
                b = b._next;
            }
        }
        CharUtil cu = this._locale.getCharUtil();
        if (p < pa) {
            this._srcValue = cu.insertChars(p - 1, this._srcValue, this._offValue, this._cchValue, src, off, cch);
            this._offValue = cu._offSrc;
            this._cchValue = cu._cchSrc;
            if (invalidate) {
                this.invalidateUser();
                this.invalidateSpecialAttr(null);
            }
        } else {
            this._srcAfter = cu.insertChars(p - pa, this._srcAfter, this._offAfter, this._cchAfter, src, off, cch);
            this._offAfter = cu._offSrc;
            this._cchAfter = cu._cchSrc;
            if (invalidate && this._parent != null) {
                this._parent.invalidateUser();
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    Xobj copyNode(Locale toLocale) {
        newParent = null;
        copy = null;
        x = this;
        block0: while (true) {
            x.ensureOccupancy();
            newX = x.newNode(toLocale);
            newX._srcValue = x._srcValue;
            newX._offValue = x._offValue;
            newX._cchValue = x._cchValue;
            newX._srcAfter = x._srcAfter;
            newX._offAfter = x._offAfter;
            newX._cchAfter = x._cchAfter;
            b = x._bookmarks;
            while (b != null) {
                if (x.hasBookmark(CDataBookmark.CDATA_BOOKMARK.getKey(), b._pos)) {
                    newX.setBookmark(b._pos, CDataBookmark.CDATA_BOOKMARK.getKey(), CDataBookmark.CDATA_BOOKMARK);
                }
                b = b._next;
            }
            if (newParent == null) {
                copy = newX;
            } else {
                newParent.appendXobj(newX);
            }
            y = x;
            x = x.walk(this, true);
            if (x == null) break;
            if (y == x._parent) {
                newParent = newX;
                continue;
            }
            while (true) {
                if (y._parent != x._parent) ** break;
                continue block0;
                newParent = newParent._parent;
                y = y._parent;
            }
            break;
        }
        copy._srcAfter = null;
        copy._offAfter = 0;
        copy._cchAfter = 0;
        return copy;
    }

    String getCharsAsString(int p, int cch, int wsr) {
        if (this.cchRight(p) == 0) {
            return "";
        }
        Object src = this.getChars(p, cch);
        if (wsr == 1) {
            return CharUtil.getString(src, this._locale._offSrc, this._locale._cchSrc);
        }
        Locale.ScrubBuffer scrub = Locale.getScrubBuffer(wsr);
        scrub.scrub(src, this._locale._offSrc, this._locale._cchSrc);
        return scrub.getResultAsString();
    }

    String getCharsAfterAsString(int off, int cch) {
        int offset = off + this._cchValue + 2;
        if (offset == this.posMax()) {
            offset = -1;
        }
        return this.getCharsAsString(offset, cch, 1);
    }

    String getCharsValueAsString(int off, int cch) {
        return this.getCharsAsString(off + 1, cch, 1);
    }

    String getValueAsString(int wsr) {
        if (!this.hasChildren()) {
            Object src = this.getFirstChars();
            if (wsr == 1) {
                String s = CharUtil.getString(src, this._locale._offSrc, this._locale._cchSrc);
                int cch = s.length();
                if (cch > 0) {
                    Xobj lastAttr = this.lastAttr();
                    assert ((lastAttr == null ? this._cchValue : lastAttr._cchAfter) == cch);
                    if (lastAttr != null) {
                        lastAttr._srcAfter = s;
                        lastAttr._offAfter = 0;
                    } else {
                        this._srcValue = s;
                        this._offValue = 0;
                    }
                }
                return s;
            }
            Locale.ScrubBuffer scrub = Locale.getScrubBuffer(wsr);
            scrub.scrub(src, this._locale._offSrc, this._locale._cchSrc);
            return scrub.getResultAsString();
        }
        Locale.ScrubBuffer scrub = Locale.getScrubBuffer(wsr);
        Cur c = this.tempCur();
        c.push();
        c.next();
        while (!c.isAtEndOfLastPush()) {
            if (c.isText()) {
                scrub.scrub(c.getChars(-1), c._offSrc, c._cchSrc);
            }
            if (c.isComment() || c.isProcinst()) {
                c.skip();
                continue;
            }
            c.next();
        }
        String s = scrub.getResultAsString();
        c.release();
        return s;
    }

    String getValueAsString() {
        return this.getValueAsString(1);
    }

    Object getFirstChars() {
        this.ensureOccupancy();
        if (this._cchValue > 0) {
            return this.getChars(1, -1);
        }
        Xobj lastAttr = this.lastAttr();
        if (lastAttr == null || lastAttr._cchAfter <= 0) {
            this._locale._offSrc = 0;
            this._locale._cchSrc = 0;
            return null;
        }
        return lastAttr.getChars(lastAttr.posAfter(), -1);
    }

    Object getChars(int pos, int cch, Cur c) {
        Object src = this.getChars(pos, cch);
        c._offSrc = this._locale._offSrc;
        c._cchSrc = this._locale._cchSrc;
        return src;
    }

    Object getChars(int pos, int cch) {
        assert (this.isNormal(pos));
        int cchRight = this.cchRight(pos);
        if (cch < 0 || cch > cchRight) {
            cch = cchRight;
        }
        if (cch == 0) {
            this._locale._offSrc = 0;
            this._locale._cchSrc = 0;
            return null;
        }
        return this.getCharsHelper(pos, cch);
    }

    Object getCharsHelper(int pos, int cch) {
        Object src;
        assert (cch > 0 && this.cchRight(pos) >= cch);
        int pa = this.posAfter();
        if (pos >= pa) {
            src = this._srcAfter;
            this._locale._offSrc = this._offAfter + pos - pa;
        } else {
            src = this._srcValue;
            this._locale._offSrc = this._offValue + pos - 1;
        }
        this._locale._cchSrc = cch;
        return src;
    }

    final void setBit(int mask) {
        this._bits |= mask;
    }

    final void clearBit(int mask) {
        this._bits &= ~mask;
    }

    final boolean bitIsSet(int mask) {
        return (this._bits & mask) != 0;
    }

    final boolean bitIsClear(int mask) {
        return (this._bits & mask) == 0;
    }

    final boolean isVacant() {
        return this.bitIsSet(256);
    }

    final boolean isOccupied() {
        return this.bitIsClear(256);
    }

    final boolean inhibitDisconnect() {
        return this.bitIsSet(1024);
    }

    final boolean isStableUser() {
        return this.bitIsSet(512);
    }

    void invalidateNil() {
        if (this._user != null) {
            this._user.invalidate_nilvalue();
        }
    }

    void setStableType(SchemaType type) {
        this.setStableUser(((TypeStoreUserFactory)((Object)type)).createTypeStoreUser());
    }

    void setStableUser(TypeStoreUser user) {
        this.disconnectNonRootUsers();
        this.disconnectUser();
        assert (this._user == null);
        this._user = user;
        this._user.attach_store(this);
        this.setBit(512);
    }

    void disconnectUser() {
        if (this._user != null && !this.inhibitDisconnect()) {
            this.ensureOccupancy();
            this._user.disconnect_store();
            this._user = null;
        }
    }

    void disconnectNonRootUsers() {
        Xobj x = this;
        while (x != null) {
            Xobj next = x.walk(this, x._user != null);
            if (!x.isRoot()) {
                x.disconnectUser();
            }
            x = next;
        }
    }

    void disconnectChildrenUsers() {
        Xobj x = this.walk(this, this._user == null);
        while (x != null) {
            Xobj next = x.walk(this, x._user != null);
            x.disconnectUser();
            x = next;
        }
    }

    final String namespaceForPrefix(String prefix, boolean defaultAlwaysMapped) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        Xobj x = this;
        while (x != null) {
            Xobj a = x._firstChild;
            while (a != null && a.isAttr()) {
                if (a.isXmlns() && a.getXmlnsPrefix().equals(prefix)) {
                    return a.getXmlnsUri();
                }
                a = a._nextSibling;
            }
            x = x._parent;
        }
        return defaultAlwaysMapped && prefix.length() == 0 ? "" : null;
    }

    final String prefixForNamespace(String ns, String suggestion, boolean createIfMissing) {
        if (ns == null) {
            ns = "";
        }
        if (ns.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (ns.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        Xobj base = this;
        while (!base.isContainer()) {
            base = base.ensureParent();
        }
        if (ns.length() == 0) {
            Xobj a = base.findXmlnsForPrefix("");
            if (a == null || a.getXmlnsUri().length() == 0) {
                return "";
            }
            if (!createIfMissing) {
                return null;
            }
            base.setAttr(this._locale.createXmlns(null), "");
            return "";
        }
        Xobj c = base;
        while (c != null) {
            for (Xobj a = c.firstAttr(); a != null; a = a.nextAttr()) {
                if (!a.isXmlns() || !a.getXmlnsUri().equals(ns) || base.findXmlnsForPrefix(a.getXmlnsPrefix()) != a) continue;
                return a.getXmlnsPrefix();
            }
            c = c._parent;
        }
        if (!createIfMissing) {
            return null;
        }
        if (suggestion != null && (suggestion.length() == 0 || suggestion.toLowerCase(java.util.Locale.ROOT).startsWith("xml") || base.findXmlnsForPrefix(suggestion) != null)) {
            suggestion = null;
        }
        if (suggestion == null) {
            String prefixBase;
            suggestion = prefixBase = QNameHelper.suggestPrefix(ns);
            int i = 1;
            while (base.findXmlnsForPrefix(suggestion) != null) {
                suggestion = prefixBase + i++;
            }
        }
        c = base;
        while (!c.isRoot() && !c.ensureParent().isRoot()) {
            c = c._parent;
        }
        base.setAttr(this._locale.createXmlns(suggestion), ns);
        return suggestion;
    }

    final QName getValueAsQName() {
        String localname;
        String prefix;
        assert (!this.hasChildren());
        String value = this.getValueAsString(3);
        int firstcolon = value.indexOf(58);
        if (firstcolon >= 0) {
            prefix = value.substring(0, firstcolon);
            localname = value.substring(firstcolon + 1);
        } else {
            prefix = "";
            localname = value;
        }
        String uri = this.namespaceForPrefix(prefix, true);
        if (uri == null) {
            return null;
        }
        return new QName(uri, localname);
    }

    final Xobj getAttr(QName name) {
        Xobj x = this._firstChild;
        while (x != null && x.isAttr()) {
            if (x._name.equals(name)) {
                return x;
            }
            x = x._nextSibling;
        }
        return null;
    }

    final QName getXsiTypeName() {
        assert (this.isContainer());
        Xobj a = this.getAttr(Locale._xsiType);
        return a == null ? null : a.getValueAsQName();
    }

    final XmlObject getObject() {
        return this.isUserNode() ? (XmlObject)((Object)this.getUser()) : null;
    }

    final TypeStoreUser getUser() {
        assert (this.isUserNode());
        assert (this._user != null || !this.isRoot() && !this.isStableUser());
        if (this._user == null) {
            TypeStoreUser parentUser = this._parent == null ? ((TypeStoreUserFactory)((Object)XmlBeans.NO_TYPE)).createTypeStoreUser() : this._parent.getUser();
            this._user = this.isElem() ? parentUser.create_element_user(this._name, this.getXsiTypeName()) : parentUser.create_attribute_user(this._name);
            this._user.attach_store(this);
        }
        return this._user;
    }

    final void invalidateUser() {
        assert (this.isValid());
        assert (this._user == null || this.isUserNode());
        if (this._user != null) {
            this._user.invalidate_value();
        }
    }

    final void ensureOccupancy() {
        assert (this.isValid());
        if (this.isVacant()) {
            assert (this.isUserNode());
            this.clearBit(256);
            TypeStoreUser user = this._user;
            this._user = null;
            String value = user.build_text(this);
            long saveVersion = this._locale._versionAll;
            long saveVersionSansText = this._locale._versionSansText;
            this.setValue(value);
            assert (saveVersionSansText == this._locale._versionSansText);
            this._locale._versionAll = saveVersion;
            assert (this._user == null);
            this._user = user;
        }
    }

    private void setValue(String val) {
        assert (CharUtil.isValid(val, 0, val.length()));
        if (val.length() <= 0) {
            return;
        }
        this._locale.notifyChange();
        Xobj lastAttr = this.lastAttr();
        int startPos = 1;
        Xobj charOwner = this;
        if (lastAttr != null) {
            charOwner = lastAttr;
            startPos = charOwner.posAfter();
        }
        charOwner.insertCharsHelper(startPos, val, 0, val.length(), true);
    }

    @Override
    public SchemaTypeLoader get_schematypeloader() {
        return this._locale._schemaTypeLoader;
    }

    @Override
    public XmlLocale get_locale() {
        return this._locale;
    }

    @Override
    public Object get_root_object() {
        return this._locale;
    }

    @Override
    public boolean is_attribute() {
        assert (this.isValid());
        return this.isAttr();
    }

    @Override
    public boolean validate_on_set() {
        assert (this.isValid());
        return this._locale._validateOnSet;
    }

    @Override
    public void invalidate_text() {
        this._locale.enter();
        try {
            assert (this.isValid());
            if (this.isOccupied()) {
                if (this.hasTextNoEnsureOccupancy() || this.hasChildren()) {
                    TypeStoreUser user = this._user;
                    this._user = null;
                    Cur c = this.tempCur();
                    c.moveNodeContents(null, false);
                    c.release();
                    assert (this._user == null);
                    this._user = user;
                }
                this.setBit(256);
            }
            assert (this.isValid());
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public String fetch_text(int wsr) {
        this._locale.enter();
        try {
            assert (this.isValid() && this.isOccupied());
            String string = this.getValueAsString(wsr);
            return string;
        }
        finally {
            this._locale.exit();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlCursor new_cursor() {
        this._locale.enter();
        try {
            Cur c = this.tempCur();
            Cursor xc = new Cursor(c);
            c.release();
            Cursor cursor = xc;
            return cursor;
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public SchemaField get_schema_field() {
        assert (this.isValid());
        if (this.isRoot()) {
            return null;
        }
        TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_attribute_field(this._name);
        }
        assert (this.isElem());
        TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return null;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    return visitor.get_schema_field();
                }
            }
            x = x._nextSibling;
        }
    }

    @Override
    public void validate(ValidatorListener eventSink) {
        this._locale.enter();
        try {
            Cur c = this.tempCur();
            new Validate(c, eventSink);
            c.release();
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public TypeStoreUser change_type(SchemaType type) {
        this._locale.enter();
        try {
            Cur c = this.tempCur();
            c.setType(type, false);
            c.release();
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TypeStoreUser substitute(QName name, SchemaType type) {
        this._locale.enter();
        try {
            Cur c = this.tempCur();
            c.setSubstitution(name, type);
            c.release();
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }

    @Override
    public QName get_xsi_type() {
        return this.getXsiTypeName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void store_text(String text) {
        this._locale.enter();
        TypeStoreUser user = this._user;
        this._user = null;
        try {
            Cur c = this.tempCur();
            c.moveNodeContents(null, false);
            if (text != null && text.length() > 0) {
                c.next();
                c.insertString(text);
            }
            c.release();
        }
        finally {
            assert (this._user == null);
            this._user = user;
            this._locale.exit();
        }
    }

    @Override
    public int compute_flags() {
        if (this.isRoot()) {
            return 0;
        }
        TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_attributeflags(this._name);
        }
        int f = parentUser.get_elementflags(this._name);
        if (f != -1) {
            return f;
        }
        TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return 0;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    return visitor.get_elementflags();
                }
            }
            x = x._nextSibling;
        }
    }

    @Override
    public String compute_default_text() {
        if (this.isRoot()) {
            return null;
        }
        TypeStoreUser parentUser = this.ensureParent().getUser();
        if (this.isAttr()) {
            return parentUser.get_default_attribute_text(this._name);
        }
        String result = parentUser.get_default_element_text(this._name);
        if (result != null) {
            return result;
        }
        TypeStoreVisitor visitor = parentUser.new_visitor();
        if (visitor == null) {
            return null;
        }
        Xobj x = this._parent._firstChild;
        while (true) {
            if (x.isElem()) {
                visitor.visit(x._name);
                if (x == this) {
                    return visitor.get_default_text();
                }
            }
            x = x._nextSibling;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean find_nil() {
        if (this.isAttr()) {
            return false;
        }
        this._locale.enter();
        try {
            Xobj a = this.getAttr(Locale._xsiNil);
            if (a == null) {
                boolean bl = false;
                return bl;
            }
            String value = a.getValueAsString(3);
            boolean bl = value.equals("true") || value.equals("1");
            return bl;
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public void invalidate_nil() {
        if (this.isAttr()) {
            return;
        }
        this._locale.enter();
        try {
            if (!this._user.build_nil()) {
                this.removeAttr(Locale._xsiNil);
            } else {
                this.setAttr(Locale._xsiNil, "true");
            }
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public int count_elements(QName name) {
        return this._locale.count(this, name, null);
    }

    @Override
    public int count_elements(QNameSet names) {
        return this._locale.count(this, null, names);
    }

    @Override
    public TypeStoreUser find_element_user(QName name, int i) {
        Xobj x = this._firstChild;
        while (x != null) {
            if (x.isElem() && x._name.equals(name) && --i < 0) {
                return x.getUser();
            }
            x = x._nextSibling;
        }
        return null;
    }

    @Override
    public TypeStoreUser find_element_user(QNameSet names, int i) {
        Xobj x = this._firstChild;
        while (x != null) {
            if (x.isElem() && names.contains(x._name) && --i < 0) {
                return x.getUser();
            }
            x = x._nextSibling;
        }
        return null;
    }

    @Override
    public <T extends XmlObject> void find_all_element_users(QName name, List<T> fillMeUp) {
        Xobj x = this._firstChild;
        while (x != null) {
            if (x.isElem() && x._name.equals(name)) {
                fillMeUp.add((XmlObject)((Object)x.getUser()));
            }
            x = x._nextSibling;
        }
    }

    @Override
    public <T extends XmlObject> void find_all_element_users(QNameSet names, List<T> fillMeUp) {
        Xobj x = this._firstChild;
        while (x != null) {
            if (x.isElem() && names.contains(x._name)) {
                fillMeUp.add((XmlObject)((Object)x.getUser()));
            }
            x = x._nextSibling;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static TypeStoreUser insertElement(QName name, Xobj x, int pos) {
        x._locale.enter();
        try {
            Cur c = x._locale.tempCur();
            c.moveTo(x, pos);
            c.createElement(name);
            TypeStoreUser user = c.getUser();
            c.release();
            TypeStoreUser typeStoreUser = user;
            return typeStoreUser;
        }
        finally {
            x._locale.exit();
        }
    }

    @Override
    public TypeStoreUser insert_element_user(QName name, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x = this._locale.findNthChildElem(this, name, null, i);
        if (x == null) {
            if (i > this._locale.count(this, name, null) + 1) {
                throw new IndexOutOfBoundsException();
            }
            return this.add_element_user(name);
        }
        return Xobj.insertElement(name, x, 0);
    }

    @Override
    public TypeStoreUser insert_element_user(QNameSet names, QName name, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x = this._locale.findNthChildElem(this, null, names, i);
        if (x == null) {
            if (i > this._locale.count(this, null, names) + 1) {
                throw new IndexOutOfBoundsException();
            }
            return this.add_element_user(name);
        }
        return Xobj.insertElement(name, x, 0);
    }

    @Override
    public TypeStoreUser add_element_user(QName name) {
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        QNameSet endSet = null;
        boolean gotEndSet = false;
        Xobj candidate = null;
        Xobj x = this._lastChild;
        while (x != null) {
            if (x.isContainer()) {
                if (x._name.equals(name)) break;
                if (!gotEndSet) {
                    endSet = this._user.get_element_ending_delimiters(name);
                    gotEndSet = true;
                }
                if (endSet == null || endSet.contains(x._name)) {
                    candidate = x;
                }
            }
            x = x._prevSibling;
        }
        return candidate == null ? Xobj.insertElement(name, this, -1) : Xobj.insertElement(name, candidate, 0);
    }

    private static void removeElement(Xobj x) {
        if (x == null) {
            throw new IndexOutOfBoundsException();
        }
        x._locale.enter();
        try {
            Cur c = x.tempCur();
            c.moveNode(null);
            c.release();
        }
        finally {
            x._locale.exit();
        }
    }

    @Override
    public void remove_element(QName name, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x = this._firstChild;
        while (!(x == null || x.isElem() && x._name.equals(name) && --i < 0)) {
            x = x._nextSibling;
        }
        Xobj.removeElement(x);
    }

    @Override
    public void remove_element(QNameSet names, int i) {
        if (i < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.isContainer()) {
            throw new IllegalStateException();
        }
        Xobj x = this._firstChild;
        while (!(x == null || x.isElem() && names.contains(x._name) && --i < 0)) {
            x = x._nextSibling;
        }
        Xobj.removeElement(x);
    }

    @Override
    public TypeStoreUser find_attribute_user(QName name) {
        Xobj a = this.getAttr(name);
        return a == null ? null : a.getUser();
    }

    @Override
    public TypeStoreUser add_attribute_user(QName name) {
        if (this.getAttr(name) != null) {
            throw new IndexOutOfBoundsException();
        }
        this._locale.enter();
        try {
            TypeStoreUser typeStoreUser = this.setAttr(name, "").getUser();
            return typeStoreUser;
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public void remove_attribute(QName name) {
        this._locale.enter();
        try {
            if (!this.removeAttr(name)) {
                throw new IndexOutOfBoundsException();
            }
        }
        finally {
            this._locale.exit();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TypeStoreUser copy_contents_from(TypeStore source) {
        Xobj xSrc = (Xobj)source;
        if (xSrc == this) {
            return this.getUser();
        }
        this._locale.enter();
        try {
            xSrc._locale.enter();
            Cur c = this.tempCur();
            try {
                Cur cSrc1 = xSrc.tempCur();
                Map<String, String> sourceNamespaces = Locale.getAllNamespaces(cSrc1, null);
                cSrc1.release();
                if (this.isAttr()) {
                    Cur cSrc = xSrc.tempCur();
                    String value = Locale.getTextValue(cSrc);
                    cSrc.release();
                    c.setValue(value);
                } else {
                    this.disconnectChildrenUsers();
                    assert (!this.inhibitDisconnect());
                    this.setBit(1024);
                    QName xsiType = this.isContainer() ? this.getXsiTypeName() : null;
                    Xobj copy = xSrc.copyNode(this._locale);
                    Cur.moveNodeContents(this, null, true);
                    c.next();
                    Cur.moveNodeContents(copy, c, true);
                    c.moveTo(this);
                    if (xsiType != null) {
                        c.setXsiType(xsiType);
                    }
                    assert (this.inhibitDisconnect());
                    this.clearBit(1024);
                }
                if (sourceNamespaces != null) {
                    if (!c.isContainer()) {
                        c.toParent();
                    }
                    Locale.applyNamespaces(c, sourceNamespaces);
                }
            }
            finally {
                c.release();
                xSrc._locale.exit();
            }
        }
        finally {
            this._locale.exit();
        }
        return this.getUser();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TypeStoreUser copy(SchemaTypeLoader stl, SchemaType type, XmlOptions options) {
        SchemaType sType = (options = XmlOptions.maskNull(options)).getDocumentType();
        if (sType == null) {
            sType = type == null ? XmlObject.type : type;
        }
        Locale locale = this.locale();
        if (options.isCopyUseNewSynchronizationDomain()) {
            locale = Locale.getLocale(stl, options);
        }
        boolean isFragment = !sType.isDocumentType() && (!sType.isNoType() || !(this instanceof DocumentXobj));
        Xobj destination = Cur.createDomDocumentRootXobj(locale, isFragment);
        locale.enter();
        try {
            Cur c = destination.tempCur();
            c.setType(type);
            c.release();
        }
        finally {
            locale.exit();
        }
        return destination.copy_contents_from(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void array_setter(XmlObject[] sources, QName elementName) {
        this._locale.enter();
        try {
            int n;
            int m = sources.length;
            ArrayList<Xobj> copies = new ArrayList<Xobj>();
            ArrayList<SchemaType> types = new ArrayList<SchemaType>();
            for (XmlObject source : sources) {
                if (source == null) {
                    throw new IllegalArgumentException("Array element null");
                }
                if (source.isImmutable()) {
                    copies.add(null);
                    types.add(null);
                    continue;
                }
                Xobj x2 = (Xobj)((TypeStoreUser)((Object)source)).get_store();
                if (x2._locale == this._locale) {
                    copies.add(x2.copyNode(this._locale));
                } else {
                    x2._locale.enter();
                    try {
                        copies.add(x2.copyNode(this._locale));
                    }
                    finally {
                        x2._locale.exit();
                    }
                }
                types.add(source.schemaType());
            }
            for (n = this.count_elements(elementName); n > m; --n) {
                this.remove_element(elementName, m);
            }
            while (m > n) {
                this.add_element_user(elementName);
                ++n;
            }
            assert (m == n);
            ArrayList elementsUser = new ArrayList();
            this.find_all_element_users(elementName, elementsUser);
            List elements = elementsUser.stream().map(x -> (TypeStoreUser)((Object)x)).map(TypeStoreUser::get_store).map(x -> (Xobj)x).collect(Collectors.toList());
            assert (elements.size() == n);
            Cur c = this.tempCur();
            for (int i = 0; i < n; ++i) {
                Xobj x3 = (Xobj)elements.get(i);
                if (sources[i].isImmutable()) {
                    x3.getObject().set(sources[i]);
                    continue;
                }
                Cur.moveNodeContents(x3, null, true);
                c.moveTo(x3);
                c.next();
                Cur.moveNodeContents((Xobj)copies.get(i), c, true);
                x3.change_type((SchemaType)types.get(i));
            }
            c.release();
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public void visit_elements(TypeStoreVisitor visitor) {
        throw new RuntimeException("Not implemeneted");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlObject[] exec_query(String queryExpr, XmlOptions options) {
        this._locale.enter();
        try {
            Cur c = this.tempCur();
            XmlObject[] result = XPathFactory.objectExecQuery(c, queryExpr, options);
            c.release();
            XmlObject[] xmlObjectArray = result;
            return xmlObjectArray;
        }
        finally {
            this._locale.exit();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String find_prefix_for_nsuri(String nsuri, String suggested_prefix) {
        this._locale.enter();
        try {
            String string = this.prefixForNamespace(nsuri, suggested_prefix, true);
            return string;
        }
        finally {
            this._locale.exit();
        }
    }

    @Override
    public String getNamespaceForPrefix(String prefix) {
        return this.namespaceForPrefix(prefix, true);
    }
}

