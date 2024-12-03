/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain.DenormalisedSidType;
import java.io.Serializable;

public class DenormalisedSid
implements Serializable,
NotExportable {
    public static final String TABLE_NAME = "DENORMALISED_SID";
    private Long id;
    private String name;
    private DenormalisedSidType type;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DenormalisedSidType getType() {
        return this.type;
    }

    public void setType(DenormalisedSidType type) {
        this.type = type;
    }
}

