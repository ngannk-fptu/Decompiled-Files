/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.FunctionMapper
 *  javax.servlet.jsp.el.FunctionMapper
 */
package org.apache.sling.scripting.jsp.jasper.runtime;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import javax.servlet.jsp.el.FunctionMapper;
import org.apache.sling.scripting.jsp.jasper.security.SecurityUtil;

public final class ProtectedFunctionMapper
extends javax.el.FunctionMapper
implements FunctionMapper {
    private HashMap<String, Method> fnmap;
    private Method theMethod;

    private ProtectedFunctionMapper() {
    }

    public static ProtectedFunctionMapper getInstance() {
        ProtectedFunctionMapper funcMapper = SecurityUtil.isPackageProtectionEnabled() ? AccessController.doPrivileged(new PrivilegedAction<ProtectedFunctionMapper>(){

            @Override
            public ProtectedFunctionMapper run() {
                return new ProtectedFunctionMapper();
            }
        }) : new ProtectedFunctionMapper();
        funcMapper.fnmap = new HashMap();
        return funcMapper;
    }

    public void mapFunction(String fnQName, final Class<?> c, final String methodName, final Class<?>[] args) {
        Method method;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            try {
                method = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                    @Override
                    public Method run() throws Exception {
                        return c.getDeclaredMethod(methodName, args);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw new RuntimeException("Invalid function mapping - no such method: " + ex.getException().getMessage());
            }
        }
        try {
            method = c.getDeclaredMethod(methodName, args);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid function mapping - no such method: " + e.getMessage());
        }
        this.fnmap.put(fnQName, method);
    }

    public static ProtectedFunctionMapper getMapForFunction(String fnQName, final Class<?> c, final String methodName, final Class<?>[] args) {
        Method method;
        ProtectedFunctionMapper funcMapper;
        if (SecurityUtil.isPackageProtectionEnabled()) {
            funcMapper = AccessController.doPrivileged(new PrivilegedAction<ProtectedFunctionMapper>(){

                @Override
                public ProtectedFunctionMapper run() {
                    return new ProtectedFunctionMapper();
                }
            });
            try {
                method = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>(){

                    @Override
                    public Method run() throws Exception {
                        return c.getDeclaredMethod(methodName, args);
                    }
                });
            }
            catch (PrivilegedActionException ex) {
                throw new RuntimeException("Invalid function mapping - no such method: " + ex.getException().getMessage());
            }
        }
        funcMapper = new ProtectedFunctionMapper();
        try {
            method = c.getDeclaredMethod(methodName, args);
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException("Invalid function mapping - no such method: " + e.getMessage());
        }
        funcMapper.theMethod = method;
        return funcMapper;
    }

    public Method resolveFunction(String prefix, String localName) {
        if (this.fnmap != null) {
            return this.fnmap.get(prefix + ":" + localName);
        }
        return this.theMethod;
    }
}

