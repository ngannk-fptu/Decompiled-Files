/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cglib.transform.impl;

import java.util.Map;
import org.springframework.asm.Type;
import org.springframework.cglib.core.EmitUtils;
import org.springframework.cglib.core.TypeUtils;
import org.springframework.cglib.transform.ClassEmitterTransformer;

public class AddPropertyTransformer
extends ClassEmitterTransformer {
    private final String[] names;
    private final Type[] types;

    public AddPropertyTransformer(Map props) {
        int size = props.size();
        this.names = props.keySet().toArray(new String[size]);
        this.types = new Type[size];
        for (int i2 = 0; i2 < size; ++i2) {
            this.types[i2] = (Type)props.get(this.names[i2]);
        }
    }

    public AddPropertyTransformer(String[] names, Type[] types) {
        this.names = names;
        this.types = types;
    }

    public void end_class() {
        if (!TypeUtils.isAbstract(this.getAccess())) {
            EmitUtils.add_properties(this, this.names, this.types);
        }
        super.end_class();
    }
}

