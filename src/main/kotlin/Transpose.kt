import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.optional
import java.io.File
import java.io.FileNotFoundException

class Transpose(args: Array<String>) {
    private val parser = ArgParser("transpose") // поиск аргументов
    private var inputFile by parser.argument(ArgType.String, description = "задаёт имя входного файла.").optional()
    // optional - т.к может не указываться
    private val outputFile by parser.option(ArgType.String, shortName = "o", description = "задаёт имя выходного файла.")
    private var num by parser.option(ArgType.Int, shortName = "a", description = "слово должно занимать num символов, " +
            "оставшееся место - пробелы.")
    private val trim by parser.option(ArgType.Boolean, shortName = "t", description = "если слово “не влезает” в выделенное для него " +
            "место (флагом -а), то его следует обрезать до нужного размера.").default(false)
    private val right by parser.option(ArgType.Boolean, shortName = "r", description = " в рамках выделенного для него места (флагом -а) " +
            "следует выравнивать по правой границе. Если данный флаг не указан, слово выравнивается по левой границе.").default(false)
    private val output = StringBuilder() // запись для start
    private val lines: List<String>
    private var numProvided: Boolean = true

    init {
        parser.parse(args)

//        println("file -> $inputFile")
//        println("oFile -> $outputFile")
//        println("num -> $num")
//        println("trim -> $trim")
//        println("right -> $right")

        if (num == null) {
            if (trim || right) {
                num = 10
            //  В случае, если флаг -а отсутствует, но присутствуют флаги -t или -r,
            //  следует выравнивать текст так, будто указан флаг “-а 10”.
            } else {
                num = 1
                numProvided = false
            }
        }

        // если файл пустой, то считываем его с консоли
        if (inputFile == null) {
            print("Введите имя входного файла: ")
            inputFile = readLine()
            if (inputFile == "") {
                throw IllegalArgumentException("Входной файл необходим для выполнения программы!")
            }
        }

        val f = File(inputFile!!)
        if (!f.exists() || !f.isFile) {
            throw FileNotFoundException("Вы забыли создать файл!")
        }
        lines = f.readLines()

    }

    fun start() {
        val columns = mutableListOf<Int>()
        val rows = mutableListOf<Int>()
        val matrix = mutableMapOf<Pair<Int, Int>, String>()
        var transMatrix = mutableMapOf<Pair<Int, Int>, String>()

        // работа с флагами
        var countColumns = 1
        lines.forEach { line ->
            val l = line.split(" ").toMutableList()
            l.removeAll(listOf(""))

            var countRows = 0
            l.forEach { word ->
                countRows += 1
                rows.add(countRows)
                columns.add(countColumns)

                var w = word
                if (word.length < num!!) {
                    if (!right) { // right = false
                        while (w.length != num) {
                            w += " "
                        }
                    } else { // right = true
                        val r = num!! - w.length
                        val newW = StringBuilder("")
                        while (newW.length != r){
                            newW.append(" ")
                        }
                        newW.append(w)
                        w = newW.toString()
                    }
                }

                if (w.length == num) {
                    matrix[Pair(countColumns, countRows)] = w
                } else if (word.length > num!!) {
                    val str = StringBuilder("")
                    var k = 0
                    val q = w.split("").toMutableList()
                    q.removeIf {it == ""}
                    if (trim) { // trim = true
                        q.forEach { letter ->
                            if (k < num!!) {
                                str.append(letter)
                                k += 1
                            }
                        }
                    } else { // trim = false
                        if (numProvided) {
                            throw IllegalStateException("Word length exceeds $num")
                        } else {
                            str.append(w)
                        }
                    }
                    matrix[Pair(countColumns, countRows)] = str.toString()
                }
            }
            countColumns += 1
        }

        matrix.forEach { (key, value) ->
            transMatrix[Pair(key.second, key.first)] = value
        }
        transMatrix = transMatrix.toSortedMap(compareBy<Pair<Int, Int>> { it.first }.thenBy { it.second })
//        println("transMap: $transMatrix")

        countColumns = 0 // счет колонны
        var countRows = 1 // счет строки
        transMatrix.forEach { (key, value) ->
            countColumns += 1
            if (key.first > countRows) { // на следующую строку перенос
                countRows = key.first
                output.setLength(output.length - 1)
                output.append("\n")
                countColumns = 1
            }
            if (key.second > countColumns) {
                while (key.second != countColumns) {
                    // для создания пропусков
                    var x = 0
                    if (trim) {
                        while (x != num!!) {
                            x += 1
                            output.append(" ")
                        }
                    } else {
                        output.append(" ")
                    }
                    output.append(",")
                    countColumns += 1
                }
            }
            output.append("$value,") // если все ок, то просто добавляем элементы
        }
        output.setLength(output.length - 1)
        val out = output.toString().replace(",", " ")
        output.clear()
        output.append(out)
        output()
    }

    fun output(): String {
        if (outputFile == null) {
            println(output) // консольный вывод
        } else {
            File(outputFile!!).writeText(output.toString()) // вывод в файл
        }
        return output.toString()
    }
}

fun main(args: Array<String>) {
    Transpose(args).start()
}