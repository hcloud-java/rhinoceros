package com.hcloud.common.netty.demo

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

/**
 *
 * <a herf="https://netty.io/wiki/user-guide-for-5.x.html">netty demo</a>
 * */
class DiscardServer(private val port: Int) {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val port = if (args.isNotEmpty()) {
                args[0].toInt()
            } else {
                9999
            }

            DiscardServer(port).run()
        }
    }

    fun run() {
        val bossGroup = NioEventLoopGroup()
        val workerGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel::class.java)
                .childHandler(
                    object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel?) {
                            ch?.pipeline()?.addLast(DiscardServiceHandler())
                        }
                    },
                )
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
            val f = b.bind(port).sync()
            f.channel().closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}
