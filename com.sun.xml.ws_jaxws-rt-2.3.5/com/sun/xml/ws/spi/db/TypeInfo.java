/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElementWrapper
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.ws.spi.db.Utils;
import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.namespace.QName;

public final class TypeInfo {
    public final QName tagName;
    public Type type;
    public final Annotation[] annotations;
    private Map<String, Object> properties = new HashMap<String, Object>();
    private boolean isGlobalElement = true;
    private TypeInfo parentCollectionType;
    private TypeInfo wrapperType;
    private Type genericType;
    private boolean nillable = true;

    public TypeInfo(QName tagName, Type type, Annotation ... annotations) {
        if (tagName == null || type == null || annotations == null) {
            String nullArgs = "";
            if (tagName == null) {
                nullArgs = "tagName";
            }
            if (type == null) {
                nullArgs = nullArgs + (nullArgs.length() > 0 ? ", type" : "type");
            }
            if (annotations == null) {
                nullArgs = nullArgs + (nullArgs.length() > 0 ? ", annotations" : "annotations");
            }
            throw new IllegalArgumentException("Argument(s) \"" + nullArgs + "\" can''t be null.)");
        }
        this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
        this.type = type;
        if (type instanceof Class && ((Class)type).isPrimitive()) {
            this.nillable = false;
        }
        this.annotations = annotations;
    }

    public <A extends Annotation> A get(Class<A> annotationType) {
        for (Annotation a : this.annotations) {
            if (a.annotationType() != annotationType) continue;
            return (A)((Annotation)annotationType.cast(a));
        }
        return null;
    }

    public TypeInfo toItemType() {
        Type t = this.genericType != null ? this.genericType : this.type;
        Type base = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass((Object)t, Collection.class);
        if (base == null) {
            return this;
        }
        return new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument((Object)base, 0), new Annotation[0]);
    }

    public Map<String, Object> properties() {
        return this.properties;
    }

    public boolean isGlobalElement() {
        return this.isGlobalElement;
    }

    public void setGlobalElement(boolean isGlobalElement) {
        this.isGlobalElement = isGlobalElement;
    }

    public TypeInfo getParentCollectionType() {
        return this.parentCollectionType;
    }

    public void setParentCollectionType(TypeInfo parentCollectionType) {
        this.parentCollectionType = parentCollectionType;
    }

    public boolean isRepeatedElement() {
        return this.parentCollectionType != null;
    }

    public Type getGenericType() {
        return this.genericType;
    }

    public void setGenericType(Type genericType) {
        this.genericType = genericType;
    }

    public boolean isNillable() {
        return this.nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public String toString() {
        return "TypeInfo: Type = " + this.type + ", tag = " + this.tagName;
    }

    public TypeInfo getItemType() {
        if (this.type instanceof Class && ((Class)this.type).isArray() && !byte[].class.equals((Object)this.type)) {
            Type componentType = ((Class)this.type).getComponentType();
            Type genericComponentType = null;
            if (this.genericType != null && this.genericType instanceof GenericArrayType) {
                GenericArrayType arrayType = (GenericArrayType)this.type;
                genericComponentType = arrayType.getGenericComponentType();
                componentType = arrayType.getGenericComponentType();
            }
            TypeInfo ti = new TypeInfo(this.tagName, componentType, this.annotations);
            if (genericComponentType != null) {
                ti.setGenericType(genericComponentType);
            }
            for (Annotation anno : this.annotations) {
                if (!(anno instanceof XmlElementWrapper)) continue;
                ti.wrapperType = this;
            }
            return ti;
        }
        Type t = this.genericType != null ? this.genericType : this.type;
        Type base = (Type)Utils.REFLECTION_NAVIGATOR.getBaseClass((Object)t, Collection.class);
        if (base != null) {
            return new TypeInfo(this.tagName, (Type)Utils.REFLECTION_NAVIGATOR.getTypeArgument((Object)base, 0), this.annotations);
        }
        return null;
    }

    public TypeInfo getWrapperType() {
        return this.wrapperType;
    }
}

