/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.framework.AopInfrastructureBean
 */
package org.springframework.security.access;

import java.util.Collection;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.security.access.ConfigAttribute;

public interface SecurityMetadataSource
extends AopInfrastructureBean {
    public Collection<ConfigAttribute> getAttributes(Object var1) throws IllegalArgumentException;

    public Collection<ConfigAttribute> getAllConfigAttributes();

    public boolean supports(Class<?> var1);
}

