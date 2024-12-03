/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;

public class ZapfDingbatsList
extends List {
    protected int zn;

    public ZapfDingbatsList(int zn) {
        super(true);
        this.zn = zn;
        float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }

    public ZapfDingbatsList(int zn, int symbolIndent) {
        super(true, symbolIndent);
        this.zn = zn;
        float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }

    public void setCharNumber(int zn) {
        this.zn = zn;
    }

    public int getCharNumber() {
        return this.zn;
    }

    public boolean add(Object o) {
        if (o instanceof ListItem) {
            ListItem item = (ListItem)o;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            chunk.append(String.valueOf((char)this.zn));
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

