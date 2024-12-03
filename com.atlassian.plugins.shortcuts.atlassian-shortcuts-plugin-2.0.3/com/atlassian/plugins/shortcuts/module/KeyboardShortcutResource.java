/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.json.DefaultJaxbJsonMarshaller
 *  com.atlassian.plugins.rest.common.json.JaxbJsonMarshaller
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.plugins.shortcuts.module;

import com.atlassian.plugins.rest.common.json.DefaultJaxbJsonMarshaller;
import com.atlassian.plugins.rest.common.json.JaxbJsonMarshaller;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcut;
import com.atlassian.plugins.shortcuts.api.KeyboardShortcutManager;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path(value="/")
@AnonymousSiteAccess
public class KeyboardShortcutResource {
    private static final CacheControl NO_CACHE = new CacheControl();
    private static final CacheControl CACHE_FOREVER = new CacheControl();
    private final JaxbJsonMarshaller jaxbJsonMarshaller;
    private final KeyboardShortcutManager keyboardShortcutManager;
    private final I18nResolver i18nResolver;

    public KeyboardShortcutResource(KeyboardShortcutManager keyboardShortcutManager, I18nResolver i18nResolver) {
        this.keyboardShortcutManager = keyboardShortcutManager;
        this.jaxbJsonMarshaller = new DefaultJaxbJsonMarshaller();
        this.i18nResolver = i18nResolver;
    }

    @GET
    @Path(value="shortcuts/{buildnumber}/{hashcode}")
    @Produces(value={"application/json"})
    public Response getShortCuts() {
        List<KeyboardShortcut> shortcuts = this.keyboardShortcutManager.getAllShortcuts();
        try {
            String jsonString = this.jaxbJsonMarshaller.marshal((Object)new Shortcuts(shortcuts, this.i18nResolver), new Class[]{Shortcuts.class, Shortcut.class});
            if ("null".equals(jsonString)) {
                jsonString = "{}";
            }
            return Response.ok((Object)jsonString).cacheControl(CACHE_FOREVER).build();
        }
        catch (JAXBException e) {
            return Response.noContent().cacheControl(NO_CACHE).build();
        }
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
        CACHE_FOREVER.setPrivate(false);
        CACHE_FOREVER.setMaxAge(Integer.MAX_VALUE);
    }

    @XmlRootElement
    public static class Shortcut {
        @XmlElement
        private Set<List<String>> keys;
        @XmlElement
        private String context;
        @XmlElement
        private String op;
        @XmlElement
        private String param;
        @XmlElement
        private String descKey;
        @XmlElement
        private String description;
        @XmlElement
        private Boolean hidden;

        private Shortcut() {
        }

        public Shortcut(KeyboardShortcut shortcut, I18nResolver i18nResolver) {
            this.keys = new LinkedHashSet<List<String>>(shortcut.getShortcuts());
            this.context = shortcut.getContext();
            this.op = shortcut.getOperation().getType().name();
            this.param = shortcut.getParameter();
            this.descKey = shortcut.getDescriptionI18nKey();
            String description = i18nResolver.getText(this.descKey);
            if (description == null || description.equals(this.descKey)) {
                description = shortcut.getDefaultDescription();
            }
            this.description = description;
            this.hidden = shortcut.isHidden();
        }
    }

    @XmlRootElement
    public static class Shortcuts {
        @XmlElement
        final List<Shortcut> shortcuts = new ArrayList<Shortcut>();

        private Shortcuts() {
        }

        public Shortcuts(List<KeyboardShortcut> origShortcuts, I18nResolver i18nResolver) {
            for (KeyboardShortcut origShortcut : origShortcuts) {
                this.shortcuts.add(new Shortcut(origShortcut, i18nResolver));
            }
        }
    }
}

