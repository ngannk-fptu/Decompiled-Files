/*
 * Decompiled with CFR 0.152.
 */
package javax.interceptor;

import java.lang.reflect.Method;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface InvocationContext {
    public Object getTarget();

    public Method getMethod();

    public Object[] getParameters();

    public void setParameters(Object[] var1);

    public Map<String, Object> getContextData();

    public Object proceed() throws Exception;
}

