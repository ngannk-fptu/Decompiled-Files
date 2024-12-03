/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.dialog.wizard.api;

public class Dialog {
    private String id;
    private String title;
    private int width;
    private int height;
    private String classNames;

    public Dialog(String id, String title, int width, int height, String classNames) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
        this.classNames = classNames;
    }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getClassNames() {
        return this.classNames;
    }
}

