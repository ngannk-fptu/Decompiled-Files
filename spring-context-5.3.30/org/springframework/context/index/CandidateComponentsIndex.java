/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.context.index;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CandidateComponentsIndex {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher(".");
    private final MultiValueMap<String, Entry> index;

    CandidateComponentsIndex(List<Properties> content) {
        this.index = CandidateComponentsIndex.parseIndex(content);
    }

    private static MultiValueMap<String, Entry> parseIndex(List<Properties> content) {
        LinkedMultiValueMap index = new LinkedMultiValueMap();
        for (Properties entry : content) {
            entry.forEach((BiConsumer<? super Object, ? super Object>)((BiConsumer<Object, Object>)(arg_0, arg_1) -> CandidateComponentsIndex.lambda$parseIndex$0((MultiValueMap)index, arg_0, arg_1)));
        }
        return index;
    }

    public Set<String> getCandidateTypes(String basePackage, String stereotype) {
        List candidates = (List)this.index.get((Object)stereotype);
        if (candidates != null) {
            return candidates.parallelStream().filter(t -> t.match(basePackage)).map(t -> ((Entry)t).type).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private static /* synthetic */ void lambda$parseIndex$0(MultiValueMap index, Object type, Object values) {
        String[] stereotypes;
        for (String stereotype : stereotypes = ((String)values).split(",")) {
            index.add((Object)stereotype, (Object)new Entry((String)type));
        }
    }

    private static class Entry {
        private final String type;
        private final String packageName;

        Entry(String type) {
            this.type = type;
            this.packageName = ClassUtils.getPackageName((String)type);
        }

        public boolean match(String basePackage) {
            if (pathMatcher.isPattern(basePackage)) {
                return pathMatcher.match(basePackage, this.packageName);
            }
            return this.type.startsWith(basePackage);
        }
    }
}

