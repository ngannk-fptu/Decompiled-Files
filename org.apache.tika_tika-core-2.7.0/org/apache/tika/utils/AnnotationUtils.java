/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.tika.config.Field;
import org.apache.tika.config.Param;
import org.apache.tika.config.ParamField;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnnotationUtils {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationUtils.class);
    private static final Map<Class<?>, List<ParamField>> PARAM_INFO = new HashMap();

    private static List<AccessibleObject> collectInfo(Class<?> clazz, Class<? extends Annotation> annotation) {
        ArrayList<AccessibleObject> members = new ArrayList<AccessibleObject>();
        ArrayList<AccessibleObject> annotatedMembers = new ArrayList<AccessibleObject>();
        for (Class<?> superClazz = clazz; superClazz != null && superClazz != Object.class; superClazz = superClazz.getSuperclass()) {
            members.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
        }
        for (AccessibleObject member : members) {
            if (!member.isAnnotationPresent(annotation)) continue;
            AccessController.doPrivileged(() -> {
                member.setAccessible(true);
                return null;
            });
            annotatedMembers.add(member);
        }
        return annotatedMembers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static void assignFieldParams(Object bean, Map<String, Param> params) throws TikaConfigException {
        Class<?> beanClass = bean.getClass();
        if (!PARAM_INFO.containsKey(beanClass)) {
            Class<TikaConfig> clazz = TikaConfig.class;
            // MONITORENTER : org.apache.tika.config.TikaConfig.class
            if (!PARAM_INFO.containsKey(beanClass)) {
                List<AccessibleObject> aObjs = AnnotationUtils.collectInfo(beanClass, Field.class);
                ArrayList<ParamField> fields = new ArrayList<ParamField>(aObjs.size());
                for (AccessibleObject aObj : aObjs) {
                    fields.add(new ParamField(aObj));
                }
                PARAM_INFO.put(beanClass, fields);
            }
            // MONITOREXIT : clazz
        }
        List<ParamField> fields = PARAM_INFO.get(beanClass);
        Iterator<ParamField> iterator = fields.iterator();
        while (iterator.hasNext()) {
            String msg;
            ParamField field = iterator.next();
            Param param = params.get(field.getName());
            if (param != null) {
                if (!field.getType().isAssignableFrom(param.getType())) {
                    msg = String.format(Locale.ROOT, "Value '%s' of type '%s' can't be assigned to field '%s' of defined type '%s'", param.getValue(), param.getValue().getClass(), field.getName(), field.getType());
                    throw new TikaConfigException(msg);
                }
                try {
                    field.assignValue(bean, param.getValue());
                    continue;
                }
                catch (InvocationTargetException e) {
                    LOG.error("Error assigning value '{}' to '{}'", param.getValue(), (Object)param.getName());
                    Throwable cause = e.getCause() == null ? e : e.getCause();
                    throw new TikaConfigException(cause.getMessage(), cause);
                }
                catch (IllegalAccessException e) {
                    LOG.error("Error assigning value '{}' to '{}'", param.getValue(), (Object)param.getName());
                    throw new TikaConfigException(e.getMessage(), e);
                }
            }
            if (field.isRequired()) {
                msg = String.format(Locale.ROOT, "Param %s is required for %s, but it is not given in config.", field.getName(), bean.getClass().getName());
                throw new TikaConfigException(msg);
            }
            LOG.debug("Param not supplied, field is not mandatory");
        }
    }
}

