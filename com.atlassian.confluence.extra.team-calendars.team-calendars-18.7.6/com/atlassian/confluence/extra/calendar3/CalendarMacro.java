/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.DefaultImagePlaceholder
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.extra.calendar3.BaseTeamCalendarsMacro;
import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

public class CalendarMacro
extends BaseTeamCalendarsMacro
implements EditorImagePlaceholder {
    private static Pattern PARAM_LENGTH_PATTERN = Pattern.compile(".*?([1-9]\\d*).*$");
    private static final Map<String, CalendarRenderer.CalendarView> CALENDAR_VIEW_MAP = Collections.unmodifiableMap(new HashMap<String, CalendarRenderer.CalendarView>(){
        {
            this.put("month", CalendarRenderer.CalendarView.month);
            this.put("week", CalendarRenderer.CalendarView.agendaWeek);
            this.put("list", CalendarRenderer.CalendarView.basicDay);
            this.put("upcoming", CalendarRenderer.CalendarView.basicDay);
            this.put("timeline", CalendarRenderer.CalendarView.timeline);
        }
    });
    private static final String PARAM_SHOWLEGEND_BOTTOM = "bottom";
    private static final String PARAM_SHOWLEGEND_RIGHT = "right";
    private final CalendarRenderer calendarRenderer;

    public CalendarMacro(I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, CalendarRenderer calendarRenderer) {
        super(i18NBeanFactory, localeManager);
        this.calendarRenderer = calendarRenderer;
    }

    public String execute(Map macroParameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)macroParameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException macroExecutionError) {
            throw new MacroException((Throwable)macroExecutionError);
        }
    }

    public String execute(Map<String, String> macroParams, String body, ConversionContext conversionContext) throws MacroExecutionException {
        int timelineHeight;
        int width;
        CalendarRenderer.CalendarRendererStatus status = this.calendarRenderer.canRenderCalender();
        if (!status.isCanRender()) {
            return status.getReason();
        }
        String defaultViewStr = StringUtils.defaultString(StringUtils.trim(macroParams.get("defaultView")));
        CalendarRenderer.CalendarView defaultView = CALENDAR_VIEW_MAP.get(defaultViewStr);
        if (StringUtils.isNotBlank(defaultViewStr) && null == defaultView) {
            return RenderUtils.blockError((String)this.getText("calendar3.error.invaliddefaultview", Arrays.asList(GeneralUtil.htmlEncode((String)defaultViewStr))), (String)"");
        }
        String widthStr = macroParams.get("width");
        try {
            width = this.getLengthParam(widthStr);
        }
        catch (IllegalArgumentException e) {
            return RenderUtils.blockError((String)this.getText("calendar3.error.invalidwidth", Arrays.asList(GeneralUtil.htmlEncode((String)widthStr))), (String)"");
        }
        String heightStr = macroParams.get("height");
        try {
            timelineHeight = this.getLengthParam(heightStr);
        }
        catch (IllegalArgumentException e) {
            return RenderUtils.blockError((String)this.getText("calendar3.error.invalidwidth", Arrays.asList(GeneralUtil.htmlEncode((String)heightStr))), (String)"");
        }
        Set<String> embeddedSubCalendarIds = this.getEmbeddedSubCalendars(macroParams);
        if (embeddedSubCalendarIds.isEmpty()) {
            return RenderUtils.blockError((String)this.getText("calendar3.error.nosubcalendarstodisplay"), (String)"");
        }
        boolean showLegend = this.isShowLegend(macroParams);
        CalendarRenderer.CalendarView initialView = this.getInitialView(defaultView);
        CalendarRenderer.RenderParamsBuilder renderParamsBuilder = this.calendarRenderer.newRenderParamsBuilder().subCalendars(embeddedSubCalendarIds).initialView(initialView).defaultFirePublicView(initialView).width(width).timelineHeight(timelineHeight).inlineAddEventButton(true).viewButtons(false).createSubCalendarDialogOnShow(false).popularSubCalendarsDialogOnShow(false).showHiddenSubCalendars(true).hideSubCalendarsPanel(!this.shouldShowSubCalendarsPanel(showLegend, initialView)).showSubCalendarNameInEventPopup(true).ignoreInvalidSubCalendarIds(true).hideCalendarTypes(true).hideWatchMenuItem(true).hideRemoveMenuItem(true).hideEditMenuItem(true).hideColorGrid(false).hideCategories(true).showLegendBottom(this.isLegendPositionedAtBottom(macroParams)).hideWeekends(BooleanUtils.toBoolean(macroParams.get("hideWeekends"))).calendarContext(CalendarRenderer.CalendarContext.page).setMacroRendering(true);
        ConversionContextOutputType conversionContextOutputType = ConversionContextOutputType.valueOf((String)StringUtils.upperCase(conversionContext.getOutputType()));
        if (ConversionContextOutputType.PREVIEW == conversionContextOutputType) {
            renderParamsBuilder.readOnly(true);
        }
        return ConversionContextOutputType.DISPLAY == conversionContextOutputType || ConversionContextOutputType.PREVIEW == conversionContextOutputType ? this.calendarRenderer.render(renderParamsBuilder.build()) : this.calendarRenderer.renderStatic(renderParamsBuilder.build());
    }

    private int getLengthParam(String param) {
        String lengthStr = StringUtils.trim(param);
        int length = -1;
        Matcher lengthMatcher = PARAM_LENGTH_PATTERN.matcher(StringUtils.defaultString(lengthStr));
        if (lengthMatcher.matches()) {
            length = Integer.parseInt(lengthMatcher.group(1));
        } else if (StringUtils.isNotBlank(lengthStr)) {
            throw new IllegalArgumentException(lengthStr);
        }
        return length;
    }

    private boolean isLegendPositionedAtBottom(Map<String, String> macroParams) {
        String showLegendStr = macroParams.get("showLegend");
        return StringUtils.isBlank(showLegendStr) || StringUtils.equals(PARAM_SHOWLEGEND_BOTTOM, showLegendStr);
    }

    private boolean isShowLegend(Map<String, String> macroParams) {
        String showLegendStr = macroParams.get("showLegend");
        return StringUtils.isEmpty(showLegendStr) || BooleanUtils.toBoolean(showLegendStr) || StringUtils.equals(PARAM_SHOWLEGEND_BOTTOM, showLegendStr) || StringUtils.equals(PARAM_SHOWLEGEND_RIGHT, showLegendStr);
    }

    private CalendarRenderer.CalendarView getInitialView(CalendarRenderer.CalendarView defaultView) {
        return null == defaultView ? CalendarRenderer.CalendarView.month : defaultView;
    }

    private boolean shouldShowSubCalendarsPanel(boolean showLegend, CalendarRenderer.CalendarView initialView) {
        return showLegend && (initialView.equals((Object)CalendarRenderer.CalendarView.month) || initialView.equals((Object)CalendarRenderer.CalendarView.agendaWeek) || initialView.equals((Object)CalendarRenderer.CalendarView.timeline));
    }

    private Set<String> getEmbeddedSubCalendars(Map<String, String> macroParams) {
        return new LinkedHashSet<String>(Arrays.asList(StringUtils.split(StringUtils.defaultString(StringUtils.defaultIfEmpty(macroParams.get("id"), macroParams.get("subCalendars"))), ";, ")));
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> macroParameters, ConversionContext conversionContext) {
        Set<String> embeddedSubCalendarIds = this.getEmbeddedSubCalendars(macroParameters);
        StringBuilder imagePlacePlaceholderGeneratorUrlBuilder = new StringBuilder("/plugins/servlet/team-calendars/macro/image-placeholder/calendar.png?_=").append(System.currentTimeMillis());
        if (!embeddedSubCalendarIds.isEmpty()) {
            imagePlacePlaceholderGeneratorUrlBuilder.append("&subCalendarIds=").append(GeneralUtil.urlEncode((String)StringUtils.join(embeddedSubCalendarIds, ",")));
        }
        String defaultViewStr = StringUtils.defaultString(StringUtils.trim(macroParameters.get("defaultView")));
        CalendarRenderer.CalendarView defaultView = CALENDAR_VIEW_MAP.get(defaultViewStr);
        imagePlacePlaceholderGeneratorUrlBuilder.append("&defaultView=").append(GeneralUtil.urlEncode((String)this.getInitialView(defaultView).toString()));
        return new DefaultImagePlaceholder(imagePlacePlaceholderGeneratorUrlBuilder.toString(), false, null);
    }
}

