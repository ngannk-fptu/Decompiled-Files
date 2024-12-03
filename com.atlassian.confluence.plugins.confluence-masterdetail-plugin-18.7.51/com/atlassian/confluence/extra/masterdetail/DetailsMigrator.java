/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetailsMigrator
implements MacroMigration {
    private static final Logger LOG = LoggerFactory.getLogger(DetailsMigrator.class);
    public static final Pattern DETAILS_PAIR_PATTERN = Pattern.compile("^(.+?):(.+?)$", 8);
    private final XhtmlContent xhtmlUtils;

    public DetailsMigrator(XhtmlContent xhtmlUtils) {
        this.xhtmlUtils = xhtmlUtils;
    }

    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        String macroBodyText = StringUtils.defaultString((String)macroDefinition.getBodyText());
        Matcher detailsPairMatchers = DETAILS_PAIR_PATTERN.matcher(macroBodyText);
        try {
            StringBuilder bodyBuilder = new StringBuilder("<table class='confluenceTable'><tbody>");
            ArrayList<RuntimeException> conversionErrors = new ArrayList<RuntimeException>();
            while (detailsPairMatchers.find()) {
                String param = detailsPairMatchers.group(1);
                String value = this.convertWikimarkupToViewFormat(detailsPairMatchers.group(2), conversionContext, conversionErrors);
                bodyBuilder.append("<tr><td class='confluenceTd'>").append(HtmlUtil.htmlEncode((String)param)).append("</td>").append("<td class='confluenceTd'>").append(value).append("</td></tr>");
            }
            if (conversionErrors.isEmpty()) {
                macroDefinition.setBody((MacroBody)new RichTextMacroBody(bodyBuilder.append("</tbody></table>").toString()));
            } else {
                LOG.error("Details macro migration failed. See errors following");
                for (RuntimeException conversionError : conversionErrors) {
                    LOG.warn("Error converting macro body to XHTML", (Throwable)conversionError);
                }
            }
        }
        catch (XhtmlException xhtmlError) {
            LOG.error(String.format("Unable to convert details macro body\n%s", macroBodyText), (Throwable)xhtmlError);
        }
        catch (XMLStreamException io) {
            LOG.error(String.format("Error streaming XML content while converting details macro body\n%s", macroBodyText), (Throwable)io);
        }
        return macroDefinition;
    }

    private String convertWikimarkupToViewFormat(String value, ConversionContext conversionContext, List<RuntimeException> errors) throws XhtmlException, XMLStreamException {
        return this.xhtmlUtils.convertStorageToView(this.xhtmlUtils.convertWikiToStorage(value, conversionContext, errors), conversionContext);
    }
}

