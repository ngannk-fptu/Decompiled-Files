/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.jssrc.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.template.soy.base.SoyBackendKind;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.types.SoyEnumType;
import com.google.template.soy.types.SoyObjectType;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.aggregate.ListType;
import com.google.template.soy.types.aggregate.MapType;
import com.google.template.soy.types.aggregate.RecordType;
import com.google.template.soy.types.aggregate.UnionType;
import com.google.template.soy.types.primitive.SanitizedType;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;

public class JsSrcUtils {
    private static final ImmutableSet<String> JS_RESERVED_WORDS = ImmutableSet.of((Object)"break", (Object)"case", (Object)"catch", (Object)"class", (Object)"const", (Object)"continue", (Object[])new String[]{"debugger", "default", "delete", "do", "else", "enum", "export", "extends", "false", "finally", "for", "function", "if", "implements", "import", "in", "instanceof", "interface", "let", "null", "new", "package", "private", "protected", "public", "return", "soy", "soydata", "static", "super", "switch", "this", "throw", "true", "try", "typeof", "var", "void", "while", "with", "yield"});

    private JsSrcUtils() {
    }

    public static String escapeUnicodeFormatChars(String str) {
        int codePoint;
        int len = str.length();
        boolean hasFormatChar = false;
        for (int i = 0; i < len; ++i) {
            if (Character.getType(str.charAt(i)) != 16) continue;
            hasFormatChar = true;
            break;
        }
        if (!hasFormatChar) {
            return str;
        }
        StringBuilder out = new StringBuilder(len * 4 / 3);
        for (int i = 0; i < len; i += Character.charCount(codePoint)) {
            codePoint = str.codePointAt(i);
            if (Character.getType(codePoint) == 16) {
                BaseUtils.appendHexEscape(out, codePoint);
                continue;
            }
            out.appendCodePoint(codePoint);
        }
        return out.toString();
    }

    public static String getJsTypeExpr(SoyType type) {
        return JsSrcUtils.getJsTypeExpr(type, false, true);
    }

    public static String getJsTypeExpr(SoyType type, boolean addParensIfNeeded, boolean addRequiredIfNeeded) {
        String nonNullablePrefix = addRequiredIfNeeded ? "!" : "";
        switch (type.getKind()) {
            case ANY: {
                return "*";
            }
            case UNKNOWN: {
                return "?";
            }
            case NULL: {
                return "null";
            }
            case BOOL: {
                return "boolean";
            }
            case STRING: {
                return "string";
            }
            case INT: 
            case FLOAT: {
                return "number";
            }
            case LIST: {
                ListType listType = (ListType)type;
                if (listType.getElementType().getKind() == SoyType.Kind.ANY) {
                    return nonNullablePrefix + "Array";
                }
                return nonNullablePrefix + "Array.<" + JsSrcUtils.getJsTypeExpr(listType.getElementType(), false, true) + ">";
            }
            case MAP: {
                MapType mapType = (MapType)type;
                if (mapType.getKeyType().getKind() == SoyType.Kind.ANY && mapType.getValueType().getKind() == SoyType.Kind.ANY) {
                    return nonNullablePrefix + "Object.<?,?>";
                }
                String keyTypeName = JsSrcUtils.getJsTypeExpr(mapType.getKeyType(), false, true);
                String valueTypeName = JsSrcUtils.getJsTypeExpr(mapType.getValueType(), false, true);
                return nonNullablePrefix + "Object.<" + keyTypeName + "," + valueTypeName + ">";
            }
            case RECORD: {
                RecordType recordType = (RecordType)type;
                ArrayList members = Lists.newArrayListWithExpectedSize((int)recordType.getMembers().size());
                for (Map.Entry member : recordType.getMembers().entrySet()) {
                    members.add((String)member.getKey() + ": " + JsSrcUtils.getJsTypeExpr((SoyType)member.getValue(), true, true));
                }
                return "{" + Joiner.on((String)", ").join((Iterable)members) + "}";
            }
            case UNION: {
                UnionType unionType = (UnionType)type;
                TreeSet typeNames = Sets.newTreeSet();
                boolean isNullable = unionType.isNullable();
                boolean hasNullableMember = false;
                for (SoyType memberType : unionType.getMembers()) {
                    if (memberType.getKind() == SoyType.Kind.NULL) continue;
                    if (memberType instanceof SanitizedType) {
                        typeNames.add(JsSrcUtils.getJsTypeName(memberType));
                        typeNames.add("string");
                        hasNullableMember = true;
                        continue;
                    }
                    if (JsSrcUtils.isDefaultOptional(memberType)) {
                        hasNullableMember = true;
                    }
                    typeNames.add(JsSrcUtils.getJsTypeExpr(memberType, false, !isNullable));
                }
                if (isNullable && !hasNullableMember) {
                    typeNames.add("null");
                }
                if (isNullable) {
                    typeNames.add("undefined");
                }
                if (typeNames.size() != 1) {
                    String result = Joiner.on((String)"|").join((Iterable)typeNames);
                    if (addParensIfNeeded) {
                        result = "(" + result + ")";
                    }
                    return result;
                }
                return (String)typeNames.first();
            }
        }
        if (type instanceof SanitizedType) {
            String result = NodeContentKinds.toJsSanitizedContentCtorName(((SanitizedType)type).getContentKind()) + "|string";
            if (addParensIfNeeded) {
                result = "(" + result + ")";
            }
            return result;
        }
        return JsSrcUtils.getJsTypeName(type);
    }

    public static String getJsTypeName(SoyType type) {
        if (type instanceof SanitizedType) {
            return NodeContentKinds.toJsSanitizedContentCtorName(((SanitizedType)type).getContentKind());
        }
        if (type.getKind() == SoyType.Kind.OBJECT) {
            return ((SoyObjectType)type).getNameForBackend(SoyBackendKind.JS_SRC);
        }
        if (type.getKind() == SoyType.Kind.ENUM) {
            return ((SoyEnumType)type).getNameForBackend(SoyBackendKind.JS_SRC);
        }
        throw new AssertionError((Object)("Unsupported type: " + type));
    }

    public static boolean isDefaultOptional(SoyType type) {
        switch (type.getKind()) {
            case LIST: 
            case MAP: 
            case OBJECT: {
                return true;
            }
        }
        return type instanceof SanitizedType;
    }

    public static boolean isReservedWord(String key) {
        return JS_RESERVED_WORDS.contains((Object)key);
    }
}

