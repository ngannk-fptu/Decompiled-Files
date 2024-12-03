/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.SchemaAnnotationImpl;
import org.apache.xmlbeans.impl.schema.SchemaAttributeGroupImpl;
import org.apache.xmlbeans.impl.schema.SchemaAttributeModelImpl;
import org.apache.xmlbeans.impl.schema.SchemaContainer;
import org.apache.xmlbeans.impl.schema.SchemaGlobalAttributeImpl;
import org.apache.xmlbeans.impl.schema.SchemaGlobalElementImpl;
import org.apache.xmlbeans.impl.schema.SchemaIdentityConstraintImpl;
import org.apache.xmlbeans.impl.schema.SchemaLocalAttributeImpl;
import org.apache.xmlbeans.impl.schema.SchemaLocalElementImpl;
import org.apache.xmlbeans.impl.schema.SchemaModelGroupImpl;
import org.apache.xmlbeans.impl.schema.SchemaParticleImpl;
import org.apache.xmlbeans.impl.schema.SchemaPropertyImpl;
import org.apache.xmlbeans.impl.schema.SchemaStringEnumEntryImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.StscComplexTypeResolver;
import org.apache.xmlbeans.impl.schema.XQuerySchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.XmlValueRef;
import org.apache.xmlbeans.impl.util.LongUTFDataInputStream;
import org.apache.xmlbeans.impl.util.LongUTFDataOutputStream;
import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupDocument;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;

class XsbReader {
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private final SchemaTypeSystemImpl typeSystem;
    private LongUTFDataInputStream _input;
    private LongUTFDataOutputStream _output;
    private SchemaTypeSystemImpl.StringPool _stringPool;
    private String _handle;
    private int _majorver;
    private int _minorver;
    private int _releaseno;
    int _actualfiletype;

    XsbReader(SchemaTypeSystemImpl typeSystem, String handle) {
        this.typeSystem = typeSystem;
        this._handle = handle;
        this._stringPool = new SchemaTypeSystemImpl.StringPool(this._handle, typeSystem.getName());
    }

    public XsbReader(SchemaTypeSystemImpl typeSystem, String handle, int filetype) {
        int actualfiletype;
        this.typeSystem = typeSystem;
        String resourcename = typeSystem.getBasePackage() + handle + ".xsb";
        InputStream rawinput = typeSystem.getLoaderStream(resourcename);
        if (rawinput == null) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Could not locate compiled schema resource " + resourcename, typeSystem.getName(), handle, 0);
        }
        this._input = new LongUTFDataInputStream(rawinput);
        this._handle = handle;
        int magic = this.readInt();
        if (magic != -629491010) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Wrong magic cookie", typeSystem.getName(), handle, 1);
        }
        this._majorver = this.readShort();
        this._minorver = this.readShort();
        if (this._majorver != 2) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Wrong major version - expecting 2, got " + this._majorver, typeSystem.getName(), handle, 2);
        }
        if (this._minorver > 24) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Incompatible minor version - expecting up to 24, got " + this._minorver, typeSystem.getName(), handle, 3);
        }
        if (this._minorver < 14) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: Incompatible minor version - expecting at least 14, got " + this._minorver, typeSystem.getName(), handle, 3);
        }
        if (this.atLeast(2, 18, 0)) {
            this._releaseno = this.readShort();
        }
        if ((actualfiletype = this.readShort()) != filetype && filetype != 65535) {
            throw new SchemaTypeLoaderException("XML-BEANS compiled schema: File has the wrong type - expecting type " + filetype + ", got type " + actualfiletype, typeSystem.getName(), handle, 4);
        }
        this._stringPool = new SchemaTypeSystemImpl.StringPool(this._handle, typeSystem.getName());
        this._stringPool.readFrom(this._input);
        this._actualfiletype = actualfiletype;
    }

    protected boolean atLeast(int majorver, int minorver, int releaseno) {
        if (this._majorver > majorver) {
            return true;
        }
        if (this._majorver < majorver) {
            return false;
        }
        if (this._minorver > minorver) {
            return true;
        }
        if (this._minorver < minorver) {
            return false;
        }
        return this._releaseno >= releaseno;
    }

    protected boolean atMost(int majorver, int minorver, int releaseno) {
        if (this._majorver > majorver) {
            return false;
        }
        if (this._majorver < majorver) {
            return true;
        }
        if (this._minorver > minorver) {
            return false;
        }
        if (this._minorver < minorver) {
            return true;
        }
        return this._releaseno <= releaseno;
    }

    int getActualFiletype() {
        return this._actualfiletype;
    }

    void writeRealHeader(String handle, int filetype) {
        String resourcename = handle.indexOf(47) >= 0 ? handle + ".xsb" : this.typeSystem.getBasePackage() + handle + ".xsb";
        OutputStream rawoutput = this.typeSystem.getSaverStream(resourcename, this._handle);
        if (rawoutput == null) {
            throw new SchemaTypeLoaderException("Could not write compiled schema resource " + resourcename, this.typeSystem.getName(), handle, 12);
        }
        this._output = new LongUTFDataOutputStream(rawoutput);
        this._handle = handle;
        this.writeInt(-629491010);
        this.writeShort(2);
        this.writeShort(24);
        this.writeShort(0);
        this.writeShort(filetype);
        this._stringPool.writeTo(this._output);
    }

    void readEnd() {
        try {
            if (this._input != null) {
                this._input.close();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this._input = null;
        this._stringPool = null;
        this._handle = null;
    }

    void writeEnd() {
        try {
            if (this._output != null) {
                this._output.flush();
                this._output.close();
            }
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
        this._output = null;
        this._stringPool = null;
        this._handle = null;
    }

    void writeIndexData() {
        this.typeSystem.getTypePool().writeHandlePool(this);
        this.writeQNameMap(this.typeSystem.globalElements());
        this.writeQNameMap(this.typeSystem.globalAttributes());
        this.writeQNameMap(this.typeSystem.modelGroups());
        this.writeQNameMap(this.typeSystem.attributeGroups());
        this.writeQNameMap(this.typeSystem.identityConstraints());
        this.writeQNameMap(this.typeSystem.globalTypes());
        this.writeDocumentTypeMap(this.typeSystem.documentTypes());
        this.writeAttributeTypeMap(this.typeSystem.attributeTypes());
        this.writeClassnameMap(this.typeSystem.getTypeRefsByClassname());
        this.writeNamespaces(this.typeSystem.getNamespaces());
        this.writeQNameMap(this.typeSystem.redefinedGlobalTypes());
        this.writeQNameMap(this.typeSystem.redefinedModelGroups());
        this.writeQNameMap(this.typeSystem.redefinedAttributeGroups());
        this.writeAnnotations(this.typeSystem.annotations());
    }

    int readShort() {
        try {
            return this._input.readUnsignedShort();
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    void writeShort(int s) {
        if (s >= 65535 || s < -1) {
            throw new SchemaTypeLoaderException("Value " + s + " out of range: must fit in a 16-bit unsigned short.", this.typeSystem.getName(), this._handle, 10);
        }
        if (this._output != null) {
            try {
                this._output.writeShort(s);
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
            }
        }
    }

    int readUnsignedShortOrInt() {
        try {
            return this._input.readUnsignedShortOrInt();
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    void writeShortOrInt(int s) {
        if (this._output != null) {
            try {
                this._output.writeShortOrInt(s);
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
            }
        }
    }

    int readInt() {
        try {
            return this._input.readInt();
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    void writeInt(int i) {
        if (this._output != null) {
            try {
                this._output.writeInt(i);
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
            }
        }
    }

    String readString() {
        int code = this.readUnsignedShortOrInt();
        return this._stringPool.stringForCode(code);
    }

    void writeString(String str) {
        int code = this._stringPool.codeForString(str);
        this.writeShortOrInt(code);
    }

    QName readQName() {
        String namespace = this.readString();
        String localname = this.readString();
        if (localname == null) {
            return null;
        }
        return new QName(namespace, localname);
    }

    void writeQName(QName qname) {
        if (qname == null) {
            this.writeString(null);
            this.writeString(null);
            return;
        }
        this.writeString(qname.getNamespaceURI());
        this.writeString(qname.getLocalPart());
    }

    SOAPArrayType readSOAPArrayType() {
        QName qName = this.readQName();
        String dimensions = this.readString();
        if (qName == null) {
            return null;
        }
        return new SOAPArrayType(qName, dimensions);
    }

    void writeSOAPArrayType(SOAPArrayType arrayType) {
        if (arrayType == null) {
            this.writeQName(null);
            this.writeString(null);
        } else {
            this.writeQName(arrayType.getQName());
            this.writeString(arrayType.soap11DimensionString());
        }
    }

    void writeAnnotation(SchemaAnnotation a) {
        if (a == null) {
            this.writeInt(-1);
            return;
        }
        SchemaAnnotation.Attribute[] attributes = a.getAttributes();
        this.writeInt(attributes.length);
        for (SchemaAnnotation.Attribute attribute : attributes) {
            QName name = attribute.getName();
            String value = attribute.getValue();
            String valueURI = attribute.getValueUri();
            this.writeQName(name);
            this.writeString(value);
            this.writeString(valueURI);
        }
        XmlObject[] documentationItems = a.getUserInformation();
        this.writeInt(documentationItems.length);
        XmlOptions opt = new XmlOptions().setSaveOuter().setSaveAggressiveNamespaces();
        for (XmlObject doc : documentationItems) {
            this.writeString(doc.xmlText(opt));
        }
        XmlObject[] appInfoItems = a.getApplicationInformation();
        this.writeInt(appInfoItems.length);
        for (XmlObject doc : appInfoItems) {
            this.writeString(doc.xmlText(opt));
        }
    }

    SchemaAnnotation readAnnotation(SchemaContainer c) {
        if (!this.atLeast(2, 19, 0)) {
            return null;
        }
        int n = this.readInt();
        if (n == -1) {
            return null;
        }
        SchemaAnnotation.Attribute[] attributes = new SchemaAnnotation.Attribute[n];
        for (int i = 0; i < n; ++i) {
            QName name = this.readQName();
            String value = this.readString();
            String valueUri = null;
            if (this.atLeast(2, 24, 0)) {
                valueUri = this.readString();
            }
            attributes[i] = new SchemaAnnotationImpl.AttributeImpl(name, value, valueUri);
        }
        n = this.readInt();
        String[] docStrings = new String[n];
        for (int i = 0; i < n; ++i) {
            docStrings[i] = this.readString();
        }
        n = this.readInt();
        String[] appInfoStrings = new String[n];
        for (int i = 0; i < n; ++i) {
            appInfoStrings[i] = this.readString();
        }
        return new SchemaAnnotationImpl(c, appInfoStrings, docStrings, attributes);
    }

    void writeAnnotations(SchemaAnnotation[] anns) {
        this.writeInt(anns.length);
        for (SchemaAnnotation ann : anns) {
            this.writeAnnotation(ann);
        }
    }

    List<SchemaAnnotation> readAnnotations() {
        int n = this.readInt();
        ArrayList<SchemaAnnotation> result = new ArrayList<SchemaAnnotation>(n);
        SchemaContainer container = this.typeSystem.getContainerNonNull("");
        for (int i = 0; i < n; ++i) {
            result.add(this.readAnnotation(container));
        }
        return result;
    }

    SchemaComponent.Ref readHandle() {
        String handle = this.readString();
        if (handle == null) {
            return null;
        }
        if (handle.charAt(0) != '_') {
            return this.typeSystem.getTypePool().refForHandle(handle);
        }
        switch (handle.charAt(2)) {
            case 'I': {
                SchemaType st = (SchemaType)BuiltinSchemaTypeSystem.get().resolveHandle(handle);
                if (st != null) {
                    return st.getRef();
                }
                st = (SchemaType)XQuerySchemaTypeSystem.get().resolveHandle(handle);
                return st.getRef();
            }
            case 'T': {
                return this.typeSystem.getLinker().findTypeRef(QNameHelper.forPretty(handle, 4));
            }
            case 'E': {
                return this.typeSystem.getLinker().findElementRef(QNameHelper.forPretty(handle, 4));
            }
            case 'A': {
                return this.typeSystem.getLinker().findAttributeRef(QNameHelper.forPretty(handle, 4));
            }
            case 'M': {
                return this.typeSystem.getLinker().findModelGroupRef(QNameHelper.forPretty(handle, 4));
            }
            case 'N': {
                return this.typeSystem.getLinker().findAttributeGroupRef(QNameHelper.forPretty(handle, 4));
            }
            case 'D': {
                return this.typeSystem.getLinker().findIdentityConstraintRef(QNameHelper.forPretty(handle, 4));
            }
            case 'R': {
                SchemaGlobalAttribute attr = this.typeSystem.getLinker().findAttribute(QNameHelper.forPretty(handle, 4));
                if (attr == null) {
                    throw new SchemaTypeLoaderException("Cannot resolve attribute for handle " + handle, this.typeSystem.getName(), this._handle, 13);
                }
                return attr.getType().getRef();
            }
            case 'S': {
                SchemaGlobalElement elem = this.typeSystem.getLinker().findElement(QNameHelper.forPretty(handle, 4));
                if (elem == null) {
                    throw new SchemaTypeLoaderException("Cannot resolve element for handle " + handle, this.typeSystem.getName(), this._handle, 13);
                }
                return elem.getType().getRef();
            }
            case 'O': {
                return this.typeSystem.getLinker().findDocumentTypeRef(QNameHelper.forPretty(handle, 4));
            }
            case 'Y': {
                SchemaType type = this.typeSystem.getLinker().typeForSignature(handle.substring(4));
                if (type == null) {
                    throw new SchemaTypeLoaderException("Cannot resolve type for handle " + handle, this.typeSystem.getName(), this._handle, 13);
                }
                return type.getRef();
            }
        }
        throw new SchemaTypeLoaderException("Cannot resolve handle " + handle, this.typeSystem.getName(), this._handle, 13);
    }

    void writeHandle(SchemaComponent comp) {
        if (comp == null || comp.getTypeSystem() == this.typeSystem) {
            this.writeString(this.typeSystem.getTypePool().handleForComponent(comp));
            return;
        }
        switch (comp.getComponentType()) {
            case 3: {
                this.writeString("_XA_" + QNameHelper.pretty(comp.getName()));
                return;
            }
            case 6: {
                this.writeString("_XM_" + QNameHelper.pretty(comp.getName()));
                return;
            }
            case 4: {
                this.writeString("_XN_" + QNameHelper.pretty(comp.getName()));
                return;
            }
            case 1: {
                this.writeString("_XE_" + QNameHelper.pretty(comp.getName()));
                return;
            }
            case 5: {
                this.writeString("_XD_" + QNameHelper.pretty(comp.getName()));
                return;
            }
            case 0: {
                SchemaType type = (SchemaType)comp;
                if (type.isBuiltinType()) {
                    this.writeString("_BI_" + type.getName().getLocalPart());
                    return;
                }
                if (type.getName() != null) {
                    this.writeString("_XT_" + QNameHelper.pretty(type.getName()));
                } else if (type.isDocumentType()) {
                    this.writeString("_XO_" + QNameHelper.pretty(type.getDocumentElementName()));
                } else {
                    this.writeString("_XY_" + type);
                }
                return;
            }
        }
        assert (false);
        throw new SchemaTypeLoaderException("Cannot write handle for component " + comp, this.typeSystem.getName(), this._handle, 13);
    }

    SchemaType.Ref readTypeRef() {
        return (SchemaType.Ref)this.readHandle();
    }

    void writeType(SchemaType type) {
        this.writeHandle(type);
    }

    Map<QName, SchemaComponent.Ref> readQNameRefMap() {
        HashMap<QName, SchemaComponent.Ref> result = new HashMap<QName, SchemaComponent.Ref>();
        int size = this.readShort();
        for (int i = 0; i < size; ++i) {
            QName name = this.readQName();
            SchemaComponent.Ref obj = this.readHandle();
            result.put(name, obj);
        }
        return result;
    }

    List<SchemaComponent.Ref> readQNameRefMapAsList(List<QName> names) {
        int size = this.readShort();
        ArrayList<SchemaComponent.Ref> result = new ArrayList<SchemaComponent.Ref>(size);
        for (int i = 0; i < size; ++i) {
            QName name = this.readQName();
            SchemaComponent.Ref obj = this.readHandle();
            result.add(obj);
            names.add(name);
        }
        return result;
    }

    void writeQNameMap(SchemaComponent[] components) {
        this.writeShort(components.length);
        for (SchemaComponent component : components) {
            this.writeQName(component.getName());
            this.writeHandle(component);
        }
    }

    void writeDocumentTypeMap(SchemaType[] doctypes) {
        this.writeShort(doctypes.length);
        for (SchemaType doctype : doctypes) {
            this.writeQName(doctype.getDocumentElementName());
            this.writeHandle(doctype);
        }
    }

    void writeAttributeTypeMap(SchemaType[] attrtypes) {
        this.writeShort(attrtypes.length);
        for (SchemaType attrtype : attrtypes) {
            this.writeQName(attrtype.getAttributeTypeAttributeName());
            this.writeHandle(attrtype);
        }
    }

    SchemaType.Ref[] readTypeRefArray() {
        int size = this.readShort();
        SchemaType.Ref[] result = new SchemaType.Ref[size];
        for (int i = 0; i < size; ++i) {
            result[i] = this.readTypeRef();
        }
        return result;
    }

    void writeTypeArray(SchemaType[] array) {
        this.writeShort(array.length);
        for (SchemaType schemaType : array) {
            this.writeHandle(schemaType);
        }
    }

    Map<String, SchemaComponent.Ref> readClassnameRefMap() {
        HashMap<String, SchemaComponent.Ref> result = new HashMap<String, SchemaComponent.Ref>();
        int size = this.readShort();
        for (int i = 0; i < size; ++i) {
            String name = this.readString();
            SchemaComponent.Ref obj = this.readHandle();
            result.put(name, obj);
        }
        return result;
    }

    void writeClassnameMap(Map<String, SchemaComponent.Ref> typesByClass) {
        this.writeShort(typesByClass.size());
        typesByClass.forEach((className, ref) -> {
            this.writeString((String)className);
            this.writeHandle(((SchemaType.Ref)ref).get());
        });
    }

    Set<String> readNamespaces() {
        HashSet<String> result = new HashSet<String>();
        int size = this.readShort();
        for (int i = 0; i < size; ++i) {
            String ns = this.readString();
            result.add(ns);
        }
        return result;
    }

    void writeNamespaces(Set<String> namespaces) {
        this.writeShort(namespaces.size());
        namespaces.forEach(this::writeString);
    }

    void checkContainerNotNull(SchemaContainer container, QName name) {
        if (container == null) {
            throw new LinkageError("Loading of resource " + name + '.' + this._handle + "failed, information from " + name + ".index.xsb is  out of sync (or conflicting index files found)");
        }
    }

    public SchemaGlobalElement finishLoadingElement() {
        try {
            int particleType = this.readShort();
            if (particleType != 4) {
                throw new SchemaTypeLoaderException("Wrong particle type ", this.typeSystem.getName(), this._handle, 11);
            }
            int particleFlags = this.readShort();
            BigInteger minOccurs = this.readBigInteger();
            BigInteger maxOccurs = this.readBigInteger();
            QNameSet transitionRules = this.readQNameSet();
            QName name = this.readQName();
            SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
            this.checkContainerNotNull(container, name);
            SchemaGlobalElementImpl impl = new SchemaGlobalElementImpl(container);
            impl.setParticleType(particleType);
            impl.setMinOccurs(minOccurs);
            impl.setMaxOccurs(maxOccurs);
            impl.setTransitionRules(transitionRules, (particleFlags & 1) != 0);
            impl.setNameAndTypeRef(name, this.readTypeRef());
            impl.setDefault(this.readString(), (particleFlags & 4) != 0, null);
            if (this.atLeast(2, 16, 0)) {
                impl.setDefaultValue(this.readXmlValueObject());
            }
            impl.setNillable((particleFlags & 8) != 0);
            impl.setBlock((particleFlags & 0x10) != 0, (particleFlags & 0x20) != 0, (particleFlags & 0x40) != 0);
            impl.setWsdlArrayType(this.readSOAPArrayType());
            impl.setAbstract((particleFlags & 0x80) != 0);
            impl.setAnnotation(this.readAnnotation(container));
            impl.setFinal((particleFlags & 0x100) != 0, (particleFlags & 0x200) != 0);
            if (this.atLeast(2, 17, 0)) {
                impl.setSubstitutionGroup((SchemaGlobalElement.Ref)this.readHandle());
            }
            int substGroupCount = this.readShort();
            for (int i = 0; i < substGroupCount; ++i) {
                impl.addSubstitutionGroupMember(this.readQName());
            }
            SchemaIdentityConstraint.Ref[] idcs = new SchemaIdentityConstraint.Ref[this.readShort()];
            for (int i = 0; i < idcs.length; ++i) {
                idcs[i] = (SchemaIdentityConstraint.Ref)this.readHandle();
            }
            impl.setIdentityConstraints(idcs);
            impl.setFilename(this.readString());
            SchemaGlobalElementImpl schemaGlobalElementImpl = impl;
            return schemaGlobalElementImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), null, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    public SchemaGlobalAttribute finishLoadingAttribute() {
        try {
            QName name = this.readQName();
            SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
            this.checkContainerNotNull(container, name);
            SchemaGlobalAttributeImpl impl = new SchemaGlobalAttributeImpl(container);
            this.loadAttribute(impl, name, container);
            impl.setFilename(this.readString());
            SchemaGlobalAttributeImpl schemaGlobalAttributeImpl = impl;
            return schemaGlobalAttributeImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), this._handle, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    SchemaModelGroup finishLoadingModelGroup() {
        QName name = this.readQName();
        SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
        this.checkContainerNotNull(container, name);
        SchemaModelGroupImpl impl = new SchemaModelGroupImpl(container);
        try {
            impl.init(name, this.readString(), this.readShort() == 1, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 15, 0) && this.readShort() == 1, ((GroupDocument)GroupDocument.Factory.parse(this.readString())).getGroup(), this.readAnnotation(container), null);
            if (this.atLeast(2, 21, 0)) {
                impl.setFilename(this.readString());
            }
            SchemaModelGroupImpl schemaModelGroupImpl = impl;
            return schemaModelGroupImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), this._handle, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    SchemaIdentityConstraint finishLoadingIdentityConstraint() {
        try {
            QName name = this.readQName();
            SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
            this.checkContainerNotNull(container, name);
            SchemaIdentityConstraintImpl impl = new SchemaIdentityConstraintImpl(container);
            impl.setName(name);
            impl.setConstraintCategory(this.readShort());
            impl.setSelector(this.readString());
            impl.setAnnotation(this.readAnnotation(container));
            String[] fields = new String[this.readShort()];
            for (int i = 0; i < fields.length; ++i) {
                fields[i] = this.readString();
            }
            impl.setFields(fields);
            if (impl.getConstraintCategory() == 2) {
                impl.setReferencedKey((SchemaIdentityConstraint.Ref)this.readHandle());
            }
            int mapCount = this.readShort();
            HashMap<String, String> nsMappings = new HashMap<String, String>();
            for (int i = 0; i < mapCount; ++i) {
                String prefix = this.readString();
                String uri = this.readString();
                nsMappings.put(prefix, uri);
            }
            impl.setNSMap(nsMappings);
            if (this.atLeast(2, 21, 0)) {
                impl.setFilename(this.readString());
            }
            SchemaIdentityConstraintImpl schemaIdentityConstraintImpl = impl;
            return schemaIdentityConstraintImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), this._handle, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    SchemaAttributeGroup finishLoadingAttributeGroup() {
        QName name = this.readQName();
        SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
        this.checkContainerNotNull(container, name);
        SchemaAttributeGroupImpl impl = new SchemaAttributeGroupImpl(container);
        try {
            impl.init(name, this.readString(), this.readShort() == 1, this.atLeast(2, 22, 0) ? this.readString() : null, this.atLeast(2, 15, 0) && this.readShort() == 1, ((AttributeGroupDocument)AttributeGroupDocument.Factory.parse(this.readString())).getAttributeGroup(), this.readAnnotation(container), null);
            if (this.atLeast(2, 21, 0)) {
                impl.setFilename(this.readString());
            }
            SchemaAttributeGroupImpl schemaAttributeGroupImpl = impl;
            return schemaAttributeGroupImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), this._handle, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    public SchemaType finishLoadingType() {
        try {
            QName name;
            int i;
            SchemaContainer cNonNull = this.typeSystem.getContainerNonNull("");
            SchemaTypeImpl impl = new SchemaTypeImpl(cNonNull, true);
            impl.setName(this.readQName());
            impl.setOuterSchemaTypeRef(this.readTypeRef());
            impl.setBaseDepth(this.readShort());
            impl.setBaseTypeRef(this.readTypeRef());
            impl.setDerivationType(this.readShort());
            impl.setAnnotation(this.readAnnotation(null));
            switch (this.readShort()) {
                case 1: {
                    impl.setContainerFieldRef(this.readHandle());
                    break;
                }
                case 2: {
                    impl.setContainerFieldIndex((short)1, this.readShort());
                    break;
                }
                case 3: {
                    impl.setContainerFieldIndex((short)2, this.readShort());
                }
            }
            String jn = this.readString();
            impl.setFullJavaName(jn == null ? "" : jn);
            jn = this.readString();
            impl.setFullJavaImplName(jn == null ? "" : jn);
            impl.setAnonymousTypeRefs(this.readTypeRefArray());
            impl.setAnonymousUnionMemberOrdinal(this.readShort());
            int flags = this.readInt();
            boolean isComplexType = (flags & 1) == 0;
            impl.setCompiled((flags & 0x800) != 0);
            impl.setDocumentType((flags & 2) != 0);
            impl.setAttributeType((flags & 0x80000) != 0);
            impl.setSimpleType(!isComplexType);
            int complexVariety = 0;
            if (isComplexType) {
                impl.setAbstractFinal((flags & 0x40000) != 0, (flags & 0x4000) != 0, (flags & 0x8000) != 0, (flags & 0x20000) != 0, (flags & 0x10000) != 0);
                impl.setBlock((flags & 0x1000) != 0, (flags & 0x2000) != 0);
                impl.setOrderSensitive((flags & 0x200) != 0);
                complexVariety = this.readShort();
                impl.setComplexTypeVariety(complexVariety);
                if (this.atLeast(2, 23, 0)) {
                    impl.setContentBasedOnTypeRef(this.readTypeRef());
                }
                SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
                int attrCount = this.readShort();
                for (int i2 = 0; i2 < attrCount; ++i2) {
                    attrModel.addAttribute(this.readAttributeData());
                }
                attrModel.setWildcardSet(this.readQNameSet());
                attrModel.setWildcardProcess(this.readShort());
                LinkedHashMap<QName, SchemaProperty> attrProperties = new LinkedHashMap<QName, SchemaProperty>();
                int attrPropCount = this.readShort();
                for (int i3 = 0; i3 < attrPropCount; ++i3) {
                    SchemaProperty prop = this.readPropertyData();
                    if (!prop.isAttribute()) {
                        throw new SchemaTypeLoaderException("Attribute property " + i3 + " is not an attribute", this.typeSystem.getName(), this._handle, 6);
                    }
                    attrProperties.put(prop.getName(), prop);
                }
                SchemaParticle contentModel = null;
                LinkedHashMap<QName, SchemaProperty> elemProperties = null;
                int isAll = 0;
                if (complexVariety == 3 || complexVariety == 4) {
                    isAll = this.readShort();
                    SchemaParticle[] parts = this.readParticleArray();
                    if (parts.length == 1) {
                        contentModel = parts[0];
                    } else if (parts.length == 0) {
                        contentModel = null;
                    } else {
                        throw new SchemaTypeLoaderException("Content model not well-formed", this.typeSystem.getName(), this._handle, 7);
                    }
                    elemProperties = new LinkedHashMap<QName, SchemaProperty>();
                    int elemPropCount = this.readShort();
                    for (i = 0; i < elemPropCount; ++i) {
                        SchemaProperty prop = this.readPropertyData();
                        if (prop.isAttribute()) {
                            throw new SchemaTypeLoaderException("Element property " + i + " is not an element", this.typeSystem.getName(), this._handle, 6);
                        }
                        elemProperties.put(prop.getName(), prop);
                    }
                }
                impl.setContentModel(contentModel, attrModel, elemProperties, attrProperties, isAll == 1);
                StscComplexTypeResolver.WildcardResult wcElt = StscComplexTypeResolver.summarizeEltWildcards(contentModel);
                StscComplexTypeResolver.WildcardResult wcAttr = StscComplexTypeResolver.summarizeAttrWildcards(attrModel);
                impl.setWildcardSummary(wcElt.typedWildcards, wcElt.hasWildcards, wcAttr.typedWildcards, wcAttr.hasWildcards);
            }
            if (!isComplexType || complexVariety == 2) {
                boolean isStringEnum;
                int simpleVariety = this.readShort();
                impl.setSimpleTypeVariety(simpleVariety);
                boolean bl = isStringEnum = (flags & 0x40) != 0;
                impl.setOrdered((flags & 4) != 0 ? 0 : ((flags & 0x400) != 0 ? 2 : 1));
                impl.setBounded((flags & 8) != 0);
                impl.setFinite((flags & 0x10) != 0);
                impl.setNumeric((flags & 0x20) != 0);
                impl.setUnionOfLists((flags & 0x80) != 0);
                impl.setSimpleFinal((flags & 0x8000) != 0, (flags & 0x20000) != 0, (flags & 0x10000) != 0);
                XmlValueRef[] facets = new XmlValueRef[12];
                boolean[] fixedFacets = new boolean[12];
                int facetCount = this.readShort();
                for (int i4 = 0; i4 < facetCount; ++i4) {
                    int facetCode = this.readShort();
                    facets[facetCode] = this.readXmlValueObject();
                    fixedFacets[facetCode] = this.readShort() == 1;
                }
                impl.setBasicFacets(facets, fixedFacets);
                impl.setWhiteSpaceRule(this.readShort());
                impl.setPatternFacet((flags & 0x100) != 0);
                int patternCount = this.readShort();
                RegularExpression[] patterns = new RegularExpression[patternCount];
                for (int i5 = 0; i5 < patternCount; ++i5) {
                    patterns[i5] = new RegularExpression(this.readString(), "X");
                }
                impl.setPatterns(patterns);
                int enumCount = this.readShort();
                XmlValueRef[] enumValues = new XmlValueRef[enumCount];
                for (i = 0; i < enumCount; ++i) {
                    enumValues[i] = this.readXmlValueObject();
                }
                impl.setEnumerationValues(enumCount == 0 ? null : enumValues);
                impl.setBaseEnumTypeRef(this.readTypeRef());
                if (isStringEnum) {
                    int seCount = this.readUnsignedShortOrInt();
                    SchemaStringEnumEntry[] entries = new SchemaStringEnumEntry[seCount];
                    for (int i6 = 0; i6 < seCount; ++i6) {
                        entries[i6] = new SchemaStringEnumEntryImpl(this.readString(), this.readShort(), this.readString());
                    }
                    impl.setStringEnumEntries(entries);
                }
                switch (simpleVariety) {
                    case 1: {
                        impl.setPrimitiveTypeRef(this.readTypeRef());
                        impl.setDecimalSize(this.readInt());
                        break;
                    }
                    case 3: {
                        impl.setPrimitiveTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
                        impl.setListItemTypeRef(this.readTypeRef());
                        break;
                    }
                    case 2: {
                        impl.setPrimitiveTypeRef(BuiltinSchemaTypeSystem.ST_ANY_SIMPLE.getRef());
                        impl.setUnionMemberTypeRefs(this.readTypeRefArray());
                        break;
                    }
                    default: {
                        throw new SchemaTypeLoaderException("Simple type does not have a recognized variety", this.typeSystem.getName(), this._handle, 8);
                    }
                }
            }
            impl.setFilename(this.readString());
            if (impl.getName() != null) {
                SchemaContainer container = this.typeSystem.getContainer(impl.getName().getNamespaceURI());
                this.checkContainerNotNull(container, impl.getName());
                impl.setContainer(container);
            } else if (impl.isDocumentType()) {
                name = impl.getDocumentElementName();
                if (name != null) {
                    SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
                    this.checkContainerNotNull(container, name);
                    impl.setContainer(container);
                }
            } else if (impl.isAttributeType() && (name = impl.getAttributeTypeAttributeName()) != null) {
                SchemaContainer container = this.typeSystem.getContainer(name.getNamespaceURI());
                this.checkContainerNotNull(container, name);
                impl.setContainer(container);
            }
            SchemaTypeImpl schemaTypeImpl = impl;
            return schemaTypeImpl;
        }
        catch (SchemaTypeLoaderException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SchemaTypeLoaderException("Cannot load type from typesystem", this.typeSystem.getName(), this._handle, 14, e);
        }
        finally {
            this.readEnd();
        }
    }

    void writeTypeData(SchemaType type) {
        this.writeQName(type.getName());
        this.writeType(type.getOuterType());
        this.writeShort(((SchemaTypeImpl)type).getBaseDepth());
        this.writeType(type.getBaseType());
        this.writeShort(type.getDerivationType());
        this.writeAnnotation(type.getAnnotation());
        if (type.getContainerField() == null) {
            this.writeShort(0);
        } else if (type.getOuterType().isAttributeType() || type.getOuterType().isDocumentType()) {
            this.writeShort(1);
            this.writeHandle((SchemaComponent)((Object)type.getContainerField()));
        } else if (type.getContainerField().isAttribute()) {
            this.writeShort(2);
            this.writeShort(((SchemaTypeImpl)type.getOuterType()).getIndexForLocalAttribute((SchemaLocalAttribute)type.getContainerField()));
        } else {
            this.writeShort(3);
            this.writeShort(((SchemaTypeImpl)type.getOuterType()).getIndexForLocalElement((SchemaLocalElement)type.getContainerField()));
        }
        this.writeString(type.getFullJavaName());
        this.writeString(type.getFullJavaImplName());
        this.writeTypeArray(type.getAnonymousTypes());
        this.writeShort(type.getAnonymousUnionMemberOrdinal());
        int flags = 0;
        if (type.isSimpleType()) {
            flags |= 1;
        }
        if (type.isDocumentType()) {
            flags |= 2;
        }
        if (type.isAttributeType()) {
            flags |= 0x80000;
        }
        if (type.ordered() != 0) {
            flags |= 4;
        }
        if (type.ordered() == 2) {
            flags |= 0x400;
        }
        if (type.isBounded()) {
            flags |= 8;
        }
        if (type.isFinite()) {
            flags |= 0x10;
        }
        if (type.isNumeric()) {
            flags |= 0x20;
        }
        if (type.hasStringEnumValues()) {
            flags |= 0x40;
        }
        if (((SchemaTypeImpl)type).isUnionOfLists()) {
            flags |= 0x80;
        }
        if (type.hasPatternFacet()) {
            flags |= 0x100;
        }
        if (type.isOrderSensitive()) {
            flags |= 0x200;
        }
        if (type.blockExtension()) {
            flags |= 0x1000;
        }
        if (type.blockRestriction()) {
            flags |= 0x2000;
        }
        if (type.finalExtension()) {
            flags |= 0x4000;
        }
        if (type.finalRestriction()) {
            flags |= 0x4000;
        }
        if (type.finalList()) {
            flags |= 0x20000;
        }
        if (type.finalUnion()) {
            flags |= 0x10000;
        }
        if (type.isAbstract()) {
            flags |= 0x40000;
        }
        this.writeInt(flags);
        if (!type.isSimpleType()) {
            this.writeShort(type.getContentType());
            this.writeType(type.getContentBasedOnType());
            SchemaAttributeModel attrModel = type.getAttributeModel();
            SchemaLocalAttribute[] attrs = attrModel.getAttributes();
            this.writeShort(attrs.length);
            for (SchemaLocalAttribute attr : attrs) {
                this.writeAttributeData(attr);
            }
            this.writeQNameSet(attrModel.getWildcardSet());
            this.writeShort(attrModel.getWildcardProcess());
            SchemaProperty[] attrProperties = type.getAttributeProperties();
            this.writeShort(attrProperties.length);
            for (SchemaProperty attrProperty : attrProperties) {
                this.writePropertyData(attrProperty);
            }
            if (type.getContentType() == 3 || type.getContentType() == 4) {
                this.writeShort(type.hasAllContent() ? 1 : 0);
                SchemaParticle[] parts = type.getContentModel() != null ? new SchemaParticle[]{type.getContentModel()} : new SchemaParticle[]{};
                this.writeParticleArray(parts);
                SchemaProperty[] eltProperties = type.getElementProperties();
                this.writeShort(eltProperties.length);
                for (SchemaProperty eltProperty : eltProperties) {
                    this.writePropertyData(eltProperty);
                }
            }
        }
        if (type.isSimpleType() || type.getContentType() == 2) {
            int i;
            this.writeShort(type.getSimpleVariety());
            int facetCount = 0;
            for (i = 0; i <= 11; ++i) {
                if (type.getFacet(i) == null) continue;
                ++facetCount;
            }
            this.writeShort(facetCount);
            for (i = 0; i <= 11; ++i) {
                RegularExpression[] facet = type.getFacet(i);
                if (facet == null) continue;
                this.writeShort(i);
                this.writeXmlValueObject((XmlAnySimpleType)facet);
                this.writeShort(type.isFacetFixed(i) ? 1 : 0);
            }
            this.writeShort(type.getWhiteSpaceRule());
            RegularExpression[] patterns = ((SchemaTypeImpl)type).getPatternExpressions();
            this.writeShort(patterns.length);
            for (RegularExpression pattern : patterns) {
                this.writeString(pattern.getPattern());
            }
            XmlAnySimpleType[] enumValues = type.getEnumerationValues();
            if (enumValues == null) {
                this.writeShort(0);
            } else {
                this.writeShortOrInt(enumValues.length);
                for (XmlAnySimpleType enumValue : enumValues) {
                    this.writeXmlValueObject(enumValue);
                }
            }
            this.writeType(type.getBaseEnumType());
            if (type.hasStringEnumValues()) {
                SchemaStringEnumEntry[] entries = type.getStringEnumEntries();
                this.writeShort(entries.length);
                for (SchemaStringEnumEntry entry : entries) {
                    this.writeString(entry.getString());
                    this.writeShort(entry.getIntValue());
                    this.writeString(entry.getEnumName());
                }
            }
            switch (type.getSimpleVariety()) {
                case 1: {
                    this.writeType(type.getPrimitiveType());
                    this.writeInt(type.getDecimalSize());
                    break;
                }
                case 3: {
                    this.writeType(type.getListItemType());
                    break;
                }
                case 2: {
                    this.writeTypeArray(type.getUnionMemberTypes());
                }
            }
        }
        this.writeString(type.getSourceName());
    }

    SchemaLocalAttribute readAttributeData() {
        SchemaLocalAttributeImpl result = new SchemaLocalAttributeImpl();
        this.loadAttribute(result, this.readQName(), null);
        return result;
    }

    void loadAttribute(SchemaLocalAttributeImpl result, QName name, SchemaContainer container) {
        result.init(name, this.readTypeRef(), this.readShort(), this.readString(), null, this.atLeast(2, 16, 0) ? this.readXmlValueObject() : null, this.readShort() == 1, this.readSOAPArrayType(), this.readAnnotation(container), null);
    }

    void writeAttributeData(SchemaLocalAttribute attr) {
        this.writeQName(attr.getName());
        this.writeType(attr.getType());
        this.writeShort(attr.getUse());
        this.writeString(attr.getDefaultText());
        this.writeXmlValueObject(attr.getDefaultValue());
        this.writeShort(attr.isFixed() ? 1 : 0);
        this.writeSOAPArrayType(((SchemaWSDLArrayType)((Object)attr)).getWSDLArrayType());
        this.writeAnnotation(attr.getAnnotation());
    }

    void writeIdConstraintData(SchemaIdentityConstraint idc) {
        this.writeQName(idc.getName());
        this.writeShort(idc.getConstraintCategory());
        this.writeString(idc.getSelector());
        this.writeAnnotation(idc.getAnnotation());
        String[] fields = idc.getFields();
        this.writeShort(fields.length);
        for (String field : fields) {
            this.writeString(field);
        }
        if (idc.getConstraintCategory() == 2) {
            this.writeHandle(idc.getReferencedKey());
        }
        Map<String, String> mappings = idc.getNSMap();
        this.writeShort(mappings.size());
        mappings.forEach((prefix, uri) -> {
            this.writeString((String)prefix);
            this.writeString((String)uri);
        });
        this.writeString(idc.getSourceName());
    }

    SchemaParticle[] readParticleArray() {
        SchemaParticle[] result = new SchemaParticle[this.readShort()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.readParticleData();
        }
        return result;
    }

    void writeParticleArray(SchemaParticle[] spa) {
        this.writeShort(spa.length);
        for (SchemaParticle schemaParticle : spa) {
            this.writeParticleData(schemaParticle);
        }
    }

    SchemaParticle readParticleData() {
        int particleType = this.readShort();
        SchemaParticleImpl result = particleType != 4 ? new SchemaParticleImpl() : new SchemaLocalElementImpl();
        this.loadParticle(result, particleType);
        return result;
    }

    void loadParticle(SchemaParticleImpl result, int particleType) {
        int particleFlags = this.readShort();
        result.setParticleType(particleType);
        result.setMinOccurs(this.readBigInteger());
        result.setMaxOccurs(this.readBigInteger());
        result.setTransitionRules(this.readQNameSet(), (particleFlags & 1) != 0);
        switch (particleType) {
            case 5: {
                result.setWildcardSet(this.readQNameSet());
                result.setWildcardProcess(this.readShort());
                break;
            }
            case 4: {
                SchemaLocalElementImpl lresult = (SchemaLocalElementImpl)result;
                lresult.setNameAndTypeRef(this.readQName(), this.readTypeRef());
                lresult.setDefault(this.readString(), (particleFlags & 4) != 0, null);
                if (this.atLeast(2, 16, 0)) {
                    lresult.setDefaultValue(this.readXmlValueObject());
                }
                lresult.setNillable((particleFlags & 8) != 0);
                lresult.setBlock((particleFlags & 0x10) != 0, (particleFlags & 0x20) != 0, (particleFlags & 0x40) != 0);
                lresult.setWsdlArrayType(this.readSOAPArrayType());
                lresult.setAbstract((particleFlags & 0x80) != 0);
                lresult.setAnnotation(this.readAnnotation(null));
                SchemaIdentityConstraint.Ref[] idcs = new SchemaIdentityConstraint.Ref[this.readShort()];
                for (int i = 0; i < idcs.length; ++i) {
                    idcs[i] = (SchemaIdentityConstraint.Ref)this.readHandle();
                }
                lresult.setIdentityConstraints(idcs);
                break;
            }
            case 1: 
            case 2: 
            case 3: {
                result.setParticleChildren(this.readParticleArray());
                break;
            }
            default: {
                throw new SchemaTypeLoaderException("Unrecognized particle type ", this.typeSystem.getName(), this._handle, 11);
            }
        }
    }

    void writeParticleData(SchemaParticle part) {
        SchemaGlobalElement gpart;
        SchemaLocalElement lpart;
        this.writeShort(part.getParticleType());
        short flags = 0;
        if (part.isSkippable()) {
            flags = (short)(flags | 1);
        }
        if (part.getParticleType() == 4) {
            lpart = (SchemaLocalElement)((Object)part);
            if (lpart.isFixed()) {
                flags = (short)(flags | 4);
            }
            if (lpart.isNillable()) {
                flags = (short)(flags | 8);
            }
            if (lpart.blockExtension()) {
                flags = (short)(flags | 0x10);
            }
            if (lpart.blockRestriction()) {
                flags = (short)(flags | 0x20);
            }
            if (lpart.blockSubstitution()) {
                flags = (short)(flags | 0x40);
            }
            if (lpart.isAbstract()) {
                flags = (short)(flags | 0x80);
            }
            if (lpart instanceof SchemaGlobalElement) {
                gpart = (SchemaGlobalElement)lpart;
                if (gpart.finalExtension()) {
                    flags = (short)(flags | 0x100);
                }
                if (gpart.finalRestriction()) {
                    flags = (short)(flags | 0x200);
                }
            }
        }
        this.writeShort(flags);
        this.writeBigInteger(part.getMinOccurs());
        this.writeBigInteger(part.getMaxOccurs());
        this.writeQNameSet(part.acceptedStartNames());
        switch (part.getParticleType()) {
            case 5: {
                this.writeQNameSet(part.getWildcardSet());
                this.writeShort(part.getWildcardProcess());
                break;
            }
            case 4: {
                lpart = (SchemaLocalElement)((Object)part);
                this.writeQName(lpart.getName());
                this.writeType(lpart.getType());
                this.writeString(lpart.getDefaultText());
                this.writeXmlValueObject(lpart.getDefaultValue());
                this.writeSOAPArrayType(((SchemaWSDLArrayType)((Object)lpart)).getWSDLArrayType());
                this.writeAnnotation(lpart.getAnnotation());
                if (lpart instanceof SchemaGlobalElement) {
                    gpart = (SchemaGlobalElement)lpart;
                    this.writeHandle(gpart.substitutionGroup());
                    QName[] substGroupMembers = gpart.substitutionGroupMembers();
                    this.writeShort(substGroupMembers.length);
                    QName[] qNameArray = substGroupMembers;
                    int n = qNameArray.length;
                    for (int i = 0; i < n; ++i) {
                        QName substGroupMember = qNameArray[i];
                        this.writeQName(substGroupMember);
                    }
                }
                SchemaIdentityConstraint[] idcs = lpart.getIdentityConstraints();
                this.writeShort(idcs.length);
                for (SchemaIdentityConstraint idc : idcs) {
                    this.writeHandle(idc);
                }
                break;
            }
            case 1: 
            case 2: 
            case 3: {
                this.writeParticleArray(part.getParticleChildren());
                break;
            }
            default: {
                throw new SchemaTypeLoaderException("Unrecognized particle type ", this.typeSystem.getName(), this._handle, 11);
            }
        }
    }

    SchemaProperty readPropertyData() {
        SchemaPropertyImpl prop = new SchemaPropertyImpl();
        prop.setName(this.readQName());
        prop.setTypeRef(this.readTypeRef());
        int propflags = this.readShort();
        prop.setAttribute((propflags & 1) != 0);
        prop.setContainerTypeRef(this.readTypeRef());
        prop.setMinOccurs(this.readBigInteger());
        prop.setMaxOccurs(this.readBigInteger());
        prop.setNillable(this.readShort());
        prop.setDefault(this.readShort());
        prop.setFixed(this.readShort());
        prop.setDefaultText(this.readString());
        prop.setJavaPropertyName(this.readString());
        prop.setJavaTypeCode(this.readShort());
        prop.setExtendsJava(this.readTypeRef(), (propflags & 2) != 0, (propflags & 4) != 0, (propflags & 8) != 0);
        if (this.atMost(2, 19, 0)) {
            prop.setJavaSetterDelimiter(this.readQNameSet());
        }
        if (this.atLeast(2, 16, 0)) {
            prop.setDefaultValue(this.readXmlValueObject());
        }
        if (!prop.isAttribute() && this.atLeast(2, 17, 0)) {
            int size = this.readShort();
            LinkedHashSet<QName> qnames = new LinkedHashSet<QName>(size);
            for (int i = 0; i < size; ++i) {
                qnames.add(this.readQName());
            }
            prop.setAcceptedNames(qnames);
        }
        prop.setImmutable();
        return prop;
    }

    void writePropertyData(SchemaProperty prop) {
        this.writeQName(prop.getName());
        this.writeType(prop.getType());
        this.writeShort((prop.isAttribute() ? 1 : 0) | (prop.extendsJavaSingleton() ? 2 : 0) | (prop.extendsJavaOption() ? 4 : 0) | (prop.extendsJavaArray() ? 8 : 0));
        this.writeType(prop.getContainerType());
        this.writeBigInteger(prop.getMinOccurs());
        this.writeBigInteger(prop.getMaxOccurs());
        this.writeShort(prop.hasNillable());
        this.writeShort(prop.hasDefault());
        this.writeShort(prop.hasFixed());
        this.writeString(prop.getDefaultText());
        this.writeString(prop.getJavaPropertyName());
        this.writeShort(prop.getJavaTypeCode());
        this.writeType(prop.javaBasedOnType());
        this.writeXmlValueObject(prop.getDefaultValue());
        if (!prop.isAttribute()) {
            QName[] names = prop.acceptedNames();
            this.writeShort(names.length);
            for (QName name : names) {
                this.writeQName(name);
            }
        }
    }

    void writeModelGroupData(SchemaModelGroup grp) {
        SchemaModelGroupImpl impl = (SchemaModelGroupImpl)grp;
        this.writeQName(impl.getName());
        this.writeString(impl.getTargetNamespace());
        this.writeShort(impl.getChameleonNamespace() != null ? 1 : 0);
        this.writeString(impl.getElemFormDefault());
        this.writeString(impl.getAttFormDefault());
        this.writeShort(impl.isRedefinition() ? 1 : 0);
        this.writeString(impl.getParseObject().xmlText(new XmlOptions().setSaveOuter()));
        this.writeAnnotation(impl.getAnnotation());
        this.writeString(impl.getSourceName());
    }

    void writeAttributeGroupData(SchemaAttributeGroup grp) {
        SchemaAttributeGroupImpl impl = (SchemaAttributeGroupImpl)grp;
        this.writeQName(impl.getName());
        this.writeString(impl.getTargetNamespace());
        this.writeShort(impl.getChameleonNamespace() != null ? 1 : 0);
        this.writeString(impl.getFormDefault());
        this.writeShort(impl.isRedefinition() ? 1 : 0);
        this.writeString(impl.getParseObject().xmlText(new XmlOptions().setSaveOuter()));
        this.writeAnnotation(impl.getAnnotation());
        this.writeString(impl.getSourceName());
    }

    XmlValueRef readXmlValueObject() {
        SchemaType.Ref typeref = this.readTypeRef();
        if (typeref == null) {
            return null;
        }
        int btc = this.readShort();
        switch (btc) {
            default: {
                assert (false);
            }
            case 0: {
                return new XmlValueRef(typeref, null);
            }
            case 65535: {
                int size = this.readShort();
                ArrayList<XmlValueRef> values = new ArrayList<XmlValueRef>();
                this.writeShort(size);
                for (int i = 0; i < size; ++i) {
                    values.add(this.readXmlValueObject());
                }
                return new XmlValueRef(typeref, values);
            }
            case 2: 
            case 3: 
            case 6: 
            case 11: 
            case 12: 
            case 13: 
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: {
                return new XmlValueRef(typeref, this.readString());
            }
            case 4: 
            case 5: {
                return new XmlValueRef(typeref, this.readByteArray());
            }
            case 7: 
            case 8: {
                return new XmlValueRef(typeref, this.readQName());
            }
            case 9: 
            case 10: 
        }
        return new XmlValueRef(typeref, this.readDouble());
    }

    void writeXmlValueObject(XmlAnySimpleType value) {
        SchemaType type = value == null ? null : value.schemaType();
        this.writeType(type);
        if (type == null) {
            return;
        }
        SchemaType iType = ((SimpleValue)((Object)value)).instanceType();
        if (iType == null) {
            this.writeShort(0);
        } else if (iType.getSimpleVariety() == 3) {
            this.writeShort(-1);
            List<? extends XmlAnySimpleType> values = ((XmlObjectBase)((Object)value)).xgetListValue();
            this.writeShort(values.size());
            values.forEach(this::writeXmlValueObject);
        } else {
            int btc = iType.getPrimitiveType().getBuiltinTypeCode();
            this.writeShort(btc);
            switch (btc) {
                case 2: 
                case 3: 
                case 6: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: {
                    this.writeString(value.getStringValue());
                    break;
                }
                case 4: 
                case 5: {
                    this.writeByteArray(((SimpleValue)((Object)value)).getByteArrayValue());
                    break;
                }
                case 7: 
                case 8: {
                    this.writeQName(((SimpleValue)((Object)value)).getQNameValue());
                    break;
                }
                case 9: {
                    this.writeDouble(((SimpleValue)((Object)value)).getFloatValue());
                    break;
                }
                case 10: {
                    this.writeDouble(((SimpleValue)((Object)value)).getDoubleValue());
                }
            }
        }
    }

    double readDouble() {
        try {
            return this._input.readDouble();
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    void writeDouble(double d) {
        if (this._output != null) {
            try {
                this._output.writeDouble(d);
            }
            catch (IOException e) {
                throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
            }
        }
    }

    QNameSet readQNameSet() {
        int flag = this.readShort();
        HashSet<String> uriSet = new HashSet<String>();
        int uriCount = this.readShort();
        for (int i = 0; i < uriCount; ++i) {
            uriSet.add(this.readString());
        }
        HashSet<QName> qnameSet1 = new HashSet<QName>();
        int qncount1 = this.readShort();
        for (int i = 0; i < qncount1; ++i) {
            qnameSet1.add(this.readQName());
        }
        HashSet<QName> qnameSet2 = new HashSet<QName>();
        int qncount2 = this.readShort();
        for (int i = 0; i < qncount2; ++i) {
            qnameSet2.add(this.readQName());
        }
        if (flag == 1) {
            return QNameSet.forSets(uriSet, null, qnameSet1, qnameSet2);
        }
        return QNameSet.forSets(null, uriSet, qnameSet2, qnameSet1);
    }

    void writeQNameSet(QNameSet set) {
        Set<String> uriSet;
        boolean invert = set.excludedURIs() != null;
        this.writeShort(invert ? 1 : 0);
        Set<String> set2 = uriSet = invert ? set.excludedURIs() : set.includedURIs();
        assert (uriSet != null);
        this.writeShort(uriSet.size());
        uriSet.forEach(this::writeString);
        Set<QName> qnameSet1 = invert ? set.excludedQNamesInIncludedURIs() : set.includedQNamesInExcludedURIs();
        this.writeShort(qnameSet1.size());
        qnameSet1.forEach(this::writeQName);
        Set<QName> qnameSet2 = invert ? set.includedQNamesInExcludedURIs() : set.excludedQNamesInIncludedURIs();
        this.writeShort(qnameSet2.size());
        qnameSet2.forEach(this::writeQName);
    }

    byte[] readByteArray() {
        try {
            short len = this._input.readShort();
            byte[] result = new byte[len];
            this._input.readFully(result);
            return result;
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    void writeByteArray(byte[] ba) {
        try {
            this.writeShort(ba.length);
            if (this._output != null) {
                this._output.write(ba);
            }
        }
        catch (IOException e) {
            throw new SchemaTypeLoaderException(e.getMessage(), this.typeSystem.getName(), this._handle, 9, e);
        }
    }

    BigInteger readBigInteger() {
        byte[] result = this.readByteArray();
        if (result.length == 0) {
            return null;
        }
        if (result.length == 1 && result[0] == 0) {
            return BigInteger.ZERO;
        }
        if (result.length == 1 && result[0] == 1) {
            return BigInteger.ONE;
        }
        return new BigInteger(result);
    }

    void writeBigInteger(BigInteger bi) {
        if (bi == null) {
            this.writeShort(0);
        } else if (bi.signum() == 0) {
            this.writeByteArray(SchemaTypeSystemImpl.SINGLE_ZERO_BYTE);
        } else {
            this.writeByteArray(bi.toByteArray());
        }
    }
}

