/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tiles.TilesException
 *  org.apache.tiles.preparer.ViewPreparer
 */
package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.TilesException;
import org.apache.tiles.preparer.ViewPreparer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.tiles3.AbstractSpringPreparerFactory;

public class SpringBeanPreparerFactory
extends AbstractSpringPreparerFactory {
    @Override
    protected ViewPreparer getPreparer(String name, WebApplicationContext context) throws TilesException {
        return context.getBean(name, ViewPreparer.class);
    }
}

