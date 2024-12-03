/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.util;

import java.util.HashMap;
import java.util.Map;

public class JiraIssuePdfExportUtil {
    private static final int PDF_EXPORT_DEFAULT_FONT_SIZE = 7;

    private JiraIssuePdfExportUtil() {
    }

    public static void addedHelperDataForPdfExport(Map<String, Object> contextMap, int numberOfColumns) {
        if (numberOfColumns > 0) {
            int limitNoOfColumns = 16;
            FontRangeHelper.getInstance().setRange(1, 11, 7).setRange(12, 13, 6).setRange(14, 16, 5);
            if (numberOfColumns > limitNoOfColumns) {
                contextMap.put("fontSize", FontRangeHelper.getInstance().getFontSize(limitNoOfColumns) + "pt");
                contextMap.put("statusFontSize", FontRangeHelper.getInstance().getFontSize(limitNoOfColumns) - 1 + "pt");
                contextMap.put("iconHeight", FontRangeHelper.getInstance().getFontSize(limitNoOfColumns) + 5 + "px");
                contextMap.put("isLimit", Boolean.TRUE);
            } else {
                contextMap.put("fontSize", FontRangeHelper.getInstance().getFontSize(numberOfColumns) + "pt");
                contextMap.put("statusFontSize", FontRangeHelper.getInstance().getFontSize(numberOfColumns) - 1 + "pt");
                contextMap.put("iconHeight", FontRangeHelper.getInstance().getFontSize(numberOfColumns) + 5 + "px");
                contextMap.put("isLimit", Boolean.FALSE);
            }
        }
    }

    private static class FontRangeHelper {
        private static FontRangeHelper instance = null;
        private final Map<Integer[], Integer> internalRangeMap = new HashMap<Integer[], Integer>();

        private FontRangeHelper() {
        }

        public static FontRangeHelper getInstance() {
            if (null == instance) {
                instance = new FontRangeHelper();
            }
            return instance;
        }

        private FontRangeHelper setRange(int start, int end, int fontSize) {
            Integer[] range = new Integer[]{start, end};
            this.internalRangeMap.put(range, fontSize);
            return this;
        }

        private int getFontSize(int numOfColumn) {
            for (Map.Entry<Integer[], Integer> entry : this.internalRangeMap.entrySet()) {
                Integer[] range = entry.getKey();
                if (numOfColumn < range[0] || numOfColumn > range[1]) continue;
                return entry.getValue();
            }
            return 7;
        }
    }
}

