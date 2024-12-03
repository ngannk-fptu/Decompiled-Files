/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoaderException;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.XsbReader;

class SchemaTypePool {
    private final SchemaTypeSystemImpl typeSystem;
    private final Map<String, SchemaComponent.Ref> _handlesToRefs = new LinkedHashMap<String, SchemaComponent.Ref>();
    private final Map<SchemaComponent, String> _componentsToHandles = new LinkedHashMap<SchemaComponent, String>();
    private boolean _started;

    SchemaTypePool(SchemaTypeSystemImpl typeSystem) {
        this.typeSystem = typeSystem;
    }

    private String addUniqueHandle(SchemaComponent obj, String base) {
        String handle = base = base.toLowerCase(Locale.ROOT);
        int index = 2;
        while (this._handlesToRefs.containsKey(handle)) {
            handle = base + index;
            ++index;
        }
        this._handlesToRefs.put(handle, obj.getComponentRef());
        this._componentsToHandles.put(obj, handle);
        return handle;
    }

    String handleForComponent(SchemaComponent comp) {
        if (comp == null) {
            return null;
        }
        if (comp.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        if (comp instanceof SchemaType) {
            return this.handleForType((SchemaType)comp);
        }
        if (comp instanceof SchemaGlobalElement) {
            return this.handleForElement((SchemaGlobalElement)comp);
        }
        if (comp instanceof SchemaGlobalAttribute) {
            return this.handleForAttribute((SchemaGlobalAttribute)comp);
        }
        if (comp instanceof SchemaModelGroup) {
            return this.handleForModelGroup((SchemaModelGroup)comp);
        }
        if (comp instanceof SchemaAttributeGroup) {
            return this.handleForAttributeGroup((SchemaAttributeGroup)comp);
        }
        if (comp instanceof SchemaIdentityConstraint) {
            return this.handleForIdentityConstraint((SchemaIdentityConstraint)comp);
        }
        throw new IllegalStateException("Component type cannot have a handle");
    }

    String handleForElement(SchemaGlobalElement element) {
        if (element == null) {
            return null;
        }
        if (element.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(element);
        if (handle == null) {
            handle = this.addUniqueHandle(element, NameUtil.upperCamelCase(element.getName().getLocalPart()) + "Element");
        }
        return handle;
    }

    String handleForAttribute(SchemaGlobalAttribute attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(attribute);
        if (handle == null) {
            handle = this.addUniqueHandle(attribute, NameUtil.upperCamelCase(attribute.getName().getLocalPart()) + "Attribute");
        }
        return handle;
    }

    String handleForModelGroup(SchemaModelGroup group) {
        if (group == null) {
            return null;
        }
        if (group.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(group);
        if (handle == null) {
            handle = this.addUniqueHandle(group, NameUtil.upperCamelCase(group.getName().getLocalPart()) + "ModelGroup");
        }
        return handle;
    }

    String handleForAttributeGroup(SchemaAttributeGroup group) {
        if (group == null) {
            return null;
        }
        if (group.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(group);
        if (handle == null) {
            handle = this.addUniqueHandle(group, NameUtil.upperCamelCase(group.getName().getLocalPart()) + "AttributeGroup");
        }
        return handle;
    }

    String handleForIdentityConstraint(SchemaIdentityConstraint idc) {
        if (idc == null) {
            return null;
        }
        if (idc.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(idc);
        if (handle == null) {
            handle = this.addUniqueHandle(idc, NameUtil.upperCamelCase(idc.getName().getLocalPart()) + "IdentityConstraint");
        }
        return handle;
    }

    String handleForType(SchemaType type) {
        if (type == null) {
            return null;
        }
        if (type.getTypeSystem() != this.typeSystem) {
            throw new IllegalArgumentException("Cannot supply handles for types from another type system");
        }
        String handle = this._componentsToHandles.get(type);
        if (handle == null) {
            QName name = type.getName();
            String suffix = "";
            if (name == null) {
                if (type.isDocumentType()) {
                    name = type.getDocumentElementName();
                    suffix = "Doc";
                } else if (type.isAttributeType()) {
                    name = type.getAttributeTypeAttributeName();
                    suffix = "AttrType";
                } else if (type.getContainerField() != null) {
                    name = type.getContainerField().getName();
                    suffix = type.getContainerField().isAttribute() ? "Attr" : "Elem";
                }
            }
            String uniq = Integer.toHexString(type.toString().hashCode() | Integer.MIN_VALUE).substring(4).toUpperCase(Locale.ROOT);
            String baseName = name == null ? "Anon" + uniq + "Type" : NameUtil.upperCamelCase(name.getLocalPart()) + uniq + suffix + "Type";
            handle = this.addUniqueHandle(type, baseName);
        }
        return handle;
    }

    SchemaComponent.Ref refForHandle(String handle) {
        if (handle == null) {
            return null;
        }
        return this._handlesToRefs.get(handle);
    }

    void startWriteMode() {
        this._started = true;
        this._componentsToHandles.clear();
        for (String handle : this._handlesToRefs.keySet()) {
            SchemaComponent comp = this._handlesToRefs.get(handle).getComponent();
            this._componentsToHandles.put(comp, handle);
        }
    }

    void writeHandlePool(XsbReader reader) {
        reader.writeShort(this._componentsToHandles.size());
        this._componentsToHandles.forEach((comp, handle) -> {
            reader.writeString((String)handle);
            reader.writeShort(this.fileTypeFromComponentType(comp.getComponentType()));
        });
    }

    int fileTypeFromComponentType(int componentType) {
        switch (componentType) {
            case 0: {
                return 2;
            }
            case 1: {
                return 3;
            }
            case 3: {
                return 4;
            }
            case 6: {
                return 6;
            }
            case 4: {
                return 7;
            }
            case 5: {
                return 8;
            }
        }
        throw new IllegalStateException("Unexpected component type");
    }

    void readHandlePool(XsbReader reader) {
        if (this._handlesToRefs.size() != 0 || this._started) {
            throw new IllegalStateException("Nonempty handle set before read");
        }
        int size = reader.readShort();
        for (int i = 0; i < size; ++i) {
            SchemaComponent.Ref result;
            String handle = reader.readString();
            int code = reader.readShort();
            switch (code) {
                case 2: {
                    result = new SchemaType.Ref(this.typeSystem, handle);
                    break;
                }
                case 3: {
                    result = new SchemaGlobalElement.Ref(this.typeSystem, handle);
                    break;
                }
                case 4: {
                    result = new SchemaGlobalAttribute.Ref(this.typeSystem, handle);
                    break;
                }
                case 6: {
                    result = new SchemaModelGroup.Ref(this.typeSystem, handle);
                    break;
                }
                case 7: {
                    result = new SchemaAttributeGroup.Ref(this.typeSystem, handle);
                    break;
                }
                case 8: {
                    result = new SchemaIdentityConstraint.Ref(this.typeSystem, handle);
                    break;
                }
                default: {
                    throw new SchemaTypeLoaderException("Schema index has an unrecognized entry of type " + code, this.typeSystem.getName(), handle, 5);
                }
            }
            this._handlesToRefs.put(handle, result);
        }
    }
}

