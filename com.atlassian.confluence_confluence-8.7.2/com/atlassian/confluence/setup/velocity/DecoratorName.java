/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.velocity;

public class DecoratorName {
    private final String spaceKey;
    private final String templateName;

    public DecoratorName(String spaceKey, String templateName) {
        this.spaceKey = spaceKey;
        this.templateName = this.stripLeadingSlash(templateName);
    }

    public DecoratorName(String source) {
        if (DecoratorName.isSpaceDecoratorSource(source)) {
            this.spaceKey = this.parseSpaceKey(source);
            this.templateName = this.parseTemplateName(source);
        } else {
            this.spaceKey = null;
            this.templateName = this.stripLeadingSlash(source);
        }
    }

    private String parseTemplateName(String source) {
        if (!DecoratorName.isSpaceDecoratorSource(source)) {
            throw new IllegalArgumentException("Cannot parse source [" + source + "] as a space decorator");
        }
        return source.substring(source.indexOf("/") + 1);
    }

    private String parseSpaceKey(String source) {
        if (!DecoratorName.isSpaceDecoratorSource(source)) {
            throw new IllegalArgumentException("Cannot parse source [" + source + "] as a space decorator");
        }
        return source.substring(1, source.indexOf("/"));
    }

    public static boolean isSpaceDecoratorSource(String source) {
        return source.startsWith("@");
    }

    private String stripLeadingSlash(String source) {
        String result = source;
        if (result.startsWith("/")) {
            result = result.substring(1);
        }
        return result;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public boolean isGlobalDecorator() {
        return this.spaceKey == null;
    }

    public boolean isSpaceDecorator() {
        return !this.isGlobalDecorator();
    }

    public String getSource() {
        if (this.isSpaceDecorator()) {
            return "@" + this.spaceKey + "/" + this.templateName;
        }
        return "/" + this.templateName;
    }

    public String toString() {
        return "{spaceKey: " + this.spaceKey + ", templateName: " + this.templateName + "}";
    }
}

