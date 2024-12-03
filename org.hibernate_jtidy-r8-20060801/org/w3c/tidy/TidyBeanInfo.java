/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class TidyBeanInfo
extends SimpleBeanInfo {
    public Image getIcon(int kind) {
        return this.loadImage("tidy.gif");
    }
}

