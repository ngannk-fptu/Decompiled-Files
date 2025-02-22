/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import javax.xml.transform.TransformerException;
import org.apache.xpath.axes.ChildTestIterator;
import org.apache.xpath.compiler.Compiler;

public class AttributeIterator
extends ChildTestIterator {
    static final long serialVersionUID = -8417986700712229686L;

    AttributeIterator(Compiler compiler, int opPos, int analysis) throws TransformerException {
        super(compiler, opPos, analysis);
    }

    @Override
    protected int getNextNode() {
        this.m_lastFetched = -1 == this.m_lastFetched ? this.m_cdtm.getFirstAttribute(this.m_context) : this.m_cdtm.getNextAttribute(this.m_lastFetched);
        return this.m_lastFetched;
    }

    @Override
    public int getAxis() {
        return 2;
    }
}

