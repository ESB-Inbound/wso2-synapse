/*
*  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.synapse.protocol.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.CharsetUtil;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.log4j.Logger;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.inbound.InboundMessageContextQueue;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Sends responses to requests that are sent to the InboundEndpoint
 */
public class InboundSourceResponseSender implements Runnable{

    private Logger logger = Logger.getLogger(InboundSourceResponseSender.class);

    public void run() {
        while(true){
            try {
                MessageContext smc = InboundMessageContextQueue.getInstance().getMessageContextQueue().take();
                ChannelHandlerContext ctx = (ChannelHandlerContext)smc.getProperty(SynapseConstants.CHANNEL_HANDLER_CONTEXT);

                //Retrieve the SOAP envelope from the MessageContext
                SOAPEnvelope envelope = smc.getEnvelope();

                if(envelope != null){
                    FullHttpResponse fullHttpResponse = getHttpResponse(envelope);
                    //Send the envelope using the ChannelHandlerContext
                    ctx.writeAndFlush(fullHttpResponse);
                }
            } catch (InterruptedException e) {
             logger.error(e.getMessage());
            }
        }
    }

    private FullHttpResponse getHttpResponse(SOAPEnvelope soapEnvelope){
        byte[] bytes = soapEnvelope.toString().getBytes();
        ByteBuf CONTENT =  Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(bytes));
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,CONTENT.duplicate());
        response.headers().set(CONTENT_TYPE, "text/xml");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
       return response;
    }






}
