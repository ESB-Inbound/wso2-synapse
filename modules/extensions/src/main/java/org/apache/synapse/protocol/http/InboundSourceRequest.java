package org.apache.synapse.protocol.http;


import io.netty.channel.ChannelHandlerContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.transport.passthru.util.StreamingOnRequestDataSource;

import java.util.HashMap;
import java.util.Map;

public class InboundSourceRequest {

    private Map<String, String> httpheaders = new HashMap<String, String>();

    private ChannelHandlerContext channelHandlerContext;
    private byte[] contentBytes;
    private byte[] trailingHeaders;
    private SynapseEnvironment synapseEnvironment;
    private String injectSeq;
    private String faultSeq;


    public String getInjectSeq() {
        return injectSeq;
    }

    public void setInjectSeq(String injectSeq) {
        this.injectSeq = injectSeq;
    }

    public String getFaultSeq() {
        return faultSeq;
    }

    public void setFaultSeq(String faultSeq) {
        this.faultSeq = faultSeq;
    }

    public SynapseEnvironment getSynapseEnvironment() {
        return synapseEnvironment;
    }

    public void setSynapseEnvironment(SynapseEnvironment synapseEnvironment) {
        this.synapseEnvironment = synapseEnvironment;
    }

    private Map<String, String> httptrailingHeaders = new HashMap<String, String>();

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public Map<String, String> getHttptrailingHeaders() {
        return httptrailingHeaders;
    }

    public void addHttpTrailingheaders(String key, String value) {
        this.httptrailingHeaders.put(key, value);
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public byte[] getTrailingHeaders() {
        return trailingHeaders;
    }

    public void setTrailingHeaders(byte[] trailingHeaders) {
        this.trailingHeaders = trailingHeaders;
    }

    public byte[] getContentBytes() {
        return contentBytes;
    }

    public void setContentBytes(byte[] contentBytes) {
        this.contentBytes = contentBytes;
    }

    public Map<String, String> getHttpheaders() {
        return httpheaders;
    }

    public void addHttpheaders(String key, String value) {
        this.httpheaders.put(key, value);
    }


}