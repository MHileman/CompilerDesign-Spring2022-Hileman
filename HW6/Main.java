/*
 * Lexical Analyzer: Main Test File
 * Created by: Matthew Hileman
 * Last Updated: 1 April 2022
 * Purpose: To test the Syntactic.java class (part A)
 */

package HW6;

//import ADT.SymbolTable;
//import ADT.Lexical;
import ADT.*;

/**
 *
 * @author abrouill SPRING 2022
 */
public class Main {

    public static void main(String[] args) {
        String filePath = "/Users/matt/Desktop/HW6/Test/CodeGenFULL-SP22.txt";
        System.out.println("Parsing "+filePath);
        boolean traceon = false; //true; //false;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();


        System.out.println("Done.");
    }

}


