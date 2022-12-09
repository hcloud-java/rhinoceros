package com.hcloud.common.spi
import org.junit.Test


class SpiExtensionFactoryTest {
    @Test
    fun getExtensionTest() {
        val testSPI = ExtensionLoader.getExtensionLoader(TestSPI::class.java).getJoin("testSPI1")
        testSPI?.test()
    }
}