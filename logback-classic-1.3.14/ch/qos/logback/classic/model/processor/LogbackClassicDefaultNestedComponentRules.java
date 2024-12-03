/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.AppenderBase
 *  ch.qos.logback.core.UnsynchronizedAppenderBase
 *  ch.qos.logback.core.filter.EvaluatorFilter
 *  ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry
 *  ch.qos.logback.core.joran.util.ParentTag_Tag_Class_Tuple
 *  ch.qos.logback.core.net.ssl.KeyManagerFactoryFactoryBean
 *  ch.qos.logback.core.net.ssl.KeyStoreFactoryBean
 *  ch.qos.logback.core.net.ssl.SSLConfiguration
 *  ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules
 *  ch.qos.logback.core.net.ssl.SSLParametersConfiguration
 *  ch.qos.logback.core.net.ssl.SecureRandomFactoryBean
 *  ch.qos.logback.core.net.ssl.TrustManagerFactoryFactoryBean
 */
package ch.qos.logback.classic.model.processor;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.joran.spi.DefaultNestedComponentRegistry;
import ch.qos.logback.core.joran.util.ParentTag_Tag_Class_Tuple;
import ch.qos.logback.core.net.ssl.KeyManagerFactoryFactoryBean;
import ch.qos.logback.core.net.ssl.KeyStoreFactoryBean;
import ch.qos.logback.core.net.ssl.SSLConfiguration;
import ch.qos.logback.core.net.ssl.SSLNestedComponentRegistryRules;
import ch.qos.logback.core.net.ssl.SSLParametersConfiguration;
import ch.qos.logback.core.net.ssl.SecureRandomFactoryBean;
import ch.qos.logback.core.net.ssl.TrustManagerFactoryFactoryBean;
import java.util.ArrayList;
import java.util.List;

public class LogbackClassicDefaultNestedComponentRules {
    public static List<ParentTag_Tag_Class_Tuple> TUPLES_LIST = LogbackClassicDefaultNestedComponentRules.createTuplesList();

    public static void addDefaultNestedComponentRegistryRules(DefaultNestedComponentRegistry registry) {
        registry.add(AppenderBase.class, "layout", PatternLayout.class);
        registry.add(UnsynchronizedAppenderBase.class, "layout", PatternLayout.class);
        registry.add(AppenderBase.class, "encoder", PatternLayoutEncoder.class);
        registry.add(UnsynchronizedAppenderBase.class, "encoder", PatternLayoutEncoder.class);
        registry.add(EvaluatorFilter.class, "evaluator", JaninoEventEvaluator.class);
        SSLNestedComponentRegistryRules.addDefaultNestedComponentRegistryRules((DefaultNestedComponentRegistry)registry);
    }

    public static List<ParentTag_Tag_Class_Tuple> createTuplesList() {
        ArrayList<ParentTag_Tag_Class_Tuple> tupleList = new ArrayList<ParentTag_Tag_Class_Tuple>();
        tupleList.add(new ParentTag_Tag_Class_Tuple("appender", "encoder", PatternLayoutEncoder.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("appender", "layout", PatternLayout.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("receiver", "ssl", SSLConfiguration.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "parameters", SSLParametersConfiguration.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "keyStore", KeyStoreFactoryBean.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "trustStore", KeyManagerFactoryFactoryBean.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "keyManagerFactory", SSLParametersConfiguration.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "trustManagerFactory", TrustManagerFactoryFactoryBean.class.getName()));
        tupleList.add(new ParentTag_Tag_Class_Tuple("ssl", "secureRandom", SecureRandomFactoryBean.class.getName()));
        return tupleList;
    }
}

