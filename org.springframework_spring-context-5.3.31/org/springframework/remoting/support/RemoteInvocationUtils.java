/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.support;

import java.util.HashSet;

public abstract class RemoteInvocationUtils {
    public static void fillInClientStackTraceIfPossible(Throwable ex) {
        if (ex != null) {
            StackTraceElement[] clientStack = new Throwable().getStackTrace();
            HashSet<Throwable> visitedExceptions = new HashSet<Throwable>();
            for (Throwable exToUpdate = ex; exToUpdate != null && !visitedExceptions.contains(exToUpdate); exToUpdate = exToUpdate.getCause()) {
                StackTraceElement[] serverStack = exToUpdate.getStackTrace();
                StackTraceElement[] combinedStack = new StackTraceElement[serverStack.length + clientStack.length];
                System.arraycopy(serverStack, 0, combinedStack, 0, serverStack.length);
                System.arraycopy(clientStack, 0, combinedStack, serverStack.length, clientStack.length);
                exToUpdate.setStackTrace(combinedStack);
                visitedExceptions.add(exToUpdate);
            }
        }
    }
}

