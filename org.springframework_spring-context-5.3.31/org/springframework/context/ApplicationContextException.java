/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.FatalBeanException
 */
package org.springframework.context;

import org.springframework.beans.FatalBeanException;

public class ApplicationContextException
extends FatalBeanException {
    public ApplicationContextException(String msg) {
        super(msg);
    }

    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

