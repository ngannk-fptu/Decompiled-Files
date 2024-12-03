/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.jsp.tagext.PageData;
import javax.servlet.jsp.tagext.ValidationMessage;

public abstract class TagLibraryValidator {
    private Map<String, Object> initParameters;

    public void setInitParameters(Map<String, Object> map) {
        this.initParameters = Collections.unmodifiableMap(new HashMap<String, Object>(map));
    }

    public Map<String, Object> getInitParameters() {
        return this.initParameters;
    }

    public ValidationMessage[] validate(String prefix, String uri, PageData page) {
        return null;
    }

    public void release() {
        this.initParameters = null;
    }
}

