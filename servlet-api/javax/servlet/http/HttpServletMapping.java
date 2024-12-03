/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.http;

import javax.servlet.http.MappingMatch;

public interface HttpServletMapping {
    public String getMatchValue();

    public String getPattern();

    public String getServletName();

    public MappingMatch getMappingMatch();
}

