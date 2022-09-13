package interpreter.ast

import interpreter.Token
import interpreter.TokenType
import interpreter.expressions.Expression
import interpreter.expressions.Expression.Visitor

class AstPrinter: Visitor<String> {

    companion object AstPrinterTest{
        fun testAstPrinter(){
            val expression: Expression = Expression.Binary(
                Expression.Unary(
                    Token(TokenType.MINUS, "-", TokenType.MINUS, 1),
                    Expression.Literal(Token(TokenType.NUMBER,"123",123,0))
                ),
                Token(TokenType.STAR, "*", TokenType.STAR, 1),
                Expression.Grouping(
                    Expression.Literal(Token(TokenType.NUMBER,"45.67",45.67f,0))
                )
            )

            println(AstPrinter().print(expression))
        }
    }
    fun print(expression: Expression) = expression.accept(this)

    private fun parentheses(name:String, vararg expressions: Expression):String {
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