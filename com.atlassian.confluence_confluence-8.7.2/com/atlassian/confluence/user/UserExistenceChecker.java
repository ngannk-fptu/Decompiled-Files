/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface UserExistenceChecker {
    public boolean exists(String var1);
}

