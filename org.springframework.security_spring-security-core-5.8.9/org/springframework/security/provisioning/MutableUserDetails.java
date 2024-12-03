/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.security.provisioning;

import org.springframework.security.core.userdetails.UserDetails;

interface MutableUserDetails
extends UserDetails {
    public void setPassword(String var1);
}

