/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xmlprops;

import com.mchange.lang.PotentiallySecondaryException;

public class XmlPropsException
extends PotentiallySecondaryException {
    public XmlPropsException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public XmlPropsException(Throwable throwable) {
        super(throwable);
    }

    public XmlPropsException(String string) {
        super(string);
    }

    public XmlPropsException() {
    }
}

