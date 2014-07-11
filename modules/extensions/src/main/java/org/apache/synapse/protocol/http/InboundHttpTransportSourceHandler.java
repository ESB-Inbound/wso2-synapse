package org.apache.synapse.protocol.http;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;
import org.apache.synapse.core.SynapseEnvironment;

/**
 * actuall event handling class for netty
 */
public class InboundHttpTransportSourceHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = Logger.getLogger(InboundHttpTransportSourceHandler.class);
    private SynapseEnvironment synapseEnvironment;
    private String injectSeq;
    private String faultSeq;
    private String outSequence;

    public InboundHttpTransportSourceHandler(SynapseEnvironment synapseEnvironment, String injectSeq, String faultSeq,String outSequence) {
        this.synapseEnvironment = synapseEnvironment;
        this.injectSeq = injectSeq;
        this.faultSeq = faultSeq;
        this.outSequence=outSequence;
    }


    private InboundRequestQueue inboundRequestQueue = new InboundRequestQueue();

    /**
     * activating registerd hndler to accept events.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * reciving events through netty.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InboundSourceRequest inboundSourceRequest = new InboundSourceRequest();
        if (msg instanceof DefaultFullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            HttpHeaders headers = fullHttpRequest.headers();
            for (String val : headers.names()) {
                inboundSourceRequest.addHttpheaders(val, headers.get(val));
            }
           inboundSourceRequest.setTo(fullHttpRequest.getUri());
            ByteBuf buf = fullHttpRequest.content();
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            inboundSourceRequest.setContentBytes(bytes);
            HttpHeaders trailingHeaders = fullHttpRequest.trailingHeaders();
            for (String val : trailingHeaders.names()) {
                inboundSourceRequest.addHttpTrailingheaders(val, trailingHeaders.get(val));
            }
        }
        inboundSourceRequest.setChannelHandlerContext(ctx);
        inboundSourceRequest.setSynapseEnvironment(this.synapseEnvironment);
        inboundSourceRequest.setInjectSeq(this.injectSeq);
        inboundSourceRequest.setFaultSeq(this.faultSeq);
        inboundSourceRequest.setOutSeq(outSequence);
        inboundRequestQueue.publish(inboundSourceRequest);
    }

}
