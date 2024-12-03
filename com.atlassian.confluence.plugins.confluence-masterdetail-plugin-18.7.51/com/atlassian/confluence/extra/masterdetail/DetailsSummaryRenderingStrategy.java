/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import java.util.Map;

public enum DetailsSummaryRenderingStrategy {
    CLIENT_SIDE,
    SERVER_SIDE;


    private static int calculatePageSizeWithMaximum(Map<String, String> macroParameters, int internalMax) {
        return Math.min(internalMax, DetailsSummaryRenderingStrategy.getConfiguredPageSize(macroParameters));
    }

    private static int getConfiguredPageSize(Map<String, String> macroParameters) {
        try {
            return Integer.parseInt(macroParameters.get("pageSize"));
        }
        catch (NumberFormatException e) {
            return 30;
        }
    }

    static DetailsSummaryRenderingStrategy strategyFor(ConversionContext conversionContext) {
        boolean isDisplayOutput = ConversionContextOutputType.DISPLAY.value().equals(conversionContext.getOutputType());
        boolean asyncRenderSafe = conversionContext.isAsyncRenderSafe();
        return isDisplayOutput && asyncRenderSafe ? CLIENT_SIDE : SERVER_SIDE;
    }

    public int calculatePageSize(Map<String, String> macroParameters) {
        return DetailsSummaryRenderingStrategy.calculatePageSizeWithMaximum(macroParameters, 1000);
    }
}

