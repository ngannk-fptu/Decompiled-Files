/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.beans;

import net.sf.ehcache.statistics.beans.AttributeProxy;

public class DoubleBeanProxy
extends AttributeProxy<Double> {
    public DoubleBeanProxy(String name, String description, boolean isRead, boolean isWrite) {
        super(Double.class, name, description, isRead, isWrite);
    }
}

