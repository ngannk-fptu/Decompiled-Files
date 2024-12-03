/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.security;

import com.thoughtworks.xstream.security.TypePermission;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExplicitTypePermission
implements TypePermission {
    final Set names;

    public ExplicitTypePermission(final Class[] types) {
        this(new Object(){

            public String[] getNames() {
                if (types == null) {
                    return null;
                }
                String[] names = new String[types.length];
                for (int i = 0; i < types.length; ++i) {
                    names[i] = types[i].getName();
                }
                return names;
            }
        }.getNames());
    }

    public ExplicitTypePermission(String[] names) {
        this.names = names == null ? Collections.EMPTY_SET : new HashSet<String>(Arrays.asList(names));
    }

    public boolean allows(Class type) {
        if (type == null) {
            return false;
        }
        return this.names.contains(type.getName());
    }
}

