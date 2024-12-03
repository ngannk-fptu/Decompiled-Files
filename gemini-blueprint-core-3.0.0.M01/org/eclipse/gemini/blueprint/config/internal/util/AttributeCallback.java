/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 */
package org.eclipse.gemini.blueprint.config.internal.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public interface AttributeCallback {
    public boolean process(Element var1, Attr var2, BeanDefinitionBuilder var3);
}

