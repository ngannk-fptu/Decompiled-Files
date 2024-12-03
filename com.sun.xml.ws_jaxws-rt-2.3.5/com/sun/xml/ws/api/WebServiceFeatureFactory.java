/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceFeature
 */
package com.sun.xml.ws.api;

import com.sun.xml.ws.api.WSFeatureList;
import com.sun.xml.ws.binding.WebServiceFeatureList;
import java.lang.annotation.Annotation;
import javax.xml.ws.WebServiceFeature;

public class WebServiceFeatureFactory {
    public static WSFeatureList getWSFeatureList(Iterable<Annotation> ann) {
        WebServiceFeatureList list = new WebServiceFeatureList();
        list.parseAnnotations(ann);
        return list;
    }

    public static WebServiceFeature getWebServiceFeature(Annotation ann) {
        return WebServiceFeatureList.getFeature(ann);
    }
}

