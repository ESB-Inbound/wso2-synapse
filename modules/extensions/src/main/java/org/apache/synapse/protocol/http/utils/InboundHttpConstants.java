package org.apache.synapse.protocol.http.utils;


public class InboundHttpConstants {

    public final static int MAXIMUM_CONNECTIONS_QUEUED = 1024;
    public final static int MAXIMUM_CHUNK_SIZE_AGGREGATOR = 1048576;
    public final static int WORKER_POOL_SIZE = 10;
    public final static int REQUEST_BUFFER_CAPACITY = 1024;
    public final static String IS_INBOUND_ENDPOINT = "inbound-endpoint";
    public final static String CHANNEL_HANDLER_CONTEXT = "channel-handler-context";
    public final static String TEXT_XML = "text/xml";

    public final static String SOAP_ACTION = "SOAPAction";
    public final static int SOAP_11 = 1;
    public final static int SOAP_12 = 2;
    public final static String PASS_THROUGH_TARGET_BUFFER = "pass-through.pipe";
    public final static String CONTENT_TYPE = "ContentType";
}