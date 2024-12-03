/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Vector;
import org.apache.axis.InternalException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.description.FieldDesc;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.FieldPropertyDescriptor;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class BeanUtils {
    public static final Object[] noArgs = new Object[0];
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$BeanUtils == null ? (class$org$apache$axis$utils$BeanUtils = BeanUtils.class$("org.apache.axis.utils.BeanUtils")) : class$org$apache$axis$utils$BeanUtils).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$BeanUtils;
    static /* synthetic */ Class class$org$apache$axis$AxisFault;
    static /* synthetic */ Class class$java$lang$Throwable;

    public static BeanPropertyDescriptor[] getPd(Class javaType) {
        return BeanUtils.getPd(javaType, null);
    }

    public static BeanPropertyDescriptor[] getPd(Class javaType, TypeDesc typeDesc) {
        BeanPropertyDescriptor[] pd;
        try {
            Class secJavaType = javaType;
            PropertyDescriptor[] rawPd = BeanUtils.getPropertyDescriptors(secJavaType);
            pd = BeanUtils.processPropertyDescriptors(rawPd, javaType, typeDesc);
        }
        catch (Exception e) {
            throw new InternalException(e);
        }
        return pd;
    }

    private static PropertyDescriptor[] getPropertyDescriptors(final Class secJavaType) {
        return (PropertyDescriptor[])AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                PropertyDescriptor[] result = null;
                try {
                    result = (class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = BeanUtils.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault).isAssignableFrom(secJavaType) ? Introspector.getBeanInfo(secJavaType, class$org$apache$axis$AxisFault == null ? (class$org$apache$axis$AxisFault = BeanUtils.class$("org.apache.axis.AxisFault")) : class$org$apache$axis$AxisFault).getPropertyDescriptors() : ((class$java$lang$Throwable == null ? (class$java$lang$Throwable = BeanUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable) != secJavaType && (class$java$lang$Throwable == null ? (class$java$lang$Throwable = BeanUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(secJavaType) ? Introspector.getBeanInfo(secJavaType, class$java$lang$Throwable == null ? (class$java$lang$Throwable = BeanUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).getPropertyDescriptors() : Introspector.getBeanInfo(secJavaType).getPropertyDescriptors());
                }
                catch (IntrospectionException introspectionException) {
                    // empty catch block
                }
                return result;
            }
        });
    }

    public static Vector getBeanAttributes(Class javaType, TypeDesc typeDesc) {
        Vector<String> ret = new Vector<String>();
        if (typeDesc == null) {
            try {
                Method getAttributeElements = javaType.getMethod("getAttributeElements", new Class[0]);
                String[] array = (String[])getAttributeElements.invoke(null, noArgs);
                ret = new Vector(array.length);
                for (int i = 0; i < array.length; ++i) {
                    ret.add(array[i]);
                }
            }
            catch (Exception e) {
                ret.clear();
            }
        } else {
            FieldDesc[] fields = typeDesc.getFields();
            if (fields != null) {
                for (int i = 0; i < fields.length; ++i) {
                    FieldDesc field = fields[i];
                    if (field.isElement()) continue;
                    ret.add(field.getFieldName());
                }
            }
        }
        return ret;
    }

    public static BeanPropertyDescriptor[] processPropertyDescriptors(PropertyDescriptor[] rawPd, Class cls) {
        return BeanUtils.processPropertyDescriptors(rawPd, cls, null);
    }

    public static BeanPropertyDescriptor[] processPropertyDescriptors(PropertyDescriptor[] rawPd, Class cls, TypeDesc typeDesc) {
        BeanPropertyDescriptor[] myPd = new BeanPropertyDescriptor[rawPd.length];
        ArrayList<BeanPropertyDescriptor> pd = new ArrayList<BeanPropertyDescriptor>();
        try {
            int j;
            boolean found;
            int i;
            for (int i2 = 0; i2 < rawPd.length; ++i2) {
                if (rawPd[i2].getName().equals("_any")) continue;
                pd.add(new BeanPropertyDescriptor(rawPd[i2]));
            }
            Field[] fields = cls.getFields();
            if (fields != null && fields.length > 0) {
                for (i = 0; i < fields.length; ++i) {
                    Field f = fields[i];
                    String clsName = f.getDeclaringClass().getName();
                    if (clsName.startsWith("java.") || clsName.startsWith("javax.") || Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) continue;
                    String fName = f.getName();
                    found = false;
                    for (j = 0; j < rawPd.length && !found; ++j) {
                        String pName = ((BeanPropertyDescriptor)pd.get(j)).getName();
                        if (pName.length() != fName.length() || !pName.substring(0, 1).equalsIgnoreCase(fName.substring(0, 1))) continue;
                        found = pName.length() == 1 || pName.substring(1).equals(fName.substring(1));
                    }
                    if (found) continue;
                    pd.add(new FieldPropertyDescriptor(f.getName(), f));
                }
            }
            if (typeDesc != null && typeDesc.getFields(true) != null) {
                ArrayList ordered = new ArrayList();
                FieldDesc[] fds = typeDesc.getFields(true);
                for (int i3 = 0; i3 < fds.length; ++i3) {
                    FieldDesc field = fds[i3];
                    if (!field.isElement()) continue;
                    found = false;
                    for (j = 0; j < pd.size() && !found; ++j) {
                        if (!field.getFieldName().equals(((BeanPropertyDescriptor)pd.get(j)).getName())) continue;
                        ordered.add(pd.remove(j));
                        found = true;
                    }
                }
                while (pd.size() > 0) {
                    ordered.add(pd.remove(0));
                }
                pd = ordered;
            }
            myPd = new BeanPropertyDescriptor[pd.size()];
            for (i = 0; i < pd.size(); ++i) {
                myPd[i] = (BeanPropertyDescriptor)pd.get(i);
            }
        }
        catch (Exception e) {
            log.error((Object)Messages.getMessage("badPropertyDesc00", cls.getName()), (Throwable)e);
            throw new InternalException(e);
        }
        return myPd;
    }

    public static BeanPropertyDescriptor getAnyContentPD(Class javaType) {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(javaType);
        return BeanUtils.getSpecificPD(pds, "_any");
    }

    public static BeanPropertyDescriptor getSpecificPD(PropertyDescriptor[] pds, String name) {
        for (int i = 0; i < pds.length; ++i) {
            PropertyDescriptor pd = pds[i];
            if (!pd.getName().equals(name)) continue;
            return new BeanPropertyDescriptor(pd);
        }
        return null;
    }

    public static BeanPropertyDescriptor getSpecificPD(BeanPropertyDescriptor[] pds, String name) {
        for (int i = 0; i < pds.length; ++i) {
            BeanPropertyDescriptor pd = pds[i];
            if (!pd.getName().equals(name)) continue;
            return pd;
        }
        return null;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

