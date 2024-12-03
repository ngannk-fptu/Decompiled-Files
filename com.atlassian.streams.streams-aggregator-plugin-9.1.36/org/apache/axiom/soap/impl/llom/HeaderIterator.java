/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import java.util.Iterator;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.llom.Checker;

public class HeaderIterator
implements Iterator {
    SOAPHeaderBlock current;
    boolean advance = false;
    Checker checker;

    public HeaderIterator(SOAPHeader header) {
        this(header, null);
    }

    public HeaderIterator(SOAPHeader header, Checker checker) {
        this.checker = checker;
        this.current = (SOAPHeaderBlock)header.getFirstElement();
        if (this.current != null && !this.checkHeader(this.current)) {
            this.advance = true;
            this.hasNext();
        }
    }

    public void remove() {
    }

    public boolean checkHeader(SOAPHeaderBlock header) {
        if (this.checker == null) {
            return true;
        }
        return this.checker.checkHeader(header);
    }

    public boolean hasNext() {
        if (!this.advance) {
            return this.current != null;
        }
        this.advance = false;
        for (OMNode sibling = this.current.getNextOMSibling(); sibling != null; sibling = sibling.getNextOMSibling()) {
            SOAPHeaderBlock possible;
            if (!(sibling instanceof SOAPHeaderBlock) || !this.checkHeader(possible = (SOAPHeaderBlock)sibling)) continue;
            this.current = (SOAPHeaderBlock)sibling;
            return true;
        }
        this.current = null;
        return false;
    }

    public Object next() {
        SOAPHeaderBlock ret = this.current;
        if (ret != null) {
            this.advance = true;
            this.hasNext();
        }
        return ret;
    }
}

