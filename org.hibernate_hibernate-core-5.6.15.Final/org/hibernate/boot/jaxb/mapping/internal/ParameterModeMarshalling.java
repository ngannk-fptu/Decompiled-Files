/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 */
package org.hibernate.boot.jaxb.mapping.internal;

import javax.persistence.ParameterMode;

public class ParameterModeMarshalling {
    public static ParameterMode fromXml(String name) {
        return ParameterMode.valueOf((String)name);
    }

    public static String toXml(ParameterMode parameterMode) {
        return parameterMode.name();
    }
}

