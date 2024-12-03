/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.configuration2.convert.AbstractListDelimiterHandler;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.ValueTransformer;

public class DisabledListDelimiterHandler
extends AbstractListDelimiterHandler {
    public static final ListDelimiterHandler INSTANCE = new DisabledListDelimiterHandler();

    @Override
    public Object escapeList(List<?> values, ValueTransformer transformer) {
        throw new UnsupportedOperationException("Escaping lists is not supported!");
    }

    @Override
    protected Collection<String> splitString(String s, boolean trim) {
        ArrayList<String> result = new ArrayList<String>(1);
        result.add(s);
        return result;
    }

    @Override
    protected String escapeString(String s) {
        return s;
    }
}

