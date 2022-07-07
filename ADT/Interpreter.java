/*
 * Interpreter: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 29 April 2022
 *      UPDATE 1: Edited READ / WRITE to use op3 instead of op1.
 * Purpose: Interpret a quadtable with a symbol table and an op table. Implementation of op codes.
 */

package ADT;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/* --------------------------------
 * ---------- INTERPRETER ---------
 * ----- PART 1, ASSIGNMENT 3 -----
 * -------------------------------- */
public class Interpreter {

    // constants
    static int MAX_RESERVE = 40;
    static int MAX_QUAD = 1000;
    // vars
    ReserveTable optable;

    // constructor - initialize reserve table using initReserve
    public Interpreter() {
        // create optable with op codes (see function)
        optable = new ReserveTable(MAX_RESERVE);
        initReserve(optable);
    }

    /* -----------------------------
     * Main Interpreter Function
     * -------------------------- */
    public void InterpretQuads(QuadTable qtable, SymbolTable stable, boolean TraceOn, String filename) {

        // setup
        int pc, opcode, op1, op2, op3;
        String opname;
        pc = 0;
        PrintWriter writer;

        // Create Writer
        try {
            writer = new PrintWriter(filename, "UTF-8");

            // check if print is on
            if (TraceOn) {
                // headers
                writer.println("Interpreter Output (created by Matthew Hileman SP2022)");
                writer.println("----------------------------------------------------------------");
                System.out.println("Interpreter Output (created by Matthew Hileman SP2022)");
                System.out.println("----------------------------------------------------------------");
            }

        // error handling: writing to file error.
        } catch (IOException e) {
            System.out.println("An error occurred. Could not create file: " + filename);
            e.printStackTrace();
            return;
        }


        // main interpreter loop
        // will end when MAX_QUAD is reach or stop is encountered
        while (pc < MAX_QUAD){

            // get quad data at current PC
            opcode = qtable.GetQuad(pc, 0);
            opname = optable.LookupCode(opcode);
            op1 = qtable.GetQuad(pc, 1);
            op2 = qtable.GetQuad(pc, 2);
            op3 = qtable.GetQuad(pc, 3);

            if (TraceOn){
                writer.println(makeTraceString(pc, opcode, op1, op2, op3));
                System.out.println(makeTraceString(pc, opcode, op1, op2, op3));
            }

            // ERROR CHECK, make sure the opcode is valid
            if (opname.equals("")){
                System.out.println("opcode error! No such opcode: " + opcode + ", at PC: " + pc);
                System.out.println("Stopping interpreter!\n");
                return;
            } else {
                // Main switch statement. Checks for op.
                switch (opname.toUpperCase()){

                    // MATH: DIV, MUL, SUB, ADD:
                    case "DIV":
                        stable.UpdateSymbol(op3, stable.GetKind(op3), stable.GetInteger(op1)
                                / stable.GetInteger(op2));
                        pc++;
                        break;

                    case "MUL":
                        stable.UpdateSymbol(op3, stable.GetKind(op3), stable.GetInteger(op1)
                                * stable.GetInteger(op2));
                        pc++;
                        break;

                    case "SUB":
                        stable.UpdateSymbol(op3, stable.GetKind(op3), stable.GetInteger(op1)
                                - stable.GetInteger(op2));
                        pc++;
                        break;

                    case "ADD":
                        stable.UpdateSymbol(op3, stable.GetKind(op3), stable.GetInteger(op1)
                                + stable.GetInteger(op2));
                        pc++;
                        break;


                    // DATA STORAGE: MOVE
                    case "MOV":
                        stable.UpdateSymbol(op3, stable.GetKind(op3), stable.GetInteger(op1));
                        pc++;
                        break;


                    // UTILITY: PRINT, READ
                    case "PRINT":
                        if (stable.GetDataType(op3) == 'I') {
                            System.out.println(stable.GetSymbol(op3) + " = " + stable.GetInteger(op3));
                        } else if (stable.GetDataType(op3) == 'S'){
                            System.out.println(stable.GetString(op3));
                        } else if (stable.GetDataType(op3) == 'F'){
                            System.out.println(stable.GetSymbol(op3) + " = " + stable.GetFloat(op3));
                        }
                        pc++;
                        break;

                    case "READ":
                        // need to create scanner
                        int temp_int;
                        Scanner userInput = new Scanner(System.in);
                        System.out.print("Enter an integer value into '" + stable.GetSymbol(op3) + "': ");

                        // get input and make sure it is an int
                        try {
                            temp_int = userInput.nextInt();
                        } catch (Exception e) {
                            System.out.println("ERROR: You did not enter an int! Ignoring input.");
                            pc++;
                            break;
                        }
                        userInput.close();

                        // assign to user input
                        stable.UpdateSymbol(op3, stable.GetKind(op3), temp_int);
                        System.out.println("Integer accepted! " + stable.GetSymbol(op3)
                                + " is now " + stable.GetInteger(op3) );
                        pc++;
                        break;


                    // BRANCHES: JMP, JZ, JP, JN, JNZ, JNP, JNN, JINDR
                    case "JMP":
                        pc = op3;
                        break;

                    case "JZ":
                        if (stable.GetInteger(op1) == 0){ pc = op3; } else { pc++; }
                        break;

                    case "JP":
                        if (stable.GetInteger(op1) > 0){ pc = op3; } else { pc++; }
                        break;

                    case "JN":
                        if (stable.GetInteger(op1) < 0){ pc = op3; } else { pc++; }
                        break;

                    case "JNZ":
                        if (stable.GetInteger(op1) != 0){ pc = op3; } else { pc++; }
                        break;

                    case "JNP":
                        if (stable.GetInteger(op1) <= 0){ pc = op3; } else { pc++; }
                        break;

                    case "JNN":
                        if (stable.GetInteger(op1) >= 0){ pc = op3; } else { pc++; }
                        break;

                    case "JINDR":
                        pc = stable.GetInteger(op3);
                        break;


                    // TERMINATE: STOP
                    case "STOP":
                        writer.println("Execution terminated by program STOP.");
                        System.out.println("Execution terminated by program STOP.");
                        pc = MAX_QUAD;
                        break;

                    // ERROR CASE: UNRECOGNIZED
                    default:
                        writer.println("ERROR: Unrecognized opname: " + opname);
                        System.out.println("ERROR: Unrecognized opname: " + opname);
                        return;

                } // switch
            } // else
        } // while

        // Close and footer
        if (TraceOn){
            // footer
            writer.println("----------------------------------------------------------------");
            System.out.println("----------------------------------------------------------------");
            // be sure to close the file's I/O!
            writer.flush();
            writer.close();
        }

    } // InterpretQuads

    // initialization of reserve table (different from the provided)
    private void initReserve(ReserveTable optable){
        optable.Add("STOP", 0);
        optable.Add("DIV", 1);
        optable.Add("MUL", 2);
        optable.Add("SUB", 3);
        optable.Add("ADD", 4);
        optable.Add("MOV", 5);
        optable.Add("PRINT", 6);
        optable.Add("READ", 7);
        optable.Add("JMP", 8);
        optable.Add("JZ", 9);
        optable.Add("JP", 10);
        optable.Add("JN", 11);
        optable.Add("JNZ", 12);
        optable.Add("JNP", 13);
        optable.Add("JNN", 14);
        optable.Add("JINDR", 15);
    }

    // factorial test - hard coded data to test the InterpretQuads function
    public boolean initializeFactorialTest(SymbolTable stable, QuadTable qtable){

        // symbol table add symbols
        stable.AddSymbol("n", 'V', 10);
        stable.AddSymbol("i", 'V', 0);
        stable.AddSymbol("product", 'V', 0);
        stable.AddSymbol("1", 'C', 1);
        stable.AddSymbol("$temp", 'V', 0);

        // quad table add quads
        qtable.AddQuad(optable.LookupName("MOV"), 3, 0, 2);     // prod = 1
        qtable.AddQuad(optable.LookupName("MOV"), 3, 0, 1);     // i = 1
        qtable.AddQuad(optable.LookupName("SUB"), 1, 0, 4);     // temp = i-n
        qtable.AddQuad(optable.LookupName("JP"), 4, 0, 7);      // if temp >=0, branch to quad index 7
        qtable.AddQuad(optable.LookupName("MUL"), 2, 1, 2);     // product = product * i
        qtable.AddQuad(optable.LookupName("ADD"), 1, 3, 1);     // i = i + 1
        qtable.AddQuad(optable.LookupName("JMP"), 0, 0, 2);     // jump to start of loop
        qtable.AddQuad(optable.LookupName("PRINT"), 2, 0, 0);   // print product
        qtable.AddQuad(optable.LookupName("STOP"), 0, 0, 0);    // stop

        // why are returning bool here?
        return true;
    }

    // same at factor test but using sums instead of mult
    public boolean initializeSummationTest(SymbolTable stable, QuadTable qtable){

        // symbol table add symbols
        stable.AddSymbol("n", 'V', 10);
        stable.AddSymbol("i", 'V', 0);
        stable.AddSymbol("product", 'V', 0);
        stable.AddSymbol("1", 'C', 1);
        stable.AddSymbol("$temp", 'V', 0);
        stable.AddSymbol("0", 'C', 0);

        // quad table add quads
        qtable.AddQuad(optable.LookupName("MOV"), 5, 0, 2);     // prod = 1
        qtable.AddQuad(optable.LookupName("MOV"), 3, 0, 1);     // i = 1
        qtable.AddQuad(optable.LookupName("SUB"), 1, 0, 4);     // temp = i-n
        qtable.AddQuad(optable.LookupName("JP"), 4, 0, 7);      // if temp >=0, branch to quad index 7
        qtable.AddQuad(optable.LookupName("ADD"), 2, 1, 2);     // product = product + i
        qtable.AddQuad(optable.LookupName("ADD"), 1, 3, 1);     // i = i + 1
        qtable.AddQuad(optable.LookupName("JMP"), 0, 0, 2);     // jump to start of loop
        qtable.AddQuad(optable.LookupName("PRINT"), 2, 0, 0);   // print product
        qtable.AddQuad(optable.LookupName("STOP"), 0, 0, 0);    // stop

        // why are returning bool here?
        return true;
    }

    // print row to make interpreter output
    private String makeTraceString(int pc, int opcode,int op1,int op2,int op3 ){
        String result = "";
        result = "PC = "+String.format("%04d", pc)+": "+(optable.LookupCode(opcode)+"       ").substring(0,6)
                +String.format("%02d",op1)+
                ", "+String.format("%02d",op2)+", "+String.format("%02d",op3);
        return result;
    }

}
