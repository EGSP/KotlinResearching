package interpreter

import interpreter.TokenType.*
import java.util.*


class Scanner(private val source:String, private val interpreter: Interpreter) {

    companion object LanguageKeywords{
        private var keywords: MutableMap<String, TokenType> = mutableMapOf()

        init{
            keywords["and"] = AND
            keywords["class"] = CLASS
            keywords["else"] = ELSE
            keywords["false"] = FALSE
            keywords["for"] = FOR
            keywords["fun"] = FUN
            keywords["if"] = IF
            keywords["null"] = NULL
            keywords["or"] = OR
            keywords["print"] = PRINT
            keywords["return"] = RETURN
            keywords["super"] = SUPER
            keywords["this"] = THIS
            keywords["true"] = TRUE
            keywords["var"] = VAR
            keywords["while"] = WHILE
        }
    }

    private val tokens:MutableList<Token> = mutableListOf()

    private var entry:Int = 0
    private var pointer:Int = 0
    private var line:Int = 1

    private val current get() = getPointedChar()
    private val next get() = getNextChar()


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

        when (val character = current) {
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

            // Игнорируем пробелы
            ' ' -> return
            '\r'-> return
            '\t'-> return

            '\n'-> incrementLine()

            '"' -> addString()

            else -> {
                if(character.isDigit()){
                    addNumber()
                }else if(isWord(character)){
                    addIdentifier()
                }else {
                    Interpreter.error(interpreter, line, "Unexpected character [$character]")
                }
            }
        }
        movePointer()
    }

    private fun isPointerOnEnd():Boolean{
        return pointer >= source.length
    }

    private fun isNextChar(expected: Char):Boolean {
        if(isPointerOnEnd()) return false
        if(source[pointer+1]!=expected) return false
        return true
    }

    private fun isExpectedChar(shift:Int, predicate: (char:Char) -> Boolean):Boolean{
        if(pointer+shift >= source.length) return false
        if(isPointerOnEnd()) return false
        if(!predicate(source[pointer+shift])) return false
        return true
    }

    private fun isWord(char:Char):Boolean{
        return char.isLetter() || char == '_'
    }

    private fun isWordOrDigit(char:Char):Boolean{
        return isWord(char) || char.isDigit()
    }

    private fun getPointedChar(): Char {
        return source[pointer]
    }

    private fun getNextChar():Char{
        return source[pointer+1]
    }

    private fun getSubstring():String{
        return source.substring(entry,pointer)
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

    private fun movePointerWhile(predicate: (char: Char) -> Boolean){
        while(isExpectedChar(1,predicate)) movePointer()
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

    private fun addNumber(){
        // Т.к. функция изначально вызвается на числе, то нужно передвинуть указатель.
        while (!isPointerOnEnd()){
            if(next.isDigit()) {
                movePointer()
                continue
            }

            if(isNextChar('.')){
                if(isExpectedChar(2) { char -> char.isDigit() }){
                    // Передвигаем указатель на точку.
                    movePointer()
                    continue
                }
                break
            }
            // Это значит мы дошли до конца файла.
            break
        }

        val number = getSubstring().toFloatOrNull()
        if(number == null)
        {
            Interpreter.error(interpreter, line, "Cannot parse number.")
        }else{
            addToken(NUMBER, number)
        }
    }

    private fun addIdentifier(){
        // Если следующий символ подходит.
        movePointerWhile {char->isWordOrDigit(char)}
        val word = getSubstring()
        addToken(keywords[word]?:IDENTIFIER, word)
    }
}