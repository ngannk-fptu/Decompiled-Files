/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 */
package org.eclipse.gemini.blueprint.service.importer.support;

import org.springframework.beans.factory.DisposableBean;

class DisposableBeanRunnableAdapter
implements Runnable {
    private final DisposableBean bean;

    public DisposableBeanRunnableAdapter(DisposableBean bean) {
        this.bean = bean;
    }

    @Override
    public void run() {
        try {
            this.bean.destroy();
        }
        catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new RuntimeException(ex);
        }
    }
}

