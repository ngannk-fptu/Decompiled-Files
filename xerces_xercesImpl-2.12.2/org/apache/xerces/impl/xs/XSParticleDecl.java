/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;

public class XSParticleDecl
implements XSParticle {
    public static final short PARTICLE_EMPTY = 0;
    public static final short PARTICLE_ELEMENT = 1;
    public static final short PARTICLE_WILDCARD = 2;
    public static final short PARTICLE_MODELGROUP = 3;
    public static final short PARTICLE_ZERO_OR_MORE = 4;
    public static final short PARTICLE_ZERO_OR_ONE = 5;
    public static final short PARTICLE_ONE_OR_MORE = 6;
    public short fType = 0;
    public XSTerm fValue = null;
    public int fMinOccurs = 1;
    public int fMaxOccurs = 1;
    public XSObjectList fAnnotations = null;
    private String fDescription = null;

    public XSParticleDecl makeClone() {
        XSParticleDecl xSParticleDecl = new XSParticleDecl();
        xSParticleDecl.fType = this.fType;
        xSParticleDecl.fMinOccurs = this.fMinOccurs;
        xSParticleDecl.fMaxOccurs = this.fMaxOccurs;
        xSParticleDecl.fDescription = this.fDescription;
        xSParticleDecl.fValue = this.fValue;
        xSParticleDecl.fAnnotations = this.fAnnotations;
        return xSParticleDecl;
    }

    public boolean emptiable() {
        return this.minEffectiveTotalRange() == 0;
    }

    public boolean isEmpty() {
        if (this.fType == 0) {
            return true;
        }
        if (this.fType == 1 || this.fType == 2) {
            return false;
        }
        return ((XSModelGroupImpl)this.fValue).isEmpty();
    }

    public int minEffectiveTotalRange() {
        if (this.fType == 0) {
            return 0;
        }
        if (this.fType == 3) {
            return ((XSModelGroupImpl)this.fValue).minEffectiveTotalRange() * this.fMinOccurs;
        }
        return this.fMinOccurs;
    }

    public int maxEffectiveTotalRange() {
        if (this.fType == 0) {
            return 0;
        }
        if (this.fType == 3) {
            int n = ((XSModelGroupImpl)this.fValue).maxEffectiveTotalRange();
            if (n == -1) {
                return -1;
            }
            if (n != 0 && this.fMaxOccurs == -1) {
                return -1;
            }
            return n * this.fMaxOccurs;
        }
        return this.fMaxOccurs;
    }

    public String toString() {
        if (this.fDescription == null) {
            StringBuffer stringBuffer = new StringBuffer();
            this.appendParticle(stringBuffer);
            if (!(this.fMinOccurs == 0 && this.fMaxOccurs == 0 || this.fMinOccurs == 1 && this.fMaxOccurs == 1)) {
                stringBuffer.append('{').append(this.fMinOccurs);
                if (this.fMaxOccurs == -1) {
                    stringBuffer.append("-UNBOUNDED");
                } else if (this.fMinOccurs != this.fMaxOccurs) {
                    stringBuffer.append('-').append(this.fMaxOccurs);
                }
                stringBuffer.append('}');
            }
            this.fDescription = stringBuffer.toString();
        }
        return this.fDescription;
    }

    void appendParticle(StringBuffer stringBuffer) {
        switch (this.fType) {
            case 0: {
                stringBuffer.append("EMPTY");
                break;
            }
            case 1: {
                stringBuffer.append(this.fValue.toString());
                break;
            }
            case 2: {
                stringBuffer.append('(');
                stringBuffer.append(this.fValue.toString());
                stringBuffer.append(')');
                break;
            }
            case 3: {
                stringBuffer.append(this.fValue.toString());
            }
        }
    }

    public void reset() {
        this.fType = 0;
        this.fValue = null;
        this.fMinOccurs = 1;
        this.fMaxOccurs = 1;
        this.fDescription = null;
        this.fAnnotations = null;
    }

    @Override
    public short getType() {
        return 8;
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
    public int getMinOccurs() {
        return this.fMinOccurs;
    }

    @Override
    public boolean getMaxOccursUnbounded() {
        return this.fMaxOccurs == -1;
    }

    @Override
    public int getMaxOccurs() {
        return this.fMaxOccurs;
    }

    @Override
    public XSTerm getTerm() {
        return this.fValue;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return null;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }
}

