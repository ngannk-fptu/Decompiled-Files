/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassBasedModelFactory;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.template.TemplateModel;
import java.util.LinkedHashMap;

public class _EnumModels
extends ClassBasedModelFactory {
    public _EnumModels(BeansWrapper wrapper) {
        super(wrapper);
    }

    protected TemplateModel createModel(Class clazz) {
        T[] obj = clazz.getEnumConstants();
        if (obj == null) {
            return null;
        }
        LinkedHashMap<String, Enum> map = new LinkedHashMap<String, Enum>();
        for (int i = 0; i < obj.length; ++i) {
            Enum value = (Enum)obj[i];
            map.put(value.name(), value);
        }
        return new SimpleMapModel(map, this.getWrapper());
    }
}

