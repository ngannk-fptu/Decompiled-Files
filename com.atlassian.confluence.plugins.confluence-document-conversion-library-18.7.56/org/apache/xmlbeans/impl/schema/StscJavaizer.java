/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.UserType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.impl.schema.SchemaPropertyImpl;
import org.apache.xmlbeans.impl.schema.SchemaStringEnumEntryImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.StscState;

public class StscJavaizer {
    private static final Logger LOG = LogManager.getLogger(StscJavaizer.class);
    private static final int MAX_ENUM_COUNT = 3668;
    private static final String[] PREFIXES = new String[]{"get", "xget", "isNil", "isSet", "sizeOf", "set", "xset", "addNew", "setNil", "unset", "insert", "add", "insertNew", "addNew", "remove"};
    static String[] PROTECTED_PROPERTIES = new String[]{"StringValue", "BooleanValue", "ByteValue", "ShortValue", "IntValue", "LongValue", "BigIntegerValue", "BigDecimalValue", "FloatValue", "DoubleValue", "ByteArrayValue", "EnumValue", "CalendarValue", "DateValue", "GDateValue", "GDurationValue", "QNameValue", "ListValue", "ObjectValue", "Class"};
    static Set<String> PROTECTED_PROPERTIES_SET = new HashSet<String>(Arrays.asList(PROTECTED_PROPERTIES));

    public static void javaizeAllTypes(boolean javaize) {
        StscState state = StscState.get();
        ArrayList<SchemaType> allSeenTypes = new ArrayList<SchemaType>();
        allSeenTypes.addAll(Arrays.asList(state.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(state.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(state.globalTypes()));
        if (javaize) {
            StscJavaizer.assignGlobalJavaNames(allSeenTypes);
        }
        for (int i = 0; i < allSeenTypes.size(); ++i) {
            SchemaType gType = (SchemaType)allSeenTypes.get(i);
            if (javaize) {
                StscJavaizer.javaizeType((SchemaTypeImpl)gType);
                String className = gType.getFullJavaName();
                if (className != null) {
                    state.addClassname(className.replace('$', '.'), gType);
                }
            } else {
                StscJavaizer.skipJavaizingType((SchemaTypeImpl)gType);
            }
            allSeenTypes.addAll(Arrays.asList(gType.getAnonymousTypes()));
            StscJavaizer.addAnonymousTypesFromRedefinition(gType, allSeenTypes);
        }
    }

    static void assignGlobalJavaNames(Collection<SchemaType> schemaTypes) {
        HashSet<String> usedNames = new HashSet<String>();
        StscState state = StscState.get();
        for (SchemaType schemaType : schemaTypes) {
            SchemaTypeImpl sImpl = (SchemaTypeImpl)schemaType;
            QName topName = StscJavaizer.findTopName(sImpl);
            String pickedName = state.getJavaname(topName, sImpl.isDocumentType() ? 2 : 1);
            if (!sImpl.isUnjavaized()) continue;
            sImpl.setFullJavaName(StscJavaizer.pickFullJavaClassName(usedNames, StscJavaizer.findTopName(sImpl), pickedName, sImpl.isDocumentType(), sImpl.isAttributeType()));
            sImpl.setFullJavaImplName(StscJavaizer.pickFullJavaImplName(usedNames, sImpl.getFullJavaName()));
            StscJavaizer.setExtensions(sImpl, state);
        }
        StscJavaizer.setUserTypes(state);
        StscJavaizer.verifyInterfaceNameCollisions(usedNames, state);
    }

    private static void verifyInterfaceNameCollisions(Set<String> usedNames, StscState state) {
        PrePostExtension[] prepost;
        InterfaceExtension[] exts;
        BindingConfig config = state.getBindingConfig();
        if (config == null) {
            return;
        }
        for (InterfaceExtension ext : exts = config.getInterfaceExtensions()) {
            String handler;
            if (usedNames.contains(ext.getInterface().toLowerCase(Locale.ROOT))) {
                state.error("InterfaceExtension interface '" + ext.getInterface() + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
            }
            if ((handler = ext.getStaticHandler()) == null || !usedNames.contains(handler.toLowerCase(Locale.ROOT))) continue;
            state.error("InterfaceExtension handler class '" + handler + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
        }
        for (PrePostExtension prePostExtension : prepost = config.getPrePostExtensions()) {
            String handler = prePostExtension.getStaticHandler();
            if (handler == null || !usedNames.contains(handler.toLowerCase(Locale.ROOT))) continue;
            state.error("PrePostExtension handler class '" + handler + "' creates a name collision with one of the generated interfaces or classes.", 0, null);
        }
    }

    private static void setUserTypes(StscState state) {
        BindingConfig config = state.getBindingConfig();
        if (config != null) {
            for (UserType utype : config.getUserTypes()) {
                SchemaTypeImpl sImpl = state.findGlobalType(utype.getName(), null, null);
                if (sImpl != null) {
                    sImpl.setUserTypeName(utype.getJavaName());
                    sImpl.setUserTypeHandlerName(utype.getStaticHandler());
                    continue;
                }
                LOG.atWarn().log("Cannot match user type for {}", (Object)utype.getName());
            }
        }
    }

    private static void setExtensions(SchemaTypeImpl sImpl, StscState state) {
        String javaName = sImpl.getFullJavaName();
        BindingConfig config = state.getBindingConfig();
        if (javaName != null && config != null) {
            sImpl.setInterfaceExtensions(config.getInterfaceExtensions(javaName));
            sImpl.setPrePostExtension(config.getPrePostExtension(javaName));
        }
    }

    private static boolean isStringType(SchemaType type) {
        if (type == null || type.getSimpleVariety() != 1) {
            return false;
        }
        return type.getPrimitiveType().getBuiltinTypeCode() == 12;
    }

    static String pickConstantName(Set<String> usedNames, String words) {
        String base = NameUtil.upperCaseUnderbar(words);
        if (base.length() == 0) {
            base = "X";
        }
        if (base.startsWith("INT_")) {
            base = "X_" + base;
        }
        int index = 1;
        String uniqName = base;
        while (usedNames.contains(uniqName)) {
            uniqName = base + "_" + ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }

    static void skipJavaizingType(SchemaTypeImpl sImpl) {
        if (sImpl.isJavaized()) {
            return;
        }
        SchemaTypeImpl baseType = (SchemaTypeImpl)sImpl.getBaseType();
        if (baseType != null) {
            StscJavaizer.skipJavaizingType(baseType);
        }
        sImpl.startJavaizing();
        StscJavaizer.secondPassProcessType(sImpl);
        sImpl.finishJavaizing();
    }

    static void secondPassProcessType(SchemaTypeImpl sImpl) {
        if (!StscJavaizer.isStringType(sImpl)) {
            return;
        }
        XmlAnySimpleType[] enumVals = sImpl.getEnumerationValues();
        if (enumVals == null) {
            return;
        }
        if (enumVals.length > 3668) {
            StscState.get().warning("SchemaType Enumeration found with too many enumeration values to create a Java enumeration. The base SchemaType \"" + sImpl.getBaseEnumType() + "\" will be used instead", 1, null);
            return;
        }
        SchemaType basedOn = sImpl.getBaseEnumType();
        if (basedOn == null) {
            return;
        }
        SchemaStringEnumEntry[] entryArray = new SchemaStringEnumEntry[enumVals.length];
        if (basedOn == sImpl) {
            HashSet<String> usedNames = new HashSet<String>();
            for (int i = 0; i < enumVals.length; ++i) {
                String val = enumVals[i].getStringValue();
                entryArray[i] = new SchemaStringEnumEntryImpl(val, i + 1, StscJavaizer.pickConstantName(usedNames, val));
            }
        } else {
            for (int i = 0; i < enumVals.length; ++i) {
                String val = enumVals[i].getStringValue();
                entryArray[i] = basedOn.enumEntryForString(val);
            }
        }
        sImpl.setStringEnumEntries(entryArray);
    }

    static void javaizeType(SchemaTypeImpl sImpl) {
        if (sImpl.isJavaized()) {
            return;
        }
        SchemaTypeImpl baseType = (SchemaTypeImpl)sImpl.getBaseType();
        if (baseType != null) {
            StscJavaizer.javaizeType(baseType);
        }
        if (sImpl.getContentBasedOnType() != null && sImpl.getContentBasedOnType() != baseType) {
            StscJavaizer.javaizeType((SchemaTypeImpl)sImpl.getContentBasedOnType());
        }
        sImpl.startJavaizing();
        sImpl.setCompiled(true);
        StscJavaizer.secondPassProcessType(sImpl);
        if (!sImpl.isSimpleType()) {
            SchemaProperty[] baseProps;
            SchemaProperty[] eltProps = sImpl.getElementProperties();
            SchemaProperty[] attrProps = sImpl.getAttributeProperties();
            HashSet<String> usedPropNames = new HashSet<String>();
            for (SchemaProperty baseProp : baseProps = baseType.getProperties()) {
                String name = baseProp.getJavaPropertyName();
                assert (!usedPropNames.contains(name));
                usedPropNames.add(name);
            }
            StscJavaizer.avoidExtensionMethods(usedPropNames, sImpl);
            boolean doInherited = true;
            while (true) {
                if (eltProps.length > 0) {
                    StscJavaizer.assignJavaPropertyNames(usedPropNames, eltProps, baseType, doInherited);
                }
                StscJavaizer.assignJavaPropertyNames(usedPropNames, attrProps, baseType, doInherited);
                if (!doInherited) break;
                doInherited = false;
            }
            SchemaProperty[] allprops = sImpl.getProperties();
            boolean insensitive = StscJavaizer.isPropertyModelOrderInsensitive(allprops);
            StscJavaizer.assignJavaTypeCodes(allprops);
            sImpl.setOrderSensitive(!insensitive);
        }
        if (sImpl.getFullJavaName() != null || sImpl.getOuterType() != null) {
            StscJavaizer.assignJavaAnonymousTypeNames(sImpl);
        }
        sImpl.finishJavaizing();
    }

    private static void avoidExtensionMethods(Set<String> usedPropNames, SchemaTypeImpl sImpl) {
        InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (InterfaceExtension ext : exts) {
                InterfaceExtension.MethodSignature[] methods;
                for (InterfaceExtension.MethodSignature method : methods = ext.getMethods()) {
                    String methodName = method.getName();
                    for (String prefix : PREFIXES) {
                        if (!methodName.startsWith(prefix)) continue;
                        usedPropNames.add(methodName.substring(prefix.length()));
                    }
                }
            }
        }
    }

    static void assignJavaAnonymousTypeNames(SchemaTypeImpl outerType) {
        SchemaType scanOuterType;
        HashSet<String> usedTypeNames = new HashSet<String>();
        SchemaType[] anonymousTypes = outerType.getAnonymousTypes();
        StscState state = StscState.get();
        int nrOfAnonTypes = anonymousTypes.length;
        if (outerType.isRedefinition()) {
            ArrayList<SchemaType> list = new ArrayList<SchemaType>();
            StscJavaizer.addAnonymousTypesFromRedefinition(outerType, list);
            if (list.size() > 0) {
                SchemaType[] temp = new SchemaType[nrOfAnonTypes + list.size()];
                list.toArray(temp);
                System.arraycopy(anonymousTypes, 0, temp, list.size(), nrOfAnonTypes);
                anonymousTypes = temp;
            }
        }
        for (scanOuterType = outerType; scanOuterType != null; scanOuterType = scanOuterType.getOuterType()) {
            usedTypeNames.add(scanOuterType.getShortJavaName());
        }
        for (scanOuterType = outerType; scanOuterType != null; scanOuterType = scanOuterType.getOuterType()) {
            usedTypeNames.add(scanOuterType.getShortJavaImplName());
        }
        usedTypeNames.add(StscJavaizer.getOutermostPackage(outerType.getFullJavaName()));
        for (int i = 0; i < anonymousTypes.length; ++i) {
            String javaname;
            SchemaTypeImpl sImpl = (SchemaTypeImpl)anonymousTypes[i];
            if (sImpl == null || sImpl.isSkippedAnonymousType()) continue;
            String localname = null;
            SchemaField containerField = sImpl.getContainerField();
            if (containerField != null) {
                QName qname = sImpl.getContainerField().getName();
                localname = qname.getLocalPart();
                javaname = state.getJavaname(sImpl.getContainerField().getName(), 1);
            } else {
                switch (sImpl.getOuterType().getSimpleVariety()) {
                    case 2: {
                        javaname = "Member";
                        break;
                    }
                    case 3: {
                        javaname = "Item";
                        break;
                    }
                    default: {
                        assert (false) : "Weird type " + sImpl.toString();
                        javaname = "Base";
                    }
                }
            }
            if (i < nrOfAnonTypes) {
                sImpl.setShortJavaName(StscJavaizer.pickInnerJavaClassName(usedTypeNames, localname, javaname));
                sImpl.setShortJavaImplName(StscJavaizer.pickInnerJavaImplName(usedTypeNames, localname, javaname == null ? null : javaname + "Impl"));
            } else {
                sImpl.setFullJavaName(outerType.getFullJavaName() + "$" + StscJavaizer.pickInnerJavaClassName(usedTypeNames, localname, javaname));
                sImpl.setFullJavaImplName(outerType.getFullJavaImplName() + "$" + StscJavaizer.pickInnerJavaImplName(usedTypeNames, localname, javaname == null ? null : javaname + "Impl"));
            }
            StscJavaizer.setExtensions(sImpl, state);
        }
    }

    static void assignJavaPropertyNames(Set<String> usedNames, SchemaProperty[] props, SchemaType baseType, boolean doInherited) {
        StscState state = StscState.get();
        for (SchemaProperty prop : props) {
            SchemaPropertyImpl sImpl = (SchemaPropertyImpl)prop;
            SchemaProperty baseProp = sImpl.isAttribute() ? baseType.getAttributeProperty(sImpl.getName()) : baseType.getElementProperty(sImpl.getName());
            if (baseProp == null == doInherited) continue;
            QName propQName = sImpl.getName();
            String theName = baseProp == null ? StscJavaizer.pickJavaPropertyName(usedNames, propQName.getLocalPart(), state.getJavaname(propQName, sImpl.isAttribute() ? 4 : 3)) : baseProp.getJavaPropertyName();
            sImpl.setJavaPropertyName(theName);
            boolean isArray = sImpl.getMaxOccurs() == null || sImpl.getMaxOccurs().compareTo(BigInteger.ONE) > 0;
            boolean isSingleton = !isArray && sImpl.getMaxOccurs().signum() > 0;
            boolean isOption = isSingleton && sImpl.getMinOccurs().signum() == 0;
            SchemaType javaBasedOnType = sImpl.getType();
            if (baseProp != null) {
                if (baseProp.extendsJavaArray()) {
                    isSingleton = false;
                    isOption = false;
                    isArray = true;
                }
                if (baseProp.extendsJavaSingleton()) {
                    isSingleton = true;
                }
                if (baseProp.extendsJavaOption()) {
                    isOption = true;
                }
                javaBasedOnType = baseProp.javaBasedOnType();
            }
            sImpl.setExtendsJava(javaBasedOnType.getRef(), isSingleton, isOption, isArray);
        }
    }

    static void assignJavaTypeCodes(SchemaProperty[] properties) {
        for (SchemaProperty property : properties) {
            SchemaPropertyImpl sImpl = (SchemaPropertyImpl)property;
            SchemaType sType = sImpl.javaBasedOnType();
            sImpl.setJavaTypeCode(StscJavaizer.javaTypeCodeForType(sType));
        }
    }

    static int javaTypeCodeInCommon(SchemaType[] types) {
        if (types == null || types.length == 0) {
            return 0;
        }
        int code = StscJavaizer.javaTypeCodeForType(types[0]);
        if (code == 19) {
            return code;
        }
        for (int i = 1; i < types.length; ++i) {
            if (code == StscJavaizer.javaTypeCodeForType(types[i])) continue;
            return 19;
        }
        return code;
    }

    static int javaTypeCodeForType(SchemaType sType) {
        if (!sType.isSimpleType()) {
            return 0;
        }
        if (((SchemaTypeImpl)sType).getUserTypeHandlerName() != null) {
            return 20;
        }
        if (sType.getSimpleVariety() == 2) {
            SchemaType baseType = sType.getUnionCommonBaseType();
            if (baseType != null && !baseType.isURType()) {
                sType = baseType;
            } else {
                return StscJavaizer.javaTypeCodeInCommon(sType.getUnionConstituentTypes());
            }
        }
        if (sType.getSimpleVariety() == 3) {
            return 16;
        }
        if (sType.isURType()) {
            return 0;
        }
        switch (sType.getPrimitiveType().getBuiltinTypeCode()) {
            case 2: {
                return 10;
            }
            case 3: {
                return 1;
            }
            case 4: {
                return 11;
            }
            case 5: {
                return 11;
            }
            case 6: {
                return 10;
            }
            case 7: {
                return 15;
            }
            case 8: {
                return 0;
            }
            case 9: {
                return 2;
            }
            case 10: {
                return 3;
            }
            case 11: {
                switch (sType.getDecimalSize()) {
                    case 8: {
                        return 4;
                    }
                    case 16: {
                        return 5;
                    }
                    case 32: {
                        return 6;
                    }
                    case 64: {
                        return 7;
                    }
                    case 1000000: {
                        return 9;
                    }
                }
                return 8;
            }
            case 12: {
                if (StscJavaizer.isStringType(sType.getBaseEnumType())) {
                    if (sType.getEnumerationValues() != null && sType.getEnumerationValues().length > 3668) {
                        return 10;
                    }
                    return 18;
                }
                return 10;
            }
            case 13: {
                return 13;
            }
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: {
                return 17;
            }
        }
        assert (false) : "unrecognized code " + sType.getPrimitiveType().getBuiltinTypeCode();
        throw new IllegalStateException("unrecognized code " + sType.getPrimitiveType().getBuiltinTypeCode() + " of " + sType.getPrimitiveType().getName());
    }

    static boolean isPropertyModelOrderInsensitive(SchemaProperty[] properties) {
        for (SchemaProperty prop : properties) {
            if (prop.hasNillable() == 1) {
                return false;
            }
            if (prop.hasDefault() == 1) {
                return false;
            }
            if (prop.hasFixed() == 1) {
                return false;
            }
            if (prop.hasDefault() == 0 || prop.getDefaultText() != null) continue;
            return false;
        }
        return true;
    }

    static boolean protectReservedGlobalClassNames(String name) {
        int i = name.lastIndexOf(46);
        String lastSegment = name.substring(i + 1);
        return lastSegment.endsWith("Document") && !lastSegment.equals("Document");
    }

    static boolean protectReservedInnerClassNames(String name) {
        return name.equals("Enum") || name.equals("Factory");
    }

    static boolean protectReservedPropertyNames(String name) {
        return PROTECTED_PROPERTIES_SET.contains(name) || name.endsWith("Array") && !name.equals("Array");
    }

    static String pickFullJavaClassName(Set<String> usedNames, QName qName, String configname, boolean isDocument, boolean isAttrType) {
        boolean protect;
        String base;
        if (configname != null && configname.indexOf(46) >= 0) {
            base = configname;
            protect = StscJavaizer.protectReservedGlobalClassNames(base);
        } else {
            String javaPrefix;
            StscState state = StscState.get();
            String uri = qName.getNamespaceURI();
            base = NameUtil.getClassNameFromQName(qName);
            String pkgPrefix = state.getPackageOverride(uri);
            if (pkgPrefix != null) {
                base = pkgPrefix + "." + base.substring(base.lastIndexOf(46) + 1);
            }
            if ((javaPrefix = state.getJavaPrefix(uri)) != null) {
                base = base.substring(0, base.lastIndexOf(46) + 1) + javaPrefix + base.substring(base.lastIndexOf(46) + 1);
            }
            if (configname != null) {
                base = base.substring(0, base.lastIndexOf(46) + 1) + configname;
            }
            protect = StscJavaizer.protectReservedGlobalClassNames(base);
            if (configname == null) {
                if (isDocument) {
                    base = base + "Document";
                } else if (isAttrType) {
                    base = base + "Attribute";
                }
                String javaSuffix = state.getJavaSuffix(uri);
                if (javaSuffix != null) {
                    base = base + javaSuffix;
                }
            }
        }
        String outermostPkg = StscJavaizer.getOutermostPackage(base);
        int index = 1;
        String uniqName = protect ? base + index : base;
        while (usedNames.contains(uniqName.toLowerCase(Locale.ROOT)) || uniqName.equals(outermostPkg)) {
            uniqName = base + ++index;
        }
        usedNames.add(uniqName.toLowerCase(Locale.ROOT));
        return uniqName;
    }

    static String getOutermostPackage(String fqcn) {
        if (fqcn == null) {
            return "";
        }
        int lastdot = fqcn.indexOf(46);
        if (lastdot < 0) {
            return "";
        }
        return fqcn.substring(0, lastdot);
    }

    static String pickFullJavaImplName(Set<String> usedNames, String intfName) {
        String className = intfName;
        String pkgName = null;
        int index = intfName.lastIndexOf(46);
        if (index >= 0) {
            className = intfName.substring(index + 1);
            pkgName = intfName.substring(0, index);
        }
        String base = pkgName + ".impl." + className + "Impl";
        index = 1;
        String uniqName = base;
        while (usedNames.contains(uniqName.toLowerCase(Locale.ROOT))) {
            uniqName = base + ++index;
        }
        usedNames.add(uniqName.toLowerCase(Locale.ROOT));
        return uniqName;
    }

    static String pickJavaPropertyName(Set<String> usedNames, String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName);
        }
        boolean protect = StscJavaizer.protectReservedPropertyNames(javaName);
        int index = 1;
        String uniqName = protect ? javaName + index : javaName;
        while (usedNames.contains(uniqName)) {
            uniqName = javaName + ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }

    static String pickInnerJavaClassName(Set<String> usedNames, String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName);
        }
        boolean protect = StscJavaizer.protectReservedInnerClassNames(javaName);
        int index = 1;
        String uniqName = protect ? javaName + index : javaName;
        while (usedNames.contains(uniqName)) {
            uniqName = javaName + ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }

    static String pickInnerJavaImplName(Set<String> usedNames, String localName, String javaName) {
        if (javaName == null) {
            javaName = NameUtil.upperCamelCase(localName) + "Impl";
        }
        String uniqName = javaName;
        int index = 1;
        while (usedNames.contains(uniqName)) {
            uniqName = javaName + ++index;
        }
        usedNames.add(uniqName);
        return uniqName;
    }

    static QName findTopName(SchemaType sType) {
        if (sType.getName() != null) {
            return sType.getName();
        }
        if (sType.isDocumentType()) {
            if (sType.getContentModel() == null || sType.getContentModel().getParticleType() != 4) {
                throw new IllegalStateException();
            }
            return sType.getDocumentElementName();
        }
        if (sType.isAttributeType()) {
            if (sType.getAttributeModel() == null || sType.getAttributeModel().getAttributes().length != 1) {
                throw new IllegalStateException();
            }
            return sType.getAttributeTypeAttributeName();
        }
        SchemaField sElt = sType.getContainerField();
        assert (sElt != null);
        assert (sType.getOuterType() == null);
        return sElt.getName();
    }

    static void addAnonymousTypesFromRedefinition(SchemaType sType, List<SchemaType> result) {
        while (((SchemaTypeImpl)sType).isRedefinition() && (sType.getDerivationType() == 2 || sType.isSimpleType())) {
            SchemaType[] newAnonTypes = (sType = sType.getBaseType()).getAnonymousTypes();
            if (newAnonTypes.length <= 0) continue;
            result.addAll(Arrays.asList(newAnonTypes));
        }
    }
}

