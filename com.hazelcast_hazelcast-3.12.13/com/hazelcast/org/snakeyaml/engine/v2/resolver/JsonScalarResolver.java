/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.resolver;

import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ResolverTuple;
import com.hazelcast.org.snakeyaml.engine.v2.resolver.ScalarResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonScalarResolver
implements ScalarResolver {
    public static final Pattern BOOL = Pattern.compile("^(?:true|false)$");
    public static final Pattern FLOAT = Pattern.compile("^(-?(0?\\.[0-9]+|[1-9][0-9]*(\\.[0-9]*)?)([eE][-+]?[0-9]+)?)$");
    public static final Pattern INT = Pattern.compile("^(?:-?(?:0|[1-9][0-9]*))$");
    public static final Pattern NULL = Pattern.compile("^(?:null)$");
    public static final Pattern EMPTY = Pattern.compile("^$");
    public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*((?<name>\\w+)((?<separator>:?(-|\\?))(?<value>\\w+)?)?)\\s*\\}$");
    protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap<Character, List<ResolverTuple>>();

    public void addImplicitResolver(Tag tag, Pattern regexp, String first) {
        if (first == null) {
            List curr = this.yamlImplicitResolvers.computeIfAbsent(null, c -> new ArrayList());
            curr.add(new ResolverTuple(tag, regexp));
        } else {
            char[] chrs = first.toCharArray();
            int j = chrs.length;
            for (int i = 0; i < j; ++i) {
                List<ResolverTuple> curr;
                Character theC = Character.valueOf(chrs[i]);
                if (theC.charValue() == '\u0000') {
                    theC = null;
                }
                if ((curr = this.yamlImplicitResolvers.get(theC)) == null) {
                    curr = new ArrayList<ResolverTuple>();
                    this.yamlImplicitResolvers.put(theC, curr);
                }
                curr.add(new ResolverTuple(tag, regexp));
            }
        }
    }

    protected void addImplicitResolvers() {
        this.addImplicitResolver(Tag.NULL, EMPTY, null);
        this.addImplicitResolver(Tag.BOOL, BOOL, "tf");
        this.addImplicitResolver(Tag.INT, INT, "-0123456789");
        this.addImplicitResolver(Tag.FLOAT, FLOAT, "-0123456789.");
        this.addImplicitResolver(Tag.NULL, NULL, "n\u0000");
        this.addImplicitResolver(Tag.ENV_TAG, ENV_FORMAT, "$");
    }

    public JsonScalarResolver() {
        this.addImplicitResolvers();
    }

    @Override
    public Tag resolve(String value, Boolean implicit) {
        Pattern regexp;
        Tag tag;
        if (!implicit.booleanValue()) {
            return Tag.STR;
        }
        List<ResolverTuple> resolvers = value.length() == 0 ? this.yamlImplicitResolvers.get(Character.valueOf('\u0000')) : this.yamlImplicitResolvers.get(Character.valueOf(value.charAt(0)));
        if (resolvers != null) {
            for (ResolverTuple v : resolvers) {
                tag = v.getTag();
                regexp = v.getRegexp();
                if (!regexp.matcher(value).matches()) continue;
                return tag;
            }
        }
        if (this.yamlImplicitResolvers.containsKey(null)) {
            for (ResolverTuple v : this.yamlImplicitResolvers.get(null)) {
                tag = v.getTag();
                regexp = v.getRegexp();
                if (!regexp.matcher(value).matches()) continue;
                return tag;
            }
        }
        return Tag.STR;
    }
}

