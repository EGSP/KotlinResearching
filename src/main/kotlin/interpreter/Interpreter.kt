package interpreter

import java.nio.file.Paths

class Interpreter {

    var errorMark:Boolean = false

    companion object Functions{
        private fun report(line:Int, location:String, message:String){
            println("[line $line] Error$location: $message")
        }

        fun error(interpreter: Interpreter,line:Int, message:String){
            report(line,"",message)
            interpreter.errorMark = true
        }
    }

    fun go(args:Array<String>){
        when(args.isEmpty()){
            true -> goConsole()
            false -> goFile()
            else -> return
        }

        errorMark = false
    }

    private fun goConsole(){
        println("Waiting script from console input")

        val text = readlnOrNull()

        when(text){
            null -> return
            else -> doInterpretation(text)
        }
    }

    private fun goFile(){
        val pathToProgram = Paths.get("").toAbsolutePath().toString()
        println(pathToProgram)
    }

    private fun doInterpretation(text:String){
        println("Interpretation start")
        val scanner = Scanner(text)
        val tokens = scanner.scanTokens()

        for(token in tokens){
            println(token.toString())
        }
        println("Interpretation end")
    }
}