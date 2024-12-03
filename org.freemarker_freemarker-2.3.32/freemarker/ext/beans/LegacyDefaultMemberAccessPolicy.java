/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ClassIntrospector;
import freemarker.ext.beans.ClassMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.template.utility.ClassUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public final class LegacyDefaultMemberAccessPolicy
implements MemberAccessPolicy {
    public static final LegacyDefaultMemberAccessPolicy INSTANCE = new LegacyDefaultMemberAccessPolicy();
    private static final String UNSAFE_METHODS_PROPERTIES = "unsafeMethods.properties";
    private static final Set<Method> UNSAFE_METHODS = LegacyDefaultMemberAccessPolicy.createUnsafeMethodsSet();
    private static final BlacklistClassMemberAccessPolicy CLASS_MEMBER_ACCESS_POLICY_INSTANCE = new BlacklistClassMemberAccessPolicy();

    private static Set<Method> createUnsafeMethodsSet() {
        try {
            Properties props = ClassUtil.loadProperties(BeansWrapper.class, UNSAFE_METHODS_PROPERTIES);
            HashSet<Method> set = new HashSet<Method>(props.size() * 4 / 3, 1.0f);
            for (Object key : props.keySet()) {
                try {
                    set.add(LegacyDefaultMemberAccessPolicy.parseMethodSpec((String)key));
                }
                catch (ClassNotFoundException | NoSuchMethodException e) {
                    if (!ClassIntrospector.DEVELOPMENT_MODE) continue;
                    throw e;
                }
            }
            return set;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load unsafe method set", e);
        }
    }

    private static Method parseMethodSpec(String methodSpec) throws ClassNotFoundException, NoSuchMethodException {
        int brace = methodSpec.indexOf(40);
        int dot = methodSpec.lastIndexOf(46, brace);
        Class clazz = ClassUtil.forName(methodSpec.substring(0, dot));
        String methodName = methodSpec.substring(dot + 1, brace);
        String argSpec = methodSpec.substring(brace + 1, methodSpec.length() - 1);
        StringTokenizer tok = new StringTokenizer(argSpec, ",");
        int argcount = tok.countTokens();
        Class[] argTypes = new Class[argcount];
        for (int i = 0; i < argcount; ++i) {
            String argClassName = tok.nextToken();
            argTypes[i] = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
            if (argTypes[i] != null) continue;
            argTypes[i] = ClassUtil.forName(argClassName);
        }
        return clazz.getMethod(methodName, argTypes);
    }

    private LegacyDefaultMemberAccessPolicy() {
    }

    @Override
    public ClassMemberAccessPolicy forClass(Class<?> containingClass) {
        return CLASS_MEMBER_ACCESS_POLICY_INSTANCE;
    }

    @Override
    public boolean isToStringAlwaysExposed() {
        return true;
    }

    private static class BlacklistClassMemberAccessPolicy
    implements ClassMemberAccessPolicy {
        private BlacklistClassMemberAccessPolicy() {
        }

        @Override
        public boolean isMethodExposed(Method method) {
            return !UNSAFE_METHODS.contains(method);
        }

        @Override
        public boolean isConstructorExposed(Constructor<?> constructor) {
            return true;
        }

        @Override
        public boolean isFieldExposed(Field field) {
            return true;
        }
    }
}

