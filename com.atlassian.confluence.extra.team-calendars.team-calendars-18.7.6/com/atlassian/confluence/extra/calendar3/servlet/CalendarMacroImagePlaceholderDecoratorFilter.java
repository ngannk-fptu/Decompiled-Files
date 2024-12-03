/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.WriteListener
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpServletResponseWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.servlet;

import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.util.PdlUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import javax.imageio.ImageIO;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarMacroImagePlaceholderDecoratorFilter
implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarMacroImagePlaceholderDecoratorFilter.class);
    private static final String DECORATED_PLACEHOLDER_IMAGE_MIME_TYPE = "image/png";

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse) {
            String macroDefinition;
            HttpServletRequest httpReq = (HttpServletRequest)servletRequest;
            HttpServletResponse httpRes = (HttpServletResponse)servletResponse;
            String definitionParam = httpReq.getParameter("definition");
            if (StringUtils.isNotBlank(definitionParam) && (macroDefinition = new String(Base64.decodeBase64(definitionParam), Charset.forName("UTF-8"))).startsWith("{calendar:")) {
                PlaceHolderHttpServletResponseWrapper httpServletResponseWrapper = new PlaceHolderHttpServletResponseWrapper(httpRes);
                filterChain.doFilter(servletRequest, (ServletResponse)httpServletResponseWrapper);
                BufferedImage originalPlaceHolderImage = ImageIO.read(new ByteArrayInputStream(httpServletResponseWrapper.getBytesWritten()));
                BufferedImage canvas = this.createCanvas();
                this.paintPlaceholderTextOnCanvas(canvas, this.cropPlaceholderText(originalPlaceHolderImage));
                this.paintMiniLogoOnCanvas(canvas);
                this.paintSelectedViewIconOnCanvas(canvas, httpReq.getParameter("view"));
                httpRes.setContentType(DECORATED_PLACEHOLDER_IMAGE_MIME_TYPE);
                this.writeImageToResponse(canvas, httpRes);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeImageToResponse(BufferedImage image, HttpServletResponse httpServletResponse) throws IOException {
        ServletOutputStream servletOutput = null;
        try {
            servletOutput = httpServletResponse.getOutputStream();
            ImageIO.write((RenderedImage)image, "png", (OutputStream)servletOutput);
        }
        finally {
            if (null != servletOutput) {
                try {
                    servletOutput.flush();
                }
                catch (IOException flushError) {
                    LOG.info("Unable to flush image to client.", (Throwable)flushError);
                }
            }
        }
    }

    private BufferedImage cropPlaceholderText(BufferedImage originalPlaceholderImage) {
        int imgWidth = originalPlaceholderImage.getWidth();
        int imgHeight = originalPlaceholderImage.getHeight();
        int offsetX = PdlUtil.isPdlEnabled() ? 27 : 24;
        BufferedImage croppedImage = originalPlaceholderImage.getSubimage(offsetX, 0, imgWidth - offsetX, imgHeight);
        if (croppedImage.getWidth() > 290) {
            croppedImage = croppedImage.getSubimage(0, 0, 290, imgHeight);
        }
        return croppedImage;
    }

    private void paintSelectedViewIconOnCanvas(BufferedImage canvas, String defaultView) throws IOException {
        String selectedIconResource = "com/atlassian/confluence/extra/calendar3/img/placeholder-calendar-month.png";
        try {
            CalendarRenderer.CalendarView chosenView = CalendarRenderer.CalendarView.valueOf(StringUtils.defaultString(defaultView));
            if (chosenView == CalendarRenderer.CalendarView.agendaWeek) {
                selectedIconResource = "com/atlassian/confluence/extra/calendar3/img/placeholder-calendar-week.png";
            }
            if (chosenView == CalendarRenderer.CalendarView.basicDay) {
                selectedIconResource = "com/atlassian/confluence/extra/calendar3/img/placeholder-calendar-list.png";
            }
            if (chosenView == CalendarRenderer.CalendarView.timeline) {
                selectedIconResource = "com/atlassian/confluence/extra/calendar3/img/placeholder-calendar-timeline.png";
            }
        }
        catch (IllegalArgumentException invalidView) {
            LOG.info("Invalid view specified to the macro placeholder image decorator", (Throwable)invalidView);
        }
        this.getGraphics2DFromImage(canvas).drawImage((Image)this.getResourceAsImage(selectedIconResource), 9, 37, null);
    }

    private void paintMiniLogoOnCanvas(BufferedImage canvas) throws IOException {
        this.getGraphics2DFromImage(canvas).drawImage((Image)this.getResourceAsImage("com/atlassian/confluence/extra/calendar3/img/logo_16.png"), 9, 9, null);
    }

    private BufferedImage getResourceAsImage(String resourcePath) throws IOException {
        try (InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);){
            BufferedImage bufferedImage = ImageIO.read(resourceStream);
            return bufferedImage;
        }
    }

    private void paintPlaceholderTextOnCanvas(BufferedImage canvas, BufferedImage placeHolderText) {
        this.getGraphics2DFromImage(canvas).drawImage((Image)placeHolderText, 27, PdlUtil.isPdlEnabled() ? 2 : 5, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private BufferedImage createCanvas() {
        BufferedImage canvas = new BufferedImage(328, 218, 2);
        Graphics2D g2d = this.getGraphics2DFromImage(canvas);
        Paint originalPaint = g2d.getPaint();
        try {
            g2d.setPaint(new Color(240, 240, 240));
            g2d.fill(new Rectangle2D.Float(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight()));
            BufferedImage bufferedImage = canvas;
            return bufferedImage;
        }
        finally {
            g2d.setPaint(originalPaint);
        }
    }

    private Graphics2D getGraphics2DFromImage(BufferedImage anImage) {
        return (Graphics2D)anImage.getGraphics();
    }

    public void destroy() {
    }

    private static class PlaceHolderHttpServletResponseWrapper
    extends HttpServletResponseWrapper {
        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        private PlaceHolderHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        public byte[] getBytesWritten() {
            return this.byteArrayOutputStream.toByteArray();
        }

        public ServletOutputStream getOutputStream() {
            this.byteArrayOutputStream.reset();
            return new ServletOutputStream(){

                public void write(int i) {
                    byteArrayOutputStream.write(i);
                }

                public boolean isReady() {
                    return true;
                }

                public void setWriteListener(WriteListener writeListener) {
                }
            };
        }

        public PrintWriter getWriter() throws IOException {
            throw new UnsupportedOperationException("Writing characters as images is not allowed");
        }
    }
}

