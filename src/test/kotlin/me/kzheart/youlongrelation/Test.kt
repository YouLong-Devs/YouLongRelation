package me.kzheart.youlongrelation

import com.google.common.io.ByteStreams
import com.google.common.primitives.Bytes
import org.junit.Test
import javax.script.ScriptEngineManager

/**
 * @author kzheart
 * @date 2021/11/4 20:44
 */
fun main() {
    val script = ScriptEngineManager().getEngineByName("javascript")
    println(script.eval("1 * 1").toString().toDouble())
    println(script.eval("1 * 1"))
//    val newDataInput = ByteStreams.newDataInput()

}