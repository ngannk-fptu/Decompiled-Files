/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath.xmlbeans;

import java.util.ConcurrentModificationException;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.xpath.XPath;
import org.apache.xmlbeans.impl.xpath.XPathEngine;
import org.apache.xmlbeans.impl.xpath.XPathExecutionContext;

class XmlbeansXPathEngine
extends XPathExecutionContext
implements XPathEngine {
    private final long _version;
    private Cur _cur;

    XmlbeansXPathEngine(XPath xpath, Cur c) {
        assert (c.isContainer());
        this._version = c.getLocale().version();
        this._cur = c.weakCur(this);
        this._cur.push();
        this.init(xpath);
        int ret = this.start();
        if ((ret & 1) != 0) {
            c.addToSelection();
        }
        this.doAttrs(ret, c);
        if ((ret & 2) == 0 || !Locale.toFirstChildElement(this._cur)) {
            this.release();
        }
    }

    private void advance(Cur c) {
        assert (this._cur != null);
        if (this._cur.isFinish()) {
            if (this._cur.isAtEndOfLastPush()) {
                this.release();
            } else {
                this.end();
                this._cur.next();
            }
        } else if (this._cur.isElem()) {
            int ret = this.element(this._cur.getName());
            if ((ret & 1) != 0) {
                c.addToSelection(this._cur);
            }
            this.doAttrs(ret, c);
            if ((ret & 2) == 0 || !Locale.toFirstChildElement(this._cur)) {
                this.end();
                this._cur.skip();
            }
        } else {
            do {
                this._cur.next();
            } while (!this._cur.isContainerOrFinish());
        }
    }

    private void doAttrs(int ret, Cur c) {
        assert (this._cur.isContainer());
        if ((ret & 4) != 0 && this._cur.toFirstAttr()) {
            do {
                if (!this.attr(this._cur.getName())) continue;
                c.addToSelection(this._cur);
            } while (this._cur.toNextAttr());
            this._cur.toParent();
        }
    }

    @Override
    public boolean next(Cur c) {
        if (this._cur != null && this._version != this._cur.getLocale().version()) {
            throw new ConcurrentModificationException("Document changed during select");
        }
        int startCount = c.selectionCount();
        while (this._cur != null) {
            this.advance(c);
            if (startCount == c.selectionCount()) continue;
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        if (this._cur != null) {
            this._cur.release();
            this._cur = null;
        }
    }
}

