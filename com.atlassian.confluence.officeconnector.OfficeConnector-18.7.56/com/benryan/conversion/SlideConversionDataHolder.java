/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.bean.BeanFile
 *  com.atlassian.plugins.conversion.convert.bean.BeanResult
 */
package com.benryan.conversion;

import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.benryan.conversion.SlideDocConversionData;
import com.benryan.conversion.SlidePageConversionData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SlideConversionDataHolder
implements Iterable<SlidePageConversionData> {
    private final List<SlidePageConversionData> pages;

    public SlideConversionDataHolder(List<SlidePageConversionData> pages) {
        this.pages = pages;
    }

    public static SlideConversionDataHolder fromBeanResult(BeanResult beanResult, SlideDocConversionData data) {
        ArrayList<SlidePageConversionData> pages = new ArrayList<SlidePageConversionData>();
        for (BeanFile beanFile : beanResult.result) {
            pages.add(new SlidePageConversionData(data, beanFile));
        }
        pages.sort(Comparator.comparingInt(SlidePageConversionData::getSlideNum));
        return new SlideConversionDataHolder(Collections.unmodifiableList(pages));
    }

    public synchronized SlidePageConversionData getPage(int slideNum) {
        if (this.pages == null || this.pages.isEmpty()) {
            return null;
        }
        return this.pages.stream().filter(slide -> slide.getSlideNum() == slideNum).findFirst().orElse(null);
    }

    @Override
    public Iterator<SlidePageConversionData> iterator() {
        return this.pages.iterator();
    }
}

