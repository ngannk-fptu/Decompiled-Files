/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;

class SchemaContainer {
    private final String _namespace;
    private SchemaTypeSystem _typeSystem;
    boolean _immutable;
    private final List<SchemaGlobalElement.Ref> _globalElements = new ArrayList<SchemaGlobalElement.Ref>();
    private final List<SchemaGlobalAttribute.Ref> _globalAttributes = new ArrayList<SchemaGlobalAttribute.Ref>();
    private final List<SchemaModelGroup.Ref> _modelGroups = new ArrayList<SchemaModelGroup.Ref>();
    private final List<SchemaModelGroup.Ref> _redefinedModelGroups = new ArrayList<SchemaModelGroup.Ref>();
    private final List<SchemaAttributeGroup.Ref> _attributeGroups = new ArrayList<SchemaAttributeGroup.Ref>();
    private final List<SchemaAttributeGroup.Ref> _redefinedAttributeGroups = new ArrayList<SchemaAttributeGroup.Ref>();
    private final List<SchemaType.Ref> _globalTypes = new ArrayList<SchemaType.Ref>();
    private final List<SchemaType.Ref> _redefinedGlobalTypes = new ArrayList<SchemaType.Ref>();
    private final List<SchemaType.Ref> _documentTypes = new ArrayList<SchemaType.Ref>();
    private final List<SchemaType.Ref> _attributeTypes = new ArrayList<SchemaType.Ref>();
    private final List<SchemaIdentityConstraint.Ref> _identityConstraints = new ArrayList<SchemaIdentityConstraint.Ref>();
    private final List<SchemaAnnotation> _annotations = new ArrayList<SchemaAnnotation>();

    SchemaContainer(String namespace) {
        this._namespace = namespace;
    }

    String getNamespace() {
        return this._namespace;
    }

    synchronized SchemaTypeSystem getTypeSystem() {
        return this._typeSystem;
    }

    synchronized void setTypeSystem(SchemaTypeSystem typeSystem) {
        this._typeSystem = typeSystem;
    }

    synchronized void setImmutable() {
        this._immutable = true;
    }

    synchronized void unsetImmutable() {
        this._immutable = false;
    }

    private void check_immutable() {
        if (this._immutable) {
            throw new IllegalStateException("Cannot add components to immutable SchemaContainer");
        }
    }

    void addGlobalElement(SchemaGlobalElement.Ref e) {
        this.check_immutable();
        this._globalElements.add(e);
    }

    List<SchemaGlobalElement> globalElements() {
        return this._globalElements.stream().map(SchemaGlobalElement.Ref::get).collect(Collectors.toList());
    }

    void addGlobalAttribute(SchemaGlobalAttribute.Ref a) {
        this.check_immutable();
        this._globalAttributes.add(a);
    }

    List<SchemaGlobalAttribute> globalAttributes() {
        return this._globalAttributes.stream().map(SchemaGlobalAttribute.Ref::get).collect(Collectors.toList());
    }

    void addModelGroup(SchemaModelGroup.Ref g) {
        this.check_immutable();
        this._modelGroups.add(g);
    }

    List<SchemaModelGroup> modelGroups() {
        return this._modelGroups.stream().map(SchemaModelGroup.Ref::get).collect(Collectors.toList());
    }

    void addRedefinedModelGroup(SchemaModelGroup.Ref g) {
        this.check_immutable();
        this._redefinedModelGroups.add(g);
    }

    List<SchemaModelGroup> redefinedModelGroups() {
        return this._redefinedModelGroups.stream().map(SchemaModelGroup.Ref::get).collect(Collectors.toList());
    }

    void addAttributeGroup(SchemaAttributeGroup.Ref g) {
        this.check_immutable();
        this._attributeGroups.add(g);
    }

    List<SchemaAttributeGroup> attributeGroups() {
        return this._attributeGroups.stream().map(SchemaAttributeGroup.Ref::get).collect(Collectors.toList());
    }

    void addRedefinedAttributeGroup(SchemaAttributeGroup.Ref g) {
        this.check_immutable();
        this._redefinedAttributeGroups.add(g);
    }

    List<SchemaAttributeGroup> redefinedAttributeGroups() {
        return this._redefinedAttributeGroups.stream().map(SchemaAttributeGroup.Ref::get).collect(Collectors.toList());
    }

    void addGlobalType(SchemaType.Ref t) {
        this.check_immutable();
        this._globalTypes.add(t);
    }

    List<SchemaType> globalTypes() {
        return this._globalTypes.stream().map(SchemaType.Ref::get).collect(Collectors.toList());
    }

    void addRedefinedType(SchemaType.Ref t) {
        this.check_immutable();
        this._redefinedGlobalTypes.add(t);
    }

    List<SchemaType> redefinedGlobalTypes() {
        return this._redefinedGlobalTypes.stream().map(SchemaType.Ref::get).collect(Collectors.toList());
    }

    void addDocumentType(SchemaType.Ref t) {
        this.check_immutable();
        this._documentTypes.add(t);
    }

    List<SchemaType> documentTypes() {
        return this._documentTypes.stream().map(SchemaType.Ref::get).collect(Collectors.toList());
    }

    void addAttributeType(SchemaType.Ref t) {
        this.check_immutable();
        this._attributeTypes.add(t);
    }

    List<SchemaType> attributeTypes() {
        return this._attributeTypes.stream().map(SchemaType.Ref::get).collect(Collectors.toList());
    }

    void addIdentityConstraint(SchemaIdentityConstraint.Ref c) {
        this.check_immutable();
        this._identityConstraints.add(c);
    }

    List<SchemaIdentityConstraint> identityConstraints() {
        return this._identityConstraints.stream().map(SchemaIdentityConstraint.Ref::get).collect(Collectors.toList());
    }

    void addAnnotation(SchemaAnnotation a) {
        this.check_immutable();
        this._annotations.add(a);
    }

    List<SchemaAnnotation> annotations() {
        return Collections.unmodifiableList(this._annotations);
    }
}

