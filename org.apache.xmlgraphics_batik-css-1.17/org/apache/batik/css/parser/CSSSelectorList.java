/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Selector
 *  org.w3c.css.sac.SelectorList
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;

public class CSSSelectorList
implements SelectorList {
    protected Selector[] list = new Selector[3];
    protected int length;

    public int getLength() {
        return this.length;
    }

    public Selector item(int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        return this.list[index];
    }

    public void append(Selector item) {
        if (this.length == this.list.length) {
            Selector[] tmp = this.list;
            this.list = new Selector[1 + this.list.length + this.list.length / 2];
            System.arraycopy(tmp, 0, this.list, 0, tmp.length);
        }
        this.list[this.length++] = item;
    }
}

