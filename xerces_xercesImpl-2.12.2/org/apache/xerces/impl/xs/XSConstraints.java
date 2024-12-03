/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public class XSConstraints {
    static final int OCCURRENCE_UNKNOWN = -2;
    static final XSSimpleType STRING_TYPE = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("string");
    private static XSParticleDecl fEmptyParticle = null;
    private static final Comparator ELEMENT_PARTICLE_COMPARATOR = new Comparator(){

        public int compare(Object object, Object object2) {
            XSParticleDecl xSParticleDecl = (XSParticleDecl)object;
            XSParticleDecl xSParticleDecl2 = (XSParticleDecl)object2;
            XSElementDecl xSElementDecl = (XSElementDecl)xSParticleDecl.fValue;
            XSElementDecl xSElementDecl2 = (XSElementDecl)xSParticleDecl2.fValue;
            String string = xSElementDecl.getNamespace();
            String string2 = xSElementDecl2.getNamespace();
            String string3 = xSElementDecl.getName();
            String string4 = xSElementDecl2.getName();
            boolean bl = string == string2;
            int n = 0;
            if (!bl) {
                n = string != null ? (string2 != null ? string.compareTo(string2) : 1) : -1;
            }
            return n != 0 ? n : string3.compareTo(string4);
        }
    };

    public static XSParticleDecl getEmptySequence() {
        if (fEmptyParticle == null) {
            XSModelGroupImpl xSModelGroupImpl = new XSModelGroupImpl();
            xSModelGroupImpl.fCompositor = (short)102;
            xSModelGroupImpl.fParticleCount = 0;
            xSModelGroupImpl.fParticles = null;
            xSModelGroupImpl.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            XSParticleDecl xSParticleDecl = new XSParticleDecl();
            xSParticleDecl.fType = (short)3;
            xSParticleDecl.fValue = xSModelGroupImpl;
            xSParticleDecl.fAnnotations = XSObjectListImpl.EMPTY_LIST;
            fEmptyParticle = xSParticleDecl;
        }
        return fEmptyParticle;
    }

    public static boolean checkTypeDerivationOk(XSTypeDefinition xSTypeDefinition, XSTypeDefinition xSTypeDefinition2, short s) {
        if (xSTypeDefinition == SchemaGrammar.fAnyType) {
            return xSTypeDefinition == xSTypeDefinition2;
        }
        if (xSTypeDefinition == SchemaGrammar.fAnySimpleType) {
            return xSTypeDefinition2 == SchemaGrammar.fAnyType || xSTypeDefinition2 == SchemaGrammar.fAnySimpleType;
        }
        if (xSTypeDefinition.getTypeCategory() == 16) {
            if (xSTypeDefinition2.getTypeCategory() == 15) {
                if (xSTypeDefinition2 == SchemaGrammar.fAnyType) {
                    xSTypeDefinition2 = SchemaGrammar.fAnySimpleType;
                } else {
                    return false;
                }
            }
            return XSConstraints.checkSimpleDerivation((XSSimpleType)xSTypeDefinition, (XSSimpleType)xSTypeDefinition2, s);
        }
        return XSConstraints.checkComplexDerivation((XSComplexTypeDecl)xSTypeDefinition, xSTypeDefinition2, s);
    }

    public static boolean checkSimpleDerivationOk(XSSimpleType xSSimpleType, XSTypeDefinition xSTypeDefinition, short s) {
        if (xSSimpleType == SchemaGrammar.fAnySimpleType) {
            return xSTypeDefinition == SchemaGrammar.fAnyType || xSTypeDefinition == SchemaGrammar.fAnySimpleType;
        }
        if (xSTypeDefinition.getTypeCategory() == 15) {
            if (xSTypeDefinition == SchemaGrammar.fAnyType) {
                xSTypeDefinition = SchemaGrammar.fAnySimpleType;
            } else {
                return false;
            }
        }
        return XSConstraints.checkSimpleDerivation(xSSimpleType, (XSSimpleType)xSTypeDefinition, s);
    }

    public static boolean checkComplexDerivationOk(XSComplexTypeDecl xSComplexTypeDecl, XSTypeDefinition xSTypeDefinition, short s) {
        if (xSComplexTypeDecl == SchemaGrammar.fAnyType) {
            return xSComplexTypeDecl == xSTypeDefinition;
        }
        return XSConstraints.checkComplexDerivation(xSComplexTypeDecl, xSTypeDefinition, s);
    }

    private static boolean checkSimpleDerivation(XSSimpleType xSSimpleType, XSSimpleType xSSimpleType2, short s) {
        if (xSSimpleType == xSSimpleType2) {
            return true;
        }
        if ((s & 2) != 0 || (xSSimpleType.getBaseType().getFinal() & 2) != 0) {
            return false;
        }
        XSSimpleType xSSimpleType3 = (XSSimpleType)xSSimpleType.getBaseType();
        if (xSSimpleType3 == xSSimpleType2) {
            return true;
        }
        if (xSSimpleType3 != SchemaGrammar.fAnySimpleType && XSConstraints.checkSimpleDerivation(xSSimpleType3, xSSimpleType2, s)) {
            return true;
        }
        if ((xSSimpleType.getVariety() == 2 || xSSimpleType.getVariety() == 3) && xSSimpleType2 == SchemaGrammar.fAnySimpleType) {
            return true;
        }
        if (xSSimpleType2.getVariety() == 3) {
            XSObjectList xSObjectList = xSSimpleType2.getMemberTypes();
            int n = xSObjectList.getLength();
            for (int i = 0; i < n; ++i) {
                xSSimpleType2 = (XSSimpleType)xSObjectList.item(i);
                if (!XSConstraints.checkSimpleDerivation(xSSimpleType, xSSimpleType2, s)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean checkComplexDerivation(XSComplexTypeDecl xSComplexTypeDecl, XSTypeDefinition xSTypeDefinition, short s) {
        if (xSComplexTypeDecl == xSTypeDefinition) {
            return true;
        }
        if ((xSComplexTypeDecl.fDerivedBy & s) != 0) {
            return false;
        }
        XSTypeDefinition xSTypeDefinition2 = xSComplexTypeDecl.fBaseType;
        if (xSTypeDefinition2 == xSTypeDefinition) {
            return true;
        }
        if (xSTypeDefinition2 == SchemaGrammar.fAnyType || xSTypeDefinition2 == SchemaGrammar.fAnySimpleType) {
            return false;
        }
        if (xSTypeDefinition2.getTypeCategory() == 15) {
            return XSConstraints.checkComplexDerivation((XSComplexTypeDecl)xSTypeDefinition2, xSTypeDefinition, s);
        }
        if (xSTypeDefinition2.getTypeCategory() == 16) {
            if (xSTypeDefinition.getTypeCategory() == 15) {
                if (xSTypeDefinition == SchemaGrammar.fAnyType) {
                    xSTypeDefinition = SchemaGrammar.fAnySimpleType;
                } else {
                    return false;
                }
            }
            return XSConstraints.checkSimpleDerivation((XSSimpleType)xSTypeDefinition2, (XSSimpleType)xSTypeDefinition, s);
        }
        return false;
    }

    public static Object ElementDefaultValidImmediate(XSTypeDefinition xSTypeDefinition, String string, ValidationContext validationContext, ValidatedInfo validatedInfo) {
        Object object;
        XSSimpleType xSSimpleType = null;
        if (xSTypeDefinition.getTypeCategory() == 16) {
            xSSimpleType = (XSSimpleType)xSTypeDefinition;
        } else {
            object = (XSComplexTypeDecl)xSTypeDefinition;
            if (((XSComplexTypeDecl)object).fContentType == 1) {
                xSSimpleType = ((XSComplexTypeDecl)object).fXSSimpleType;
            } else if (((XSComplexTypeDecl)object).fContentType == 3) {
                if (!((XSParticleDecl)((XSComplexTypeDecl)object).getParticle()).emptiable()) {
                    return null;
                }
            } else {
                return null;
            }
        }
        object = null;
        if (xSSimpleType == null) {
            xSSimpleType = STRING_TYPE;
        }
        try {
            object = xSSimpleType.validate(string, validationContext, validatedInfo);
            if (validatedInfo != null) {
                object = xSSimpleType.validate(validatedInfo.stringValue(), validationContext, validatedInfo);
            }
        }
        catch (InvalidDatatypeValueException invalidDatatypeValueException) {
            return null;
        }
        return object;
    }

    static void reportSchemaError(XMLErrorReporter xMLErrorReporter, SimpleLocator simpleLocator, String string, Object[] objectArray) {
        if (simpleLocator != null) {
            xMLErrorReporter.reportError(simpleLocator, "http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)1);
        } else {
            xMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)1);
        }
    }

    public static void fullSchemaChecking(XSGrammarBucket xSGrammarBucket, SubstitutionGroupHandler substitutionGroupHandler, CMBuilder cMBuilder, XMLErrorReporter xMLErrorReporter) {
        Object object;
        Object object2;
        int n;
        Object[] objectArray;
        SchemaGrammar[] schemaGrammarArray = xSGrammarBucket.getGrammars();
        for (int i = schemaGrammarArray.length - 1; i >= 0; --i) {
            substitutionGroupHandler.addSubstitutionGroup(schemaGrammarArray[i].getSubstitutionGroups());
        }
        XSParticleDecl xSParticleDecl = new XSParticleDecl();
        XSParticleDecl xSParticleDecl2 = new XSParticleDecl();
        xSParticleDecl.fType = (short)3;
        xSParticleDecl2.fType = (short)3;
        for (int i = schemaGrammarArray.length - 1; i >= 0; --i) {
            objectArray = schemaGrammarArray[i].getRedefinedGroupDecls();
            SimpleLocator[] simpleLocatorArray = schemaGrammarArray[i].getRGLocators();
            n = 0;
            while (n < objectArray.length) {
                XSGroupDecl xSGroupDecl = objectArray[n++];
                object2 = xSGroupDecl.fModelGroup;
                XSGroupDecl xSGroupDecl2 = objectArray[n++];
                XSModelGroupImpl xSModelGroupImpl = xSGroupDecl2.fModelGroup;
                xSParticleDecl.fValue = object2;
                xSParticleDecl2.fValue = xSModelGroupImpl;
                if (xSModelGroupImpl == null) {
                    if (object2 == null) continue;
                    XSConstraints.reportSchemaError(xMLErrorReporter, simpleLocatorArray[n / 2 - 1], "src-redefine.6.2.2", new Object[]{xSGroupDecl.fName, "rcase-Recurse.2"});
                    continue;
                }
                if (object2 == null) {
                    if (xSParticleDecl2.emptiable()) continue;
                    XSConstraints.reportSchemaError(xMLErrorReporter, simpleLocatorArray[n / 2 - 1], "src-redefine.6.2.2", new Object[]{xSGroupDecl.fName, "rcase-Recurse.2"});
                    continue;
                }
                try {
                    XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler);
                }
                catch (XMLSchemaException xMLSchemaException) {
                    object = xMLSchemaException.getKey();
                    XSConstraints.reportSchemaError(xMLErrorReporter, simpleLocatorArray[n / 2 - 1], (String)object, xMLSchemaException.getArgs());
                    XSConstraints.reportSchemaError(xMLErrorReporter, simpleLocatorArray[n / 2 - 1], "src-redefine.6.2.2", new Object[]{xSGroupDecl.fName, object});
                }
            }
        }
        object2 = new SymbolHash();
        for (int i = schemaGrammarArray.length - 1; i >= 0; --i) {
            int n2 = 0;
            n = schemaGrammarArray[i].fFullChecked ? 1 : 0;
            XSComplexTypeDecl[] xSComplexTypeDeclArray = schemaGrammarArray[i].getUncheckedComplexTypeDecls();
            objectArray = schemaGrammarArray[i].getUncheckedCTLocators();
            for (int j = 0; j < xSComplexTypeDeclArray.length; ++j) {
                Object object3;
                if (n == 0 && xSComplexTypeDeclArray[j].fParticle != null) {
                    ((SymbolHash)object2).clear();
                    try {
                        XSConstraints.checkElementDeclsConsistent(xSComplexTypeDeclArray[j], xSComplexTypeDeclArray[j].fParticle, (SymbolHash)object2, substitutionGroupHandler);
                    }
                    catch (XMLSchemaException xMLSchemaException) {
                        XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
                    }
                }
                if (xSComplexTypeDeclArray[j].fBaseType != null && xSComplexTypeDeclArray[j].fBaseType != SchemaGrammar.fAnyType && xSComplexTypeDeclArray[j].fDerivedBy == 2 && xSComplexTypeDeclArray[j].fBaseType instanceof XSComplexTypeDecl) {
                    object3 = xSComplexTypeDeclArray[j].fParticle;
                    object = ((XSComplexTypeDecl)xSComplexTypeDeclArray[j].fBaseType).fParticle;
                    if (object3 == null) {
                        if (object != null && !((XSParticleDecl)object).emptiable()) {
                            XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], "derivation-ok-restriction.5.3.2", new Object[]{xSComplexTypeDeclArray[j].fName, xSComplexTypeDeclArray[j].fBaseType.getName()});
                        }
                    } else if (object != null) {
                        try {
                            XSConstraints.particleValidRestriction(xSComplexTypeDeclArray[j].fParticle, substitutionGroupHandler, ((XSComplexTypeDecl)xSComplexTypeDeclArray[j].fBaseType).fParticle, substitutionGroupHandler);
                        }
                        catch (XMLSchemaException xMLSchemaException) {
                            XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
                            XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], "derivation-ok-restriction.5.4.2", new Object[]{xSComplexTypeDeclArray[j].fName});
                        }
                    } else {
                        XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], "derivation-ok-restriction.5.4.2", new Object[]{xSComplexTypeDeclArray[j].fName});
                    }
                }
                object3 = xSComplexTypeDeclArray[j].getContentModel(cMBuilder, true);
                boolean bl = false;
                if (object3 != null) {
                    try {
                        bl = object3.checkUniqueParticleAttribution(substitutionGroupHandler);
                    }
                    catch (XMLSchemaException xMLSchemaException) {
                        XSConstraints.reportSchemaError(xMLErrorReporter, (SimpleLocator)objectArray[j], xMLSchemaException.getKey(), xMLSchemaException.getArgs());
                    }
                }
                if (n != 0 || !bl) continue;
                xSComplexTypeDeclArray[n2++] = xSComplexTypeDeclArray[j];
            }
            if (n != 0) continue;
            schemaGrammarArray[i].setUncheckedTypeNum(n2);
            schemaGrammarArray[i].fFullChecked = true;
        }
    }

    public static void checkElementDeclsConsistent(XSComplexTypeDecl xSComplexTypeDecl, XSParticleDecl xSParticleDecl, SymbolHash symbolHash, SubstitutionGroupHandler substitutionGroupHandler) throws XMLSchemaException {
        short s = xSParticleDecl.fType;
        if (s == 2) {
            return;
        }
        if (s == 1) {
            XSElementDecl xSElementDecl = (XSElementDecl)xSParticleDecl.fValue;
            XSConstraints.findElemInTable(xSComplexTypeDecl, xSElementDecl, symbolHash);
            if (xSElementDecl.fScope == 1) {
                XSElementDecl[] xSElementDeclArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
                for (int i = 0; i < xSElementDeclArray.length; ++i) {
                    XSConstraints.findElemInTable(xSComplexTypeDecl, xSElementDeclArray[i], symbolHash);
                }
            }
            return;
        }
        XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
        for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
            XSConstraints.checkElementDeclsConsistent(xSComplexTypeDecl, xSModelGroupImpl.fParticles[i], symbolHash, substitutionGroupHandler);
        }
    }

    public static void findElemInTable(XSComplexTypeDecl xSComplexTypeDecl, XSElementDecl xSElementDecl, SymbolHash symbolHash) throws XMLSchemaException {
        String string = xSElementDecl.fName + "," + xSElementDecl.fTargetNamespace;
        XSElementDecl xSElementDecl2 = null;
        xSElementDecl2 = (XSElementDecl)symbolHash.get(string);
        if (xSElementDecl2 == null) {
            symbolHash.put(string, xSElementDecl);
        } else {
            if (xSElementDecl == xSElementDecl2) {
                return;
            }
            if (xSElementDecl.fType != xSElementDecl2.fType) {
                throw new XMLSchemaException("cos-element-consistent", new Object[]{xSComplexTypeDecl.fName, xSElementDecl.fName});
            }
        }
    }

    private static boolean particleValidRestriction(XSParticleDecl xSParticleDecl, SubstitutionGroupHandler substitutionGroupHandler, XSParticleDecl xSParticleDecl2, SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        return XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler2, true);
    }

    private static boolean particleValidRestriction(XSParticleDecl xSParticleDecl, SubstitutionGroupHandler substitutionGroupHandler, XSParticleDecl xSElementDeclArray, SubstitutionGroupHandler substitutionGroupHandler2, boolean bl) throws XMLSchemaException {
        int n;
        int n2;
        XSElementDecl[] xSElementDeclArray2;
        Vector<XSParticleDecl> vector = null;
        Vector vector2 = null;
        int n3 = -2;
        int n4 = -2;
        boolean bl2 = false;
        if (xSParticleDecl.isEmpty() && !xSElementDeclArray.emptiable()) {
            throw new XMLSchemaException("cos-particle-restrict.a", null);
        }
        if (!xSParticleDecl.isEmpty() && xSElementDeclArray.isEmpty()) {
            throw new XMLSchemaException("cos-particle-restrict.b", null);
        }
        int n5 = xSParticleDecl.fType;
        if (n5 == 3) {
            n5 = ((XSModelGroupImpl)xSParticleDecl.fValue).fCompositor;
            XSParticleDecl xSParticleDecl2 = XSConstraints.getNonUnaryGroup(xSParticleDecl);
            if (xSParticleDecl2 != xSParticleDecl) {
                xSParticleDecl = xSParticleDecl2;
                n5 = xSParticleDecl.fType;
                if (n5 == 3) {
                    n5 = ((XSModelGroupImpl)xSParticleDecl.fValue).fCompositor;
                }
            }
            vector = XSConstraints.removePointlessChildren(xSParticleDecl);
        }
        int n6 = xSParticleDecl.fMinOccurs;
        int n7 = xSParticleDecl.fMaxOccurs;
        if (substitutionGroupHandler != null && n5 == 1) {
            XSElementDecl xSElementDecl = (XSElementDecl)xSParticleDecl.fValue;
            if (xSElementDecl.fScope == 1 && (xSElementDeclArray2 = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl)).length > 0) {
                n5 = 101;
                n3 = n6;
                n4 = n7;
                vector = new Vector(xSElementDeclArray2.length + 1);
                for (n2 = 0; n2 < xSElementDeclArray2.length; ++n2) {
                    XSConstraints.addElementToParticleVector(vector, xSElementDeclArray2[n2]);
                }
                XSConstraints.addElementToParticleVector(vector, xSElementDecl);
                Collections.sort(vector, ELEMENT_PARTICLE_COMPARATOR);
                substitutionGroupHandler = null;
            }
        }
        if ((n = xSElementDeclArray.fType) == 3) {
            n = ((XSModelGroupImpl)xSElementDeclArray.fValue).fCompositor;
            xSElementDeclArray2 = XSConstraints.getNonUnaryGroup((XSParticleDecl)xSElementDeclArray);
            if (xSElementDeclArray2 != xSElementDeclArray) {
                xSElementDeclArray = xSElementDeclArray2;
                n = xSElementDeclArray.fType;
                if (n == 3) {
                    n = ((XSModelGroupImpl)xSElementDeclArray.fValue).fCompositor;
                }
            }
            vector2 = XSConstraints.removePointlessChildren((XSParticleDecl)xSElementDeclArray);
        }
        int n8 = xSElementDeclArray.fMinOccurs;
        n2 = xSElementDeclArray.fMaxOccurs;
        if (substitutionGroupHandler2 != null && n == 1) {
            XSElementDecl[] xSElementDeclArray3;
            XSElementDecl xSElementDecl = (XSElementDecl)xSElementDeclArray.fValue;
            if (xSElementDecl.fScope == 1 && (xSElementDeclArray3 = substitutionGroupHandler2.getSubstitutionGroup(xSElementDecl)).length > 0) {
                n = 101;
                vector2 = new Vector(xSElementDeclArray3.length + 1);
                for (int i = 0; i < xSElementDeclArray3.length; ++i) {
                    XSConstraints.addElementToParticleVector(vector2, xSElementDeclArray3[i]);
                }
                XSConstraints.addElementToParticleVector(vector2, xSElementDecl);
                Collections.sort(vector2, ELEMENT_PARTICLE_COMPARATOR);
                substitutionGroupHandler2 = null;
                bl2 = true;
            }
        }
        switch (n5) {
            case 1: {
                switch (n) {
                    case 1: {
                        XSConstraints.checkNameAndTypeOK((XSElementDecl)xSParticleDecl.fValue, n6, n7, (XSElementDecl)xSElementDeclArray.fValue, n8, n2);
                        return bl2;
                    }
                    case 2: {
                        XSConstraints.checkNSCompat((XSElementDecl)xSParticleDecl.fValue, n6, n7, (XSWildcardDecl)xSElementDeclArray.fValue, n8, n2, bl);
                        return bl2;
                    }
                    case 101: {
                        vector = new Vector<XSParticleDecl>();
                        vector.addElement(xSParticleDecl);
                        XSConstraints.checkRecurseLax(vector, 1, 1, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 102: 
                    case 103: {
                        vector = new Vector();
                        vector.addElement(xSParticleDecl);
                        XSConstraints.checkRecurse(vector, 1, 1, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                }
                throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
            case 2: {
                switch (n) {
                    case 2: {
                        XSConstraints.checkNSSubset((XSWildcardDecl)xSParticleDecl.fValue, n6, n7, (XSWildcardDecl)xSElementDeclArray.fValue, n8, n2);
                        return bl2;
                    }
                    case 1: 
                    case 101: 
                    case 102: 
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"any:choice,sequence,all,elt"});
                    }
                }
                throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
            case 103: {
                switch (n) {
                    case 2: {
                        if (n3 == -2) {
                            n3 = xSParticleDecl.minEffectiveTotalRange();
                        }
                        if (n4 == -2) {
                            n4 = xSParticleDecl.maxEffectiveTotalRange();
                        }
                        XSConstraints.checkNSRecurseCheckCardinality(vector, n3, n4, substitutionGroupHandler, (XSParticleDecl)xSElementDeclArray, n8, n2, bl);
                        return bl2;
                    }
                    case 103: {
                        XSConstraints.checkRecurse(vector, n6, n7, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 1: 
                    case 101: 
                    case 102: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"all:choice,sequence,elt"});
                    }
                }
                throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
            case 101: {
                switch (n) {
                    case 2: {
                        if (n3 == -2) {
                            n3 = xSParticleDecl.minEffectiveTotalRange();
                        }
                        if (n4 == -2) {
                            n4 = xSParticleDecl.maxEffectiveTotalRange();
                        }
                        XSConstraints.checkNSRecurseCheckCardinality(vector, n3, n4, substitutionGroupHandler, (XSParticleDecl)xSElementDeclArray, n8, n2, bl);
                        return bl2;
                    }
                    case 101: {
                        XSConstraints.checkRecurseLax(vector, n6, n7, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 1: 
                    case 102: 
                    case 103: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"choice:all,sequence,elt"});
                    }
                }
                throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
            case 102: {
                switch (n) {
                    case 2: {
                        if (n3 == -2) {
                            n3 = xSParticleDecl.minEffectiveTotalRange();
                        }
                        if (n4 == -2) {
                            n4 = xSParticleDecl.maxEffectiveTotalRange();
                        }
                        XSConstraints.checkNSRecurseCheckCardinality(vector, n3, n4, substitutionGroupHandler, (XSParticleDecl)xSElementDeclArray, n8, n2, bl);
                        return bl2;
                    }
                    case 103: {
                        XSConstraints.checkRecurseUnordered(vector, n6, n7, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 102: {
                        XSConstraints.checkRecurse(vector, n6, n7, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 101: {
                        int n9 = n6 * vector.size();
                        int n10 = n7 == -1 ? n7 : n7 * vector.size();
                        XSConstraints.checkMapAndSum(vector, n9, n10, substitutionGroupHandler, vector2, n8, n2, substitutionGroupHandler2);
                        return bl2;
                    }
                    case 1: {
                        throw new XMLSchemaException("cos-particle-restrict.2", new Object[]{"seq:elt"});
                    }
                }
                throw new XMLSchemaException("Internal-Error", new Object[]{"in particleValidRestriction"});
            }
        }
        return bl2;
    }

    private static void addElementToParticleVector(Vector vector, XSElementDecl xSElementDecl) {
        XSParticleDecl xSParticleDecl = new XSParticleDecl();
        xSParticleDecl.fValue = xSElementDecl;
        xSParticleDecl.fType = 1;
        vector.addElement(xSParticleDecl);
    }

    private static XSParticleDecl getNonUnaryGroup(XSParticleDecl xSParticleDecl) {
        if (xSParticleDecl.fType == 1 || xSParticleDecl.fType == 2) {
            return xSParticleDecl;
        }
        if (xSParticleDecl.fMinOccurs == 1 && xSParticleDecl.fMaxOccurs == 1 && xSParticleDecl.fValue != null && ((XSModelGroupImpl)xSParticleDecl.fValue).fParticleCount == 1) {
            return XSConstraints.getNonUnaryGroup(((XSModelGroupImpl)xSParticleDecl.fValue).fParticles[0]);
        }
        return xSParticleDecl;
    }

    private static Vector removePointlessChildren(XSParticleDecl xSParticleDecl) {
        if (xSParticleDecl.fType == 1 || xSParticleDecl.fType == 2) {
            return null;
        }
        Vector vector = new Vector();
        XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
        for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
            XSConstraints.gatherChildren(xSModelGroupImpl.fCompositor, xSModelGroupImpl.fParticles[i], vector);
        }
        return vector;
    }

    private static void gatherChildren(int n, XSParticleDecl xSParticleDecl, Vector vector) {
        int n2 = xSParticleDecl.fMinOccurs;
        int n3 = xSParticleDecl.fMaxOccurs;
        short s = xSParticleDecl.fType;
        if (s == 3) {
            s = ((XSModelGroupImpl)xSParticleDecl.fValue).fCompositor;
        }
        if (s == 1 || s == 2) {
            vector.addElement(xSParticleDecl);
            return;
        }
        if (n2 != 1 || n3 != 1) {
            vector.addElement(xSParticleDecl);
        } else if (n == s) {
            XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
            for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
                XSConstraints.gatherChildren(s, xSModelGroupImpl.fParticles[i], vector);
            }
        } else if (!xSParticleDecl.isEmpty()) {
            vector.addElement(xSParticleDecl);
        }
    }

    private static void checkNameAndTypeOK(XSElementDecl xSElementDecl, int n, int n2, XSElementDecl xSElementDecl2, int n3, int n4) throws XMLSchemaException {
        short s;
        if (xSElementDecl.fName != xSElementDecl2.fName || xSElementDecl.fTargetNamespace != xSElementDecl2.fTargetNamespace) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.1", new Object[]{xSElementDecl.fName, xSElementDecl.fTargetNamespace, xSElementDecl2.fName, xSElementDecl2.fTargetNamespace});
        }
        if (!xSElementDecl2.getNillable() && xSElementDecl.getNillable()) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.2", new Object[]{xSElementDecl.fName});
        }
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.3", new Object[]{xSElementDecl.fName, Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        if (xSElementDecl2.getConstraintType() == 2) {
            if (xSElementDecl.getConstraintType() != 2) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.a", new Object[]{xSElementDecl.fName, xSElementDecl2.fDefault.stringValue()});
            }
            s = 0;
            if (xSElementDecl.fType.getTypeCategory() == 16 || ((XSComplexTypeDecl)xSElementDecl.fType).fContentType == 1) {
                s = 1;
            }
            if (s == 0 && !xSElementDecl2.fDefault.normalizedValue.equals(xSElementDecl.fDefault.normalizedValue) || s != 0 && !xSElementDecl2.fDefault.actualValue.equals(xSElementDecl.fDefault.actualValue)) {
                throw new XMLSchemaException("rcase-NameAndTypeOK.4.b", new Object[]{xSElementDecl.fName, xSElementDecl.fDefault.stringValue(), xSElementDecl2.fDefault.stringValue()});
            }
        }
        XSConstraints.checkIDConstraintRestriction(xSElementDecl, xSElementDecl2);
        s = xSElementDecl.fBlock;
        short s2 = xSElementDecl2.fBlock;
        if ((s & s2) != s2 || s == 0 && s2 != 0) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.6", new Object[]{xSElementDecl.fName});
        }
        if (!XSConstraints.checkTypeDerivationOk(xSElementDecl.fType, xSElementDecl2.fType, (short)25)) {
            throw new XMLSchemaException("rcase-NameAndTypeOK.7", new Object[]{xSElementDecl.fName, xSElementDecl.fType.getName(), xSElementDecl2.fType.getName()});
        }
    }

    private static void checkIDConstraintRestriction(XSElementDecl xSElementDecl, XSElementDecl xSElementDecl2) throws XMLSchemaException {
    }

    private static boolean checkOccurrenceRange(int n, int n2, int n3, int n4) {
        return n >= n3 && (n4 == -1 || n2 != -1 && n2 <= n4);
    }

    private static void checkNSCompat(XSElementDecl xSElementDecl, int n, int n2, XSWildcardDecl xSWildcardDecl, int n3, int n4, boolean bl) throws XMLSchemaException {
        if (bl && !XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSCompat.2", new Object[]{xSElementDecl.fName, Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        if (!xSWildcardDecl.allowNamespace(xSElementDecl.fTargetNamespace)) {
            throw new XMLSchemaException("rcase-NSCompat.1", new Object[]{xSElementDecl.fName, xSElementDecl.fTargetNamespace});
        }
    }

    private static void checkNSSubset(XSWildcardDecl xSWildcardDecl, int n, int n2, XSWildcardDecl xSWildcardDecl2, int n3, int n4) throws XMLSchemaException {
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSSubset.2", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        if (!xSWildcardDecl.isSubsetOf(xSWildcardDecl2)) {
            throw new XMLSchemaException("rcase-NSSubset.1", null);
        }
        if (xSWildcardDecl.weakerProcessContents(xSWildcardDecl2)) {
            throw new XMLSchemaException("rcase-NSSubset.3", new Object[]{xSWildcardDecl.getProcessContentsAsString(), xSWildcardDecl2.getProcessContentsAsString()});
        }
    }

    private static void checkNSRecurseCheckCardinality(Vector vector, int n, int n2, SubstitutionGroupHandler substitutionGroupHandler, XSParticleDecl xSParticleDecl, int n3, int n4, boolean bl) throws XMLSchemaException {
        if (bl && !XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.2", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        int n5 = vector.size();
        try {
            for (int i = 0; i < n5; ++i) {
                XSParticleDecl xSParticleDecl2 = (XSParticleDecl)vector.elementAt(i);
                XSConstraints.particleValidRestriction(xSParticleDecl2, substitutionGroupHandler, xSParticleDecl, null, false);
            }
        }
        catch (XMLSchemaException xMLSchemaException) {
            throw new XMLSchemaException("rcase-NSRecurseCheckCardinality.1", null);
        }
    }

    private static void checkRecurse(Vector vector, int n, int n2, SubstitutionGroupHandler substitutionGroupHandler, Vector vector2, int n3, int n4, SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        XSParticleDecl xSParticleDecl;
        int n5;
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-Recurse.1", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        int n6 = vector.size();
        int n7 = vector2.size();
        int n8 = 0;
        block2: for (n5 = 0; n5 < n6; ++n5) {
            xSParticleDecl = (XSParticleDecl)vector.elementAt(n5);
            for (int i = n8; i < n7; ++i) {
                XSParticleDecl xSParticleDecl2 = (XSParticleDecl)vector2.elementAt(i);
                ++n8;
                try {
                    XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler2);
                    continue block2;
                }
                catch (XMLSchemaException xMLSchemaException) {
                    if (xSParticleDecl2.emptiable()) continue;
                    throw new XMLSchemaException("rcase-Recurse.2", null);
                }
            }
            throw new XMLSchemaException("rcase-Recurse.2", null);
        }
        for (n5 = n8; n5 < n7; ++n5) {
            xSParticleDecl = (XSParticleDecl)vector2.elementAt(n5);
            if (xSParticleDecl.emptiable()) continue;
            throw new XMLSchemaException("rcase-Recurse.2", null);
        }
    }

    private static void checkRecurseUnordered(Vector vector, int n, int n2, SubstitutionGroupHandler substitutionGroupHandler, Vector vector2, int n3, int n4, SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        XSParticleDecl xSParticleDecl;
        int n5;
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-RecurseUnordered.1", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        int n6 = vector.size();
        int n7 = vector2.size();
        boolean[] blArray = new boolean[n7];
        block2: for (n5 = 0; n5 < n6; ++n5) {
            xSParticleDecl = (XSParticleDecl)vector.elementAt(n5);
            for (int i = 0; i < n7; ++i) {
                XSParticleDecl xSParticleDecl2 = (XSParticleDecl)vector2.elementAt(i);
                try {
                    XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler2);
                    if (blArray[i]) {
                        throw new XMLSchemaException("rcase-RecurseUnordered.2", null);
                    }
                    blArray[i] = true;
                    continue block2;
                }
                catch (XMLSchemaException xMLSchemaException) {
                    continue;
                }
            }
            throw new XMLSchemaException("rcase-RecurseUnordered.2", null);
        }
        for (n5 = 0; n5 < n7; ++n5) {
            xSParticleDecl = (XSParticleDecl)vector2.elementAt(n5);
            if (blArray[n5] || xSParticleDecl.emptiable()) continue;
            throw new XMLSchemaException("rcase-RecurseUnordered.2", null);
        }
    }

    private static void checkRecurseLax(Vector vector, int n, int n2, SubstitutionGroupHandler substitutionGroupHandler, Vector vector2, int n3, int n4, SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-RecurseLax.1", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        int n5 = vector.size();
        int n6 = vector2.size();
        int n7 = 0;
        block2: for (int i = 0; i < n5; ++i) {
            XSParticleDecl xSParticleDecl = (XSParticleDecl)vector.elementAt(i);
            for (int j = n7; j < n6; ++j) {
                XSParticleDecl xSParticleDecl2 = (XSParticleDecl)vector2.elementAt(j);
                ++n7;
                try {
                    if (!XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler2)) continue block2;
                    --n7;
                    continue block2;
                }
                catch (XMLSchemaException xMLSchemaException) {
                    continue;
                }
            }
            throw new XMLSchemaException("rcase-RecurseLax.2", null);
        }
    }

    private static void checkMapAndSum(Vector vector, int n, int n2, SubstitutionGroupHandler substitutionGroupHandler, Vector vector2, int n3, int n4, SubstitutionGroupHandler substitutionGroupHandler2) throws XMLSchemaException {
        if (!XSConstraints.checkOccurrenceRange(n, n2, n3, n4)) {
            throw new XMLSchemaException("rcase-MapAndSum.2", new Object[]{Integer.toString(n), n2 == -1 ? "unbounded" : Integer.toString(n2), Integer.toString(n3), n4 == -1 ? "unbounded" : Integer.toString(n4)});
        }
        int n5 = vector.size();
        int n6 = vector2.size();
        block2: for (int i = 0; i < n5; ++i) {
            XSParticleDecl xSParticleDecl = (XSParticleDecl)vector.elementAt(i);
            for (int j = 0; j < n6; ++j) {
                XSParticleDecl xSParticleDecl2 = (XSParticleDecl)vector2.elementAt(j);
                try {
                    XSConstraints.particleValidRestriction(xSParticleDecl, substitutionGroupHandler, xSParticleDecl2, substitutionGroupHandler2);
                    continue block2;
                }
                catch (XMLSchemaException xMLSchemaException) {
                    continue;
                }
            }
            throw new XMLSchemaException("rcase-MapAndSum.1", null);
        }
    }

    public static boolean overlapUPA(XSElementDecl xSElementDecl, XSElementDecl xSElementDecl2, SubstitutionGroupHandler substitutionGroupHandler) {
        int n;
        if (xSElementDecl.fName == xSElementDecl2.fName && xSElementDecl.fTargetNamespace == xSElementDecl2.fTargetNamespace) {
            return true;
        }
        XSElementDecl[] xSElementDeclArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
        for (n = xSElementDeclArray.length - 1; n >= 0; --n) {
            if (xSElementDeclArray[n].fName != xSElementDecl2.fName || xSElementDeclArray[n].fTargetNamespace != xSElementDecl2.fTargetNamespace) continue;
            return true;
        }
        xSElementDeclArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl2);
        for (n = xSElementDeclArray.length - 1; n >= 0; --n) {
            if (xSElementDeclArray[n].fName != xSElementDecl.fName || xSElementDeclArray[n].fTargetNamespace != xSElementDecl.fTargetNamespace) continue;
            return true;
        }
        return false;
    }

    public static boolean overlapUPA(XSElementDecl xSElementDecl, XSWildcardDecl xSWildcardDecl, SubstitutionGroupHandler substitutionGroupHandler) {
        if (xSWildcardDecl.allowNamespace(xSElementDecl.fTargetNamespace)) {
            return true;
        }
        XSElementDecl[] xSElementDeclArray = substitutionGroupHandler.getSubstitutionGroup(xSElementDecl);
        for (int i = xSElementDeclArray.length - 1; i >= 0; --i) {
            if (!xSWildcardDecl.allowNamespace(xSElementDeclArray[i].fTargetNamespace)) continue;
            return true;
        }
        return false;
    }

    public static boolean overlapUPA(XSWildcardDecl xSWildcardDecl, XSWildcardDecl xSWildcardDecl2) {
        XSWildcardDecl xSWildcardDecl3 = xSWildcardDecl.performIntersectionWith(xSWildcardDecl2, xSWildcardDecl.fProcessContents);
        return xSWildcardDecl3 == null || xSWildcardDecl3.fType != 3 || xSWildcardDecl3.fNamespaceList.length != 0;
    }

    public static boolean overlapUPA(Object object, Object object2, SubstitutionGroupHandler substitutionGroupHandler) {
        if (object instanceof XSElementDecl) {
            if (object2 instanceof XSElementDecl) {
                return XSConstraints.overlapUPA((XSElementDecl)object, (XSElementDecl)object2, substitutionGroupHandler);
            }
            return XSConstraints.overlapUPA((XSElementDecl)object, (XSWildcardDecl)object2, substitutionGroupHandler);
        }
        if (object2 instanceof XSElementDecl) {
            return XSConstraints.overlapUPA((XSElementDecl)object2, (XSWildcardDecl)object, substitutionGroupHandler);
        }
        return XSConstraints.overlapUPA((XSWildcardDecl)object, (XSWildcardDecl)object2);
    }
}

