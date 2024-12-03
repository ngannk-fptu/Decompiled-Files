/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.Version
 *  com.fasterxml.jackson.databind.Module$SetupContext
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.module.SimpleModule
 */
package org.springframework.security.jackson2;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Collections;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jackson2.AnonymousAuthenticationTokenMixin;
import org.springframework.security.jackson2.BadCredentialsExceptionMixin;
import org.springframework.security.jackson2.RememberMeAuthenticationTokenMixin;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.jackson2.SimpleGrantedAuthorityMixin;
import org.springframework.security.jackson2.UnmodifiableListMixin;
import org.springframework.security.jackson2.UnmodifiableMapMixin;
import org.springframework.security.jackson2.UnmodifiableSetMixin;
import org.springframework.security.jackson2.UserMixin;
import org.springframework.security.jackson2.UsernamePasswordAuthenticationTokenMixin;

public class CoreJackson2Module
extends SimpleModule {
    public CoreJackson2Module() {
        super(CoreJackson2Module.class.getName(), new Version(1, 0, 0, null, null, null));
    }

    public void setupModule(Module.SetupContext context) {
        SecurityJackson2Modules.enableDefaultTyping((ObjectMapper)context.getOwner());
        context.setMixInAnnotations(AnonymousAuthenticationToken.class, AnonymousAuthenticationTokenMixin.class);
        context.setMixInAnnotations(RememberMeAuthenticationToken.class, RememberMeAuthenticationTokenMixin.class);
        context.setMixInAnnotations(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class);
        context.setMixInAnnotations(Collections.unmodifiableSet(Collections.emptySet()).getClass(), UnmodifiableSetMixin.class);
        context.setMixInAnnotations(Collections.unmodifiableList(Collections.emptyList()).getClass(), UnmodifiableListMixin.class);
        context.setMixInAnnotations(Collections.unmodifiableMap(Collections.emptyMap()).getClass(), UnmodifiableMapMixin.class);
        context.setMixInAnnotations(User.class, UserMixin.class);
        context.setMixInAnnotations(UsernamePasswordAuthenticationToken.class, UsernamePasswordAuthenticationTokenMixin.class);
        context.setMixInAnnotations(BadCredentialsException.class, BadCredentialsExceptionMixin.class);
    }
}

