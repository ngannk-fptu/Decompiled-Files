/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor
 *  ognl.MethodFailedException
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.util.FilesystemUtils;
import com.opensymphony.xwork2.ognl.accessor.XWorkMethodAccessor;
import java.util.Arrays;
import java.util.Map;
import ognl.MethodFailedException;

public class ConfluenceMethodAccessor
extends XWorkMethodAccessor {
    public Object callMethod(Map context, Object target, String methodName, Object[] args) throws MethodFailedException {
        if (ConfluenceMethodAccessor.isPotentialPathTraversal(args)) {
            throw new MethodFailedException(target, methodName, (Throwable)new IllegalArgumentException("Potential path traversal detected"));
        }
        return super.callMethod(context, target, methodName, args);
    }

    public Object callStaticMethod(Map context, Class targetClass, String methodName, Object[] args) throws MethodFailedException {
        if (ConfluenceMethodAccessor.isPotentialPathTraversal(args)) {
            throw new MethodFailedException((Object)targetClass, methodName, (Throwable)new IllegalArgumentException("Potential path traversal detected"));
        }
        return super.callStaticMethod(context, targetClass, methodName, args);
    }

    public static boolean isPotentialPathTraversal(Object[] args) {
        return Arrays.stream(args).filter(arg -> arg instanceof String).map(arg -> (String)arg).anyMatch(FilesystemUtils::containsPathTraversal);
    }
}

