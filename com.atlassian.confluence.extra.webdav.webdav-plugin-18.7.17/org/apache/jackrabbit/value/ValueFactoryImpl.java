/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.AbstractValueFactory;

public class ValueFactoryImpl
extends AbstractValueFactory {
    private static final ValueFactory valueFactory = new ValueFactoryImpl();

    protected ValueFactoryImpl() {
    }

    public static ValueFactory getInstance() {
        return valueFactory;
    }

    @Override
    protected void checkPathFormat(String pathValue) throws ValueFormatException {
    }

    @Override
    protected void checkNameFormat(String nameValue) throws ValueFormatException {
    }
}

