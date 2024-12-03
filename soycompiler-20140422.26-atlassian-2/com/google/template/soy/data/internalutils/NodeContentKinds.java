/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableBiMap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.data.internalutils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SanitizedContent;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class NodeContentKinds {
    private static final ImmutableBiMap<String, SanitizedContent.ContentKind> KIND_ATTRIBUTE_TO_SANITIZED_CONTENT_KIND_BI_MAP = ImmutableBiMap.builder().put((Object)"attributes", (Object)SanitizedContent.ContentKind.ATTRIBUTES).put((Object)"css", (Object)SanitizedContent.ContentKind.CSS).put((Object)"html", (Object)SanitizedContent.ContentKind.HTML).put((Object)"js", (Object)SanitizedContent.ContentKind.JS).put((Object)"text", (Object)SanitizedContent.ContentKind.TEXT).put((Object)"uri", (Object)SanitizedContent.ContentKind.URI).build();
    private static final ImmutableMap<SanitizedContent.ContentKind, String> KIND_TO_JS_CTOR_NAME = ImmutableMap.builder().put((Object)SanitizedContent.ContentKind.HTML, (Object)"soydata.SanitizedHtml").put((Object)SanitizedContent.ContentKind.ATTRIBUTES, (Object)"soydata.SanitizedHtmlAttribute").put((Object)SanitizedContent.ContentKind.JS, (Object)"soydata.SanitizedJs").put((Object)SanitizedContent.ContentKind.JS_STR_CHARS, (Object)"soydata.SanitizedJsStrChars").put((Object)SanitizedContent.ContentKind.URI, (Object)"soydata.SanitizedUri").put((Object)SanitizedContent.ContentKind.CSS, (Object)"soydata.SanitizedCss").put((Object)SanitizedContent.ContentKind.TEXT, (Object)"soydata.UnsanitizedText").build();
    private static final ImmutableMap<SanitizedContent.ContentKind, String> KIND_TO_JS_ORDAINER_NAME = ImmutableMap.builder().put((Object)SanitizedContent.ContentKind.HTML, (Object)"soydata.VERY_UNSAFE.ordainSanitizedHtml").put((Object)SanitizedContent.ContentKind.ATTRIBUTES, (Object)"soydata.VERY_UNSAFE.ordainSanitizedHtmlAttribute").put((Object)SanitizedContent.ContentKind.JS, (Object)"soydata.VERY_UNSAFE.ordainSanitizedJs").put((Object)SanitizedContent.ContentKind.URI, (Object)"soydata.VERY_UNSAFE.ordainSanitizedUri").put((Object)SanitizedContent.ContentKind.CSS, (Object)"soydata.VERY_UNSAFE.ordainSanitizedCss").put((Object)SanitizedContent.ContentKind.TEXT, (Object)"soydata.markUnsanitizedText").build();
    private static final ImmutableMap<SanitizedContent.ContentKind, String> KIND_TO_JS_ORDAINER_NAME_FOR_INTERNAL_BLOCKS = ImmutableMap.builder().put((Object)SanitizedContent.ContentKind.HTML, (Object)"soydata.VERY_UNSAFE.$$ordainSanitizedHtmlForInternalBlocks").put((Object)SanitizedContent.ContentKind.ATTRIBUTES, (Object)"soydata.VERY_UNSAFE.$$ordainSanitizedAttributesForInternalBlocks").put((Object)SanitizedContent.ContentKind.JS, (Object)"soydata.VERY_UNSAFE.$$ordainSanitizedJsForInternalBlocks").put((Object)SanitizedContent.ContentKind.URI, (Object)"soydata.VERY_UNSAFE.$$ordainSanitizedUriForInternalBlocks").put((Object)SanitizedContent.ContentKind.CSS, (Object)"soydata.VERY_UNSAFE.$$ordainSanitizedCssForInternalBlocks").put((Object)SanitizedContent.ContentKind.TEXT, (Object)"soydata.$$markUnsanitizedTextForInternalBlocks").build();

    public static SanitizedContent.ContentKind forAttributeValue(String attributeValue) {
        return (SanitizedContent.ContentKind)((Object)KIND_ATTRIBUTE_TO_SANITIZED_CONTENT_KIND_BI_MAP.get((Object)attributeValue));
    }

    public static String toAttributeValue(SanitizedContent.ContentKind kind) {
        return (String)KIND_ATTRIBUTE_TO_SANITIZED_CONTENT_KIND_BI_MAP.inverse().get((Object)kind);
    }

    public static Set<String> getAttributeValues() {
        return KIND_ATTRIBUTE_TO_SANITIZED_CONTENT_KIND_BI_MAP.keySet();
    }

    public static String toJsSanitizedContentCtorName(SanitizedContent.ContentKind contentKind) {
        return (String)Preconditions.checkNotNull((Object)KIND_TO_JS_CTOR_NAME.get((Object)contentKind));
    }

    public static String toJsSanitizedContentOrdainer(SanitizedContent.ContentKind contentKind) {
        return (String)Preconditions.checkNotNull((Object)KIND_TO_JS_ORDAINER_NAME.get((Object)contentKind));
    }

    public static String toJsSanitizedContentOrdainerForInternalBlocks(SanitizedContent.ContentKind contentKind) {
        return (String)Preconditions.checkNotNull((Object)KIND_TO_JS_ORDAINER_NAME_FOR_INTERNAL_BLOCKS.get((Object)contentKind));
    }

    private NodeContentKinds() {
    }

    static {
        if (!KIND_TO_JS_CTOR_NAME.keySet().containsAll(EnumSet.allOf(SanitizedContent.ContentKind.class))) {
            throw new AssertionError((Object)"Not all ContentKind enums have a JS constructor");
        }
        ImmutableSet soyContentKinds = KIND_ATTRIBUTE_TO_SANITIZED_CONTENT_KIND_BI_MAP.values();
        if (!KIND_TO_JS_ORDAINER_NAME.keySet().containsAll((Collection)soyContentKinds)) {
            throw new AssertionError((Object)"Not all Soy-accessible ContentKind enums have a JS ordainer");
        }
        if (!KIND_TO_JS_ORDAINER_NAME_FOR_INTERNAL_BLOCKS.keySet().containsAll((Collection)soyContentKinds)) {
            throw new AssertionError((Object)"Not all Soy-accessible ContentKind enums have a JS ordainer");
        }
    }
}

