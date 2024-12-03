/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.MessageSource
 *  org.springframework.context.MessageSourceAware
 *  org.springframework.context.support.MessageSourceAccessor
 *  org.springframework.security.crypto.factory.PasswordEncoderFactories
 *  org.springframework.security.crypto.password.PasswordEncoder
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 *  reactor.core.scheduler.Scheduler
 *  reactor.core.scheduler.Schedulers
 */
package org.springframework.security.authentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public abstract class AbstractUserDetailsReactiveAuthenticationManager
implements ReactiveAuthenticationManager,
MessageSourceAware {
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private ReactiveUserDetailsPasswordService userDetailsPasswordService;
    private Scheduler scheduler = Schedulers.boundedElastic();
    private UserDetailsChecker preAuthenticationChecks = this::defaultPreAuthenticationChecks;
    private UserDetailsChecker postAuthenticationChecks = this::defaultPostAuthenticationChecks;

    private void defaultPreAuthenticationChecks(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            this.logger.debug((Object)"User account is locked");
            throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked", "User account is locked"));
        }
        if (!user.isEnabled()) {
            this.logger.debug((Object)"User account is disabled");
            throw new DisabledException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!user.isAccountNonExpired()) {
            this.logger.debug((Object)"User account is expired");
            throw new AccountExpiredException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }

    private void defaultPostAuthenticationChecks(UserDetails user) {
        if (!user.isCredentialsNonExpired()) {
            this.logger.debug((Object)"User account credentials have expired");
            throw new CredentialsExpiredException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
        }
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String presentedPassword = (String)authentication.getCredentials();
        return this.retrieveUser(username).doOnNext(this.preAuthenticationChecks::check).publishOn(this.scheduler).filter(userDetails -> this.passwordEncoder.matches((CharSequence)presentedPassword, userDetails.getPassword())).switchIfEmpty(Mono.defer(() -> Mono.error((Throwable)new BadCredentialsException("Invalid Credentials")))).flatMap(userDetails -> this.upgradeEncodingIfNecessary((UserDetails)userDetails, presentedPassword)).doOnNext(this.postAuthenticationChecks::check).map(this::createUsernamePasswordAuthenticationToken);
    }

    private Mono<UserDetails> upgradeEncodingIfNecessary(UserDetails userDetails, String presentedPassword) {
        boolean upgradeEncoding;
        boolean bl = upgradeEncoding = this.userDetailsPasswordService != null && this.passwordEncoder.upgradeEncoding(userDetails.getPassword());
        if (upgradeEncoding) {
            String newPassword = this.passwordEncoder.encode((CharSequence)presentedPassword);
            return this.userDetailsPasswordService.updatePassword(userDetails, newPassword);
        }
        return Mono.just((Object)userDetails);
    }

    private UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(UserDetails userDetails) {
        return UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        Assert.notNull((Object)passwordEncoder, (String)"passwordEncoder cannot be null");
        this.passwordEncoder = passwordEncoder;
    }

    public void setScheduler(Scheduler scheduler) {
        Assert.notNull((Object)scheduler, (String)"scheduler cannot be null");
        this.scheduler = scheduler;
    }

    public void setUserDetailsPasswordService(ReactiveUserDetailsPasswordService userDetailsPasswordService) {
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
        Assert.notNull((Object)this.postAuthenticationChecks, (String)"postAuthenticationChecks cannot be null");
        this.postAuthenticationChecks = postAuthenticationChecks;
    }

    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull((Object)messageSource, (String)"messageSource cannot be null");
        this.messages = new MessageSourceAccessor(messageSource);
    }

    protected abstract Mono<UserDetails> retrieveUser(String var1);
}

