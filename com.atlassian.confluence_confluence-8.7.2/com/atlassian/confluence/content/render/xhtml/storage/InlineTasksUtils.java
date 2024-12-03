/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.select.Elements
 *  org.jsoup.select.Selector$SelectorParseException
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.view.inlinetask.ViewInlineTaskConstants;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.core.PluginDataSourceFactory;
import java.util.ArrayList;
import java.util.List;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

public class InlineTasksUtils {
    public static final QName LI_TAG = new QName("http://www.w3.org/1999/xhtml", "li");
    public static final QName UL_TAG = new QName("http://www.w3.org/1999/xhtml", "ul");
    public static final QName TABLE_TAG = new QName("http://www.w3.org/1999/xhtml", "table");
    public static final QName P_TAG = new QName("http://www.w3.org/1999/xhtml", "p");
    public static final String INLINE_TASK_ID_DATA_ATTRIBUTE_NAME = new String("data-inline-task-id");
    public static final String INLINE_TASK_STATUS_NAME = new String("class");

    public static List<DataSource> getRequiredResources(DataSourceFactory dataSourceFactory, String htmlContent) {
        ArrayList<DataSource> resources = new ArrayList<DataSource>();
        PluginDataSourceFactory pluginDsFactory = dataSourceFactory.createForPlugin("com.atlassian.confluence.plugins.confluence-inline-tasks").orElse(null);
        if (pluginDsFactory == null) {
            return resources;
        }
        if (StringUtils.isNotEmpty((CharSequence)htmlContent)) {
            int incompleteTaskCount;
            int completeTaskCount;
            Document dom = Jsoup.parse((String)htmlContent);
            if (InlineTasksUtils.matchSelector(dom, InlineTasksUtils.convertClassStringToSelector("inline-task-list diff-inline-task-list")).size() > 0) {
                completeTaskCount = InlineTasksUtils.matchSelector(dom, "span" + InlineTasksUtils.convertClassStringToSelector("inline-task checked")).size();
                incompleteTaskCount = InlineTasksUtils.matchSelector(dom, "span" + InlineTasksUtils.convertClassStringToSelector("inline-task")).size();
            } else if (InlineTasksUtils.matchSelector(dom, InlineTasksUtils.convertClassStringToSelector(ViewInlineTaskConstants.TASK_LIST_IDENTIFYING_CSS_CLASS)).size() > 0) {
                completeTaskCount = InlineTasksUtils.matchSelector(dom, InlineTasksUtils.convertClassStringToSelector(ViewInlineTaskConstants.COMPLETED_TASK_CSS_CLASS)).size();
                incompleteTaskCount = InlineTasksUtils.matchSelector(dom, "[" + ViewInlineTaskConstants.TASK_ID_DATA_ATTRIBUTE + "]").size() - completeTaskCount;
            } else {
                completeTaskCount = 0;
                incompleteTaskCount = 0;
            }
            int dueDateCount = InlineTasksUtils.matchSelector(dom, "time").size() > 0 ? 1 : 0;
            if (completeTaskCount > 0) {
                resources.add(pluginDsFactory.getResourceFromModuleByName("inline-task-mail-resources", "inline-task-checked-icon").get());
            }
            if (incompleteTaskCount > 0) {
                resources.add(pluginDsFactory.getResourceFromModuleByName("inline-task-mail-resources", "inline-task-unchecked-icon").get());
            }
            if (dueDateCount > 0) {
                resources.add(pluginDsFactory.getResourceFromModuleByName("inline-task-mail-resources", "inline-task-calendar-icon").get());
            }
        }
        return resources;
    }

    private static Elements matchSelector(Document dom, String selector) {
        try {
            return dom.select(selector);
        }
        catch (Selector.SelectorParseException e) {
            return new Elements();
        }
    }

    private static String convertClassStringToSelector(String classString) {
        return "." + classString.replaceAll("\\s+", ".");
    }
}

