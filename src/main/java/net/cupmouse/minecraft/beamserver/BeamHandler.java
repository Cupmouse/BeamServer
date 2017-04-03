package net.cupmouse.minecraft.beamserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Arrays;

public class BeamHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("connected!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("disconnected");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        try {
            ByteBuf buffer = Unpooled.buffer(in.readableBytes());
            buffer.writeBytes(in);
            System.out.println(buffer.readableBytes());
            System.out.println(Arrays.toString(buffer.array()));

            // 使い終わったやつはりりーすする
            buffer.release();
        } finally {
            // msg.write~~を使わない場合は自動的にリリースされないので、最後にリリースする。これを忘れないように。
            in.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
