package com.hcloud.common.netty.demo

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.util.ReferenceCountUtil

class DiscardServiceHandler : ChannelHandlerAdapter() {
    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        // Close the connection when an exception is raised
        cause?.printStackTrace()
        ctx?.close()
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any?) {
        // Discard the received data silently
        val `in` = msg as? ByteBuf
        val isReadable = `in`?.isReadable ?: false
        try {
            while (isReadable) {
                println(`in`?.readByte())
                System.out.flush()
            }
        } finally {
            ReferenceCountUtil.release(msg)
        }
    }
}
