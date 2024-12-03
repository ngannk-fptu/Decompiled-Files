/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELException
 */
package org.apache.el.parser;

import java.util.HashMap;
import java.util.Map;
import javax.el.ELException;
import org.apache.el.lang.EvaluationContext;
import org.apache.el.parser.AstMapEntry;
import org.apache.el.parser.SimpleNode;

public class AstMapData
extends SimpleNode {
    public AstMapData(int id) {
        super(id);
    }

    @Override
    public Object getValue(EvaluationContext ctx) throws ELException {
        HashMap<Object, Object> result = new HashMap<Object, Object>();
        if (this.children != null) {
            for (SimpleNode child : this.children) {
                AstMapEntry mapEntry = (AstMapEntry)child;
                Object key = mapEntry.children[0].getValue(ctx);
                Object value = mapEntry.children[1].getValue(ctx);
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public Class<?> getType(EvaluationContext ctx) throws ELException {
        return Map.class;
    }
}

