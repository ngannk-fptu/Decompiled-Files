/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

public class TagAttributeInfo {
    public static final String ID = "id";
    private final String name;
    private final String type;
    private final boolean reqTime;
    private final boolean required;
    private final boolean fragment;
    private final String description;
    private final boolean deferredValue;
    private final boolean deferredMethod;
    private final String expectedTypeName;
    private final String methodSignature;

    public TagAttributeInfo(String name, boolean required, String type, boolean reqTime) {
        this(name, required, type, reqTime, false);
    }

    public TagAttributeInfo(String name, boolean required, String type, boolean reqTime, boolean fragment) {
        this(name, required, type, reqTime, fragment, null, false, false, null, null);
    }

    public TagAttributeInfo(String name, boolean required, String type, boolean reqTime, boolean fragment, String description, boolean deferredValue, boolean deferredMethod, String expectedTypeName, String methodSignature) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.reqTime = reqTime;
        this.fragment = fragment;
        this.description = description;
        this.deferredValue = deferredValue;
        this.deferredMethod = deferredMethod;
        this.expectedTypeName = expectedTypeName;
        this.methodSignature = methodSignature;
    }

    public String getName() {
        return this.name;
    }

    public String getTypeName() {
        return this.type;
    }

    public boolean canBeRequestTime() {
        return this.reqTime;
    }

    public boolean isRequired() {
        return this.required;
    }

    public static TagAttributeInfo getIdAttribute(TagAttributeInfo[] tagAttributeInfos) {
        for (TagAttributeInfo tagAttributeInfo : tagAttributeInfos) {
            if (!tagAttributeInfo.getName().equals(ID)) continue;
            return tagAttributeInfo;
        }
        return null;
    }

    public boolean isFragment() {
        return this.fragment;
    }

    public String toString() {
        StringBuilder b = new StringBuilder(64);
        b.append("name = " + this.name + " ");
        b.append("type = " + this.type + " ");
        b.append("reqTime = " + this.reqTime + " ");
        b.append("required = " + this.required + " ");
        b.append("fragment = " + this.fragment + " ");
        b.append("deferredValue = " + this.deferredValue + " ");
        b.append("expectedTypeName = " + this.expectedTypeName + " ");
        b.append("deferredMethod = " + this.deferredMethod + " ");
        b.append("methodSignature = " + this.methodSignature);
        return b.toString();
    }

    public boolean isDeferredMethod() {
        return this.deferredMethod;
    }

    public boolean isDeferredValue() {
        return this.deferredValue;
    }

    public String getDescription() {
        return this.description;
    }

    public String getExpectedTypeName() {
        return this.expectedTypeName;
    }

    public String getMethodSignature() {
        return this.methodSignature;
    }
}

