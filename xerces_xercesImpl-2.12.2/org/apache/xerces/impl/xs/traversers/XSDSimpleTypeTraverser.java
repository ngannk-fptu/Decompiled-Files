/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.util.ArrayList;
import java.util.Vector;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.Element;

class XSDSimpleTypeTraverser
extends XSDAbstractTraverser {
    private boolean fIsBuiltIn = false;

    XSDSimpleTypeTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    XSSimpleType traverseGlobal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, true, xSDocumentInfo);
        String string = (String)objectArray[XSAttributeChecker.ATTIDX_NAME];
        if (string == null) {
            objectArray[XSAttributeChecker.ATTIDX_NAME] = "(no name)";
        }
        XSSimpleType xSSimpleType = this.traverseSimpleTypeDecl(element, objectArray, xSDocumentInfo, schemaGrammar);
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        if (string == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, SchemaSymbols.ATT_NAME}, element);
            xSSimpleType = null;
        }
        if (xSSimpleType != null) {
            if (schemaGrammar.getGlobalTypeDecl(xSSimpleType.getName()) == null) {
                schemaGrammar.addGlobalSimpleTypeDecl(xSSimpleType);
            }
            String string2 = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo);
            XSTypeDefinition xSTypeDefinition = schemaGrammar.getGlobalTypeDecl(xSSimpleType.getName(), string2);
            if (xSTypeDefinition == null) {
                schemaGrammar.addGlobalSimpleTypeDecl(xSSimpleType, string2);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (xSTypeDefinition != null && xSTypeDefinition instanceof XSSimpleType) {
                    xSSimpleType = (XSSimpleType)xSTypeDefinition;
                }
                this.fSchemaHandler.addGlobalTypeDecl(xSSimpleType);
            }
        }
        return xSSimpleType;
    }

    XSSimpleType traverseLocal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        String string = this.genAnonTypeName(element);
        XSSimpleType xSSimpleType = this.getSimpleType(string, element, objectArray, xSDocumentInfo, schemaGrammar);
        if (xSSimpleType instanceof XSSimpleTypeDecl) {
            ((XSSimpleTypeDecl)xSSimpleType).setAnonymous(true);
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSSimpleType;
    }

    private XSSimpleType traverseSimpleTypeDecl(Element element, Object[] objectArray, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        String string = (String)objectArray[XSAttributeChecker.ATTIDX_NAME];
        return this.getSimpleType(string, element, objectArray, xSDocumentInfo, schemaGrammar);
    }

    private String genAnonTypeName(Element element) {
        StringBuffer stringBuffer = new StringBuffer("#AnonType_");
        Element element2 = DOMUtil.getParent(element);
        while (element2 != null && element2 != DOMUtil.getRoot(DOMUtil.getDocument(element2))) {
            stringBuffer.append(element2.getAttribute(SchemaSymbols.ATT_NAME));
            element2 = DOMUtil.getParent(element2);
        }
        return stringBuffer.toString();
    }

    private XSSimpleType getSimpleType(String string, Element element, Object[] objectArray, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        XSObjectList xSObjectList;
        int n;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        XInt xInt = (XInt)objectArray[XSAttributeChecker.ATTIDX_FINAL];
        int n2 = xInt == null ? xSDocumentInfo.fFinalDefault : xInt.intValue();
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSObject[] xSObjectArray = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            object4 = this.traverseAnnotationDecl(element2, objectArray, false, xSDocumentInfo);
            if (object4 != null) {
                xSObjectArray = new XSAnnotationImpl[]{object4};
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        } else {
            object4 = DOMUtil.getSyntheticAnnotation(element);
            if (object4 != null) {
                XSAnnotationImpl xSAnnotationImpl = this.traverseSyntheticAnnotation(element, (String)object4, objectArray, false, xSDocumentInfo);
                xSObjectArray = new XSAnnotationImpl[]{xSAnnotationImpl};
            }
        }
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))"}, element);
            return this.errorType(string, xSDocumentInfo.fTargetNamespace, (short)2);
        }
        object4 = DOMUtil.getLocalName(element2);
        short s = 2;
        boolean bl = false;
        boolean bl2 = false;
        boolean bl3 = false;
        if (((String)object4).equals(SchemaSymbols.ELT_RESTRICTION)) {
            s = 2;
            bl = true;
        } else if (((String)object4).equals(SchemaSymbols.ELT_LIST)) {
            s = 16;
            bl2 = true;
        } else if (((String)object4).equals(SchemaSymbols.ELT_UNION)) {
            s = 8;
            bl3 = true;
        } else {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", object4}, element);
            return this.errorType(string, xSDocumentInfo.fTargetNamespace, (short)2);
        }
        Element element3 = DOMUtil.getNextSiblingElement(element2);
        if (element3 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SIMPLETYPE, "(annotation?, (restriction | list | union))", DOMUtil.getLocalName(element3)}, element3);
        }
        Object[] objectArray2 = this.fAttrChecker.checkAttributes(element2, false, xSDocumentInfo);
        QName qName = (QName)objectArray2[bl ? XSAttributeChecker.ATTIDX_BASE : XSAttributeChecker.ATTIDX_ITEMTYPE];
        Vector vector = (Vector)objectArray2[XSAttributeChecker.ATTIDX_MEMBERTYPES];
        Element element4 = DOMUtil.getFirstChildElement(element2);
        if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
            object3 = this.traverseAnnotationDecl(element4, objectArray2, false, xSDocumentInfo);
            if (object3 != null) {
                if (xSObjectArray == null) {
                    xSObjectArray = new XSAnnotationImpl[]{object3};
                } else {
                    object2 = new XSAnnotationImpl[2];
                    object2[0] = xSObjectArray[0];
                    xSObjectArray = object2;
                    xSObjectArray[1] = object3;
                }
            }
            element4 = DOMUtil.getNextSiblingElement(element4);
        } else {
            object3 = DOMUtil.getSyntheticAnnotation(element2);
            if (object3 != null) {
                object2 = this.traverseSyntheticAnnotation(element2, (String)object3, objectArray2, false, xSDocumentInfo);
                if (xSObjectArray == null) {
                    xSObjectArray = new XSAnnotationImpl[]{object2};
                } else {
                    object = new XSAnnotationImpl[2];
                    object[0] = xSObjectArray[0];
                    xSObjectArray = object;
                    xSObjectArray[1] = object2;
                }
            }
        }
        object3 = null;
        if ((bl || bl2) && qName != null && (object3 = this.findDTValidator(element2, string, qName, s, xSDocumentInfo)) == null && this.fIsBuiltIn) {
            this.fIsBuiltIn = false;
            return null;
        }
        object2 = null;
        object = null;
        if (bl3 && vector != null && vector.size() > 0) {
            n = vector.size();
            object2 = new ArrayList(n);
            for (int i = 0; i < n; ++i) {
                object = this.findDTValidator(element2, string, (QName)vector.elementAt(i), (short)8, xSDocumentInfo);
                if (object == null) continue;
                if (object.getVariety() == 3) {
                    xSObjectList = object.getMemberTypes();
                    for (int j = 0; j < xSObjectList.getLength(); ++j) {
                        ((ArrayList)object2).add(xSObjectList.item(j));
                    }
                    continue;
                }
                ((ArrayList)object2).add(object);
            }
        }
        if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            if (bl || bl2) {
                if (qName != null) {
                    this.reportSchemaError(bl2 ? "src-simple-type.3.a" : "src-simple-type.2.a", null, element4);
                }
                if (object3 == null) {
                    object3 = this.traverseLocal(element4, xSDocumentInfo, schemaGrammar);
                }
                element4 = DOMUtil.getNextSiblingElement(element4);
            } else if (bl3) {
                if (object2 == null) {
                    object2 = new ArrayList(2);
                }
                do {
                    if ((object = this.traverseLocal(element4, xSDocumentInfo, schemaGrammar)) == null) continue;
                    if (object.getVariety() == 3) {
                        xSObjectList = object.getMemberTypes();
                        for (n = 0; n < xSObjectList.getLength(); ++n) {
                            ((ArrayList)object2).add(xSObjectList.item(n));
                        }
                    } else {
                        ((ArrayList)object2).add(object);
                    }
                } while ((element4 = DOMUtil.getNextSiblingElement(element4)) != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_SIMPLETYPE));
            }
        } else if ((bl || bl2) && qName == null) {
            this.reportSchemaError(bl2 ? "src-simple-type.3.b" : "src-simple-type.2.b", null, element2);
        } else if (bl3 && (vector == null || vector.size() == 0)) {
            this.reportSchemaError("src-union-memberTypes-or-simpleTypes", null, element2);
        }
        if ((bl || bl2) && object3 == null) {
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return this.errorType(string, xSDocumentInfo.fTargetNamespace, bl ? (short)2 : 16);
        }
        if (bl3 && (object2 == null || ((ArrayList)object2).size() == 0)) {
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return this.errorType(string, xSDocumentInfo.fTargetNamespace, (short)8);
        }
        if (bl2 && this.isListDatatype((XSSimpleType)object3)) {
            this.reportSchemaError("cos-st-restricts.2.1", new Object[]{string, object3.getName()}, element2);
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return this.errorType(string, xSDocumentInfo.fTargetNamespace, (short)16);
        }
        XSSimpleType xSSimpleType = null;
        if (bl) {
            xSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(string, xSDocumentInfo.fTargetNamespace, (short)n2, (XSSimpleType)object3, xSObjectArray == null ? null : new XSObjectListImpl(xSObjectArray, xSObjectArray.length));
        } else if (bl2) {
            xSSimpleType = this.fSchemaHandler.fDVFactory.createTypeList(string, xSDocumentInfo.fTargetNamespace, (short)n2, (XSSimpleType)object3, xSObjectArray == null ? null : new XSObjectListImpl(xSObjectArray, xSObjectArray.length));
        } else if (bl3) {
            XSSimpleType[] xSSimpleTypeArray = ((ArrayList)object2).toArray(new XSSimpleType[((ArrayList)object2).size()]);
            xSSimpleType = this.fSchemaHandler.fDVFactory.createTypeUnion(string, xSDocumentInfo.fTargetNamespace, (short)n2, xSSimpleTypeArray, xSObjectArray == null ? null : new XSObjectListImpl(xSObjectArray, xSObjectArray.length));
        }
        if (bl && element4 != null) {
            XSDAbstractTraverser.FacetInfo facetInfo = this.traverseFacets(element4, xSSimpleType, (XSSimpleType)object3, xSDocumentInfo);
            element4 = facetInfo.nodeAfterFacets;
            try {
                this.fValidationState.setNamespaceSupport(xSDocumentInfo.fNamespaceSupport);
                xSSimpleType.applyFacets(facetInfo.facetdata, facetInfo.fPresentFacets, facetInfo.fFixedFacets, this.fValidationState);
            }
            catch (InvalidDatatypeFacetException invalidDatatypeFacetException) {
                this.reportSchemaError(invalidDatatypeFacetException.getKey(), invalidDatatypeFacetException.getArgs(), element2);
                xSSimpleType = this.fSchemaHandler.fDVFactory.createTypeRestriction(string, xSDocumentInfo.fTargetNamespace, (short)n2, (XSSimpleType)object3, xSObjectArray == null ? null : new XSObjectListImpl(xSObjectArray, xSObjectArray.length));
            }
        }
        if (element4 != null) {
            if (bl) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_RESTRICTION, "(annotation?, (simpleType?, (minExclusive | minInclusive | maxExclusive | maxInclusive | totalDigits | fractionDigits | length | minLength | maxLength | enumeration | whiteSpace | pattern)*))", DOMUtil.getLocalName(element4)}, element4);
            } else if (bl2) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_LIST, "(annotation?, (simpleType?))", DOMUtil.getLocalName(element4)}, element4);
            } else if (bl3) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_UNION, "(annotation?, (simpleType*))", DOMUtil.getLocalName(element4)}, element4);
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
        return xSSimpleType;
    }

    private XSSimpleType findDTValidator(Element element, String string, QName qName, short s, XSDocumentInfo xSDocumentInfo) {
        if (qName == null) {
            return null;
        }
        XSTypeDefinition xSTypeDefinition = (XSTypeDefinition)this.fSchemaHandler.getGlobalDecl(xSDocumentInfo, 7, qName, element);
        if (xSTypeDefinition == null) {
            return null;
        }
        if (xSTypeDefinition.getTypeCategory() != 16) {
            this.reportSchemaError("cos-st-restricts.1.1", new Object[]{qName.rawname, string}, element);
            return null;
        }
        if (xSTypeDefinition == SchemaGrammar.fAnySimpleType && s == 2) {
            if (this.checkBuiltIn(string, xSDocumentInfo.fTargetNamespace)) {
                return null;
            }
            this.reportSchemaError("cos-st-restricts.1.1", new Object[]{qName.rawname, string}, element);
            return null;
        }
        if ((xSTypeDefinition.getFinal() & s) != 0) {
            if (s == 2) {
                this.reportSchemaError("st-props-correct.3", new Object[]{string, qName.rawname}, element);
            } else if (s == 16) {
                this.reportSchemaError("cos-st-restricts.2.3.1.1", new Object[]{qName.rawname, string}, element);
            } else if (s == 8) {
                this.reportSchemaError("cos-st-restricts.3.3.1.1", new Object[]{qName.rawname, string}, element);
            }
            return null;
        }
        return (XSSimpleType)xSTypeDefinition;
    }

    private final boolean checkBuiltIn(String string, String string2) {
        if (string2 != SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            return false;
        }
        if (SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(string) != null) {
            this.fIsBuiltIn = true;
        }
        return this.fIsBuiltIn;
    }

    private boolean isListDatatype(XSSimpleType xSSimpleType) {
        if (xSSimpleType.getVariety() == 2) {
            return true;
        }
        if (xSSimpleType.getVariety() == 3) {
            XSObjectList xSObjectList = xSSimpleType.getMemberTypes();
            for (int i = 0; i < xSObjectList.getLength(); ++i) {
                if (((XSSimpleType)xSObjectList.item(i)).getVariety() != 2) continue;
                return true;
            }
        }
        return false;
    }

    private XSSimpleType errorType(String string, String string2, short s) {
        XSSimpleType xSSimpleType = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getTypeDefinition("string");
        switch (s) {
            case 2: {
                return this.fSchemaHandler.fDVFactory.createTypeRestriction(string, string2, (short)0, xSSimpleType, null);
            }
            case 16: {
                return this.fSchemaHandler.fDVFactory.createTypeList(string, string2, (short)0, xSSimpleType, null);
            }
            case 8: {
                return this.fSchemaHandler.fDVFactory.createTypeUnion(string, string2, (short)0, new XSSimpleType[]{xSSimpleType}, null);
            }
        }
        return null;
    }
}

