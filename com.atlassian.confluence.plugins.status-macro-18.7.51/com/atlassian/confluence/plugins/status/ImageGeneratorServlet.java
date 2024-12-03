/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.EditorImagePlaceholder
 *  com.atlassian.confluence.macro.ImagePlaceholder
 *  com.atlassian.confluence.pages.thumbnail.Dimensions
 *  com.atlassian.confluence.web.UrlBuilder
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.status;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.confluence.plugins.status.StatusMacroConfiguration;
import com.atlassian.confluence.web.UrlBuilder;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class ImageGeneratorServlet
extends HttpServlet
implements EditorImagePlaceholder {
    private static final Dimensions IMAGE_DIMENSION = new Dimensions(88, 18);
    private static final String SERVLET_PATH = "/plugins/servlet/status-macro/placeholder";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StatusMacroConfiguration configuration = StatusMacroConfiguration.createFor(req.getParameterMap());
        Object text = configuration.getTitle();
        try (InputStream imageStream = ((Object)((Object)this)).getClass().getClassLoader().getResourceAsStream(this.filenameFor(configuration));){
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font = new Font("Arial", 1, 22);
            graphics.setFont(font);
            graphics.setColor(configuration.getColour().forText(configuration.isSubtle()));
            int txtWidth = graphics.getFontMetrics().stringWidth((String)text);
            int txtHeight = graphics.getFontMetrics().getHeight();
            int imgWidth = bufferedImage.getWidth();
            int imgHeight = bufferedImage.getHeight();
            int horizontalPadding = 24;
            int ellipsisWidth = graphics.getFontMetrics().stringWidth("...");
            if (txtWidth + horizontalPadding > imgWidth) {
                Object newText = "";
                int pos = 0;
                while (graphics.getFontMetrics().stringWidth((String)newText) + ellipsisWidth + horizontalPadding <= imgWidth) {
                    newText = (String)newText + ((String)text).charAt(pos);
                    ++pos;
                }
                text = (String)newText + "...";
            }
            int yPos = imgHeight / 2 + txtHeight / 2 - graphics.getFontMetrics().getDescent();
            int xPos = imgWidth / 2 - graphics.getFontMetrics().stringWidth((String)text) / 2;
            graphics.drawString((String)text, xPos, yPos);
            resp.setContentType("image/png");
            ImageIO.write((RenderedImage)bufferedImage, "png", (OutputStream)resp.getOutputStream());
        }
    }

    private String filenameFor(StatusMacroConfiguration configuration) {
        return "images/" + configuration.getColour().name().toLowerCase(Locale.ENGLISH) + (configuration.isSubtle() ? "-subtle" : "") + "@2x.png";
    }

    public ImagePlaceholder getImagePlaceholder(final Map<String, String> parameters, ConversionContext context) {
        return new ImagePlaceholder(){

            public String getUrl() {
                return ImageGeneratorServlet.this.buildUrlToImage(parameters);
            }

            public Dimensions getDimensions() {
                return IMAGE_DIMENSION;
            }

            public boolean applyPlaceholderChrome() {
                return false;
            }
        };
    }

    private String buildUrlToImage(Map<String, String> parameters) {
        UrlBuilder builder = new UrlBuilder(SERVLET_PATH);
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            builder.add(parameter.getKey(), parameter.getValue());
        }
        return builder.toUrl();
    }
}

