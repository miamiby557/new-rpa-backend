package com.szcinda.rpa.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class NettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    /**
     * 消息规则：
     * 1 注册连接 CONNECTING#客户端唯一标识
     * 2 刷新流程 REFRESH#客户端唯一标识
     * 3 更新流程缓存 REFRESH_REVIEW#客户端唯一标识#(流程&流程)
     * 4 执行或者结束流程 (START|STOP)#客户端唯一标识#流程名称
     */
    /**
     * 管理一个全局map，保存连接进服务端的通道数量
     */
    public static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> CHANNEL_MAP = new ConcurrentHashMap<>();
    // KEY 代表 每个客户端的唯一标识
    public static final ConcurrentHashMap<String, ChannelId> CLIENT_MAP = new ConcurrentHashMap<>();
    // 客户端流程
    public static final ConcurrentHashMap<String, List<String>> FLOW_MAP = new ConcurrentHashMap<>();

    public static void receiveWechatData(String data) {
        if (StringUtils.isEmpty(data)) {
            return;
        }
        String[] array = data.split("#");
        System.out.println("参数1:" + array[0] + ",参数2:" + array[1] + ",参数3：" + array[2]);
        Assert.hasLength(array[1], "主机参数不能为空");
        boolean hasHost = CLIENT_MAP.containsKey(array[1]);
        Assert.isTrue(hasHost, "RPA主机不在线");
        ChannelId channelId = CLIENT_MAP.get(array[1]);
        hasHost = CHANNEL_MAP.containsKey(channelId);
        Assert.isTrue(hasHost, "RPA主机不在线");
        ChannelHandlerContext context = CHANNEL_MAP.get(channelId);
        Assert.isTrue(context != null, "RPA主机不在线");
        context.writeAndFlush(Unpooled.copiedBuffer(data.getBytes()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String body = (String) msg;
//        body = body.replace("$$", "");
        System.out.println("接收到客户端消息:" + body);
        if (body.startsWith("CONNECTING")) {
            String[] array = body.split("#");
            // 保存客户端映射关系
            CLIENT_MAP.put(array[1], ctx.channel().id());
            System.out.println("当前客户端数量:" + CHANNEL_MAP.size());
        } else if (body.startsWith("START") || body.startsWith("STOP")) {
            String[] array = body.split("#");
            ChannelId channelId = CLIENT_MAP.get(array[1]);
            ChannelHandlerContext context = CHANNEL_MAP.get(channelId);
            context.writeAndFlush(Unpooled.copiedBuffer(body.getBytes()));
        } else if (body.startsWith("REFRESH_REVIEW")) {//REFRESH_REVIEW
            String[] array = body.split("#");
            String clientId = array[1];
            if (array.length == 3) {
                String flows = array[2];
                String[] flowList = flows.split("&");
                FLOW_MAP.put(clientId, Arrays.asList(flowList));
            }
        } else if (body.startsWith("REFRESH")) {
            String[] array = body.split("#");
            ChannelId channelId = CLIENT_MAP.get(array[1]);
            ChannelHandlerContext context = CHANNEL_MAP.get(channelId);
            context.writeAndFlush(Unpooled.copiedBuffer(body.getBytes()));
        } else if (body.startsWith("GET / HTTP")) {
            responseClient(body, ctx);
        }
    }

    private void responseClient(String text, ChannelHandlerContext ctx) {
        String[] arr = text.split("\n");
        for (String t : arr) {
            if (t.startsWith("Sec-WebSocket-Key")) {
                String[] keys = t.split(":");
                System.out.println(keys[1].trim());
                String originKey = keys[1].trim() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
                MessageDigest alga;
                byte[] digesta = null;
                try {
                    alga = MessageDigest.getInstance("SHA-1");
                    alga.update(originKey.getBytes());
                    digesta = alga.digest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] respKey = Base64.encodeBase64(digesta);
                String header = "HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: Websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: " + new String(respKey) + "\r\n\r\n";
                System.out.println("header:" + header);
                ctx.writeAndFlush(Unpooled.copiedBuffer(header.getBytes()));
            }
        }
    }

    /**
     * 从客户端收到新的数据、读取完成时调用
     *
     * @param ctx
     */
    /*@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws IOException {
        System.out.println("读取客户端消息完成...");
        ctx.flush();
    }*/

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getLocalizedMessage());
        System.out.println("客户端出现异常断开...");
        ctx.close();//抛出异常，断开与客户端的连接
    }

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ctx.channel().read();
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        //此处不能使用ctx.close()，否则客户端始终无法与服务端建立连接
        System.out.println("与客户端连接成功:IP:" + clientIp + ",name:" + ctx.name());
        CHANNEL_MAP.put(ctx.channel().id(), ctx);
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close(); //断开连接时，必须关闭，否则造成资源浪费，并发量很大情况下可能造成宕机
        System.out.println("客户端断开连接:" + clientIp);
        CHANNEL_MAP.remove(ctx.channel().id());
    }

    /**
     * 服务端当read超时, 会调用这个方法
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ctx.close();//超时时断开连接
        System.out.println("读取客户端消息超时:" + clientIp);
    }
}
