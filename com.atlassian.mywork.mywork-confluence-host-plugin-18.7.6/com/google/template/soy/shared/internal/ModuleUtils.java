/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.template.soy.shared.internal.NonpluginFunction;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModuleUtils {
    private ModuleUtils() {
    }

    public static <T extends SoyFunction> Map<String, T> buildSpecificSoyFunctionsMap(Set<SoyFunction> soyFunctionsSet, Class<T> specificSoyFunctionType) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        HashSet seenFnNames = Sets.newHashSetWithExpectedSize((int)soyFunctionsSet.size());
        for (SoyFunction fn : soyFunctionsSet) {
            if (!specificSoyFunctionType.isAssignableFrom(fn.getClass())) continue;
            String fnName = fn.getName();
            if (seenFnNames.contains(fnName) || NonpluginFunction.forFunctionName(fnName) != null) {
                throw new IllegalStateException("Found two implementations of " + specificSoyFunctionType.getSimpleName() + " with the same function name '" + fnName + "'.");
            }
            seenFnNames.add(fnName);
            mapBuilder.put((Object)fnName, specificSoyFunctionType.cast(fn));
        }
        return mapBuilder.build();
    }

    public static <T extends SoyFunction, D extends SoyFunction> Map<String, T> buildSpecificSoyFunctionsMapWithAdaptation(Set<SoyFunction> soyFunctionsSet, Class<T> specificSoyFunctionType, Class<D> equivDeprecatedSoyFunctionType, Function<D, T> adaptFn) {
        Map<String, T> tMap = ModuleUtils.buildSpecificSoyFunctionsMap(soyFunctionsSet, specificSoyFunctionType);
        Map<String, D> dMap = ModuleUtils.buildSpecificSoyFunctionsMap(soyFunctionsSet, equivDeprecatedSoyFunctionType);
        ImmutableMap.Builder resultMapBuilder = ImmutableMap.builder();
        resultMapBuilder.putAll(tMap);
        for (String functionName : dMap.keySet()) {
            if (tMap.containsKey(functionName)) {
                if (((SoyFunction)tMap.get(functionName)).equals(dMap.get(functionName))) {
                    throw new IllegalStateException(String.format("Found function named '%s' that implements both %s and %s -- please remove the latter deprecated interface.", functionName, specificSoyFunctionType.getSimpleName(), equivDeprecatedSoyFunctionType.getSimpleName()));
                }
                throw new IllegalStateException(String.format("Found two functions with the same name '%s', one implementing %s and the other implementing %s", functionName, specificSoyFunctionType.getSimpleName(), equivDeprecatedSoyFunctionType.getSimpleName()));
            }
            resultMapBuilder.put((Object)functionName, adaptFn.apply(dMap.get(functionName)));
        }
        return resultMapBuilder.build();
    }

    public static <T extends SoyPrintDirective> Map<String, T> buildSpecificSoyDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet, Class<T> specificSoyDirectiveType) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        HashSet seenDirectiveNames = Sets.newHashSetWithExpectedSize((int)soyDirectivesSet.size());
        for (SoyPrintDirective directive : soyDirectivesSet) {
            if (!specificSoyDirectiveType.isAssignableFrom(directive.getClass())) continue;
            String directiveName = directive.getName();
            if (seenDirectiveNames.contains(directiveName)) {
                throw new IllegalStateException("Found two implementations of " + specificSoyDirectiveType.getSimpleName() + " with the same directive name '" + directiveName + "'.");
            }
            seenDirectiveNames.add(directiveName);
            mapBuilder.put((Object)directiveName, specificSoyDirectiveType.cast(directive));
        }
        return mapBuilder.build();
    }

    public static <T extends SoyPrintDirective, D extends SoyPrintDirective> Map<String, T> buildSpecificSoyDirectivesMapWithAdaptation(Set<SoyPrintDirective> soyDirectivesSet, Class<T> specificSoyDirectiveType, Class<D> equivDeprecatedSoyDirectiveType, Function<D, T> adaptFn) {
        Map<String, T> tMap = ModuleUtils.buildSpecificSoyDirectivesMap(soyDirectivesSet, specificSoyDirectiveType);
        Map<String, D> dMap = ModuleUtils.buildSpecificSoyDirectivesMap(soyDirectivesSet, equivDeprecatedSoyDirectiveType);
        ImmutableMap.Builder resultMapBuilder = ImmutableMap.builder();
        resultMapBuilder.putAll(tMap);
        for (String directiveName : dMap.keySet()) {
            if (tMap.containsKey(directiveName)) {
                if (((SoyPrintDirective)tMap.get(directiveName)).equals(dMap.get(directiveName))) {
                    throw new IllegalStateException(String.format("Found print directive named '%s' that implements both %s and %s -- please remove the latter deprecated interface.", directiveName, specificSoyDirectiveType.getSimpleName(), equivDeprecatedSoyDirectiveType.getSimpleName()));
                }
                throw new IllegalStateException(String.format("Found two print directives with the same name '%s', one implementing %s and the other implementing %s", directiveName, specificSoyDirectiveType.getSimpleName(), equivDeprecatedSoyDirectiveType.getSimpleName()));
            }
            resultMapBuilder.put((Object)directiveName, adaptFn.apply(dMap.get(directiveName)));
        }
        return resultMapBuilder.build();
    }
}

