/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 */
package com.atlassian.plugin.webresource.impl.helpers.url;

import com.atlassian.plugin.webresource.ContextSubBatchResourceUrl;
import com.atlassian.plugin.webresource.ResourceUrl;
import com.atlassian.plugin.webresource.ResourceUrlImpl;
import com.atlassian.plugin.webresource.WebResourceSubBatchUrl;
import com.atlassian.plugin.webresource.impl.CachedCondition;
import com.atlassian.plugin.webresource.impl.CachedTransformers;
import com.atlassian.plugin.webresource.impl.Globals;
import com.atlassian.plugin.webresource.impl.RequestCache;
import com.atlassian.plugin.webresource.impl.RequestState;
import com.atlassian.plugin.webresource.impl.UrlBuildingStrategy;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.discovery.BundleFinder;
import com.atlassian.plugin.webresource.impl.discovery.Found;
import com.atlassian.plugin.webresource.impl.discovery.PredicateFailStrategy;
import com.atlassian.plugin.webresource.impl.helpers.BaseHelpers;
import com.atlassian.plugin.webresource.impl.helpers.StateEncodedUrlResult;
import com.atlassian.plugin.webresource.impl.helpers.url.CalculatedBatches;
import com.atlassian.plugin.webresource.impl.helpers.url.ContextBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.ContextBatchKey;
import com.atlassian.plugin.webresource.impl.helpers.url.ParamsComparator;
import com.atlassian.plugin.webresource.impl.helpers.url.SplitSubBatches;
import com.atlassian.plugin.webresource.impl.helpers.url.SubBatch;
import com.atlassian.plugin.webresource.impl.helpers.url.WebResourceBatch;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Tuple;
import com.atlassian.plugin.webresource.impl.support.UrlCache;
import com.atlassian.plugin.webresource.impl.support.http.BaseRouter;
import com.atlassian.plugin.webresource.legacy.LegacyUrlGenerationHelpers;
import com.atlassian.plugin.webresource.models.RawRequest;
import com.atlassian.plugin.webresource.url.DefaultUrlBuilder;
import com.atlassian.plugin.webresource.util.HashBuilder;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UrlGenerationHelpers
extends BaseHelpers {
    protected static UrlCache.IncludedExcludedConditionsAndBatchingOptions buildIncludedExcludedConditionsAndBatchingOptions(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, Collection<String> topLevelIncluded, Collection<String> topLevelExcluded) {
        HashSet<UrlCache.EvaluatedCondition> evaluatedConditions = new HashSet<UrlCache.EvaluatedCondition>();
        Predicate conditionEvaluator = bundle -> Optional.ofNullable(bundle.getCondition()).map(condition -> {
            boolean evaluationResult = condition.evaluateSafely(requestCache, urlBuilderStrategy);
            evaluatedConditions.add(new UrlCache.EvaluatedCondition((CachedCondition)condition, evaluationResult));
            if (condition.isLegacy()) {
                return true;
            }
            return evaluationResult;
        }).orElse(true);
        Found results = new BundleFinder(requestCache.getSnapshot()).included(topLevelIncluded).excluded(topLevelExcluded, (Predicate<Bundle>)Predicates.alwaysTrue()).deepFilter((Predicate<Bundle>)conditionEvaluator).endAndGetResult();
        LinkedHashSet<String> reducedIncluded = new LinkedHashSet<String>(topLevelIncluded);
        Set<String> reducedExcluded = results.getReducedExclusions();
        return new UrlCache.IncludedExcludedConditionsAndBatchingOptions(new UrlCache.IncludedAndExcluded(reducedIncluded, reducedExcluded), evaluatedConditions, requestCache.getGlobals().getConfig().resplitMergedContextBatchesForThisRequest());
    }

    @Deprecated
    public static Set<String> resolveExcluded(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, Collection<String> topLevelIncluded, Collection<String> topLevelExcluded) {
        return UrlGenerationHelpers.resolveExcluded(requestCache, requestCache.getGlobals().getUrlCache(), urlBuilderStrategy, topLevelIncluded, topLevelExcluded);
    }

    @Deprecated
    public static Set<String> resolveExcluded(RequestCache requestCache, UrlCache urlCache, UrlBuildingStrategy urlBuilderStrategy, Collection<String> topLevelIncluded, Collection<String> topLevelExcluded) {
        UrlCache.IncludedExcludedConditionsAndBatchingOptions cacheKey = UrlGenerationHelpers.buildIncludedExcludedConditionsAndBatchingOptions(requestCache, urlBuilderStrategy, topLevelIncluded, topLevelExcluded);
        return urlCache.getResolvedExcluded(cacheKey, key -> LegacyUrlGenerationHelpers.calculateBatches((RequestCache)requestCache, (UrlBuildingStrategy)urlBuilderStrategy, key.getIncluded(), key.getExcluded(), (boolean)false).excludedResolved);
    }

    @Deprecated
    protected static CalculatedBatches calculateBatches(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, LinkedHashSet<String> topLevelIncluded, LinkedHashSet<String> allExcluded) {
        return UrlGenerationHelpers.calculateBatches(requestCache, urlBuilderStrategy, topLevelIncluded, allExcluded, Collections.emptySet());
    }

    protected static CalculatedBatches calculateBatches(RequestCache requestCache, UrlBuildingStrategy urlBuildingStrategy, LinkedHashSet<String> topLevelIncluded, LinkedHashSet<String> allExcluded, Set<String> topLevelExcluded) {
        LegacyUrlGenerationHelpers.Resolved resolved = LegacyUrlGenerationHelpers.calculateBatches(requestCache, urlBuildingStrategy, topLevelIncluded, allExcluded, topLevelExcluded, false);
        Tuple<List<ContextBatch>, List<WebResourceBatch>> subBatches = UrlGenerationHelpers.splitIntoSubBatches(requestCache, urlBuildingStrategy, resolved.contextBatchKeys, resolved.webResourceBatchKeys, allExcluded);
        List<ContextBatch> contextBatches = subBatches.getFirst();
        List<WebResourceBatch> webResourceBatches = subBatches.getLast();
        return new CalculatedBatches(contextBatches, webResourceBatches, resolved.excludedResolved);
    }

    protected static List<ResourceUrl> collectUrlStateAndBuildResourceUrls(RequestState requestState, UrlBuildingStrategy urlBuilderStrategy, List<ContextBatch> contextBatches, List<WebResourceBatch> webResourceBatches) {
        RequestCache requestCache = requestState.getRequestCache();
        Globals globals = requestCache.getGlobals();
        ArrayList<ResourceUrl> resourceUrls = new ArrayList<ResourceUrl>();
        for (String type : Config.BATCH_TYPES) {
            for (ContextBatch contextBatch : contextBatches) {
                List<String> excludedResolvedWithoutApplyingConditions = new BundleFinder(requestCache.getSnapshot()).included(contextBatch.getExcludedWithoutApplyingConditions()).end();
                List contextBatchResourceUrls = contextBatch.getSubBatches().stream().filter(subBatch -> !subBatch.getResourcesOfType(requestCache, type).isEmpty()).map(subBatch -> {
                    StateEncodedUrlResult taintAndUrlBuilder = UrlGenerationHelpers.encodeStateInUrlIfSupported(requestCache, urlBuilderStrategy, type, subBatch.getResourcesParams(), subBatch.getBundles(), requestCache.getSnapshot().toBundles(contextBatch.getSkippedWebResourcesWithUrlReadingConditions()), requestCache.getSnapshot().toBundles(excludedResolvedWithoutApplyingConditions));
                    DefaultUrlBuilder urlBuilder = taintAndUrlBuilder.getUrlBuilder();
                    return new ContextSubBatchResourceUrl(requestCache.getGlobals(), contextBatch, (SubBatch)subBatch, type, urlBuilder.buildParams(), urlBuilder.buildHash(), taintAndUrlBuilder.isTaint(), urlBuilder.getPrebakeErrors());
                }).collect(Collectors.toList());
                if (contextBatch.isAdditionalSortingRequired()) {
                    ParamsComparator paramsComparator = new ParamsComparator();
                    contextBatchResourceUrls.sort((url1, url2) -> {
                        int result = paramsComparator.compare(url1.getParams(), url2.getParams());
                        String aUrl = BaseRouter.buildUrl("", url1.getParams());
                        String bUrl = BaseRouter.buildUrl("", url2.getParams());
                        return result == 0 ? aUrl.compareTo(bUrl) : result;
                    });
                }
                if (globals.getConfig().isContextBatchingEnabled()) {
                    resourceUrls.addAll(contextBatchResourceUrls);
                } else {
                    for (ContextSubBatchResourceUrl contextSubBatchResourceUrl : contextBatchResourceUrls) {
                        Map<String, String> contextBatchResourceParams = contextSubBatchResourceUrl.getSubBatch().getResourcesParams();
                        SubBatch subBatch2 = contextSubBatchResourceUrl.getSubBatch();
                        List<Resource> contextBatchResources = subBatch2.getResourcesOfType(requestCache, type);
                        LinkedHashMap<Bundle, List> bundles = new LinkedHashMap<Bundle, List>();
                        for (Resource resource : contextBatchResources) {
                            bundles.computeIfAbsent(resource.getParent(), key -> new ArrayList()).add(resource);
                        }
                        for (Map.Entry entry : bundles.entrySet()) {
                            Bundle bundle = (Bundle)entry.getKey();
                            List resources = (List)entry.getValue();
                            StateEncodedUrlResult taintAndUrlBuilder = UrlGenerationHelpers.encodeStateInUrlIfSupported(requestCache, urlBuilderStrategy, type, contextBatchResourceParams, bundle, new ArrayList<Bundle>(), new ArrayList<Bundle>());
                            DefaultUrlBuilder urlBuilder = taintAndUrlBuilder.getUrlBuilder();
                            Map<String, String> webResourceBatchHttpParams = urlBuilder.buildParams();
                            if (globals.getConfig().isWebResourceBatchingEnabled()) {
                                SubBatch webResourceSubBatch = new SubBatch(webResourceBatchHttpParams, bundle, resources);
                                resourceUrls.add(new WebResourceSubBatchUrl(requestCache.getGlobals(), bundle.getKey(), webResourceSubBatch, type, webResourceBatchHttpParams, urlBuilder.buildHash(), taintAndUrlBuilder.isTaint(), urlBuilder.getPrebakeErrors()));
                                continue;
                            }
                            for (Resource resource : resources) {
                                resourceUrls.add(new ResourceUrlImpl(globals, resource, webResourceBatchHttpParams, urlBuilder.buildHash(), taintAndUrlBuilder.isTaint(), urlBuilder.getPrebakeErrors()));
                            }
                        }
                    }
                }
                resourceUrls.addAll(UrlGenerationHelpers.createResourceUrlsForRedirectResources(requestCache, urlBuilderStrategy, contextBatch.getStandaloneResourcesOfType(requestCache, type)));
            }
            for (WebResourceBatch webResourceBatch : webResourceBatches) {
                for (SubBatch subBatch3 : webResourceBatch.getSubBatches()) {
                    List<Resource> resources = subBatch3.getResourcesOfType(requestCache, type);
                    if (resources.isEmpty()) continue;
                    StateEncodedUrlResult taintAndUrlBuilder = UrlGenerationHelpers.encodeStateInUrlIfSupported(requestCache, urlBuilderStrategy, type, subBatch3.getResourcesParams(), subBatch3.getBundles(), new ArrayList<Bundle>(), new ArrayList<Bundle>());
                    DefaultUrlBuilder urlBuilder = taintAndUrlBuilder.getUrlBuilder();
                    if (globals.getConfig().isWebResourceBatchingEnabled()) {
                        resourceUrls.add(new WebResourceSubBatchUrl(requestCache.getGlobals(), webResourceBatch.getKey(), subBatch3, type, urlBuilder.buildParams(), urlBuilder.buildHash(), taintAndUrlBuilder.isTaint(), urlBuilder.getPrebakeErrors()));
                        continue;
                    }
                    for (Resource resource : resources) {
                        resourceUrls.add(new ResourceUrlImpl(globals, resource, urlBuilder.buildParams(), urlBuilder.buildHash(), taintAndUrlBuilder.isTaint(), urlBuilder.getPrebakeErrors()));
                    }
                }
                resourceUrls.addAll(UrlGenerationHelpers.createResourceUrlsForRedirectResources(requestCache, urlBuilderStrategy, webResourceBatch.getStandaloneResourcesOfType(requestCache, type)));
            }
        }
        return resourceUrls;
    }

    public static Tuple<List<ContextBatch>, List<WebResourceBatch>> splitIntoSubBatches(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, List<ContextBatchKey> contextBatchKeys, List<String> webResourceBatchKeys, Set<String> allExcluded) {
        SplitSubBatches result;
        ArrayList<ContextBatch> contextBatches = new ArrayList<ContextBatch>();
        HashSet<String> alreadyIncluded = new HashSet<String>(allExcluded);
        for (ContextBatchKey key : contextBatchKeys) {
            Found found = new BundleFinder(requestCache.getSnapshot()).included(key.getIncluded()).excludedResolved(alreadyIncluded).deepFilter(UrlGenerationHelpers.isConditionsSatisfied(requestCache, urlBuilderStrategy)).deepFilter((Predicate<Bundle>)Predicates.not(UrlGenerationHelpers.hasLegacyCondition())).onDeepFilterFail(PredicateFailStrategy.CONTINUE).endAndGetResult();
            ArrayList<String> skippedWebResourcesWithUrlReadingConditions = new ArrayList<String>();
            for (Bundle bundle : requestCache.getSnapshot().toBundles(found.getSkipped())) {
                if (bundle.getCondition() == null || bundle.getCondition().isLegacy()) continue;
                skippedWebResourcesWithUrlReadingConditions.add(bundle.getKey());
            }
            LinkedHashSet<String> excludedWithoutApplyingConditions = key.getExcluded();
            result = UrlGenerationHelpers.splitBatchIntoSubBatches(requestCache, found, true);
            contextBatches.add(new ContextBatch(key.getIncluded(), key.getExcluded(), skippedWebResourcesWithUrlReadingConditions, new ArrayList<String>(excludedWithoutApplyingConditions), result.getContextSubBatches(), result.getContextStandaloneResources(), result.isAdditionalSortingRequired()));
            alreadyIncluded.addAll(found.getFound());
        }
        ArrayList<WebResourceBatch> webResourceBatches = new ArrayList<WebResourceBatch>();
        for (String key : webResourceBatchKeys) {
            ArrayList<String> keys = new ArrayList<String>();
            keys.add(key);
            Found found = new BundleFinder(requestCache.getSnapshot()).included(keys).deep(false).deepFilter(UrlGenerationHelpers.isConditionsSatisfied(requestCache, urlBuilderStrategy)).endAndGetResult();
            result = UrlGenerationHelpers.splitBatchIntoSubBatches(requestCache, found, false);
            if (!result.getContextSubBatches().isEmpty() && result.getLegacyWebResources().isEmpty()) {
                webResourceBatches.add(new WebResourceBatch(key, result.getContextSubBatches(), result.getContextStandaloneResources()));
                continue;
            }
            if (result.getContextSubBatches().isEmpty() && !result.getLegacyWebResources().isEmpty()) {
                if (!result.getContextStandaloneResources().isEmpty()) {
                    throw new RuntimeException("single web resource cannot have context standalone resources!");
                }
                if (result.getLegacyWebResources().size() > 1) {
                    throw new RuntimeException("single web resource cannot split into multiple web resources!");
                }
                webResourceBatches.add(result.getLegacyWebResources().get(0));
                continue;
            }
            if (contextBatchKeys.isEmpty() || result.getLegacyWebResources().isEmpty()) continue;
            throw new RuntimeException("single web resource batch could be either legacy or not, but not both at the same time!");
        }
        return new Tuple<List<ContextBatch>, List<WebResourceBatch>>(contextBatches, webResourceBatches);
    }

    public static String calculateBundleHash(RawRequest rawRequest, RequestCache requestCache) {
        return UrlGenerationHelpers.calculateBundlesHash(UrlGenerationHelpers.getAllBundlesForContext(rawRequest, requestCache));
    }

    public static String calculateBundlesHash(List<Bundle> bundles) {
        HashBuilder hashBuilder = new HashBuilder();
        bundles.forEach(bundle -> {
            hashBuilder.add(bundle.getKey());
            hashBuilder.add(bundle.getVersion());
        });
        return hashBuilder.build();
    }

    protected static SplitSubBatches splitBatchIntoSubBatches(RequestCache requestCache, Found found, boolean doSorting) {
        String IS_STANDALONE = "_isStandalone";
        List<Bundle> bundles = requestCache.getSnapshot().toBundles(found.getFound());
        List<Bundle> bundlesForHashCalculation = requestCache.getSnapshot().toBundles(found.getAll());
        HashMap<Resource, Integer> allResourcesOrdered = new HashMap<Resource, Integer>();
        int i = 0;
        Comparator<Resource> RESOURCE_COMPARATOR = Comparator.comparingInt(allResourcesOrdered::get);
        LinkedHashMap<Map, List> uniqueParams = new LinkedHashMap<Map, List>();
        LinkedHashMap<Bundle, Map> legacyUniqueParams = new LinkedHashMap<Bundle, Map>();
        for (Bundle bundle : bundles) {
            for (Resource resource : bundle.getResources(requestCache).values()) {
                allResourcesOrdered.put(resource, i);
                ++i;
                Map<String, String> params = resource.getUrlParams();
                if (!resource.isBatchable()) {
                    params.put("_isStandalone", "true");
                }
                if (!resource.getParent().hasLegacyConditions()) {
                    uniqueParams.computeIfAbsent(params, key -> new ArrayList()).add(resource);
                    continue;
                }
                Map uniqueParameters = legacyUniqueParams.computeIfAbsent(resource.getParent(), key -> new LinkedHashMap());
                uniqueParameters.computeIfAbsent(params, key -> new ArrayList()).add(resource);
            }
        }
        SplitSubBatches result = new SplitSubBatches();
        ArrayList uniqueParamsSorted = new ArrayList(uniqueParams.keySet());
        ParamsComparator paramsComparator = new ParamsComparator();
        if (doSorting) {
            uniqueParamsSorted.sort(paramsComparator);
        }
        result.setAdditionalSortingRequired(paramsComparator.isAdditionalSortingRequired());
        LinkedHashMap legacyUniqueParamsSorted = new LinkedHashMap();
        for (Map.Entry entry : legacyUniqueParams.entrySet()) {
            ArrayList webResourceUniqueParamsSorted = new ArrayList(((Map)entry.getValue()).keySet());
            legacyUniqueParamsSorted.put(entry.getKey(), webResourceUniqueParamsSorted);
        }
        result.setContextSubBatches(new ArrayList<SubBatch>());
        result.setContextStandaloneResources(new ArrayList<Resource>());
        for (Map params : uniqueParamsSorted) {
            if (params.containsKey("_isStandalone")) {
                result.getContextStandaloneResources().addAll((Collection)uniqueParams.get(params));
                continue;
            }
            result.getContextSubBatches().add(new SubBatch(params, bundles, (List)uniqueParams.get(params), bundlesForHashCalculation));
        }
        result.getContextStandaloneResources().sort(RESOURCE_COMPARATOR);
        result.setLegacyWebResources(new ArrayList<WebResourceBatch>());
        for (Map.Entry entry : legacyUniqueParams.entrySet()) {
            ArrayList<Resource> webResourceStandaloneResources = new ArrayList<Resource>();
            ArrayList<SubBatch> webResourceSubBatches = new ArrayList<SubBatch>();
            for (Map params : (List)legacyUniqueParamsSorted.get(entry.getKey())) {
                if (params.containsKey("_isStandalone")) {
                    webResourceStandaloneResources.addAll((Collection)uniqueParams.get(params));
                    continue;
                }
                List resources = (List)((Map)legacyUniqueParams.get(entry.getKey())).get(params);
                webResourceSubBatches.add(new SubBatch(params, (Bundle)entry.getKey(), resources));
            }
            webResourceStandaloneResources.sort(RESOURCE_COMPARATOR);
            result.getLegacyWebResources().add(new WebResourceBatch(((Bundle)entry.getKey()).getKey(), webResourceSubBatches, webResourceStandaloneResources));
        }
        return result;
    }

    public static List<Resource> resourcesOfType(Collection<Resource> resources, String type) {
        ArrayList<Resource> result = new ArrayList<Resource>();
        for (Resource resource : resources) {
            if (!type.equals(resource.getNameOrLocationType())) continue;
            result.add(resource);
        }
        return result;
    }

    protected static StateEncodedUrlResult encodeStateInUrlIfSupported(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, String type, Map<String, String> params, Bundle bundle, List<Bundle> skipped, List<Bundle> excludedWithoutApplyingConditions) {
        ArrayList<Bundle> bundles = new ArrayList<Bundle>();
        bundles.add(bundle);
        return UrlGenerationHelpers.encodeStateInUrlIfSupported(requestCache, urlBuilderStrategy, type, params, bundles, skipped, excludedWithoutApplyingConditions);
    }

    protected static StateEncodedUrlResult encodeStateInUrlIfSupported(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, String type, Map<String, String> params, List<Bundle> bundles, List<Bundle> skipped, List<Bundle> excludedWithoutApplyingConditions) {
        DefaultUrlBuilder urlBuilder = new DefaultUrlBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addToQueryString(entry.getKey(), entry.getValue());
        }
        ArrayList<Bundle> bundlesWithSkippedAndExcluded = new ArrayList<Bundle>(bundles);
        bundlesWithSkippedAndExcluded.addAll(skipped);
        bundlesWithSkippedAndExcluded.addAll(excludedWithoutApplyingConditions);
        boolean taint = false;
        for (Bundle bundle : bundlesWithSkippedAndExcluded) {
            CachedCondition condition = bundle.getCondition();
            if (condition == null) continue;
            condition.addToUrlSafely(requestCache, urlBuilder, urlBuilderStrategy);
            taint |= condition.isLegacy();
        }
        for (Bundle bundle : bundles) {
            CachedTransformers transformers = bundle.getTransformers();
            if (transformers != null) {
                for (String locationType : bundle.getLocationResourceTypesFor(type)) {
                    taint |= transformers.addToUrlSafely(urlBuilder, urlBuilderStrategy, locationType, requestCache.getGlobals().getConfig().getTransformerCache(), bundle.getTransformerParameters(), bundle.getKey());
                }
            }
            for (String locationType : bundle.getLocationResourceTypesFor(type)) {
                requestCache.getGlobals().getConfig().getStaticTransformers().addToUrl(locationType, bundle.getTransformerParameters(), urlBuilder, urlBuilderStrategy);
            }
        }
        return new StateEncodedUrlResult(taint, urlBuilder);
    }

    protected static List<ResourceUrl> createResourceUrlsForRedirectResources(RequestCache requestCache, UrlBuildingStrategy urlBuilderStrategy, List<Resource> resources) {
        ArrayList<ResourceUrl> resourceUrls = new ArrayList<ResourceUrl>();
        for (Resource resource : resources) {
            CachedTransformers transformers;
            DefaultUrlBuilder urlBuilder = new DefaultUrlBuilder();
            for (Map.Entry<String, String> entry : resource.getParams().entrySet()) {
                urlBuilder.addToQueryString(entry.getKey(), entry.getValue());
            }
            boolean taint = false;
            Bundle webResource = resource.getParent();
            CachedCondition condition = webResource.getCondition();
            if (condition != null) {
                condition.addToUrlSafely(requestCache, urlBuilder, urlBuilderStrategy);
                taint = condition.isLegacy();
            }
            if ((transformers = webResource.getTransformers()) != null) {
                taint |= transformers.addToUrlSafely(urlBuilder, urlBuilderStrategy, resource.getLocationType(), requestCache.getGlobals().getConfig().getTransformerCache(), webResource.getTransformerParameters(), webResource.getKey());
            }
            requestCache.getGlobals().getConfig().getStaticTransformers().addToUrl(resource.getLocationType(), webResource.getTransformerParameters(), urlBuilder, urlBuilderStrategy);
            resourceUrls.add(new ResourceUrlImpl(requestCache.getGlobals(), resource, urlBuilder.buildParams(), urlBuilder.buildHash(), taint, urlBuilder.getPrebakeErrors()));
        }
        return resourceUrls;
    }

    private static List<Bundle> getAllBundlesForContext(RawRequest raw, RequestCache requestCache) {
        Found found = new BundleFinder(requestCache.getSnapshot()).included(raw.getIncludedAsLooseType()).excluded(raw.getExcludedAsLooseType(), (Predicate<Bundle>)Predicates.alwaysTrue()).deepFilter((Predicate<Bundle>)Predicates.not(UrlGenerationHelpers.hasLegacyCondition())).onDeepFilterFail(PredicateFailStrategy.CONTINUE).endAndGetResult();
        return requestCache.getGlobals().getSnapshot().toBundles(found.getAll());
    }
}

