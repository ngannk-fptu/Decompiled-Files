/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.AbstractUUIDGenerator
 */
package com.atlassian.confluence.user.persistence.dao.hibernate;

import java.io.Serializable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.AbstractUUIDGenerator;

public class ConfluenceHexUserKeyGenerator
extends AbstractUUIDGenerator {
    public Serializable generate(SharedSessionContractImplementor session, Object obj) {
        return this.format(this.getIP()) + this.format(this.getJVM()) + this.format(this.getHiTime()) + this.format(this.getLoTime()) + this.format(this.getCount());
    }

    private String format(int intValue) {
        String formatted = Integer.toHexString(intValue);
        StringBuilder buf = new StringBuilder("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }

    private String format(short shortValue) {
        String formatted = Integer.toHexString(shortValue);
        StringBuilder buf = new StringBuilder("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
}

