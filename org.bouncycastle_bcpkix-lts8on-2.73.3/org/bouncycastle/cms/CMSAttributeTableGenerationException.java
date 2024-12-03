/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.cms.CMSRuntimeException;

public class CMSAttributeTableGenerationException
extends CMSRuntimeException {
    Exception e;

    public CMSAttributeTableGenerationException(String name) {
        super(name);
    }

    public CMSAttributeTableGenerationException(String name, Exception e) {
        super(name);
        this.e = e;
    }

    @Override
    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override
    public Throwable getCause() {
        return this.e;
    }
}

