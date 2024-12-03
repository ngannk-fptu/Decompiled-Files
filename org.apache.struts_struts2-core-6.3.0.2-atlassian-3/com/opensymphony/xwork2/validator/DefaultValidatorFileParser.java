/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlHelper;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.validator.ValidatorConfig;
import com.opensymphony.xwork2.validator.ValidatorFactory;
import com.opensymphony.xwork2.validator.ValidatorFileParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class DefaultValidatorFileParser
implements ValidatorFileParser {
    private static Logger LOG = LogManager.getLogger(DefaultValidatorFileParser.class);
    static final String DEFAULT_MULTI_TEXTVALUE_SEPARATOR = " ";
    static final String MULTI_TEXTVALUE_SEPARATOR_CONFIG_KEY = "xwork.validatorfileparser.multi_textvalue_separator";
    private ObjectFactory objectFactory;
    private String multiTextvalueSeparator = " ";

    @Inject(value="xwork.validatorfileparser.multi_textvalue_separator", required=false)
    public void setMultiTextvalueSeparator(String type) {
        this.multiTextvalueSeparator = type;
    }

    public String getMultiTextvalueSeparator() {
        return this.multiTextvalueSeparator;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Override
    public List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, String resourceName) {
        ArrayList<ValidatorConfig> validatorCfgs = new ArrayList<ValidatorConfig>();
        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);
        HashMap<String, String> dtdMappings = new HashMap<String, String>();
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0//EN", "xwork-validator-1.0.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0.2//EN", "xwork-validator-1.0.2.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator 1.0.3//EN", "xwork-validator-1.0.3.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator Config 1.0//EN", "xwork-validator-config-1.0.dtd");
        Document doc = DomHelper.parse(in, dtdMappings);
        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");
            NodeList validatorNodes = doc.getElementsByTagName("validator");
            this.addValidatorConfigs(validatorFactory, validatorNodes, new HashMap<String, Object>(), validatorCfgs);
            for (int i = 0; i < fieldNodes.getLength(); ++i) {
                Element fieldElement = (Element)fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                HashMap<String, Object> extraParams = new HashMap<String, Object>();
                extraParams.put("fieldName", fieldName);
                NodeList validatorNodes2 = fieldElement.getElementsByTagName("field-validator");
                this.addValidatorConfigs(validatorFactory, validatorNodes2, extraParams, validatorCfgs);
            }
        }
        return validatorCfgs;
    }

    @Override
    public void parseValidatorDefinitions(Map<String, String> validators, InputStream is, String resourceName) {
        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);
        HashMap<String, String> dtdMappings = new HashMap<String, String>();
        dtdMappings.put("-//Apache Struts//XWork Validator Config 1.0//EN", "xwork-validator-config-1.0.dtd");
        dtdMappings.put("-//Apache Struts//XWork Validator Definition 1.0//EN", "xwork-validator-definition-1.0.dtd");
        Document doc = DomHelper.parse(in, dtdMappings);
        if (doc != null) {
            NodeList nodes = doc.getElementsByTagName("validator");
            for (int i = 0; i < nodes.getLength(); ++i) {
                Element validatorElement = (Element)nodes.item(i);
                String name = validatorElement.getAttribute("name");
                String className = validatorElement.getAttribute("class");
                try {
                    this.objectFactory.buildValidator(className, new HashMap<String, Object>(), ActionContext.getContext().getContextMap());
                    validators.put(name, className);
                    continue;
                }
                catch (Exception e) {
                    throw new ConfigurationException("Unable to load validator class " + className, e, validatorElement);
                }
            }
        }
    }

    public String getTextValue(Element valueEle) {
        StringBuilder value = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        boolean firstCDataFound = false;
        for (int i = 0; i < nl.getLength(); ++i) {
            String nodeValue;
            Node item = nl.item(i);
            if ((!(item instanceof CharacterData) || item instanceof Comment) && !(item instanceof EntityReference) || (nodeValue = item.getNodeValue()) == null) continue;
            if (firstCDataFound) {
                value.append(this.getMultiTextvalueSeparator());
            } else {
                firstCDataFound = true;
            }
            value.append(nodeValue.trim());
        }
        return value.toString().trim();
    }

    private void addValidatorConfigs(ValidatorFactory factory, NodeList validatorNodes, Map<String, Object> extraParams, List<ValidatorConfig> validatorCfgs) {
        for (int j = 0; j < validatorNodes.getLength(); ++j) {
            Element validatorElement = (Element)validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            HashMap<String, Object> params = new HashMap<String, Object>(extraParams);
            params.putAll(XmlHelper.getParams(validatorElement));
            try {
                factory.lookupRegisteredValidatorType(validatorType);
            }
            catch (IllegalArgumentException ex) {
                throw new ConfigurationException("Invalid validation type: " + validatorType, (Object)validatorElement);
            }
            ValidatorConfig.Builder vCfg = new ValidatorConfig.Builder(validatorType).addParams(params).location(DomHelper.getLocationObject(validatorElement)).shortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")));
            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element)messageNodes.item(0);
            Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = defaultMessageNode == null ? "" : defaultMessageNode.getNodeValue();
            vCfg.defaultMessage(defaultMessage);
            Map<String, String> messageParams = XmlHelper.getParams(messageElement);
            String key = messageElement.getAttribute("key");
            if (key != null && key.trim().length() > 0) {
                vCfg.messageKey(key);
                if (messageParams.containsKey("defaultMessage")) {
                    vCfg.defaultMessage(messageParams.get("defaultMessage"));
                }
                TreeMap<Integer, String> sortedMessageParameters = new TreeMap<Integer, String>();
                for (Map.Entry<String, String> messageParamEntry : messageParams.entrySet()) {
                    try {
                        int _order = Integer.parseInt(messageParamEntry.getKey());
                        sortedMessageParameters.put(_order, messageParamEntry.getValue());
                    }
                    catch (NumberFormatException numberFormatException) {}
                }
                vCfg.messageParams(sortedMessageParameters.values().toArray(new String[sortedMessageParameters.values().size()]));
            } else if (messageParams != null && messageParams.size() > 0 && LOG.isWarnEnabled()) {
                LOG.warn("validator of type [" + validatorType + "] have i18n message parameters defined but no i18n message key, it's parameters will be ignored");
            }
            validatorCfgs.add(vCfg.build());
        }
    }
}

