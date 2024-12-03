/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.FatalBeanException
 *  org.springframework.beans.factory.parsing.ReaderContext
 *  org.springframework.beans.factory.xml.XmlReaderContext
 *  org.springframework.core.type.filter.AnnotationTypeFilter
 *  org.springframework.core.type.filter.AspectJTypeFilter
 *  org.springframework.core.type.filter.AssignableTypeFilter
 *  org.springframework.core.type.filter.RegexPatternTypeFilter
 *  org.springframework.core.type.filter.TypeFilter
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.parsing.ReaderContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AspectJTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TypeFilterParser {
    private static final String FILTER_TYPE_ATTRIBUTE = "type";
    private static final String FILTER_EXPRESSION_ATTRIBUTE = "expression";
    private final ReaderContext readerContext;
    private final ClassLoader classLoader;

    public TypeFilterParser(XmlReaderContext readerContext) {
        this((ReaderContext)readerContext, ConfigurationUtils.getRequiredClassLoader(readerContext));
    }

    TypeFilterParser(ReaderContext readerContext, ClassLoader classLoader) {
        Assert.notNull((Object)readerContext, (String)"ReaderContext must not be null!");
        Assert.notNull((Object)classLoader, (String)"ClassLoader must not be null!");
        this.readerContext = readerContext;
        this.classLoader = classLoader;
    }

    public Collection<TypeFilter> parseTypeFilters(Element element, Type type) {
        NodeList nodeList = element.getChildNodes();
        HashSet<TypeFilter> filters = new HashSet<TypeFilter>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            Element childElement = type.getElement(node);
            if (childElement == null) continue;
            try {
                filters.add(this.createTypeFilter(childElement, this.classLoader));
                continue;
            }
            catch (RuntimeException e) {
                this.readerContext.error(e.getMessage(), this.readerContext.extractSource((Object)element), e.getCause());
            }
        }
        return filters;
    }

    protected TypeFilter createTypeFilter(Element element, ClassLoader classLoader) {
        String filterType = element.getAttribute(FILTER_TYPE_ATTRIBUTE);
        String expression = element.getAttribute(FILTER_EXPRESSION_ATTRIBUTE);
        try {
            FilterType filter = FilterType.fromString(filterType);
            return filter.getFilter(expression, classLoader);
        }
        catch (ClassNotFoundException ex) {
            throw new FatalBeanException("Type filter class not found: " + expression, (Throwable)ex);
        }
    }

    public static enum Type {
        INCLUDE("include-filter"),
        EXCLUDE("exclude-filter");

        private String elementName;

        private Type(String elementName) {
            this.elementName = elementName;
        }

        @Nullable
        Element getElement(Node node) {
            String localName;
            if (node.getNodeType() == 1 && this.elementName.equals(localName = node.getLocalName())) {
                return (Element)node;
            }
            return null;
        }
    }

    private static enum FilterType {
        ANNOTATION{

            @Override
            public TypeFilter getFilter(String expression, ClassLoader classLoader) throws ClassNotFoundException {
                return new AnnotationTypeFilter(classLoader.loadClass(expression));
            }
        }
        ,
        ASSIGNABLE{

            @Override
            public TypeFilter getFilter(String expression, ClassLoader classLoader) throws ClassNotFoundException {
                return new AssignableTypeFilter(classLoader.loadClass(expression));
            }
        }
        ,
        ASPECTJ{

            @Override
            public TypeFilter getFilter(String expression, ClassLoader classLoader) {
                return new AspectJTypeFilter(expression, classLoader);
            }
        }
        ,
        REGEX{

            @Override
            public TypeFilter getFilter(String expression, ClassLoader classLoader) {
                return new RegexPatternTypeFilter(Pattern.compile(expression));
            }
        }
        ,
        CUSTOM{

            @Override
            public TypeFilter getFilter(String expression, ClassLoader classLoader) throws ClassNotFoundException {
                Class<?> filterClass = classLoader.loadClass(expression);
                if (!TypeFilter.class.isAssignableFrom(filterClass)) {
                    throw new IllegalArgumentException("Class is not assignable to [" + TypeFilter.class.getName() + "]: " + expression);
                }
                return (TypeFilter)BeanUtils.instantiateClass(filterClass);
            }
        };


        abstract TypeFilter getFilter(String var1, ClassLoader var2) throws ClassNotFoundException;

        static FilterType fromString(String typeString) {
            for (FilterType filter : FilterType.values()) {
                if (!filter.name().equalsIgnoreCase(typeString)) continue;
                return filter;
            }
            throw new IllegalArgumentException("Unsupported filter type: " + typeString);
        }
    }
}

