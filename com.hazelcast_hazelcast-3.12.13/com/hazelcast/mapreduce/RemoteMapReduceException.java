/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.core.HazelcastException;
import java.util.List;

@Deprecated
public class RemoteMapReduceException
extends HazelcastException {
    public RemoteMapReduceException(String message, List<Exception> remoteCauses) {
        super(message);
        this.setStackTraceElements(remoteCauses);
    }

    private void setStackTraceElements(List<Exception> remoteCauses) {
        StackTraceElement[] originalElements = super.getStackTrace();
        int stackTraceSize = originalElements.length;
        for (Exception remoteCause : remoteCauses) {
            stackTraceSize += remoteCause.getStackTrace().length + 1;
        }
        StackTraceElement[] elements = new StackTraceElement[stackTraceSize];
        System.arraycopy(originalElements, 0, elements, 0, originalElements.length);
        int pos = originalElements.length;
        for (Exception remoteCause : remoteCauses) {
            StackTraceElement[] remoteStackTraceElements = remoteCause.getStackTrace();
            elements[pos++] = new StackTraceElement("--- Remote Exception: " + remoteCause.getMessage() + " ---", "", null, 0);
            for (int i = 0; i < remoteStackTraceElements.length; ++i) {
                StackTraceElement element = remoteStackTraceElements[i];
                String className = "    " + element.getClassName();
                String methodName = element.getMethodName();
                String fileName = element.getFileName();
                elements[pos++] = new StackTraceElement(className, methodName, fileName, element.getLineNumber());
            }
        }
        this.setStackTrace(elements);
    }
}

