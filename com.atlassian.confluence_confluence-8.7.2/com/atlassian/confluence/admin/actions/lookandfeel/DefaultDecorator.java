/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.TemplateSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.util.Assert;

public class DefaultDecorator {
    private static final List<DefaultDecorator> DEFAULT_DECORATORS = DefaultDecorator.getDefaultDecorators();
    public static final String TYPE_DECORATOR = "decorator";
    public static final String TYPE_EXPORT = "export";
    public static final String TYPE_CONTENT = "content";
    private final String fileName;
    private final String key;
    private final String type;

    private DefaultDecorator(String type, String key, String fileName) {
        Assert.notNull((Object)type, (String)"type must not be null");
        Assert.notNull((Object)key, (String)"key must not be null");
        Assert.notNull((Object)fileName, (String)"fileName must not be null");
        this.type = type;
        this.key = key;
        this.fileName = fileName;
    }

    private static List<DefaultDecorator> getDefaultDecorators() {
        List<DefaultDecorator> decorators = Arrays.asList(new DefaultDecorator(TYPE_DECORATOR, "main", "decorators/main.vmd"), new DefaultDecorator(TYPE_DECORATOR, "global", "decorators/global.vmd"), new DefaultDecorator(TYPE_DECORATOR, "popup", "decorators/popup.vmd"), new DefaultDecorator(TYPE_DECORATOR, "printable", "decorators/printable.vmd"), new DefaultDecorator(TYPE_DECORATOR, "admin", "decorators/admin.vmd"), new DefaultDecorator(TYPE_CONTENT, "space", "decorators/space.vmd"), new DefaultDecorator(TYPE_CONTENT, "page", "decorators/page.vmd"), new DefaultDecorator(TYPE_CONTENT, "blogpost", "decorators/blogpost.vmd"), new DefaultDecorator(TYPE_CONTENT, "sharedcomments", "decorators/components/sharedcomments.vmd"), new DefaultDecorator(TYPE_CONTENT, "comments", "decorators/components/comments.vmd"), new DefaultDecorator(TYPE_EXPORT, "space", TemplateSupport.classToTemplatePath(Space.class) + ".htmlexport.vm"), new DefaultDecorator(TYPE_EXPORT, "page", TemplateSupport.classToTemplatePath(Page.class) + ".htmlexport.vm"));
        return Collections.unmodifiableList(decorators);
    }

    public static List<DefaultDecorator> getDecorators() {
        return DEFAULT_DECORATORS;
    }

    public static List<DefaultDecorator> getDecorators(String type) {
        ArrayList<DefaultDecorator> list = new ArrayList<DefaultDecorator>();
        if (type == null) {
            return list;
        }
        for (DefaultDecorator decorator : DEFAULT_DECORATORS) {
            if (!decorator.getType().equals(type)) continue;
            list.add(decorator);
        }
        return list;
    }

    public static DefaultDecorator getByFileName(String fileName) {
        for (DefaultDecorator decorator : DEFAULT_DECORATORS) {
            if (!decorator.getFileName().equals(fileName)) continue;
            return decorator;
        }
        return null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getKey() {
        return this.key;
    }

    public String getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultDecorator)) {
            return false;
        }
        DefaultDecorator decorator = (DefaultDecorator)o;
        return this.fileName.equals(decorator.fileName);
    }

    public int hashCode() {
        return this.fileName.hashCode();
    }

    public String toString() {
        return "DefaultDecorator: " + this.key + " (" + this.fileName + ")";
    }

    public String getNameKey() {
        return "decorator." + this.type + "." + this.key + ".name";
    }

    public String getDescriptionKey() {
        return "decorator." + this.type + "." + this.key + ".description";
    }

    public String getTypeNameKey() {
        return "decorator." + this.type + ".typename";
    }

    public String getTypeDescriptionKey() {
        return "decorator." + this.type + ".typedescription";
    }
}

