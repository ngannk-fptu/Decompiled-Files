/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xb.xmlconfig.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.values.JavaStringHolderEx;
import org.apache.xmlbeans.impl.xb.xmlconfig.JavaName;

public class JavaNameImpl
extends JavaStringHolderEx
implements JavaName {
    private static final long serialVersionUID = 1L;

    public JavaNameImpl(SchemaType sType) {
        super(sType, false);
    }

    protected JavaNameImpl(SchemaType sType, boolean b) {
        super(sType, b);
    }
}

