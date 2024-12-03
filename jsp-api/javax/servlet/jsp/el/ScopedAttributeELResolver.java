/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELClass
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.ImportHandler
 */
package javax.servlet.jsp.el;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import javax.el.ELClass;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ImportHandler;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

public class ScopedAttributeELResolver
extends ELResolver {
    private static final Class<?> AST_IDENTIFIER_KEY;

    public Object getValue(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        Object result = null;
        if (base == null) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                String key = property.toString();
                PageContext page = (PageContext)context.getContext(JspContext.class);
                result = page.findAttribute(key);
                if (result == null) {
                    ImportHandler importHandler;
                    Boolean value;
                    boolean resolveClass = true;
                    if (AST_IDENTIFIER_KEY != null && (value = (Boolean)context.getContext(AST_IDENTIFIER_KEY)) != null && value.booleanValue()) {
                        resolveClass = false;
                    }
                    if ((importHandler = context.getImportHandler()) != null) {
                        Class clazz = null;
                        if (resolveClass) {
                            clazz = importHandler.resolveClass(key);
                        }
                        if (clazz != null) {
                            result = new ELClass(clazz);
                        }
                        if (result == null && (clazz = importHandler.resolveStatic(key)) != null) {
                            try {
                                result = clazz.getField(key).get(null);
                            }
                            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException exception) {
                                // empty catch block
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public Class<Object> getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            return Object.class;
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
            if (property != null) {
                String key = property.toString();
                PageContext page = (PageContext)context.getContext(JspContext.class);
                int scope = page.getAttributesScope(key);
                if (scope != 0) {
                    page.setAttribute(key, value, scope);
                } else {
                    page.setAttribute(key, value);
                }
            }
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null) {
            context.setPropertyResolved(base, property);
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        FeatureDescriptor descriptor;
        Object value;
        String name;
        PageContext ctxt = (PageContext)context.getContext(JspContext.class);
        ArrayList<FeatureDescriptor> list = new ArrayList<FeatureDescriptor>();
        Enumeration<String> e = ctxt.getAttributeNamesInScope(1);
        while (e.hasMoreElements()) {
            name = e.nextElement();
            value = ctxt.getAttribute(name, 1);
            descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("page scoped attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        e = ctxt.getAttributeNamesInScope(2);
        while (e.hasMoreElements()) {
            name = e.nextElement();
            value = ctxt.getAttribute(name, 2);
            descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("request scope attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        if (ctxt.getSession() != null) {
            e = ctxt.getAttributeNamesInScope(3);
            while (e.hasMoreElements()) {
                name = e.nextElement();
                value = ctxt.getAttribute(name, 3);
                descriptor = new FeatureDescriptor();
                descriptor.setName(name);
                descriptor.setDisplayName(name);
                descriptor.setExpert(false);
                descriptor.setHidden(false);
                descriptor.setPreferred(true);
                descriptor.setShortDescription("session scoped attribute");
                descriptor.setValue("type", value.getClass());
                descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
                list.add(descriptor);
            }
        }
        e = ctxt.getAttributeNamesInScope(4);
        while (e.hasMoreElements()) {
            name = e.nextElement();
            value = ctxt.getAttribute(name, 4);
            descriptor = new FeatureDescriptor();
            descriptor.setName(name);
            descriptor.setDisplayName(name);
            descriptor.setExpert(false);
            descriptor.setHidden(false);
            descriptor.setPreferred(true);
            descriptor.setShortDescription("application scoped attribute");
            descriptor.setValue("type", value.getClass());
            descriptor.setValue("resolvableAtDesignTime", Boolean.FALSE);
            list.add(descriptor);
        }
        return list.iterator();
    }

    public Class<String> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }

    static {
        Class<?> key = null;
        try {
            key = Class.forName("org.apache.el.parser.AstIdentifier");
        }
        catch (Exception exception) {
            // empty catch block
        }
        AST_IDENTIFIER_KEY = key;
    }
}

