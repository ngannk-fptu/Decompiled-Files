/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.stereotype.Component;

@Component
public class ComponentImports {
    @ComponentImport
    private UserManager salUserManager;
}

