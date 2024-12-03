/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.types;

import nonapi.io.github.classgraph.types.Parser;

public final class TypeUtils {
    private TypeUtils() {
    }

    public static boolean getIdentifierToken(Parser parser, boolean stopAtDollarSign) {
        boolean consumedChar = false;
        while (parser.hasMore()) {
            char c = parser.peek();
            if (c == '/') {
                parser.appendToToken('.');
                parser.next();
                consumedChar = true;
                continue;
            }
            if (c == ';' || c == '[' || c == '<' || c == '>' || c == ':' || stopAtDollarSign && c == '$') break;
            parser.appendToToken(c);
            parser.next();
            consumedChar = true;
        }
        return consumedChar;
    }

    private static void appendModifierKeyword(StringBuilder buf, String modifierKeyword) {
        if (buf.length() > 0 && buf.charAt(buf.length() - 1) != ' ') {
            buf.append(' ');
        }
        buf.append(modifierKeyword);
    }

    public static void modifiersToString(int modifiers, ModifierType modifierType, boolean isDefault, StringBuilder buf) {
        if ((modifiers & 1) != 0) {
            TypeUtils.appendModifierKeyword(buf, "public");
        } else if ((modifiers & 2) != 0) {
            TypeUtils.appendModifierKeyword(buf, "private");
        } else if ((modifiers & 4) != 0) {
            TypeUtils.appendModifierKeyword(buf, "protected");
        }
        if (modifierType != ModifierType.FIELD && (modifiers & 0x400) != 0) {
            TypeUtils.appendModifierKeyword(buf, "abstract");
        }
        if ((modifiers & 8) != 0) {
            TypeUtils.appendModifierKeyword(buf, "static");
        }
        if (modifierType == ModifierType.FIELD) {
            if ((modifiers & 0x40) != 0) {
                TypeUtils.appendModifierKeyword(buf, "volatile");
            }
            if ((modifiers & 0x80) != 0) {
                TypeUtils.appendModifierKeyword(buf, "transient");
            }
        }
        if ((modifiers & 0x10) != 0) {
            TypeUtils.appendModifierKeyword(buf, "final");
        }
        if (modifierType == ModifierType.METHOD) {
            if ((modifiers & 0x20) != 0) {
                TypeUtils.appendModifierKeyword(buf, "synchronized");
            }
            if (isDefault) {
                TypeUtils.appendModifierKeyword(buf, "default");
            }
        }
        if ((modifiers & 0x1000) != 0) {
            TypeUtils.appendModifierKeyword(buf, "synthetic");
        }
        if (modifierType != ModifierType.FIELD && (modifiers & 0x40) != 0) {
            TypeUtils.appendModifierKeyword(buf, "bridge");
        }
        if (modifierType == ModifierType.METHOD && (modifiers & 0x100) != 0) {
            TypeUtils.appendModifierKeyword(buf, "native");
        }
        if (modifierType != ModifierType.FIELD && (modifiers & 0x800) != 0) {
            TypeUtils.appendModifierKeyword(buf, "strictfp");
        }
    }

    public static enum ModifierType {
        CLASS,
        METHOD,
        FIELD;

    }
}

