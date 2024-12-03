/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;
import javax.annotation.Nullable;

public abstract class SanitizedType
extends PrimitiveType {
    public abstract SanitizedContent.ContentKind getContentKind();

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof SanitizedContent && ((SanitizedContent)value).getContentKind() == this.getContentKind();
    }

    public String toString() {
        return this.getContentKind().toString().toLowerCase();
    }

    public static SanitizedType getTypeForContentKind(@Nullable SanitizedContent.ContentKind contentKind) {
        if (contentKind == null) {
            return null;
        }
        switch (contentKind) {
            case ATTRIBUTES: {
                return AttributesType.getInstance();
            }
            case CSS: {
                return CssType.getInstance();
            }
            case HTML: {
                return HtmlType.getInstance();
            }
            case JS: {
                return JsType.getInstance();
            }
            case URI: {
                return UriType.getInstance();
            }
        }
        return null;
    }

    public static final class JsType
    extends SanitizedType {
        private static final JsType INSTANCE = new JsType();

        private JsType() {
        }

        @Override
        public SoyType.Kind getKind() {
            return SoyType.Kind.JS;
        }

        @Override
        public SanitizedContent.ContentKind getContentKind() {
            return SanitizedContent.ContentKind.JS;
        }

        public static JsType getInstance() {
            return INSTANCE;
        }
    }

    public static final class CssType
    extends SanitizedType {
        private static final CssType INSTANCE = new CssType();

        private CssType() {
        }

        @Override
        public SoyType.Kind getKind() {
            return SoyType.Kind.CSS;
        }

        @Override
        public SanitizedContent.ContentKind getContentKind() {
            return SanitizedContent.ContentKind.CSS;
        }

        public static CssType getInstance() {
            return INSTANCE;
        }
    }

    public static final class UriType
    extends SanitizedType {
        private static final UriType INSTANCE = new UriType();

        private UriType() {
        }

        @Override
        public SoyType.Kind getKind() {
            return SoyType.Kind.URI;
        }

        @Override
        public SanitizedContent.ContentKind getContentKind() {
            return SanitizedContent.ContentKind.URI;
        }

        public static UriType getInstance() {
            return INSTANCE;
        }
    }

    public static final class AttributesType
    extends SanitizedType {
        private static final AttributesType INSTANCE = new AttributesType();

        private AttributesType() {
        }

        @Override
        public SoyType.Kind getKind() {
            return SoyType.Kind.ATTRIBUTES;
        }

        @Override
        public SanitizedContent.ContentKind getContentKind() {
            return SanitizedContent.ContentKind.ATTRIBUTES;
        }

        public static AttributesType getInstance() {
            return INSTANCE;
        }
    }

    public static final class HtmlType
    extends SanitizedType {
        private static final HtmlType INSTANCE = new HtmlType();

        private HtmlType() {
        }

        @Override
        public SoyType.Kind getKind() {
            return SoyType.Kind.HTML;
        }

        @Override
        public SanitizedContent.ContentKind getContentKind() {
            return SanitizedContent.ContentKind.HTML;
        }

        public static HtmlType getInstance() {
            return INSTANCE;
        }
    }
}

