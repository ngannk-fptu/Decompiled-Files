/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg;

import org.hibernate.AnnotationException;

@Deprecated
public class RecoverableException
extends AnnotationException {
    public RecoverableException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public RecoverableException(String msg) {
        super(msg);
    }
}

