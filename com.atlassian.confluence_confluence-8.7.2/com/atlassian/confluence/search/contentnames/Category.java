/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.search.contentnames;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Category {
    public static final Category PAGES = new Category("page");
    public static final Category BLOGS = new Category("blogpost");
    public static final Category CONTENT = new Category("content");
    public static final Category ATTACHMENTS = new Category("attachment");
    public static final Category PEOPLE = new Category("userinfo");
    public static final Category SPACES = new Category("spacedesc");
    public static final Category COMMENTS = new Category("comment");
    public static final Category PERSONAL_SPACE = new Category("personalspacedesc");
    public static final Category CUSTOM = new Category("custom");
    private static final Map<String, Category> standardCategoryTypes = new HashMap<String, Category>(9);
    private final String name;

    public static Category getCategory(String type) {
        if (StringUtils.isBlank((CharSequence)type)) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        Category cat = standardCategoryTypes.get(type);
        if (cat != null) {
            return cat;
        }
        return new Category(type);
    }

    public static Set<Category> getCategories(String contentType) {
        HashSet<Category> categories = new HashSet<Category>();
        categories.add(Category.getCategory(contentType));
        if ("page".equals(contentType) || "blogpost".equals(contentType)) {
            categories.add(CONTENT);
        }
        return categories;
    }

    public Category(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }
        this.name = name.intern();
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Category)) {
            return false;
        }
        Category other = (Category)o;
        return new EqualsBuilder().append((Object)this.name, (Object)other.name).isEquals();
    }

    public int hashCode() {
        return 629 + this.name.hashCode();
    }

    public String toString() {
        ToStringBuilder builder = new ToStringBuilder((Object)this);
        builder.append("name", (Object)this.name);
        return builder.toString();
    }

    static {
        standardCategoryTypes.put(PAGES.getName(), PAGES);
        standardCategoryTypes.put(BLOGS.getName(), BLOGS);
        standardCategoryTypes.put(CONTENT.getName(), CONTENT);
        standardCategoryTypes.put(ATTACHMENTS.getName(), ATTACHMENTS);
        standardCategoryTypes.put(PEOPLE.getName(), PEOPLE);
        standardCategoryTypes.put(SPACES.getName(), SPACES);
        standardCategoryTypes.put(COMMENTS.getName(), COMMENTS);
        standardCategoryTypes.put(PERSONAL_SPACE.getName(), PERSONAL_SPACE);
    }
}

