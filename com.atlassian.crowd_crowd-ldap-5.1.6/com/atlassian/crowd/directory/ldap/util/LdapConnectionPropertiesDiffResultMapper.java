/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.diff.NormalizingDiffBuilder
 *  com.atlassian.crowd.common.util.MaskingUtil
 *  com.atlassian.crowd.common.util.ThrowingFunction
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.builder.DiffResult
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.util;

import com.atlassian.crowd.common.diff.NormalizingDiffBuilder;
import com.atlassian.crowd.common.util.MaskingUtil;
import com.atlassian.crowd.common.util.ThrowingFunction;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.connectionpool.SpringLdapPoolConfigService;
import com.atlassian.crowd.directory.ldap.connectionpool.data.LdapPoolConfig;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapConnectionPropertiesDiffResultMapper {
    private static final Logger logger = LoggerFactory.getLogger(LdapConnectionPropertiesDiffResultMapper.class);
    private final SpringLdapPoolConfigService springLdapPoolConfigService;

    public LdapConnectionPropertiesDiffResultMapper(SpringLdapPoolConfigService springLdapPoolConfigService) {
        this.springLdapPoolConfigService = springLdapPoolConfigService;
    }

    public DiffResult<LDAPPropertiesMapper> getConnectionPropertiesDifference(LDAPPropertiesMapper left, LDAPPropertiesMapper right) {
        return new LDAPConnectionPropertiesDiffBuilder(left, right).append("connectionURL", left.getConnectionURL(), right.getConnectionURL()).append("username", left.getUsername(), right.getUsername()).appendPassword(left.getPassword(), right.getPassword()).append("secureMode", (Object)left.getSecureMode(), (Object)right.getSecureMode()).appendLdapPoolConfig(left.getLdapPoolConfig(), right.getLdapPoolConfig(), (ThrowingFunction<String, LdapPoolConfig>)((ThrowingFunction)this.springLdapPoolConfigService::toLdapPoolConfigDto)).build();
    }

    private static class LDAPConnectionPropertiesDiffBuilder
    extends NormalizingDiffBuilder<LDAPPropertiesMapper> {
        private static final Set<String> SANITIZED_PROPERTIES = ImmutableSet.of((Object)"password");

        public LDAPConnectionPropertiesDiffBuilder(LDAPPropertiesMapper left, LDAPPropertiesMapper right) {
            super((Object)left, (Object)right, SanitizingToStringStyle.STYLE);
        }

        public LDAPConnectionPropertiesDiffBuilder appendLdapPoolConfig(@Nullable String left, @Nullable String right, ThrowingFunction<String, LdapPoolConfig> mapper) {
            if (Objects.equals(left, right)) {
                this.append("ldapPoolConfig", left, right);
            } else {
                try {
                    this.appendDiff("ldapPoolConfig", ((LdapPoolConfig)mapper.apply((Object)left)).diff((LdapPoolConfig)mapper.apply((Object)right)));
                }
                catch (Exception anyException) {
                    logger.trace("Deserializing LdapPoolConfig failed", (Throwable)anyException);
                    this.append("ldapPoolConfig", left, right);
                }
            }
            return this;
        }

        public LDAPConnectionPropertiesDiffBuilder appendPassword(String leftValue, String rightValue) {
            this.append("password", leftValue, rightValue);
            return this;
        }

        public LDAPConnectionPropertiesDiffBuilder append(String fieldName, Object lhs, Object rhs) {
            super.append(fieldName, lhs, rhs);
            return this;
        }

        public LDAPConnectionPropertiesDiffBuilder append(String fieldName, String lhs, String rhs) {
            super.append(fieldName, lhs, rhs);
            return this;
        }

        private static class SanitizingToStringStyle
        extends ToStringStyle {
            private static final ToStringStyle STYLE = new SanitizingToStringStyle();
            private static final long serialVersionUID = 1L;

            protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
                if (SANITIZED_PROPERTIES.contains(fieldName)) {
                    value = MaskingUtil.sanitize((Object)value);
                }
                buffer.append(value);
            }

            private SanitizingToStringStyle() {
                this.setUseClassName(false);
                this.setUseIdentityHashCode(false);
                this.setNullText("null");
                this.setFieldSeparator(", ");
            }

            private Object readResolve() {
                return STYLE;
            }
        }
    }
}

