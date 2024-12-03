/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.view.RenderResult;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.renderer.RenderContext;

public interface FormatConverter {
    public String validateAndConvertToStorageFormat(ConfluenceActionSupport var1, String var2, RenderContext var3);

    public String validateAndConvertToStorageFormat(String var1, RenderContext var2) throws BadRequestException;

    public String convertToStorageFormat(String var1, RenderContext var2) throws XhtmlParsingException, XhtmlException;

    public String convertToEditorFormat(String var1, RenderContext var2);

    public RenderResult convertToEditorFormatWithResult(String var1, RenderContext var2);

    public String convertToViewFormat(String var1, RenderContext var2);

    public String cleanEditorFormat(String var1, RenderContext var2) throws XhtmlException;

    public String cleanStorageFormat(String var1);
}

