/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.impl.store.Cursor;
import org.apache.xmlbeans.impl.store.Xobj;

class Bookmark
implements XmlCursor.XmlMark {
    Xobj _xobj;
    int _pos;
    Bookmark _next;
    Bookmark _prev;
    Object _key;
    Object _value;

    Bookmark() {
    }

    boolean isOnList(Bookmark head) {
        while (head != null) {
            if (head == this) {
                return true;
            }
            head = head._next;
        }
        return false;
    }

    Bookmark listInsert(Bookmark head) {
        assert (this._next == null && this._prev == null);
        if (head == null) {
            head = this._prev = this;
        } else {
            this._prev = head._prev;
            head._prev = head._prev._next = this;
        }
        return head;
    }

    Bookmark listRemove(Bookmark head) {
        assert (this._prev != null && this.isOnList(head));
        if (this._prev == this) {
            head = null;
        } else {
            if (head == this) {
                head = this._next;
            } else {
                this._prev._next = this._next;
            }
            if (this._next == null) {
                if (head != null) {
                    head._prev = this._prev;
                }
            } else {
                this._next._prev = this._prev;
                this._next = null;
            }
        }
        this._prev = null;
        assert (this._next == null);
        return head;
    }

    void moveTo(Xobj x, int p) {
        assert (this.isOnList(this._xobj._bookmarks));
        if (this._xobj != x) {
            this._xobj._bookmarks = this.listRemove(this._xobj._bookmarks);
            x._bookmarks = this.listInsert(x._bookmarks);
            this._xobj = x;
        }
        this._pos = p;
    }

    @Override
    public XmlCursor createCursor() {
        if (this._xobj == null) {
            throw new IllegalStateException("Attempting to create a cursor on a bookmark that has been cleared or replaced.");
        }
        return Cursor.newCursor(this._xobj, this._pos);
    }
}

