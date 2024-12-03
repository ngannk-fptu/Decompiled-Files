/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

import com.atlassian.renderer.wysiwyg.NodeContext;
import com.atlassian.renderer.wysiwyg.converter.DefaultWysiwygConverter;

interface Converter {
    public boolean canConvert(NodeContext var1);

    public String convertNode(NodeContext var1, DefaultWysiwygConverter var2);
}

