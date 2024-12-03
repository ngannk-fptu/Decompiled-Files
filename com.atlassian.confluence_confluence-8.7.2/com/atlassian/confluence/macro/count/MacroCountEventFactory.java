/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.macro.count;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.count.MacroCount;
import com.atlassian.confluence.macro.count.MacroCountEvent;
import com.atlassian.confluence.pages.Contained;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MacroCountEventFactory {
    private static final String MACRO_CREATE_EVENT_NAME = "confluence.macro.create";

    public static @NonNull MacroCountEvent newMacroCreateCountEvent(Object src, @Nullable ContentEntityObject entity, ConversionContext context, MacroCount macroCount) {
        return new MacroCountEvent(src, StringUtils.trimToEmpty((String)(entity != null ? entity.getIdAsString() : null)), StringUtils.trimToEmpty((String)MacroCountEventFactory.extractContainerEntityId(entity)), StringUtils.trimToEmpty((String)context.getOutputType()), StringUtils.trimToEmpty((String)context.getOutputDeviceType()), StringUtils.trimToEmpty((String)(entity != null ? entity.getType() : null)), macroCount.getCount(), macroCount.getMacroType(), MACRO_CREATE_EVENT_NAME);
    }

    private static String extractContainerEntityId(@Nullable ContentEntityObject entity) {
        if (entity instanceof Contained) {
            Object container = ((Contained)((Object)entity)).getContainer();
            return container != null ? ((ContentEntityObject)container).getIdAsString() : null;
        }
        return null;
    }
}

