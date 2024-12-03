/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import java.util.Properties;
import java.util.Set;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.generator.model.NodeElement;
import net.sf.ehcache.config.generator.model.SimpleNodeAttribute;
import net.sf.ehcache.config.generator.model.SimpleNodeElement;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.search.attribute.AttributeType;
import net.sf.ehcache.search.attribute.JavaBeanAttributeExtractor;
import net.sf.ehcache.search.attribute.ReflectionAttributeExtractor;
import net.sf.ehcache.util.ClassLoaderUtil;
import net.sf.ehcache.util.PropertyUtil;

public class SearchAttribute {
    private static final Class<?> UNRESOLVED = UnresolvedType.class;
    private String name;
    private String className;
    private String expression;
    private String properties;
    private String propertySeparator;
    private String typeName;
    private Class<?> type;

    public void setName(String name) {
        this.name = name;
    }

    public void setClass(String className) {
        if (this.expression != null) {
            throw new InvalidConfigurationException("Cannot set both class and expression for a search attribute");
        }
        this.className = className;
    }

    public void setExpression(String expression) {
        if (this.className != null) {
            throw new InvalidConfigurationException("Cannot set both class and expression for a search attribute");
        }
        this.expression = expression;
    }

    public void setType(String type) {
        this.type = UNRESOLVED;
        this.typeName = type;
    }

    public void setType(Class<?> type) {
        this.typeName = this.validateType(type);
        this.type = type;
    }

    public String getClassName() {
        return this.className;
    }

    public String getExpression() {
        return this.expression;
    }

    public String getName() {
        return this.name;
    }

    public String getTypeName() {
        return this.typeName;
    }

    Class<?> getType(ClassLoader loader) {
        if (this.type == UNRESOLVED) {
            this.type = this.validateType(this.typeName, loader);
        }
        return this.type;
    }

    public AttributeExtractor constructExtractor(ClassLoader loader) {
        if (this.name == null) {
            throw new InvalidConfigurationException("search attribute has no name");
        }
        if (this.expression != null) {
            return new ReflectionAttributeExtractor(this.expression);
        }
        if (this.className != null) {
            if (this.properties != null) {
                return (AttributeExtractor)ClassLoaderUtil.createNewInstance(loader, this.className, new Class[]{Properties.class}, new Object[]{PropertyUtil.parseProperties(this.properties, this.propertySeparator)});
            }
            return (AttributeExtractor)ClassLoaderUtil.createNewInstance(loader, this.className);
        }
        return new JavaBeanAttributeExtractor(this.name);
    }

    public SearchAttribute name(String name) {
        this.setName(name);
        return this;
    }

    public SearchAttribute className(String className) {
        this.setClass(className);
        return this;
    }

    public SearchAttribute expression(String expression) {
        this.setExpression(expression);
        return this;
    }

    public SearchAttribute type(String type) {
        this.setType(type);
        return this;
    }

    public SearchAttribute type(Class<?> type) {
        this.setType(type);
        return this;
    }

    public void setProperties(String props) {
        this.properties = props;
    }

    public void setPropertySeparator(String sep) {
        this.propertySeparator = sep;
    }

    public SearchAttribute propertySeparator(String sep) {
        this.setPropertySeparator(sep);
        return this;
    }

    public SearchAttribute properties(String props) {
        this.setProperties(props);
        return this;
    }

    public NodeElement asConfigElement(NodeElement parent) {
        SimpleNodeElement rv = new SimpleNodeElement(parent, "searchAttribute");
        rv.addAttribute(new SimpleNodeAttribute("name", this.name));
        if (this.expression != null) {
            rv.addAttribute(new SimpleNodeAttribute("expression", this.expression));
        } else if (this.className != null) {
            rv.addAttribute(new SimpleNodeAttribute("class", this.className));
            if (this.properties != null) {
                rv.addAttribute(new SimpleNodeAttribute("properties", this.properties));
            }
            if (this.propertySeparator != null) {
                rv.addAttribute(new SimpleNodeAttribute("propertySeparator", this.propertySeparator));
            }
        }
        if (this.typeName != null) {
            rv.addAttribute(new SimpleNodeAttribute("type", this.typeName));
        }
        return rv;
    }

    private Class<?> validateType(String attrType, ClassLoader loader) {
        Class<?> realType = null;
        for (Class<?> c : AttributeType.getSupportedJavaTypes()) {
            if (attrType.equals(c.getName())) {
                realType = c;
                break;
            }
            String[] groups = c.getName().split("\\.");
            if (!attrType.equals(groups[groups.length - 1])) continue;
            if (realType != null) {
                throw new InvalidConfigurationException("Ambiguous attribute type " + attrType);
            }
            realType = c;
        }
        if (realType == null) {
            try {
                realType = loader.loadClass(attrType);
            }
            catch (ClassNotFoundException e) {
                throw new InvalidConfigurationException(String.format("Unable to load class specified as type of attribute %s: %s", this.name, e.getMessage()));
            }
            if (!realType.isEnum()) {
                throw new InvalidConfigurationException(String.format("Unsupported attribute type specified %s for search attribute %s", attrType, this.name));
            }
        }
        return realType;
    }

    private String validateType(Class<?> attrType) {
        Set<Class<?>> knownTypes = AttributeType.getSupportedJavaTypes();
        String t = attrType.getName();
        if (!knownTypes.contains(attrType) && !attrType.isEnum()) {
            throw new InvalidConfigurationException(String.format("Unsupported attribute type specified %s for search attribute %s", t, this.name));
        }
        return t;
    }

    private static class UnresolvedType {
        private UnresolvedType() {
        }
    }
}

