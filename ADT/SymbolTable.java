/*
 * Symbol Table: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 3 Feb 2022
 * Purpose: The symbol table is a large indexed list, similar to the other table classes.
 *          It contains: name, kind, data_type,
 *                       and value (stored in 3 variables that are int/double/string depending on value type).
 *          Symbol table is used as a "variable" list guide.
 */

package ADT;

import java.io.IOException;
import java.io.PrintWriter;

/* --------------------------------
 * ------ SYMBOL TABLE CLASS  -----
 * ----- PART 1, ASSIGNMENT 2 -----
 * -------------------------------- */
public class SymbolTable {

    // elements
    int elementCount;
    SymbolObj[] symbolArray;    //declaring array
    char constantkind = 'C';

    // Constructor
    public SymbolTable(int maxSize){

        // error handling: make sure the max size is greater than 0 (would create an error when making an array!)
        try {
            // initialize reserve table (opArray) with Operation objects as the type.
            symbolArray = new SymbolObj[maxSize];
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred. The max-size for the Symbol table cannot be less than 0!");
            e.printStackTrace();
        }
        // initialize how many elements are used in the Symbol Table to 0.
        elementCount = 0;
    }


    /* -----------------------------
     * Add Symbol Overloaded Methods
     * -------------------------- */
    public int AddSymbol(String symbol, char kind, int value){

        // local
        int existingIndex = LookupSymbol(symbol);
        char data_type = 'I';

        // return index if already exists
        if (existingIndex > 0){
            return existingIndex;

        // ERROR CASE: return -1 if array full
        } else if (elementCount == symbolArray.length) {
            return -1;

        // add symbol and return index
        } else {
            symbolArray[elementCount] = new SymbolObj(symbol, kind, value, data_type);
            elementCount ++;
            return elementCount - 1;
        }
    } //AddSymbol1

    public int AddSymbol(String symbol, char kind, double value){
        System.out.println("Adding float: " + value);

        //local
        int existingIndex = LookupSymbol(symbol);
        char data_type = 'F';

        // return index if already exists
        if (existingIndex > 0){
            return existingIndex;

        // ERROR CASE: return -1 if array full
        } else if (elementCount == symbolArray.length) {
            return -1;

        // add symbol and return index
        } else {
            symbolArray[elementCount] = new SymbolObj(symbol, kind, value, data_type);
            elementCount ++;
            return elementCount - 1;
        }
    }

    public int AddSymbol(String symbol, char kind, String value){

        // local
        int existingIndex = LookupSymbol(symbol);
        char data_type = 'S';

        // return index if already exists
        if (existingIndex > 0){
            return existingIndex;

        // ERROR CASE: return -1 if array full
        } else if (elementCount == symbolArray.length) {
            return -1;

        // add symbol and return index
        } else {
            symbolArray[elementCount] = new SymbolObj(symbol, kind, value, data_type);
            elementCount ++;
            return elementCount - 1;
        }
    }


    // LOOKUP METHOD - returns -1 if not found
    public int LookupSymbol(String symbol){
    // look up the [index] of the given the [symbol] (-1 if dne)

        // search each element (n lookup time)
        for (int i = 0; i < elementCount; i++){

            // check if string values are equal (ignoring case)
            if (symbolArray[i].name.compareToIgnoreCase(symbol) == 0){
                return i;   // return index
            }
        }

        // if entire symbol table is searched and symbol not found, return -1.
        return -1;
    }


    // GETSYMBOL METHOD - returns string symbol given index.
    public String GetSymbol(int index){

        // ERROR CASE: return empty string if out of bounds index.
        if (index > elementCount){
            return "";

        // return symbol
        } else {
            return symbolArray[index].name;
        }
    }

    // GETKIND METHOD - returns char kind given index.
    public char GetKind(int index){

        // ERROR CASE: return empty char if out of bounds index.
        if (index > elementCount){
            // special empty character value (null character)
            return Character.MIN_VALUE;

            // return symbol
        } else {
            return symbolArray[index].kind;
        }
    }

    // GETDATATYPE METHOD - returns char data_type given index.
    public char GetDataType(int index){

        // ERROR CASE: return empty char if out of bounds index.
        if (index > elementCount){
            // special empty character value (null character)
            return Character.MIN_VALUE;

            // return symbol
        } else {
            return symbolArray[index].data_type;
        }
    }

    // GETSTRING METHOD - returns string value given index.
    public String GetString(int index){

        // ERROR CASE: return empty string if out of bounds index.
        if (index > elementCount){
            return "";

            // return symbol
        } else {
            return symbolArray[index].stringValue;
        }
    }

    // GETINTEGER METHOD - returns int data_type given index.
    public int GetInteger(int index){

        int temp_value = 0;
        // Need a try because there is no empty integer.
        try {
            temp_value = symbolArray[index].integerValue;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred. The index is out of bounds for GetInteger lookup!");
            e.printStackTrace();
        }
        return temp_value;
    }

    // GETFLOAT METHOD - returns int data_type given index.
    public double GetFloat(int index){

        double temp_value = 0;
        // Need a try because there is no empty integer.
        try {
            temp_value = symbolArray[index].floatValue;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("An error occurred. The index is out of bounds for GetFloat lookup!");
            e.printStackTrace();
        }
        return temp_value;
    }

    /* --------------------------------
     * Update Symbol Overloaded Methods
     * ----------------------------- */
    // update an existing symbol without creating a new one
    public void UpdateSymbol(int index, char kind, int value){
        char data_type = 'I';
        if (index <= elementCount) {
            symbolArray[index].kind = kind;
            symbolArray[index].integerValue = value;
            symbolArray[index].data_type = data_type;
        } else {
            System.out.println("An error occurred. UpdateSymbol attempted on an empty symbol row!");
        }
    }
    public void UpdateSymbol(int index, char kind, double value){
        char data_type = 'F';
        if (index <= elementCount) {
            symbolArray[index].kind = kind;
            symbolArray[index].floatValue = value;
            symbolArray[index].data_type = data_type;
        } else {
            System.out.println("An error occurred. UpdateSymbol attempted on an empty symbol row!");
        }
    }
    public void UpdateSymbol(int index, char kind, String value){
        char data_type = 'S';
        if (index <= elementCount) {
            symbolArray[index].kind = kind;
            symbolArray[index].stringValue = value;
            symbolArray[index].data_type = data_type;
        } else {
            System.out.println("An error occurred. UpdateSymbol attempted on an empty symbol row!");
        }
    }

    // print symbol table into a file
    public void PrintSymbolTable(String filename){
        // need to try and create and open a writer in case it fails.
        try {
            PrintWriter writer = new PrintWriter(filename, "UTF-8");
            // headers
            writer.printf("%s%n", "Symbol Table (created by Matthew Hileman - UCCS CS4100, SP2022)");
            writer.println("----------------------------------------------------------------");
            writer.printf("%-3s| %-30s| %-3s| %-3s| %s%n", "I", "Symbol", "K", "DT", "Val");
            writer.println("----------------------------------------------------------------");

            // write each element in tabular output format
            for (int i = 0; i < elementCount; i++) {
                writer.printf("%-3s| %-30s| %-3s| %-3s| ", i, symbolArray[i].name, symbolArray[i].kind,
                        symbolArray[i].data_type);

                // switch to handle value type used.
                switch (symbolArray[i].data_type) {
                    case 'I':
                        writer.printf("%s%n", symbolArray[i].integerValue);
                        break;
                    case 'F':
                        writer.printf("%s%n", symbolArray[i].floatValue);
                        break;
                    case 'S':
                        writer.printf("%s%n", symbolArray[i].stringValue);
                        break;
                }
            } // for

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
