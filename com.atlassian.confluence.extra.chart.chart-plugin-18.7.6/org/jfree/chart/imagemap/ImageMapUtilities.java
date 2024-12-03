/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.imagemap;

import java.io.IOException;
import java.io.PrintWriter;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.imagemap.OverLIBToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.StandardURLTagFragmentGenerator;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.util.StringUtils;

public class ImageMapUtilities {
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info) throws IOException {
        ImageMapUtilities.writeImageMap(writer, name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, boolean useOverLibForToolTips) throws IOException {
        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator = null;
        toolTipTagFragmentGenerator = useOverLibForToolTips ? new OverLIBToolTipTagFragmentGenerator() : new StandardToolTipTagFragmentGenerator();
        ImageMapUtilities.writeImageMap(writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator());
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) throws IOException {
        writer.println(ImageMapUtilities.getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }

    public static String getImageMap(String name, ChartRenderingInfo info) {
        return ImageMapUtilities.getImageMap(name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    public static String getImageMap(String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) {
        StringBuffer sb = new StringBuffer();
        sb.append("<map id=\"" + ImageMapUtilities.htmlEscape(name) + "\" name=\"" + ImageMapUtilities.htmlEscape(name) + "\">");
        sb.append(StringUtils.getLineSeparator());
        EntityCollection entities = info.getEntityCollection();
        if (entities != null) {
            int count = entities.getEntityCount();
            for (int i = count - 1; i >= 0; --i) {
                String area;
                ChartEntity entity = entities.getEntity(i);
                if (entity.getToolTipText() == null && entity.getURLText() == null || (area = entity.getImageMapAreaTag(toolTipTagFragmentGenerator, urlTagFragmentGenerator)).length() <= 0) continue;
                sb.append(area);
                sb.append(StringUtils.getLineSeparator());
            }
        }
        sb.append("</map>");
        return sb.toString();
    }

    public static String htmlEscape(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Null 'input' argument.");
        }
        StringBuffer result = new StringBuffer();
        int length = input.length();
        for (int i = 0; i < length; ++i) {
            char c = input.charAt(i);
            if (c == '&') {
                result.append("&amp;");
                continue;
            }
            if (c == '\"') {
                result.append("&quot;");
                continue;
            }
            if (c == '<') {
                result.append("&lt;");
                continue;
            }
            if (c == '>') {
                result.append("&gt;");
                continue;
            }
            if (c == '\'') {
                result.append("&#39;");
                continue;
            }
            if (c == '\\') {
                result.append("&#092;");
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public static String javascriptEscape(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Null 'input' argument.");
        }
        StringBuffer result = new StringBuffer();
        int length = input.length();
        for (int i = 0; i < length; ++i) {
            char c = input.charAt(i);
            if (c == '\"') {
                result.append("\\\"");
                continue;
            }
            if (c == '\'') {
                result.append("\\'");
                continue;
            }
            if (c == '\\') {
                result.append("\\\\");
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }
}

