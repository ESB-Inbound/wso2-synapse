package org.apache.synapse.protocol.http;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import org.apache.synapse.protocol.http.utils.InboundHttpConstants;


public class InboundHttpListner {
   private static Logger logger = Logger.getLogger(InboundHttpListner.class);
  private int port;



 public InboundHttpListner(int port){
     this.port = port;
 }

 public InboundHttpListner(){
 }

  public void start(){
      logger.info("Starting Inbound Http Listner on Port " + this.port);
      EventLoopGroup bossGroup = new NioEventLoopGroup();
      EventLoopGroup workerGroup = new NioEventLoopGroup();
      try {
          ServerBootstrap b = new ServerBootstrap();
          b.option(ChannelOption.SO_BACKLOG, InboundHttpConstants.MAXIMUM_CONNECTIONS_QUEUED);
          b.group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .childHandler(new InboundHttpTransportHandlerInitializer());

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
  }













    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
