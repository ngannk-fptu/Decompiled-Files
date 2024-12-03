/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.importexport.resource.DownloadResourceWriter
 *  com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.extra.dynamictasklist2.DateRenderer;
import com.atlassian.confluence.extra.dynamictasklist2.NameRenderer;
import com.atlassian.confluence.extra.dynamictasklist2.TaskListManager;
import com.atlassian.confluence.extra.dynamictasklist2.model.TaskList;
import com.atlassian.confluence.extra.dynamictasklist2.util.TaskListUtil;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.ConfluenceRenderContextOutputType;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTaskListMacro
extends BaseMacro
implements Macro {
    private static final Logger log = LoggerFactory.getLogger(AbstractTaskListMacro.class);
    private static final String RENDER_MODE_PARAM = "renderMode";
    private static final String STATIC_RENDER_MODE = "static";
    private final WebResourceManager webResourceManager;
    private final WritableDownloadResourceManager writableDownloadResourceManager;
    private final SettingsManager settingsManager;
    private final WikiStyleRenderer wikiStyleRenderer;
    private final UserAccessor userAccessor;
    private final TaskListManager taskListManager;
    private final FormatSettingsManager formatSettingsManager;
    private final VelocityHelperService velocityHelperService;
    private final LocaleManager localeManager;

    protected AbstractTaskListMacro(@ComponentImport WebResourceManager webResourceManager, @ComponentImport WritableDownloadResourceManager writableDownloadResourceManager, @ComponentImport SettingsManager settingsManager, @ComponentImport WikiStyleRenderer wikiStyleRenderer, @ComponentImport UserAccessor userAccessor, TaskListManager taskListManager, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport VelocityHelperService velocityHelperService, @ComponentImport LocaleManager localeManager) {
        this.webResourceManager = webResourceManager;
        this.writableDownloadResourceManager = writableDownloadResourceManager;
        this.settingsManager = settingsManager;
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.userAccessor = userAccessor;
        this.taskListManager = taskListManager;
        this.formatSettingsManager = formatSettingsManager;
        this.velocityHelperService = velocityHelperService;
        this.localeManager = localeManager;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public boolean hasBody() {
        return true;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        String promptOnDelete;
        PageContext pageContext = conversionContext.getPageContext();
        ContentEntityObject content = conversionContext.getEntity();
        if (content == null) {
            throw new MacroExecutionException("Cannot display tasklist because containing content does not yet exist.");
        }
        if (content instanceof Comment) {
            throw new MacroExecutionException("Task lists can not be used in comments.");
        }
        String listName = parameters.get("title");
        if (listName == null) {
            listName = parameters.get("0");
        }
        int occurrence = TaskListUtil.incrementOccurrenceInPageContext(listName, pageContext);
        Map contextMap = MacroUtils.defaultVelocityContext();
        TaskList taskList = this.taskListManager.getTaskListWithNameFromContent(content, listName, occurrence);
        if (taskList == null) {
            taskList = new TaskList(listName, occurrence);
        }
        contextMap.put("tasklist", taskList);
        contextMap.put("content", content);
        contextMap.put("nameRenderer", new NameRenderer(this.wikiStyleRenderer, pageContext));
        contextMap.put("dateRenderer", new DateRenderer(this.userAccessor, this.formatSettingsManager, this.localeManager));
        contextMap.put("random", new Random());
        contextMap.put("adgEnabled", TaskListUtil.isAdgEnabled());
        contextMap.put("remoteUser", AuthenticatedUserThreadLocal.get());
        boolean renderAsEditable = this.renderAsEditable(parameters, (RenderContext)pageContext);
        contextMap.put("editable", renderAsEditable);
        contextMap.put("forceIneditable", content instanceof Draft || !content.isPersistent());
        String width = parameters.get("width");
        if (width != null) {
            taskList.getConfig().setWidth(width);
        }
        if ((promptOnDelete = parameters.get("promptOnDelete")) != null) {
            taskList.getConfig().setPromptOnDelete(Boolean.parseBoolean(promptOnDelete));
        }
        if ("pdf".equals(pageContext.getOutputType()) || "word".equals(pageContext.getOutputType()) || ConfluenceRenderContextOutputType.PAGE_GADGET.toString().equals(pageContext.getOutputType())) {
            String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
            String pageUrl = baseUrl + content.getUrlPath();
            contextMap.put("pageLink", pageUrl);
            contextMap.put("openIconPath", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "open", "png", pageContext.getOutputType()));
            contextMap.put("closedIconPath", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "closed", "png", pageContext.getOutputType()));
            contextMap.put("checkedCheckBox", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "checkbox_checked", "gif", pageContext.getOutputType()));
            contextMap.put("uncheckedCheckBox", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "checkbox_unchecked", "gif", pageContext.getOutputType()));
            contextMap.put("majorPriorityIconPath", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "priority_major", "gif", pageContext.getOutputType()));
            contextMap.put("minorPriorityIconPath", baseUrl + this.getImageResourcePath("templates/extra/dynamictasklist2/images/", "priority_minor", "gif", pageContext.getOutputType()));
            if ("pdf".equals(pageContext.getOutputType())) {
                contextMap.put("progressBarIconPath", this.getProgressBarPath(taskList.getPercentComplete()));
            } else {
                contextMap.put("progressBarIconPath", "");
            }
        }
        try {
            if (renderAsEditable) {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/macro.vm", contextMap);
            }
            if ("pdf".equals(pageContext.getOutputType()) || "word".equals(pageContext.getOutputType())) {
                return this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/tasklist-export-static.vm", contextMap);
            }
            return this.velocityHelperService.getRenderedTemplate("templates/extra/dynamictasklist2/tasklist-static.vm", contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to display TaskList!", (Throwable)e);
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.PLAIN_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        try {
            return this.execute((Map<String, String>)parameters, body, (ConversionContext)new DefaultConversionContext(renderContext));
        }
        catch (MacroExecutionException e) {
            throw new MacroException((Throwable)e);
        }
    }

    private String getImageResourcePath(String path, String imageName, String imageFormat, String renderContext) {
        if ("pdf".equals(renderContext)) {
            DownloadResourceWriter downloadResourceWriter = this.getDownloadResourceWriter(imageName, imageFormat);
            try (InputStream inputStream = this.getLocalResource(path + imageName + "." + imageFormat);
                 OutputStream outputStream = downloadResourceWriter.getStreamForWriting();){
                if (inputStream != null && outputStream != null) {
                    IOUtils.copy((InputStream)inputStream, (OutputStream)outputStream);
                }
            }
            catch (IOException e) {
                log.error("Error while copying inputStream to outputStream!", (Throwable)e);
            }
            return downloadResourceWriter.getResourcePath();
        }
        return this.webResourceManager.getStaticPluginResource("confluence.extra.dynamictasklist2:dynamictasklist2", "images/" + imageName + "." + imageFormat);
    }

    private String getProgressBarPath(int percentCompleted) {
        DownloadResourceWriter downloadResourceWriter = this.getDownloadResourceWriter("progressBar", "gif");
        BufferedImage img = this.createProgressBarImage(percentCompleted);
        try (OutputStream outputStream = downloadResourceWriter.getStreamForWriting();){
            ImageIO.write((RenderedImage)img, "gif", outputStream);
        }
        catch (IOException e) {
            log.error("Error writing buffered image to outputStream!", (Throwable)e);
        }
        return downloadResourceWriter.getResourcePath();
    }

    private BufferedImage createProgressBarImage(int percentCompleted) {
        int width = 600;
        int height = 5;
        int ratio = width / 100;
        BufferedImage img = new BufferedImage(width, height, 1);
        img.createGraphics();
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setColor(new Color(236, 216, 169));
        g.fillRect(0, 0, width, height);
        g.setColor(new Color(131, 157, 204));
        g.fillRect(0, 0, percentCompleted * ratio, height);
        return img;
    }

    private InputStream getLocalResource(String path) {
        return ((Object)((Object)this)).getClass().getClassLoader().getResourceAsStream(path);
    }

    private DownloadResourceWriter getDownloadResourceWriter(String imageName, String imageFormat) {
        return this.writableDownloadResourceManager.getResourceWriter(StringUtils.defaultString((String)AuthenticatedUserThreadLocal.getUsername()), imageName, imageFormat);
    }

    private boolean renderAsEditable(Map params, RenderContext renderContext) {
        String outputType = renderContext.getOutputType();
        return !"pdf".equals(outputType) && !"word".equals(outputType) && !"email".equals(outputType) && !"feed".equals(outputType) && !"html_export".equals(outputType) && !ConfluenceRenderContextOutputType.PAGE_GADGET.toString().equals(outputType) && !STATIC_RENDER_MODE.equals(params.get(RENDER_MODE_PARAM));
    }
}

