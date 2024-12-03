/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.MethodFailedException
 *  ognl.NoSuchPropertyException
 *  ognl.Ognl
 *  ognl.OgnlContext
 *  ognl.OgnlException
 *  ognl.OgnlRuntime
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.ognl.accessor.RootAccessor;
import com.opensymphony.xwork2.util.CompoundRoot;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class CompoundRootAccessor
implements RootAccessor {
    private static final Logger LOG = LogManager.getLogger(CompoundRootAccessor.class);
    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static final Map<MethodCall, Boolean> invalidMethods = new ConcurrentHashMap<MethodCall, Boolean>();
    private boolean devMode;
    private boolean disallowCustomOgnlMap;

    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return null;
    }

    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return null;
    }

    @Inject(value="struts.devMode")
    protected void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean((String)mode);
    }

    @Inject(value="struts.ognl.disallowCustomOgnlMap", required=false)
    public void useDisallowCustomOgnlMap(String disallowCustomOgnlMap) {
        this.disallowCustomOgnlMap = BooleanUtils.toBoolean((String)disallowCustomOgnlMap);
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        CompoundRoot root = (CompoundRoot)target;
        OgnlContext ognlContext = (OgnlContext)context;
        for (Object o : root) {
            if (o == null) continue;
            try {
                if (OgnlRuntime.hasSetProperty((OgnlContext)ognlContext, o, (Object)name)) {
                    OgnlRuntime.setProperty((OgnlContext)ognlContext, o, (Object)name, (Object)value);
                    return;
                }
                if (!(o instanceof Map)) continue;
                Map map = (Map)o;
                try {
                    map.put(name, value);
                    return;
                }
                catch (UnsupportedOperationException unsupportedOperationException) {
                }
            }
            catch (IntrospectionException introspectionException) {}
        }
        boolean reportError = BooleanUtils.toBoolean((Boolean)((Boolean)context.get("com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp")));
        if (reportError || this.devMode) {
            String msg = String.format("No object in the CompoundRoot has a publicly accessible property named '%s' (no setter could be found).", name);
            if (reportError) {
                throw new StrutsException(msg);
            }
            LOG.warn(msg);
        }
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        CompoundRoot root = (CompoundRoot)target;
        OgnlContext ognlContext = (OgnlContext)context;
        if (name instanceof Integer) {
            Integer index = (Integer)name;
            return root.cutStack(index);
        }
        if (name instanceof String) {
            if ("top".equals(name)) {
                if (!root.isEmpty()) {
                    return root.get(0);
                }
                return null;
            }
            for (Object o : root) {
                if (o == null) continue;
                try {
                    if (!OgnlRuntime.hasGetProperty((OgnlContext)ognlContext, o, (Object)name) && (!(o instanceof Map) || !((Map)o).containsKey(name))) continue;
                    return OgnlRuntime.getProperty((OgnlContext)ognlContext, o, (Object)name);
                }
                catch (OgnlException e) {
                    if (e.getReason() == null) continue;
                    String msg = "Caught an Ognl exception while getting property " + name;
                    throw new StrutsException(msg, e);
                }
                catch (IntrospectionException introspectionException) {
                }
            }
            if (context.containsKey(OgnlValueStack.THROW_EXCEPTION_ON_FAILURE)) {
                throw new NoSuchPropertyException(target, name);
            }
            return null;
        }
        return null;
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) throws MethodFailedException {
        CompoundRoot root = (CompoundRoot)target;
        if ("describe".equals(name)) {
            Object v = objects != null && objects.length == 1 ? objects[0] : root.get(0);
            if (v instanceof Collection || v instanceof Map || v.getClass().isArray()) {
                return v.toString();
            }
            try {
                Map descriptors = OgnlRuntime.getPropertyDescriptors(v.getClass());
                int maxSize = 0;
                for (Object pdName : descriptors.keySet()) {
                    if (((String)pdName).length() <= maxSize) continue;
                    maxSize = ((String)pdName).length();
                }
                TreeSet<String> set = new TreeSet<String>();
                for (Object pd : descriptors.values()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(((FeatureDescriptor)pd).getName()).append(": ");
                    int padding = maxSize - ((FeatureDescriptor)pd).getName().length();
                    for (int i = 0; i < padding; ++i) {
                        sb.append(" ");
                    }
                    sb.append(((PropertyDescriptor)pd).getPropertyType().getName());
                    set.add(sb.toString());
                }
                StringBuilder sb = new StringBuilder();
                for (String aSet : set) {
                    sb.append(aSet).append("\n");
                }
                return sb.toString();
            }
            catch (IntrospectionException | OgnlException e) {
                LOG.debug("Got exception in callMethod", e);
                return null;
            }
        }
        Throwable reason = null;
        Class[] argTypes = this.getArgTypes(objects);
        for (Object o : root) {
            if (o == null) continue;
            Class<?> clazz = o.getClass();
            MethodCall mc = null;
            if (argTypes != null) {
                mc = new MethodCall(clazz, name, argTypes);
            }
            if (argTypes != null && invalidMethods.containsKey(mc)) continue;
            try {
                return OgnlRuntime.callMethod((OgnlContext)((OgnlContext)context), o, (String)name, (Object[])objects);
            }
            catch (OgnlException e) {
                reason = e.getReason();
                if (reason != null && !(reason instanceof NoSuchMethodException)) break;
                if (mc == null || reason == null) continue;
                invalidMethods.put(mc, Boolean.TRUE);
            }
        }
        if (context.containsKey(OgnlValueStack.THROW_EXCEPTION_ON_FAILURE)) {
            throw new MethodFailedException(target, name, reason);
        }
        return null;
    }

    public Object callStaticMethod(Map transientVars, Class aClass, String s, Object[] objects) throws MethodFailedException {
        return null;
    }

    public Class classForName(String className, Map context) throws ClassNotFoundException {
        String nodeClassName;
        Object root = Ognl.getRoot((Map)context);
        if (this.disallowCustomOgnlMap && "ognl.ASTMap".equals(nodeClassName = ((OgnlContext)context).getCurrentNode().getClass().getName())) {
            LOG.error("Constructing OGNL ASTMap's from custom classes is forbidden. Attempted class: {}", (Object)className);
            return null;
        }
        try {
            if (root instanceof CompoundRoot && className.startsWith("vs")) {
                CompoundRoot compoundRoot = (CompoundRoot)root;
                if ("vs".equals(className)) {
                    return compoundRoot.peek().getClass();
                }
                int index = Integer.parseInt(className.substring(2));
                return compoundRoot.get(index - 1).getClass();
            }
        }
        catch (Exception e) {
            LOG.debug("Got exception when tried to get class for name [{}]", (Object)className, (Object)e);
        }
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private Class[] getArgTypes(Object[] args) {
        if (args == null) {
            return EMPTY_CLASS_ARRAY;
        }
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; ++i) {
            Object arg = args[i];
            classes[i] = arg != null ? arg.getClass() : Object.class;
        }
        return classes;
    }

    static class MethodCall {
        Class clazz;
        String name;
        Class[] args;
        int hash;

        public MethodCall(Class clazz, String name, Class[] args) {
            this.clazz = clazz;
            this.name = name;
            this.args = args;
            this.hash = clazz.hashCode() + name.hashCode();
            for (Class arg : args) {
                this.hash += arg.hashCode();
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof MethodCall)) {
                return false;
            }
            MethodCall mc = (MethodCall)obj;
            return mc.clazz.equals(this.clazz) && mc.name.equals(this.name) && Arrays.equals(mc.args, this.args);
        }

        public int hashCode() {
            return this.hash;
        }
    }
}

