package tool

import java.io.IOException
import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        exitProcess(64)
    }
    val outputDir = args[0]
    defineAst(outputDir, "Expression", listOf(
        "Literal  : Token token",
        "Unary    : Token operator, Expression rightExpression",
        "Binary   : Expression leftExpression, Token operator, Expression rightExpression",
        "Grouping : Expression groupedExpression",
        )
    )
}

private fun defineAst(outputDir: String, baseName: String, types: List<String>){
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")
    writer.println("package interpreter.expressions")
    writer.println()
    writer.println("import interpreter.Token")
    writer.println()
    writer.println("abstract class $baseName(){")

    for(type in types){
        val typeData = type.split(":")
        val typeName = typeData[0].trim()
        val typeFields = typeData[1].trim()

        defineType(writer, baseName, typeName, typeFields)
    }

    writer.println("}")
    writer.close()
}

private fun defineType(writer:PrintWriter, baseName: String, typeName: String, typeFields:String){

    writer.print("class $typeName(")
    val fields = typeFields.split(",")
    for((index,field) in fields.withIndex()){
        val fieldData = field.trim().split(" ")
        val fieldType = fieldData[0].trim()
        val fieldName = fieldData[1].trim()

        // В конец дописываем запятую, если есть ещё поля.
        writer.print(" $fieldName:$fieldType" + if(index<fields.size-1) "," else "")
    }
    writer.println("):$baseName()")
}