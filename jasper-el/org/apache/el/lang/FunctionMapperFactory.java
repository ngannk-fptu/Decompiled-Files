/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.FunctionMapper
 */
package org.apache.el.lang;

import java.lang.reflect.Method;
import javax.el.FunctionMapper;
import org.apache.el.lang.FunctionMapperImpl;
import org.apache.el.util.MessageFactory;

public class FunctionMapperFactory
extends FunctionMapper {
    protected FunctionMapperImpl memento = null;
    protected final FunctionMapper target;

    public FunctionMapperFactory(FunctionMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException(MessageFactory.get("error.noFunctionMapperTarget"));
        }
        this.target = mapper;
    }

    public Method resolveFunction(String prefix, String localName) {
        Method m;
        if (this.memento == null) {
            this.memento = new FunctionMapperImpl();
        }
        if ((m = this.target.resolveFunction(prefix, localName)) != null) {
            this.memento.mapFunction(prefix, localName, m);
        }
        return m;
    }

    public void mapFunction(String prefix, String localName, Method method) {
        if (this.memento == null) {
            this.memento = new FunctionMapperImpl();
        }
        this.memento.mapFunction(prefix, localName, method);
    }

    public FunctionMapper create() {
        return this.memento;
    }
}

