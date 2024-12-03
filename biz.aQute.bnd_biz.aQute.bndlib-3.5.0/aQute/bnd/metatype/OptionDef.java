/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.metatype;

import aQute.lib.tag.Tag;

public class OptionDef {
    String label;
    String value;

    public OptionDef(String label, String value) {
        this.label = label;
        this.value = value;
    }

    Tag getTag() {
        Tag option = new Tag("Option", new Object[0]).addAttribute("label", this.label).addAttribute("value", this.value);
        return option;
    }
}

