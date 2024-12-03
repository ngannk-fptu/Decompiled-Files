/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFactory
 *  io.netty.channel.socket.DatagramChannel
 *  io.netty.resolver.AddressResolverGroup
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.ClassLoaderHelper
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.resolver.AddressResolverGroup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.ClassLoaderHelper;

@SdkProtectedApi
public class DnsResolverLoader {
    private DnsResolverLoader() {
    }

    public static AddressResolverGroup<InetSocketAddress> init(ChannelFactory<? extends DatagramChannel> datagramChannelFactory) {
        try {
            Class addressResolver = ClassLoaderHelper.loadClass((String)DnsResolverLoader.getAddressResolverGroup(), (boolean)false, (Class[])new Class[]{null});
            Class dnsNameResolverBuilder = ClassLoaderHelper.loadClass((String)DnsResolverLoader.getDnsNameResolverBuilder(), (boolean)false, (Class[])new Class[]{null});
            Object dnsResolverObj = dnsNameResolverBuilder.newInstance();
            Method method = dnsResolverObj.getClass().getMethod("channelFactory", ChannelFactory.class);
            method.invoke(dnsResolverObj, datagramChannelFactory);
            Object e = addressResolver.getConstructor(dnsNameResolverBuilder).newInstance(dnsResolverObj);
            return (AddressResolverGroup)e;
        }
        catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot find module io.netty.resolver.dns  To use netty non blocking dns, the 'netty-resolver-dns' module from io.netty must be on the class path. ", e);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to create AddressResolverGroup", e);
        }
    }

    private static String getAddressResolverGroup() {
        return "io.netty.resolver.dns.DnsAddressResolverGroup";
    }

    private static String getDnsNameResolverBuilder() {
        return "io.netty.resolver.dns.DnsNameResolverBuilder";
    }
}

