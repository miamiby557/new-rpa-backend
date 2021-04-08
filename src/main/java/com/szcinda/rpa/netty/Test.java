package com.szcinda.rpa.netty;

public class Test {
    public static void main(String[] args) {
        String text = "GET / HTTP/1.1\n" +
                "Upgrade: websocket\n" +
                "Connection: Upgrade\n" +
                "X-Real-IP: 103.27.26.63\n" +
                "Host: 127.0.0.1:6688\n" +
                "Pragma: no-cache\n" +
                "Cache-Control: no-cache\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.122 Safari/537.36\n" +
                "Origin: http://tool.hibbba.com\n" +
                "Sec-WebSocket-Version: 13\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Accept-Language: zh-CN,zh;q=0.9\n" +
                "Sec-WebSocket-Key: jvhbQ1cQ6Yjy+x5YCuAbIw==\n" +
                "Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits\n" +
                "\n" +
                "\n";
//        String[] arr = text.split("\n");
//        for (String t : arr) {
//            if (t.startsWith("Sec-WebSocket-Key")) {
//                String[] keys = t.split(":");
//                System.out.println(keys[1].trim());
//                String originKey = keys[1].trim() + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//                MessageDigest alga = null;
//                byte[] digesta = null;
//                try {
//                    alga = MessageDigest.getInstance("SHA-1");
//                    alga.update(originKey.getBytes());
//                    digesta = alga.digest();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                byte[] respKey = Base64.encodeBase64(digesta);
//                String header = "HTTP/1.1 101 Switching Protocols\r\n " +
//                        "Upgrade: Websocket\r\n" +
//                        "Connection: Upgrade\r\n" +
//                        "Sec-WebSocket-Accept: " + new String(respKey) + "\r\n\r\n";
//                System.out.println(header);
//            }
//        }

    }
}
