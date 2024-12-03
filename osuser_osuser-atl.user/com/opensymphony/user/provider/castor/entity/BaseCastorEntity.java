/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.user.provider.castor.entity;

import com.opensymphony.user.provider.castor.entity.CastorEntity;
import java.math.BigDecimal;

public class BaseCastorEntity
implements CastorEntity {
    private BigDecimal id = null;
    private String name = null;

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

