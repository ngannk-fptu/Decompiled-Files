/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.ext;

import java.lang.reflect.ReflectPermission;
import java.net.URL;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.FactoryFinder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RuntimeDelegate {
    public static final String JAXRS_RUNTIME_DELEGATE_PROPERTY = "javax.ws.rs.ext.RuntimeDelegate";
    private static final String JAXRS_DEFAULT_RUNTIME_DELEGATE = "com.sun.ws.rs.ext.RuntimeDelegateImpl";
    private static ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
    private static volatile RuntimeDelegate rd;

    protected RuntimeDelegate() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static RuntimeDelegate getInstance() {
        RuntimeDelegate result = rd;
        if (result != null) return result;
        Class<RuntimeDelegate> clazz = RuntimeDelegate.class;
        synchronized (RuntimeDelegate.class) {
            result = rd;
            if (result != null) return result;
            rd = result = RuntimeDelegate.findDelegate();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return result;
        }
    }

    private static RuntimeDelegate findDelegate() {
        try {
            Object delegate = FactoryFinder.find(JAXRS_RUNTIME_DELEGATE_PROPERTY, JAXRS_DEFAULT_RUNTIME_DELEGATE);
            if (!(delegate instanceof RuntimeDelegate)) {
                Class<RuntimeDelegate> pClass = RuntimeDelegate.class;
                String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast" + delegate.getClass().getClassLoader().getResource(classnameAsResource) + "to" + targetTypeURL.toString());
            }
            return (RuntimeDelegate)delegate;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void setInstance(RuntimeDelegate rd) throws SecurityException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(rp);
        }
        Class<RuntimeDelegate> clazz = RuntimeDelegate.class;
        synchronized (RuntimeDelegate.class) {
            RuntimeDelegate.rd = rd;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public abstract UriBuilder createUriBuilder();

    public abstract Response.ResponseBuilder createResponseBuilder();

    public abstract Variant.VariantListBuilder createVariantListBuilder();

    public abstract <T> T createEndpoint(Application var1, Class<T> var2) throws IllegalArgumentException, UnsupportedOperationException;

    public abstract <T> HeaderDelegate<T> createHeaderDelegate(Class<T> var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface HeaderDelegate<T> {
        public T fromString(String var1) throws IllegalArgumentException;

        public String toString(T var1);
    }
}

