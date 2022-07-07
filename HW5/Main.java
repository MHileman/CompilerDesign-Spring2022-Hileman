/*
 * Lexical Analyzer: Main Test File
 * Created by: Matthew Hileman
 * Last Updated: 1 April 2022
 * Purpose: To test the Syntactic.java class (part A)
 */

package HW5;

import ADT.SymbolTable;
import ADT.Lexical;
import ADT.*;



public class Main {

    public static void main(String[] args) {

        // Print Header
        System.out.printf("%s%n", "--------------------------------------------------------------------------------");
        System.out.printf("%s%n", "Matthew Hileman, 0636, CS4100, SPRING 2022, Compiler IDE used: IntelliJ IDEA CE");
        System.out.printf("%s%n", "--------------------------------------------------------------------------------");

        // Main
        String filePath = "/Users/matt/Desktop/HW5/BadSyntax-1-ASP22.txt";
        boolean traceon = true;
        Syntactic parser = new Syntactic(filePath, traceon);
        parser.parse();
        System.out.println("Done.");
    }

}

