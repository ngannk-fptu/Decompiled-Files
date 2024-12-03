/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Chunk;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;

public class ZapfDingbatsNumberList
extends List {
    protected int type;

    public ZapfDingbatsNumberList(int type) {
        super(true);
        this.type = type;
        float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }

    public ZapfDingbatsNumberList(int type, int symbolIndent) {
        super(true, symbolIndent);
        this.type = type;
        float fontsize = this.symbol.getFont().getSize();
        this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
        this.postSymbol = " ";
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public boolean add(Object o) {
        if (o instanceof ListItem) {
            ListItem item = (ListItem)o;
            Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
            switch (this.type) {
                case 0: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 171)));
                    break;
                }
                case 1: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 181)));
                    break;
                }
                case 2: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 191)));
                    break;
                }
                default: {
                    chunk.append(String.valueOf((char)(this.first + this.list.size() + 201)));
                }
            }
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

