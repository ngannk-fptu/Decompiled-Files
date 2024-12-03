/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 */
package com.atlassian.spring.hosted;

import java.net.URL;
import org.springframework.beans.BeansException;

public class HostedOverrideNotAllowedException
extends BeansException {
    public HostedOverrideNotAllowedException(String bean, URL url) {
        super("Hosted override in " + url + " trying to override bean " + bean + " but bean is not overridable");
    }
}

