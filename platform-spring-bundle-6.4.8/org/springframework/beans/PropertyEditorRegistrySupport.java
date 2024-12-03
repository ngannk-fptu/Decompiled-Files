/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharArrayPropertyEditor;
import org.springframework.beans.propertyeditors.CharacterEditor;
import org.springframework.beans.propertyeditors.CharsetEditor;
import org.springframework.beans.propertyeditors.ClassArrayEditor;
import org.springframework.beans.propertyeditors.ClassEditor;
import org.springframework.beans.propertyeditors.CurrencyEditor;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomMapEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.FileEditor;
import org.springframework.beans.propertyeditors.InputSourceEditor;
import org.springframework.beans.propertyeditors.InputStreamEditor;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.beans.propertyeditors.PathEditor;
import org.springframework.beans.propertyeditors.PatternEditor;
import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.beans.propertyeditors.ReaderEditor;
import org.springframework.beans.propertyeditors.StringArrayPropertyEditor;
import org.springframework.beans.propertyeditors.TimeZoneEditor;
import org.springframework.beans.propertyeditors.URIEditor;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.beans.propertyeditors.ZoneIdEditor;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.xml.sax.InputSource;

public class PropertyEditorRegistrySupport
implements PropertyEditorRegistry {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    @Nullable
    private ConversionService conversionService;
    private boolean defaultEditorsActive = false;
    private boolean configValueEditorsActive = false;
    @Nullable
    private Map<Class<?>, PropertyEditor> defaultEditors;
    @Nullable
    private Map<Class<?>, PropertyEditor> overriddenDefaultEditors;
    @Nullable
    private Map<Class<?>, PropertyEditor> customEditors;
    @Nullable
    private Map<String, CustomEditorHolder> customEditorsForPath;
    @Nullable
    private Map<Class<?>, PropertyEditor> customEditorCache;

    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    protected void registerDefaultEditors() {
        this.defaultEditorsActive = true;
    }

    public void useConfigValueEditors() {
        this.configValueEditorsActive = true;
    }

    public void overrideDefaultEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        if (this.overriddenDefaultEditors == null) {
            this.overriddenDefaultEditors = new HashMap();
        }
        this.overriddenDefaultEditors.put(requiredType, propertyEditor);
    }

    @Nullable
    public PropertyEditor getDefaultEditor(Class<?> requiredType) {
        PropertyEditor editor;
        if (!this.defaultEditorsActive) {
            return null;
        }
        if (this.overriddenDefaultEditors != null && (editor = this.overriddenDefaultEditors.get(requiredType)) != null) {
            return editor;
        }
        if (this.defaultEditors == null) {
            this.createDefaultEditors();
        }
        return this.defaultEditors.get(requiredType);
    }

    private void createDefaultEditors() {
        this.defaultEditors = new HashMap(64);
        this.defaultEditors.put(Charset.class, new CharsetEditor());
        this.defaultEditors.put(Class.class, new ClassEditor());
        this.defaultEditors.put(Class[].class, new ClassArrayEditor());
        this.defaultEditors.put(Currency.class, new CurrencyEditor());
        this.defaultEditors.put(File.class, new FileEditor());
        this.defaultEditors.put(InputStream.class, new InputStreamEditor());
        if (!shouldIgnoreXml) {
            this.defaultEditors.put(InputSource.class, new InputSourceEditor());
        }
        this.defaultEditors.put(Locale.class, new LocaleEditor());
        this.defaultEditors.put(Path.class, new PathEditor());
        this.defaultEditors.put(Pattern.class, new PatternEditor());
        this.defaultEditors.put(Properties.class, new PropertiesEditor());
        this.defaultEditors.put(Reader.class, new ReaderEditor());
        this.defaultEditors.put(Resource[].class, new ResourceArrayPropertyEditor());
        this.defaultEditors.put(TimeZone.class, new TimeZoneEditor());
        this.defaultEditors.put(URI.class, new URIEditor());
        this.defaultEditors.put(URL.class, new URLEditor());
        this.defaultEditors.put(UUID.class, new UUIDEditor());
        this.defaultEditors.put(ZoneId.class, new ZoneIdEditor());
        this.defaultEditors.put(Collection.class, new CustomCollectionEditor(Collection.class));
        this.defaultEditors.put(Set.class, new CustomCollectionEditor(Set.class));
        this.defaultEditors.put(SortedSet.class, new CustomCollectionEditor(SortedSet.class));
        this.defaultEditors.put(List.class, new CustomCollectionEditor(List.class));
        this.defaultEditors.put(SortedMap.class, new CustomMapEditor(SortedMap.class));
        this.defaultEditors.put(byte[].class, new ByteArrayPropertyEditor());
        this.defaultEditors.put(char[].class, new CharArrayPropertyEditor());
        this.defaultEditors.put(Character.TYPE, new CharacterEditor(false));
        this.defaultEditors.put(Character.class, new CharacterEditor(true));
        this.defaultEditors.put(Boolean.TYPE, new CustomBooleanEditor(false));
        this.defaultEditors.put(Boolean.class, new CustomBooleanEditor(true));
        this.defaultEditors.put(Byte.TYPE, new CustomNumberEditor(Byte.class, false));
        this.defaultEditors.put(Byte.class, new CustomNumberEditor(Byte.class, true));
        this.defaultEditors.put(Short.TYPE, new CustomNumberEditor(Short.class, false));
        this.defaultEditors.put(Short.class, new CustomNumberEditor(Short.class, true));
        this.defaultEditors.put(Integer.TYPE, new CustomNumberEditor(Integer.class, false));
        this.defaultEditors.put(Integer.class, new CustomNumberEditor(Integer.class, true));
        this.defaultEditors.put(Long.TYPE, new CustomNumberEditor(Long.class, false));
        this.defaultEditors.put(Long.class, new CustomNumberEditor(Long.class, true));
        this.defaultEditors.put(Float.TYPE, new CustomNumberEditor(Float.class, false));
        this.defaultEditors.put(Float.class, new CustomNumberEditor(Float.class, true));
        this.defaultEditors.put(Double.TYPE, new CustomNumberEditor(Double.class, false));
        this.defaultEditors.put(Double.class, new CustomNumberEditor(Double.class, true));
        this.defaultEditors.put(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
        this.defaultEditors.put(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
        if (this.configValueEditorsActive) {
            StringArrayPropertyEditor sae = new StringArrayPropertyEditor();
            this.defaultEditors.put(String[].class, sae);
            this.defaultEditors.put(short[].class, sae);
            this.defaultEditors.put(int[].class, sae);
            this.defaultEditors.put(long[].class, sae);
        }
    }

    protected void copyDefaultEditorsTo(PropertyEditorRegistrySupport target) {
        target.defaultEditorsActive = this.defaultEditorsActive;
        target.configValueEditorsActive = this.configValueEditorsActive;
        target.defaultEditors = this.defaultEditors;
        target.overriddenDefaultEditors = this.overriddenDefaultEditors;
    }

    @Override
    public void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor) {
        this.registerCustomEditor(requiredType, null, propertyEditor);
    }

    @Override
    public void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor) {
        if (requiredType == null && propertyPath == null) {
            throw new IllegalArgumentException("Either requiredType or propertyPath is required");
        }
        if (propertyPath != null) {
            if (this.customEditorsForPath == null) {
                this.customEditorsForPath = new LinkedHashMap<String, CustomEditorHolder>(16);
            }
            this.customEditorsForPath.put(propertyPath, new CustomEditorHolder(propertyEditor, requiredType));
        } else {
            if (this.customEditors == null) {
                this.customEditors = new LinkedHashMap(16);
            }
            this.customEditors.put(requiredType, propertyEditor);
            this.customEditorCache = null;
        }
    }

    @Override
    @Nullable
    public PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath) {
        Class<?> requiredTypeToUse = requiredType;
        if (propertyPath != null) {
            if (this.customEditorsForPath != null) {
                PropertyEditor editor = this.getCustomEditor(propertyPath, requiredType);
                if (editor == null) {
                    ArrayList<String> strippedPaths = new ArrayList<String>();
                    this.addStrippedPropertyPaths(strippedPaths, "", propertyPath);
                    Iterator it = strippedPaths.iterator();
                    while (it.hasNext() && editor == null) {
                        String strippedPath = (String)it.next();
                        editor = this.getCustomEditor(strippedPath, requiredType);
                    }
                }
                if (editor != null) {
                    return editor;
                }
            }
            if (requiredType == null) {
                requiredTypeToUse = this.getPropertyType(propertyPath);
            }
        }
        return this.getCustomEditor(requiredTypeToUse);
    }

    public boolean hasCustomEditorForElement(@Nullable Class<?> elementType, @Nullable String propertyPath) {
        if (propertyPath != null && this.customEditorsForPath != null) {
            for (Map.Entry<String, CustomEditorHolder> entry : this.customEditorsForPath.entrySet()) {
                if (!PropertyAccessorUtils.matchesProperty(entry.getKey(), propertyPath) || entry.getValue().getPropertyEditor(elementType) == null) continue;
                return true;
            }
        }
        return elementType != null && this.customEditors != null && this.customEditors.containsKey(elementType);
    }

    @Nullable
    protected Class<?> getPropertyType(String propertyPath) {
        return null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(String propertyName, @Nullable Class<?> requiredType) {
        CustomEditorHolder holder = this.customEditorsForPath != null ? this.customEditorsForPath.get(propertyName) : null;
        return holder != null ? holder.getPropertyEditor(requiredType) : null;
    }

    @Nullable
    private PropertyEditor getCustomEditor(@Nullable Class<?> requiredType) {
        if (requiredType == null || this.customEditors == null) {
            return null;
        }
        PropertyEditor editor = this.customEditors.get(requiredType);
        if (editor == null) {
            if (this.customEditorCache != null) {
                editor = this.customEditorCache.get(requiredType);
            }
            if (editor == null) {
                for (Map.Entry<Class<?>, PropertyEditor> entry : this.customEditors.entrySet()) {
                    if (editor != null) break;
                    Class<?> key = entry.getKey();
                    if (!key.isAssignableFrom(requiredType)) continue;
                    editor = entry.getValue();
                    if (this.customEditorCache == null) {
                        this.customEditorCache = new HashMap();
                    }
                    this.customEditorCache.put(requiredType, editor);
                }
            }
        }
        return editor;
    }

    @Nullable
    protected Class<?> guessPropertyTypeFromEditors(String propertyName) {
        if (this.customEditorsForPath != null) {
            CustomEditorHolder editorHolder = this.customEditorsForPath.get(propertyName);
            if (editorHolder == null) {
                ArrayList<String> strippedPaths = new ArrayList<String>();
                this.addStrippedPropertyPaths(strippedPaths, "", propertyName);
                Iterator it = strippedPaths.iterator();
                while (it.hasNext() && editorHolder == null) {
                    String strippedName = (String)it.next();
                    editorHolder = this.customEditorsForPath.get(strippedName);
                }
            }
            if (editorHolder != null) {
                return editorHolder.getRegisteredType();
            }
        }
        return null;
    }

    protected void copyCustomEditorsTo(PropertyEditorRegistry target, @Nullable String nestedProperty) {
        String actualPropertyName;
        String string = actualPropertyName = nestedProperty != null ? PropertyAccessorUtils.getPropertyName(nestedProperty) : null;
        if (this.customEditors != null) {
            this.customEditors.forEach(target::registerCustomEditor);
        }
        if (this.customEditorsForPath != null) {
            this.customEditorsForPath.forEach((editorPath, editorHolder) -> {
                if (nestedProperty != null) {
                    int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(editorPath);
                    if (pos != -1) {
                        String editorNestedProperty = editorPath.substring(0, pos);
                        String editorNestedPath = editorPath.substring(pos + 1);
                        if (editorNestedProperty.equals(nestedProperty) || editorNestedProperty.equals(actualPropertyName)) {
                            target.registerCustomEditor(((CustomEditorHolder)editorHolder).getRegisteredType(), editorNestedPath, ((CustomEditorHolder)editorHolder).getPropertyEditor());
                        }
                    }
                } else {
                    target.registerCustomEditor(((CustomEditorHolder)editorHolder).getRegisteredType(), (String)editorPath, ((CustomEditorHolder)editorHolder).getPropertyEditor());
                }
            });
        }
    }

    private void addStrippedPropertyPaths(List<String> strippedPaths, String nestedPath, String propertyPath) {
        int endIndex;
        int startIndex = propertyPath.indexOf(91);
        if (startIndex != -1 && (endIndex = propertyPath.indexOf(93)) != -1) {
            String prefix = propertyPath.substring(0, startIndex);
            String key = propertyPath.substring(startIndex, endIndex + 1);
            String suffix = propertyPath.substring(endIndex + 1);
            strippedPaths.add(nestedPath + prefix + suffix);
            this.addStrippedPropertyPaths(strippedPaths, nestedPath + prefix, suffix);
            this.addStrippedPropertyPaths(strippedPaths, nestedPath + prefix + key, suffix);
        }
    }

    private static final class CustomEditorHolder {
        private final PropertyEditor propertyEditor;
        @Nullable
        private final Class<?> registeredType;

        private CustomEditorHolder(PropertyEditor propertyEditor, @Nullable Class<?> registeredType) {
            this.propertyEditor = propertyEditor;
            this.registeredType = registeredType;
        }

        private PropertyEditor getPropertyEditor() {
            return this.propertyEditor;
        }

        @Nullable
        private Class<?> getRegisteredType() {
            return this.registeredType;
        }

        @Nullable
        private PropertyEditor getPropertyEditor(@Nullable Class<?> requiredType) {
            if (this.registeredType == null || requiredType != null && (ClassUtils.isAssignable(this.registeredType, requiredType) || ClassUtils.isAssignable(requiredType, this.registeredType)) || requiredType == null && !Collection.class.isAssignableFrom(this.registeredType) && !this.registeredType.isArray()) {
                return this.propertyEditor;
            }
            return null;
        }
    }
}

