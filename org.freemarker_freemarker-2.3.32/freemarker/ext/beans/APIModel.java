/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;

final class APIModel
extends BeanModel {
    APIModel(Object object, BeansWrapper wrapper) {
        super(object, wrapper, false);
    }

    protected boolean isMethodsShadowItems() {
        return true;
    }
}

