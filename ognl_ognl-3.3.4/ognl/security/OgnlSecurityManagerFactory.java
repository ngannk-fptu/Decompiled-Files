/*
 * Decompiled with CFR 0.152.
 */
package ognl.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.AllPermission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import ognl.security.OgnlSecurityManager;

public class OgnlSecurityManagerFactory
extends SecureClassLoader {
    private static Object ognlSecurityManager;
    private Class<?> ognlSecurityManagerClass;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static Object getOgnlSecurityManager() {
        if (ognlSecurityManager != null) return ognlSecurityManager;
        Class<SecurityManager> clazz = SecurityManager.class;
        synchronized (SecurityManager.class) {
            if (ognlSecurityManager != null) return ognlSecurityManager;
            SecurityManager sm = System.getSecurityManager();
            if (sm == null || !sm.getClass().getName().equals(OgnlSecurityManager.class.getName())) {
                try {
                    ognlSecurityManager = new OgnlSecurityManagerFactory().build(sm);
                }
                catch (Exception exception) {}
            } else {
                ognlSecurityManager = sm;
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return ognlSecurityManager;
        }
    }

    private OgnlSecurityManagerFactory() throws IOException {
        super(OgnlSecurityManagerFactory.class.getClassLoader());
        PermissionCollection pc = new AllPermission().newPermissionCollection();
        pc.add(new AllPermission());
        ProtectionDomain pd = new ProtectionDomain(null, pc);
        byte[] byteArray = OgnlSecurityManagerFactory.toByteArray(this.getParent().getResourceAsStream(OgnlSecurityManager.class.getName().replace('.', '/') + ".class"));
        this.ognlSecurityManagerClass = this.defineClass(null, byteArray, 0, byteArray.length, pd);
    }

    private Object build(SecurityManager parentSecurityManager) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.ognlSecurityManagerClass.getConstructor(SecurityManager.class).newInstance(parentSecurityManager);
    }

    private static byte[] toByteArray(InputStream input) throws IOException {
        int n;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}

