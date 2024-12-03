/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.whitelist.LegacyWhitelistRule
 *  com.atlassian.plugins.whitelist.WhitelistRule
 *  com.atlassian.security.xml.libs.SecureDom4jFactory
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.Node
 *  org.dom4j.io.SAXReader
 */
package com.atlassian.plugins.whitelist.core.migration.confluence.macros;

import com.atlassian.plugins.whitelist.LegacyWhitelistRule;
import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.core.migration.confluence.macros.BandanaMacroWhitelistXmlData;
import com.atlassian.security.xml.libs.SecureDom4jFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class BandanaMacroWhitelistXmlParser {
    private static final boolean WHITELIST_ACTIVATED_BY_DEFAULT = true;
    private final SAXReader saxReader = SecureDom4jFactory.newSaxReader();

    public BandanaMacroWhitelistXmlData parseData(String xml) {
        if (StringUtils.isNotEmpty((CharSequence)xml)) {
            Document document = this.parseDocument((String)Preconditions.checkNotNull((Object)xml, (Object)"xml"));
            return new BandanaMacroWhitelistXmlData(this.isAllowAll(document), this.getAcceptRules(document));
        }
        return new BandanaMacroWhitelistXmlData(true, Collections.emptyList());
    }

    private Document parseDocument(String xml) {
        try {
            return this.saxReader.read((Reader)new StringReader(xml));
        }
        catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAllowAll(Document document) {
        Node node = document.selectSingleNode("//allowAll/text()");
        return node != null && Boolean.parseBoolean(node.getStringValue());
    }

    private Collection<WhitelistRule> getAcceptRules(Document document) {
        List nodeList = document.selectNodes("//acceptRules/string");
        List whitelistBeanList = Lists.transform((List)nodeList, this.transformNodeToWhitelistRule());
        return Lists.newArrayList((Iterable)Iterables.filter((Iterable)whitelistBeanList, (Predicate)Predicates.notNull()));
    }

    private Function<Node, WhitelistRule> transformNodeToWhitelistRule() {
        return new Function<Node, WhitelistRule>(){

            public WhitelistRule apply(@Nullable Node input) {
                if (input == null) {
                    return null;
                }
                String textContent = input.getText();
                if (StringUtils.isEmpty((CharSequence)textContent)) {
                    return null;
                }
                return new LegacyWhitelistRule(textContent);
            }
        };
    }
}

