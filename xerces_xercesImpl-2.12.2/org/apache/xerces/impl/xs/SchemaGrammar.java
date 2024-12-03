/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.lang.ref.SoftReference;
import java.util.Vector;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.util.ObjectListImpl;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.impl.xs.util.XSNamedMap4Types;
import org.apache.xerces.impl.xs.util.XSNamedMapImpl;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAnnotation;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSIDCDefinition;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNotationDeclaration;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.xml.sax.SAXException;

public class SchemaGrammar
implements XSGrammar,
XSNamespaceItem {
    String fTargetNamespace;
    SymbolHash fGlobalAttrDecls;
    SymbolHash fGlobalAttrGrpDecls;
    SymbolHash fGlobalElemDecls;
    SymbolHash fGlobalGroupDecls;
    SymbolHash fGlobalNotationDecls;
    SymbolHash fGlobalIDConstraintDecls;
    SymbolHash fGlobalTypeDecls;
    SymbolHash fGlobalAttrDeclsExt;
    SymbolHash fGlobalAttrGrpDeclsExt;
    SymbolHash fGlobalElemDeclsExt;
    SymbolHash fGlobalGroupDeclsExt;
    SymbolHash fGlobalNotationDeclsExt;
    SymbolHash fGlobalIDConstraintDeclsExt;
    SymbolHash fGlobalTypeDeclsExt;
    SymbolHash fAllGlobalElemDecls;
    XSDDescription fGrammarDescription = null;
    XSAnnotationImpl[] fAnnotations = null;
    int fNumAnnotations;
    private SymbolTable fSymbolTable = null;
    private SoftReference fSAXParser = null;
    private SoftReference fDOMParser = null;
    private boolean fIsImmutable = false;
    private static final int BASICSET_COUNT = 29;
    private static final int FULLSET_COUNT = 46;
    private static final int GRAMMAR_XS = 1;
    private static final int GRAMMAR_XSI = 2;
    Vector fImported = null;
    private static final int INITIAL_SIZE = 16;
    private static final int INC_SIZE = 16;
    private int fCTCount = 0;
    private XSComplexTypeDecl[] fComplexTypeDecls = new XSComplexTypeDecl[16];
    private SimpleLocator[] fCTLocators = new SimpleLocator[16];
    private static final int REDEFINED_GROUP_INIT_SIZE = 2;
    private int fRGCount = 0;
    private XSGroupDecl[] fRedefinedGroupDecls = new XSGroupDecl[2];
    private SimpleLocator[] fRGLocators = new SimpleLocator[1];
    boolean fFullChecked = false;
    private int fSubGroupCount = 0;
    private XSElementDecl[] fSubGroups = new XSElementDecl[16];
    public static final XSComplexTypeDecl fAnyType = new XSAnyType();
    public static final BuiltinSchemaGrammar SG_SchemaNS = new BuiltinSchemaGrammar(1, 1);
    private static final BuiltinSchemaGrammar SG_SchemaNSExtended = new BuiltinSchemaGrammar(1, 2);
    public static final XSSimpleType fAnySimpleType = (XSSimpleType)SG_SchemaNS.getGlobalTypeDecl("anySimpleType");
    public static final BuiltinSchemaGrammar SG_XSI = new BuiltinSchemaGrammar(2, 1);
    private static final short MAX_COMP_IDX = 16;
    private static final boolean[] GLOBAL_COMP = new boolean[]{false, true, true, true, false, true, true, false, false, false, true, true, false, false, false, true, true};
    private XSNamedMap[] fComponents = null;
    private ObjectList[] fComponentsExt = null;
    private Vector fDocuments = null;
    private Vector fLocations = null;

    protected SchemaGrammar() {
    }

    public SchemaGrammar(String string, XSDDescription xSDDescription, SymbolTable symbolTable) {
        this.fTargetNamespace = string;
        this.fGrammarDescription = xSDDescription;
        this.fSymbolTable = symbolTable;
        this.fGlobalAttrDecls = new SymbolHash(12);
        this.fGlobalAttrGrpDecls = new SymbolHash(5);
        this.fGlobalElemDecls = new SymbolHash(25);
        this.fGlobalGroupDecls = new SymbolHash(5);
        this.fGlobalNotationDecls = new SymbolHash(1);
        this.fGlobalIDConstraintDecls = new SymbolHash(3);
        this.fGlobalAttrDeclsExt = new SymbolHash(12);
        this.fGlobalAttrGrpDeclsExt = new SymbolHash(5);
        this.fGlobalElemDeclsExt = new SymbolHash(25);
        this.fGlobalGroupDeclsExt = new SymbolHash(5);
        this.fGlobalNotationDeclsExt = new SymbolHash(1);
        this.fGlobalIDConstraintDeclsExt = new SymbolHash(3);
        this.fGlobalTypeDeclsExt = new SymbolHash(25);
        this.fAllGlobalElemDecls = new SymbolHash(25);
        this.fGlobalTypeDecls = this.fTargetNamespace == SchemaSymbols.URI_SCHEMAFORSCHEMA ? SchemaGrammar.SG_SchemaNS.fGlobalTypeDecls.makeClone() : new SymbolHash(25);
    }

    public SchemaGrammar(SchemaGrammar schemaGrammar) {
        int n;
        this.fTargetNamespace = schemaGrammar.fTargetNamespace;
        this.fGrammarDescription = schemaGrammar.fGrammarDescription.makeClone();
        this.fSymbolTable = schemaGrammar.fSymbolTable;
        this.fGlobalAttrDecls = schemaGrammar.fGlobalAttrDecls.makeClone();
        this.fGlobalAttrGrpDecls = schemaGrammar.fGlobalAttrGrpDecls.makeClone();
        this.fGlobalElemDecls = schemaGrammar.fGlobalElemDecls.makeClone();
        this.fGlobalGroupDecls = schemaGrammar.fGlobalGroupDecls.makeClone();
        this.fGlobalNotationDecls = schemaGrammar.fGlobalNotationDecls.makeClone();
        this.fGlobalIDConstraintDecls = schemaGrammar.fGlobalIDConstraintDecls.makeClone();
        this.fGlobalTypeDecls = schemaGrammar.fGlobalTypeDecls.makeClone();
        this.fGlobalAttrDeclsExt = schemaGrammar.fGlobalAttrDeclsExt.makeClone();
        this.fGlobalAttrGrpDeclsExt = schemaGrammar.fGlobalAttrGrpDeclsExt.makeClone();
        this.fGlobalElemDeclsExt = schemaGrammar.fGlobalElemDeclsExt.makeClone();
        this.fGlobalGroupDeclsExt = schemaGrammar.fGlobalGroupDeclsExt.makeClone();
        this.fGlobalNotationDeclsExt = schemaGrammar.fGlobalNotationDeclsExt.makeClone();
        this.fGlobalIDConstraintDeclsExt = schemaGrammar.fGlobalIDConstraintDeclsExt.makeClone();
        this.fGlobalTypeDeclsExt = schemaGrammar.fGlobalTypeDeclsExt.makeClone();
        this.fAllGlobalElemDecls = schemaGrammar.fAllGlobalElemDecls.makeClone();
        this.fNumAnnotations = schemaGrammar.fNumAnnotations;
        if (this.fNumAnnotations > 0) {
            this.fAnnotations = new XSAnnotationImpl[schemaGrammar.fAnnotations.length];
            System.arraycopy(schemaGrammar.fAnnotations, 0, this.fAnnotations, 0, this.fNumAnnotations);
        }
        this.fSubGroupCount = schemaGrammar.fSubGroupCount;
        if (this.fSubGroupCount > 0) {
            this.fSubGroups = new XSElementDecl[schemaGrammar.fSubGroups.length];
            System.arraycopy(schemaGrammar.fSubGroups, 0, this.fSubGroups, 0, this.fSubGroupCount);
        }
        this.fCTCount = schemaGrammar.fCTCount;
        if (this.fCTCount > 0) {
            this.fComplexTypeDecls = new XSComplexTypeDecl[schemaGrammar.fComplexTypeDecls.length];
            this.fCTLocators = new SimpleLocator[schemaGrammar.fCTLocators.length];
            System.arraycopy(schemaGrammar.fComplexTypeDecls, 0, this.fComplexTypeDecls, 0, this.fCTCount);
            System.arraycopy(schemaGrammar.fCTLocators, 0, this.fCTLocators, 0, this.fCTCount);
        }
        this.fRGCount = schemaGrammar.fRGCount;
        if (this.fRGCount > 0) {
            this.fRedefinedGroupDecls = new XSGroupDecl[schemaGrammar.fRedefinedGroupDecls.length];
            this.fRGLocators = new SimpleLocator[schemaGrammar.fRGLocators.length];
            System.arraycopy(schemaGrammar.fRedefinedGroupDecls, 0, this.fRedefinedGroupDecls, 0, this.fRGCount);
            System.arraycopy(schemaGrammar.fRGLocators, 0, this.fRGLocators, 0, this.fRGCount / 2);
        }
        if (schemaGrammar.fImported != null) {
            this.fImported = new Vector();
            for (n = 0; n < schemaGrammar.fImported.size(); ++n) {
                this.fImported.add(schemaGrammar.fImported.elementAt(n));
            }
        }
        if (schemaGrammar.fLocations != null) {
            for (n = 0; n < schemaGrammar.fLocations.size(); ++n) {
                this.addDocument(null, (String)schemaGrammar.fLocations.elementAt(n));
            }
        }
    }

    @Override
    public XMLGrammarDescription getGrammarDescription() {
        return this.fGrammarDescription;
    }

    public boolean isNamespaceAware() {
        return true;
    }

    public void setImportedGrammars(Vector vector) {
        this.fImported = vector;
    }

    public Vector getImportedGrammars() {
        return this.fImported;
    }

    public final String getTargetNamespace() {
        return this.fTargetNamespace;
    }

    public void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl) {
        this.fGlobalAttrDecls.put(xSAttributeDecl.fName, xSAttributeDecl);
        xSAttributeDecl.setNamespaceItem(this);
    }

    public void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl, String string) {
        this.fGlobalAttrDeclsExt.put((string != null ? string : "") + "," + xSAttributeDecl.fName, xSAttributeDecl);
        if (xSAttributeDecl.getNamespaceItem() == null) {
            xSAttributeDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl) {
        this.fGlobalAttrGrpDecls.put(xSAttributeGroupDecl.fName, xSAttributeGroupDecl);
        xSAttributeGroupDecl.setNamespaceItem(this);
    }

    public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl, String string) {
        this.fGlobalAttrGrpDeclsExt.put((string != null ? string : "") + "," + xSAttributeGroupDecl.fName, xSAttributeGroupDecl);
        if (xSAttributeGroupDecl.getNamespaceItem() == null) {
            xSAttributeGroupDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalElementDeclAll(XSElementDecl xSElementDecl) {
        if (this.fAllGlobalElemDecls.get(xSElementDecl) == null) {
            this.fAllGlobalElemDecls.put(xSElementDecl, xSElementDecl);
            if (xSElementDecl.fSubGroup != null) {
                if (this.fSubGroupCount == this.fSubGroups.length) {
                    this.fSubGroups = SchemaGrammar.resize(this.fSubGroups, this.fSubGroupCount + 16);
                }
                this.fSubGroups[this.fSubGroupCount++] = xSElementDecl;
            }
        }
    }

    public void addGlobalElementDecl(XSElementDecl xSElementDecl) {
        this.fGlobalElemDecls.put(xSElementDecl.fName, xSElementDecl);
        xSElementDecl.setNamespaceItem(this);
    }

    public void addGlobalElementDecl(XSElementDecl xSElementDecl, String string) {
        this.fGlobalElemDeclsExt.put((string != null ? string : "") + "," + xSElementDecl.fName, xSElementDecl);
        if (xSElementDecl.getNamespaceItem() == null) {
            xSElementDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl) {
        this.fGlobalGroupDecls.put(xSGroupDecl.fName, xSGroupDecl);
        xSGroupDecl.setNamespaceItem(this);
    }

    public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl, String string) {
        this.fGlobalGroupDeclsExt.put((string != null ? string : "") + "," + xSGroupDecl.fName, xSGroupDecl);
        if (xSGroupDecl.getNamespaceItem() == null) {
            xSGroupDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl) {
        this.fGlobalNotationDecls.put(xSNotationDecl.fName, xSNotationDecl);
        xSNotationDecl.setNamespaceItem(this);
    }

    public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl, String string) {
        this.fGlobalNotationDeclsExt.put((string != null ? string : "") + "," + xSNotationDecl.fName, xSNotationDecl);
        if (xSNotationDecl.getNamespaceItem() == null) {
            xSNotationDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition) {
        this.fGlobalTypeDecls.put(xSTypeDefinition.getName(), xSTypeDefinition);
        if (xSTypeDefinition instanceof XSComplexTypeDecl) {
            ((XSComplexTypeDecl)xSTypeDefinition).setNamespaceItem(this);
        } else if (xSTypeDefinition instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xSTypeDefinition).setNamespaceItem(this);
        }
    }

    public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition, String string) {
        this.fGlobalTypeDeclsExt.put((string != null ? string : "") + "," + xSTypeDefinition.getName(), xSTypeDefinition);
        if (xSTypeDefinition.getNamespaceItem() == null) {
            if (xSTypeDefinition instanceof XSComplexTypeDecl) {
                ((XSComplexTypeDecl)xSTypeDefinition).setNamespaceItem(this);
            } else if (xSTypeDefinition instanceof XSSimpleTypeDecl) {
                ((XSSimpleTypeDecl)xSTypeDefinition).setNamespaceItem(this);
            }
        }
    }

    public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl) {
        this.fGlobalTypeDecls.put(xSComplexTypeDecl.getName(), xSComplexTypeDecl);
        xSComplexTypeDecl.setNamespaceItem(this);
    }

    public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, String string) {
        this.fGlobalTypeDeclsExt.put((string != null ? string : "") + "," + xSComplexTypeDecl.getName(), xSComplexTypeDecl);
        if (xSComplexTypeDecl.getNamespaceItem() == null) {
            xSComplexTypeDecl.setNamespaceItem(this);
        }
    }

    public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType) {
        this.fGlobalTypeDecls.put(xSSimpleType.getName(), xSSimpleType);
        if (xSSimpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xSSimpleType).setNamespaceItem(this);
        }
    }

    public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType, String string) {
        this.fGlobalTypeDeclsExt.put((string != null ? string : "") + "," + xSSimpleType.getName(), xSSimpleType);
        if (xSSimpleType.getNamespaceItem() == null && xSSimpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xSSimpleType).setNamespaceItem(this);
        }
    }

    public final void addIDConstraintDecl(XSElementDecl xSElementDecl, IdentityConstraint identityConstraint) {
        xSElementDecl.addIDConstraint(identityConstraint);
        this.fGlobalIDConstraintDecls.put(identityConstraint.getIdentityConstraintName(), identityConstraint);
    }

    public final void addIDConstraintDecl(XSElementDecl xSElementDecl, IdentityConstraint identityConstraint, String string) {
        this.fGlobalIDConstraintDeclsExt.put((string != null ? string : "") + "," + identityConstraint.getIdentityConstraintName(), identityConstraint);
    }

    public final XSAttributeDecl getGlobalAttributeDecl(String string) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(string);
    }

    public final XSAttributeDecl getGlobalAttributeDecl(String string, String string2) {
        return (XSAttributeDecl)this.fGlobalAttrDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String string) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(string);
    }

    public final XSAttributeGroupDecl getGlobalAttributeGroupDecl(String string, String string2) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final XSElementDecl getGlobalElementDecl(String string) {
        return (XSElementDecl)this.fGlobalElemDecls.get(string);
    }

    public final XSElementDecl getGlobalElementDecl(String string, String string2) {
        return (XSElementDecl)this.fGlobalElemDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final XSGroupDecl getGlobalGroupDecl(String string) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(string);
    }

    public final XSGroupDecl getGlobalGroupDecl(String string, String string2) {
        return (XSGroupDecl)this.fGlobalGroupDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final XSNotationDecl getGlobalNotationDecl(String string) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(string);
    }

    public final XSNotationDecl getGlobalNotationDecl(String string, String string2) {
        return (XSNotationDecl)this.fGlobalNotationDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final XSTypeDefinition getGlobalTypeDecl(String string) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(string);
    }

    public final XSTypeDefinition getGlobalTypeDecl(String string, String string2) {
        return (XSTypeDefinition)this.fGlobalTypeDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final IdentityConstraint getIDConstraintDecl(String string) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(string);
    }

    public final IdentityConstraint getIDConstraintDecl(String string, String string2) {
        return (IdentityConstraint)this.fGlobalIDConstraintDeclsExt.get((string2 != null ? string2 : "") + "," + string);
    }

    public final boolean hasIDConstraints() {
        return this.fGlobalIDConstraintDecls.getLength() > 0;
    }

    public void addComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, SimpleLocator simpleLocator) {
        if (this.fCTCount == this.fComplexTypeDecls.length) {
            this.fComplexTypeDecls = SchemaGrammar.resize(this.fComplexTypeDecls, this.fCTCount + 16);
            this.fCTLocators = SchemaGrammar.resize(this.fCTLocators, this.fCTCount + 16);
        }
        this.fCTLocators[this.fCTCount] = simpleLocator;
        this.fComplexTypeDecls[this.fCTCount++] = xSComplexTypeDecl;
    }

    public void addRedefinedGroupDecl(XSGroupDecl xSGroupDecl, XSGroupDecl xSGroupDecl2, SimpleLocator simpleLocator) {
        if (this.fRGCount == this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = SchemaGrammar.resize(this.fRedefinedGroupDecls, this.fRGCount << 1);
            this.fRGLocators = SchemaGrammar.resize(this.fRGLocators, this.fRGCount);
        }
        this.fRGLocators[this.fRGCount / 2] = simpleLocator;
        this.fRedefinedGroupDecls[this.fRGCount++] = xSGroupDecl;
        this.fRedefinedGroupDecls[this.fRGCount++] = xSGroupDecl2;
    }

    final XSComplexTypeDecl[] getUncheckedComplexTypeDecls() {
        if (this.fCTCount < this.fComplexTypeDecls.length) {
            this.fComplexTypeDecls = SchemaGrammar.resize(this.fComplexTypeDecls, this.fCTCount);
            this.fCTLocators = SchemaGrammar.resize(this.fCTLocators, this.fCTCount);
        }
        return this.fComplexTypeDecls;
    }

    final SimpleLocator[] getUncheckedCTLocators() {
        if (this.fCTCount < this.fCTLocators.length) {
            this.fComplexTypeDecls = SchemaGrammar.resize(this.fComplexTypeDecls, this.fCTCount);
            this.fCTLocators = SchemaGrammar.resize(this.fCTLocators, this.fCTCount);
        }
        return this.fCTLocators;
    }

    final XSGroupDecl[] getRedefinedGroupDecls() {
        if (this.fRGCount < this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = SchemaGrammar.resize(this.fRedefinedGroupDecls, this.fRGCount);
            this.fRGLocators = SchemaGrammar.resize(this.fRGLocators, this.fRGCount / 2);
        }
        return this.fRedefinedGroupDecls;
    }

    final SimpleLocator[] getRGLocators() {
        if (this.fRGCount < this.fRedefinedGroupDecls.length) {
            this.fRedefinedGroupDecls = SchemaGrammar.resize(this.fRedefinedGroupDecls, this.fRGCount);
            this.fRGLocators = SchemaGrammar.resize(this.fRGLocators, this.fRGCount / 2);
        }
        return this.fRGLocators;
    }

    final void setUncheckedTypeNum(int n) {
        this.fCTCount = n;
        this.fComplexTypeDecls = SchemaGrammar.resize(this.fComplexTypeDecls, this.fCTCount);
        this.fCTLocators = SchemaGrammar.resize(this.fCTLocators, this.fCTCount);
    }

    final XSElementDecl[] getSubstitutionGroups() {
        if (this.fSubGroupCount < this.fSubGroups.length) {
            this.fSubGroups = SchemaGrammar.resize(this.fSubGroups, this.fSubGroupCount);
        }
        return this.fSubGroups;
    }

    public static SchemaGrammar getS4SGrammar(short s) {
        if (s == 1) {
            return SG_SchemaNS;
        }
        return SG_SchemaNSExtended;
    }

    static final XSComplexTypeDecl[] resize(XSComplexTypeDecl[] xSComplexTypeDeclArray, int n) {
        XSComplexTypeDecl[] xSComplexTypeDeclArray2 = new XSComplexTypeDecl[n];
        System.arraycopy(xSComplexTypeDeclArray, 0, xSComplexTypeDeclArray2, 0, Math.min(xSComplexTypeDeclArray.length, n));
        return xSComplexTypeDeclArray2;
    }

    static final XSGroupDecl[] resize(XSGroupDecl[] xSGroupDeclArray, int n) {
        XSGroupDecl[] xSGroupDeclArray2 = new XSGroupDecl[n];
        System.arraycopy(xSGroupDeclArray, 0, xSGroupDeclArray2, 0, Math.min(xSGroupDeclArray.length, n));
        return xSGroupDeclArray2;
    }

    static final XSElementDecl[] resize(XSElementDecl[] xSElementDeclArray, int n) {
        XSElementDecl[] xSElementDeclArray2 = new XSElementDecl[n];
        System.arraycopy(xSElementDeclArray, 0, xSElementDeclArray2, 0, Math.min(xSElementDeclArray.length, n));
        return xSElementDeclArray2;
    }

    static final SimpleLocator[] resize(SimpleLocator[] simpleLocatorArray, int n) {
        SimpleLocator[] simpleLocatorArray2 = new SimpleLocator[n];
        System.arraycopy(simpleLocatorArray, 0, simpleLocatorArray2, 0, Math.min(simpleLocatorArray.length, n));
        return simpleLocatorArray2;
    }

    public synchronized void addDocument(Object object, String string) {
        if (this.fDocuments == null) {
            this.fDocuments = new Vector();
            this.fLocations = new Vector();
        }
        this.fDocuments.addElement(object);
        this.fLocations.addElement(string);
    }

    public synchronized void removeDocument(int n) {
        if (this.fDocuments != null && n >= 0 && n < this.fDocuments.size()) {
            this.fDocuments.removeElementAt(n);
            this.fLocations.removeElementAt(n);
        }
    }

    @Override
    public String getSchemaNamespace() {
        return this.fTargetNamespace;
    }

    synchronized DOMParser getDOMParser() {
        Object object;
        if (this.fDOMParser != null && (object = (DOMParser)this.fDOMParser.get()) != null) {
            return object;
        }
        object = new XML11Configuration(this.fSymbolTable);
        ((XML11Configuration)object).setFeature("http://xml.org/sax/features/namespaces", true);
        ((XML11Configuration)object).setFeature("http://xml.org/sax/features/validation", false);
        DOMParser dOMParser = new DOMParser((XMLParserConfiguration)object);
        try {
            dOMParser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        }
        catch (SAXException sAXException) {
            // empty catch block
        }
        this.fDOMParser = new SoftReference<DOMParser>(dOMParser);
        return dOMParser;
    }

    synchronized SAXParser getSAXParser() {
        Object object;
        if (this.fSAXParser != null && (object = (SAXParser)this.fSAXParser.get()) != null) {
            return object;
        }
        object = new XML11Configuration(this.fSymbolTable);
        ((XML11Configuration)object).setFeature("http://xml.org/sax/features/namespaces", true);
        ((XML11Configuration)object).setFeature("http://xml.org/sax/features/validation", false);
        SAXParser sAXParser = new SAXParser((XMLParserConfiguration)object);
        this.fSAXParser = new SoftReference<SAXParser>(sAXParser);
        return sAXParser;
    }

    @Override
    public synchronized XSNamedMap getComponents(short s) {
        if (s <= 0 || s > 16 || !GLOBAL_COMP[s]) {
            return XSNamedMapImpl.EMPTY_MAP;
        }
        if (this.fComponents == null) {
            this.fComponents = new XSNamedMap[17];
        }
        if (this.fComponents[s] == null) {
            SymbolHash symbolHash = null;
            switch (s) {
                case 3: 
                case 15: 
                case 16: {
                    symbolHash = this.fGlobalTypeDecls;
                    break;
                }
                case 1: {
                    symbolHash = this.fGlobalAttrDecls;
                    break;
                }
                case 2: {
                    symbolHash = this.fGlobalElemDecls;
                    break;
                }
                case 5: {
                    symbolHash = this.fGlobalAttrGrpDecls;
                    break;
                }
                case 6: {
                    symbolHash = this.fGlobalGroupDecls;
                    break;
                }
                case 11: {
                    symbolHash = this.fGlobalNotationDecls;
                    break;
                }
                case 10: {
                    symbolHash = this.fGlobalIDConstraintDecls;
                }
            }
            this.fComponents[s] = s == 15 || s == 16 ? new XSNamedMap4Types(this.fTargetNamespace, symbolHash, s) : new XSNamedMapImpl(this.fTargetNamespace, symbolHash);
        }
        return this.fComponents[s];
    }

    public synchronized ObjectList getComponentsExt(short s) {
        if (s <= 0 || s > 16 || !GLOBAL_COMP[s]) {
            return ObjectListImpl.EMPTY_LIST;
        }
        if (this.fComponentsExt == null) {
            this.fComponentsExt = new ObjectList[17];
        }
        if (this.fComponentsExt[s] == null) {
            SymbolHash symbolHash = null;
            switch (s) {
                case 3: 
                case 15: 
                case 16: {
                    symbolHash = this.fGlobalTypeDeclsExt;
                    break;
                }
                case 1: {
                    symbolHash = this.fGlobalAttrDeclsExt;
                    break;
                }
                case 2: {
                    symbolHash = this.fGlobalElemDeclsExt;
                    break;
                }
                case 5: {
                    symbolHash = this.fGlobalAttrGrpDeclsExt;
                    break;
                }
                case 6: {
                    symbolHash = this.fGlobalGroupDeclsExt;
                    break;
                }
                case 11: {
                    symbolHash = this.fGlobalNotationDeclsExt;
                    break;
                }
                case 10: {
                    symbolHash = this.fGlobalIDConstraintDeclsExt;
                }
            }
            Object[] objectArray = symbolHash.getEntries();
            this.fComponentsExt[s] = new ObjectListImpl(objectArray, objectArray.length);
        }
        return this.fComponentsExt[s];
    }

    public synchronized void resetComponents() {
        this.fComponents = null;
        this.fComponentsExt = null;
    }

    @Override
    public XSTypeDefinition getTypeDefinition(String string) {
        return this.getGlobalTypeDecl(string);
    }

    @Override
    public XSAttributeDeclaration getAttributeDeclaration(String string) {
        return this.getGlobalAttributeDecl(string);
    }

    @Override
    public XSElementDeclaration getElementDeclaration(String string) {
        return this.getGlobalElementDecl(string);
    }

    @Override
    public XSAttributeGroupDefinition getAttributeGroup(String string) {
        return this.getGlobalAttributeGroupDecl(string);
    }

    @Override
    public XSModelGroupDefinition getModelGroupDefinition(String string) {
        return this.getGlobalGroupDecl(string);
    }

    @Override
    public XSNotationDeclaration getNotationDeclaration(String string) {
        return this.getGlobalNotationDecl(string);
    }

    @Override
    public XSIDCDefinition getIDCDefinition(String string) {
        return this.getIDConstraintDecl(string);
    }

    @Override
    public StringList getDocumentLocations() {
        return new StringListImpl(this.fLocations);
    }

    @Override
    public XSModel toXSModel() {
        return new XSModelImpl(new SchemaGrammar[]{this});
    }

    @Override
    public XSModel toXSModel(XSGrammar[] xSGrammarArray) {
        if (xSGrammarArray == null || xSGrammarArray.length == 0) {
            return this.toXSModel();
        }
        int n = xSGrammarArray.length;
        boolean bl = false;
        for (int i = 0; i < n; ++i) {
            if (xSGrammarArray[i] != this) continue;
            bl = true;
            break;
        }
        SchemaGrammar[] schemaGrammarArray = new SchemaGrammar[bl ? n : n + 1];
        for (int i = 0; i < n; ++i) {
            schemaGrammarArray[i] = (SchemaGrammar)xSGrammarArray[i];
        }
        if (!bl) {
            schemaGrammarArray[n] = this;
        }
        return new XSModelImpl(schemaGrammarArray);
    }

    @Override
    public XSObjectList getAnnotations() {
        if (this.fNumAnnotations == 0) {
            return XSObjectListImpl.EMPTY_LIST;
        }
        return new XSObjectListImpl(this.fAnnotations, this.fNumAnnotations);
    }

    public void addAnnotation(XSAnnotationImpl xSAnnotationImpl) {
        if (xSAnnotationImpl == null) {
            return;
        }
        if (this.fAnnotations == null) {
            this.fAnnotations = new XSAnnotationImpl[2];
        } else if (this.fNumAnnotations == this.fAnnotations.length) {
            XSAnnotationImpl[] xSAnnotationImplArray = new XSAnnotationImpl[this.fNumAnnotations << 1];
            System.arraycopy(this.fAnnotations, 0, xSAnnotationImplArray, 0, this.fNumAnnotations);
            this.fAnnotations = xSAnnotationImplArray;
        }
        this.fAnnotations[this.fNumAnnotations++] = xSAnnotationImpl;
    }

    public void setImmutable(boolean bl) {
        this.fIsImmutable = bl;
    }

    public boolean isImmutable() {
        return this.fIsImmutable;
    }

    private static class BuiltinAttrDecl
    extends XSAttributeDecl {
        public BuiltinAttrDecl(String string, String string2, XSSimpleType xSSimpleType, short s) {
            this.fName = string;
            this.fTargetNamespace = string2;
            this.fType = xSSimpleType;
            this.fScope = s;
        }

        public void setValues(String string, String string2, XSSimpleType xSSimpleType, short s, short s2, ValidatedInfo validatedInfo, XSComplexTypeDecl xSComplexTypeDecl) {
        }

        @Override
        public void reset() {
        }

        @Override
        public XSAnnotation getAnnotation() {
            return null;
        }

        @Override
        public XSNamespaceItem getNamespaceItem() {
            return SG_XSI;
        }
    }

    private static class XSAnyType
    extends XSComplexTypeDecl {
        public XSAnyType() {
            this.fName = "anyType";
            this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fBaseType = this;
            this.fDerivedBy = (short)2;
            this.fContentType = (short)3;
            this.fParticle = this.createParticle();
            this.fAttrGrp = this.createAttrGrp();
        }

        public void setValues(String string, String string2, XSTypeDefinition xSTypeDefinition, short s, short s2, short s3, short s4, boolean bl, XSAttributeGroupDecl xSAttributeGroupDecl, XSSimpleType xSSimpleType, XSParticleDecl xSParticleDecl) {
        }

        @Override
        public void setName(String string) {
        }

        @Override
        public void setIsAbstractType() {
        }

        @Override
        public void setContainsTypeID() {
        }

        @Override
        public void setIsAnonymous() {
        }

        @Override
        public void reset() {
        }

        @Override
        public XSObjectList getAnnotations() {
            return XSObjectListImpl.EMPTY_LIST;
        }

        @Override
        public XSNamespaceItem getNamespaceItem() {
            return SG_SchemaNS;
        }

        private XSAttributeGroupDecl createAttrGrp() {
            XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
            xSWildcardDecl.fProcessContents = (short)3;
            XSAttributeGroupDecl xSAttributeGroupDecl = new XSAttributeGroupDecl();
            xSAttributeGroupDecl.fAttributeWC = xSWildcardDecl;
            return xSAttributeGroupDecl;
        }

        private XSParticleDecl createParticle() {
            XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
            xSWildcardDecl.fProcessContents = (short)3;
            XSParticleDecl xSParticleDecl = new XSParticleDecl();
            xSParticleDecl.fMinOccurs = 0;
            xSParticleDecl.fMaxOccurs = -1;
            xSParticleDecl.fType = (short)2;
            xSParticleDecl.fValue = xSWildcardDecl;
            XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
            xSModelGroupImpl.fCompositor = (short)102;
            xSModelGroupImpl.fParticleCount = 1;
            xSModelGroupImpl.fParticles = new XSParticleDecl[1];
            xSModelGroupImpl.fParticles[0] = xSParticleDecl;
            XSParticleDecl xSParticleDecl2 = new XSParticleDecl();
            xSParticleDecl2.fType = (short)3;
            xSParticleDecl2.fValue = xSModelGroupImpl;
            return xSParticleDecl2;
        }
    }

    public static final class Schema4Annotations
    extends SchemaGrammar {
        public static final Schema4Annotations INSTANCE = new Schema4Annotations();

        private Schema4Annotations() {
            this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
            this.fGrammarDescription = new XSDDescription();
            this.fGrammarDescription.fContextType = (short)3;
            this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
            this.fGlobalAttrDecls = new SymbolHash(1);
            this.fGlobalAttrGrpDecls = new SymbolHash(1);
            this.fGlobalElemDecls = new SymbolHash(6);
            this.fGlobalGroupDecls = new SymbolHash(1);
            this.fGlobalNotationDecls = new SymbolHash(1);
            this.fGlobalIDConstraintDecls = new SymbolHash(1);
            this.fGlobalAttrDeclsExt = new SymbolHash(1);
            this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
            this.fGlobalElemDeclsExt = new SymbolHash(6);
            this.fGlobalGroupDeclsExt = new SymbolHash(1);
            this.fGlobalNotationDeclsExt = new SymbolHash(1);
            this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
            this.fGlobalTypeDeclsExt = new SymbolHash(1);
            this.fAllGlobalElemDecls = new SymbolHash(6);
            this.fGlobalTypeDecls = Schema4Annotations.SG_SchemaNS.fGlobalTypeDecls;
            XSElementDecl xSElementDecl = this.createAnnotationElementDecl(SchemaSymbols.ELT_ANNOTATION);
            XSElementDecl xSElementDecl2 = this.createAnnotationElementDecl(SchemaSymbols.ELT_DOCUMENTATION);
            XSElementDecl xSElementDecl3 = this.createAnnotationElementDecl(SchemaSymbols.ELT_APPINFO);
            this.fGlobalElemDecls.put(xSElementDecl.fName, xSElementDecl);
            this.fGlobalElemDecls.put(xSElementDecl2.fName, xSElementDecl2);
            this.fGlobalElemDecls.put(xSElementDecl3.fName, xSElementDecl3);
            this.fGlobalElemDeclsExt.put("," + xSElementDecl.fName, xSElementDecl);
            this.fGlobalElemDeclsExt.put("," + xSElementDecl2.fName, xSElementDecl2);
            this.fGlobalElemDeclsExt.put("," + xSElementDecl3.fName, xSElementDecl3);
            this.fAllGlobalElemDecls.put(xSElementDecl, xSElementDecl);
            this.fAllGlobalElemDecls.put(xSElementDecl2, xSElementDecl2);
            this.fAllGlobalElemDecls.put(xSElementDecl3, xSElementDecl3);
            XSComplexTypeDecl xSComplexTypeDecl = new XSComplexTypeDecl();
            XSComplexTypeDecl xSComplexTypeDecl2 = new XSComplexTypeDecl();
            XSComplexTypeDecl xSComplexTypeDecl3 = new XSComplexTypeDecl();
            xSElementDecl.fType = xSComplexTypeDecl;
            xSElementDecl2.fType = xSComplexTypeDecl2;
            xSElementDecl3.fType = xSComplexTypeDecl3;
            XSAttributeGroupDecl xSAttributeGroupDecl = new XSAttributeGroupDecl();
            XSAttributeGroupDecl xSAttributeGroupDecl2 = new XSAttributeGroupDecl();
            XSAttributeGroupDecl xSAttributeGroupDecl3 = new XSAttributeGroupDecl();
            XSObject xSObject = new XSAttributeUseImpl();
            xSObject.fAttrDecl = new XSAttributeDecl();
            xSObject.fAttrDecl.setValues(SchemaSymbols.ATT_ID, null, (XSSimpleType)this.fGlobalTypeDecls.get("ID"), (short)0, (short)2, null, xSComplexTypeDecl, null);
            xSObject.fUse = 0;
            xSObject.fConstraintType = 0;
            XSObject xSObject2 = new XSAttributeUseImpl();
            xSObject2.fAttrDecl = new XSAttributeDecl();
            xSObject2.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, xSComplexTypeDecl2, null);
            xSObject2.fUse = 0;
            xSObject2.fConstraintType = 0;
            XSAttributeUseImpl xSAttributeUseImpl = new XSAttributeUseImpl();
            xSAttributeUseImpl.fAttrDecl = new XSAttributeDecl();
            xSAttributeUseImpl.fAttrDecl.setValues("lang".intern(), NamespaceContext.XML_URI, (XSSimpleType)this.fGlobalTypeDecls.get("language"), (short)0, (short)2, null, xSComplexTypeDecl2, null);
            xSAttributeUseImpl.fUse = 0;
            xSAttributeUseImpl.fConstraintType = 0;
            XSAttributeUseImpl xSAttributeUseImpl2 = new XSAttributeUseImpl();
            xSAttributeUseImpl2.fAttrDecl = new XSAttributeDecl();
            xSAttributeUseImpl2.fAttrDecl.setValues(SchemaSymbols.ATT_SOURCE, null, (XSSimpleType)this.fGlobalTypeDecls.get("anyURI"), (short)0, (short)2, null, xSComplexTypeDecl3, null);
            xSAttributeUseImpl2.fUse = 0;
            xSAttributeUseImpl2.fConstraintType = 0;
            XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
            xSWildcardDecl.fNamespaceList = new String[]{this.fTargetNamespace, null};
            xSWildcardDecl.fType = (short)2;
            xSWildcardDecl.fProcessContents = (short)3;
            xSAttributeGroupDecl.addAttributeUse((XSAttributeUseImpl)xSObject);
            xSAttributeGroupDecl.fAttributeWC = xSWildcardDecl;
            xSAttributeGroupDecl2.addAttributeUse((XSAttributeUseImpl)xSObject2);
            xSAttributeGroupDecl2.addAttributeUse(xSAttributeUseImpl);
            xSAttributeGroupDecl2.fAttributeWC = xSWildcardDecl;
            xSAttributeGroupDecl3.addAttributeUse(xSAttributeUseImpl2);
            xSAttributeGroupDecl3.fAttributeWC = xSWildcardDecl;
            xSObject = this.createUnboundedModelGroupParticle();
            xSObject2 = new XSModelGroupImpl();
            ((XSModelGroupImpl)xSObject2).fCompositor = (short)101;
            ((XSModelGroupImpl)xSObject2).fParticleCount = 2;
            ((XSModelGroupImpl)xSObject2).fParticles = new XSParticleDecl[2];
            ((XSModelGroupImpl)xSObject2).fParticles[0] = this.createChoiceElementParticle(xSElementDecl3);
            ((XSModelGroupImpl)xSObject2).fParticles[1] = this.createChoiceElementParticle(xSElementDecl2);
            ((XSParticleDecl)xSObject).fValue = xSObject2;
            xSObject2 = this.createUnboundedAnyWildcardSequenceParticle();
            xSComplexTypeDecl.setValues("#AnonType_" + SchemaSymbols.ELT_ANNOTATION, this.fTargetNamespace, fAnyType, (short)2, (short)0, (short)3, (short)2, false, xSAttributeGroupDecl, null, (XSParticleDecl)xSObject, XSObjectListImpl.EMPTY_LIST);
            xSComplexTypeDecl.setName("#AnonType_" + SchemaSymbols.ELT_ANNOTATION);
            xSComplexTypeDecl.setIsAnonymous();
            xSComplexTypeDecl2.setValues("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION, this.fTargetNamespace, fAnyType, (short)2, (short)0, (short)3, (short)3, false, xSAttributeGroupDecl2, null, (XSParticleDecl)xSObject2, XSObjectListImpl.EMPTY_LIST);
            xSComplexTypeDecl2.setName("#AnonType_" + SchemaSymbols.ELT_DOCUMENTATION);
            xSComplexTypeDecl2.setIsAnonymous();
            xSComplexTypeDecl3.setValues("#AnonType_" + SchemaSymbols.ELT_APPINFO, this.fTargetNamespace, fAnyType, (short)2, (short)0, (short)3, (short)3, false, xSAttributeGroupDecl3, null, (XSParticleDecl)xSObject2, XSObjectListImpl.EMPTY_LIST);
            xSComplexTypeDecl3.setName("#AnonType_" + SchemaSymbols.ELT_APPINFO);
            xSComplexTypeDecl3.setIsAnonymous();
        }

        @Override
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }

        @Override
        public void setImportedGrammars(Vector vector) {
        }

        @Override
        public void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl) {
        }

        public void addGlobalAttributeDecl(XSAttributeGroupDecl xSAttributeGroupDecl, String string) {
        }

        @Override
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl) {
        }

        @Override
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl, String string) {
        }

        @Override
        public void addGlobalElementDecl(XSElementDecl xSElementDecl) {
        }

        @Override
        public void addGlobalElementDecl(XSElementDecl xSElementDecl, String string) {
        }

        @Override
        public void addGlobalElementDeclAll(XSElementDecl xSElementDecl) {
        }

        @Override
        public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl) {
        }

        @Override
        public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl, String string) {
        }

        @Override
        public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl) {
        }

        @Override
        public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl, String string) {
        }

        @Override
        public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition) {
        }

        @Override
        public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition, String string) {
        }

        @Override
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl) {
        }

        @Override
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, String string) {
        }

        @Override
        public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType) {
        }

        @Override
        public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType, String string) {
        }

        @Override
        public void addComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, SimpleLocator simpleLocator) {
        }

        @Override
        public void addRedefinedGroupDecl(XSGroupDecl xSGroupDecl, XSGroupDecl xSGroupDecl2, SimpleLocator simpleLocator) {
        }

        @Override
        public synchronized void addDocument(Object object, String string) {
        }

        @Override
        synchronized DOMParser getDOMParser() {
            return null;
        }

        @Override
        synchronized SAXParser getSAXParser() {
            return null;
        }

        private XSElementDecl createAnnotationElementDecl(String string) {
            XSElementDecl xSElementDecl = new XSElementDecl();
            xSElementDecl.fName = string;
            xSElementDecl.fTargetNamespace = this.fTargetNamespace;
            xSElementDecl.setIsGlobal();
            xSElementDecl.fBlock = (short)7;
            xSElementDecl.setConstraintType((short)0);
            return xSElementDecl;
        }

        private XSParticleDecl createUnboundedModelGroupParticle() {
            XSParticleDecl xSParticleDecl = new XSParticleDecl();
            xSParticleDecl.fMinOccurs = 0;
            xSParticleDecl.fMaxOccurs = -1;
            xSParticleDecl.fType = (short)3;
            return xSParticleDecl;
        }

        private XSParticleDecl createChoiceElementParticle(XSElementDecl xSElementDecl) {
            XSParticleDecl xSParticleDecl = new XSParticleDecl();
            xSParticleDecl.fMinOccurs = 1;
            xSParticleDecl.fMaxOccurs = 1;
            xSParticleDecl.fType = 1;
            xSParticleDecl.fValue = xSElementDecl;
            return xSParticleDecl;
        }

        private XSParticleDecl createUnboundedAnyWildcardSequenceParticle() {
            XSParticleDecl xSParticleDecl = this.createUnboundedModelGroupParticle();
            XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
            xSModelGroupImpl.fCompositor = (short)102;
            xSModelGroupImpl.fParticleCount = 1;
            xSModelGroupImpl.fParticles = new XSParticleDecl[1];
            xSModelGroupImpl.fParticles[0] = this.createAnyLaxWildcardParticle();
            xSParticleDecl.fValue = xSModelGroupImpl;
            return xSParticleDecl;
        }

        private XSParticleDecl createAnyLaxWildcardParticle() {
            XSParticleDecl xSParticleDecl = new XSParticleDecl();
            xSParticleDecl.fMinOccurs = 1;
            xSParticleDecl.fMaxOccurs = 1;
            xSParticleDecl.fType = (short)2;
            XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
            xSWildcardDecl.fNamespaceList = null;
            xSWildcardDecl.fType = 1;
            xSWildcardDecl.fProcessContents = (short)3;
            xSParticleDecl.fValue = xSWildcardDecl;
            return xSParticleDecl;
        }
    }

    public static class BuiltinSchemaGrammar
    extends SchemaGrammar {
        private static final String EXTENDED_SCHEMA_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.ExtendedSchemaDVFactoryImpl";

        public BuiltinSchemaGrammar(int n, short s) {
            SchemaDVFactory schemaDVFactory = s == 1 ? SchemaDVFactory.getInstance() : SchemaDVFactory.getInstance(EXTENDED_SCHEMA_FACTORY_CLASS);
            if (n == 1) {
                this.fTargetNamespace = SchemaSymbols.URI_SCHEMAFORSCHEMA;
                this.fGrammarDescription = new XSDDescription();
                this.fGrammarDescription.fContextType = (short)3;
                this.fGrammarDescription.setNamespace(SchemaSymbols.URI_SCHEMAFORSCHEMA);
                this.fGlobalAttrDecls = new SymbolHash(1);
                this.fGlobalAttrGrpDecls = new SymbolHash(1);
                this.fGlobalElemDecls = new SymbolHash(1);
                this.fGlobalGroupDecls = new SymbolHash(1);
                this.fGlobalNotationDecls = new SymbolHash(1);
                this.fGlobalIDConstraintDecls = new SymbolHash(1);
                this.fGlobalAttrDeclsExt = new SymbolHash(1);
                this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                this.fGlobalElemDeclsExt = new SymbolHash(1);
                this.fGlobalGroupDeclsExt = new SymbolHash(1);
                this.fGlobalNotationDeclsExt = new SymbolHash(1);
                this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                this.fGlobalTypeDeclsExt = new SymbolHash(1);
                this.fAllGlobalElemDecls = new SymbolHash(1);
                this.fGlobalTypeDecls = schemaDVFactory.getBuiltInTypes();
                int n2 = this.fGlobalTypeDecls.getLength();
                Object[] objectArray = new XSTypeDefinition[n2];
                this.fGlobalTypeDecls.getValues(objectArray, 0);
                for (int i = 0; i < n2; ++i) {
                    Object object = objectArray[i];
                    if (!(object instanceof XSSimpleTypeDecl)) continue;
                    ((XSSimpleTypeDecl)object).setNamespaceItem(this);
                }
                this.fGlobalTypeDecls.put(fAnyType.getName(), fAnyType);
            } else if (n == 2) {
                this.fTargetNamespace = SchemaSymbols.URI_XSI;
                this.fGrammarDescription = new XSDDescription();
                this.fGrammarDescription.fContextType = (short)3;
                this.fGrammarDescription.setNamespace(SchemaSymbols.URI_XSI);
                this.fGlobalAttrGrpDecls = new SymbolHash(1);
                this.fGlobalElemDecls = new SymbolHash(1);
                this.fGlobalGroupDecls = new SymbolHash(1);
                this.fGlobalNotationDecls = new SymbolHash(1);
                this.fGlobalIDConstraintDecls = new SymbolHash(1);
                this.fGlobalTypeDecls = new SymbolHash(1);
                this.fGlobalAttrDeclsExt = new SymbolHash(1);
                this.fGlobalAttrGrpDeclsExt = new SymbolHash(1);
                this.fGlobalElemDeclsExt = new SymbolHash(1);
                this.fGlobalGroupDeclsExt = new SymbolHash(1);
                this.fGlobalNotationDeclsExt = new SymbolHash(1);
                this.fGlobalIDConstraintDeclsExt = new SymbolHash(1);
                this.fGlobalTypeDeclsExt = new SymbolHash(1);
                this.fAllGlobalElemDecls = new SymbolHash(1);
                this.fGlobalAttrDecls = new SymbolHash(8);
                String string = null;
                String string2 = null;
                XSSimpleType xSSimpleType = null;
                short s2 = 1;
                string = SchemaSymbols.XSI_TYPE;
                string2 = SchemaSymbols.URI_XSI;
                xSSimpleType = schemaDVFactory.getBuiltInType("QName");
                this.fGlobalAttrDecls.put(string, new BuiltinAttrDecl(string, string2, xSSimpleType, s2));
                string = SchemaSymbols.XSI_NIL;
                string2 = SchemaSymbols.URI_XSI;
                xSSimpleType = schemaDVFactory.getBuiltInType("boolean");
                this.fGlobalAttrDecls.put(string, new BuiltinAttrDecl(string, string2, xSSimpleType, s2));
                XSSimpleType xSSimpleType2 = schemaDVFactory.getBuiltInType("anyURI");
                string = SchemaSymbols.XSI_SCHEMALOCATION;
                string2 = SchemaSymbols.URI_XSI;
                xSSimpleType = schemaDVFactory.createTypeList("#AnonType_schemaLocation", SchemaSymbols.URI_XSI, (short)0, xSSimpleType2, null);
                if (xSSimpleType instanceof XSSimpleTypeDecl) {
                    ((XSSimpleTypeDecl)xSSimpleType).setAnonymous(true);
                }
                this.fGlobalAttrDecls.put(string, new BuiltinAttrDecl(string, string2, xSSimpleType, s2));
                string = SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION;
                string2 = SchemaSymbols.URI_XSI;
                xSSimpleType = xSSimpleType2;
                this.fGlobalAttrDecls.put(string, new BuiltinAttrDecl(string, string2, xSSimpleType, s2));
            }
        }

        @Override
        public XMLGrammarDescription getGrammarDescription() {
            return this.fGrammarDescription.makeClone();
        }

        @Override
        public void setImportedGrammars(Vector vector) {
        }

        @Override
        public void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl) {
        }

        @Override
        public void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl, String string) {
        }

        @Override
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl) {
        }

        @Override
        public void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl, String string) {
        }

        @Override
        public void addGlobalElementDecl(XSElementDecl xSElementDecl) {
        }

        @Override
        public void addGlobalElementDecl(XSElementDecl xSElementDecl, String string) {
        }

        @Override
        public void addGlobalElementDeclAll(XSElementDecl xSElementDecl) {
        }

        @Override
        public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl) {
        }

        @Override
        public void addGlobalGroupDecl(XSGroupDecl xSGroupDecl, String string) {
        }

        @Override
        public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl) {
        }

        @Override
        public void addGlobalNotationDecl(XSNotationDecl xSNotationDecl, String string) {
        }

        @Override
        public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition) {
        }

        @Override
        public void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition, String string) {
        }

        @Override
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl) {
        }

        @Override
        public void addGlobalComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, String string) {
        }

        @Override
        public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType) {
        }

        @Override
        public void addGlobalSimpleTypeDecl(XSSimpleType xSSimpleType, String string) {
        }

        @Override
        public void addComplexTypeDecl(XSComplexTypeDecl xSComplexTypeDecl, SimpleLocator simpleLocator) {
        }

        @Override
        public void addRedefinedGroupDecl(XSGroupDecl xSGroupDecl, XSGroupDecl xSGroupDecl2, SimpleLocator simpleLocator) {
        }

        @Override
        public synchronized void addDocument(Object object, String string) {
        }

        @Override
        synchronized DOMParser getDOMParser() {
            return null;
        }

        @Override
        synchronized SAXParser getSAXParser() {
            return null;
        }
    }
}

