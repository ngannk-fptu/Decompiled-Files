/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  com.atlassian.botocss.BotocssStyles
 *  com.atlassian.botocss.DocumentFunctions
 *  org.apache.commons.lang3.StringUtils
 *  org.jsoup.nodes.Document
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail;

import com.atlassian.botocss.Botocss;
import com.atlassian.botocss.BotocssStyles;
import com.atlassian.botocss.DocumentFunctions;
import com.atlassian.confluence.mail.MailContentProcessor;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotocssMailContentProcessor
implements MailContentProcessor {
    private static final Logger log = LoggerFactory.getLogger(BotocssMailContentProcessor.class);
    private static final Function<Document, Document> STYLE_PRUNING_AND_ZERO_INDENT_FUNCTION = DocumentFunctions.ZERO_INDENT.compose(document -> {
        document.select("style.delete-email-style").remove();
        log.debug("Pruning selected styles from the email.");
        return document;
    });

    @Override
    public String process(String input) {
        if (StringUtils.isEmpty((CharSequence)input)) {
            return input;
        }
        log.debug("Botocss is about to inject styles in to the email.");
        try {
            String result = Botocss.inject((String)input, (BotocssStyles)BotocssStyles.EMPTY, STYLE_PRUNING_AND_ZERO_INDENT_FUNCTION);
            if (StringUtils.isBlank((CharSequence)result)) {
                log.warn("The result of the Botocss injection was blank! Returning the input.");
                return input;
            }
            return result;
        }
        catch (Exception e) {
            log.error("Botocss got botched! Oh god, there's collagen everywhere!", (Throwable)e);
            log.warn("The Botocss operation failed. Returning the input.");
            return input;
        }
    }
}

