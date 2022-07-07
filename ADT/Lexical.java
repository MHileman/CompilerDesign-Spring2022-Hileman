/*
 * Lexical Analyzer: Lexical Class File
 * Created by: Matthew Hileman
 * Last Updated: 19 April 2022
 * Purpose: Identify and classify tokens from an input file of compiled language.
 */

package ADT;
import java.io.*;

/* --------------------------------
 * -------- LEXICAL CLASS  --------
 * ----- PART 2, ASSIGNMENT 1 -----
 * -------------------------------- */
public class Lexical {

    private File file;                        // File to be read for input
    private FileReader filereader;            // Reader, Java reqd
    private BufferedReader bufferedreader;    // Buffered, Java reqd
    private String line;                      // Current line of input from file
    private int linePos;                      // Current character position in the current line
    private SymbolTable saveSymbols;          // SymbolTable used in Lexical sent as parameter to construct
    private boolean EOF;                      // End Of File indicator
    private boolean echo;                     // true means echo each input line
    private boolean printToken;               // true to print found tokens here
    private int lineCount;                    // line #in file, for echo-ing
    private boolean needLine;                 // track when to read a new line
                                              // Tables to hold the reserve words and the mnemonics for token codes
    private ReserveTable reserveWords = new ReserveTable(50); // a few more than # reserves
    private ReserveTable mnemonics = new ReserveTable(50);    // a few more than # reserves

    // global char (current character)
    char currCh;

    /* -----------------------
     * ***** CONSTANTS ***** *
     * -------------------- */
    // mnemonic codes - comps
    private final int UNKNOWN_CHAR = 99;
    public final int _GRTR = 38;
    public final int _LESS = 39;
    public final int _GREQ = 40;
    public final int _LEEQ = 41;
    public final int _EQLS = 42;
    public final int _NEQL = 43;
    // mnemonic codes - types
    private final int IDENT_ID = 50;
    private final int INTEGER_ID = 51;
    private final int FLOAT_ID = 52;
    private final int STRING_ID = 53;
    private final int UNKNOWN_ID = 99;
    // comment syntax
    final char comment_start1 = '{';
    final char comment_end1 = '}';
    final char comment_start2 = '(';
    final char comment_startend = '*';
    final char comment_end2 = ')';
    // max lengths
    private final int MAX_IDENT = 20;
    private final int MAX_NUMBER = 9;
    private boolean truncated = false;

    /* -----------------------
     * **** CONSTRUCTOR **** *
     * -------------------- */
    public Lexical(String filename, SymbolTable symbols, boolean echoOn){
        saveSymbols = symbols;  // map the initialized parameter to the local ST
        echo = echoOn;          // store echo status
        lineCount = 0;          // start the line number count
        line = "";              // line starts empty
        needLine = true;        // need to read a line
        printToken = false;     // default OFF, do not print tokens here
                                // within GetNextToken; call setPrintToken to change it publicly.
        linePos = -1;           // no chars read yet

        // call initializations of tables
        initReserveWords(reserveWords);
        initMnemonics(mnemonics);

        // set up the file access, get first character, line retrieved 1st time
        try {
            file = new File(filename);                        // creates a new file instance
            filereader = new FileReader(file);                // reads the file
            bufferedreader = new BufferedReader(filereader);  // creates a buffering character input stream
            EOF = false;
            currCh = GetNextChar();
        } catch (IOException e) {
            EOF = true;
            e.printStackTrace();
        }
    } // constructor


    // [ MAIN TOKEN CLASS USED THROUGHOUT ]
    // token class is declared here, no accessors needed
    public class token {
        // elements
        public String lexeme;
        public int code;
        public String mnemonic;
        // constructor
        token() {
            lexeme = "";
            code = 0;
            mnemonic = "";
        }
    }

    // Public access to the current End Of File status
    public boolean EOF() {
        return EOF;
    }

    // DEBUG enabler, turns on token printing inside of GetNextToken
    public void setPrintToken(boolean on) {
        printToken = on;
    }

    /* @@@ */
    // RESERVE WORDS
    private void initReserveWords(ReserveTable reserveWords) {
        // Named reserve words
        reserveWords.Add("GO_TO", 0);
        reserveWords.Add("INTEGER", 1);
        reserveWords.Add("TO", 2);
        reserveWords.Add("DO", 3);
        reserveWords.Add("IF", 4);
        reserveWords.Add("THEN", 5);
        reserveWords.Add("ELSE", 6);
        reserveWords.Add("FOR", 7);
        reserveWords.Add("OF", 8);
        reserveWords.Add("PRINTLN", 9);
        reserveWords.Add("READLN", 10);
        reserveWords.Add("BEGIN", 11);
        reserveWords.Add("END", 12);
        reserveWords.Add("VAR", 13);
        reserveWords.Add("DOWHILE", 14);
        reserveWords.Add("PROGRAM", 15);
        reserveWords.Add("LABEL", 16);
        reserveWords.Add("REPEAT", 17);
        reserveWords.Add("UNTIL", 18);
        reserveWords.Add("PROCEDURE", 19);
        reserveWords.Add("DOWNTO", 20);
        reserveWords.Add("FUNCTION", 21);
        reserveWords.Add("RETURN", 22);
        reserveWords.Add("FLOAT", 23);
        reserveWords.Add("STRING", 24);
        reserveWords.Add("ARRAY", 25);

        // 1 and 2-char tokens
        reserveWords.Add("/", 30);
        reserveWords.Add("*", 31);
        reserveWords.Add("+", 32);
        reserveWords.Add("-", 33);
        reserveWords.Add("(", 34);
        reserveWords.Add(")", 35);
        reserveWords.Add(";", 36);
        reserveWords.Add(":=", 37);
        reserveWords.Add(">", 38);
        reserveWords.Add("<", 39);
        reserveWords.Add(">=", 40);
        reserveWords.Add("<=", 41);
        reserveWords.Add("=", 42);
        reserveWords.Add("<>", 43);
        reserveWords.Add(",", 44);
        reserveWords.Add("[", 45);
        reserveWords.Add("]", 46);
        reserveWords.Add(":", 47);
        reserveWords.Add(".", 48);
    }

    /* @@@ */
    // add 5-character student created mnemonics corresponding to reserve values (and others)
    private void initMnemonics(ReserveTable mnemonics) {
        mnemonics.Add("GOTO_", 0);
        mnemonics.Add("INTGR", 1);
        mnemonics.Add("TO___", 2);
        mnemonics.Add("DO___", 3);
        mnemonics.Add("IF___", 4);
        mnemonics.Add("THEN_", 5);
        mnemonics.Add("ELSE_", 6);
        mnemonics.Add("FOR__", 7);
        mnemonics.Add("OF___", 8);
        mnemonics.Add("PRINT", 9);
        mnemonics.Add("READL", 10);
        mnemonics.Add("BEGIN", 11);
        mnemonics.Add("END__", 12);
        mnemonics.Add("VAR__", 13);
        mnemonics.Add("DOWHI", 14);
        mnemonics.Add("PROGR", 15);
        mnemonics.Add("LABEL", 16);
        mnemonics.Add("REPEA", 17);
        mnemonics.Add("UNTIL", 18);
        mnemonics.Add("PROCE", 19);
        mnemonics.Add("DOWNT", 20);
        mnemonics.Add("FUNCT", 21);
        mnemonics.Add("RETUR", 22);
        mnemonics.Add("FLOAT", 23);
        mnemonics.Add("STRIN", 24);
        mnemonics.Add("ARRAY", 25);

        // 1 and 2 char tokens
        mnemonics.Add("_FRSL", 30);
        mnemonics.Add("_STAR", 31);
        mnemonics.Add("_PLUS", 32);
        mnemonics.Add("_DASH", 33);
        mnemonics.Add("_FPAR", 34);
        mnemonics.Add("_BPAR", 35);
        mnemonics.Add("_SEMI", 36);
        mnemonics.Add("_CEQA", 37);
        mnemonics.Add("_GRTR", 38);
        mnemonics.Add("_LESS", 39);
        mnemonics.Add("_GREQ", 40);
        mnemonics.Add("_LEEQ", 41);
        mnemonics.Add("_EQUA", 42);
        mnemonics.Add("_COMP", 43);
        mnemonics.Add("_COMA", 44);
        mnemonics.Add("_FBRC", 45);
        mnemonics.Add("_BBRC", 46);
        mnemonics.Add("_COLO", 47);
        mnemonics.Add("_PERI", 48);

        // types
        mnemonics.Add("_IDNT", 50);
        mnemonics.Add("_INTG", 51);
        mnemonics.Add("_FLOA", 52);
        mnemonics.Add("_STRN", 53);

        // unknown
        mnemonics.Add("UNKWN", 99);
    }

    // Character category for alphabetic chars, upper and lower case
    private boolean isLetter(char ch) {
        return (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z')));
    }

    // Character category for 0..9
    private boolean isDigit(char ch) {
        return ((ch >= '0') && (ch <= '9'));
    }

    // Category for any whitespace to be skipped over
    // space, tab, and newline
    private boolean isWhitespace(char ch) {
        return ((ch == ' ') || (ch == '\t') || (ch == '\n'));
    }

    // Returns the VALUE of the next character without removing it from the
    //    input line.  Useful for checking 2-character tokens that start with
    //    a 1-character token.
    private char PeekNextChar() {
        char result = ' ';
        if ((needLine) || (EOF)) {
            result = ' '; //at end of line, so nothing
        } else //
        {
            if ((linePos + 1) < line.length()) { //have a char to peek
                result = line.charAt(linePos + 1);
            }
        }
        return result;
    }

    // Called by GetNextChar when the characters in the current line
    // buffer string (line) are used up.
    private void GetNextLine() {
        try {
            line = bufferedreader.readLine();  //returns a null string when EOF
            if ((line != null) && (echo)) {
                lineCount++;
                System.out.println(String.format("%04d", lineCount) + " " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line == null) {    // The readLine returns null at EOF, set flag
            EOF = true;
        }
        linePos = -1;      // reset vars for new line if we have one
        needLine = false;  // we have one, no need
        //the line is ready for the next call to get a char with GetNextChar
    }

    // Returns the next character in the input file, returning a
    // /n newline character at the end of each input line or at EOF
    public char GetNextChar() {
        char result;
        if (needLine) //ran out last time we got a char, so get a new line
        {
            GetNextLine();
        }
        //try to get char from line buff
        // if EOF there is no new char, just return endofline, DONE
        if (EOF) {
            result = '\n';
            needLine = false;
        } else {
            // if there are more characters left in the input buffer
            if ((linePos < line.length() - 1)) { //have a character available
                linePos++;
                result = line.charAt(linePos);
            } else {
                //need a new line, but want to return eoln on this call first
                result = '\n';
                needLine = true; //will read a new line on next GetNextChar call
            }
        }
        return result;
    }

    // skips over any comment as if it were whitespace, so it is ignored
    public char skipComment(char curr) {
        // if this is the start of a comment...
        if (curr == comment_start1) {
            curr = GetNextChar();
            // loop until the end of comment or EOF is reached
            while ((curr != comment_end1) && (!EOF)) {
                curr = GetNextChar();
            }
            // if the file ended before the comment terminated
            if (EOF) {
                System.out.println("WARNING: Comment not terminated before End Of File");
            } else {
                // keep getting the next char
                curr = GetNextChar();
            }
        } else {
            // this is for the 2-character comment start, different start/end
            if ((curr == comment_start2) && (PeekNextChar() == comment_startend)) {
                curr = GetNextChar(); // get the second
                curr = GetNextChar(); // into comment or end of comment
                //while comment end is not reached
                while ((!((curr == comment_startend) && (PeekNextChar() == comment_end2))) && (!EOF)) {
                    curr = GetNextChar();
                }
                // EOF before comment end
                if (EOF) {
                    System.out.println("WARNING: Comment not terminated before End Of File");
                } else {
                    curr = GetNextChar();          //must move past close
                    curr = GetNextChar();          //must get following
                }
            }

        }
        return (curr);
    }

    //reads past any white space, blank lines, comments
    public char skipWhiteSpace() {

        do {
            while ((isWhitespace(currCh)) && (!EOF)) {
                currCh = GetNextChar();
            }
            currCh = skipComment(currCh);
        } while (isWhitespace(currCh) && (!EOF));
        return currCh;
    }

    // returns TRUE if ch is a prefix to a 2-character token like := or <=
    private boolean isPrefix(char ch) {
        return ((ch == ':') || (ch == '<') || (ch == '>'));
    }

    // returns TRUE if ch is the string delimiter
    private boolean isStringStart(char ch) {
        return ch == '"';
    }


    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    /* ---------------------------------
     * -LEXICAL STUDENT CREATED METHODS-
     * -------------------------------- */
    // identifiers
    private token getIdent() {

        // initialize token
        token result = new token();
        result.lexeme = "";

        // identifier consisting of letter, digit, |, or _
        while (isLetter(currCh) || isDigit(currCh) || currCh == '|' || currCh == '_'){
            result.lexeme += currCh;
            currCh = GetNextChar();
        }

        // check if reserve word, get code if so
        result.code = reserveWords.LookupName(result.lexeme);
        if (result.code == reserveWords.notFound){
            result.code = IDENT_ID;
        }

        // return resulting token
        return result;

    } // getIdent


    // Get Number is an integer or double (can also be E form)
    /* <digit>+[.<digit>*[E[+|-]<digit>+]] */
    private token getNumber() {

        // initialize token
        token result = new token();
        result.lexeme = "";

        /* --------START OF REGEX---------- */
        // <digit>+ (is 1 or more because first read has to be digit to enter getNumber())
        while (isDigit(currCh)){
            result.lexeme += currCh;
            currCh = GetNextChar();
        }

        // [.<digit>*
        if (currCh == '.') {
            result.lexeme += currCh;
            currCh = GetNextChar();
            while (isDigit(currCh)) {
                result.lexeme += currCh;
                currCh = GetNextChar();
            }

            // [E
            if (Character.toLowerCase(currCh) == 'e'){
                result.lexeme += currCh;
                currCh = GetNextChar();

                // [+|-]
                if (currCh == '+' | currCh == '-') {
                    result.lexeme += currCh;
                    currCh = GetNextChar();

                }

                    // <digit>+]] (need if to get first digit of the 1 or more)
                    if (isDigit(currCh)){
                        result.lexeme += currCh;
                        currCh = GetNextChar();
                        while (isDigit(currCh)){
                            result.lexeme += currCh;
                            currCh = GetNextChar();
                        }

                    // ERROR - expected one digit after [+|-]
                    }

            } // [E
        } // [.<digit>*
        /* --------END OF REGEX---------- */

        // check resulting lexeme and set token code
        if (integerOK(result.lexeme)){
            result.code = INTEGER_ID;
        } else if (doubleOK(result.lexeme)){
            result.code = FLOAT_ID;
        } else {
            // error - place unknown ID.
            result.code = UNKNOWN_ID;
        }

        // return token
        return result;

    } // getNumber()



    // get string needs to skip the first and last to remove quotations from a string
    private token getString() {

        // initialize token
        token result = new token();
        result.lexeme = "";

        // need to skip past first quote
        currCh = GetNextChar();

        while (currCh != '"'){
            result.lexeme += currCh;
            currCh = GetNextChar();

            // error - if unending string is found.
            if (currCh == '\n'){
                System.out.println("WARNING: Unterminated string found.");
                result.code = UNKNOWN_ID;
                return result;
            }
        }

        // need to skip past last quote
        currCh = GetNextChar();

        // set as stringID in the case that string is closed
        result.code = STRING_ID;

        // return token
        return result;
    }


    // Everything that is not above runs getOneTwoChar. Even unknown characters come here.
    private token getOneTwoChar() {

        // initialize token
        token result = new token();
        result.lexeme = "";

        // Two Char Token
        if (isPrefix(currCh)){
            if ((PeekNextChar() == '=') || (PeekNextChar() == '>')){
                result.lexeme += currCh;
                currCh = GetNextChar();
                result.lexeme += currCh;
                currCh = GetNextChar();

            // single character (but could have been prefix)
            } else {
                result.lexeme += currCh;
                currCh = GetNextChar();
            }

        // one-char token (known and unknown)
        } else {
            result.lexeme += currCh;
            currCh = GetNextChar();
        }

        // check if reserve word single token, get code if so
        result.code = reserveWords.LookupName(result.lexeme);
        if (result.code == reserveWords.notFound){
            result.code = UNKNOWN_ID;
        }

        return result;
    }

    // checks if the token needs to be truncated.
    public token checkTruncate(token result) {
        // truncate long lexemes, validate doubles and integers
        // handle appropriate types and add to symbol table as needed
        switch (result.code) {
            case IDENT_ID:
                if (result.lexeme.length() > MAX_IDENT){
                    result.lexeme = result.lexeme.substring(0, MAX_IDENT);
                    truncated = true;
                    System.out.println("WARNING: Identifier Truncated, greater than length 30. New: " + result.lexeme);
                }
                break;
            case INTEGER_ID:
            case FLOAT_ID:
                if (result.lexeme.length() > MAX_NUMBER){
                    result.lexeme = result.lexeme.substring(0, MAX_NUMBER);
                    System.out.println("WARNING: Identifier Truncated, greater than length 9. New: " + result.lexeme);
                    truncated = true;
                }
                break;
            case STRING_ID:
                // max string length? Not implemented (not in assignment description)
                break;
            default:
                break; // default, don't add - no truncation needed.
        }
        return result;
    }
    // END OF STUDENT CREATED METHODS
    //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    // Checks to see if a string contains a valid DOUBLE
    public boolean doubleOK(String stin) {
        boolean result;
        Double x;
        try {
            x = Double.parseDouble(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // Checks the input string for a valid INTEGER
    public boolean integerOK(String stin) {
        boolean result;
        int x;
        try {
            x = Integer.parseInt(stin);
            result = true;
        } catch (NumberFormatException ex) {
            result = false;
        }
        return result;
    }

    // given a mnemonic, find its token code value
    public int codeFor(String mnemonic) {
        return mnemonics.LookupName(mnemonic);
    }
    // given a mnemonic, return its reserve word
    public String reserveFor(String mnemonic) {
        return reserveWords.LookupCode(mnemonics.LookupName(mnemonic));
    }



    /* ----------------------
     *  MAIN LEXICAL METHOD *
     * -------------------- */
    public token GetNextToken() {
        // init truncated flag and get token
        truncated = false;
        token result = new token();

        currCh = skipWhiteSpace();
        if (isLetter(currCh)) { //is ident
            result = getIdent();
        } else if (isDigit(currCh)) { //is numeric
            result = getNumber();
        } else if (isStringStart(currCh)) { //string literal
            result = getString();
        } else {
            // default character check
            result = getOneTwoChar();
        }

        if ((result.lexeme.equals("")) || (EOF)) {
            result = null;
        }


        if (result != null) {

            // set the mnemonic
            result.mnemonic = mnemonics.LookupCode(result.code);

            // truncate if needed
            result = checkTruncate(result);

            // STUDENT CREATED SYMBOL TABLE BELOW.
            // set symbol table
            if (result.code == IDENT_ID){
                saveSymbols.AddSymbol(result.lexeme, 'V', 0);
            } else if (result.code == INTEGER_ID){
                if (truncated){
                    saveSymbols.AddSymbol(result.lexeme, 'C', 0);
                } else {
                    saveSymbols.AddSymbol(result.lexeme, 'C', Integer.parseInt(result.lexeme));
                }
            } else if (result.code == FLOAT_ID){
                if (truncated){
                    saveSymbols.AddSymbol(result.lexeme, 'C', 0.0);
                } else {
                    saveSymbols.AddSymbol(result.lexeme, 'C', Double.parseDouble(result.lexeme));
                }
            } else if (result.code == STRING_ID){
                saveSymbols.AddSymbol(result.lexeme, 'C', result.lexeme);
            }

            // print token if print is on
            if (printToken) {
                System.out.println("\t" + result.mnemonic + " | \t" + String.format("%04d", result.code) + " | \t" + result.lexeme);
            }
        }

        // result is next token.
        return result;

    } // getNextToken

} // lexical
