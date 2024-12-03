/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.description;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.cache.MethodCache;

public class TypeDesc
implements Serializable {
    public static final Class[] noClasses = new Class[0];
    public static final Object[] noObjects = new Object[0];
    private static Map classMap = new Hashtable();
    private boolean lookedForAny = false;
    private boolean canSearchParents = true;
    private boolean hasSearchedParents = false;
    private TypeDesc parentDesc = null;
    private Class javaClass = null;
    private QName xmlType = null;
    private FieldDesc[] fields;
    private HashMap fieldNameMap = new HashMap();
    private HashMap fieldElementMap = null;
    private boolean _hasAttributes = false;
    private BeanPropertyDescriptor[] propertyDescriptors = null;
    private Map propertyMap = null;
    private BeanPropertyDescriptor anyDesc = null;

    public TypeDesc(Class javaClass) {
        this(javaClass, true);
    }

    public TypeDesc(Class javaClass, boolean canSearchParents) {
        this.javaClass = javaClass;
        this.canSearchParents = canSearchParents;
        Class cls = javaClass.getSuperclass();
        if (cls != null && !cls.getName().startsWith("java.")) {
            this.parentDesc = TypeDesc.getTypeDescForClass(cls);
        }
    }

    public static void registerTypeDescForClass(Class cls, TypeDesc td) {
        classMap.put(cls, td);
    }

    public static TypeDesc getTypeDescForClass(Class cls) {
        TypeDesc result = (TypeDesc)classMap.get(cls);
        if (result == null) {
            try {
                Method getTypeDesc = MethodCache.getInstance().getMethod(cls, "getTypeDesc", noClasses);
                if (getTypeDesc != null && (result = (TypeDesc)getTypeDesc.invoke(null, noObjects)) != null) {
                    classMap.put(cls, result);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return result;
    }

    public BeanPropertyDescriptor getAnyDesc() {
        return this.anyDesc;
    }

    public FieldDesc[] getFields() {
        return this.fields;
    }

    public FieldDesc[] getFields(boolean searchParents) {
        if (this.canSearchParents && searchParents && !this.hasSearchedParents) {
            FieldDesc[] parentFields;
            if (this.parentDesc != null && (parentFields = this.parentDesc.getFields(true)) != null) {
                if (this.fields != null) {
                    FieldDesc[] ret = new FieldDesc[parentFields.length + this.fields.length];
                    System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
                    System.arraycopy(this.fields, 0, ret, parentFields.length, this.fields.length);
                    this.fields = ret;
                } else {
                    FieldDesc[] ret = new FieldDesc[parentFields.length];
                    System.arraycopy(parentFields, 0, ret, 0, parentFields.length);
                    this.fields = ret;
                }
            }
            this.hasSearchedParents = true;
        }
        return this.fields;
    }

    public void setFields(FieldDesc[] newFields) {
        this.fieldNameMap = new HashMap();
        this.fields = newFields;
        this._hasAttributes = false;
        this.fieldElementMap = null;
        for (int i = 0; i < newFields.length; ++i) {
            FieldDesc field = newFields[i];
            if (!field.isElement()) {
                this._hasAttributes = true;
            }
            this.fieldNameMap.put(field.getFieldName(), field);
        }
    }

    public void addFieldDesc(FieldDesc field) {
        if (field == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullFieldDesc"));
        }
        int numFields = 0;
        if (this.fields != null) {
            numFields = this.fields.length;
        }
        FieldDesc[] newFields = new FieldDesc[numFields + 1];
        if (this.fields != null) {
            System.arraycopy(this.fields, 0, newFields, 0, numFields);
        }
        newFields[numFields] = field;
        this.fields = newFields;
        this.fieldNameMap.put(field.getFieldName(), field);
        if (!this._hasAttributes && !field.isElement()) {
            this._hasAttributes = true;
        }
    }

    public QName getElementNameForField(String fieldName) {
        FieldDesc desc = (FieldDesc)this.fieldNameMap.get(fieldName);
        if (desc == null) {
            if (this.canSearchParents && this.parentDesc != null) {
                return this.parentDesc.getElementNameForField(fieldName);
            }
        } else if (desc.isElement()) {
            return desc.getXmlName();
        }
        return null;
    }

    public QName getAttributeNameForField(String fieldName) {
        FieldDesc desc = (FieldDesc)this.fieldNameMap.get(fieldName);
        if (desc == null) {
            if (this.canSearchParents && this.parentDesc != null) {
                return this.parentDesc.getAttributeNameForField(fieldName);
            }
        } else if (!desc.isElement()) {
            QName ret = desc.getXmlName();
            if (ret == null) {
                ret = new QName("", fieldName);
            }
            return ret;
        }
        return null;
    }

    public String getFieldNameForElement(QName qname, boolean ignoreNS) {
        String cached;
        if (this.fieldElementMap != null && (cached = (String)this.fieldElementMap.get(qname)) != null) {
            return cached;
        }
        String result = null;
        String localPart = qname.getLocalPart();
        for (int i = 0; this.fields != null && i < this.fields.length; ++i) {
            QName xmlName;
            FieldDesc field = this.fields[i];
            if (!field.isElement() || !localPart.equals((xmlName = field.getXmlName()).getLocalPart()) || !ignoreNS && !qname.getNamespaceURI().equals(xmlName.getNamespaceURI())) continue;
            result = field.getFieldName();
            break;
        }
        if (result == null && this.canSearchParents && this.parentDesc != null) {
            result = this.parentDesc.getFieldNameForElement(qname, ignoreNS);
        }
        if (result != null) {
            if (this.fieldElementMap == null) {
                this.fieldElementMap = new HashMap();
            }
            this.fieldElementMap.put(qname, result);
        }
        return result;
    }

    public String getFieldNameForAttribute(QName qname) {
        String possibleMatch = null;
        for (int i = 0; this.fields != null && i < this.fields.length; ++i) {
            FieldDesc field = this.fields[i];
            if (field.isElement()) continue;
            if (qname.equals(field.getXmlName())) {
                return field.getFieldName();
            }
            if (!qname.getNamespaceURI().equals("") || !qname.getLocalPart().equals(field.getFieldName())) continue;
            possibleMatch = field.getFieldName();
        }
        if (possibleMatch == null && this.canSearchParents && this.parentDesc != null) {
            possibleMatch = this.parentDesc.getFieldNameForAttribute(qname);
        }
        return possibleMatch;
    }

    public FieldDesc getFieldByName(String name) {
        FieldDesc ret = (FieldDesc)this.fieldNameMap.get(name);
        if (ret == null && this.canSearchParents && this.parentDesc != null) {
            ret = this.parentDesc.getFieldByName(name);
        }
        return ret;
    }

    public boolean hasAttributes() {
        if (this._hasAttributes) {
            return true;
        }
        if (this.canSearchParents && this.parentDesc != null) {
            return this.parentDesc.hasAttributes();
        }
        return false;
    }

    public QName getXmlType() {
        return this.xmlType;
    }

    public void setXmlType(QName xmlType) {
        this.xmlType = xmlType;
    }

    public BeanPropertyDescriptor[] getPropertyDescriptors() {
        if (this.propertyDescriptors == null) {
            this.makePropertyDescriptors();
        }
        return this.propertyDescriptors;
    }

    private synchronized void makePropertyDescriptors() {
        if (this.propertyDescriptors != null) {
            return;
        }
        this.propertyDescriptors = BeanUtils.getPd(this.javaClass, this);
        if (!this.lookedForAny) {
            this.anyDesc = BeanUtils.getAnyContentPD(this.javaClass);
            this.lookedForAny = true;
        }
    }

    public BeanPropertyDescriptor getAnyContentDescriptor() {
        if (!this.lookedForAny) {
            this.anyDesc = BeanUtils.getAnyContentPD(this.javaClass);
            this.lookedForAny = true;
        }
        return this.anyDesc;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map getPropertyDescriptorMap() {
        TypeDesc typeDesc = this;
        synchronized (typeDesc) {
            if (this.propertyMap != null) {
                return this.propertyMap;
            }
            if (this.propertyDescriptors == null) {
                this.getPropertyDescriptors();
            }
            this.propertyMap = new HashMap();
            for (int i = 0; i < this.propertyDescriptors.length; ++i) {
                BeanPropertyDescriptor descriptor = this.propertyDescriptors[i];
                this.propertyMap.put(descriptor.getName(), descriptor);
            }
        }
        return this.propertyMap;
    }
}

