/*
 * Decompiled with CFR 0.152.
 */
package javax.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.el.Util;

public class ResourceBundleELResolver
extends ELResolver {
    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                try {
                    return ((ResourceBundle)base).getObject(property.toString());
                }
                catch (MissingResourceException mre) {
                    return "???" + property.toString() + "???";
                }
            }
        }
        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
        }
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            throw new PropertyNotWritableException(Util.message(context, "resolverNotWritable", base.getClass().getName()));
        }
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base instanceof ResourceBundle) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return false;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        if (base instanceof ResourceBundle) {
            ArrayList<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>();
            Enumeration<String> e = ((ResourceBundle)base).getKeys();
            while (e.hasMoreElements()) {
                String key = e.nextElement();
                FeatureDescriptor feat = new FeatureDescriptor();
                feat.setDisplayName(key);
                feat.setShortDescription("");
                feat.setExpert(false);
                feat.setHidden(false);
                feat.setName(key);
                feat.setPreferred(true);
                feat.setValue("resolvableAtDesignTime", Boolean.TRUE);
                feat.setValue("type", String.class);
                feats.add(feat);
            }
            return feats.iterator();
        }
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        if (base instanceof ResourceBundle) {
            return String.class;
        }
        return null;
    }
}

