/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  javax.annotation.Resource$AuthenticationType
 *  javax.annotation.Resources
 *  javax.annotation.security.DeclareRoles
 *  javax.annotation.security.RunAs
 *  javax.servlet.ServletSecurityElement
 *  javax.servlet.annotation.ServletSecurity
 *  org.apache.tomcat.util.descriptor.web.ContextEnvironment
 *  org.apache.tomcat.util.descriptor.web.ContextResource
 *  org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef
 *  org.apache.tomcat.util.descriptor.web.ContextService
 *  org.apache.tomcat.util.descriptor.web.FilterDef
 *  org.apache.tomcat.util.descriptor.web.MessageDestinationRef
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RunAs;
import javax.servlet.ServletSecurityElement;
import javax.servlet.annotation.ServletSecurity;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.util.Introspection;
import org.apache.tomcat.util.descriptor.web.ContextEnvironment;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.apache.tomcat.util.descriptor.web.ContextResourceEnvRef;
import org.apache.tomcat.util.descriptor.web.ContextService;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.MessageDestinationRef;
import org.apache.tomcat.util.res.StringManager;

public class WebAnnotationSet {
    private static final String SEPARATOR = "/";
    private static final String MAPPED_NAME_PROPERTY = "mappedName";
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");

    public static void loadApplicationAnnotations(Context context) {
        WebAnnotationSet.loadApplicationListenerAnnotations(context);
        WebAnnotationSet.loadApplicationFilterAnnotations(context);
        WebAnnotationSet.loadApplicationServletAnnotations(context);
    }

    protected static void loadApplicationListenerAnnotations(Context context) {
        String[] applicationListeners;
        for (String className : applicationListeners = context.findApplicationListeners()) {
            Class<?> clazz = Introspection.loadClass(context, className);
            if (clazz == null) continue;
            WebAnnotationSet.loadClassAnnotation(context, clazz);
            WebAnnotationSet.loadFieldsAnnotation(context, clazz);
            WebAnnotationSet.loadMethodsAnnotation(context, clazz);
        }
    }

    protected static void loadApplicationFilterAnnotations(Context context) {
        FilterDef[] filterDefs;
        for (FilterDef filterDef : filterDefs = context.findFilterDefs()) {
            Class<?> clazz = Introspection.loadClass(context, filterDef.getFilterClass());
            if (clazz == null) continue;
            WebAnnotationSet.loadClassAnnotation(context, clazz);
            WebAnnotationSet.loadFieldsAnnotation(context, clazz);
            WebAnnotationSet.loadMethodsAnnotation(context, clazz);
        }
    }

    protected static void loadApplicationServletAnnotations(Context context) {
        Container[] children;
        for (Container child : children = context.findChildren()) {
            ServletSecurity servletSecurity;
            Class<?> clazz;
            Wrapper wrapper;
            if (!(child instanceof Wrapper) || (wrapper = (Wrapper)child).getServletClass() == null || (clazz = Introspection.loadClass(context, wrapper.getServletClass())) == null) continue;
            WebAnnotationSet.loadClassAnnotation(context, clazz);
            WebAnnotationSet.loadFieldsAnnotation(context, clazz);
            WebAnnotationSet.loadMethodsAnnotation(context, clazz);
            RunAs runAs = clazz.getAnnotation(RunAs.class);
            if (runAs != null) {
                wrapper.setRunAs(runAs.value());
            }
            if ((servletSecurity = clazz.getAnnotation(ServletSecurity.class)) == null) continue;
            context.addServletSecurity(new ApplicationServletRegistration(wrapper, context), new ServletSecurityElement(servletSecurity));
        }
    }

    protected static void loadClassAnnotation(Context context, Class<?> clazz) {
        DeclareRoles declareRolesAnnotation;
        Resources resourcesAnnotation;
        Resource resourceAnnotation = clazz.getAnnotation(Resource.class);
        if (resourceAnnotation != null) {
            WebAnnotationSet.addResource(context, resourceAnnotation);
        }
        if ((resourcesAnnotation = clazz.getAnnotation(Resources.class)) != null && resourcesAnnotation.value() != null) {
            for (Resource resource : resourcesAnnotation.value()) {
                WebAnnotationSet.addResource(context, resource);
            }
        }
        if ((declareRolesAnnotation = clazz.getAnnotation(DeclareRoles.class)) != null && declareRolesAnnotation.value() != null) {
            for (String role : declareRolesAnnotation.value()) {
                context.addSecurityRole(role);
            }
        }
    }

    protected static void loadFieldsAnnotation(Context context, Class<?> clazz) {
        Field[] fields = Introspection.getDeclaredFields(clazz);
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Resource annotation = field.getAnnotation(Resource.class);
                if (annotation == null) continue;
                String defaultName = clazz.getName() + SEPARATOR + field.getName();
                Class<?> defaultType = field.getType();
                WebAnnotationSet.addResource(context, annotation, defaultName, defaultType);
            }
        }
    }

    protected static void loadMethodsAnnotation(Context context, Class<?> clazz) {
        Method[] methods = Introspection.getDeclaredMethods(clazz);
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                Resource annotation = method.getAnnotation(Resource.class);
                if (annotation == null) continue;
                if (!Introspection.isValidSetter(method)) {
                    throw new IllegalArgumentException(sm.getString("webAnnotationSet.invalidInjection"));
                }
                String defaultName = clazz.getName() + SEPARATOR + Introspection.getPropertyName(method);
                Class<?> defaultType = method.getParameterTypes()[0];
                WebAnnotationSet.addResource(context, annotation, defaultName, defaultType);
            }
        }
    }

    protected static void addResource(Context context, Resource annotation) {
        WebAnnotationSet.addResource(context, annotation, null, null);
    }

    protected static void addResource(Context context, Resource annotation, String defaultName, Class<?> defaultType) {
        String name = WebAnnotationSet.getName(annotation, defaultName);
        String type = WebAnnotationSet.getType(annotation, defaultType);
        if (type.equals("java.lang.String") || type.equals("java.lang.Character") || type.equals("java.lang.Integer") || type.equals("java.lang.Boolean") || type.equals("java.lang.Double") || type.equals("java.lang.Byte") || type.equals("java.lang.Short") || type.equals("java.lang.Long") || type.equals("java.lang.Float")) {
            ContextEnvironment resource = new ContextEnvironment();
            resource.setName(name);
            resource.setType(type);
            resource.setDescription(annotation.description());
            resource.setProperty(MAPPED_NAME_PROPERTY, (Object)annotation.mappedName());
            resource.setLookupName(annotation.lookup());
            context.getNamingResources().addEnvironment(resource);
        } else if (type.equals("javax.xml.rpc.Service")) {
            ContextService service = new ContextService();
            service.setName(name);
            service.setWsdlfile(annotation.mappedName());
            service.setType(type);
            service.setDescription(annotation.description());
            service.setLookupName(annotation.lookup());
            context.getNamingResources().addService(service);
        } else if (type.equals("javax.sql.DataSource") || type.equals("javax.jms.ConnectionFactory") || type.equals("javax.jms.QueueConnectionFactory") || type.equals("javax.jms.TopicConnectionFactory") || type.equals("javax.mail.Session") || type.equals("java.net.URL") || type.equals("javax.resource.cci.ConnectionFactory") || type.equals("org.omg.CORBA_2_3.ORB") || type.endsWith("ConnectionFactory")) {
            ContextResource resource = new ContextResource();
            resource.setName(name);
            resource.setType(type);
            if (annotation.authenticationType() == Resource.AuthenticationType.CONTAINER) {
                resource.setAuth("Container");
            } else if (annotation.authenticationType() == Resource.AuthenticationType.APPLICATION) {
                resource.setAuth("Application");
            }
            resource.setScope(annotation.shareable() ? "Shareable" : "Unshareable");
            resource.setProperty(MAPPED_NAME_PROPERTY, (Object)annotation.mappedName());
            resource.setDescription(annotation.description());
            resource.setLookupName(annotation.lookup());
            context.getNamingResources().addResource(resource);
        } else if (type.equals("javax.jms.Queue") || type.equals("javax.jms.Topic")) {
            MessageDestinationRef resource = new MessageDestinationRef();
            resource.setName(name);
            resource.setType(type);
            resource.setUsage(annotation.mappedName());
            resource.setDescription(annotation.description());
            resource.setLookupName(annotation.lookup());
            context.getNamingResources().addMessageDestinationRef(resource);
        } else {
            ContextResourceEnvRef resource = new ContextResourceEnvRef();
            resource.setName(name);
            resource.setType(type);
            resource.setProperty(MAPPED_NAME_PROPERTY, (Object)annotation.mappedName());
            resource.setDescription(annotation.description());
            resource.setLookupName(annotation.lookup());
            context.getNamingResources().addResourceEnvRef(resource);
        }
    }

    private static String getType(Resource annotation, Class<?> defaultType) {
        Class<Object> type = annotation.type();
        if (type == null || type.equals(Object.class)) {
            type = defaultType != null ? defaultType : Object.class;
        }
        return Introspection.convertPrimitiveType(type).getCanonicalName();
    }

    private static String getName(Resource annotation, String defaultName) {
        String name = annotation.name();
        if ((name == null || name.equals("")) && defaultName != null) {
            name = defaultName;
        }
        return name;
    }
}

