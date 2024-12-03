/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.wysiwyg.converter;

interface Separation {
    public static final Separation ALWAYS_NEWLINE = new Separation(){

        @Override
        public String getSeparator() {
            return "\n";
        }

        @Override
        public String getTableSeparator() {
            return "\n";
        }

        @Override
        public String getListSeparator() {
            return "\n";
        }
    };
    public static final Separation ALWAYS_EMPTY = new Separation(){

        @Override
        public String getSeparator() {
            return "";
        }

        @Override
        public String getTableSeparator() {
            return "";
        }

        @Override
        public String getListSeparator() {
            return "";
        }
    };

    public String getSeparator();

    public String getTableSeparator();

    public String getListSeparator();
}

