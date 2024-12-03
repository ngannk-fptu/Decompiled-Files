/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.Table
 */
package com.atlassian.plugins.navlink.producer.contentlinks.customcontentlink;

import net.java.ao.Entity;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.Table;

@Table(value="CustomContentLink")
public interface CustomContentLinkAO
extends Entity {
    public String getLinkUrl();

    public void setLinkUrl(String var1);

    @Indexed
    public String getContentKey();

    public void setContentKey(String var1);

    public String getLinkLabel();

    public void setLinkLabel(String var1);

    public int getSequence();

    public void setSequence(int var1);
}

