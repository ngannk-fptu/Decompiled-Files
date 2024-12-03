/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.exception;

import java.util.Arrays;
import javax.management.ObjectName;

public class UnableReadAttributeException
extends RuntimeException {
    public UnableReadAttributeException(Throwable cause, String ... attributes) {
        super(String.format("Unable to read attributes %s from Mbean : [Catalina:type=ThreadPool]", Arrays.toString(attributes)), cause);
    }

    public UnableReadAttributeException(ObjectName objectName) {
        super(String.format("Unable to find Object name : [%s] ThreadPool type under Catalina domain", objectName.getCanonicalName()));
    }
}

