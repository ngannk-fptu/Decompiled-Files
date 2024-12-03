/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.parsing.ParseState;

public class BeanEntry
implements ParseState.Entry {
    private final String beanDefinitionName;

    public BeanEntry(String beanDefinitionName) {
        this.beanDefinitionName = beanDefinitionName;
    }

    public String toString() {
        return "Bean '" + this.beanDefinitionName + "'";
    }
}

