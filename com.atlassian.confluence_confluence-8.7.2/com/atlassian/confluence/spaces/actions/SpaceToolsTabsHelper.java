/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.ozymandias.SafePluginPointAccess
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.ConditionalDescriptor
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
 *  com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor
 *  com.atlassian.plugin.web.model.WebLabel
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.struts2.ServletActionContext
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugin.descriptor.web.descriptors.ConfluenceWebSectionModuleDescriptor;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.ozymandias.SafePluginPointAccess;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.ConditionalDescriptor;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.plugin.web.descriptors.WebSectionModuleDescriptor;
import com.atlassian.plugin.web.model.WebLabel;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.struts2.ServletActionContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceToolsTabsHelper {
    private static final String SPACE_TOOLS_WEB_SECTION_KEY = "system.space.tools";
    private static final String SPACE_TOOLS_ADDONS_WEB_SECTION_KEY = "system.space.tools/addons";
    private static final String SPACE_OPERATIONS_WEB_SECTION_KEY = "system.space.advanced";
    private static final ImmutableList<String> SPACE_TOOLS_SECTION_KEYS = ImmutableList.of((Object)"system.space.tools", (Object)"system.space.advanced");
    private static final String SPACE_ADMIN_WEB_SECTION_KEY = "system.space.admin";
    private static final ImmutableList<String> ADMIN_SPACE_TOOLS_SECTION_KEYS = ImmutableList.of((Object)"system.space.tools", (Object)"system.space.advanced", (Object)"system.space.admin");
    private static final String SPACE_WEB_SECTION_KEY = "system.space";
    private static final Logger log = LoggerFactory.getLogger(SpaceToolsTabsHelper.class);
    private WebInterfaceManager webInterfaceManager;
    private PermissionManager permissionManager;
    private final Set<String> SECTIONS_FOR_ADDON_TAB = ImmutableSet.of((Object)"system.space.advanced/advanced", (Object)"system.space.advanced/exportsection", (Object)"system.space.advanced/subscribesection", (Object)"system.space.admin/spaceops", (Object)"system.space.admin/security", (Object)"system.space.admin/looknfeel", (Object[])new String[]{"system.space.admin/import", "system.space.admin/spaceops"});
    private static final String LAST_WEB_ITEM_COOKIE = "confluence.last-web-item-clicked";

    public SpaceToolsTabsHelper(WebInterfaceManager webInterfaceManager, PermissionManager permissionManager) {
        this.webInterfaceManager = webInterfaceManager;
        this.permissionManager = permissionManager;
    }

    public SpaceToolsTabs getNavigation(ConfluenceActionSupport action) {
        return this.getNavigation(action, (SpaceToolsTabs spaceToolsTabs) -> {});
    }

    public SpaceToolsTabs getNavigation(ConfluenceActionSupport action, String selectedWebItem) {
        return this.getNavigation(action, (SpaceToolsTabs spaceToolsTabs) -> {
            String lastWebItemClicked;
            boolean selectedItemFound = false;
            if (StringUtils.isNotBlank((CharSequence)selectedWebItem)) {
                selectedItemFound = spaceToolsTabs.setSelected(selectedWebItem);
            }
            if (!selectedItemFound && StringUtils.isNotBlank((CharSequence)(lastWebItemClicked = this.getLastWebItemClickedFromCookie()))) {
                spaceToolsTabs.setSelected(lastWebItemClicked);
            }
        });
    }

    private SpaceToolsTabs getNavigation(ConfluenceActionSupport action, Consumer<SpaceToolsTabs> consumer) {
        try (Ticker ignored = Timers.start((String)SPACE_TOOLS_WEB_SECTION_KEY);){
            Map<String, Object> context = action.getWebInterfaceContext().toMap();
            SpaceToolsTabs spaceToolsTabs = this.getSpaceToolsTabs(context, action);
            consumer.accept(spaceToolsTabs);
            SpaceToolsTabs spaceToolsTabs2 = spaceToolsTabs;
            return spaceToolsTabs2;
        }
    }

    private SpaceToolsTabs getSpaceToolsTabs(Map<String, Object> context, ConfluenceActionSupport action) {
        ArrayList<Pair> sectionItems = new ArrayList<Pair>();
        ArrayList addonTabItems = new ArrayList();
        ImmutableList<String> sectionLocations = this.canAdminister(action) ? ADMIN_SPACE_TOOLS_SECTION_KEYS : SPACE_TOOLS_SECTION_KEYS;
        for (Object sectionLocation : sectionLocations) {
            List sections = this.webInterfaceManager.getDisplayableSections((String)sectionLocation, context);
            for (WebSectionModuleDescriptor section : sections) {
                String sectionFullKey = (String)sectionLocation + "/" + section.getKey();
                List webItems = this.webInterfaceManager.getItems(sectionFullKey);
                if (!this.SECTIONS_FOR_ADDON_TAB.contains(sectionFullKey)) {
                    sectionItems.add(Pair.pair((Object)section, (Object)webItems));
                    continue;
                }
                addonTabItems.addAll(webItems);
            }
        }
        SpaceToolsTabs spaceToolsTabs = new SpaceToolsTabs();
        for (Pair pair : sectionItems) {
            WebSectionModuleDescriptor section = (WebSectionModuleDescriptor)pair.left();
            String sectionFullKey = section.getLocation() + "/" + section.getKey();
            Iterable webItems = (Iterable)pair.right();
            Iterable allItems = SPACE_TOOLS_ADDONS_WEB_SECTION_KEY.equals(sectionFullKey) ? com.google.common.collect.Iterables.concat((Iterable)webItems, addonTabItems) : webItems;
            Iterable displayedItems = com.google.common.collect.Iterables.filter((Iterable)allItems, desc -> SpaceToolsTabsHelper.shouldDisplay(desc, context));
            Iterables.first((Iterable)displayedItems).forEach(firstItem -> spaceToolsTabs.addTopLevelTab(sectionFullKey, (ConfluenceWebSectionModuleDescriptor)section, (WebItemModuleDescriptor)firstItem, displayedItems, context));
        }
        ArrayList legacyWebItems = Lists.newArrayList((Iterable)this.webInterfaceManager.getDisplayableItems(SPACE_WEB_SECTION_KEY, context));
        for (WebItemModuleDescriptor legacyWebItem : legacyWebItems) {
            spaceToolsTabs.addTopLevelTab(SPACE_WEB_SECTION_KEY, legacyWebItem, context);
        }
        return spaceToolsTabs;
    }

    private static <T extends ConditionalDescriptor & ModuleDescriptor> boolean shouldDisplay(T descriptor, Map<String, Object> context) {
        try {
            return descriptor.getCondition() == null || descriptor.getCondition().shouldDisplay(context);
        }
        catch (Throwable t) {
            SafePluginPointAccess.handleException((Throwable)t, (ModuleDescriptor)descriptor);
            return false;
        }
    }

    private @Nullable String getLastWebItemClickedFromCookie() {
        String webSectionAndItem = this.getLastWebSectionAndItemClicked();
        if (webSectionAndItem == null || !webSectionAndItem.contains("/")) {
            return webSectionAndItem;
        }
        return webSectionAndItem.substring(webSectionAndItem.lastIndexOf("/") + 1);
    }

    private @Nullable String getLastWebSectionAndItemClicked() {
        HttpServletRequest request = ServletActionContext.getRequest();
        if (request != null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (!cookie.getName().equals(LAST_WEB_ITEM_COOKIE)) continue;
                return HtmlUtil.urlDecode(cookie.getValue());
            }
        }
        return null;
    }

    private boolean canAdminister(ConfluenceActionSupport action) {
        Space space = action instanceof Spaced ? ((Spaced)((Object)action)).getSpace() : null;
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, space);
    }

    public static class SpaceToolsTabItem {
        private String label;
        private String link;
        private String linkId;
        private String sectionKey;
        private String sectionFullKey;
        private String webItemKey;
        private boolean selected;
        private boolean hideSingleWebItem;

        public SpaceToolsTabItem(String label, String link, String linkId, @Nullable String sectionKey, @Nullable String sectionFullKey, String webItemKey, boolean hideSingleWebItem) {
            this.label = label;
            this.link = link;
            this.linkId = linkId;
            this.sectionKey = sectionKey;
            this.sectionFullKey = sectionFullKey;
            this.webItemKey = webItemKey;
            this.hideSingleWebItem = hideSingleWebItem;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getLink() {
            return this.link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLinkId() {
            return this.linkId;
        }

        public void setLinkId(String linkId) {
            this.linkId = linkId;
        }

        public @Nullable String getSectionKey() {
            return this.sectionKey;
        }

        public void setSectionKey(@Nullable String sectionKey) {
            this.sectionKey = sectionKey;
        }

        public @Nullable String getSectionFullKey() {
            return this.sectionFullKey;
        }

        public void setSectionFullKey(@Nullable String sectionFullKey) {
            this.sectionFullKey = sectionFullKey;
        }

        public String getWebItemKey() {
            return this.webItemKey;
        }

        public void setWebItemKey(String webItemKey) {
            this.webItemKey = webItemKey;
        }

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isHideSingleWebItem() {
            return this.hideSingleWebItem;
        }
    }

    public static class SpaceToolsTabs {
        private List<SpaceToolsTabItem> firstLevelNavigation = new ArrayList<SpaceToolsTabItem>();
        private List<Iterable<SpaceToolsTabItem>> secondLevelNavigations = new ArrayList<Iterable<SpaceToolsTabItem>>();
        private List<SpaceToolsTabItem> selectedSecondLevelNavigation;

        public void addTopLevelTab(String sectionFullKey, ConfluenceWebSectionModuleDescriptor section, WebItemModuleDescriptor firstWebItem, Iterable<WebItemModuleDescriptor> secondLevelNavigation, Map<String, Object> context) {
            HttpServletRequest req = ServletActionContext.getRequest();
            this.firstLevelNavigation.add(new SpaceToolsTabItem(section.getWebLabel().getDisplayableLabel(req, context), firstWebItem.getLink().getDisplayableUrl(req, context), firstWebItem.getLink().getId(), section.getKey(), sectionFullKey, firstWebItem.getKey(), section.hideSingleWebItem()));
            this.secondLevelNavigations.add(this.createSecondLevelNavigation(secondLevelNavigation, context));
        }

        public void addTopLevelTab(String sectionFullKey, WebItemModuleDescriptor legacyWebItem, Map<String, Object> context) {
            HttpServletRequest req = ServletActionContext.getRequest();
            if (legacyWebItem.getLink() == null || legacyWebItem.getWebLabel() == null) {
                log.warn("Skipping adding legacy webitem {} as it has no link and/or label", (Object)legacyWebItem.getKey());
                return;
            }
            this.firstLevelNavigation.add(new SpaceToolsTabItem(legacyWebItem.getWebLabel().getDisplayableLabel(req, context), legacyWebItem.getLink().getDisplayableUrl(req, context), legacyWebItem.getLink().getId(), legacyWebItem.getKey(), sectionFullKey, legacyWebItem.getKey(), false));
            this.secondLevelNavigations.add(this.createSecondLevelNavigation(Collections.emptyList(), context));
        }

        public boolean setSelected(String selectedWebItem) {
            for (int i = 0; i < this.secondLevelNavigations.size(); ++i) {
                ArrayList secondLevelNavigation = Lists.newArrayList(this.secondLevelNavigations.get(i));
                for (SpaceToolsTabItem item : secondLevelNavigation) {
                    if (!selectedWebItem.equals(item.getWebItemKey())) continue;
                    this.firstLevelNavigation.get(i).setSelected(true);
                    this.selectedSecondLevelNavigation = secondLevelNavigation;
                    item.setSelected(true);
                    return true;
                }
            }
            for (SpaceToolsTabItem item : this.firstLevelNavigation) {
                if (!selectedWebItem.equals(item.getWebItemKey())) continue;
                item.setSelected(true);
                this.selectedSecondLevelNavigation = Collections.emptyList();
                return true;
            }
            return false;
        }

        private Iterable<SpaceToolsTabItem> createSecondLevelNavigation(Iterable<WebItemModuleDescriptor> secondLevelNavigation, Map<String, Object> context) {
            HttpServletRequest req = ServletActionContext.getRequest();
            return Iterables.flatMap(secondLevelNavigation, webItem -> {
                try {
                    return Option.some((Object)new SpaceToolsTabItem(webItem.getWebLabel().getDisplayableLabel(req, context), webItem.getLink().getDisplayableUrl(req, context), webItem.getLink().getId(), null, null, webItem.getKey(), false));
                }
                catch (NullPointerException e) {
                    if (webItem == null) {
                        log.warn("Didn't add a 'null' web item to the secondary navigation area of Space Tools.");
                    } else {
                        WebLabel itemLabel = webItem.getWebLabel();
                        log.warn("Encountered NPE trying to add the secondary navigation item '" + (itemLabel != null ? itemLabel.getKey() : "(no label)") + "' to Space Tools - item was skipped.");
                    }
                    return Option.none();
                }
            });
        }

        public List<SpaceToolsTabItem> getFirstLevelNavigation() {
            return this.firstLevelNavigation;
        }

        public List<SpaceToolsTabItem> getSecondLevelNavigation() {
            return this.selectedSecondLevelNavigation;
        }

        public String toString() {
            return new ToStringBuilder((Object)this).append("firstLevelNavigation", this.firstLevelNavigation != null ? Integer.valueOf(this.firstLevelNavigation.size()) : null).append("secondLevelNavigation", this.selectedSecondLevelNavigation != null ? Integer.valueOf(this.selectedSecondLevelNavigation.size()) : null).toString();
        }
    }
}

