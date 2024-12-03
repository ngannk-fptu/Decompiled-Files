/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;

public final class XSDeclarationPool {
    private static final int CHUNK_SHIFT = 8;
    private static final int CHUNK_SIZE = 256;
    private static final int CHUNK_MASK = 255;
    private static final int INITIAL_CHUNK_COUNT = 4;
    private XSElementDecl[][] fElementDecl = new XSElementDecl[4][];
    private int fElementDeclIndex = 0;
    private XSParticleDecl[][] fParticleDecl = new XSParticleDecl[4][];
    private int fParticleDeclIndex = 0;
    private XSModelGroupImpl[][] fModelGroup = new XSModelGroupImpl[4][];
    private int fModelGroupIndex = 0;
    private XSAttributeDecl[][] fAttrDecl = new XSAttributeDecl[4][];
    private int fAttrDeclIndex = 0;
    private XSComplexTypeDecl[][] fCTDecl = new XSComplexTypeDecl[4][];
    private int fCTDeclIndex = 0;
    private XSSimpleTypeDecl[][] fSTDecl = new XSSimpleTypeDecl[4][];
    private int fSTDeclIndex = 0;
    private XSAttributeUseImpl[][] fAttributeUse = new XSAttributeUseImpl[4][];
    private int fAttributeUseIndex = 0;
    private SchemaDVFactoryImpl dvFactory;

    public void setDVFactory(SchemaDVFactoryImpl schemaDVFactoryImpl) {
        this.dvFactory = schemaDVFactoryImpl;
    }

    public final XSElementDecl getElementDecl() {
        int n = this.fElementDeclIndex >> 8;
        int n2 = this.fElementDeclIndex & 0xFF;
        this.ensureElementDeclCapacity(n);
        if (this.fElementDecl[n][n2] == null) {
            this.fElementDecl[n][n2] = new XSElementDecl();
        } else {
            this.fElementDecl[n][n2].reset();
        }
        ++this.fElementDeclIndex;
        return this.fElementDecl[n][n2];
    }

    public final XSAttributeDecl getAttributeDecl() {
        int n = this.fAttrDeclIndex >> 8;
        int n2 = this.fAttrDeclIndex & 0xFF;
        this.ensureAttrDeclCapacity(n);
        if (this.fAttrDecl[n][n2] == null) {
            this.fAttrDecl[n][n2] = new XSAttributeDecl();
        } else {
            this.fAttrDecl[n][n2].reset();
        }
        ++this.fAttrDeclIndex;
        return this.fAttrDecl[n][n2];
    }

    public final XSAttributeUseImpl getAttributeUse() {
        int n = this.fAttributeUseIndex >> 8;
        int n2 = this.fAttributeUseIndex & 0xFF;
        this.ensureAttributeUseCapacity(n);
        if (this.fAttributeUse[n][n2] == null) {
            this.fAttributeUse[n][n2] = new XSAttributeUseImpl();
        } else {
            this.fAttributeUse[n][n2].reset();
        }
        ++this.fAttributeUseIndex;
        return this.fAttributeUse[n][n2];
    }

    public final XSComplexTypeDecl getComplexTypeDecl() {
        int n = this.fCTDeclIndex >> 8;
        int n2 = this.fCTDeclIndex & 0xFF;
        this.ensureCTDeclCapacity(n);
        if (this.fCTDecl[n][n2] == null) {
            this.fCTDecl[n][n2] = new XSComplexTypeDecl();
        } else {
            this.fCTDecl[n][n2].reset();
        }
        ++this.fCTDeclIndex;
        return this.fCTDecl[n][n2];
    }

    public final XSSimpleTypeDecl getSimpleTypeDecl() {
        int n = this.fSTDeclIndex >> 8;
        int n2 = this.fSTDeclIndex & 0xFF;
        this.ensureSTDeclCapacity(n);
        if (this.fSTDecl[n][n2] == null) {
            this.fSTDecl[n][n2] = this.dvFactory.newXSSimpleTypeDecl();
        } else {
            this.fSTDecl[n][n2].reset();
        }
        ++this.fSTDeclIndex;
        return this.fSTDecl[n][n2];
    }

    public final XSParticleDecl getParticleDecl() {
        int n = this.fParticleDeclIndex >> 8;
        int n2 = this.fParticleDeclIndex & 0xFF;
        this.ensureParticleDeclCapacity(n);
        if (this.fParticleDecl[n][n2] == null) {
            this.fParticleDecl[n][n2] = new XSParticleDecl();
        } else {
            this.fParticleDecl[n][n2].reset();
        }
        ++this.fParticleDeclIndex;
        return this.fParticleDecl[n][n2];
    }

    public final XSModelGroupImpl getModelGroup() {
        int n = this.fModelGroupIndex >> 8;
        int n2 = this.fModelGroupIndex & 0xFF;
        this.ensureModelGroupCapacity(n);
        if (this.fModelGroup[n][n2] == null) {
            this.fModelGroup[n][n2] = new XSModelGroupImpl();
        } else {
            this.fModelGroup[n][n2].reset();
        }
        ++this.fModelGroupIndex;
        return this.fModelGroup[n][n2];
    }

    private boolean ensureElementDeclCapacity(int n) {
        if (n >= this.fElementDecl.length) {
            this.fElementDecl = XSDeclarationPool.resize(this.fElementDecl, this.fElementDecl.length * 2);
        } else if (this.fElementDecl[n] != null) {
            return false;
        }
        this.fElementDecl[n] = new XSElementDecl[256];
        return true;
    }

    private static XSElementDecl[][] resize(XSElementDecl[][] xSElementDeclArray, int n) {
        XSElementDecl[][] xSElementDeclArray2 = new XSElementDecl[n][];
        System.arraycopy(xSElementDeclArray, 0, xSElementDeclArray2, 0, xSElementDeclArray.length);
        return xSElementDeclArray2;
    }

    private boolean ensureParticleDeclCapacity(int n) {
        if (n >= this.fParticleDecl.length) {
            this.fParticleDecl = XSDeclarationPool.resize(this.fParticleDecl, this.fParticleDecl.length * 2);
        } else if (this.fParticleDecl[n] != null) {
            return false;
        }
        this.fParticleDecl[n] = new XSParticleDecl[256];
        return true;
    }

    private boolean ensureModelGroupCapacity(int n) {
        if (n >= this.fModelGroup.length) {
            this.fModelGroup = XSDeclarationPool.resize(this.fModelGroup, this.fModelGroup.length * 2);
        } else if (this.fModelGroup[n] != null) {
            return false;
        }
        this.fModelGroup[n] = new XSModelGroupImpl[256];
        return true;
    }

    private static XSParticleDecl[][] resize(XSParticleDecl[][] xSParticleDeclArray, int n) {
        XSParticleDecl[][] xSParticleDeclArray2 = new XSParticleDecl[n][];
        System.arraycopy(xSParticleDeclArray, 0, xSParticleDeclArray2, 0, xSParticleDeclArray.length);
        return xSParticleDeclArray2;
    }

    private static XSModelGroupImpl[][] resize(XSModelGroupImpl[][] xSModelGroupImplArray, int n) {
        XSModelGroupImpl[][] xSModelGroupImplArray2 = new XSModelGroupImpl[n][];
        System.arraycopy(xSModelGroupImplArray, 0, xSModelGroupImplArray2, 0, xSModelGroupImplArray.length);
        return xSModelGroupImplArray2;
    }

    private boolean ensureAttrDeclCapacity(int n) {
        if (n >= this.fAttrDecl.length) {
            this.fAttrDecl = XSDeclarationPool.resize(this.fAttrDecl, this.fAttrDecl.length * 2);
        } else if (this.fAttrDecl[n] != null) {
            return false;
        }
        this.fAttrDecl[n] = new XSAttributeDecl[256];
        return true;
    }

    private static XSAttributeDecl[][] resize(XSAttributeDecl[][] xSAttributeDeclArray, int n) {
        XSAttributeDecl[][] xSAttributeDeclArray2 = new XSAttributeDecl[n][];
        System.arraycopy(xSAttributeDeclArray, 0, xSAttributeDeclArray2, 0, xSAttributeDeclArray.length);
        return xSAttributeDeclArray2;
    }

    private boolean ensureAttributeUseCapacity(int n) {
        if (n >= this.fAttributeUse.length) {
            this.fAttributeUse = XSDeclarationPool.resize(this.fAttributeUse, this.fAttributeUse.length * 2);
        } else if (this.fAttributeUse[n] != null) {
            return false;
        }
        this.fAttributeUse[n] = new XSAttributeUseImpl[256];
        return true;
    }

    private static XSAttributeUseImpl[][] resize(XSAttributeUseImpl[][] xSAttributeUseImplArray, int n) {
        XSAttributeUseImpl[][] xSAttributeUseImplArray2 = new XSAttributeUseImpl[n][];
        System.arraycopy(xSAttributeUseImplArray, 0, xSAttributeUseImplArray2, 0, xSAttributeUseImplArray.length);
        return xSAttributeUseImplArray2;
    }

    private boolean ensureSTDeclCapacity(int n) {
        if (n >= this.fSTDecl.length) {
            this.fSTDecl = XSDeclarationPool.resize(this.fSTDecl, this.fSTDecl.length * 2);
        } else if (this.fSTDecl[n] != null) {
            return false;
        }
        this.fSTDecl[n] = new XSSimpleTypeDecl[256];
        return true;
    }

    private static XSSimpleTypeDecl[][] resize(XSSimpleTypeDecl[][] xSSimpleTypeDeclArray, int n) {
        XSSimpleTypeDecl[][] xSSimpleTypeDeclArray2 = new XSSimpleTypeDecl[n][];
        System.arraycopy(xSSimpleTypeDeclArray, 0, xSSimpleTypeDeclArray2, 0, xSSimpleTypeDeclArray.length);
        return xSSimpleTypeDeclArray2;
    }

    private boolean ensureCTDeclCapacity(int n) {
        if (n >= this.fCTDecl.length) {
            this.fCTDecl = XSDeclarationPool.resize(this.fCTDecl, this.fCTDecl.length * 2);
        } else if (this.fCTDecl[n] != null) {
            return false;
        }
        this.fCTDecl[n] = new XSComplexTypeDecl[256];
        return true;
    }

    private static XSComplexTypeDecl[][] resize(XSComplexTypeDecl[][] xSComplexTypeDeclArray, int n) {
        XSComplexTypeDecl[][] xSComplexTypeDeclArray2 = new XSComplexTypeDecl[n][];
        System.arraycopy(xSComplexTypeDeclArray, 0, xSComplexTypeDeclArray2, 0, xSComplexTypeDeclArray.length);
        return xSComplexTypeDeclArray2;
    }

    public void reset() {
        this.fElementDeclIndex = 0;
        this.fParticleDeclIndex = 0;
        this.fModelGroupIndex = 0;
        this.fSTDeclIndex = 0;
        this.fCTDeclIndex = 0;
        this.fAttrDeclIndex = 0;
        this.fAttributeUseIndex = 0;
    }
}

