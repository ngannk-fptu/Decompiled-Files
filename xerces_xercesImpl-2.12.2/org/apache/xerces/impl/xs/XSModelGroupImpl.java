/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;

public class XSModelGroupImpl
implements XSModelGroup {
    public static final short MODELGROUP_CHOICE = 101;
    public static final short MODELGROUP_SEQUENCE = 102;
    public static final short MODELGROUP_ALL = 103;
    public short fCompositor;
    public XSParticleDecl[] fParticles = null;
    public int fParticleCount = 0;
    public XSObjectList fAnnotations = null;
    private String fDescription = null;

    public boolean isEmpty() {
        for (int i = 0; i < this.fParticleCount; ++i) {
            if (this.fParticles[i].isEmpty()) continue;
            return false;
        }
        return true;
    }

    public int minEffectiveTotalRange() {
        if (this.fCompositor == 101) {
            return this.minEffectiveTotalRangeChoice();
        }
        return this.minEffectiveTotalRangeAllSeq();
    }

    private int minEffectiveTotalRangeAllSeq() {
        int n = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            n += this.fParticles[i].minEffectiveTotalRange();
        }
        return n;
    }

    private int minEffectiveTotalRangeChoice() {
        int n = 0;
        if (this.fParticleCount > 0) {
            n = this.fParticles[0].minEffectiveTotalRange();
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            int n2 = this.fParticles[i].minEffectiveTotalRange();
            if (n2 >= n) continue;
            n = n2;
        }
        return n;
    }

    public int maxEffectiveTotalRange() {
        if (this.fCompositor == 101) {
            return this.maxEffectiveTotalRangeChoice();
        }
        return this.maxEffectiveTotalRangeAllSeq();
    }

    private int maxEffectiveTotalRangeAllSeq() {
        int n = 0;
        for (int i = 0; i < this.fParticleCount; ++i) {
            int n2 = this.fParticles[i].maxEffectiveTotalRange();
            if (n2 == -1) {
                return -1;
            }
            n += n2;
        }
        return n;
    }

    private int maxEffectiveTotalRangeChoice() {
        int n = 0;
        if (this.fParticleCount > 0 && (n = this.fParticles[0].maxEffectiveTotalRange()) == -1) {
            return -1;
        }
        for (int i = 1; i < this.fParticleCount; ++i) {
            int n2 = this.fParticles[i].maxEffectiveTotalRange();
            if (n2 == -1) {
                return -1;
            }
            if (n2 <= n) continue;
            n = n2;
        }
        return n;
    }

    public String toString() {
        if (this.fDescription == null) {
            StringBuffer stringBuffer = new StringBuffer();
            if (this.fCompositor == 103) {
                stringBuffer.append("all(");
            } else {
                stringBuffer.append('(');
            }
            if (this.fParticleCount > 0) {
                stringBuffer.append(this.fParticles[0].toString());
            }
            for (int i = 1; i < this.fParticleCount; ++i) {
                if (this.fCompositor == 101) {
                    stringBuffer.append('|');
                } else {
                    stringBuffer.append(',');
                }
                stringBuffer.append(this.fParticles[i].toString());
            }
            stringBuffer.append(')');
            this.fDescription = stringBuffer.toString();
        }
        return this.fDescription;
    }

    public void reset() {
        this.fCompositor = (short)102;
        this.fParticles = null;
        this.fParticleCount = 0;
        this.fDescription = null;
        this.fAnnotations = null;
    }

    @Override
    public short getType() {
        return 7;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public short getCompositor() {
        if (this.fCompositor == 101) {
            return 2;
        }
        if (this.fCompositor == 102) {
            return 1;
        }
        return 3;
    }

    @Override
    public XSObjectList getParticles() {
        return new XSObjectListImpl(this.fParticles, this.fParticleCount);
    }

    @Override
    public XSAnnotation getAnnotation() {
        return this.fAnnotations != null ? (XSAnnotation)this.fAnnotations.item(0) : null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }
}

