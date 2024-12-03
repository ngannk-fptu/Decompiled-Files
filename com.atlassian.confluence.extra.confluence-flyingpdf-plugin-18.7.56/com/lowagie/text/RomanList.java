/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.factories.RomanNumberFactory;

public class RomanList
extends List {
    public RomanList() {
        super(true);
    }

    public RomanList(int symbolIndent) {
        super(true, symbolIndent);
    }

    public RomanList(boolean lowercase, int symbolIndent) {
        super(true, symbolIndent);
        this.lowercase = lowercase;
    }

    public boolean add(Object o) {
        if (o instanceof ListItem) {
            ListItem item = (ListItem)o;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(RomanNumberFactory.getString(this.first + this.list.size(), this.lowercase));
            chunk.append(this.postSymbol);
            item.setListSymbol(chunk);
            item.setIndentationLeft(this.symbolIndent, this.autoindent);
            item.setIndentationRight(0.0f);
            this.list.add(item);
        } else {
            if (o instanceof List) {
                List nested = (List)o;
                nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
                --this.first;
                return this.list.add(nested);
            }
            if (o instanceof String) {
                return this.add(new ListItem((String)o));
            }
        }
        return false;
    }
}

