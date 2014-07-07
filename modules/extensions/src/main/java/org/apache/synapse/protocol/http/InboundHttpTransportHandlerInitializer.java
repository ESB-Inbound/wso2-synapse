package org.apache.synapse.protocol.http;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.log4j.Logger;
import org.apache.synapse.protocol.http.utils.InboundHttpConstants;


public class InboundHttpTransportHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = Logger.getLogger(InboundHttpTransportHandlerInitializer.class);



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.info("initializing channel pipeline");
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(InboundHttpConstants.MAXIMUM_CHUNK_SIZE_AGGREGATOR));
        p.addLast("handler", new InboundHttpTransportSourceHandler());
    }
}
