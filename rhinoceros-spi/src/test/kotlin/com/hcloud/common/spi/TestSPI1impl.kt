package com.hcloud.common.spi

@Join
class TestSPI1impl : TestSPI {
    override fun test() {
        print("com.hcloud.common.api.TestSPI1impl")
    }
}