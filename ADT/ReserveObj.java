/*
 * Reserve Object: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 24 Jan 2022
 * Purpose: The operation class is an object that is used in the ReserveTable class as the rows in the reserve table.
 *          Instead of different arrays for the opcdoe and the opname, they will be stored in the same array in
 *          this object.
 */

package ADT;

/* --------------------------------
 * ------ RESERVE OBJ CLASS  ------
 * ----- PART 1, ASSIGNMENT 1 -----
 * -------------------------------- */
public class ReserveObj {

    // elements
    String name;
    int code;

    // constructor
    public ReserveObj(String inputName, int inputCode){
        name = inputName;
        code = inputCode;
    }
}
