/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.beanutils;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.configuration2.beanutils.ConstructorArg;

public interface BeanDeclaration {
    public String getBeanFactoryName();

    public Object getBeanFactoryParameter();

    public String getBeanClassName();

    public Map<String, Object> getBeanProperties();

    public Map<String, Object> getNestedBeanDeclarations();

    public Collection<ConstructorArg> getConstructorArgs();
}

