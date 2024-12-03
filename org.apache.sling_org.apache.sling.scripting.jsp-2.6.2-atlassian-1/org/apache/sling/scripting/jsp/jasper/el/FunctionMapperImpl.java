/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.FunctionMapper
 *  javax.servlet.jsp.el.FunctionMapper
 */
package org.apache.sling.scripting.jsp.jasper.el;

import java.lang.reflect.Method;
import javax.servlet.jsp.el.FunctionMapper;

public final class FunctionMapperImpl
extends javax.el.FunctionMapper {
    private final FunctionMapper fnMapper;

    public FunctionMapperImpl(FunctionMapper fnMapper) {
        this.fnMapper = fnMapper;
    }

    public Method resolveFunction(String prefix, String localName) {
        return this.fnMapper.resolveFunction(prefix, localName);
    }
}

