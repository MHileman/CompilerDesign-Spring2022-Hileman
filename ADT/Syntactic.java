/*
 * Lexical Analyzer: Syntactic Class File
 * Created by: Matthew Hileman
 * Last Updated: 29 April 2022
 * Purpose: To correctly parse the token order from lexical using the given language below.
 */

/*  --------------------------
 *   ***** LANGUAGE CFG *****
 *  --------------------------
 * Notation: In the CFG below, the following conventions are used:
 *      1) Anything prefaced by a $ is a terminal token (symbol or reserved word); anything
 *          inside of <> pointy brackets is a non-terminal.
 *      2) An item enclosed in ‘[‘,’]’ square braces is optional
 *      3) An item enclosed in ‘{‘,’}’ curly braces is repeatable; ‘*’ is ‘0 or more times’, while ‘+’ is
 *          ‘1 or more times’.
 *      4) Vertical bars, ‘|’, are OR connectors; any one of the items they separate may be
 *          selected.
 *      5) Note that all named elements of the form $SOMETHING are token codes for terminals
 *          which are defined for this language and returned by the lexical analyzer.
* ------------------------------------------------------------------------------------------------
 * <program> -> $PROGRAM <prog-identifier> $SEMICOLON <block> $PERIOD
 * <block> -> $BEGIN <statement> {$SEMICOLON <statement>}* $END
 * <prog-identifier> -> <identifier>
 * <statement> -> <variable> $COLON-EQUALS <simple expression>
 * <variable> -> <identifier>
 * <simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
 * <addop> -> $PLUS | $MINUS
 * <sign> -> $PLUS | $MINUS
 * <term> -> <factor> {<mulop> <factor> }*
 * <mulop> -> $MULTIPLY | $DIVIDE
 * <factor> -> <unsigned constant> |
 *             <variable> |
 *             $LPAR  <simple expression> $RPAR
 * <unsigned constant> -> <unsigned number>
 * <unsigned number> -> $FLOAT | $INTEGER (Token codes 51 only - int)
 * <identifier> -> $IDENTIFIER (Token code 50)
 *-------------------------------
 * NEW ADDITIONS (FINAL PROJECT COMBINED WITH 3B):
 *-------------------------------
 * <block> -> <block-body>
 * <block-body> -> $BEGIN <statement> {$SCOLN <statement>} $END
 *
 * <statement>-> [
 *      <block-body> |
 *      <variable> $ASSIGN <simple expression> |
 *      $IF <relexpression> $THEN <statement> [$ELSE <statement>] |
 *      $WHILE <relexpression> $DO <statement> |
 *      $PRINTLN $LPAR (<simple expression> |
 *      <stringconst>) $RPAR $READN  $LPAR <variable>  $RPAR
 * ]+
 *
 * <relexpression> -> <simple expression> <relop> <simple expression>
 * <relop> -> $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
 *
 * <stringconst> -> $STRINGTYPE (Token code 53)
 */

package ADT;

/* --------------------------------
 * ------- SYNTACTIC CLASS  -------
 * ----- PART 3, ASSIGNMENT A -----
 * ------------ PART 4 ------------
 * -------------------------------- */
public class Syntactic {

    private String filein;              //The full file path to input file
    private SymbolTable symbolList;     //Symbol table storing ident/const
    private QuadTable quads;            //Quads Table for final output
    private Interpreter interp;         //Interpreter
    private Lexical lex;                //Lexical analyzer 
    private Lexical.token token;        //Next Token retrieved
    private boolean traceon;            //Controls tracing mode 
    private int level = 0;              //Controls indent for trace mode
    private int temp_count;         //Controls temp naming
    private boolean anyErrors;          //Set TRUE if an error happens

    private final int symbolSize = 250;
    private final int quadSize = 1500;

    private int Minus1Index;
    private int Plus1Index;

    // constructor
    public Syntactic(String filename, boolean traceOn) {

        filein = filename;
        traceon = traceOn;

        // for storing newly created program symbols
        symbolList = new SymbolTable(symbolSize);

        // Minus1Index and Plus1Index are preloaded for the locations of symbols -1 and 1 in S.T.
        Minus1Index = symbolList.AddSymbol("-1", symbolList.constantkind, -1);
        Plus1Index = symbolList.AddSymbol("1", symbolList.constantkind, 1);


        // initialize quads
        quads = new QuadTable(quadSize);
        // initialize interpreter
        interp = new Interpreter();
        // initialize lexical
        lex = new Lexical(filein, symbolList, true);
        lex.setPrintToken(traceOn);

        // initialize error flag
        anyErrors = false;

        // initialize temp names
        temp_count = 0;
    }

    // The interface to the syntax analyzer, initiates parsing
    // Uses variable RECUR to get return values throughout the non-terminal methods
    public void parse() {

        // make filename pattern for symbol table and quad table output later
        String filenameBase = filein.substring(0, filein.length() - 4);
        System.out.println(filenameBase);
        int recur = 0;

        // prime the pump
        token = lex.GetNextToken();

        // call PROGRAM
        recur = Program();

        // done, add a final STOP quad
        quads.AddQuad(interp.optable.LookupName("STOP"), 0, 0, 0);

        // print symbol table and quad before execute
        symbolList.PrintSymbolTable(filenameBase + "ST-before.txt");
        quads.PrintQuadTable(filenameBase + "QUADS.txt");

        //interpret
        if (!anyErrors) {
            // set TraceOn here to true to print interpreter to file.
            interp.InterpretQuads(quads, symbolList, false, filenameBase + "TRACE.txt");
        } else {
            System.out.println("Errors, unable to run program.");
        }
        symbolList.PrintSymbolTable(filenameBase + "ST-after.txt");

    }

    /* ------------------------------------------------------------------
     * NON TERMINALS BELOW - MAKE UP THE MAJORITY OF SYNTACTIC
     * ------------------------------------------------------------------
     * Non-terminals are the entire program's flow - and call each other, returning -1 if error.
     */

    // Non Terminal
    // <program> -> $PROGRAM <prog-identifier> $SEMICOLON <block> $PERIOD
    private int Program() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        // check for prog, semi, perd
        trace("Program", true);
        if (token.code == lex.codeFor("PROGR")) {
            token = lex.GetNextToken();
            recur = ProgIdentifier();
            if (token.code == lex.codeFor("_SEMI")) {
                token = lex.GetNextToken();
                recur = Block();
                if (token.code == lex.codeFor("_PERI")) {
                    if (!anyErrors) {
                        System.out.println("Success.");
                    } else {
                        System.out.println("Compilation failed.");
                    }
                } else {
                    error(lex.reserveFor("_PERI"), token.lexeme);
                }
            } else {
                error(lex.reserveFor("_SEMI"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("PROGR"), token.lexeme);
        }
        trace("Program", false);
        return recur;
    }

    // Non Terminal
    // <block> -> <block-body>
    private int Block() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Block", true);
        recur = BlockBody();
        trace("Block", false);
        return recur;
    }

    // Non Terminal
    // <block-body> -> $BEGIN <statement> {$SEMICOLON <statement>}* $END
    private int BlockBody() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("BlockBody", true);
        if (token.code == lex.codeFor("BEGIN")) {
            token = lex.GetNextToken();
            recur = Statement();
            while ((token.code == lex.codeFor("_SEMI")) && (!lex.EOF()) && (!
                    anyErrors)) {
                token = lex.GetNextToken();
                recur = Statement();
            }
            if (token.code == lex.codeFor("END__")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("END__"), token.lexeme);
            }
        } else {
            error(lex.reserveFor("BEGIN"), token.lexeme);
        }
        trace("BlockBody", false);

        return recur;
    }

    // Non Terminal
    // <prog-identifier> -> <identifier>
    private int ProgIdentifier() {
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("ProgIdentifier", true);
        // This non-term is used to uniquely mark the program identifier
        if (token.code == lex.codeFor("_IDNT")) {
            // Because this is the progIdentifier, it will get a 'p' type to
            // prevent re-use as a var
            symbolList.UpdateSymbol(symbolList.LookupSymbol(token.lexeme), 'P', 0);

            // move on
            token = lex.GetNextToken();

        } else {
            error("<PROG_IDENTIFIER>", token.lexeme);
        }
        trace("ProgIdentifier", false);
        return recur;
    }

    // Non Terminal
    /*  * <statement>-> [
     *      <block-body> |
     *      <variable> $ASSIGN <simple expression> |
     *      $IF <relexpression> $THEN <statement> [$ELSE <statement>] |
     *      $WHILE <relexpression> $DO <statement> |
     *      $PRINTLN $LPAR (<simple expression> | <stringconst>)  $RPAR |
     *      $READN  $LPAR <variable>  $RPAR
     * ]+
     */
    private int Statement() {
        int recur = 0;

        if (anyErrors) {
            return -1;
        }
        trace("Statement", true);

        // block-body
        if (token.code == lex.codeFor("BEGIN")) {
            recur = BlockBody();

        // assignment
        } else if (token.code == lex.codeFor("_IDNT")) {
            recur = handleAssignment();

        // if/then
        } else if (token.code == lex.codeFor("IF___")) {
            recur = handleIf();

        // while
        } else if (token.code == lex.codeFor("DOWHI")) {
            recur = handleWhile();

        // print
        } else if (token.code == lex.codeFor("PRINT")) {
            recur = handlePrint();

        // read
        } else if (token.code == lex.codeFor("READL")){
            recur = handleRead();

        // error case - not a statement
        } else {
                error("Start of Statement", token.lexeme);
        }

        trace("Statement", false);
        return recur;
    }

    // Not a NT - statement extender
    // <variable> $COLON-EQUALS <simple expression>
    private int handleAssignment() {
        int recur = 0;
        int left, right;

        if (anyErrors) {
            return -1;
        }
        trace("handleAssignment", true);

        // Variable moves ahead, set left to index of that variable (symbolTable)
        left = Variable();

        // check for assignment operator
        if (token.code == lex.codeFor("_CEQA")) {
            token = lex.GetNextToken();

            // SimpleExpression moves ahead, set right to result's index
            right = SimpleExpression();
            quads.AddQuad(interp.optable.LookupName("MOV"), right, 0, left);
        } else {
            error(lex.reserveFor("_CEQA"), token.lexeme);
        }
        trace("handleAssignment", false);
        return recur;
    }

    // Not a NT - statement extender
    // $IF <relexpression> $THEN <statement> [$ELSE <statement>]
    private int handleIf() {
        int recur = 0;
        int branchQuad, patchElse = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleIf", true);

        // $IF already read from Statement(). Get next!
        token = lex.GetNextToken();

        // <relexpression>
        branchQuad = RelExpression();

        // $THEN
        if (token.code == lex.codeFor("THEN_")) {

            // move past $THEN
            token = lex.GetNextToken();

            // <statement>
            recur = Statement();

            // [$ELSE <statement>]
            if (token.code == lex.codeFor("ELSE_")){

                // move past $ELSE
                token = lex.GetNextToken();

                // save back-fill quad to jump (unknown right now)
                patchElse = quads.NextQuad();
                quads.AddQuad(interp.optable.LookupName("JMP"), 0, 0, 0);

                // conditional jump
                quads.setQuadOp3(branchQuad, quads.NextQuad());

                // else body
                recur = Statement();

                // change target location
                quads.setQuadOp3(patchElse, quads.NextQuad());

            // no else present, fix IF branch location
            } else {
                quads.setQuadOp3(branchQuad, quads.NextQuad());
            }

        // error - expected THEN
        } else {
            error(lex.reserveFor("THEN_"), token.lexeme);
        }

        trace("handleIf", false);
        return recur;
    }

    // Not a NT - statement extender
    // $DOWHILE <relexp> <statement>
    private int handleWhile() {
        int recur = 0;
        int saveTop, branchQuad;
        if (anyErrors) {
            return -1;
        }
        trace("handleWhile", true);

        // $DOWHILE already read from Statement(). Get next!
        token = lex.GetNextToken();

        // need to save the top of loop.
        saveTop = quads.NextQuad();

        // <relexpression>
        branchQuad = RelExpression();

        // <statement>
        recur = Statement();

        // addquad
        quads.AddQuad(interp.optable.LookupName("JMP"), 0, 0, saveTop);  // jump to top of loop
        quads.setQuadOp3(branchQuad, quads.NextQuad());

        trace("handleWhile", false);
        return recur;
    }

    // Not a NT - statement extender
    // $PRINTLN $LPAR (<simple expression> | <stringconst> | <identifier>)  $RPAR
    private int handlePrint() {
        int recur = 0;
        int toprint = 0;
        if (anyErrors) {
            return -1;
        }

        trace("handlePrint", true);
        // $PRINTLN already read from Statement(). Get next!
        token = lex.GetNextToken();

        // $LPAR
        if (token.code == lex.codeFor("_FPAR")) {
            token = lex.GetNextToken();

            // (<simple expression> | <stringconst>)
            // also apparently | <simple expression>
            if (token.code == lex.codeFor("_PLUS") || token.code == lex.codeFor("_DASH")
                    || token.code == lex.codeFor("_INTG") || token.code == lex.codeFor("_FLOA")){
                toprint = SimpleExpression();

            // string constant
            } else if (token.code == lex.codeFor("_STRN")) {
                toprint = StringConst();

            // identifier
            } else if (token.code == lex.codeFor("_IDNT")){
                toprint = symbolList.LookupSymbol(token.lexeme);
                token = lex.GetNextToken();

            } else {
                error("<simple expression> or <string constant> or <identifier>", token.lexeme);
            }

            quads.AddQuad(interp.optable.LookupName("PRINT"), 0, 0, toprint);

            // $RPAR
            if (token.code == lex.codeFor("_BPAR")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("_BPAR"), token.lexeme);
            }

         // error - expected $LPAR
        } else {
            error(lex.reserveFor("_FPAR"), token.lexeme);
        }

        trace("handlePrint", false);
        return recur;
    }


    // Not a NT - statement extender
    // $READN  $LPAR <variable>  $RPAR
    private int handleRead() {
        int recur = 0;
        int toRead = 0;
        if (anyErrors) {
            return -1;
        }
        trace("handleRead", true);

        // $READN already read from Statement(). Get next!
        token = lex.GetNextToken();

        // $LPAR
        if (token.code == lex.codeFor("_FPAR")) {
            token = lex.GetNextToken();

            //<variable>
            toRead = Variable();

            // $RPAR
            if (token.code == lex.codeFor("_BPAR")) {
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("_BPAR"), token.lexeme);
            }

            quads.AddQuad(interp.optable.LookupName("READ"), 0, 0, toRead);

            // error - expected $LPAR
        } else {
            error(lex.reserveFor("_FPAR"), token.lexeme);
        }

        trace("handleRead", false);
        return recur;
    }


    // Non Terminal
    // <variable> -> <identifier>
    private int Variable(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Variable", true);

        // result = identifier
        if (token.code == lex.codeFor("_IDNT")) {

            // index of var from last getNext Call.
            recur = symbolList.LookupSymbol(token.lexeme);
            token = lex.GetNextToken();
        } else {
            error("<VARIABLE>", token.lexeme);
        }
        trace ("Variable", false);

        return recur;
    }

    // Non Terminal
    // <simple expression> -> [<sign>]  <term>  {<addop>  <term>}*
    private int SimpleExpression() {
        int left, right, signval, temp, opcode;
        signval = 0;

        if (anyErrors) {
            return -1;
        }

        trace("SimpleExpression", true);

        // [<sign>] (no error needed as its optional)
        if (token.code == lex.codeFor("_PLUS")
                || token.code == lex.codeFor("_DASH")) {

            // gets next token, set signal to -1 if negative, 1 otherwise
            signval = Sign();
        }

        // <term>
        left = Term();

        // add negation quad (if negative)
        if (signval == -1){
            quads.AddQuad(interp.optable.LookupName("MUL"), left, symbolList.LookupSymbol("Minus1Index"), left);
        }

        // {<addop>  <term>}*
        while (((token.code == lex.codeFor("_PLUS")) || (token.code == lex.codeFor("_DASH")))
                && (!lex.EOF()) && (!anyErrors)) {
            opcode = Addop();
            right = Term();

            // index of new temp variable
            temp = GenSymbol();

            // add quad
            quads.AddQuad(opcode, left, right, temp);

            // new leftmost is last result
            left = temp;
        }

        trace("SimpleExpression", false);

        return left;
    }


    // Non Terminal
    // <relexpression> -> <simple expression>  <relop>  <simple expression>
    private int RelExpression(){
        int left, right, saveRelop, result, temp;

        if (anyErrors) {
            return -1;
        }

        trace("RelExpression", true);
        /*
        To compare A and B using any relational operator, we simply need to get the result of A-B
            and then compare the result.
        */

        // <simple expression>  <relop>  <simple expression>
        left = SimpleExpression();      // A
        saveRelop = Relop();            // opcode of relational operator
        right = SimpleExpression();     // B

        temp = GenSymbol();             // temporary variable
        quads.AddQuad(interp.optable.LookupName("SUB"), left, right, temp);     // the A-B quad
        result = quads.NextQuad();      // NextQuad gets the index of the next available quad
        quads.AddQuad(relopToOpcode(saveRelop), temp, 0, 0);          // target unset

        trace ("RelExpression", false);
        // returns next available quad
        return result;
    }


    // Helper Function for RelExpression
    // returns opcode for quad given relop
    private int relopToOpcode(int relop){

        int result = -1;
        if (anyErrors) {
            return result;
        }

        /*
        Java does not like non-constant switch, so chained if else...
            would have to restructure interpreter to get it to work with switch.
        */
        // A = B, then JNZ is false branch
        if (relop == lex.codeFor("_EQUA")){
            result = interp.optable.LookupName("JNZ");
        // A <> B, then JZ is false branch
        } else if (relop == lex.codeFor("_COMP")){
            result = interp.optable.LookupName("JZ");
        // A < B, then JNN is false branch
        } else if (relop == lex.codeFor("_LESS")) {
            result = interp.optable.LookupName("JNN");
        // A > B, then JNP is false branch
        } else if (relop == lex.codeFor("_GRTR")) {
            result = interp.optable.LookupName("JNP");
        // A <= B, then JP is false branch
        } else if (relop == lex.codeFor("_LEEQ")) {
            result = interp.optable.LookupName("JP");
        // A >= B, then JN is false branch
        } else if (relop == lex.codeFor("_GREQ")) {
            result = interp.optable.LookupName("JN");
        }

        return result;
    }

    // Non Terminal
    // <relop> -> $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
    private int Relop(){
        int result = -1;
        if (anyErrors) {
            return result;
        }

        // $EQ | $LSS | $GTR | $NEQ | $LEQ | $GEQ
        trace("Relop", true);
        if (token.code == lex.codeFor("_EQUA") || token.code == lex.codeFor("_LESS") ||
                token.code == lex.codeFor("_GRTR") || token.code == lex.codeFor("_COMP") ||
                token.code == lex.codeFor("_GREQ") || token.code == lex.codeFor("_LEEQ")) {
            result = token.code;
            token = lex.GetNextToken();
        } else {
            error("relational operator", token.lexeme);
        }
        trace ("Relop", false);

        return result;
    }

    // Non Terminal
    // <addop> -> $PLUS | $MINUS
    private int Addop(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Addop", true);
        if (token.code == lex.codeFor("_PLUS")) {
            recur = interp.optable.LookupName("ADD");
            token = lex.GetNextToken();

        } else if (token.code == lex.codeFor("_DASH")) {
            recur = interp.optable.LookupName("SUB");
            token = lex.GetNextToken();

        } else {
            error(lex.reserveFor("_PLUS") + " or "
                    + lex.reserveFor("_DASH"), token.lexeme);
        }
        trace ("Addop", false);

        return recur;
    }

    private int Mulop(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Mulop", true);
        if (token.code == lex.codeFor("_FRSL")) {
            recur = interp.optable.LookupName("DIV");
            token = lex.GetNextToken();

        } else if (token.code == lex.codeFor("_STAR")){
            recur = interp.optable.LookupName("MUL");
            token = lex.GetNextToken();

        } else {
            error(lex.reserveFor("_FRSL") + " or "
                    + lex.reserveFor("_STAR"), token.lexeme);
        }
        trace ("Mulop", false);

        return recur;
    }

    // Non Terminal
    // <sign> -> $PLUS | $MINUS
    private int Sign(){
        int recur = 1;

        trace("Sign", true);

        if (token.code == lex.codeFor("_PLUS")){
            token = lex.GetNextToken();
            // return 1 if positive

        } else if (token.code == lex.codeFor("_DASH")) {
            token = lex.GetNextToken();
            // return -1 if negative
            recur = -1;

        } else {
            error(lex.reserveFor("_PLUS") + " or "
                    + lex.reserveFor("_DASH"), token.lexeme);
        }

        trace ("Sign", false);

        return recur;
    }

    // Non Terminal
    // <term> -> <factor> {<mulop> <factor> }*
    private int Term(){
        int left, right, opcode, temp;
        if (anyErrors) {
            return -1;
        }

        trace("Term", true);

        left = Factor();
        // mulop, repeated
        while (((token.code == lex.codeFor("_FRSL")) || (token.code == lex.codeFor("_STAR")))
                && (!lex.EOF()) && (!anyErrors)) {
            opcode = Mulop();
            right = Factor();

            // index of new temp variable
            temp = GenSymbol();

            // add quad
            quads.AddQuad(opcode, left, right, temp);

            // new leftmost is last result
            left = temp;
        }
        trace ("Term", false);

        return left;
    }

    // Non Terminal
    // <factor> -> <unsigned constant> | <variable> | $LPAR <simple expression> $RPAR
    private int Factor(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("Factor", true);
        // unsigned constant
        if ((token.code == lex.codeFor("_FLOA")) || (token.code == lex.codeFor("_INTG"))){
            recur = UnsignedConstant();
        // variable
        } else if (token.code == lex.codeFor("_IDNT")){
            recur = Variable();
        // simple expression
        } else if (token.code == lex.codeFor("_FPAR")){
            token = lex.GetNextToken();
            recur = SimpleExpression();

            if (token.code == lex.codeFor("_BPAR")){
                token = lex.GetNextToken();
            } else {
                error(lex.reserveFor("_BPAR"), token.lexeme);
            }
        // error case - none
        } else {
            error("constant, variable, or simple expression", token.lexeme);
        }
        trace ("Factor", false);

        return recur;
    }


    // None Terminal
    // <stringconst> -> $STRINGTYPE
    private int StringConst(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("StringConst", true);
        if (token.code == lex.codeFor("_STRN")) {
            recur = symbolList.LookupSymbol(token.lexeme);
            token = lex.GetNextToken();
        } else {
            error("<StringConst>", token.lexeme);
        }
        trace ("StringConst", false);

        return recur;
    }

    // Non Terminal
    // <unsigned constant> -> <unsigned number>
    private int UnsignedConstant(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("UnsignedConstant", true);
        recur = UnsignedNumber();
        trace ("UnsignedConstant", false);

        return recur;
    }

    // Non Terminal
    // <unsigned number> -> $FLOAT | $INTEGER
    private int UnsignedNumber(){
        int recur = 0;
        if (anyErrors) {
            return -1;
        }

        trace("UnsignedNumber", true);

        if ((token.code == lex.codeFor("_FLOA")) || (token.code == lex.codeFor("_INTG"))) {
            recur = symbolList.LookupSymbol(token.lexeme);
            token = lex.GetNextToken();
        } else {
            error("<FLOAT> or <INTEGER>", token.lexeme);
        }

        trace ("UnsignedNumber", false);

        return recur;
    }

    // generates a temporary variable in the symbol table, returns its index
    private int GenSymbol(){
        int recur;
        recur = symbolList.AddSymbol("@"+temp_count, 'V', 0);
        temp_count ++;
        return recur;
    }

    /**
     * *************************************************
     */
    /*     UTILITY FUNCTIONS USED THROUGHOUT THIS CLASS */

    // error provides a simple way to print an error statement to standard output and avoid reduncancy
    private void error(String wanted, String got) {
        anyErrors = true;
        System.out.println("ERROR: Expected " + wanted + " but found " + got);
    }

    // trace simply RETURNs if traceon is false; otherwise, it prints an
    // ENTERING or EXITING message using the proc string
    private void trace(String proc, boolean enter) {
        String tabs = "";
        if (!traceon) {
            return;
        }
        if (enter) {
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("--> Entering " + proc);
            level++;
        } else {
            if (level > 0) {
                level--;
            }
            tabs = repeatChar(" ", level);
            System.out.print(tabs);
            System.out.println("<-- Exiting " + proc);
        }
    }

    // repeatChar returns a string containing x repetitions of string s;
    // nice for making a varying indent format
    private String repeatChar(String s, int x) {
        int i;
        String result = "";
        for (i = 1; i <= x; i++) {
            result = result + s;
        }
        return result;
    }

} //Syntatic Class