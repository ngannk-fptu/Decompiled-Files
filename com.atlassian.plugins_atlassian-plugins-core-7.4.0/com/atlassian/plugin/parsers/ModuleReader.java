/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.dom4j.CharacterData
 *  org.dom4j.Element
 */
package com.atlassian.plugin.parsers;

import com.google.common.base.Preconditions;
import org.dom4j.CharacterData;
import org.dom4j.Element;

public final class ModuleReader {
    private final Element module;

    public ModuleReader(Element module) {
        this.module = (Element)Preconditions.checkNotNull((Object)module);
        Preconditions.checkState((!(module instanceof CharacterData) ? 1 : 0) != 0, (Object)"Module elements cannot be text nodes!");
    }

    public String getType() {
        return this.module.getName();
    }
}

