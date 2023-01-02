import org.junit.Assert.assertSame
import org.junit.Test
import java.io.*

class ObjectSerializationIntegrationTest {
    @Test
    fun `object instance is the same after deserialization`() {
        assertSame(TestObject, serializeDeserialize(TestObject))
        assertSame(DirectlyImplementsTestInterface, serializeDeserialize(DirectlyImplementsTestInterface))
        assertSame(IndirectlyImplementsTestInterface, serializeDeserialize(IndirectlyImplementsTestInterface))
        assertSame(SerializableFromScala, serializeDeserialize(SerializableFromScala))
    }

    @Test(expected = NotSerializableException::class)
    fun `cannot serialize non-serializable object`() {
        serializeDeserialize(NotSerializable)
    }

    @Test(expected = NotSerializableException::class)
    fun `cannot serialize non-serializable object from scala`() {
        serializeDeserialize(NotSerializableFromScala)
    }

    private fun serializeDeserialize(instance: Any): Any? {
        val outputStream = ByteArrayOutputStream()
        ObjectOutputStream(outputStream).use {
            it.writeObject(instance)
        }
        return ObjectInputStream(ByteArrayInputStream(outputStream.toByteArray())).use {
            it.readObject()
        }
    }
}
