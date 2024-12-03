/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.Problem;

public class BeanDefinitionParsingException
extends BeanDefinitionStoreException {
    public BeanDefinitionParsingException(Problem problem) {
        super(problem.getResourceDescription(), problem.toString(), problem.getRootCause());
    }
}

