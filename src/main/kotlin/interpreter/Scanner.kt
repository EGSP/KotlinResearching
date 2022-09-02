package interpreter

import java.util.Collections

class Scanner(private val source:String) {
    private val tokens:MutableList<Token> = mutableListOf()

    private var entry:Int = 0;
    private var pointer:Int = 0;
    private var line:Int = 1;

    fun scanTokens():List<Token>{
        if(source.isEmpty())
            return emptyList()

        while (!isPointerOnEnd()){
            entry = pointer
            scanToken()
        }

        tokens.add(Token(TokenType.EOF,"",TokenType.EOF, pointer))
        return Collections.unmodifiableList(tokens)
    }

    private fun isPointerOnEnd():Boolean{
        return pointer >= source.length
    }

    private fun scanToken(){
        val character = getPointedChar()

        movePointer()
    }

    private fun getPointedChar(): Char {
        return source[pointer]
    }

    private fun movePointer(){
        pointer++
    }

    // Добавляет токены в список токенов. Вместо null литерала передается используемый tokenType
    private fun addToken(tokenType: TokenType){
        addToken(tokenType,tokenType)
    }

    private fun addToken(tokenType: TokenType, literal:Any){
        val lexeme = source.substring(entry,pointer)
        tokens.add(Token(tokenType,lexeme,literal,line))
    }


}