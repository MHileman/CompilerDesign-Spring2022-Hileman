/*
 * Quad Object: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 3 Feb 2022
 * Purpose: The quad object is for the quad table class. A simple 4-size array that contains: opcode, op1, op2, op3.
 */

package ADT;

/* --------------------------------
 * -------- QUAD OBJ CLASS  -------
 * ----- PART 1, ASSIGNMENT 2 -----
 * -------------------------------- */
public class QuadObj {

    // elements
    int[] opArray = new int[4];

    // constructor
    public QuadObj(int inputOpcode, int inputOp1, int inputOp2, int inputOp3){
        opArray[0] = inputOpcode;
        opArray[1] = inputOp1;
        opArray[2] = inputOp2;
        opArray[3] = inputOp3;
    }
}
