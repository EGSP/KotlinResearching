package interpreter.ast

import interpreter.TokenType
import interpreter.expressions.Expression
import interpreter.expressions.Expression.Visitor

class AstPrinter: Visitor<String> {
    fun print(expression: Expression) = expression.accept(this)

    fun parentheses(name:String, vararg expressions: Expression):String {
        val builder = StringBuilder()

        // Проходимся по всем выражениям и рекурсивно печатаем их
        builder.append("(").append(name)
        for (expression in expressions) {
            builder.append(" ")
            builder.append(expression.accept(this))
        }
        builder.append(")")

        return builder.toString()
    }

    override fun visitLiteralExpression(expression: Expression.Literal): String {
        return when(expression.token.type){
            TokenType.NULL -> "null"
            else -> expression.token.literal.toString()
        }
    }

    override fun visitUnaryExpression(expression: Expression.Unary): String {
        return parentheses(expression.operator.lexeme,
            expression.rightExpression)
    }

    override fun visitBinaryExpression(expression: Expression.Binary): String {
        return parentheses(expression.operator.lexeme,
            expression.leftExpression, expression.rightExpression)
    }

    override fun visitGroupingExpression(expression: Expression.Grouping): String {
        return parentheses("group", expression.groupedExpression)
    }
}