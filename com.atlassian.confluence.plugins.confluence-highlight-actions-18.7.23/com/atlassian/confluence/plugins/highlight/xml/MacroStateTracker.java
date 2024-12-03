/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.highlight.xml;

import com.atlassian.confluence.plugins.highlight.xml.ModificationStateTrackerV2;
import com.google.common.collect.ImmutableSet;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class MacroStateTracker
implements ModificationStateTrackerV2 {
    private static final Logger logger = LoggerFactory.getLogger(MacroStateTracker.class);
    private static final String macroStrV2 = "ac:structured-macro";
    private static final String macroStrV1 = "ac:macro";
    private static final String macroParamStr = "ac:parameter";
    private static final String macroNameParamStr = "ac:name";
    private static final String macroBody = "ac:rich-text-body";
    private static final String macroPlaintextBody = "ac:plain-text-body";
    private static final String macroHiddenAttrName = "hidden";
    private static final Set<String> allowedAcTags = ImmutableSet.of((Object)"ac:macro", (Object)"ac:structured-macro", (Object)"ac:parameter", (Object)"ac:rich-text-body", (Object)"ac:plain-text-body");
    private static final Set<String> allowedMacroBody = ImmutableSet.of((Object)"ac:rich-text-body", (Object)"ac:plain-text-body");
    private Stack<MacroInfo> macroInfos = new Stack();
    private boolean processMacroParam;
    private static Function<MacroInfo, Boolean> defaultAllowLogic = macroInfo -> macroInfo.isHasBody();
    private static Map<String, Function<MacroInfo, Boolean>> allowingMacroBehaviours = Stream.of(new AbstractMap.SimpleEntry<String, Function<MacroInfo, Boolean>>("details", macroInfo -> {
        String hiddenValue = macroInfo.getParam(macroHiddenAttrName);
        boolean isInvisible = StringUtils.isNotEmpty((CharSequence)hiddenValue) && Boolean.parseBoolean(hiddenValue);
        return !isInvisible && defaultAllowLogic.apply((MacroInfo)macroInfo) != false;
    })).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    @Override
    public boolean shouldProcessText(Node node) {
        if (this.processMacroParam) {
            MacroInfo macroInfo;
            Node nameAttrValue;
            logger.debug("Processing macro param node");
            Node parentNode = node.getParentNode();
            if (parentNode.getAttributes() != null && (nameAttrValue = parentNode.getAttributes().getNamedItem(macroNameParamStr)) != null && (macroInfo = this.macroInfos.peek()) != null) {
                String name = nameAttrValue.getNodeValue();
                String value = node.getNodeValue();
                logger.debug("Adding attribute name {} - value {}", (Object)name, (Object)value);
                macroInfo.addParam(name, value);
            }
            logger.debug("Processing macro param node ==> Finish");
        }
        return this.isMacroAllow();
    }

    public boolean isMacroAllow() {
        boolean isAllow = true;
        for (int i = 0; i < this.macroInfos.size(); ++i) {
            MacroInfo macroInfo = (MacroInfo)this.macroInfos.get(i);
            isAllow = isAllow && macroInfo.isAllow();
        }
        return isAllow;
    }

    @Override
    public void forward(Node node, String tagName) {
        Node nameAttrValue;
        if (!allowedAcTags.contains(tagName)) {
            return;
        }
        if (macroParamStr.equalsIgnoreCase(tagName) && !this.macroInfos.empty()) {
            logger.debug("Start process macro param node");
            this.processMacroParam = true;
            return;
        }
        if (allowedMacroBody.contains(tagName) && !this.macroInfos.empty()) {
            MacroInfo macroInfo = this.macroInfos.peek();
            if (macroInfo != null) {
                logger.debug("Current macro has a body");
                macroInfo.setHasBody(true);
            } else {
                logger.debug("Don't have any existing macro in stack");
            }
            return;
        }
        String macroName = "";
        if (node.getAttributes() != null && (nameAttrValue = node.getAttributes().getNamedItem(macroNameParamStr)) != null) {
            macroName = nameAttrValue.getNodeValue();
        }
        this.macroInfos.add(new MacroInfo(macroName));
    }

    @Override
    public void back(Node node, String tagName) {
        if (macroParamStr.equalsIgnoreCase(tagName) && !this.macroInfos.empty()) {
            this.processMacroParam = false;
        }
        if (!macroStrV2.equalsIgnoreCase(tagName)) {
            return;
        }
        logger.debug("Finish processing a macro");
        this.macroInfos.pop();
    }

    @Override
    public boolean allowInsertion() {
        return false;
    }

    private class MacroInfo {
        private boolean hasBody = false;
        private String name;
        private Map<String, String> params;

        public MacroInfo(String name) {
            this.name = name;
            this.params = new HashMap<String, String>();
        }

        public String getName() {
            return this.name;
        }

        public void addParam(String name, String value) {
            this.params.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
        }

        public String getParam(String paramName) {
            return this.params.getOrDefault(paramName, "");
        }

        public void setHasBody(boolean hasBody) {
            this.hasBody = hasBody;
        }

        public boolean isHasBody() {
            return this.hasBody;
        }

        public boolean isAllow() {
            return allowingMacroBehaviours.getOrDefault(this.name, defaultAllowLogic).apply(this);
        }
    }
}

