package org.apache.synapse.protocol.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.apache.synapse.core.SynapseEnvironment;

import org.apache.synapse.inbound.InboundListner;
import org.apache.synapse.protocol.http.utils.InboundHttpConstants;


public class InboundHttpListner implements InboundListner {
    private static Logger logger = Logger.getLogger(InboundHttpListner.class);
    private int port;
    private SynapseEnvironment synapseEnvironment;
    private String injectSeq;
    private String faultSeq;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public InboundHttpListner(int port, SynapseEnvironment synapseEnvironment, String injectSeq, String faultSeq) {
        this.port = port;
        this.synapseEnvironment = synapseEnvironment;
        this.injectSeq = injectSeq;
        this.faultSeq = faultSeq;

//        InboundSourceResponseSender responseSender = new InboundSourceResponseSender();
//        responseSender.run();
    }

    public InboundHttpListner() {
    }

    public void start() {
        logger.info("Starting Inbound Http Listner on Port " + this.port);
        Thread t = new Thread(new Runnable() {
            public void run() {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.option(ChannelOption.SO_BACKLOG, InboundHttpConstants.MAXIMUM_CONNECTIONS_QUEUED);
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new InboundHttpTransportHandlerInitializer(synapseEnvironment, injectSeq, faultSeq));

                    Channel ch = null;
                    try {
                        ch = b.bind(port).sync().channel();
                        ch.closeFuture().sync();
                        logger.info("Inbound Listner Started");
                    } catch (InterruptedException e) {
                        logger.info("");
                    }
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }},
            "Inbound Listner");
            t.start();
    }

    public void shutDown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
