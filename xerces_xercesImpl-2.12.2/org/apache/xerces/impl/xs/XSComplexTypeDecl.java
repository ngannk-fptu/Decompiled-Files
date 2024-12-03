/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.TypeInfo;

public class XSComplexTypeDecl
implements XSComplexTypeDefinition,
TypeInfo {
    String fName = null;
    String fTargetNamespace = null;
    XSTypeDefinition fBaseType = null;
    short fDerivedBy = (short)2;
    short fFinal = 0;
    short fBlock = 0;
    short fMiscFlags = 0;
    XSAttributeGroupDecl fAttrGrp = null;
    short fContentType = 0;
    XSSimpleType fXSSimpleType = null;
    XSParticleDecl fParticle = null;
    XSCMValidator fCMValidator = null;
    XSCMValidator fUPACMValidator = null;
    XSObjectListImpl fAnnotations = null;
    private XSNamespaceItem fNamespaceItem = null;
    static final int DERIVATION_ANY = 0;
    static final int DERIVATION_RESTRICTION = 1;
    static final int DERIVATION_EXTENSION = 2;
    static final int DERIVATION_UNION = 4;
    static final int DERIVATION_LIST = 8;
    private static final short CT_IS_ABSTRACT = 1;
    private static final short CT_HAS_TYPE_ID = 2;
    private static final short CT_IS_ANONYMOUS = 4;

    public void setValues(String string, String string2, XSTypeDefinition xSTypeDefinition, short s, short s2, short s3, short s4, boolean bl, XSAttributeGroupDecl xSAttributeGroupDecl, XSSimpleType xSSimpleType, XSParticleDecl xSParticleDecl, XSObjectListImpl xSObjectListImpl) {
        this.fTargetNamespace = string2;
        this.fBaseType = xSTypeDefinition;
        this.fDerivedBy = s;
        this.fFinal = s2;
        this.fBlock = s3;
        this.fContentType = s4;
        if (bl) {
            this.fMiscFlags = (short)(this.fMiscFlags | 1);
        }
        this.fAttrGrp = xSAttributeGroupDecl;
        this.fXSSimpleType = xSSimpleType;
        this.fParticle = xSParticleDecl;
        this.fAnnotations = xSObjectListImpl;
    }

    public void setName(String string) {
        this.fName = string;
    }

    @Override
    public short getTypeCategory() {
        return 15;
    }

    @Override
    public String getTypeName() {
        return this.fName;
    }

    public short getFinalSet() {
        return this.fFinal;
    }

    public String getTargetNamespace() {
        return this.fTargetNamespace;
    }

    public boolean containsTypeID() {
        return (this.fMiscFlags & 2) != 0;
    }

    public void setIsAbstractType() {
        this.fMiscFlags = (short)(this.fMiscFlags | 1);
    }

    public void setContainsTypeID() {
        this.fMiscFlags = (short)(this.fMiscFlags | 2);
    }

    public void setIsAnonymous() {
        this.fMiscFlags = (short)(this.fMiscFlags | 4);
    }

    public XSCMValidator getContentModel(CMBuilder cMBuilder) {
        return this.getContentModel(cMBuilder, false);
    }

    public synchronized XSCMValidator getContentModel(CMBuilder cMBuilder, boolean bl) {
        if (this.fCMValidator == null) {
            if (bl) {
                if (this.fUPACMValidator == null) {
                    this.fUPACMValidator = cMBuilder.getContentModel(this, true);
                    if (this.fUPACMValidator != null && !this.fUPACMValidator.isCompactedForUPA()) {
                        this.fCMValidator = this.fUPACMValidator;
                    }
                }
                return this.fUPACMValidator;
            }
            this.fCMValidator = cMBuilder.getContentModel(this, false);
        }
        return this.fCMValidator;
    }

    public XSAttributeGroupDecl getAttrGrp() {
        return this.fAttrGrp;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        this.appendTypeInfo(stringBuffer);
        return stringBuffer.toString();
    }

    void appendTypeInfo(StringBuffer stringBuffer) {
        String[] stringArray = new String[]{"EMPTY", "SIMPLE", "ELEMENT", "MIXED"};
        String[] stringArray2 = new String[]{"EMPTY", "EXTENSION", "RESTRICTION"};
        stringBuffer.append("Complex type name='").append(this.fTargetNamespace).append(',').append(this.getTypeName()).append("', ");
        if (this.fBaseType != null) {
            stringBuffer.append(" base type name='").append(this.fBaseType.getName()).append("', ");
        }
        stringBuffer.append(" content type='").append(stringArray[this.fContentType]).append("', ");
        stringBuffer.append(" isAbstract='").append(this.getAbstract()).append("', ");
        stringBuffer.append(" hasTypeId='").append(this.containsTypeID()).append("', ");
        stringBuffer.append(" final='").append(this.fFinal).append("', ");
        stringBuffer.append(" block='").append(this.fBlock).append("', ");
        if (this.fParticle != null) {
            stringBuffer.append(" particle='").append(this.fParticle.toString()).append("', ");
        }
        stringBuffer.append(" derivedBy='").append(stringArray2[this.fDerivedBy]).append("'. ");
    }

    @Override
    public boolean derivedFromType(XSTypeDefinition xSTypeDefinition, short s) {
        XSTypeDefinition xSTypeDefinition2;
        if (xSTypeDefinition == null) {
            return false;
        }
        if (xSTypeDefinition == SchemaGrammar.fAnyType) {
            return true;
        }
        for (xSTypeDefinition2 = this; xSTypeDefinition2 != xSTypeDefinition && xSTypeDefinition2 != SchemaGrammar.fAnySimpleType && xSTypeDefinition2 != SchemaGrammar.fAnyType; xSTypeDefinition2 = xSTypeDefinition2.getBaseType()) {
        }
        return xSTypeDefinition2 == xSTypeDefinition;
    }

    @Override
    public boolean derivedFrom(String string, String string2, short s) {
        XSTypeDefinition xSTypeDefinition;
        if (string2 == null) {
            return false;
        }
        if (string != null && string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anyType")) {
            return true;
        }
        for (xSTypeDefinition = this; !(string2.equals(xSTypeDefinition.getName()) && (string == null && xSTypeDefinition.getNamespace() == null || string != null && string.equals(xSTypeDefinition.getNamespace())) || xSTypeDefinition == SchemaGrammar.fAnySimpleType || xSTypeDefinition == SchemaGrammar.fAnyType); xSTypeDefinition = xSTypeDefinition.getBaseType()) {
        }
        return xSTypeDefinition != SchemaGrammar.fAnySimpleType && xSTypeDefinition != SchemaGrammar.fAnyType;
    }

    public boolean isDOMDerivedFrom(String string, String string2, int n) {
        if (string2 == null) {
            return false;
        }
        if (string != null && string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anyType") && n == 1 && n == 2) {
            return true;
        }
        if ((n & 1) != 0 && this.isDerivedByRestriction(string, string2, n, this)) {
            return true;
        }
        if ((n & 2) != 0 && this.isDerivedByExtension(string, string2, n, this)) {
            return true;
        }
        if (((n & 8) != 0 || (n & 4) != 0) && (n & 1) == 0 && (n & 2) == 0) {
            if (string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anyType")) {
                string2 = "anySimpleType";
            }
            if (!this.fName.equals("anyType") || !this.fTargetNamespace.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
                if (this.fBaseType != null && this.fBaseType instanceof XSSimpleTypeDecl) {
                    return ((XSSimpleTypeDecl)this.fBaseType).isDOMDerivedFrom(string, string2, n);
                }
                if (this.fBaseType != null && this.fBaseType instanceof XSComplexTypeDecl) {
                    return ((XSComplexTypeDecl)this.fBaseType).isDOMDerivedFrom(string, string2, n);
                }
            }
        }
        if ((n & 2) == 0 && (n & 1) == 0 && (n & 8) == 0 && (n & 4) == 0) {
            return this.isDerivedByAny(string, string2, n, this);
        }
        return false;
    }

    private boolean isDerivedByAny(String string, String string2, int n, XSTypeDefinition xSTypeDefinition) {
        XSTypeDefinition xSTypeDefinition2 = null;
        boolean bl = false;
        while (xSTypeDefinition != null && xSTypeDefinition != xSTypeDefinition2) {
            if (string2.equals(xSTypeDefinition.getName()) && (string == null && xSTypeDefinition.getNamespace() == null || string != null && string.equals(xSTypeDefinition.getNamespace()))) {
                bl = true;
                break;
            }
            if (this.isDerivedByRestriction(string, string2, n, xSTypeDefinition)) {
                return true;
            }
            if (!this.isDerivedByExtension(string, string2, n, xSTypeDefinition)) {
                return true;
            }
            xSTypeDefinition2 = xSTypeDefinition;
            xSTypeDefinition = xSTypeDefinition.getBaseType();
        }
        return bl;
    }

    private boolean isDerivedByRestriction(String string, String string2, int n, XSTypeDefinition xSTypeDefinition) {
        XSTypeDefinition xSTypeDefinition2 = null;
        while (xSTypeDefinition != null && xSTypeDefinition != xSTypeDefinition2) {
            if (string != null && string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anySimpleType")) {
                return false;
            }
            if (string2.equals(xSTypeDefinition.getName()) && string != null && string.equals(xSTypeDefinition.getNamespace()) || xSTypeDefinition.getNamespace() == null && string == null) {
                return true;
            }
            if (xSTypeDefinition instanceof XSSimpleTypeDecl) {
                if (string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anyType")) {
                    string2 = "anySimpleType";
                }
                return ((XSSimpleTypeDecl)xSTypeDefinition).isDOMDerivedFrom(string, string2, n);
            }
            if (((XSComplexTypeDecl)xSTypeDefinition).getDerivationMethod() != 2) {
                return false;
            }
            xSTypeDefinition2 = xSTypeDefinition;
            xSTypeDefinition = xSTypeDefinition.getBaseType();
        }
        return false;
    }

    private boolean isDerivedByExtension(String string, String string2, int n, XSTypeDefinition xSTypeDefinition) {
        boolean bl = false;
        XSTypeDefinition xSTypeDefinition2 = null;
        while (!(xSTypeDefinition == null || xSTypeDefinition == xSTypeDefinition2 || string != null && string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anySimpleType") && SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xSTypeDefinition.getNamespace()) && "anyType".equals(xSTypeDefinition.getName()))) {
            if (string2.equals(xSTypeDefinition.getName()) && (string == null && xSTypeDefinition.getNamespace() == null || string != null && string.equals(xSTypeDefinition.getNamespace()))) {
                return bl;
            }
            if (xSTypeDefinition instanceof XSSimpleTypeDecl) {
                if (string.equals(SchemaSymbols.URI_SCHEMAFORSCHEMA) && string2.equals("anyType")) {
                    string2 = "anySimpleType";
                }
                if ((n & 2) != 0) {
                    return bl & ((XSSimpleTypeDecl)xSTypeDefinition).isDOMDerivedFrom(string, string2, n & 1);
                }
                return bl & ((XSSimpleTypeDecl)xSTypeDefinition).isDOMDerivedFrom(string, string2, n);
            }
            if (((XSComplexTypeDecl)xSTypeDefinition).getDerivationMethod() == 1) {
                bl |= true;
            }
            xSTypeDefinition2 = xSTypeDefinition;
            xSTypeDefinition = xSTypeDefinition.getBaseType();
        }
        return false;
    }

    public void reset() {
        this.fName = null;
        this.fTargetNamespace = null;
        this.fBaseType = null;
        this.fDerivedBy = (short)2;
        this.fFinal = 0;
        this.fBlock = 0;
        this.fMiscFlags = 0;
        this.fAttrGrp.reset();
        this.fContentType = 0;
        this.fXSSimpleType = null;
        this.fParticle = null;
        this.fCMValidator = null;
        this.fUPACMValidator = null;
        if (this.fAnnotations != null) {
            this.fAnnotations.clearXSObjectList();
        }
        this.fAnnotations = null;
    }

    @Override
    public short getType() {
        return 3;
    }

    @Override
    public String getName() {
        return this.getAnonymous() ? null : this.fName;
    }

    @Override
    public boolean getAnonymous() {
        return (this.fMiscFlags & 4) != 0;
    }

    @Override
    public String getNamespace() {
        return this.fTargetNamespace;
    }

    @Override
    public XSTypeDefinition getBaseType() {
        return this.fBaseType;
    }

    @Override
    public short getDerivationMethod() {
        return this.fDerivedBy;
    }

    @Override
    public boolean isFinal(short s) {
        return (this.fFinal & s) != 0;
    }

    @Override
    public short getFinal() {
        return this.fFinal;
    }

    @Override
    public boolean getAbstract() {
        return (this.fMiscFlags & 1) != 0;
    }

    @Override
    public XSObjectList getAttributeUses() {
        return this.fAttrGrp.getAttributeUses();
    }

    @Override
    public XSWildcard getAttributeWildcard() {
        return this.fAttrGrp.getAttributeWildcard();
    }

    @Override
    public short getContentType() {
        return this.fContentType;
    }

    @Override
    public XSSimpleTypeDefinition getSimpleType() {
        return this.fXSSimpleType;
    }

    @Override
    public XSParticle getParticle() {
        return this.fParticle;
    }

    @Override
    public boolean isProhibitedSubstitution(short s) {
        return (this.fBlock & s) != 0;
    }

    @Override
    public short getProhibitedSubstitutions() {
        return this.fBlock;
    }

    @Override
    public XSObjectList getAnnotations() {
        return this.fAnnotations != null ? this.fAnnotations : XSObjectListImpl.EMPTY_LIST;
    }

    @Override
    public XSNamespaceItem getNamespaceItem() {
        return this.fNamespaceItem;
    }

    void setNamespaceItem(XSNamespaceItem xSNamespaceItem) {
        this.fNamespaceItem = xSNamespaceItem;
    }

    public XSAttributeUse getAttributeUse(String string, String string2) {
        return this.fAttrGrp.getAttributeUse(string, string2);
    }

    @Override
    public String getTypeNamespace() {
        return this.getNamespace();
    }

    @Override
    public boolean isDerivedFrom(String string, String string2, int n) {
        return this.isDOMDerivedFrom(string, string2, n);
    }
}

