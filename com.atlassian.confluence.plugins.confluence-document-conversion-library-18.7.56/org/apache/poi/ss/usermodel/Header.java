/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.HeaderFooter;

public interface Header
extends HeaderFooter {
    @Override
    public String getLeft();

    @Override
    public void setLeft(String var1);

    @Override
    public String getCenter();

    @Override
    public void setCenter(String var1);

    @Override
    public String getRight();

    @Override
    public void setRight(String var1);
}

