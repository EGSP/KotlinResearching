import interpreter.Interpreter
import interpreter.Token
import interpreter.TokenType
import interpreter.ast.AstPrinter
import interpreter.expressions.Expression


fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    AstPrinter.testAstPrinter()

    val interpreter = Interpreter()
    interpreter.go(args)
}