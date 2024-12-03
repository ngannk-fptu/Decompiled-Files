/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class MapELResolver
extends ELResolver {
    private static final Class<?> UNMODIFIABLE = Collections.unmodifiableMap(new HashMap()).getClass();
    private final boolean readOnly;

    public MapELResolver() {
        this.readOnly = false;
    }

    public MapELResolver(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            return Object.class;
        }
        return null;
    }

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            return ((Map)base).get(property);
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            if (this.readOnly) {
                throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
            }
            try {
                Map map = (Map)base;
                map.put(property, value);
            }
            catch (UnsupportedOperationException e) {
                throw new PropertyNotWritableException(e);
            }
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof Map) {
            context.setPropertyResolved(base, property);
            return this.readOnly || UNMODIFIABLE.equals(base.getClass());
        }
        return this.readOnly;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof Map) {
            Iterator itr = ((Map)base).keySet().iterator();
            ArrayList<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();
            while (itr.hasNext()) {
                Object key = itr.next();
                FeatureDescriptor desc = new FeatureDescriptor();
                desc.setDisplayName(key.toString());
                desc.setShortDescription("");
                desc.setExpert(false);
                desc.setHidden(false);
                desc.setName(key.toString());
                desc.setPreferred(true);
                desc.setValue("resolvableAtDesignTime", Boolean.TRUE);
                desc.setValue("type", key.getClass());
                feats.add(desc);
            }
            return feats.iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof Map) {
            return Object.class;
        }
        return null;
    }
}

