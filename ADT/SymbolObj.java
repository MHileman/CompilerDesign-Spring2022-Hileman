/*
 * Symbol Object: Java Class File
 * Created by: Matthew Hileman
 * Last Updated: 3 Feb 2022
 * Purpose: Object row for the Symbol Table. Contains several type identifiers, and overloaded constructors.
 */

package ADT;

/* --------------------------------
 * ------- SYMBOL OBJ CLASS  ------
 * ----- PART 1, ASSIGNMENT 2 -----
 * -------------------------------- */
public class SymbolObj {

    // elements
    String name;
    char kind;              // Label, Variable, Constant (L, V, C)
    char data_type;         // Integer, Float, String (I, F, S)
    int integerValue;
    double floatValue;
    String stringValue;

    // constructors
    public SymbolObj(String inputName, char inputKind, int inputValue, char inputDataType){
        name = inputName;
        kind = inputKind;
        integerValue = inputValue;
        data_type = inputDataType;
    }
    public SymbolObj(String inputName, char inputKind, double inputValue, char inputDataType){
        name = inputName;
        kind = inputKind;
        floatValue = inputValue;
        data_type = inputDataType;
    }
    public SymbolObj(String inputName, char inputKind, String inputValue, char inputDataType){
        name = inputName;
        kind = inputKind;
        stringValue = inputValue;
        data_type = inputDataType;
    }
}
