/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.SchemaAttributeModelImpl;
import org.apache.xmlbeans.impl.schema.SchemaGlobalAttributeImpl;
import org.apache.xmlbeans.impl.schema.SchemaGlobalElementImpl;
import org.apache.xmlbeans.impl.schema.SchemaIdentityConstraintImpl;
import org.apache.xmlbeans.impl.schema.SchemaLocalAttributeImpl;
import org.apache.xmlbeans.impl.schema.SchemaLocalElementImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.StscComplexTypeResolver;
import org.apache.xmlbeans.impl.schema.StscSimpleTypeResolver;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.schema.StscTranslator;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.Element;
import org.apache.xmlbeans.impl.xb.xsdschema.KeyrefDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;

public class StscResolver {
    public static void resolveAll() {
        StscState state = StscState.get();
        SchemaType[] documentTypes = state.documentTypes();
        for (int i = 0; i < documentTypes.length; ++i) {
            StscResolver.resolveSubstitutionGroup((SchemaTypeImpl)documentTypes[i]);
        }
        ArrayList<SchemaType> allSeenTypes = new ArrayList<SchemaType>();
        allSeenTypes.addAll(Arrays.asList(state.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(state.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(state.redefinedGlobalTypes()));
        allSeenTypes.addAll(Arrays.asList(state.globalTypes()));
        for (int i = 0; i < allSeenTypes.size(); ++i) {
            SchemaType gType = (SchemaType)allSeenTypes.get(i);
            StscResolver.resolveType((SchemaTypeImpl)gType);
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
        }
        StscResolver.resolveIdentityConstraints();
    }

    public static boolean resolveType(SchemaTypeImpl sImpl) {
        if (sImpl.isResolved()) {
            return true;
        }
        if (sImpl.isResolving()) {
            StscState.get().error("Cyclic dependency error", 13, sImpl.getParseObject());
            return false;
        }
        sImpl.startResolving();
        if (sImpl.isDocumentType()) {
            StscResolver.resolveDocumentType(sImpl);
        } else if (sImpl.isAttributeType()) {
            StscResolver.resolveAttributeType(sImpl);
        } else if (sImpl.isSimpleType()) {
            StscSimpleTypeResolver.resolveSimpleType(sImpl);
        } else {
            StscComplexTypeResolver.resolveComplexType(sImpl);
        }
        sImpl.finishResolving();
        return true;
    }

    public static boolean resolveSubstitutionGroup(SchemaTypeImpl sImpl) {
        assert (sImpl.isDocumentType());
        if (sImpl.isSGResolved()) {
            return true;
        }
        if (sImpl.isSGResolving()) {
            StscState.get().error("Cyclic dependency error", 13, sImpl.getParseObject());
            return false;
        }
        sImpl.startResolvingSGs();
        TopLevelElement elt = (TopLevelElement)sImpl.getParseObject();
        SchemaTypeImpl substitutionGroup = null;
        QName eltName = new QName(sImpl.getTargetNamespace(), elt.getName());
        if (elt.isSetSubstitutionGroup()) {
            substitutionGroup = StscState.get().findDocumentType(elt.getSubstitutionGroup(), sImpl.getChameleonNamespace(), sImpl.getTargetNamespace());
            if (substitutionGroup == null) {
                StscState.get().notFoundError(elt.getSubstitutionGroup(), 1, elt.xgetSubstitutionGroup(), true);
            } else if (!StscResolver.resolveSubstitutionGroup(substitutionGroup)) {
                substitutionGroup = null;
            } else {
                sImpl.setSubstitutionGroup(elt.getSubstitutionGroup());
            }
        }
        while (substitutionGroup != null) {
            substitutionGroup.addSubstitutionGroupMember(eltName);
            if (substitutionGroup.getSubstitutionGroup() == null) break;
            substitutionGroup = StscState.get().findDocumentType(substitutionGroup.getSubstitutionGroup(), substitutionGroup.getChameleonNamespace(), null);
            assert (substitutionGroup != null) : "Could not find document type for substitutionGroup";
            if (StscResolver.resolveSubstitutionGroup(substitutionGroup)) continue;
            substitutionGroup = null;
        }
        sImpl.finishResolvingSGs();
        return true;
    }

    public static void resolveDocumentType(SchemaTypeImpl sImpl) {
        assert (sImpl.isResolving());
        assert (sImpl.isDocumentType());
        ArrayList<SchemaType> anonTypes = new ArrayList<SchemaType>();
        SchemaGlobalElementImpl element = (SchemaGlobalElementImpl)StscTranslator.translateElement((Element)sImpl.getParseObject(), sImpl.getTargetNamespace(), sImpl.isChameleon(), null, null, anonTypes, sImpl);
        SchemaLocalElementImpl contentModel = null;
        if (element != null) {
            StscState.get().addGlobalElement(element);
            contentModel = new SchemaLocalElementImpl();
            contentModel.setParticleType(4);
            StscTranslator.copyGlobalElementToLocalElement(element, contentModel);
            contentModel.setMinOccurs(BigInteger.ONE);
            contentModel.setMaxOccurs(BigInteger.ONE);
            contentModel.setTransitionNotes(QNameSet.EMPTY, true);
        }
        Map<QName, SchemaProperty> elementPropertyModel = StscComplexTypeResolver.buildContentPropertyModelByQName(contentModel, sImpl);
        SchemaTypeImpl baseType = sImpl.getSubstitutionGroup() == null ? BuiltinSchemaTypeSystem.ST_ANY_TYPE : StscState.get().findDocumentType(sImpl.getSubstitutionGroup(), sImpl.isChameleon() ? sImpl.getTargetNamespace() : null, null);
        sImpl.setBaseTypeRef(baseType.getRef());
        sImpl.setBaseDepth(baseType.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        sImpl.setComplexTypeVariety(3);
        sImpl.setContentModel(contentModel, new SchemaAttributeModelImpl(), elementPropertyModel, Collections.EMPTY_MAP, false);
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, QNameSet.EMPTY, false);
        sImpl.setAnonymousTypeRefs(StscResolver.makeRefArray(anonTypes));
    }

    public static void resolveAttributeType(SchemaTypeImpl sImpl) {
        assert (sImpl.isResolving());
        assert (sImpl.isAttributeType());
        ArrayList<SchemaType> anonTypes = new ArrayList<SchemaType>();
        SchemaGlobalAttributeImpl attribute = (SchemaGlobalAttributeImpl)StscTranslator.translateAttribute((Attribute)sImpl.getParseObject(), sImpl.getTargetNamespace(), null, sImpl.isChameleon(), anonTypes, sImpl, null, false);
        SchemaAttributeModelImpl attributeModel = new SchemaAttributeModelImpl();
        if (attribute != null) {
            StscState.get().addGlobalAttribute(attribute);
            SchemaLocalAttributeImpl attributeCopy = new SchemaLocalAttributeImpl();
            StscTranslator.copyGlobalAttributeToLocalAttribute(attribute, attributeCopy);
            attributeModel.addAttribute(attributeCopy);
        }
        sImpl.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_TYPE.getRef());
        sImpl.setBaseDepth(sImpl.getBaseDepth() + 1);
        sImpl.setDerivationType(1);
        sImpl.setComplexTypeVariety(1);
        Map<QName, SchemaProperty> attributePropertyModel = StscComplexTypeResolver.buildAttributePropertyModelByQName(attributeModel, sImpl);
        sImpl.setContentModel(null, attributeModel, Collections.EMPTY_MAP, attributePropertyModel, false);
        sImpl.setWildcardSummary(QNameSet.EMPTY, false, QNameSet.EMPTY, false);
        sImpl.setAnonymousTypeRefs(StscResolver.makeRefArray(anonTypes));
    }

    private static SchemaType.Ref[] makeRefArray(Collection<SchemaType> typeList) {
        return (SchemaType.Ref[])typeList.stream().map(SchemaType::getRef).toArray(SchemaType.Ref[]::new);
    }

    public static void resolveIdentityConstraints() {
        SchemaIdentityConstraintImpl[] idcs;
        StscState state = StscState.get();
        for (SchemaIdentityConstraintImpl idc : idcs = state.idConstraints()) {
            if (idc.isResolved()) continue;
            KeyrefDocument.Keyref xsdkr = (KeyrefDocument.Keyref)idc.getParseObject();
            QName keyName = xsdkr.getRefer();
            SchemaIdentityConstraintImpl key = null;
            key = state.findIdConstraint(keyName, idc.getChameleonNamespace(), idc.getTargetNamespace());
            if (key == null) {
                state.notFoundError(keyName, 5, xsdkr, true);
                continue;
            }
            if (key.getConstraintCategory() == 2) {
                state.error("c-props-correct.1", null, idc.getParseObject());
            }
            if (key.getFields().length != idc.getFields().length) {
                state.error("c-props-correct.2", null, idc.getParseObject());
            }
            idc.setReferencedKey(key.getRef());
        }
    }
}

