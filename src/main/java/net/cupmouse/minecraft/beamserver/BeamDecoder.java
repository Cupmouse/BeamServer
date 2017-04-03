package net.cupmouse.minecraft.beamserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class BeamDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        out.add(in.readBytes(in.readableBytes()));
//
        in.markReaderIndex();
        int length = in.readShort() & 0xffff;

        if (in.readableBytes() >= length) {
            out.add(in.readBytes(length));
        } else {
            in.resetReaderIndex();
        }
    }
}
