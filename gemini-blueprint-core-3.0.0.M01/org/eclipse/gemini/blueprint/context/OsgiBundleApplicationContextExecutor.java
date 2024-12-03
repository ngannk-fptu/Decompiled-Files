/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 */
package org.eclipse.gemini.blueprint.context;

import org.springframework.beans.BeansException;

public interface OsgiBundleApplicationContextExecutor {
    public void refresh() throws BeansException, IllegalStateException;

    public void close();
}

