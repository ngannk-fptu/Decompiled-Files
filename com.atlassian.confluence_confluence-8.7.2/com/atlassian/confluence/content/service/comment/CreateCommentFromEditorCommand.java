/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.service.comment.CreateCommentCommandImpl;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.NewCommentDeduplicator;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCommentFromEditorCommand
extends CreateCommentCommandImpl {
    private static final Logger log = LoggerFactory.getLogger(CreateCommentFromEditorCommand.class);
    protected EditorConverter editConverter;
    protected String editorFormatCommentBody;

    public CreateCommentFromEditorCommand(PermissionManager permissionManager, ContentEntityManager contentManager, CommentManager commentManager, EditorConverter editConverter, long pageId, long parentCommentId, String commentBody, NewCommentDeduplicator commentDeduplicator) {
        super(permissionManager, contentManager, commentManager, pageId, parentCommentId, null, commentDeduplicator);
        this.editConverter = editConverter;
        this.editorFormatCommentBody = commentBody;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        PageContext renderContext = this.getContent().toPageContext();
        try {
            DefaultConversionContext ctx = new DefaultConversionContext(renderContext);
            ctx.setProperty(ConversionContext.IS_VALIDATING_COMMENT, Boolean.TRUE);
            this.commentBody = this.editConverter.convert(this.editorFormatCommentBody, ctx);
        }
        catch (XhtmlParsingException ex) {
            validator.addValidationError("content.xhtml.parse.failed", ex.getLineNumber(), ex.getColumnNumber(), ex.getMessage());
            return;
        }
        catch (XhtmlException ex) {
            validator.addValidationError("content.xhtml.editor.conversion.failed", new Object[0]);
            log.warn("XhtmlException converting editor format to storage format. Turn on debug level logging to see editor format data.", (Throwable)ex);
            log.debug("The editor data that could not be converted\n: {}", (Object)this.editorFormatCommentBody);
            return;
        }
        super.validateInternal(validator);
    }
}

