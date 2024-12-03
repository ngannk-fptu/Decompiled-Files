/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserInterfaceState
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.SpaceComparator
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.collections4.comparators.ReverseComparator
 *  org.apache.commons.lang3.StringUtils
 *  org.joda.time.DateMidnight
 *  org.joda.time.base.AbstractInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.macros.dashboard.DashboardMacroSupport;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserInterfaceState;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.SpaceComparator;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.base.AbstractInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpacesListMacro
extends BaseMacro {
    public static final String MACRO_NAME = "spaces";
    private static final Logger log = LoggerFactory.getLogger(SpacesListMacro.class);
    @ComponentImport
    private SpaceManager spaceManager;
    @ComponentImport
    private LabelManager labelManager;
    @ComponentImport
    private LocaleManager localeManager;
    @ComponentImport
    private GlobalSettingsManager settingsManager;
    @ComponentImport
    private PermissionManager permissionManager;
    @ComponentImport
    private UserAccessor userAccessor;
    @ComponentImport
    private FormatSettingsManager formatSettingsManager;
    @ComponentImport
    private VelocityHelperService velocityHelperService;

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Map<String, String> macroParameters = SpacesListMacro.castMacroParams(parameters);
        try (Ticker ignored = Timers.start((String)"Macro: {spaces}");){
            String spacesSelectedTab;
            Map<String, Object> contextMap = this.getDefaultContextMap(macroParameters);
            DashboardMacroSupport dashboardSupport = this.getDashboardMacroSupport(Boolean.parseBoolean(macroParameters.get("includeArchivedSpaces")));
            String labels = macroParameters.get("labels");
            if (StringUtils.isNotEmpty((CharSequence)labels)) {
                contextMap.put("spacesForLabels", this.getSpacesForLabels(labels, dashboardSupport));
                contextMap.put("labelNames", this.getLabelNames(labels));
                String string2 = this.renderMacro(contextMap, "com/atlassian/confluence/plugins/macros/dashboard/spacelist-bylabels.vm");
                return string2;
            }
            String specifiedTabName = macroParameters.get("0");
            String displaySingleTab = Tabs.getTabName(specifiedTabName);
            if (StringUtils.isNotEmpty((CharSequence)displaySingleTab) && Tabs.isValidName(displaySingleTab)) {
                contextMap.put("displaySingleTab", displaySingleTab);
                spacesSelectedTab = displaySingleTab;
            } else {
                spacesSelectedTab = Tabs.getTabName(dashboardSupport.getSpacesSelectedTab());
            }
            contextMap.put("spacesSelectedTab", spacesSelectedTab);
            List<String> availableTeams = dashboardSupport.getViewableTeamLabels();
            String selectedTeam = this.getSelectedTeam(availableTeams, dashboardSupport);
            contextMap.put("viewableTeamLabels", availableTeams);
            contextMap.put("selectedTeam", selectedTeam);
            if (dashboardSupport.getRequest() != null) {
                contextMap.put("baseurl", this.buildBaseUrl(dashboardSupport.getRequest(), Arrays.asList("spacesSelectedTab", "spacesSelectedTeam")));
            }
            contextMap.put("favouriteSpaces", dashboardSupport.getFavouriteSpaces());
            contextMap.put("recentlyCreatedSpaces", dashboardSupport.getNewSpaces());
            ListBuilder<Space> listBuilder = dashboardSupport.getPermittedSpacesBuilder();
            ArrayList spacesEditable = new ArrayList(listBuilder.getAvailableSize());
            for (List spaces : listBuilder) {
                spacesEditable.addAll(spaces);
            }
            contextMap.put("spacesEditableByUser", spacesEditable);
            if (Tabs.ALL.equals(spacesSelectedTab)) {
                contextMap.put("allViewableSpaces", dashboardSupport.getAllViewableSpaces());
            }
            if (Tabs.CATEGORY.equals(spacesSelectedTab)) {
                contextMap.put("spacesForTeam", this.getSpacesForTeam(selectedTeam, dashboardSupport));
            }
            if (Tabs.NEW.equals(spacesSelectedTab)) {
                contextMap.put("sortedRecentlyCreatedSpaces", this.getSortedRecentlyCreatedSpaces(dashboardSupport));
            }
            String string = this.renderMacro(contextMap, "com/atlassian/confluence/plugins/macros/dashboard/macro-spacelist.vm");
            return string;
        }
        catch (IOException e) {
            throw new MacroException("Failed to render macro.", (Throwable)e);
        }
    }

    protected DashboardMacroSupport getDashboardMacroSupport(boolean includeArchivedSpaces) {
        return new DashboardMacroSupport(this.labelManager, this.spaceManager, this.localeManager, this.formatSettingsManager, this.userAccessor, this.permissionManager, includeArchivedSpaces);
    }

    protected String renderMacro(Map contextMap, String velocityTemplatePath) throws IOException {
        try {
            return this.velocityHelperService.getRenderedTemplate(velocityTemplatePath, contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to load the space list template.", (Throwable)e);
            throw new IOException(e.getMessage());
        }
    }

    private Map<String, Object> getDefaultContextMap(Map<String, String> macroParameters) {
        Map contextMap = this.velocityHelperService.createDefaultVelocityContext();
        contextMap.put("i18NBean", this.getI18n());
        contextMap.put("labelManager", this.labelManager);
        String widthParameter = HtmlUtil.htmlEncode((String)macroParameters.get("width"));
        if (!StringUtils.isNotEmpty((CharSequence)widthParameter)) {
            widthParameter = "100%";
        }
        contextMap.put("tableWidth", widthParameter);
        contextMap.put("remoteUser", AuthenticatedUserThreadLocal.get());
        contextMap.put("domainName", this.getGlobalSettings().getBaseUrl());
        return contextMap;
    }

    protected Settings getGlobalSettings() {
        return this.settingsManager.getGlobalSettings();
    }

    protected I18NBean getI18n() {
        return GeneralUtil.getI18n();
    }

    public String getName() {
        return MACRO_NAME;
    }

    public String getSelectedTeam(List<String> availableTeams, DashboardMacroSupport support) {
        String result;
        if (availableTeams == null || availableTeams.size() == 0) {
            return "";
        }
        String requestSelectedTeam = support.getRequestParameter("spacesSelectedTeam");
        if (requestSelectedTeam != null && availableTeams.contains(requestSelectedTeam)) {
            this.getUserInterfaceState().setDashboardSpacesSelectedTeam(requestSelectedTeam);
            result = requestSelectedTeam;
        } else {
            String existingPreference = this.getUserInterfaceState().getDashboardSpacesSelectedTeam();
            result = StringUtils.isNotEmpty((CharSequence)existingPreference) && availableTeams.contains(existingPreference) ? existingPreference : availableTeams.get(0);
        }
        return result;
    }

    public UserInterfaceState getUserInterfaceState() {
        return new UserInterfaceState((User)AuthenticatedUserThreadLocal.get());
    }

    public Set<Space> getSpacesForLabels(String labels, DashboardMacroSupport dashboardMacroSupport) {
        TreeSet<Space> result = new TreeSet<Space>((Comparator<Space>)new SpaceComparator());
        Set<Space> permittedSpaces = dashboardMacroSupport.getPermittedSpaces();
        List<String> labelNames = this.getLabelNames(labels);
        for (String labelName : labelNames) {
            Label label = this.labelManager.getLabel(labelName);
            if (label == null) continue;
            result.addAll(this.labelManager.getSpacesWithLabel(label));
        }
        result.retainAll(permittedSpaces);
        return result;
    }

    private List<String> getLabelNames(String commaDelimitedLabelNames) {
        StringTokenizer st = new StringTokenizer(commaDelimitedLabelNames, " ,");
        ArrayList<String> result = new ArrayList<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            String labelName = st.nextToken();
            result.add(labelName);
        }
        return result;
    }

    public List<Space> getSpacesForTeam(String selectedTeam, DashboardMacroSupport dashboardMacroSupport) {
        Set<Space> permittedSpaces = dashboardMacroSupport.getPermittedSpaces();
        if (StringUtils.isNotEmpty((CharSequence)selectedTeam)) {
            Label label = this.labelManager.getLabel("team:" + selectedTeam);
            if (label != null) {
                List spaces = this.labelManager.getSpacesWithLabel(label);
                spaces.retainAll(permittedSpaces);
                Collections.sort(spaces, new SpaceComparator());
                return spaces;
            }
            this.getUserInterfaceState().setDashboardSpacesSelectedTeam(null);
        }
        return Collections.emptyList();
    }

    public Map<String, List<Space>> getSortedRecentlyCreatedSpaces(DashboardMacroSupport support) {
        TreeMap datesToSpaceLists = new TreeMap((Comparator<DateMidnight>)new ReverseComparator());
        for (Space space : support.getNewSpaces()) {
            DateMidnight key = new DateMidnight(space.getCreationDate().getTime());
            if (datesToSpaceLists.get(key) == null) {
                ArrayList<Space> tempSpaceList = new ArrayList<Space>();
                tempSpaceList.add(space);
                datesToSpaceLists.put(key, tempSpaceList);
                continue;
            }
            ((List)datesToSpaceLists.get(key)).add(space);
        }
        LinkedHashMap<String, List<Space>> result = new LinkedHashMap<String, List<Space>>();
        for (Map.Entry entry : datesToSpaceLists.entrySet()) {
            result.put(support.getDateFormatter().formatDateFull(((AbstractInstant)entry.getKey()).toDate()), (List)entry.getValue());
        }
        for (List spaces : result.values()) {
            Collections.sort(spaces, new SpaceComparator());
        }
        return result;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    public void setSettingsManager(GlobalSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    private static Map<String, String> castMacroParams(Map macroParams) {
        return macroParams;
    }

    private String buildBaseUrl(HttpServletRequest request, List<String> ignoredParams) {
        String uri = request.getRequestURI();
        UrlBuilder baseUrl = new UrlBuilder(uri);
        Map<String, String[]> requestParams = SpacesListMacro.castRequestParams(request.getParameterMap());
        for (Map.Entry<String, String[]> param : requestParams.entrySet()) {
            if (ignoredParams.contains(param.getKey())) continue;
            baseUrl.add(param.getKey(), param.getValue()[0]);
        }
        return GeneralUtil.appendAmpersandOrQuestionMark((String)baseUrl.toString());
    }

    private static Map<String, String[]> castRequestParams(Map requestParams) {
        return requestParams;
    }

    public static enum Tabs {
        ALL("all"),
        CATEGORY("category"),
        FAVOURITE("favourite"),
        NEW("new");

        private final String tabName;
        private static final Set<String> ALL_TAB_NAMES;
        private static final Map<String, String> LEGACY_TAB_NAMES;

        private Tabs(String tabName) {
            this.tabName = tabName;
        }

        public String toString() {
            return this.tabName;
        }

        public boolean equals(String tabName) {
            return this.tabName.equals(tabName);
        }

        public static boolean isValidName(String tabName) {
            return ALL_TAB_NAMES.contains(tabName) || LEGACY_TAB_NAMES.containsKey(tabName);
        }

        public static String getTabName(String tabName) {
            if (ALL_TAB_NAMES.contains(tabName)) {
                return tabName;
            }
            return LEGACY_TAB_NAMES.get(tabName);
        }

        static {
            HashSet<String> tabNames = new HashSet<String>(Tabs.values().length);
            for (Tabs tab : Tabs.values()) {
                tabNames.add(tab.toString());
            }
            ALL_TAB_NAMES = Collections.unmodifiableSet(tabNames);
            HashMap<String, String> legacyTabNames = new HashMap<String, String>();
            legacyTabNames.put("favorite", FAVOURITE.toString());
            legacyTabNames.put("my", FAVOURITE.toString());
            legacyTabNames.put("team", CATEGORY.toString());
            LEGACY_TAB_NAMES = Collections.unmodifiableMap(legacyTabNames);
        }
    }
}

