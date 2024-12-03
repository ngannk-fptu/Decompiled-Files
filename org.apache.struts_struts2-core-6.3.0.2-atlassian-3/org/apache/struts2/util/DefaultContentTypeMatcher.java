/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.WildcardHelper;
import java.util.Map;
import org.apache.struts2.util.ContentTypeMatcher;

public class DefaultContentTypeMatcher
implements ContentTypeMatcher<int[]> {
    private PatternMatcher<int[]> matcher = new WildcardHelper();

    @Override
    public int[] compilePattern(String data) {
        return this.matcher.compilePattern(data);
    }

    @Override
    public boolean match(Map<String, String> map, String data, int[] expr) {
        return this.matcher.match(map, data, expr);
    }
}

