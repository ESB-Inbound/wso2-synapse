package org.apache.synapse.protocol.http;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;


public class InboundHttpTransportSourceHandler extends ChannelInboundHandlerAdapter {
private Logger logger = Logger.getLogger(InboundHttpTransportSourceHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       InboundSourceRequest inboundSourceRequest = new InboundSourceRequest();
        if(msg instanceof DefaultFullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
            HttpHeaders headers = fullHttpRequest.headers();
            for (String val : headers.names()) {
                inboundSourceRequest.addHttpheaders(val, headers.get(val));
            }
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
    }

}
