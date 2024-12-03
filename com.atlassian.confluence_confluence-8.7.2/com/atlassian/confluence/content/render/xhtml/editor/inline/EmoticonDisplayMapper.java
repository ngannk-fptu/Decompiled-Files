/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.inline;

import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;

public interface EmoticonDisplayMapper {
    public String getRelativeImageUrl(Emoticon var1);

    public String getAbsoluteImageUrl(Emoticon var1);

    public String getImageName(Emoticon var1);
}

