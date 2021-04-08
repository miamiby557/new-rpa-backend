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
        // ChannelOutboundHandler，依照逆序执行
        ch.pipeline().addLast("encoder", new StringEncoder());

        // 属于ChannelInboundHandler，依照顺序执行
        ch.pipeline().addLast("decoder", new StringDecoder());
        ChannelPipeline pipeline = ch.pipeline();
        // 处理粘包问题
        ByteBuf delimiter = Unpooled.copiedBuffer("$$".getBytes());
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(9056, delimiter));
        /**
         * 自定义ChannelInboundHandlerAdapter
         */
        ch.pipeline().addLast(new NettyChannelInboundHandlerAdapter());
    }
}
