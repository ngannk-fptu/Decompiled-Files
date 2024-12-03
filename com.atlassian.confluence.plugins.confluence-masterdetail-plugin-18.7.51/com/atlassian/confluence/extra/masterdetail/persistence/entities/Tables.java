/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail.persistence.entities;

import com.atlassian.confluence.extra.masterdetail.persistence.entities.BodyContentTable;
import org.springframework.stereotype.Component;

@Component
public class Tables {
    public static final BodyContentTable BODY_CONTENT_TABLE = new BodyContentTable("bodycontent");
}

