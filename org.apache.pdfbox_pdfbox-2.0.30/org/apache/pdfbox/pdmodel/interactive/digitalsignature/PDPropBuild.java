/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDPropBuildDataDict;

public class PDPropBuild
implements COSObjectable {
    private COSDictionary dictionary;

    public PDPropBuild() {
        this.dictionary = new COSDictionary();
        this.dictionary.setDirect(true);
    }

    public PDPropBuild(COSDictionary dict) {
        this.dictionary = dict;
        this.dictionary.setDirect(true);
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public PDPropBuildDataDict getFilter() {
        PDPropBuildDataDict filter = null;
        COSDictionary filterDic = this.dictionary.getCOSDictionary(COSName.FILTER);
        if (filterDic != null) {
            filter = new PDPropBuildDataDict(filterDic);
        }
        return filter;
    }

    public void setPDPropBuildFilter(PDPropBuildDataDict filter) {
        this.dictionary.setItem(COSName.FILTER, (COSObjectable)filter);
    }

    public PDPropBuildDataDict getPubSec() {
        PDPropBuildDataDict pubSec = null;
        COSDictionary pubSecDic = this.dictionary.getCOSDictionary(COSName.PUB_SEC);
        if (pubSecDic != null) {
            pubSec = new PDPropBuildDataDict(pubSecDic);
        }
        return pubSec;
    }

    public void setPDPropBuildPubSec(PDPropBuildDataDict pubSec) {
        this.dictionary.setItem(COSName.PUB_SEC, (COSObjectable)pubSec);
    }

    public PDPropBuildDataDict getApp() {
        PDPropBuildDataDict app = null;
        COSDictionary appDic = this.dictionary.getCOSDictionary(COSName.APP);
        if (appDic != null) {
            app = new PDPropBuildDataDict(appDic);
        }
        return app;
    }

    public void setPDPropBuildApp(PDPropBuildDataDict app) {
        this.dictionary.setItem(COSName.APP, (COSObjectable)app);
    }
}

