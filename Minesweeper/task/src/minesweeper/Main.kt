package minesweeper

class Minesweeper {
    private val typeOfMines: String = "X"
    private val typeOfSpace: String = "."
    private val typeOfMarks: String = "*"
    private val typeOfFree: String = "/"
    private val minefieldSize: Int = 9
    private val numberOfMines: Int
    private val mines = mutableListOf<Pair<Int, Int>>()
    private val marks = mutableListOf<Pair<Int, Int>>()
    private val minefield = List(minefieldSize * minefieldSize) { Dot() }.chunked(minefieldSize) // mineField<Dot>[][]
    private var isInitializeMines = false

    class Dot {
        var value = 0
        var isMine = false
        var isMark = false
        var isVisible = false
    }

    init {
        print("How many mines do you want on the field? ")
        numberOfMines = readLine()!!.toInt()
        if (numberOfMines > minefieldSize * minefieldSize) throw Exception("Impossible number of mines!")
        printMinefields()
        game()
    }

    private fun game() {
        do {
            print("Set/delete mine marks (x and y coordinates): ")
            val str = readLine()!!
            val (x, y) = str.split(" ").take(2).map(String::toInt)
            when (str.split(" ").last()) {
                "mine" ->
                    if (!markField(x, y)) println("There is a number here!")
                "free" ->
                    if (!isInitializeMines) {
                        initializeMines(x, y)
                        isInitializeMines = true
                        exploreFields(x, y)
                    } else
                        if (minefield[y - 1][x - 1].isMine) {
                            mines.forEach { (x, y) -> minefield[y - 1][x - 1].isVisible = true }
                            printMinefields()
                            println("You stepped on a mine and failed!")
                            return
                        } else
                            exploreFields(x, y)
            }
            printMinefields()
        } while (!checkGame())
        println("Congratulations! You found all the mines!")
    }

    private fun randomCoordinate() = (Math.random() * minefieldSize).toInt() + 1

    private fun initializeMines(firstX: Int, firstY: Int) {
        repeat(numberOfMines) {
            do {
                val x = randomCoordinate()
                val y = randomCoordinate()
                if (!mines.contains(Pair(x, y)) && !(x == firstX && y == firstY)) {
                    mines.add(Pair(x, y))
                    minefield[y - 1][x - 1].isMine = true
                    numbersAroundMine(x, y, true)
                    break
                }
            } while (true)
        }
    }

    private fun numbersAroundMine(x: Int, y: Int, b: Boolean) {
        if (x in 1..minefieldSize && y in 1..minefieldSize) {
            minefield[y - 1][x - 1].value++
            if (b)
                (-1..1).forEach { i ->
                    (-1..1).forEach { j ->
                        numbersAroundMine(x + i, y + j, false)
                    }
                }
        }
    }

    private fun markField(x: Int, y: Int) =
        if (minefield[y - 1][x - 1].isMark) {
            marks.remove(x to y)
            minefield[y - 1][x - 1].isMark = false
            true
        } else {
            if (!minefield[y - 1][x - 1].isVisible) {
                marks.add(x to y)
                minefield[y - 1][x - 1].isMark = true
                true
            } else
                false
        }

    private fun exploreFields(x: Int, y: Int) {
        if (x in 1..minefieldSize && y in 1..minefieldSize && !minefield[y - 1][x - 1].isVisible) {
            minefield[y - 1][x - 1].isVisible = true
            marks.remove(x to y)
            if (minefield[y - 1][x - 1].value == 0) {
                exploreFields(x - 1, y - 1)
                exploreFields(x, y - 1)
                exploreFields(x + 1, y - 1)
                exploreFields(x - 1, y)
                exploreFields(x + 1, y)
                exploreFields(x - 1, y + 1)
                exploreFields(x, y + 1)
                exploreFields(x + 1, y + 1)
            }
        }
    }

    private fun checkGame() = mines.containsAll(marks) && marks.containsAll(mines) ||
            minefield.flatten().count { it.isVisible } == minefieldSize * minefieldSize - numberOfMines

    private fun printMinefields() {
        var i = 1
        println("\n" +
                " |123456789|\n" +
                "—|—————————|\n" +
                (minefield
                    .flatten()
                    .map {
                        if (it.isVisible)
                            when {
                                it.isMine -> typeOfMines
                                it.value == 0 -> typeOfFree
                                else -> it.value.toString()
                            }
                        else
                            when {
                                it.isMark -> typeOfMarks
                                else -> typeOfSpace
                            }
                    }
                    .chunked(minefieldSize)
                    .joinToString("") {
                        "${i++}|" + it.joinToString("") + "|\n"
                    }) +
                "—|—————————|"
        )
    }
}

fun main() {
    Minesweeper()
}