/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.data.spel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.spel.spi.Function;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class Functions {
    private static final String MESSAGE_TEMPLATE = "There are multiple matching methods of name '%s' for parameter types (%s), but no exact match. Make sure to provide only one matching overload or one with exactly those types.";
    private final MultiValueMap<String, Function> functions = new LinkedMultiValueMap();

    Functions() {
    }

    void addAll(Map<String, Function> newFunctions) {
        newFunctions.forEach((n, f) -> {
            List<Function> currentElements = this.get((String)n);
            if (!Functions.contains(currentElements, f)) {
                this.functions.add(n, f);
            }
        });
    }

    void addAll(MultiValueMap<String, Function> newFunctions) {
        newFunctions.forEach((k, list) -> {
            List<Function> currentElements = this.get((String)k);
            list.stream().filter(f -> !Functions.contains(currentElements, f)).forEach(f -> this.functions.add(k, f));
        });
    }

    List<Function> get(String name) {
        return (List)this.functions.getOrDefault((Object)name, Collections.emptyList());
    }

    Optional<Function> get(String name, List<TypeDescriptor> argumentTypes) {
        Stream<Function> candidates = this.get(name).stream().filter(f -> f.supports(argumentTypes));
        List<Function> collect = candidates.collect(Collectors.toList());
        return Functions.bestMatch(collect, argumentTypes);
    }

    private static boolean contains(List<Function> elements, Function f) {
        return elements.stream().anyMatch(f::isSignatureEqual);
    }

    private static Optional<Function> bestMatch(List<Function> candidates, List<TypeDescriptor> argumentTypes) {
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        if (candidates.size() == 1) {
            return Optional.of(candidates.get(0));
        }
        Optional<Function> exactMatch = candidates.stream().filter(f -> f.supportsExact(argumentTypes)).findFirst();
        if (!exactMatch.isPresent()) {
            throw new IllegalStateException(Functions.createErrorMessage(candidates, argumentTypes));
        }
        return exactMatch;
    }

    private static String createErrorMessage(List<Function> candidates, List<TypeDescriptor> argumentTypes) {
        String argumentTypeString = argumentTypes.stream().map(TypeDescriptor::getName).collect(Collectors.joining(","));
        return String.format(MESSAGE_TEMPLATE, candidates.get(0).getName(), argumentTypeString);
    }
}

