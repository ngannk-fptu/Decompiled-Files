/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.SACMediaList;

public class CSSSACMediaList
implements SACMediaList {
    protected String[] list = new String[3];
    protected int length;

    @Override
    public int getLength() {
        return this.length;
    }

    @Override
    public String item(int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        return this.list[index];
    }

    public void append(String item) {
        if (this.length == this.list.length) {
            String[] tmp = this.list;
            this.list = new String[1 + this.list.length + this.list.length / 2];
            System.arraycopy(tmp, 0, this.list, 0, tmp.length);
        }
        this.list[this.length++] = item;
    }
}

