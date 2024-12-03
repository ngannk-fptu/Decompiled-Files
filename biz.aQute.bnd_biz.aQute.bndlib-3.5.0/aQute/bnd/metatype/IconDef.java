/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.lib.tag.Tag;

public class IconDef {
    String resource;
    int size;

    public IconDef(String resource, int size) {
        this.resource = resource;
        this.size = size;
    }

    Tag getTag() {
        Tag icon = new Tag("Icon", new Object[0]).addAttribute("resource", this.resource).addAttribute("size", this.size);
        return icon;
    }
}

