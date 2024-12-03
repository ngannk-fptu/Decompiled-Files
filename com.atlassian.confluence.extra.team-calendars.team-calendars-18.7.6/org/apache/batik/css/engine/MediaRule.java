/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.SACMediaList
 */
package org.apache.batik.css.engine;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.StyleSheet;
import org.w3c.css.sac.SACMediaList;

public class MediaRule
extends StyleSheet
implements Rule {
    public static final short TYPE = 1;
    protected SACMediaList mediaList;

    @Override
    public short getType() {
        return 1;
    }

    public void setMediaList(SACMediaList ml) {
        this.mediaList = ml;
    }

    public SACMediaList getMediaList() {
        return this.mediaList;
    }

    @Override
    public String toString(CSSEngine eng) {
        int i;
        StringBuffer sb = new StringBuffer();
        sb.append("@media");
        if (this.mediaList != null) {
            for (i = 0; i < this.mediaList.getLength(); ++i) {
                sb.append(' ');
                sb.append(this.mediaList.item(i));
            }
        }
        sb.append(" {\n");
        for (i = 0; i < this.size; ++i) {
            sb.append(this.rules[i].toString(eng));
        }
        sb.append("}\n");
        return sb.toString();
    }
}

