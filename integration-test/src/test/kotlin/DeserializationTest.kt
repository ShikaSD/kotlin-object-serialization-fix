import org.junit.Assert.assertSame
import org.junit.Test
import java.io.*

class ObjectSerializationIntegrationTest {
    @Test
    fun `object instance is the same after deserialization`() {
        assertSame(TestObject, serializeDeserialize(TestObject))
        assertSame(DirectlyImplementsTestInterface, serializeDeserialize(DirectlyImplementsTestInterface))
        assertSame(IndirectlyImplementsTestInterface, serializeDeserialize(IndirectlyImplementsTestInterface))
    }

    private fun serializeDeserialize(instance: Serializable): Serializable {
        val outputStream = ByteArrayOutputStream()
        ObjectOutputStream(outputStream).use {
            it.writeObject(instance)
        }
        return ObjectInputStream(ByteArrayInputStream(outputStream.toByteArray())).use {
            it.readObject() as Serializable
        }
    }
}
