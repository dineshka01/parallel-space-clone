package com.yourpackage.parallelspace.identity

import com.yourpackage.parallelspace.model.DeviceIdentity
import java.security.SecureRandom
import java.util.UUID

class IdentityGenerator {

    private val random = SecureRandom()

    fun generate(): DeviceIdentity {
        val modelIndex = random.nextInt(MODELS.size)
        val (model, manufacturer) = MODELS[modelIndex]
        val brand = manufacturer
        val dev = model.lowercase().replace(" ", "_")
        val androidVer = ANDROID_VERSIONS[random.nextInt(ANDROID_VERSIONS.size)]
        val sdk = SDK_MAP[androidVer] ?: 34
        val buildId = randomString(8).uppercase()
        val fingerprint = "$brand/$dev/$dev:$androidVer/$buildId/${randomHex(6)}:user/release-keys"

        return DeviceIdentity(
            deviceId = "35${randomDigits(13)}",
            androidId = randomHex(16),
            gsfId = randomHex(16),
            advertisingId = UUID.randomUUID().toString(),
            wifiMac = randomMac(),
            bluetoothMac = randomMac(),
            wifiSsid = "${listOf("HOME","WiFi","NET","Fiber","Office")[random.nextInt(5)]}-${random.nextInt(1000,99999)}",
            wifiBssid = randomMac(),
            imei = generateIMEI(),
            imsi = generateIMSI(),
            simSerial = "89${randomDigits(17)}",
            subscriberId = "310${randomDigits(12)}",
            phoneNumber = "+1${randomDigits(10)}",
            serialNumber = randomHex(12).lowercase(),
            brand = brand,
            model = model,
            manufacturer = manufacturer,
            device = dev,
            product = dev,
            hardware = listOf("qcom","exynos","tensor","mt6897","mt6989")[random.nextInt(5)],
            fingerprint = fingerprint,
            hostname = "android-${randomHex(8).lowercase()}",
            display = "$model.$buildId",
            bootloader = listOf("slider-1.0","taro-1.1","zuma-2.0","pineapple-1.0")[random.nextInt(4)],
            timezone = TIMEZONES[random.nextInt(TIMEZONES.size)],
            locale = LOCALES[random.nextInt(LOCALES.size)],
            country = COUNTRIES[random.nextInt(COUNTRIES.size)],
            kernelVersion = KERNELS[random.nextInt(KERNELS.size)],
            osVersion = androidVer,
            sdkVersion = sdk,
            screenResolution = RESOLUTIONS[random.nextInt(RESOLUTIONS.size)],
            screenDensity = listOf(420,440,480,560,640)[random.nextInt(5)],
            totalRam = listOf(6L,8L,12L,16L,24L)[random.nextInt(5)] * 1024L * 1024L * 1024L,
            totalStorage = listOf(128L,256L,512L,1000L)[random.nextInt(4)] * 1024L * 1024L * 1024L,
            coreCount = listOf(8,8,10,12)[random.nextInt(4)],
            batteryLevel = random.nextFloat() * 0.7f + 0.2f,
            availableAccounts = emptyList()
        )
    }

    private fun generateIMEI(): String {
        val body = randomDigits(14)
        var sum = 0; var alt = true
        for (i in body.length - 1 downTo 0) {
            var n = body[i].digitToInt()
            if (alt) { n *= 2; if (n > 9) n -= 9 }
            sum += n; alt = !alt
        }
        return body + ((10 - (sum % 10)) % 10)
    }

    private fun generateIMSI(): String {
        val mcc = listOf("310","404","234","250","440","450")[random.nextInt(6)]
        val mnc = listOf("01","02","10","20","30","40")[random.nextInt(6)]
        return mcc + mnc + randomDigits(10)
    }

    private fun randomMac(): String {
        val bytes = ByteArray(6)
        random.nextBytes(bytes)
        bytes[0] = (bytes[0].toInt() and 0xFE or 0x02).toByte()
        return bytes.joinToString(":") { "%02X".format(it) }
    }

    private fun randomHex(len: Int) = (1..len).map { HEX[random.nextInt(16)] }.joinToString("")
    private fun randomDigits(len: Int) = (1..len).map { DIGITS[random.nextInt(10)] }.joinToString("")
    private fun randomString(len: Int) = (1..len).map { ALPHA[random.nextInt(36)] }.joinToString("")

    companion object {
        private val HEX = "0123456789ABCDEF"
        private val DIGITS = "0123456789"
        private val ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

        private val MODELS = listOf(
            "Pixel 9 Pro" to "Google", "Pixel 9" to "Google",
            "Galaxy S25 Ultra" to "Samsung", "Galaxy S25" to "Samsung",
            "Mi 14 Pro" to "Xiaomi", "OnePlus 13" to "OnePlus",
            "Find X8 Pro" to "OPPO", "vivo X200 Pro" to "vivo",
            "Edge 50 Ultra" to "Motorola", "Phone (3a)" to "Nothing",
            "ROG Phone 9" to "Asus", "Xperia 1 VII" to "Sony"
        )
        private val ANDROID_VERSIONS = listOf("15", "14", "13")
        private val SDK_MAP = mapOf("15" to 35, "14" to 34, "13" to 33)
        private val TIMEZONES = listOf("America/New_York","Asia/Kolkata","Europe/London","Asia/Tokyo","Australia/Sydney","America/Chicago","Europe/Berlin")
        private val LOCALES = listOf("en_US","en_IN","en_GB","ja_JP","ko_KR","de_DE","fr_FR")
        private val COUNTRIES = listOf("US","IN","GB","JP","KR","DE","FR","AU","AE","BR")
        private val KERNELS = listOf("5.15.123-android13-8","5.10.198-android12-9","6.1.49-android14-5","6.6.30-android15-3","5.15.148-android14-8")
        private val RESOLUTIONS = listOf("1080x2400","1440x3120","1080x2340","1260x2800","1080x2412","1440x3088")
    }
}
