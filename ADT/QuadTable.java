/*
 * Quad Table: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 3 Feb 2022
 * Purpose: The quad table is a list of opcodes and three ops that tell the compiler
 *          different information depending on the op.
 *          Consists of a single array that contains: opcode, op1, op2, op3.
 */

package ADT;

import java.io.IOException;
import java.io.PrintWriter;

/* --------------------------------
 * ------- QUAD TABLE CLASS  ------
 * ----- PART 1, ASSIGNMENT 2 -----
 * -------------------------------- */
public class QuadTable {

    // elements
    int elementCount;
    QuadObj[] quadArray;    //declaring array

    // Constructor
    public QuadTable(int maxSize){

        // error handling: make sure the max size is greater than 0 (would create an error when making an array!)
        try {
            // initialize reserve table (opArray) with Operation objects as the type.
            quadArray = new QuadObj[maxSize];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred. The max-size for the Symbol table cannot be less than 0!");
            e.printStackTrace();
        }
        // initialize how many elements are used in the Symbol Table to 0.
        elementCount = 0;
    }

    // returns the index of where the next available row is (important for code generation!)
    public int NextQuad(){
        return elementCount;
    }

    // adds quad to the quad table
    public void AddQuad(int opcode, int op1, int op2, int op3){

        // DON'T NEED ERROR CASE: index should never go out of bounds - if it does, java will throw error automatically.
        quadArray[elementCount] = new QuadObj(opcode, op1, op2, op3);
        elementCount++;
    }

    // returns int of a row and column from the quad table
    public int GetQuad(int index, int column){

        // ERROR CASES: if index is over or column is over
        if (index > elementCount){
            System.out.println("An error occurred. Given index in GetQuad is greater than element count!");
            return 0;
        } else if (column > 3){
            System.out.println("An error occurred. Given column in GetQuad is greater than 3 " +
                               "(there are 4 total quads)!");
            return 0;
        } else {
            // get the quad array (row)
            // get the opArray of that quad (column)
            return quadArray[index].opArray[column];
        }
    }

    // changes a quad at a given location (NEEDED AT CODE GENERATION)
    public void UpdateQuad(int index, int opcode, int op1, int op2, int op3){
        if (index <= elementCount) {
            quadArray[index].opArray[0] = opcode;
            quadArray[index].opArray[1] = op1;
            quadArray[index].opArray[2] = op2;
            quadArray[index].opArray[3] = op3;
        } else {
            System.out.println("An error occurred. UpdateQuad attempted on an empty quad row!");
        }
    }

    // similar to UpdateQuad, but only for op3.
    public void setQuadOp3(int quadIndex, int op3){
        if (quadIndex <= elementCount) {
            quadArray[quadIndex].opArray[3] = op3;
        } else {
            System.out.println("An error occurred. UpdateQuad attempted on an empty quad row!");
        }
    }

    // print symbol table into a file
    public void PrintQuadTable(String filename){
        // need to try and create and open a writer in case it fails.
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            // headers
            writer.printf("%s%n", "Quad Table (created by Matthew Hileman - UCCS CS4100, SP2022)");
            writer.println("----------------------------------------------------------------");
            writer.printf("%-8s| %-8s| %-8s| %-8s| %s%n", "Index", "opcode", "op1", "op2", "op3");
            writer.println("----------------------------------------------------------------");

            // write each element in tabular output format
            for (int i = 0; i < elementCount; i++) {
                writer.printf("%-8s| %-8s| %-8s| %-8s| %s%n", i, quadArray[i].opArray[0], quadArray[i].opArray[1],
                        quadArray[i].opArray[2], quadArray[i].opArray[3]);
            }

            // footer
            writer.println("----------------------------------------------------------------");

            // be sure to close the file's I/O!
            writer.close();

            // error handling: writing to file error.
        } catch (IOException e) {
            System.out.println("An error occurred. Could not create file: " + filename);
            e.printStackTrace();
        }
    } // PrintSymbolTable

}
