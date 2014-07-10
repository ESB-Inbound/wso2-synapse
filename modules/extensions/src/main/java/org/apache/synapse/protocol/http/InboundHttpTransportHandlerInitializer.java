package org.apache.synapse.protocol.http;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.log4j.Logger;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.protocol.http.utils.InboundHttpConstants;

/**
 * register event handlers in the pipeline
 */
public class InboundHttpTransportHandlerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = Logger.getLogger(InboundHttpTransportHandlerInitializer.class);
    private SynapseEnvironment synapseEnvironment;
    private String injectSeq;
    private String faultSeq;

    public InboundHttpTransportHandlerInitializer(SynapseEnvironment synapseEnvironment, String injectSeq, String faultSeq) {
        this.synapseEnvironment = synapseEnvironment;
        this.injectSeq = injectSeq;
        this.faultSeq = faultSeq;
    }

    /**
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.info("initializing channel pipeline");
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(InboundHttpConstants.MAXIMUM_CHUNK_SIZE_AGGREGATOR));
        p.addLast("handler", new InboundHttpTransportSourceHandler(synapseEnvironment, injectSeq, faultSeq));
    }
}
