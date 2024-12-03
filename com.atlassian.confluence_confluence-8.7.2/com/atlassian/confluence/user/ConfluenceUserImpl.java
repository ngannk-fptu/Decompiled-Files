/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.hibernate.extras.ExportableField
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.impl.DefaultUser
 *  io.atlassian.util.concurrent.Lazy
 *  io.atlassian.util.concurrent.Suppliers
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.hibernate.type.StringType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.DeletedUser;
import com.atlassian.confluence.user.HasBackingUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.hibernate.extras.ExportableField;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultUser;
import io.atlassian.util.concurrent.Lazy;
import io.atlassian.util.concurrent.Suppliers;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceUserImpl
implements ConfluenceUser,
Cloneable,
Serializable,
HasBackingUser {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceUserImpl.class);
    private static final long serialVersionUID = -7128183776011419820L;
    private UserKey key;
    private String name;
    private String lowerName;
    @ExportableField(type=StringType.class)
    private transient String email;
    private transient Supplier<User> backingUserSupplier;

    public ConfluenceUserImpl() {
    }

    public ConfluenceUserImpl(String username, String fullName, String email) {
        this((User)new DefaultUser(username, fullName, email));
    }

    public ConfluenceUserImpl(User backingUser) {
        if (backingUser == null) {
            throw new IllegalArgumentException("The backingUser is a required parameters.");
        }
        this.name = backingUser.getName();
        this.lowerName = IdentifierUtils.toLowerCase((String)this.name);
        this.backingUserSupplier = Suppliers.memoize((Object)backingUser);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public @Nullable String getLowerName() {
        return this.lowerName;
    }

    @Override
    public UserKey getKey() {
        return this.key;
    }

    public String getFullName() {
        return this.getBackingUser().getFullName();
    }

    public String getEmail() {
        if (this.email == null) {
            this.email = this.getBackingUser().getEmail();
        }
        return this.email;
    }

    @Override
    public User getBackingUser() {
        return this.backingUserSupplier.get();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLowerName(@Nullable String lowerName) {
        this.lowerName = lowerName;
        this.resetBackingUserSupplier();
    }

    private void resetBackingUserSupplier() {
        this.backingUserSupplier = Lazy.supplier(() -> {
            if (this.name != null && this.key != null && this.name.equals(this.key.getStringValue())) {
                return new DeletedUser(this.key, GeneralUtil.getI18n());
            }
            if (this.lowerName != null) {
                return ConfluenceUserImpl.lookupBackingUser(this.lowerName, this.key);
            }
            return UnknownUser.unknownUser(this.name, GeneralUtil.getI18n());
        });
    }

    private static User lookupBackingUser(String lowerName, UserKey key) {
        try {
            User backingUser = ConfluenceUserImpl.backingUserManager().getUser(lowerName);
            if (backingUser != null) {
                return backingUser;
            }
            log.debug("No backing User was found with the username {} and the user key {}", (Object)lowerName, (Object)key);
        }
        catch (EntityException ex) {
            log.warn("Could not load the backing user for the user with key={}, username={}", (Object)key, (Object)lowerName);
        }
        return UnknownUser.unknownUser(lowerName, GeneralUtil.getI18n());
    }

    private static UserManager backingUserManager() {
        return (UserManager)ContainerManager.getComponent((String)"backingUserManager");
    }

    private void setKey(UserKey key) {
        this.key = key;
    }

    public void setBackingUser(User backingUser) {
        this.name = backingUser.getName();
        this.email = backingUser.getEmail();
        this.backingUserSupplier = Suppliers.memoize((Object)backingUser);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfluenceUserImpl)) {
            return false;
        }
        ConfluenceUserImpl that = (ConfluenceUserImpl)o;
        return Objects.equals(this.getKey(), that.getKey()) && Objects.equals(this.getLowerName(), that.getLowerName());
    }

    public int hashCode() {
        return Objects.hash(this.getKey(), this.getLowerName());
    }

    public String toString() {
        return "ConfluenceUserImpl{name='" + this.getName() + "', key=" + this.getKey() + "}";
    }

    public ConfluenceUserImpl clone() throws CloneNotSupportedException {
        ConfluenceUserImpl clone = (ConfluenceUserImpl)super.clone();
        clone.backingUserSupplier = this.backingUserSupplier;
        clone.lowerName = this.lowerName;
        clone.name = this.name;
        clone.key = this.key;
        return clone;
    }

    private Object readResolve() throws ObjectStreamException {
        this.resetBackingUserSupplier();
        return this;
    }
}

