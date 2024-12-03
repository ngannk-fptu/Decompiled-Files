/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.importexport.resource.DownloadResourceWriter
 *  com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.links.linktypes.AbstractPageLink
 *  com.atlassian.confluence.links.linktypes.AttachmentLink
 *  com.atlassian.confluence.links.linktypes.BlogPostLink
 *  com.atlassian.confluence.links.linktypes.PageLink
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailInfo
 *  com.atlassian.confluence.pages.thumbnail.ThumbnailManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.core.util.LocaleUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.chart;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.chart.ChartData;
import com.atlassian.confluence.extra.chart.ChartUtil;
import com.atlassian.confluence.extra.chart.ConfluenceChartFactory;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.links.linktypes.AttachmentLink;
import com.atlassian.confluence.links.linktypes.BlogPostLink;
import com.atlassian.confluence.links.linktypes.PageLink;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailInfo;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.core.util.LocaleUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.awt.Color;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChartMacro
extends BaseMacro
implements Macro {
    private final SettingsManager settingsManager;
    private final LanguageManager languageManager;
    private final AttachmentManager attachmentManager;
    private final PermissionManager permissionManager;
    private final ThumbnailManager thumbnailManager;
    private final WritableDownloadResourceManager downloadResourceManager;
    private final XhtmlContent xhtmlContent;
    private final LinkResolver linkResolver;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18nBeanFactory;
    private static final Logger log = LoggerFactory.getLogger(ChartMacro.class);
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final int MAX_WIDTH = Integer.getInteger("confluence.chart.macro.width.max", 3000);
    private static final int MAX_HEIGHT = Integer.getInteger("confluence.chart.macro.height.max", 3000);
    private static final double DEFAULT_PIE_EXPLODE = 0.3;
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String HORIZONTAL = "horizontal";
    private static final String VERTICAL = "vertical";
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String NEW = "new";
    private static final String REPLACE = "replace";
    private static final String KEEP = "keep";
    private static final String PIE_TYPE = "pie";
    private static final String BAR_TYPE = "bar";
    private static final String LINE_TYPE = "line";
    private static final String AREA_TYPE = "area";
    private static final String XYLINE_TYPE = "xyline";
    private static final String XYAREA_TYPE = "xyarea";
    private static final String XYBAR_TYPE = "xybar";
    private static final String XYSTEP_TYPE = "xystep";
    private static final String XYSTEPAREA_TYPE = "xysteparea";
    private static final String SCATTER_TYPE = "scatter";
    private static final String TIMESERIES_TYPE = "timeseries";
    private static final String GANTT_TYPE = "gantt";
    private static final String START = "start";
    private static final String MIDDLE = "middle";
    private static final String END = "end";
    private static final Map<String, Integer> COLOR_MAP = Collections.unmodifiableMap(new HashMap<String, Integer>(){
        {
            this.put("aqua", 65535);
            this.put("black", 0);
            this.put("blue", 255);
            this.put("cyan", 65535);
            this.put("fuchsia", 0xFF00FF);
            this.put("gray", 0x808080);
            this.put("green", 65280);
            this.put("lime", 65280);
            this.put("maroon", 0x800000);
            this.put("navy", 128);
            this.put("olive", 0x808000);
            this.put("purple", 0xFFC0FF);
            this.put("red", 0xFF0000);
            this.put("silver", 0xC0C0C0);
            this.put("teal", 0x808000);
            this.put("violet", 0xEE82EE);
            this.put("white", 0xFFFFFF);
            this.put("yellow", 0xFFFF00);
        }
    });

    public ChartMacro(@ComponentImport SettingsManager settingsManager, @ComponentImport LanguageManager languageManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport PermissionManager permissionManager, @ComponentImport ThumbnailManager thumbnailManager, @ComponentImport WritableDownloadResourceManager downloadResourceManager, @ComponentImport XhtmlContent xhtmlContent, @ComponentImport LinkResolver linkResolver, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18nBeanFactory) {
        this.settingsManager = settingsManager;
        this.languageManager = languageManager;
        this.attachmentManager = attachmentManager;
        this.permissionManager = permissionManager;
        this.thumbnailManager = thumbnailManager;
        this.downloadResourceManager = downloadResourceManager;
        this.xhtmlContent = xhtmlContent;
        this.linkResolver = linkResolver;
        this.localeManager = localeManager;
        this.i18nBeanFactory = i18nBeanFactory;
    }

    public TokenType getTokenType(Map map, String s, RenderContext renderContext) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
            ArrayList errorList = new ArrayList();
            body = this.xhtmlContent.convertWikiToView(body, (ConversionContext)conversionContext, errorList);
            if (!errorList.isEmpty()) {
                for (RuntimeException runtimeException : errorList) {
                    log.error("RuntimeException while parsing wiki markup ", (Throwable)runtimeException);
                }
                throw new MacroException(this.getI18NBean().getText("confluence.extra.chart.chart.error.parseWikiToStorage"));
            }
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)conversionContext);
        }
        catch (XhtmlException | MacroExecutionException | XMLStreamException e) {
            throw new MacroException(e);
        }
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute(Map<String, String> parameters, String chartDataHtml, ConversionContext conversionContext) throws MacroExecutionException {
        parameters = this.toLowerCase(parameters);
        try {
            String dataDisplay;
            StringBuilder chartHtmlBuilder;
            block21: {
                chartHtmlBuilder = new StringBuilder();
                String xDisplayData = this.getStringParameter(parameters, "displaydata", FALSE);
                dataDisplay = this.getStringParameter(parameters, "datadisplay", xDisplayData);
                if (!dataDisplay.equalsIgnoreCase(FALSE) && dataDisplay.equalsIgnoreCase(BEFORE)) {
                    chartHtmlBuilder.append(chartDataHtml);
                }
                try {
                    Attachment chartImageAttachment = this.getAttachment(parameters, conversionContext, chartDataHtml);
                    if (chartImageAttachment != null) {
                        chartHtmlBuilder.append(this.getChartImageHtml(this.getBooleanParameter(parameters, "thumbnail", false), chartImageAttachment));
                        break block21;
                    }
                    String imageFormat = this.getStringParameter(parameters, "imageformat", "png");
                    if (!this.isImageFormatSupported(imageFormat)) {
                        log.error(String.format("Invalid image format specified: %s", imageFormat));
                        throw new MacroExecutionException(this.getI18NBean().getText("confluence.extra.chart.chart.error.invalidImageFormat", Arrays.asList(imageFormat)));
                    }
                    DownloadResourceWriter downloadResourceWriter = this.downloadResourceManager.getResourceWriter(StringUtils.defaultString((String)AuthenticatedUserThreadLocal.getUsername()), "chart", "." + imageFormat);
                    OutputStream outputStream = null;
                    try {
                        outputStream = downloadResourceWriter.getStreamForWriting();
                        ImageIO.write((RenderedImage)this.getChartImage(parameters, chartDataHtml), imageFormat, outputStream);
                        int width = this.getIntegerParameter(parameters, "width", 300, 0, MAX_WIDTH);
                        int height = this.getIntegerParameter(parameters, "height", 300, 0, MAX_HEIGHT);
                        String altText = this.getStringParameter(parameters, "alttext", null);
                        if (altText == null) {
                            Object titleString = this.getStringParameter(parameters, "title", "");
                            String subTitleString = this.getStringParameter(parameters, "subtitle", "");
                            String xlabel = this.getStringParameter(parameters, "xlabel", null);
                            String ylabel = this.getStringParameter(parameters, "ylabel", null);
                            String type = this.getStringParameter(parameters, "type", PIE_TYPE);
                            type = this.getI18NBean().getText("confluence.extra.chart.chart.chartType." + type);
                            if (subTitleString != "") {
                                titleString = (String)titleString + " " + subTitleString;
                            }
                            if (titleString == "") {
                                altText = type;
                            } else if (titleString != "" && (xlabel == null || ylabel == null)) {
                                altText = this.getI18NBean().getText("confluence.extra.chart.chart.altText1", Arrays.asList(type, titleString));
                            } else if (titleString != "" && xlabel != null && ylabel != null) {
                                altText = this.getI18NBean().getText("confluence.extra.chart.chart.altText2", Arrays.asList(type, titleString, ylabel, xlabel));
                            }
                        }
                        chartHtmlBuilder.append(String.format("<img src=\"%s\" width=\"%d\" height=\"%d\" draggable=\"false\" alt=\"%s\" tabindex=\"0\">", downloadResourceWriter.getResourcePath(), width, height, altText));
                    }
                    finally {
                        IOUtils.closeQuietly((OutputStream)outputStream);
                    }
                }
                catch (ParseException | SeriesException parseError) {
                    chartHtmlBuilder.append(this.getErrorPanel(parseError.getMessage()));
                }
            }
            if (dataDisplay.equalsIgnoreCase(TRUE) || dataDisplay.equalsIgnoreCase(AFTER)) {
                chartHtmlBuilder.append("<br>").append(chartDataHtml);
            }
            return chartHtmlBuilder.toString();
        }
        catch (IOException ioError) {
            log.error("Unable to generate chart image", (Throwable)ioError);
            throw new MacroExecutionException((Throwable)ioError);
        }
        catch (XhtmlException | XMLStreamException xhtmlError) {
            log.error("Unable to render macro body to XHTML", xhtmlError);
            throw new MacroExecutionException(xhtmlError);
        }
        catch (CloneNotSupportedException attachmentCloneError) {
            log.error("Unable to process specified attachment", (Throwable)attachmentCloneError);
            throw new MacroExecutionException((Throwable)attachmentCloneError);
        }
        catch (CannotGenerateThumbnailException thumbnailError) {
            log.error("Unable to create thumbnail version of specified attachment", (Throwable)thumbnailError);
            throw new MacroExecutionException((Throwable)thumbnailError);
        }
    }

    private String getChartImageHtml(boolean thumbnail, Attachment chartImage) throws CannotGenerateThumbnailException {
        StringBuilder chartImageHtml = new StringBuilder("<span class=\"image-wrap\">");
        if (thumbnail && this.thumbnailManager.isThumbnailable(chartImage)) {
            this.thumbnailManager.getThumbnail(chartImage);
            ThumbnailInfo thumbnailInfo = this.thumbnailManager.getThumbnailInfo(chartImage);
            chartImageHtml.append(String.format("<a class=\"confluence-thumbnail-link\" href=\"%s%s\" draggable=\"false\"><img src=\"%s\" width=\"%d\" height=\"%d\" draggable=\"false\"></a>", this.settingsManager.getGlobalSettings().getBaseUrl(), chartImage.getDownloadPathWithoutVersion(), thumbnailInfo.getThumbnailUrlPath(), thumbnailInfo.getThumbnailWidth(), thumbnailInfo.getThumbnailHeight()));
        } else {
            chartImageHtml.append(String.format("<img src=\"%s%s\" draggable=\"false\">", this.settingsManager.getGlobalSettings().getBaseUrl(), chartImage.getDownloadPath()));
        }
        return chartImageHtml.append("</span>").toString();
    }

    BufferedImage getChartImage(Map<String, String> parameters, String chartDataHtml) throws ParseException, MacroExecutionException {
        return this.getChart(parameters, chartDataHtml).createBufferedImage(this.getIntegerParameter(parameters, "width", 300, 0, MAX_WIDTH), this.getIntegerParameter(parameters, "height", 300, 0, MAX_HEIGHT));
    }

    JFreeChart getChart(Map<String, String> parameters, String rendered) throws ParseException, MacroExecutionException {
        Plot plot;
        JFreeChart chart;
        AbstractDataset dataset;
        String title = parameters.get("title");
        String xLabel = parameters.get("xlabel");
        String yLabel = parameters.get("ylabel");
        String opacity = parameters.get("opacity");
        String bgColor = this.getStringParameter(parameters, "bgcolor", "white");
        String borderColor = parameters.get("bordercolor");
        String type = this.getStringParameter(parameters, "type", PIE_TYPE);
        String subTitle = this.getStringParameter(parameters, "subtitle", "");
        boolean legend = this.getBooleanParameter(parameters, "legend", true);
        boolean is3d = this.getBooleanParameter(parameters, "3d", false);
        boolean stacked = this.getBooleanParameter(parameters, "stacked", false);
        boolean timeSeries = this.getBooleanParameter(parameters, TIMESERIES_TYPE, false);
        boolean showShapes = this.getBooleanParameter(parameters, "showshapes", true);
        boolean tooltips = false;
        boolean urls = false;
        boolean forgive = this.getBooleanParameter(parameters, "forgive", true);
        PlotOrientation plotOrientation = PlotOrientation.VERTICAL;
        if (StringUtils.equalsIgnoreCase((String)parameters.get("orientation"), (String)HORIZONTAL)) {
            plotOrientation = PlotOrientation.HORIZONTAL;
        }
        String xTableNumber = this.getStringParameter(parameters, "tableNumber", "");
        String tables = this.getStringParameter(parameters, "tables", xTableNumber);
        String columns = this.getStringParameter(parameters, "columns", "");
        String language = this.getStringParameter(parameters, "language", "");
        String country = this.getStringParameter(parameters, "country", "");
        ChartData chartData = new ChartData(rendered, tables, columns, forgive);
        chartData.setTimePeriod(this.getStringParameter(parameters, "timeperiod", "Day"));
        if (!StringUtils.isEmpty((String)parameters.get("dateformat"))) {
            chartData.addDateFormat(new SimpleDateFormat(parameters.get("dateformat")));
        }
        this.setupLocales(chartData, language, country);
        if (!StringUtils.isEmpty((String)parameters.get("dataorientation"))) {
            chartData.setVerticalDataOrientation(VERTICAL.equalsIgnoreCase(parameters.get("dataorientation")));
        }
        if (PIE_TYPE.equalsIgnoreCase(type)) {
            dataset = new DefaultPieDataset();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createPieChart(title, dataset, legend, tooltips, urls, is3d);
        } else if (BAR_TYPE.equalsIgnoreCase(type)) {
            dataset = new DefaultCategoryDataset();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createBarChart(title, xLabel, yLabel, (CategoryDataset)((Object)dataset), plotOrientation, legend, tooltips, urls, is3d, stacked);
            if (is3d && opacity == null) {
                opacity = "100";
            }
        } else if (AREA_TYPE.equalsIgnoreCase(type)) {
            dataset = new DefaultCategoryDataset();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createAreaChart(title, xLabel, yLabel, (CategoryDataset)((Object)dataset), plotOrientation, legend, tooltips, urls, stacked);
            if (!stacked && opacity == null) {
                opacity = "50";
            }
        } else if (LINE_TYPE.equalsIgnoreCase(type)) {
            dataset = new DefaultCategoryDataset();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createLineChart(title, xLabel, yLabel, (CategoryDataset)((Object)dataset), plotOrientation, legend, tooltips, urls, is3d, showShapes);
        } else if (XYLINE_TYPE.equalsIgnoreCase(type)) {
            dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createXYLineChart(title, xLabel, yLabel, (XYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
        } else if (XYAREA_TYPE.equalsIgnoreCase(type)) {
            if (stacked) {
                dataset = new DefaultTableXYDataset();
                chartData.processData(dataset);
                chart = ConfluenceChartFactory.createStackedXYAreaChart(title, xLabel, yLabel, (TableXYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
            } else {
                dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
                chartData.processData(dataset);
                chart = ConfluenceChartFactory.createXYAreaChart(title, xLabel, yLabel, (XYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
            }
        } else if (XYBAR_TYPE.equalsIgnoreCase(type)) {
            dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createXYBarChart(title, xLabel, timeSeries, yLabel, (IntervalXYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
        } else if (XYSTEP_TYPE.equalsIgnoreCase(type)) {
            dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createXYStepChart(title, xLabel, yLabel, (XYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
        } else if (XYSTEPAREA_TYPE.equalsIgnoreCase(type)) {
            dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createXYStepAreaChart(title, xLabel, yLabel, (XYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
        } else if (SCATTER_TYPE.equalsIgnoreCase(type)) {
            dataset = timeSeries ? new TimeSeriesCollection() : new XYSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createScatterPlot(title, xLabel, yLabel, (XYDataset)((Object)dataset), plotOrientation, legend, tooltips, urls);
        } else if (TIMESERIES_TYPE.equalsIgnoreCase(type)) {
            dataset = new TimeSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createTimeSeriesChart(title, xLabel, yLabel, (XYDataset)((Object)dataset), legend, tooltips, urls);
        } else if (GANTT_TYPE.equalsIgnoreCase(type) && ChartUtil.isVersion103Capable()) {
            chartData.setVerticalDataOrientation(true);
            dataset = new TaskSeriesCollection();
            chartData.processData(dataset);
            chart = ConfluenceChartFactory.createGanttChart(title, xLabel, yLabel, (IntervalCategoryDataset)((Object)dataset), legend, tooltips, urls);
        } else {
            throw new MacroExecutionException("Unsupported chart type: " + type);
        }
        chart.setBackgroundPaint(this.stringToColor(bgColor));
        chart.addSubtitle(new TextTitle(subTitle));
        if (borderColor != null) {
            chart.setBorderPaint(this.stringToColor(borderColor));
            chart.setBorderVisible(true);
        }
        if ((plot = chart.getPlot()) instanceof PiePlot) {
            this.handlePiePlotCustomization(parameters, (PiePlot)plot);
        } else if (plot instanceof XYPlot && timeSeries) {
            ((XYPlot)plot).setDomainAxis(new DateAxis(xLabel));
        }
        this.handleAxisCustomization(parameters, plot, chartData);
        this.handleOpacityCustomization(opacity, plot);
        this.handleColorCustomization(parameters, plot);
        return chart;
    }

    void handlePiePlotCustomization(Map parameters, PiePlot plot) {
        String pieSectionLabel = this.getStringParameter(parameters, "piesectionlabel", "%0%").replaceAll("%0%", "\\{0\\}").replaceAll("%1%", "\\{1\\}").replaceAll("%2%", "\\{2\\}");
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(pieSectionLabel));
        if (ChartUtil.isVersion103Capable()) {
            String[] explodeList;
            String pieSectionExplode = this.getStringParameter(parameters, "piesectionexplode", "");
            for (String anExplodeList : explodeList = pieSectionExplode.split(",")) {
                if (StringUtils.isBlank((String)anExplodeList)) continue;
                try {
                    plot.setExplodePercent((Comparable)((Object)anExplodeList.trim()), 0.3);
                }
                catch (Exception exception) {
                    log.debug("Ignore errors");
                }
            }
        }
    }

    void handleAxisCustomization(Map parameters, Plot plot, ChartData chartData) throws MacroExecutionException {
        if (plot instanceof CategoryPlot) {
            this.handleCategoryAxisCustomization(parameters, ((CategoryPlot)plot).getDomainAxis());
            this.handleValueAxisCustomization(parameters, ((CategoryPlot)plot).getRangeAxis(), chartData, "range");
        } else if (plot instanceof XYPlot) {
            this.handleValueAxisCustomization(parameters, ((XYPlot)plot).getDomainAxis(), chartData, "domain");
            this.handleValueAxisCustomization(parameters, ((XYPlot)plot).getRangeAxis(), chartData, "range");
        }
    }

    private void handleCategoryAxisCustomization(Map parameters, CategoryAxis axis) {
        String categoryLabelPosition = "STANDARD";
        if (!StringUtils.isEmpty((String)((String)parameters.get("categorylabelposition")))) {
            categoryLabelPosition = (String)parameters.get("categorylabelposition");
        }
        if (categoryLabelPosition.equalsIgnoreCase("up45")) {
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        } else if (categoryLabelPosition.equalsIgnoreCase("up90")) {
            axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        } else if (categoryLabelPosition.equalsIgnoreCase("down45")) {
            axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        } else if (categoryLabelPosition.equalsIgnoreCase("down90")) {
            axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        }
    }

    private void handleValueAxisCustomization(Map parameters, ValueAxis axis, ChartData chartData, String qualifier) throws MacroExecutionException {
        Double axisLabelAngle = this.getDoubleParameter(parameters, qualifier + "axislabelangle", null);
        if (axisLabelAngle != null) {
            axis.setLabelAngle(Math.toRadians(axisLabelAngle));
        }
        boolean axisRotateTickLabel = this.getBooleanParameter(parameters, qualifier + "axisrotateticklabel", false);
        axis.setVerticalTickLabels(axisRotateTickLabel);
        if (axis instanceof DateAxis) {
            this.handleDateAxisCustomization(parameters, (DateAxis)axis, chartData, qualifier);
        } else {
            Double axisLowerBound = this.getDoubleParameter(parameters, qualifier + "axislowerbound", null);
            Double axisUpperBound = this.getDoubleParameter(parameters, qualifier + "axisupperbound", null);
            Double axisTickUnit = this.getDoubleParameter(parameters, qualifier + "axistickunit", null);
            if (axisLowerBound != null) {
                axis.setLowerBound(axisLowerBound);
            }
            if (axisUpperBound != null) {
                axis.setUpperBound(axisUpperBound);
            }
            if (axisTickUnit != null && axis instanceof NumberAxis) {
                ((NumberAxis)axis).setTickUnit(new NumberTickUnit(axisTickUnit));
            }
        }
    }

    private void handleDateAxisCustomization(Map parameters, DateAxis axis, ChartData chartData, String qualifier) throws MacroExecutionException {
        axis.setDateFormatOverride(chartData.getDateFormat(0));
        String axisLowerBound = this.getStringParameter(parameters, qualifier + "axislowerbound", null);
        String axisUpperBound = this.getStringParameter(parameters, qualifier + "axisupperbound", null);
        String axisTickUnit = this.getStringParameter(parameters, qualifier + "axistickunit", null);
        String dateTickMarkPosition = this.getStringParameter(parameters, "datetickmarkposition", null);
        if (axisLowerBound != null) {
            try {
                axis.setMinimumDate(chartData.toDate(axisLowerBound));
            }
            catch (ParseException exception) {
                throw new MacroExecutionException("Invalid date format for " + qualifier + "AxisLowerBound parameter: " + axisLowerBound);
            }
        }
        if (axisUpperBound != null) {
            try {
                axis.setMaximumDate(chartData.toDate(axisUpperBound));
            }
            catch (ParseException ignore) {
                throw new MacroExecutionException("Invalid date format for " + qualifier + "AxisUpperBound parameter: " + axisUpperBound);
            }
        }
        if (axisTickUnit != null) {
            this.setDateTick(parameters, axis, axisTickUnit);
        }
        if (dateTickMarkPosition != null) {
            if (START.equalsIgnoreCase(dateTickMarkPosition)) {
                axis.setTickMarkPosition(DateTickMarkPosition.START);
            } else if (MIDDLE.equalsIgnoreCase(dateTickMarkPosition)) {
                axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
            } else if (END.equalsIgnoreCase(dateTickMarkPosition)) {
                axis.setTickMarkPosition(DateTickMarkPosition.END);
            }
        }
    }

    void handleOpacityCustomization(String opacity, Plot plot) throws MacroExecutionException {
        if (opacity != null) {
            try {
                Integer iOpacity = new Integer(opacity);
                if (iOpacity < 0 || iOpacity > 100) {
                    throw new MacroExecutionException("opacity parameter value '" + opacity + "' not between 0 and 100");
                }
                plot.setForegroundAlpha(iOpacity.floatValue() / 100.0f);
            }
            catch (NumberFormatException exception) {
                throw new MacroExecutionException("opacity parameter value '" + opacity + "' not a number between 0 and 100");
            }
        }
    }

    void handleColorCustomization(Map parameters, Plot plot) throws MacroExecutionException {
        String colors = (String)parameters.get("colors");
        if (colors != null) {
            String[] color = colors.split(",");
            for (int i = 0; i < color.length; ++i) {
                PiePlot piePlot;
                PieDataset pieDataset;
                if (plot instanceof CategoryPlot) {
                    ((CategoryPlot)plot).getRenderer().setSeriesPaint(i, this.stringToColor(color[i]));
                    continue;
                }
                if (plot instanceof XYPlot) {
                    ((XYPlot)plot).getRenderer().setSeriesPaint(i, this.stringToColor(color[i]));
                    continue;
                }
                if (!(plot instanceof PiePlot) || i >= (pieDataset = (piePlot = (PiePlot)plot).getDataset()).getItemCount()) continue;
                piePlot.setSectionPaint(pieDataset.getKey(i), (Paint)this.stringToColor(color[i]));
            }
        }
    }

    Color stringToColor(String colorName) throws MacroExecutionException {
        int colorValue;
        String colorNameTrimmed = StringUtils.trim((String)StringUtils.lowerCase((String)colorName));
        if (StringUtils.isBlank((String)colorNameTrimmed)) {
            return null;
        }
        if (COLOR_MAP.containsKey(colorNameTrimmed)) {
            colorValue = COLOR_MAP.get(colorNameTrimmed);
        } else {
            try {
                if (!StringUtils.startsWith((String)colorNameTrimmed, (String)"#") || colorNameTrimmed.length() <= 1) {
                    throw new NumberFormatException(String.format("Invalid custom color specified %s", colorNameTrimmed));
                }
                colorValue = Integer.parseInt(colorNameTrimmed.substring(1), 16);
            }
            catch (NumberFormatException notHexValue) {
                throw new MacroExecutionException(String.format("Invalid color %s", colorNameTrimmed), (Throwable)notHexValue);
            }
        }
        return new Color(colorValue);
    }

    void setDateTick(Map parameters, DateAxis axis, String tick) throws MacroExecutionException {
        char[] timeChars = new char[]{'u', 's', 'm', 'h', 'd', 'M', 'y'};
        int findAt = StringUtils.indexOfAny((String)tick, (char[])timeChars);
        String value = findAt < 0 ? tick : tick.substring(0, findAt);
        int count = 0;
        try {
            count = Integer.parseInt(value.trim());
        }
        catch (NumberFormatException ignore) {
            throw new MacroExecutionException("Invalid format for date axis tick unit: " + tick);
        }
        int unit = -1;
        if (findAt >= 0) {
            if (tick.charAt(findAt) == 'y') {
                unit = 0;
            } else if (tick.charAt(findAt) == 'M') {
                unit = 1;
            } else if (tick.charAt(findAt) == 'd') {
                unit = 2;
            } else if (tick.charAt(findAt) == 'h') {
                unit = 3;
            } else if (tick.charAt(findAt) == 'm') {
                unit = 4;
            } else if (tick.charAt(findAt) == 's') {
                unit = 5;
            } else if (tick.charAt(findAt) == 'u') {
                unit = 6;
            } else {
                count = 0;
            }
        } else {
            String timePeriod = this.getStringParameter(parameters, "timeperiod", "Day");
            if (timePeriod.equalsIgnoreCase("year")) {
                unit = 0;
            } else if (timePeriod.equalsIgnoreCase("quarter")) {
                unit = 1;
                count *= 3;
            } else if (timePeriod.equalsIgnoreCase("month")) {
                unit = 1;
            } else if (timePeriod.equalsIgnoreCase("day")) {
                unit = 2;
            } else if (timePeriod.equalsIgnoreCase("week")) {
                unit = 2;
                count *= 7;
            } else if (timePeriod.equalsIgnoreCase("hour")) {
                unit = 3;
            } else if (timePeriod.equalsIgnoreCase("minute")) {
                unit = 4;
            } else if (timePeriod.equalsIgnoreCase("second")) {
                unit = 5;
            } else if (timePeriod.equalsIgnoreCase("millisecond")) {
                unit = 6;
            } else {
                count = 0;
            }
        }
        if (count > 0) {
            axis.setTickUnit(new DateTickUnit(unit, count));
        }
    }

    Attachment getAttachment(Map<String, String> parameters, ConversionContext conversionContext, String chartDataHtml) throws ParseException, MacroExecutionException, XMLStreamException, XhtmlException, IOException, CloneNotSupportedException {
        Object attachmentLink = this.getStringParameter(parameters, "attachment", null);
        if (StringUtils.isNotBlank((String)attachmentLink)) {
            int indexOfCaret = ((String)attachmentLink).indexOf(94);
            if (indexOfCaret == -1) {
                attachmentLink = "^" + (String)attachmentLink;
            }
            Link aLink = this.linkResolver.createLink((RenderContext)conversionContext.getEntity().toPageContext(), (String)attachmentLink);
            String imageFormat = this.getStringParameter(parameters, "imageformat", "png");
            if (aLink instanceof AttachmentLink) {
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                Attachment theAttachment = ((AttachmentLink)aLink).getAttachment();
                String theAttachmentFileName = theAttachment.getFileName();
                String attachmentVersion = this.getStringParameter(parameters, "attachmentversion", NEW);
                if (StringUtils.equals((String)KEEP, (String)attachmentVersion)) {
                    return theAttachment;
                }
                ContentEntityObject theAttachmentContentEntity = theAttachment.getContainer();
                if (StringUtils.equals((String)NEW, (String)attachmentVersion) && !this.permissionManager.hasCreatePermission((User)currentUser, (Object)theAttachmentContentEntity, Attachment.class)) {
                    throw new MacroExecutionException(String.format("Export not valid. Not authorized to add %s from page: %s (%d)", theAttachmentFileName, theAttachmentContentEntity.getTitle(), theAttachmentContentEntity.getId()));
                }
                if (!(!StringUtils.equals((String)REPLACE, (String)attachmentVersion) || this.permissionManager.hasPermission((User)currentUser, Permission.REMOVE, (Object)theAttachment) && this.permissionManager.hasCreatePermission((User)currentUser, (Object)theAttachmentContentEntity, Attachment.class))) {
                    throw new MacroExecutionException(String.format("Export not valid. Not authorized to recreate %s from page: %s (%d)", theAttachmentFileName, theAttachmentContentEntity.getTitle(), theAttachmentContentEntity.getId()));
                }
                byte[] chartByteArray = this.getChartAsByteArray(this.getChartImage(parameters, chartDataHtml), imageFormat);
                if (this.isSameImage(theAttachment, chartByteArray)) {
                    return theAttachment;
                }
                if (StringUtils.equals((String)REPLACE, (String)attachmentVersion)) {
                    this.attachmentManager.removeAttachmentFromServer(theAttachment);
                    theAttachment = null;
                }
                return this.saveChartImageAsAttachment(theAttachmentContentEntity, "image/" + imageFormat, theAttachmentFileName, chartByteArray, this.getStringParameter(parameters, "attachmentcomment", null), theAttachment, conversionContext.getOutputType());
            }
            if (aLink instanceof UnpermittedLink) {
                throw new MacroExecutionException("Export not valid. Not authorized to view specified attachment");
            }
            if (aLink instanceof UnresolvedLink) {
                indexOfCaret = ((String)attachmentLink).indexOf(94);
                if (indexOfCaret >= 0 && indexOfCaret < ((String)attachmentLink).length() - 1) {
                    ContentEntityObject theAttachmentContentEntity = conversionContext.getEntity();
                    if (indexOfCaret > 0) {
                        Link contentEntityLink = this.linkResolver.createLink((RenderContext)conversionContext.getEntity().toPageContext(), ((String)attachmentLink).substring(0, indexOfCaret));
                        if (contentEntityLink instanceof UnpermittedLink) {
                            throw new MacroExecutionException("Export not valid. Not authorized to view specified attachment");
                        }
                        if (contentEntityLink instanceof PageLink || contentEntityLink instanceof BlogPostLink) {
                            theAttachmentContentEntity = ((AbstractPageLink)contentEntityLink).getDestinationContent();
                        }
                    }
                    return this.saveChartImageAsAttachment(theAttachmentContentEntity, "image/" + imageFormat, ((String)attachmentLink).substring(indexOfCaret + 1), this.getChartAsByteArray(this.getChartImage(parameters, chartDataHtml), imageFormat), null, null, conversionContext.getOutputType());
                }
                throw new MacroExecutionException(String.format("Invalid attachment link %s", attachmentLink));
            }
        }
        return null;
    }

    private Attachment saveChartImageAsAttachment(ContentEntityObject attachmentContent, String attachmentMimeType, String attachmentFileName, byte[] chartImageBytes, String comment, Attachment currentVersionOfAttachment, String outputType) throws IOException {
        Attachment previousVersionOfAttachment = null != currentVersionOfAttachment && currentVersionOfAttachment.isPersistent() ? (Attachment)currentVersionOfAttachment.clone() : null;
        Attachment chartImageAttachment = null == previousVersionOfAttachment ? new Attachment() : currentVersionOfAttachment;
        chartImageAttachment.setContainer(attachmentContent);
        chartImageAttachment.setMediaType(attachmentMimeType);
        chartImageAttachment.setVersionComment(comment);
        chartImageAttachment.setFileName(attachmentFileName);
        chartImageAttachment.setFileSize((long)chartImageBytes.length);
        if (!"preview".equals(outputType)) {
            if (null == previousVersionOfAttachment) {
                attachmentContent.addAttachment(chartImageAttachment);
            }
            this.attachmentManager.saveAttachment(chartImageAttachment, previousVersionOfAttachment, (InputStream)new ByteArrayInputStream(chartImageBytes));
        }
        return chartImageAttachment;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] getChartAsByteArray(RenderedImage image, String imageFormat) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, imageFormat, outputStream);
            byte[] byArray = outputStream.toByteArray();
            return byArray;
        }
        finally {
            IOUtils.closeQuietly((OutputStream)outputStream);
        }
    }

    void setupLocales(ChartData chartData, String language, String country) {
        if (!language.trim().equals("") || !country.trim().equals("")) {
            chartData.addLocale(new Locale(language, country));
        }
        LocaleUtils localeUtils = new LocaleUtils();
        chartData.addLocale(localeUtils.getLocale(this.settingsManager.getGlobalSettings().getGlobalDefaultLocale()));
        List list = this.languageManager.getLanguages();
        list.forEach(item -> chartData.addLocale(item.getLocale()));
    }

    String getErrorPanel(String message) {
        return RenderUtils.blockError((String)message, (String)"");
    }

    Map<String, String> toLowerCase(Map<String, String> params) {
        HashMap<String, String> paramsWithLowerCasedKeys = new HashMap<String, String>(params.size());
        for (Map.Entry<String, String> paramValue : params.entrySet()) {
            paramsWithLowerCasedKeys.put(StringUtils.lowerCase((String)paramValue.getKey()), paramValue.getValue());
        }
        return paramsWithLowerCasedKeys;
    }

    int getIntegerParameter(Map parameters, String param, int defaultSize, int lowerBound, int higherBound) throws MacroExecutionException {
        int result = this.getIntegerParameter(parameters, param, defaultSize);
        if (result < lowerBound) {
            result = defaultSize;
        } else if (result > higherBound) {
            result = higherBound;
        }
        return result;
    }

    Integer getIntegerParameter(Map parameters, String param, Integer def) throws MacroExecutionException {
        Integer result = def;
        if (!StringUtils.isEmpty((String)((String)parameters.get(param)))) {
            try {
                result = new Integer((String)parameters.get(param));
            }
            catch (NumberFormatException exception) {
                throw new MacroExecutionException("Invalid " + param + " parameter.  It must be an integer.");
            }
        }
        return result;
    }

    Double getDoubleParameter(Map parameters, String param, Double def) throws MacroExecutionException {
        Double result = def;
        if (!StringUtils.isEmpty((String)((String)parameters.get(param)))) {
            try {
                result = new Double((String)parameters.get(param));
            }
            catch (NumberFormatException exception) {
                throw new MacroExecutionException("Invalid " + param + " parameter.  It must be an double value.");
            }
        }
        return result;
    }

    String getStringParameter(Map parameters, String param, String def) {
        String result = def;
        if (!StringUtils.isEmpty((String)((String)parameters.get(param)))) {
            result = (String)parameters.get(param);
        }
        return result;
    }

    boolean getBooleanParameter(Map parameters, String param, boolean def) {
        String value = (String)parameters.get(param);
        boolean result = value != null && value.equalsIgnoreCase(def ? FALSE : TRUE) ? !def : def;
        return result;
    }

    private boolean isImageFormatSupported(String imageFormat) {
        String[] writerNames;
        for (String writerFormat : writerNames = ImageIO.getWriterFormatNames()) {
            if (!writerFormat.equalsIgnoreCase(imageFormat)) continue;
            return true;
        }
        return false;
    }

    private I18NBean getI18NBean() {
        return this.i18nBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isSameImage(Attachment anAttachment, byte[] chartImageBytes) throws IOException {
        InputStream attachmentInput = this.attachmentManager.getAttachmentData(anAttachment);
        try {
            boolean bl = IOUtils.contentEquals((InputStream)attachmentInput, (InputStream)new ByteArrayInputStream(chartImageBytes));
            return bl;
        }
        finally {
            IOUtils.closeQuietly((InputStream)attachmentInput);
        }
    }
}

