/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.binding;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public class FeatureListUtil {
    @NotNull
    public static WebServiceFeatureList mergeList(WebServiceFeatureList ... lists) {
        WebServiceFeatureList result = new WebServiceFeatureList();
        for (WebServiceFeatureList list : lists) {
            result.addAll(list);
        }
        return result;
    }

    @Nullable
    public static <F extends WebServiceFeature> F mergeFeature(@NotNull Class<F> featureType, @Nullable WebServiceFeatureList list1, @Nullable WebServiceFeatureList list2) throws WebServiceException {
        F feature2;
        Object feature1 = list1 != null ? (Object)list1.get(featureType) : null;
        F f = feature2 = list2 != null ? (F)list2.get(featureType) : null;
        if (feature1 == null) {
            return feature2;
        }
        if (feature2 == null) {
            return (F)feature1;
        }
        if (feature1.equals(feature2)) {
            return (F)feature1;
        }
        throw new WebServiceException(feature1 + ", " + feature2);
    }

    public static boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> featureType, @Nullable WebServiceFeatureList list1, @Nullable WebServiceFeatureList list2) throws WebServiceException {
        WebServiceFeature mergedFeature = FeatureListUtil.mergeFeature(featureType, list1, list2);
        return mergedFeature != null && mergedFeature.isEnabled();
    }
}

