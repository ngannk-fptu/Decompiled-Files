/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.core.support;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class JdbcBeanDefinitionReader {
    private final PropertiesBeanDefinitionReader propReader;
    @Nullable
    private JdbcTemplate jdbcTemplate;

    public JdbcBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
        this.propReader = new PropertiesBeanDefinitionReader(beanFactory);
    }

    public JdbcBeanDefinitionReader(PropertiesBeanDefinitionReader reader) {
        Assert.notNull((Object)reader, (String)"Bean definition reader must not be null");
        this.propReader = reader;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        Assert.notNull((Object)jdbcTemplate, (String)"JdbcTemplate must not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    public void loadBeanDefinitions(String sql) {
        Assert.notNull((Object)this.jdbcTemplate, (String)"Not fully configured - specify DataSource or JdbcTemplate");
        Properties props = new Properties();
        this.jdbcTemplate.query(sql, rs -> {
            String beanName = rs.getString(1);
            String property = rs.getString(2);
            String value = rs.getString(3);
            props.setProperty(beanName + '.' + property, value);
        });
        this.propReader.registerBeanDefinitions((Map)props);
    }
}

