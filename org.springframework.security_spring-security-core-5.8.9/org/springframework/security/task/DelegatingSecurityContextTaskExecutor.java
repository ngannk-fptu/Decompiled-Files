/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.TaskExecutor
 */
package org.springframework.security.task;

import java.util.concurrent.Executor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutor;
import org.springframework.security.core.context.SecurityContext;

public class DelegatingSecurityContextTaskExecutor
extends DelegatingSecurityContextExecutor
implements TaskExecutor {
    public DelegatingSecurityContextTaskExecutor(TaskExecutor delegateTaskExecutor, SecurityContext securityContext) {
        super((Executor)delegateTaskExecutor, securityContext);
    }

    public DelegatingSecurityContextTaskExecutor(TaskExecutor delegate) {
        this(delegate, null);
    }
}

