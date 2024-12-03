/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
 *  org.springframework.beans.factory.xml.ParserContext
 */
package org.eclipse.gemini.blueprint.config.internal;

import java.util.Locale;
import org.eclipse.gemini.blueprint.bundle.BundleActionEnum;
import org.eclipse.gemini.blueprint.bundle.BundleFactoryBean;
import org.eclipse.gemini.blueprint.config.internal.util.AttributeCallback;
import org.eclipse.gemini.blueprint.config.internal.util.ParserUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BundleBeanDefinitionParser
extends AbstractSingleBeanDefinitionParser {
    private static final String ACTION = "action";
    private static final String DESTROY_ACTION = "destroy-action";
    private static final String ACTION_PROP = "bundleAction";
    private static final String DESTROY_ACTION_PROP = "bundleDestroyAction";
    private static final String BUNDLE_PROP = "bundle";

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        BundleActionCallback callback = new BundleActionCallback();
        ParserUtils.parseCustomAttributes(element, builder, new AttributeCallback[]{callback});
        if (element.hasChildNodes()) {
            NodeList nodes = element.getChildNodes();
            boolean foundElement = false;
            for (int i = 0; i < nodes.getLength() && !foundElement; ++i) {
                Node nd = nodes.item(i);
                if (!(nd instanceof Element)) continue;
                foundElement = true;
                Object obj = parserContext.getDelegate().parsePropertySubElement((Element)nd, (BeanDefinition)builder.getBeanDefinition());
                builder.addPropertyValue(BUNDLE_PROP, obj);
            }
        }
        builder.setRole(2);
    }

    protected Class getBeanClass(Element element) {
        return BundleFactoryBean.class;
    }

    static class BundleActionCallback
    implements AttributeCallback {
        BundleActionCallback() {
        }

        @Override
        public boolean process(Element parent, Attr attribute, BeanDefinitionBuilder builder) {
            String name = attribute.getLocalName();
            if (BundleBeanDefinitionParser.ACTION.equals(name)) {
                builder.addPropertyValue(BundleBeanDefinitionParser.ACTION_PROP, this.parseAction(parent, attribute));
                return false;
            }
            if (BundleBeanDefinitionParser.DESTROY_ACTION.equals(name)) {
                builder.addPropertyValue(BundleBeanDefinitionParser.DESTROY_ACTION_PROP, this.parseAction(parent, attribute));
                return false;
            }
            return true;
        }

        private Object parseAction(Element parent, Attr attribute) {
            return Enum.valueOf(BundleActionEnum.class, attribute.getValue().toUpperCase(Locale.ENGLISH));
        }
    }
}

