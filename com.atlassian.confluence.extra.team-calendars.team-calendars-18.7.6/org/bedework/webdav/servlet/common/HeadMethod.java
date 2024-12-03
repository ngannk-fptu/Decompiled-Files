/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.common;

import org.bedework.webdav.servlet.common.GetMethod;

public class HeadMethod
extends GetMethod {
    @Override
    public void init() {
        this.doContent = false;
    }
}

