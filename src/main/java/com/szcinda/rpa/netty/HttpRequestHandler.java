package com.szcinda.rpa.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        super();
        this.wsUri = wsUri;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (wsUri.equalsIgnoreCase(msg.getUri().substring(0, 3))) {
            String userId = findUserIdByUri(msg.getUri());
            if (userId != null && userId.trim().length() > 0) {

            } else {
            }// 没有获取到用户Id
            ctx.fireChannelRead(msg.setUri(wsUri).retain());
        }
    }

    private String findUserIdByUri(String uri) {// 通过Uid获取用户Id--uri中包含userId

        return null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace(System.err);
    }
}
