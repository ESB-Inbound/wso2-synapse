package org.apache.synapse.inbound;


import org.apache.synapse.core.SynapseEnvironment;

public interface ListenerFactory {

    public InboundListener createInboundListener(String protocol, int port, SynapseEnvironment synapseEnvironment, String injectingSeq, String onErrorSeq);

}
