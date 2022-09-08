package interpreter.expressions

import interpreter.Token

abstract class Expression() {
    class Literal(token: Token) : Expression()
    class Unary(operator: Token, rightExpression: Expression) : Expression()
    class Binary(leftExpression: Expression, operator: Token, rightExpression: Expression) : Expression()
    class Grouping(groupedExpression: Expression) : Expression()
}
