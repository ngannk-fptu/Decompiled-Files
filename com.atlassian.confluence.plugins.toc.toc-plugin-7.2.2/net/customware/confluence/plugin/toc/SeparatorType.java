/*
 * Decompiled with CFR 0.152.
 */
package net.customware.confluence.plugin.toc;

public enum SeparatorType {
    BRACKET("bracket", "[ ", " ] [ ", " ]"),
    BRACKETS("brackets", "[ ", " ] [ ", " ]"),
    BRACE("brace", "{ ", " } { ", " }"),
    BRACES("braces", "{ ", " } { ", " }"),
    PAREN("paren", "( ", " ) ( ", " )"),
    PARENS("parens", "( ", " ) ( ", " )"),
    PIPE("pipe", "", " | ", ""),
    PIPES("pipes", "", " | ", ""),
    COMMA("comma", "", " , ", "");

    private final String name;
    private final String pre;
    private final String mid;
    private final String post;

    private SeparatorType(String name, String pre, String mid, String post) {
        this.name = name;
        this.pre = pre;
        this.mid = mid;
        this.post = post;
    }

    public static SeparatorType valueOfSeparator(String separatorName) {
        SeparatorType separatorType = null;
        for (SeparatorType type : SeparatorType.values()) {
            if (!type.toString().equalsIgnoreCase(separatorName)) continue;
            separatorType = type;
            break;
        }
        return separatorType;
    }

    public String getPre() {
        return this.pre;
    }

    public String getMid() {
        return this.mid;
    }

    public String getPost() {
        return this.post;
    }

    public String toString() {
        return this.name;
    }
}

