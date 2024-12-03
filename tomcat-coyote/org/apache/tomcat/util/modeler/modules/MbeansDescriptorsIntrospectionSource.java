/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.modeler.modules;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.AttributeInfo;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.OperationInfo;
import org.apache.tomcat.util.modeler.ParameterInfo;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.modeler.modules.ModelerSource;
import org.apache.tomcat.util.res.StringManager;

public class MbeansDescriptorsIntrospectionSource
extends ModelerSource {
    private static final Log log = LogFactory.getLog(MbeansDescriptorsIntrospectionSource.class);
    private static final StringManager sm = StringManager.getManager(MbeansDescriptorsIntrospectionSource.class);
    private Registry registry;
    private String type;
    private final List<ObjectName> mbeans = new ArrayList<ObjectName>();
    private static final Map<String, String> specialMethods = new HashMap<String, String>();
    private static final Class<?>[] supportedTypes;

    public void setRegistry(Registry reg) {
        this.registry = reg;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public List<ObjectName> loadDescriptors(Registry registry, String type, Object source) throws Exception {
        this.setRegistry(registry);
        this.setType(type);
        this.setSource(source);
        this.execute();
        return this.mbeans;
    }

    public void execute() throws Exception {
        if (this.registry == null) {
            this.registry = Registry.getRegistry(null, null);
        }
        try {
            ManagedBean managed = this.createManagedBean(this.registry, null, (Class)this.source, this.type);
            if (managed == null) {
                return;
            }
            managed.setName(this.type);
            this.registry.addManagedBean(managed);
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("modules.readDescriptorsError"), (Throwable)ex);
        }
    }

    private boolean supportedType(Class<?> ret) {
        for (Class<?> supportedType : supportedTypes) {
            if (ret != supportedType) continue;
            return true;
        }
        return this.isBeanCompatible(ret);
    }

    private boolean isBeanCompatible(Class<?> javaType) {
        if (javaType.isArray() || javaType.isPrimitive()) {
            return false;
        }
        if (javaType.getName().startsWith("java.") || javaType.getName().startsWith("javax.")) {
            return false;
        }
        try {
            javaType.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            return false;
        }
        Class<?> superClass = javaType.getSuperclass();
        return superClass == null || superClass == Object.class || superClass == Exception.class || superClass == Throwable.class || this.isBeanCompatible(superClass);
    }

    private void initMethods(Class<?> realClass, Set<String> attNames, Map<String, Method> getAttMap, Map<String, Method> setAttMap, Map<String, Method> invokeAttMap) {
        Method[] methods;
        for (Method method : methods = realClass.getMethods()) {
            String name = method.getName();
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (!Modifier.isPublic(method.getModifiers())) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Not public " + method));
                continue;
            }
            if (method.getDeclaringClass() == Object.class) continue;
            Class<?>[] params = method.getParameterTypes();
            if (name.startsWith("get") && params.length == 0) {
                Class<?> ret = method.getReturnType();
                if (!this.supportedType(ret)) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Unsupported type " + method));
                    continue;
                }
                name = MbeansDescriptorsIntrospectionSource.unCapitalize(name.substring(3));
                getAttMap.put(name, method);
                attNames.add(name);
                continue;
            }
            if (name.startsWith("is") && params.length == 0) {
                Class<?> ret = method.getReturnType();
                if (Boolean.TYPE != ret) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Unsupported type " + method + " " + ret));
                    continue;
                }
                name = MbeansDescriptorsIntrospectionSource.unCapitalize(name.substring(2));
                getAttMap.put(name, method);
                attNames.add(name);
                continue;
            }
            if (name.startsWith("set") && params.length == 1) {
                if (!this.supportedType(params[0])) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)("Unsupported type " + method + " " + params[0]));
                    continue;
                }
                name = MbeansDescriptorsIntrospectionSource.unCapitalize(name.substring(3));
                setAttMap.put(name, method);
                attNames.add(name);
                continue;
            }
            if (params.length == 0) {
                if (specialMethods.get(method.getName()) != null) continue;
                invokeAttMap.put(name, method);
                continue;
            }
            boolean supported = true;
            for (Class<?> param : params) {
                if (this.supportedType(param)) continue;
                supported = false;
                break;
            }
            if (!supported) continue;
            invokeAttMap.put(name, method);
        }
    }

    public ManagedBean createManagedBean(Registry registry, String domain, Class<?> realClass, String type) {
        ManagedBean mbean = new ManagedBean();
        HashSet<String> attrNames = new HashSet<String>();
        HashMap<String, Method> getAttMap = new HashMap<String, Method>();
        HashMap<String, Method> setAttMap = new HashMap<String, Method>();
        HashMap<String, Method> invokeAttMap = new HashMap<String, Method>();
        this.initMethods(realClass, attrNames, getAttMap, setAttMap, invokeAttMap);
        try {
            for (String string : attrNames) {
                Method sm;
                AttributeInfo ai = new AttributeInfo();
                ai.setName(string);
                Method gm = (Method)getAttMap.get(string);
                if (gm != null) {
                    ai.setGetMethod(gm.getName());
                    Class<?> t = gm.getReturnType();
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                }
                if ((sm = (Method)setAttMap.get(string)) != null) {
                    Class<?> t = sm.getParameterTypes()[0];
                    if (t != null) {
                        ai.setType(t.getName());
                    }
                    ai.setSetMethod(sm.getName());
                }
                ai.setDescription("Introspected attribute " + string);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Introspected attribute " + string + " " + gm + " " + sm));
                }
                if (gm == null) {
                    ai.setReadable(false);
                }
                if (sm == null) {
                    ai.setWriteable(false);
                }
                if (sm == null && gm == null) continue;
                mbean.addAttribute(ai);
            }
            for (Map.Entry entry : invokeAttMap.entrySet()) {
                String name = (String)entry.getKey();
                Method m = (Method)entry.getValue();
                OperationInfo op = new OperationInfo();
                op.setName(name);
                op.setReturnType(m.getReturnType().getName());
                op.setDescription("Introspected operation " + name);
                Class<?>[] params = m.getParameterTypes();
                for (int i = 0; i < params.length; ++i) {
                    ParameterInfo pi = new ParameterInfo();
                    pi.setType(params[i].getName());
                    pi.setName(("param" + i).intern());
                    pi.setDescription(("Introspected parameter param" + i).intern());
                    op.addParameter(pi);
                }
                mbean.addOperation(op);
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("Setting name: " + type));
            }
            mbean.setName(type);
            return mbean;
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("source.introspectionError", new Object[]{realClass.getName()}), (Throwable)ex);
            return null;
        }
    }

    private static String unCapitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    static {
        specialMethods.put("preDeregister", "");
        specialMethods.put("postDeregister", "");
        supportedTypes = new Class[]{Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE, Character.class, Character.TYPE, Short.class, Short.TYPE, Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE, String.class, String[].class, BigDecimal.class, BigInteger.class, ObjectName.class, Object[].class, File.class};
    }
}

