package org.apache.synapse.inbound;


import org.apache.synapse.core.SynapseEnvironment;

import java.util.Properties;

public interface ListnerFactory {

    public InboundListner creatInboundListner(String protocol, int port, SynapseEnvironment synapseEnvironment, String injectingSeq, String onErrorSeq, String outSequence);

}
