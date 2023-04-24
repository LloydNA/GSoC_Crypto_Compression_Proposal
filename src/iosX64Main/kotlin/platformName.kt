import platform.Foundation.NSString

actual fun platformName(): String = objc()

@Suppress("CAST_NEVER_SUCCEEDS")
fun objc():String{
    val a: NSString = "iosX64" as NSString


    return a as String;
}