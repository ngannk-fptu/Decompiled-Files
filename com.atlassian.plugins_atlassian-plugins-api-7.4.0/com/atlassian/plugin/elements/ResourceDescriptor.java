/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.plugin.elements;

import com.atlassian.plugin.elements.ResourceLocation;
import com.atlassian.plugin.loaders.LoaderUtils;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;
import org.dom4j.Element;

public class ResourceDescriptor {
    static final String ALLOW_PUBLIC_USE_PARAM = "allow-public-use";
    private final String type;
    private final String name;
    private final String location;
    private final String contentType;
    private final Pattern pattern;
    private final String content;
    private final Map<String, String> params;
    private final ResourceLocation ourLocation;

    public ResourceDescriptor(Element element) {
        this.type = element.attributeValue("type");
        String name = element.attributeValue("name");
        String namePattern = element.attributeValue("namePattern");
        if (name == null && namePattern == null) {
            throw new RuntimeException("resource descriptor needs one of 'name' and 'namePattern' attributes.");
        }
        if (name != null && namePattern != null) {
            throw new RuntimeException("resource descriptor can have only one of 'name' and 'namePattern' attributes.");
        }
        this.name = name;
        this.location = element.attributeValue("location");
        if (namePattern != null && this.location == null) {
            throw new RuntimeException("resource descriptor must have the 'location' attribute specified when the 'namePattern' attribute is used");
        }
        if (namePattern != null && !this.location.endsWith("/")) {
            throw new RuntimeException("when 'namePattern' is specified, 'location' must be a directory (ending in '/')");
        }
        this.params = LoaderUtils.getParams(element);
        this.validateParameters();
        this.content = element.getTextTrim() != null && !"".equals(element.getTextTrim()) ? element.getTextTrim() : null;
        this.contentType = this.getParameter("content-type");
        if (namePattern != null) {
            this.pattern = Pattern.compile(namePattern);
            this.ourLocation = null;
        } else {
            this.ourLocation = new ResourceLocation(this.location, name, this.type, this.contentType, this.content, this.params);
            this.pattern = null;
        }
    }

    private void validateParameters() {
        String allowPublicUse = this.getParameter(ALLOW_PUBLIC_USE_PARAM);
        if (allowPublicUse != null && !Boolean.valueOf(allowPublicUse).toString().equals(allowPublicUse)) {
            throw new IllegalArgumentException("An illegal value [" + allowPublicUse + "] for param " + ALLOW_PUBLIC_USE_PARAM + ":  found in resource [" + this.name + "]");
        }
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        if (this.name == null) {
            throw new RuntimeException("tried to get name from ResourceDescriptor with null name and namePattern = " + this.pattern);
        }
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getContent() {
        return this.content;
    }

    public boolean doesTypeAndNameMatch(String type, String name) {
        if (type != null && type.equalsIgnoreCase(this.type)) {
            if (this.pattern != null) {
                return this.pattern.matcher(name).matches();
            }
            return name != null && name.equalsIgnoreCase(this.name);
        }
        return false;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(this.params);
    }

    public String getParameter(String key) {
        return this.params.get(key);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceDescriptor)) {
            return false;
        }
        ResourceDescriptor resourceDescriptor = (ResourceDescriptor)o;
        if (this.name != null) {
            if (!this.name.equals(resourceDescriptor.name)) {
                return false;
            }
        } else if (this.pattern != null) {
            if (resourceDescriptor.pattern == null) {
                return false;
            }
            if (!this.pattern.toString().equals(resourceDescriptor.pattern.toString())) {
                return false;
            }
        }
        return !(this.type == null ? resourceDescriptor.type != null : !this.type.equals(resourceDescriptor.type));
    }

    public int hashCode() {
        int result = 0;
        if (this.type != null) {
            result = this.type.hashCode();
        }
        if (this.name != null) {
            result = 29 * result + this.name.hashCode();
        } else if (this.pattern != null) {
            result = 29 * result + this.pattern.hashCode();
        }
        return result;
    }

    public ResourceLocation getResourceLocationForName(String name) {
        if (this.pattern != null) {
            if (this.pattern.matcher(name).matches()) {
                return new ResourceLocation(this.getLocation(), name, this.type, this.contentType, this.content, this.params);
            }
            throw new RuntimeException("This descriptor does not provide resources named " + name);
        }
        return this.ourLocation;
    }
}

