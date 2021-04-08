package com.szcinda.rpa.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        // 处理粘包问题
        ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(9056, delimiter));
        ch.pipeline().addLast("encoder", new StringEncoder());
        ch.pipeline().addLast("decoder", new StringDecoder());
        ch.pipeline().addLast(new NettyChannelInboundHandlerAdapter());
    }
}
