/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XNIException;

final class BalancedDTDGrammar
extends DTDGrammar {
    private boolean fMixed;
    private int fDepth = 0;
    private short[] fOpStack = null;
    private int[][] fGroupIndexStack;
    private int[] fGroupIndexStackSizes;

    public BalancedDTDGrammar(SymbolTable symbolTable, XMLDTDDescription xMLDTDDescription) {
        super(symbolTable, xMLDTDDescription);
    }

    @Override
    public final void startContentModel(String string, Augmentations augmentations) throws XNIException {
        this.fDepth = 0;
        this.initializeContentModelStacks();
        super.startContentModel(string, augmentations);
    }

    @Override
    public final void startGroup(Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        this.initializeContentModelStacks();
        this.fMixed = false;
    }

    @Override
    public final void pcdata(Augmentations augmentations) throws XNIException {
        this.fMixed = true;
    }

    @Override
    public final void element(String string, Augmentations augmentations) throws XNIException {
        this.addToCurrentGroup(this.addUniqueLeafNode(string));
    }

    @Override
    public final void separator(short s, Augmentations augmentations) throws XNIException {
        if (s == 0) {
            this.fOpStack[this.fDepth] = 4;
        } else if (s == 1) {
            this.fOpStack[this.fDepth] = 5;
        }
    }

    @Override
    public final void occurrence(short s, Augmentations augmentations) throws XNIException {
        if (!this.fMixed) {
            int n = this.fGroupIndexStackSizes[this.fDepth] - 1;
            if (s == 2) {
                this.fGroupIndexStack[this.fDepth][n] = this.addContentSpecNode((short)1, this.fGroupIndexStack[this.fDepth][n], -1);
            } else if (s == 3) {
                this.fGroupIndexStack[this.fDepth][n] = this.addContentSpecNode((short)2, this.fGroupIndexStack[this.fDepth][n], -1);
            } else if (s == 4) {
                this.fGroupIndexStack[this.fDepth][n] = this.addContentSpecNode((short)3, this.fGroupIndexStack[this.fDepth][n], -1);
            }
        }
    }

    @Override
    public final void endGroup(Augmentations augmentations) throws XNIException {
        int n = this.fGroupIndexStackSizes[this.fDepth];
        int n2 = n > 0 ? this.addContentSpecNodes(0, n - 1) : this.addUniqueLeafNode(null);
        --this.fDepth;
        this.addToCurrentGroup(n2);
    }

    @Override
    public final void endDTD(Augmentations augmentations) throws XNIException {
        super.endDTD(augmentations);
        this.fOpStack = null;
        this.fGroupIndexStack = null;
        this.fGroupIndexStackSizes = null;
    }

    @Override
    protected final void addContentSpecToElement(XMLElementDecl xMLElementDecl) {
        int n = this.fGroupIndexStackSizes[0] > 0 ? this.fGroupIndexStack[0][0] : -1;
        this.setContentSpecIndex(this.fCurrentElementIndex, n);
    }

    private int addContentSpecNodes(int n, int n2) {
        if (n == n2) {
            return this.fGroupIndexStack[this.fDepth][n];
        }
        int n3 = n + n2 >>> 1;
        return this.addContentSpecNode(this.fOpStack[this.fDepth], this.addContentSpecNodes(n, n3), this.addContentSpecNodes(n3 + 1, n2));
    }

    private void initializeContentModelStacks() {
        if (this.fOpStack == null) {
            this.fOpStack = new short[8];
            this.fGroupIndexStack = new int[8][];
            this.fGroupIndexStackSizes = new int[8];
        } else if (this.fDepth == this.fOpStack.length) {
            short[] sArray = new short[this.fDepth * 2];
            System.arraycopy(this.fOpStack, 0, sArray, 0, this.fDepth);
            this.fOpStack = sArray;
            int[][] nArrayArray = new int[this.fDepth * 2][];
            System.arraycopy(this.fGroupIndexStack, 0, nArrayArray, 0, this.fDepth);
            this.fGroupIndexStack = nArrayArray;
            int[] nArray = new int[this.fDepth * 2];
            System.arraycopy(this.fGroupIndexStackSizes, 0, nArray, 0, this.fDepth);
            this.fGroupIndexStackSizes = nArray;
        }
        this.fOpStack[this.fDepth] = -1;
        this.fGroupIndexStackSizes[this.fDepth] = 0;
    }

    private void addToCurrentGroup(int n) {
        int[] nArray = this.fGroupIndexStack[this.fDepth];
        int n2 = this.fDepth;
        int n3 = this.fGroupIndexStackSizes[n2];
        this.fGroupIndexStackSizes[n2] = n3 + 1;
        int n4 = n3;
        if (nArray == null) {
            nArray = new int[8];
            this.fGroupIndexStack[this.fDepth] = nArray;
        } else if (n4 == nArray.length) {
            int[] nArray2 = new int[nArray.length * 2];
            System.arraycopy(nArray, 0, nArray2, 0, nArray.length);
            nArray = nArray2;
            this.fGroupIndexStack[this.fDepth] = nArray;
        }
        nArray[n4] = n;
    }
}

