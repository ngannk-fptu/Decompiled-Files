/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.io.Serializable;
import java.util.ArrayList;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.WrappedRuntimeException;

public final class IteratorPool
implements Serializable {
    static final long serialVersionUID = -460927331149566998L;
    private final DTMIterator m_orig;
    private final ArrayList m_freeStack;

    public IteratorPool(DTMIterator original) {
        this.m_orig = original;
        this.m_freeStack = new ArrayList();
    }

    public synchronized DTMIterator getInstanceOrThrow() throws CloneNotSupportedException {
        if (this.m_freeStack.isEmpty()) {
            return (DTMIterator)this.m_orig.clone();
        }
        DTMIterator result = (DTMIterator)this.m_freeStack.remove(this.m_freeStack.size() - 1);
        return result;
    }

    public synchronized DTMIterator getInstance() {
        if (this.m_freeStack.isEmpty()) {
            try {
                return (DTMIterator)this.m_orig.clone();
            }
            catch (Exception ex) {
                throw new WrappedRuntimeException(ex);
            }
        }
        DTMIterator result = (DTMIterator)this.m_freeStack.remove(this.m_freeStack.size() - 1);
        return result;
    }

    public synchronized void freeInstance(DTMIterator obj) {
        this.m_freeStack.add(obj);
    }
}

