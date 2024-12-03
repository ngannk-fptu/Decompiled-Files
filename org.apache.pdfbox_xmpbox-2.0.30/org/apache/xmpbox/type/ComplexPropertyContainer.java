/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.ArrayList;
import java.util.List;
import org.apache.xmpbox.type.AbstractField;

public class ComplexPropertyContainer {
    private final List<AbstractField> properties = new ArrayList<AbstractField>();

    protected AbstractField getFirstEquivalentProperty(String localName, Class<? extends AbstractField> type) {
        List<AbstractField> list = this.getPropertiesByLocalName(localName);
        if (list != null) {
            for (AbstractField abstractField : list) {
                if (!abstractField.getClass().equals(type)) continue;
                return abstractField;
            }
        }
        return null;
    }

    public void addProperty(AbstractField obj) {
        this.removeProperty(obj);
        this.properties.add(obj);
    }

    public List<AbstractField> getAllProperties() {
        return this.properties;
    }

    public List<AbstractField> getPropertiesByLocalName(String localName) {
        List<AbstractField> absFields = this.getAllProperties();
        if (absFields != null) {
            ArrayList<AbstractField> list = new ArrayList<AbstractField>();
            for (AbstractField abstractField : absFields) {
                if (!abstractField.getPropertyName().equals(localName)) continue;
                list.add(abstractField);
            }
            if (list.isEmpty()) {
                return null;
            }
            return list;
        }
        return null;
    }

    public boolean isSameProperty(AbstractField prop1, AbstractField prop2) {
        if (prop1.getClass().equals(prop2.getClass())) {
            String pn1 = prop1.getPropertyName();
            String pn2 = prop2.getPropertyName();
            if (pn1 == null) {
                return pn2 == null;
            }
            if (pn1.equals(pn2)) {
                return prop1.equals(prop2);
            }
        }
        return false;
    }

    public boolean containsProperty(AbstractField property) {
        for (AbstractField tmp : this.getAllProperties()) {
            if (!this.isSameProperty(tmp, property)) continue;
            return true;
        }
        return false;
    }

    public void removeProperty(AbstractField property) {
        this.properties.remove(property);
    }

    public void removePropertiesByName(String localName) {
        if (this.properties.isEmpty()) {
            return;
        }
        List<AbstractField> propList = this.getPropertiesByLocalName(localName);
        if (propList == null) {
            return;
        }
        for (AbstractField field : propList) {
            this.properties.remove(field);
        }
    }
}

