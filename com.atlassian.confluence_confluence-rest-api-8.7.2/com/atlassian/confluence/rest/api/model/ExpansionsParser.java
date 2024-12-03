/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.rest.api.model;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@ExperimentalApi
public class ExpansionsParser {
    public static Expansion[] parse(String expand) {
        if (StringUtils.isBlank((CharSequence)expand)) {
            return new Expansion[0];
        }
        return ExpansionsParser.parse(expand.split(",")).toArray();
    }

    public static Expansion[] parseExperimental(String expand) {
        if (StringUtils.isBlank((CharSequence)expand)) {
            return new Expansion[0];
        }
        return ExpansionsParser.parseExperimental(expand.split(",")).toArray();
    }

    public static Expansions parseAsExpansions(String expand) {
        return new Expansions(ExpansionsParser.parse(expand));
    }

    public static Expansions parseExperimentalAsExpansions(String expand) {
        return new Expansions(ExpansionsParser.parseExperimental(expand));
    }

    public static Expansion parseSingle(String expand) {
        Expansion[] expansions = ExpansionsParser.parse(expand);
        if (expansions.length > 0) {
            return expansions[0];
        }
        return new Expansion("");
    }

    public static Expansion parseExperimentalSingle(String expand) {
        Expansion[] expansions = ExpansionsParser.parseExperimental(expand);
        if (expansions.length > 0) {
            return expansions[0];
        }
        return new Expansion("");
    }

    public static String asString(Expansions expansions) {
        if (expansions == null) {
            return null;
        }
        return ExpansionsParser.asString(expansions.toArray());
    }

    public static String asString(Expansion[] expansionsArr) {
        return new ExpansionAsStringImpl().asString(expansionsArr);
    }

    public static Expansions parse(String ... individualExpansions) {
        return new ExpansionsParserImpl(false).parseExpansion(individualExpansions);
    }

    public static Expansions parseExperimental(String ... individualExpansions) {
        return new ExpansionsParserImpl(true).parseExpansion(individualExpansions);
    }

    public static Expansions parseWithPrefix(String prefix, String expansions) {
        Expansion[] subExpansions = ExpansionsParser.parse(expansions);
        return new Expansions(new Expansion[]{new Expansion(prefix, new Expansions(subExpansions))});
    }

    public static Expansions parseExperimentalWithPrefix(String prefix, String expansions) {
        Expansion[] subExpansions = ExpansionsParser.parseExperimental(expansions);
        return new Expansions(new Expansion[]{new Expansion(prefix, new Expansions(subExpansions))});
    }

    private static final class ExpansionAsStringImpl {
        private final List<String> parents;

        private ExpansionAsStringImpl() {
            this(new ArrayList<String>());
        }

        private ExpansionAsStringImpl(List<String> parents) {
            this.parents = parents;
        }

        private String asString(Expansion[] expansionsArr) {
            if (expansionsArr.length == 0) {
                return null;
            }
            ArrayList<String> expansionStrings = new ArrayList<String>();
            for (Expansion expansion : expansionsArr) {
                expansionStrings.add(this.asString(expansion));
            }
            Collections.sort(expansionStrings);
            return StringUtils.join(expansionStrings, (String)",");
        }

        private String asString(Expansion expansion) {
            String propertyName = expansion.getPropertyName();
            Expansions subExpansions = expansion.getSubExpansions();
            ArrayList<String> pathParts = new ArrayList<String>(this.parents);
            pathParts.add(propertyName);
            if (subExpansions.isEmpty()) {
                return StringUtils.join(pathParts, (String)".");
            }
            return new ExpansionAsStringImpl(pathParts).asString(subExpansions.toArray());
        }
    }

    private static final class ExpansionsParserImpl {
        private static final ImmutableSet<String> EXPERIMENTAL_EXPANSIONS = ImmutableSet.of();
        private final boolean allowExperimental;

        private ExpansionsParserImpl(boolean allowExperimental) {
            this.allowExperimental = allowExperimental;
        }

        private Expansions parseExpansion(String ... individualExpansions) {
            ArrayList expandyBits = new ArrayList(individualExpansions.length);
            for (String individualExpansion : individualExpansions) {
                this.toExpansion(this.allowExperimental, individualExpansion).map(expandyBits::add);
            }
            return new Expansions(expandyBits);
        }

        private Option<Expansion> toExpansion(boolean allowExperimental, String individualExpansion) {
            String[] pathParts = individualExpansion.split("\\.");
            return this.toExpansion(allowExperimental, pathParts);
        }

        private Option<Expansion> toExpansion(boolean allowExperimental, String[] pathDotParts) {
            String pathStart = pathDotParts[0];
            if (!allowExperimental && EXPERIMENTAL_EXPANSIONS.contains((Object)pathStart)) {
                return Option.none();
            }
            if (pathDotParts.length == 1) {
                return Option.option((Object)new Expansion(pathStart));
            }
            return Option.option((Object)new Expansion(pathStart, new Expansions(this.toExpansion(allowExperimental, this.rest(pathDotParts)))));
        }

        private String[] rest(String[] pathParts) {
            return Arrays.copyOfRange(pathParts, 1, pathParts.length);
        }
    }
}

