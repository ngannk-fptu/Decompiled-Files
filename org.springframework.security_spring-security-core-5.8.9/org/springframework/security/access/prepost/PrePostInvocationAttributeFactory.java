/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopInfrastructureBean
 */
package org.springframework.security.access.prepost;

import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;

public interface PrePostInvocationAttributeFactory
extends AopInfrastructureBean {
    public PreInvocationAttribute createPreInvocationAttribute(String var1, String var2, String var3);

    public PostInvocationAttribute createPostInvocationAttribute(String var1, String var2);
}

