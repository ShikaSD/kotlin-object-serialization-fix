package me.shika

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.lang.reflect.Method

@OptIn(ExperimentalCompilerApi::class)
@RunWith(Parameterized::class)
class ObjectSerializationFixTest(enableFir: Boolean) {
    companion object {
        @Parameters(name = "FIR: {1}")
        @JvmStatic
        fun data() = arrayOf(false, true)
    }

    private val compiler = KotlinCompilation().apply {
        compilerPluginRegistrars = listOf(ObjectSerializationCompilerPluginRegistrar())
        supportsK2 = true
        languageVersion = if (enableFir) "1.9" else "2.0"
    }

    private val SERIALIZABLE_OBJECT = """
        import java.io.Serializable
        
        object Serial : Serializable
    """.source()

    @Test
    fun `adds readResolve to obj extending Serializable`() {
        compiler.sources = listOf(SERIALIZABLE_OBJECT)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("Serial")
        assertTrue(klass.methods.any { it.addedReadResolve()})
    }

    private val NOT_SERIALIZABLE_OBJECT = """
        object NotSerial
    """.source()

    @Test
    fun `does not add readResolve to obj not extending Serializable`() {
        compiler.sources = listOf(NOT_SERIALIZABLE_OBJECT)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("NotSerial")
        assertTrue(klass.methods.none { it.addedReadResolve() })
    }

    private val SERIALIZABLE_PARENT = """
        import java.io.Serializable
        
        open class SerialParent : Serializable
        object SerialChild : SerialParent()
    """.source()

    @Test
    fun `adds readResolve to obj extending serializable class`() {
        compiler.sources = listOf(SERIALIZABLE_PARENT)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("SerialChild")
        assertTrue(klass.methods.any { it.addedReadResolve() })
    }

    private val SERIALIZABLE_PARENT_INTERFACE = """
        import java.io.Serializable
        
        interface SerialParent : Serializable
        object SerialChild : SerialParent
    """.source()

    @Test
    fun `adds readResolve to obj extending serializable interface`() {
        compiler.sources = listOf(SERIALIZABLE_PARENT_INTERFACE)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("SerialChild")
        assertTrue(klass.methods.any { it.addedReadResolve() })
    }

    private val SERIALIZABLE_READ_EXISTS = """
        import java.io.Serializable
        
        object SerialChild : Serializable {
            fun readResolve() = SerialChild
        }
    """.source()

    @Test
    fun `does not add readResolve to obj if read resolve already exits`() {
        compiler.sources = listOf(SERIALIZABLE_READ_EXISTS)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("SerialChild")
        assertTrue(klass.methods.none { it.addedReadResolve() })
    }

    private val SERIALIZABLE_READ_EXISTS_PARENT = """
        import java.io.Serializable
        
        object SerialChild : Serializable {
            fun readResolve() = SerialChild
        }
    """.source()

    @Test
    fun `does not add readResolve to obj if read resolve already exits in parent`() {
        compiler.sources = listOf(SERIALIZABLE_READ_EXISTS_PARENT)
        val result = compiler.compile()

        val klass = result.classLoader.loadClass("SerialChild")
        assertTrue(klass.methods.none { it.addedReadResolve() })
    }

    private fun Method.addedReadResolve() =
        name == "readResolve"
            && parameterCount == 0
            && returnType == Object::class.java
            && isSynthetic
}

fun String.source() = SourceFile.kotlin("Source.kt", this, trimIndent = true)
