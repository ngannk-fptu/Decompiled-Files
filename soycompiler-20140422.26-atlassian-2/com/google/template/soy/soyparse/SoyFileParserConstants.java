/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soyparse;

public interface SoyFileParserConstants {
    public static final int EOF = 0;
    public static final int DELPACKAGE_TAG = 1;
    public static final int XXX_DELPACKAGE_TAG_NOT_SOL = 2;
    public static final int XXX_DELPACKAGE_TAG_NOT_EOL = 3;
    public static final int XXX_DELPACKAGE_INVALID = 4;
    public static final int NAMESPACE_TAG = 5;
    public static final int XXX_NAMESPACE_TAG_NOT_SOL = 6;
    public static final int XXX_NAMESPACE_TAG_NOT_EOL = 7;
    public static final int XXX_NAMESPACE_INVALID = 8;
    public static final int ALIAS_TAG = 9;
    public static final int XXX_ALIAS_TAG_NOT_SOL = 10;
    public static final int XXX_ALIAS_TAG_NOT_EOL = 11;
    public static final int XXX_ALIAS_INVALID = 12;
    public static final int SOY_DOC_START = 13;
    public static final int SOY_DOC_NEWLINE = 14;
    public static final int SOY_DOC_CHAR = 15;
    public static final int SOY_DOC_END = 16;
    public static final int XXX_SOY_DOC_START_NOT_SOL = 17;
    public static final int XXX_SOY_DOC_END_NOT_EOL = 18;
    public static final int ANY_TEMPLATE_TAG = 19;
    public static final int TEMPLATE_CONTENT_CHAR = 20;
    public static final int TEMPLATE_CONTENT = 21;
    public static final int XXX_TEMPLATE_TAG_NOT_SOL = 22;
    public static final int XXX_TEMPLATE_TAG_NOT_EOL = 23;
    public static final int XXX_END_TEMPLATE_TAG_NOT_SOL = 24;
    public static final int XXX_END_TEMPLATE_TAG_NOT_EOL = 25;
    public static final int XXX_TEMPLATE_TAG_INVALID = 26;
    public static final int XXX_END_TEMPLATE_TAG_INVALID = 27;
    public static final int ANY_TEMPLATE_CMD_NAME = 28;
    public static final int SKIPPED_NEWLINE = 29;
    public static final int SKIPPED_CHAR = 30;
    public static final int ANY_CHAR = 31;
    public static final int WS = 32;
    public static final int NEWLINE = 33;
    public static final int WS_UNTIL_EOL = 34;
    public static final int NOT_SOL = 35;
    public static final int NOT_EOL = 36;
    public static final int NOT_BRACE = 37;
    public static final int IDENT = 38;
    public static final int DOTTED_IDENT = 39;
    public static final int DEFAULT = 0;
    public static final int IN_TEMPLATE = 1;
    public static final int IN_SOY_DOC = 2;
    public static final String[] tokenImage = new String[]{"<EOF>", "<DELPACKAGE_TAG>", "<XXX_DELPACKAGE_TAG_NOT_SOL>", "<XXX_DELPACKAGE_TAG_NOT_EOL>", "\"{delpackage\"", "<NAMESPACE_TAG>", "<XXX_NAMESPACE_TAG_NOT_SOL>", "<XXX_NAMESPACE_TAG_NOT_EOL>", "\"{namespace\"", "<ALIAS_TAG>", "<XXX_ALIAS_TAG_NOT_SOL>", "<XXX_ALIAS_TAG_NOT_EOL>", "\"{alias\"", "\"/**\"", "<SOY_DOC_NEWLINE>", "<SOY_DOC_CHAR>", "<SOY_DOC_END>", "<XXX_SOY_DOC_START_NOT_SOL>", "<XXX_SOY_DOC_END_NOT_EOL>", "<ANY_TEMPLATE_TAG>", "<TEMPLATE_CONTENT_CHAR>", "<TEMPLATE_CONTENT>", "<XXX_TEMPLATE_TAG_NOT_SOL>", "<XXX_TEMPLATE_TAG_NOT_EOL>", "<XXX_END_TEMPLATE_TAG_NOT_SOL>", "<XXX_END_TEMPLATE_TAG_NOT_EOL>", "<XXX_TEMPLATE_TAG_INVALID>", "<XXX_END_TEMPLATE_TAG_INVALID>", "<ANY_TEMPLATE_CMD_NAME>", "<SKIPPED_NEWLINE>", "<SKIPPED_CHAR>", "<ANY_CHAR>", "<WS>", "<NEWLINE>", "<WS_UNTIL_EOL>", "<NOT_SOL>", "<NOT_EOL>", "<NOT_BRACE>", "<IDENT>", "<DOTTED_IDENT>"};
}

