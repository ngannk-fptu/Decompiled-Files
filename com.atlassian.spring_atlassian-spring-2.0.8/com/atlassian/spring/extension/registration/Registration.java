/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 */
package com.atlassian.spring.extension.registration;

import com.atlassian.spring.extension.registration.RegistrationException;
import org.springframework.beans.factory.BeanFactory;

interface Registration {
    public void register(BeanFactory var1) throws RegistrationException;
}

