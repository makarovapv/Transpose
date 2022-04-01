import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import kotlin.IllegalStateException as IllegalStateException

class TestTranspose {
    @Test
    fun transposeAndCheckOutputString() {
        val file = File("testFile1.txt")
        file.writeText("A   B C\nD E")
        val expectedOutput = "A D\nB E\nC"
        // тестируем транспонирование без опций и запись в строку
        val transpose = Transpose(arrayOf("testFile1.txt"))
        transpose.start()
        assertEquals(expectedOutput, transpose.output())
        file.delete()
    }

    @Test
    fun transposeAndCheckOutputFile() {
        val file = File("testFile1.txt")
        file.writeText("A   B C\nD E")
        /*
        A   B C
        D E
         */
        /*
            A     B     C
         */
        val outputFile = File("outputFile1.txt")
        val expectedOutput = "A D\nB E\nC"
        // тестируем транспонирование без опций и запись в выходной файл по аргументу -о
        val transpose = Transpose(arrayOf("-o", "outputFile1.txt", "testFile1.txt"))
        transpose.start()
        assertEquals(expectedOutput, outputFile.readText())
        file.delete()
        outputFile.delete()
    }

    @Test
    fun transposeAndTrim() {
        val file = File("testFile2.txt")
        file.writeText("ABC   BA CDEF\nDPOIK EGHJLFSSDFHSDGHSD")
        val outputFile = File("outputFile2.txt")
        val expectedOutput = "ABC        DPOIK     \nBA         EGHJLFSSDF\nCDEF      "
        val transpose = Transpose(arrayOf("-t", "-o", "outputFile2.txt", "testFile2.txt"))
        transpose.start()
        assertEquals(expectedOutput, outputFile.readText())
        file.delete()
        outputFile.delete()
    }

    @Test
    fun transposeAndTrimWithCertainNum() {
        val file = File("testFile3.txt")
        file.writeText("ABC   B CDEF\nDPOIK EGHJLFSSDFHSDGHSD")
        val outputFile = File("outputFile3.txt")
        val expectedOutput = "AB DP\nB  EG\nCD"
        val transpose = Transpose(arrayOf("-a", "2", "-t", "-o", "outputFile3.txt", "testFile3.txt"))
        transpose.start()
        assertEquals(expectedOutput, outputFile.readText())
        file.delete()
        outputFile.delete()
    }

    @Test
    fun transposeAndRight() {
        val file = File("testFile4.txt")
        file.writeText("ABC   B CDEF\nDPOIK EGHJL")
        val outputFile = File("outputFile4.txt")
        val expectedOutput = "  ABC DPOIK\n    B EGHJL\n CDEF"
        val transpose = Transpose(arrayOf("-a", "5", "-r", "-o", "outputFile4.txt", "testFile4.txt"))
        transpose.start()
        assertEquals(expectedOutput, outputFile.readText())
        file.delete()
        outputFile.delete()
    }

    @Test
    fun transposeWithWordExceedingLimit() {
        val file = File("testFile4.txt")
        file.writeText("ABC   B CDEF\nDPOIK EGHJLF")
        val outputFile = File("outputFile4.txt")
        val transpose = Transpose(arrayOf("-a", "5", "-o", "outputFile4.txt", "testFile4.txt"))
        val exception = assertThrows(IllegalStateException::class.java) { transpose.start() }
        assertEquals("Word length exceeds 5", exception.message)
        file.delete()
        outputFile.delete()
    }

    @Test
    fun transposeTrimAndRight() {
        val file = File("testFile4.txt")
        file.writeText("ABC   B CDEF\nDPOIK EGHJLFSSDFHSDGHSD")
        val outputFile = File("outputFile4.txt")
        val expectedOutput = "  ABC DPOIK\n    B EGHJL\n CDEF"
        val transpose = Transpose(arrayOf("-a", "5", "-r", "-t", "-o", "outputFile4.txt", "testFile4.txt"))
        transpose.start()
        assertEquals(expectedOutput, outputFile.readText())
        file.delete()
        outputFile.delete()
    }
}