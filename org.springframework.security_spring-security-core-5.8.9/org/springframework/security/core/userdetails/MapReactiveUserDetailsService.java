/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.security.core.userdetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class MapReactiveUserDetailsService
implements ReactiveUserDetailsService,
ReactiveUserDetailsPasswordService {
    private final Map<String, UserDetails> users;

    public MapReactiveUserDetailsService(Map<String, UserDetails> users) {
        this.users = users;
    }

    public MapReactiveUserDetailsService(UserDetails ... users) {
        this(Arrays.asList(users));
    }

    public MapReactiveUserDetailsService(Collection<UserDetails> users) {
        Assert.notEmpty(users, (String)"users cannot be null or empty");
        this.users = new ConcurrentHashMap<String, UserDetails>();
        for (UserDetails user : users) {
            this.users.put(this.getKey(user.getUsername()), user);
        }
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        String key = this.getKey(username);
        UserDetails result = this.users.get(key);
        return result != null ? Mono.just((Object)User.withUserDetails(result).build()) : Mono.empty();
    }

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return Mono.just((Object)user).map(userDetails -> this.withNewPassword((UserDetails)userDetails, newPassword)).doOnNext(userDetails -> {
            String key = this.getKey(user.getUsername());
            this.users.put(key, (UserDetails)userDetails);
        });
    }

    private UserDetails withNewPassword(UserDetails userDetails, String newPassword) {
        return User.withUserDetails(userDetails).password(newPassword).build();
    }

    private String getKey(String username) {
        return username.toLowerCase();
    }
}

