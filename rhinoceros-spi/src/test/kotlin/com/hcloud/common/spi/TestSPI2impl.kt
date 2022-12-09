package com.hcloud.common.spi

@Join
class TestSPI2impl : TestSPI {
    override fun test() {
        print("com.hcloud.common.api.TestSPI2impl")
    }
}