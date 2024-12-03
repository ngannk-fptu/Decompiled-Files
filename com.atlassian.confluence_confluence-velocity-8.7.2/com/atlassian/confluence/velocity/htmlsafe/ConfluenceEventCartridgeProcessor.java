/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy
 *  com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessingChain
 *  com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor
 *  com.atlassian.velocity.htmlsafe.context.IncorrectHtmlEncodingWarningProcessor
 *  com.atlassian.velocity.htmlsafe.context.InsertionPolicyCartridgeProcessor
 *  com.atlassian.velocity.htmlsafe.context.NoOpEventCartridgeProcessor
 *  org.apache.velocity.app.event.EventCartridge
 */
package com.atlassian.confluence.velocity.htmlsafe;

import com.atlassian.confluence.velocity.htmlsafe.ConfluenceHtmlEntityEncodingPolicy;
import com.atlassian.velocity.htmlsafe.ReferenceInsertionPolicy;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessingChain;
import com.atlassian.velocity.htmlsafe.context.EventCartridgeProcessor;
import com.atlassian.velocity.htmlsafe.context.IncorrectHtmlEncodingWarningProcessor;
import com.atlassian.velocity.htmlsafe.context.InsertionPolicyCartridgeProcessor;
import com.atlassian.velocity.htmlsafe.context.NoOpEventCartridgeProcessor;
import org.apache.velocity.app.event.EventCartridge;

public final class ConfluenceEventCartridgeProcessor
implements EventCartridgeProcessor {
    private final EventCartridgeProcessor delegate;

    public ConfluenceEventCartridgeProcessor() {
        this(new ConfluenceHtmlEntityEncodingPolicy());
    }

    ConfluenceEventCartridgeProcessor(ReferenceInsertionPolicy referenceInsertionPolicy) {
        this.delegate = ConfluenceEventCartridgeProcessor.createDelegate(referenceInsertionPolicy);
    }

    public void processCartridge(EventCartridge cartridge) {
        this.delegate.processCartridge(cartridge);
    }

    private static EventCartridgeProcessor createDelegate(ReferenceInsertionPolicy referenceInsertionPolicy) {
        HtmlEncodingSystemProperty property = new HtmlEncodingSystemProperty();
        InsertionPolicyCartridgeProcessor insertionPolicyProcessor = new InsertionPolicyCartridgeProcessor(referenceInsertionPolicy);
        if (!property.isPresent()) {
            return new EventCartridgeProcessingChain(new EventCartridgeProcessor[]{new IncorrectHtmlEncodingWarningProcessor(), insertionPolicyProcessor});
        }
        if (property.isSet()) {
            return insertionPolicyProcessor;
        }
        return new NoOpEventCartridgeProcessor();
    }

    private static final class HtmlEncodingSystemProperty {
        private final String PROPERTY = System.getProperty("confluence.html.encode.automatic");

        private HtmlEncodingSystemProperty() {
        }

        boolean isPresent() {
            return this.PROPERTY != null;
        }

        boolean isSet() {
            return Boolean.parseBoolean(this.PROPERTY);
        }
    }
}

