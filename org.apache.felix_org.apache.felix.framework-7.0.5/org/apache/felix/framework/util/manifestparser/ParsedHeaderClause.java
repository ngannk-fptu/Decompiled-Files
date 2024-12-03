/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util.manifestparser;

import java.util.List;
import java.util.Map;

public class ParsedHeaderClause {
    public final List<String> m_paths;
    public final Map<String, String> m_dirs;
    public final Map<String, Object> m_attrs;
    public final Map<String, String> m_types;

    public ParsedHeaderClause(List<String> paths, Map<String, String> dirs, Map<String, Object> attrs, Map<String, String> types) {
        this.m_paths = paths;
        this.m_dirs = dirs;
        this.m_attrs = attrs;
        this.m_types = types;
    }
}

