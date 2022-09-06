package interpreter

import interpreter.TokenType.*
import java.util.*


class Scanner(private val source:String, private val interpreter: Interpreter) {
    private val tokens:MutableList<Token> = mutableListOf()

    private var entry:Int = 0
    private var pointer:Int = 0
    private var line:Int = 1

    fun scanTokens():List<Token>{
        if(source.isEmpty())
            return emptyList()

        while (!isPointerOnEnd()){
            entry = pointer
            scanToken()
        }

        tokens.add(Token(EOF,"", EOF, pointer))
        return Collections.unmodifiableList(tokens)
    }

    private fun scanToken(){
        val character = getPointedChar()

        when (character) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)

            '!' -> {
                addToken(if(isNextChar('=')) BANG_EQUAL else BANG)
                movePointer()
            }
            '=' -> {
                addToken(if(isNextChar('=')) EQUAL_EQUAL else EQUAL)
                movePointer()
            }
            '<' ->{
                addToken(if(isNextChar('=')) LESS_EQUAL else LESS)
                movePointer()
            }
            '>'->{
                addToken(if(isNextChar('=')) GREATER_EQUAL else GREATER)
            }

            '/'->{
                // Начинается комментарий
                if(isNextChar('/')) {
                    // Идем до конца комментария, строки или файла. Нам нужно его полностью пропустить
                    while (!isPointerOnEnd() && !isNextChar('\n')) movePointer()
                }else
                {
                    addToken(SLASH)
                }
            }

            '"' -> addString()

            // Игнорируем пробелы
            ' ' -> return
            '\r'-> return
            '\t'-> return

            '\n'-> incrementLine()

            else -> Interpreter.error(interpreter,line,"Unexpected character [$character]")
        }
        movePointer()
    }

    private fun isPointerOnEnd():Boolean{
        return pointer >= source.length
    }

    private fun getPointedChar(): Char {
        return source[pointer]
    }

    private fun isNextChar(expected: Char):Boolean {
        if(isPointerOnEnd()) return false
        if(source[pointer+1]!=expected) return false
        return true
    }

    private fun incrementLine() = line++

    private fun movePointer(){
        pointer++
    }

    // Передвигает указатель до указанного символа.
    // Возвращает true если дошёл успешно, false - если не удалось дойти.
    private fun movePointerTo(char: Char):Boolean{
        while(!isPointerOnEnd()){
            if(isNextChar('\n')) {
                incrementLine()
                movePointer()
                continue
            }
            when(isNextChar(char)){
                true ->{
                    movePointer()
                    return true
                }
                false -> movePointer()
            }
        }
        return false
    }

    // Добавляет токены в список токенов. Вместо null литерала передается используемый tokenType
    private fun addToken(tokenType: TokenType){
        addToken(tokenType,tokenType)
    }

    private fun addToken(tokenType: TokenType, literal:Any){
        val lexeme = source.substring(entry,pointer)
        tokens.add(Token(tokenType,lexeme,literal,line))
    }

    private fun addString(){
        if(movePointerTo('"')){
            // + и - нужны, чтобы убрать из начения кавычки.
            val str = source.substring(entry+1,pointer-1)
            addToken(STRING, str)
        }else{
            Interpreter.error(interpreter, line, "String literal is not closed.")
        }
    }


}