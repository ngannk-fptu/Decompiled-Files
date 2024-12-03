/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.Template
 *  org.apache.velocity.runtime.parser.node.ASTDirective
 *  org.apache.velocity.runtime.parser.node.ParserVisitor
 *  org.apache.velocity.runtime.parser.node.SimpleNode
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.apache.velocity.runtime.visitor.BaseVisitor
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.setup.velocity.DynamicPluginResourceLoader;
import com.atlassian.confluence.util.velocity.ConfluenceVelocityTemplate;
import com.atlassian.confluence.util.velocity.ResourceLoaderWrapper;
import org.apache.velocity.Template;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.runtime.visitor.BaseVisitor;

final class ConfluenceVelocityTemplateImpl
extends Template
implements ConfluenceVelocityTemplate {
    private volatile Boolean isAutoEncodeDisabled = null;
    private volatile Boolean isHtmlSafe = null;

    ConfluenceVelocityTemplateImpl() {
    }

    @Override
    public boolean isAutoEncodeDisabled() {
        if (this.data == null) {
            return false;
        }
        if (this.isAutoEncodeDisabled != null) {
            return this.isAutoEncodeDisabled;
        }
        DisableAntiXssDetectionVisitor detectionVisitor = new DisableAntiXssDetectionVisitor();
        ((SimpleNode)this.data).jjtAccept((ParserVisitor)detectionVisitor, new Object());
        this.isAutoEncodeDisabled = detectionVisitor.isAutoEncodeDisabled();
        return this.isAutoEncodeDisabled;
    }

    @Override
    public boolean isDeclaredHtmlSafe() {
        if (this.data == null) {
            return false;
        }
        if (this.isHtmlSafe != null) {
            return this.isHtmlSafe;
        }
        HtmlSafeDetectionVisitor detectionVisitor = new HtmlSafeDetectionVisitor();
        ((SimpleNode)this.data).jjtAccept((ParserVisitor)detectionVisitor, new Object());
        this.isHtmlSafe = detectionVisitor.isDeclaredHtmlSafe();
        return this.isHtmlSafe;
    }

    private ResourceLoader getBaseResourceLoader() {
        ResourceLoader loader = super.getResourceLoader();
        if (loader instanceof ResourceLoaderWrapper) {
            return ((ResourceLoaderWrapper)loader).getBaseResourceLoader();
        }
        return loader;
    }

    @Override
    public boolean isPluginTemplate() {
        return this.getBaseResourceLoader() instanceof DynamicPluginResourceLoader;
    }

    private static class HtmlSafeDetectionVisitor
    extends BaseVisitor {
        private boolean declaredHtmlSafe = false;

        private HtmlSafeDetectionVisitor() {
        }

        public Object visit(ASTDirective node, Object data) {
            if (this.declaredHtmlSafe) {
                return true;
            }
            if (node.getDirectiveName().equals("htmlSafe")) {
                this.declaredHtmlSafe = true;
            }
            return this.declaredHtmlSafe;
        }

        public boolean isDeclaredHtmlSafe() {
            return this.declaredHtmlSafe;
        }
    }

    private static class DisableAntiXssDetectionVisitor
    extends BaseVisitor {
        private boolean disableAutoEncode = false;

        private DisableAntiXssDetectionVisitor() {
        }

        public Object visit(ASTDirective node, Object data) {
            if (!this.disableAutoEncode && node.getDirectiveName().equals("disableAntiXss")) {
                this.disableAutoEncode = true;
            }
            return true;
        }

        public boolean isAutoEncodeDisabled() {
            return this.disableAutoEncode;
        }
    }
}

