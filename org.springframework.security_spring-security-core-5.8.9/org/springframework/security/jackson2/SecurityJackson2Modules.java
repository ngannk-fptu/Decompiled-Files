/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JacksonAnnotation
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$As
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$Id
 *  com.fasterxml.jackson.databind.DatabindContext
 *  com.fasterxml.jackson.databind.DeserializationConfig
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.Module
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectMapper$DefaultTypeResolverBuilder
 *  com.fasterxml.jackson.databind.ObjectMapper$DefaultTyping
 *  com.fasterxml.jackson.databind.cfg.MapperConfig
 *  com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
 *  com.fasterxml.jackson.databind.jsontype.NamedType
 *  com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
 *  com.fasterxml.jackson.databind.jsontype.TypeIdResolver
 *  com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.core.log.LogMessage
 *  org.springframework.util.ClassUtils
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.log.LogMessage;
import org.springframework.util.ClassUtils;

public final class SecurityJackson2Modules {
    private static final Log logger = LogFactory.getLog(SecurityJackson2Modules.class);
    private static final List<String> securityJackson2ModuleClasses = Arrays.asList("org.springframework.security.jackson2.CoreJackson2Module", "org.springframework.security.cas.jackson2.CasJackson2Module", "org.springframework.security.web.jackson2.WebJackson2Module", "org.springframework.security.web.server.jackson2.WebServerJackson2Module");
    private static final String webServletJackson2ModuleClass = "org.springframework.security.web.jackson2.WebServletJackson2Module";
    private static final String oauth2ClientJackson2ModuleClass = "org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module";
    private static final String javaTimeJackson2ModuleClass = "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule";
    private static final String ldapJackson2ModuleClass = "org.springframework.security.ldap.jackson2.LdapJackson2Module";
    private static final String saml2Jackson2ModuleClass = "org.springframework.security.saml2.jackson2.Saml2Jackson2Module";

    private SecurityJackson2Modules() {
    }

    public static void enableDefaultTyping(ObjectMapper mapper) {
        TypeResolverBuilder typeBuilder;
        if (mapper != null && (typeBuilder = mapper.getDeserializationConfig().getDefaultTyper(null)) == null) {
            mapper.setDefaultTyping(SecurityJackson2Modules.createAllowlistedDefaultTyping());
        }
    }

    private static Module loadAndGetInstance(String className, ClassLoader loader) {
        try {
            Class securityModule = ClassUtils.forName((String)className, (ClassLoader)loader);
            if (securityModule != null) {
                logger.debug((Object)LogMessage.format((String)"Loaded module %s, now registering", (Object)className));
                return (Module)securityModule.newInstance();
            }
        }
        catch (Exception ex) {
            logger.debug((Object)LogMessage.format((String)"Cannot load module %s", (Object)className), (Throwable)ex);
        }
        return null;
    }

    public static List<Module> getModules(ClassLoader loader) {
        ArrayList<Module> modules = new ArrayList<Module>();
        for (String className : securityJackson2ModuleClasses) {
            SecurityJackson2Modules.addToModulesList(loader, modules, className);
        }
        if (ClassUtils.isPresent((String)"javax.servlet.http.Cookie", (ClassLoader)loader)) {
            SecurityJackson2Modules.addToModulesList(loader, modules, webServletJackson2ModuleClass);
        }
        if (ClassUtils.isPresent((String)"org.springframework.security.oauth2.client.OAuth2AuthorizedClient", (ClassLoader)loader)) {
            SecurityJackson2Modules.addToModulesList(loader, modules, oauth2ClientJackson2ModuleClass);
        }
        if (ClassUtils.isPresent((String)javaTimeJackson2ModuleClass, (ClassLoader)loader)) {
            SecurityJackson2Modules.addToModulesList(loader, modules, javaTimeJackson2ModuleClass);
        }
        if (ClassUtils.isPresent((String)ldapJackson2ModuleClass, (ClassLoader)loader)) {
            SecurityJackson2Modules.addToModulesList(loader, modules, ldapJackson2ModuleClass);
        }
        if (ClassUtils.isPresent((String)saml2Jackson2ModuleClass, (ClassLoader)loader)) {
            SecurityJackson2Modules.addToModulesList(loader, modules, saml2Jackson2ModuleClass);
        }
        return modules;
    }

    private static void addToModulesList(ClassLoader loader, List<Module> modules, String className) {
        Module module = SecurityJackson2Modules.loadAndGetInstance(className, loader);
        if (module != null) {
            modules.add(module);
        }
    }

    private static TypeResolverBuilder<? extends TypeResolverBuilder> createAllowlistedDefaultTyping() {
        AllowlistTypeResolverBuilder result = new AllowlistTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL);
        result = result.init(JsonTypeInfo.Id.CLASS, null);
        result = result.inclusion(JsonTypeInfo.As.PROPERTY);
        return result;
    }

    static class AllowlistTypeIdResolver
    implements TypeIdResolver {
        private static final Set<String> ALLOWLIST_CLASS_NAMES;
        private final TypeIdResolver delegate;

        AllowlistTypeIdResolver(TypeIdResolver delegate) {
            this.delegate = delegate;
        }

        public void init(JavaType baseType) {
            this.delegate.init(baseType);
        }

        public String idFromValue(Object value) {
            return this.delegate.idFromValue(value);
        }

        public String idFromValueAndType(Object value, Class<?> suggestedType) {
            return this.delegate.idFromValueAndType(value, suggestedType);
        }

        public String idFromBaseType() {
            return this.delegate.idFromBaseType();
        }

        public JavaType typeFromId(DatabindContext context, String id) throws IOException {
            boolean isExplicitMixin;
            DeserializationConfig config = (DeserializationConfig)context.getConfig();
            JavaType result = this.delegate.typeFromId(context, id);
            String className = result.getRawClass().getName();
            if (this.isInAllowlist(className)) {
                return result;
            }
            boolean bl = isExplicitMixin = config.findMixInClassFor(result.getRawClass()) != null;
            if (isExplicitMixin) {
                return result;
            }
            JacksonAnnotation jacksonAnnotation = (JacksonAnnotation)AnnotationUtils.findAnnotation((Class)result.getRawClass(), JacksonAnnotation.class);
            if (jacksonAnnotation != null) {
                return result;
            }
            throw new IllegalArgumentException("The class with " + id + " and name of " + className + " is not in the allowlist. If you believe this class is safe to deserialize, please provide an explicit mapping using Jackson annotations or by providing a Mixin. If the serialization is only done by a trusted source, you can also enable default typing. See https://github.com/spring-projects/spring-security/issues/4370 for details");
        }

        private boolean isInAllowlist(String id) {
            return ALLOWLIST_CLASS_NAMES.contains(id);
        }

        public String getDescForKnownTypeIds() {
            return this.delegate.getDescForKnownTypeIds();
        }

        public JsonTypeInfo.Id getMechanism() {
            return this.delegate.getMechanism();
        }

        static {
            HashSet<String> names = new HashSet<String>();
            names.add("java.util.ArrayList");
            names.add("java.util.Collections$EmptyList");
            names.add("java.util.Collections$EmptyMap");
            names.add("java.util.Collections$UnmodifiableRandomAccessList");
            names.add("java.util.Collections$SingletonList");
            names.add("java.util.Date");
            names.add("java.time.Instant");
            names.add("java.net.URL");
            names.add("java.util.TreeMap");
            names.add("java.util.HashMap");
            names.add("java.util.LinkedHashMap");
            names.add("org.springframework.security.core.context.SecurityContextImpl");
            names.add("java.util.Arrays$ArrayList");
            ALLOWLIST_CLASS_NAMES = Collections.unmodifiableSet(names);
        }
    }

    static class AllowlistTypeResolverBuilder
    extends ObjectMapper.DefaultTypeResolverBuilder {
        AllowlistTypeResolverBuilder(ObjectMapper.DefaultTyping defaultTyping) {
            super(defaultTyping, (PolymorphicTypeValidator)BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build());
        }

        protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, PolymorphicTypeValidator subtypeValidator, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
            TypeIdResolver result = super.idResolver(config, baseType, subtypeValidator, subtypes, forSer, forDeser);
            return new AllowlistTypeIdResolver(result);
        }
    }
}

