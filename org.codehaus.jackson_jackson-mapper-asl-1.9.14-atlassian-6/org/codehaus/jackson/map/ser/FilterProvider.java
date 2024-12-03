/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import org.codehaus.jackson.map.ser.BeanPropertyFilter;

public abstract class FilterProvider {
    public abstract BeanPropertyFilter findFilter(Object var1);
}

