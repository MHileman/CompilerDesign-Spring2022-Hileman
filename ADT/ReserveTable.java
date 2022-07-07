/*
 * Reserve Table: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 24 Jan 2022
 * Purpose: The reserve table is a list of indexed names and codes that are used throughout the compiler.
 *          The object has the capability to add operators and lookup operator opcodes or names based on each other.
*/

// Reserve Table Rows are their own object (called operation, imported from its own file).
package ADT;
// needed to write to text file in the print method.
import java.io.*;


/* --------------------------------
 * ----- RESERVE TABLE CLASS  -----
 * ----- PART 1, ASSIGNMENT 1 -----
 * -------------------------------- */
public class ReserveTable {

    // elements
    int elementCount;
    int notFound = -1;
    ReserveObj[] opArray;    //declaring array

    // Constructor - set the max number of rows (stored as an array, opArray).
    public ReserveTable(int maxSize) {

        // error handling: make sure the max size is greater than 0 (would create an error when making an array!)
        try {
            // initialize reserve table (opArray) with Operation objects as the type.
            opArray = new ReserveObj[maxSize];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred. The max-size for the reserve table cannot be less than 0!");
            e.printStackTrace();
        }

        // initialize how many elements used of the reserve table to 0.
        elementCount = 0;
    }


    // adds a name and opcode to reserve table (contained in Operation),
    //   then returns the index of the added operation.
    public int Add(String name, int code){
        opArray[elementCount] = new ReserveObj(name, code);
        elementCount ++;
        return elementCount - 1;
    }


    // look up the [code] given the [name] (-1 if dne)
    public int LookupName(String name){

        // search each element (n lookup time)
        for (int i = 0; i < elementCount; i++){

            // check if string values are equal (ignoring case)
            if (opArray[i].name.compareToIgnoreCase(name) == 0){
                return opArray[i].code;
            }
        }

        // if entire opArray is searched and string not found, return -1.
        return -1;
    } // LookupName


    // look up the [name] given the [code] (empty string if dne)
    public String LookupCode(int code){

        // search each element (n lookup time)
        for (int i = 0; i < elementCount; i++){
            if (opArray[i].code == code){
                return opArray[i].name;
            }
        }

        // if entire opArray is searched and code not found, return empty string.
        return "";
    } // LookupCode


    // prints the reserve table into a neat .txt file!
    // will overwrite any existing file.
    public void PrintReserveTable(String filename){

        // need to try and create and open a writer in case it fails.
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            // headers
            writer.printf("%s%n", "Reserve Table (created by Matthew Hileman - UCCS CS4100, SP2022)");
            writer.println("----------------------------------------------------------------");
            writer.printf("%-12s%-12s%s%n", "Index", "Name", "Code");
            writer.println("----------------------------------------------------------------");
            // write each element in tabular output format
            for (int i = 0; i < elementCount; i++) {
                writer.printf("%-12d%-12s%d%n", i, opArray[i].name, opArray[i].code);
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
    } // PrintReserveTable
}
