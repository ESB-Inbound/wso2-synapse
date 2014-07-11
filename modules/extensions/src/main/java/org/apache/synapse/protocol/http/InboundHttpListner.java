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

/**
 * Start ServerBootStrap in a given port for http inbound connections
 */

public class InboundHttpListner implements InboundListner {
    private static Logger logger = Logger.getLogger(InboundHttpListner.class);
    private int port;
    private SynapseEnvironment synapseEnvironment;
    private String injectSeq;
    private String faultSeq;
    private String outSequence;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Thread listnerThread;
    private Thread responseSender;

    public InboundHttpListner(int port, SynapseEnvironment synapseEnvironment, String injectSeq, String faultSeq,String outSequence) {
        this.port = port;
        this.synapseEnvironment = synapseEnvironment;
        this.injectSeq = injectSeq;
        this.faultSeq = faultSeq;
        this.outSequence=outSequence;
        responseSender = new Thread(new InboundSourceResponseSender());

    }

    public InboundHttpListner() {
    }

    public void start() {
        logger.info("Starting Inbound Http Listner on Port " + this.port);
        listnerThread = new Thread(new Runnable() {
            public void run() {
                bossGroup = new NioEventLoopGroup();
                workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.option(ChannelOption.SO_BACKLOG, InboundHttpConstants.MAXIMUM_CONNECTIONS_QUEUED);
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new InboundHttpTransportHandlerInitializer(synapseEnvironment, injectSeq, faultSeq,outSequence));

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
            listnerThread.start();

        if(responseSender != null){
            logger.info("Starting Inbound response sender");
            responseSender.start();
        }
    }

    public void shutDown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        if(listnerThread != null){
            listnerThread.stop();
        }
        if(responseSender != null){
            responseSender.stop();
        }

    }


    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
