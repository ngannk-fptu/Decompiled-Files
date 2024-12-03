/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.service.comment;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XhtmlParsingException;
import com.atlassian.confluence.content.render.xhtml.editor.EditorConverter;
import com.atlassian.confluence.content.service.comment.EditCommentCommandImpl;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditCommentFromEditorCommand
extends EditCommentCommandImpl {
    private static final Logger log = LoggerFactory.getLogger(EditCommentFromEditorCommand.class);
    protected EditorConverter editConverter;
    protected String editorFormatCommentBody;

    public EditCommentFromEditorCommand(PermissionManager permissionManager, CommentManager commentManager, EditorConverter editConverter, long commentId, String newCommentBody) {
        super(permissionManager, commentManager, commentId, newCommentBody);
        this.editConverter = editConverter;
        this.editorFormatCommentBody = newCommentBody;
    }

    @Override
    protected void validateInternal(ServiceCommandValidator validator) {
        Comment comment = this.getComment();
        if (comment == null) {
            validator.addFieldValidationError("no-comment", "comment.edit.not.found");
            return;
        }
        ContentEntityObject container = comment.getContainer();
        if (container == null) {
            validator.addFieldValidationError("no-comment", "comment.edit.not.found");
            return;
        }
        PageContext renderContext = container.toPageContext();
        try {
            this.newCommentBody = this.editConverter.convert(this.editorFormatCommentBody, new DefaultConversionContext(renderContext));
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

