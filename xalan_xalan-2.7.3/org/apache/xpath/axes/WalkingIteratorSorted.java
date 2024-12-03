/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xpath.axes;

import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.axes.AxesWalker;
import org.apache.xpath.axes.WalkerFactory;
import org.apache.xpath.axes.WalkingIterator;
import org.apache.xpath.compiler.Compiler;

public class WalkingIteratorSorted
extends WalkingIterator {
    static final long serialVersionUID = -4512512007542368213L;
    protected boolean m_inNaturalOrderStatic = false;

    public WalkingIteratorSorted(PrefixResolver nscontext) {
        super(nscontext);
    }

    WalkingIteratorSorted(Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers) throws TransformerException {
        super(compiler, opPos, analysis, shouldLoadWalkers);
    }

    @Override
    public boolean isDocOrdered() {
        return this.m_inNaturalOrderStatic;
    }

    boolean canBeWalkedInNaturalDocOrderStatic() {
        if (null != this.m_firstWalker) {
            AxesWalker walker = this.m_firstWalker;
            int prevAxis = -1;
            boolean prevIsSimpleDownAxis = true;
            int i = 0;
            while (null != walker) {
                int axis = walker.getAxis();
                if (walker.isDocOrdered()) {
                    boolean isSimpleDownAxis;
                    boolean bl = isSimpleDownAxis = axis == 3 || axis == 13 || axis == 19;
                    if (!isSimpleDownAxis && axis != -1) {
                        boolean isLastWalker;
                        boolean bl2 = isLastWalker = null == walker.getNextWalker();
                        return isLastWalker && (walker.isDocOrdered() && (axis == 4 || axis == 5 || axis == 17 || axis == 18) || axis == 2);
                    }
                } else {
                    return false;
                }
                walker = walker.getNextWalker();
                ++i;
            }
            return true;
        }
        return false;
    }

    @Override
    public void fixupVariables(Vector vars, int globalsSize) {
        super.fixupVariables(vars, globalsSize);
        int analysis = this.getAnalysisBits();
        this.m_inNaturalOrderStatic = WalkerFactory.isNaturalDocOrder(analysis);
    }
}

